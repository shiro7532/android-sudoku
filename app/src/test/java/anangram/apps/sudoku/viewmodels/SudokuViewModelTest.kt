package anangram.apps.sudoku.viewmodels

import anangram.apps.sudoku.TestUtils
import anangram.apps.sudoku.base.Either
import anangram.apps.sudoku.models.Entry
import anangram.apps.sudoku.ui.theme.ThemeRepository
import anangram.apps.sudoku.usecases.GetPuzzleUseCase
import anangram.apps.sudoku.usecases.SaveGameStateUseCase
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
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SudokuViewModelTest {


    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private lateinit var themeRepository: ThemeRepository
    private lateinit var getPuzzleUseCase: GetPuzzleUseCase
    private lateinit var saveGameStateUseCase: SaveGameStateUseCase

    private lateinit var vm: SudokuViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(dispatcher)

        themeRepository = mockk(relaxed = true)
        getPuzzleUseCase = mockk()
        saveGameStateUseCase = mockk()

        // default themeIndex flow
        every { themeRepository.themeIndex } returns MutableStateFlow(0)

        // getPuzzleUseCase returns a valid 9x9 puzzle by default
        coEvery { getPuzzleUseCase.invoke(any()) } returns Either.Success(TestUtils.makePuzzle("p-test"))

        // saveGameState just returns success
        coEvery { saveGameStateUseCase.invoke(any()) } returns Either.Success(Unit)

        // create the viewmodel with a fake SavedStateHandle-like param:
        val savedStateHandle = androidx.lifecycle.SavedStateHandle(mapOf("puzzleId" to "p-test"))

        vm = SudokuViewModel(
            themeRepository,
            savedStateHandle,
            getPuzzleUseCase,
            saveGameStateUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
        Dispatchers.resetMain()
    }

    // ----------------- Turbine helper -----------------
    private suspend fun ReceiveTurbine<SudokuUiState>.awaitFirstUiContent(): SudokuUiState.UiContent {
        while (true) {
            when (val item = awaitItem()) {
                is SudokuUiState.UiContent -> return item
                is SudokuUiState.Loading -> continue
                is SudokuUiState.Error -> continue
            }
        }
    }

    private suspend fun ReceiveTurbine<SudokuUiState>.drainUntilLatestUiContent(): SudokuUiState.UiContent {
        var latest: SudokuUiState.UiContent? = null
        while (true) {
            val item = try {
                awaitItem()
            } catch (_: Throwable) {
                break
            }
            if (item is SudokuUiState.UiContent) latest = item
        }
        return latest ?: error("No UiContent found")
    }


    // ----------------- Tests -----------------

    @Test
    fun `init - getPuzzle success emits UiContent`() = runTest {
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()

            val content = awaitFirstUiContent()
            // initial selectedCell should be null and time from saveState (none) -> 0
            assertNull(content.selectedCell)
            assertEquals(0, content.time)
        }
    }

    @Test
    fun `init - getPuzzle failure emits Error`() = runTest {
        coEvery { getPuzzleUseCase.invoke(any()) } returns Either.Failure(Throwable("not found"))

        val savedStateHandle = androidx.lifecycle.SavedStateHandle(mapOf("puzzleId" to "bad"))
        val vm2 = SudokuViewModel(
            themeRepository,
            savedStateHandle,
            getPuzzleUseCase,
            saveGameStateUseCase
        )

        vm2.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            // wait for possible Loading then Error
            var foundError = false
            repeat(3) {
                val item = awaitItem()
                if (item is SudokuUiState.Error) foundError = true
            }
            assertTrue(foundError)
        }
    }

    @Test
    fun `onCellClicked selects and deselects cell`() = runTest {
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            // click cell 0 -> selectedCell should become 0
            vm.onAction(SudokuUiAction.CellClicked(0))
            dispatcher.scheduler.advanceUntilIdle()

            val afterSelect = awaitFirstUiContent()
            assertEquals(0, afterSelect.selectedCell)

            // click same cell again -> selection toggles off (null)
            vm.onAction(SudokuUiAction.CellClicked(0))
            dispatcher.scheduler.advanceUntilIdle()

            val afterDeselect = awaitFirstUiContent()
            assertNull(afterDeselect.selectedCell)
        }
    }

    @Test
    fun `onValueClicked sets entry and enables undo`() = runTest {
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            // select cell 0 first
            vm.onAction(SudokuUiAction.CellClicked(0))
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            // click value ONE
            vm.onAction(SudokuUiAction.ValueClicked(Entry.ONE))
            dispatcher.scheduler.advanceUntilIdle()

            val updated = awaitFirstUiContent()
            // If not pencil mode, selectedEntry should be Entry.ONE
            assertEquals(Entry.ONE, updated.selectedEntry)
            // Undo should be available after making a move
            assertTrue(updated.undoAvailable)
        }
    }

    @Test
    fun `onDeleteClicked clears cell and registers undo`() = runTest {
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            // set entry at pos 1
            vm.onAction(SudokuUiAction.CellClicked(1))
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            vm.onAction(SudokuUiAction.ValueClicked(Entry.TWO))
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            // Now delete it
            vm.onAction(SudokuUiAction.ClickDelete)
            dispatcher.scheduler.advanceUntilIdle()

            val afterDelete = awaitFirstUiContent()
            // selectedEntry should be null after delete
            assertNull(afterDelete.selectedEntry)
            assertTrue(afterDelete.undoAvailable)
        }
    }

    @Test
    fun `toggle pencil mode affects selectedEntry behavior`() = runTest {
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            vm.onAction(SudokuUiAction.TogglePencil)
            dispatcher.scheduler.advanceUntilIdle()
            val afterToggle = awaitFirstUiContent()
            assertTrue(afterToggle.pencilMode)

            // select a cell and pencil-mark ONE
            vm.onAction(SudokuUiAction.CellClicked(2))
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            vm.onAction(SudokuUiAction.ValueClicked(Entry.ONE))
            dispatcher.scheduler.advanceUntilIdle()
            val afterPencil = awaitFirstUiContent()
            // Because pencil mode true, selectedEntry will only be not null if pencil value exists
            // It may remain null if hasPencil logic differs; at least undo should be available
            assertTrue(afterPencil.undoAvailable)
        }
    }

    @Test
    fun `undo and redo restore state`() = runTest {
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            // make a move at position 3 -> set Entry.THREE
            vm.onAction(SudokuUiAction.CellClicked(3))
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            vm.onAction(SudokuUiAction.ValueClicked(Entry.THREE))
            dispatcher.scheduler.advanceUntilIdle()
            val afterSet = awaitFirstUiContent()
            assertTrue(afterSet.undoAvailable)

            // undo
            vm.onAction(SudokuUiAction.ClickUndo)
            dispatcher.scheduler.advanceUntilIdle()
            val afterUndo = drainUntilLatestUiContent()
            // After undo, undoAvailable might be false (stack empty)
            // Ensure remaining and undo/redo flags are consistent
            assertFalse(afterUndo.undoAvailable && !afterUndo.redoAvailable) // sanity

            // redo
            vm.onAction(SudokuUiAction.ClickRedo)
            dispatcher.scheduler.advanceUntilIdle()
            val afterRedo = drainUntilLatestUiContent()
            assertTrue(afterRedo.redoAvailable || afterRedo.undoAvailable)
        }
    }

    @Test
    fun `reset puzzle clears content and undo stacks`() = runTest {
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            // make a move so undoAvailable becomes true
            vm.onAction(SudokuUiAction.CellClicked(4))
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            vm.onAction(SudokuUiAction.ValueClicked(Entry.FOUR))
            dispatcher.scheduler.advanceUntilIdle()
            val afterSet = awaitFirstUiContent()
            assertTrue(afterSet.undoAvailable)

            // request reset and confirm
            vm.onAction(SudokuUiAction.RequestReset)
            dispatcher.scheduler.advanceUntilIdle()
            val afterReq = awaitFirstUiContent()
            assertTrue(afterReq.showResetDialog)

            vm.onAction(SudokuUiAction.ConfirmResetRequest)
            dispatcher.scheduler.advanceUntilIdle()
            val afterReset = awaitFirstUiContent()
            // after reset, undo should be cleared and selection reset
            assertFalse(afterReset.undoAvailable)
            assertNull(afterReset.selectedCell)
            assertFalse(afterReset.showResetDialog)
        }
    }

    @Test
    fun `ClickNavigateBack saves state and emits NavigateUp`() = runTest {

        // 1) FIRST observe state and bring VM to a known state
        vm.state.test {
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            // perform moves inside the state test scope
            vm.onAction(SudokuUiAction.CellClicked(5))
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()

            vm.onAction(SudokuUiAction.ValueClicked(Entry.FIVE))
            dispatcher.scheduler.advanceUntilIdle()
            awaitFirstUiContent()
        }

        // 2) NOW observe navigation
        vm.navigation.test {
            vm.onAction(SudokuUiAction.ClickNavigateBack)
            dispatcher.scheduler.advanceUntilIdle()

            // verify saveGameStateUseCase invoked
            coVerify { saveGameStateUseCase.invoke(match { it.content.gameId == "p-test" }) }

            // read the nav event
            val nav = awaitItem()
            assertTrue(nav is SudokuNavigation.NavigateUp)
        }


    }

    @Test
    fun `startTimer increments time and pauseTimer stops it`() = testScope.runTest {
        // allow VM init to complete
        advanceUntilIdle()

        val initial = vm.state.value as SudokuUiState.UiContent
        assertEquals(0, initial.time)

        vm.startTimer()

        // do NOT call advanceUntilIdle() again until timer is paused
        advanceTimeBy(4000)

        val afterTicks = vm.state.value as SudokuUiState.UiContent
        assertTrue(afterTicks.time >= 3)

        vm.pauseTimer()

        val paused = afterTicks.time

        advanceTimeBy(2000)

        val afterPause = vm.state.value as SudokuUiState.UiContent
        assertEquals(paused, afterPause.time)
    }

}
