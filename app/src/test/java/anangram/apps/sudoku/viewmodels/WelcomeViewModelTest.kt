package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.TestUtils
import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.models.PuzzleDifficulty
import anangram.apps.sudoku.ui.theme.ThemeRepository
import anangram.apps.sudoku.usecases.GetInProgressPuzzleUseCase
import anangram.apps.sudoku.usecases.GetNewPuzzleByDifficultyUseCase
import anangram.apps.sudoku.usecases.PreloadPuzzlesUseCase
import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class WelcomeViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var themeRepository: ThemeRepository
    private lateinit var preloadUseCase: PreloadPuzzlesUseCase
    private lateinit var getInProgressUseCase: GetInProgressPuzzleUseCase
    private lateinit var getNewPuzzleUseCase: GetNewPuzzleByDifficultyUseCase

    private lateinit var vm: WelcomeViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)

        themeRepository = mockk(relaxed = true)
        preloadUseCase = mockk()
        getInProgressUseCase = mockk()
        getNewPuzzleUseCase = mockk()

        // default: themeIndex flow = 0
        every { themeRepository.themeIndex } returns MutableStateFlow(0)

        // default preload success
        coEvery { preloadUseCase.invoke() } returns Either.Success(Unit)

        // default no in-progress game
        coEvery { getInProgressUseCase.invoke(any()) } returns Either.Success(null)

        // default new puzzle success (valid 81-cell puzzle)
        coEvery { getNewPuzzleUseCase.invoke(any()) } returns Either.Success(
            TestUtils.makePuzzle("new-123")
        )

        vm = WelcomeViewModel(
            themeRepository,
            preloadUseCase,
            getInProgressUseCase,
            getNewPuzzleUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    /**
     * Turbine helper: consume emissions until a UiContent is observed and return it.
     * This avoids brittle skipItems(count) usage and makes tests robust to extra emissions.
     */
    private suspend fun ReceiveTurbine<WelcomeUiState>.awaitFirstUiContent(): WelcomeUiState.UiContent {
        while (true) {
            when (val item = awaitItem()) {
                is WelcomeUiState.UiContent -> return item
                is WelcomeUiState.Loading -> continue
                is WelcomeUiState.Error -> continue
            }
        }
    }

    @Test
    fun `init - preload success emits UiContent`() = runTest {
        vm.state.test {
            // Wait for the ViewModel init to run
            dispatcher.scheduler.advanceUntilIdle()

            val content = awaitFirstUiContent()
            assertEquals(PuzzleDifficulty.EASY, content.difficulty)
            assertEquals(0, content.themeIndex)
        }
    }

    @Test
    fun `init - preload failure emits Error state`() = runTest {
        coEvery { preloadUseCase.invoke() } returns Either.Failure(Throwable("boom"))

        val vm2 = WelcomeViewModel(
            themeRepository,
            preloadUseCase,
            getInProgressUseCase,
            getNewPuzzleUseCase
        )

        vm2.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            val item = awaitItem() // could be Loading or Error depending on timing
            // If first is Loading, next should be Error. Use helper loop:
            if (item is WelcomeUiState.Error) {
                item
            } else {
                // keep consuming until error found
                var found: WelcomeUiState? = null
                while (found == null) {
                    val next = awaitItem()
                    if (next is WelcomeUiState.Error) found = next
                }
                found as WelcomeUiState.Error
            }
        }
    }

    @Test
    fun `SetDifficulty updates difficulty and queries in-progress`() = runTest {
        // when HARD queried, return an in-progress puzzle
        coEvery { getInProgressUseCase.invoke(match { it.content == PuzzleDifficulty.HARD }) } returns
                Either.Success(TestUtils.makePuzzle("p1"))

        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            // ensure initial UiContent consumed
            val initial = awaitFirstUiContent()
            assertEquals(PuzzleDifficulty.EASY, initial.difficulty)

            // perform action
            vm.onAction(WelcomeUiAction.SetDifficulty(PuzzleDifficulty.HARD))
            dispatcher.scheduler.advanceUntilIdle()

            // now wait for updated UiContent emission
            val updated = awaitFirstUiContent()
            assertEquals(PuzzleDifficulty.HARD, updated.difficulty)
            assertEquals("p1", updated.inProgressGameId)
        }

        coVerify { getInProgressUseCase.invoke(match { it.content == PuzzleDifficulty.HARD }) }
    }

    @Test
    fun `SetTheme calls repository and updates state`() = runTest {
        coEvery { themeRepository.setTheme(3) } returns Unit

        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent() // consume initial

            vm.onAction(WelcomeUiAction.SetTheme(3))
            dispatcher.scheduler.advanceUntilIdle()

            val updated = awaitFirstUiContent()
            assertEquals(3, updated.themeIndex)
        }

        coVerify { themeRepository.setTheme(3) }
    }

    @Test
    fun `ToggleThemeSelector flips isThemeSelectorOpen`() = runTest {
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent() // consume initial

            vm.onAction(WelcomeUiAction.ToggleThemeSelector)
            dispatcher.scheduler.advanceUntilIdle()

            val updated = awaitFirstUiContent()
            assertTrue(updated.isThemeSelectorOpen)
        }
    }

    @Test
    fun `StartNewGame emits navigation Game on success`() = runTest {
        vm.navigation.test {
            dispatcher.scheduler.advanceUntilIdle()

            vm.onAction(WelcomeUiAction.StartNewGame)
            dispatcher.scheduler.advanceUntilIdle()

            val nav = awaitItem()
            assertTrue(nav is WelcomeNavigation.Game)
            assertEquals("new-123", nav.id)
        }
    }

    @Test
    fun `StartNewGame emits Error state when new puzzle usecase returns failure`() = runTest {
        coEvery { getNewPuzzleUseCase.invoke(any()) } returns Either.Failure(Throwable("nope"))

        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent() // consume initial

            vm.onAction(WelcomeUiAction.StartNewGame)
            dispatcher.scheduler.advanceUntilIdle()

            // look for Error in subsequent emissions
            var foundError = false
            val item = awaitItem()
            if (item is WelcomeUiState.Error) foundError = true

            assertTrue(foundError)
        }
    }

    @Test
    fun `ResumeGame emits Game navigation when inProgress id exists`() = runTest {
        coEvery { getInProgressUseCase.invoke(any()) } returns Either.Success(TestUtils.makePuzzle("in-321"))

        // re-init to pick up changed behaviour
        vm = WelcomeViewModel(
            themeRepository,
            preloadUseCase,
            getInProgressUseCase,
            getNewPuzzleUseCase
        )

        vm.navigation.test {
            dispatcher.scheduler.advanceUntilIdle()

            vm.onAction(WelcomeUiAction.ResumeGame)
            dispatcher.scheduler.advanceUntilIdle()

            val nav = awaitItem()
            assertTrue(nav is WelcomeNavigation.Game)
            assertEquals("in-321", nav.id)
        }
    }

    @Test
    fun `GoToLeaderboards emits LeaderBoard navigation`() = runTest {
        vm.navigation.test {
            vm.onAction(WelcomeUiAction.GoToLeaderboards)
            val nav = awaitItem()
            assertTrue(nav is WelcomeNavigation.LeaderBoard)
        }
    }
}
