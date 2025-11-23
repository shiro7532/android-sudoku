# 🧩 Sudoku

A modern, fully-featured Sudoku game built with **Jetpack Compose**, **Clean Architecture**, **Room
**, **Retrofit**, and **Kotlin Coroutines**.

This project serves as a *showcase app* for clean modular design, scalable architecture, persistent
game state, animations, UI polish, and offline-first puzzle storage — ideal for both production and
portfolio use.

---

## ✨ Demo

> Splash and Welcome https://github.com/user-attachments/assets/d971c4b6-a14a-46cc-81ad-c25991c6efc9
> Gameplay https://github.com/user-attachments/assets/f9b56686-dcd3-4a42-8142-64df9294d566
> Error Handling https://github.com/user-attachments/assets/4a13784e-8d77-4efd-88bc-c0f074a33eac
> Game Reset https://github.com/user-attachments/assets/b78d2a2e-2475-4e53-bdc6-4f6c472af94c
> Game Won https://github.com/user-attachments/assets/f0f665a7-df5f-4990-b9ea-6c17bfc8e14c
> Leaderboard https://github.com/user-attachments/assets/ec7b2e9b-afac-4b98-a25d-6d3c3f4cabb4


## 🚀 Features

### 🎮 Core Gameplay

- Classic **9×9 Sudoku solver interface**
- Auto-highlight for:
    - Selected cell
    - Conflicting cells
    - Same-value cells
    - Row/column/box neighbors
- Pencil mode
- Undo & Redo powered by a custom **UndoRedoManager**
- Smart error highlighting
- **Auto-check** when the board is fully filled
- Win detection with trophy animation

### 🎨 Theming

- Dynamic theme selector
- Persistent theme index using **DataStore Preferences**

### 📦 Puzzle System

- Puzzles fetched from:
  ```
  https://sudoku-api.vercel.app/api/dosuku?query={
    newboard(limit:5) {
      grids { value, solution, difficulty }
      results
      message
    }
  }
  ```
- Preload strategy on app start
- Convert puzzle strings into domain `PuzzleModel`
- Offline-first storage using `Room`

### 💾 Persistence

- Autoload in-progress puzzle
- Autosave using:
    - `PuzzleSaveState`
    - `CellSaveState`
    - Room entity `GameStateEntity`
- Saves:
    - Time spent
    - Pencil marks
    - Per-cell progress
    - Completed history

### 🗂 Leaderboard + History

- Grouped by difficulty
- Sorted by completion time
- Rendered directly from Room

### 🛠 Architecture

- **Clean Architecture**
- **MVVM / MVI Hybrid**
    - ViewModel-driven state (StateFlow)
    - Event reducers
    - Independent cell-level state
- **UseCase-based domain layer**
- `PuzzleModel` encapsulates:
    - Neighbors
    - Validations
    - Pencil operations
    - Conflict detection
    - Undo/Redo transitions

---

## 🏛 Architecture Overview

```
┌─────────────────────┐
│      UI Layer       │
│  Jetpack Compose    │
│  Screens + State    │
└─────────▲───────────┘
          │ StateFlow
          │ Events
┌─────────┴───────────┐
│   Presentation      │
│    ViewModels       │
│  (MVVM / MVI Hybrid)│
│ UndoRedoManager     │
└─────────▲───────────┘
          │ UseCases
┌─────────┴───────────┐
│     Domain Layer    │
│ PuzzleModel         │
│ PuzzleSaveState     │
│ GetPuzzleUseCase    │
│ SaveGameStateUseCase│
└─────────▲───────────┘
          │ Repos
┌─────────┴───────────┐
│     Data Layer      │
│ Retrofit API Client │
│ Room (DAO + DB)     │
│ Entities / Mappers  │
└─────────────────────┘
```

---

## 🗃 Room Database Structure

### Entities

#### `SudokuPuzzleEntity`

```kotlin
@Entity(tableName = "sudoku_puzzles")
data class SudokuPuzzleEntity(
    @PrimaryKey val id: String,
    val difficulty: PuzzleDifficulty,
    val puzzle: String,
    val solution: String,
)
```

#### `GameStateEntity`

```kotlin
@Entity(tableName = "game_states")
data class GameStateEntity(
    @PrimaryKey val puzzleId: String,
    val isInProgress: Boolean,
    val timeSpentSec: Int,
    val saveTimestamp: Long,
    val cells: List<SudokuCell>
)
```

#### Relation

```kotlin
data class PuzzleWithGameState(
    @Embedded val puzzle: SudokuPuzzleEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "puzzleId"
    )
    val gameState: GameStateEntity?
)
```

---

## 🏗 DAO (SudokuDao)

Key operations:

- Fetch puzzle by ID
- Fetch in-progress game
- Fetch next new puzzle by difficulty
- Group by difficulty without game state
- Fetch completed puzzles ordered by time
- Insert puzzle
- Insert game state

Room handles:

- SQL relations
- Converters (Difficulty + SudokuCell list)

---

## 🔧 Domain: PuzzleModel

`PuzzleModel` is the heart of the engine:

- Generates neighbor sets (row/column/box)
- Maintains ouija sets (entry → cells mapping)
- Applies:
    - Set entry
    - Delete
    - Toggle pencil
    - Undo/Redo entry states
- Computes "remaining" counters
- Auto-checks completion
- Performs error detection

---

## 🎚 Undo / Redo Engine

Custom `UndoRedoManager`:

- Stack-based history
- Inverse operations
- Supports both pencil and normal mode
- Serializable entry states
- Fast time travel with highlight reset logic

---

## 🌐 API & Networking

Retrofit with Kotlinx Serialization:

```kotlin
`https://sudoku-api.vercel.app/api/dosuku?query={...}`
```

- Entire response is consumed in one shot
- No caching required
- New boards fetched on demand

---

## 🛠 Tech Stack

### UI

- Jetpack Compose
- Material 3
- Compose Navigation
- Splash APIs

### Architecture

- MVVM + MVI hybrid
- UseCases
- Domain-driven modeling

### Data

- Room
- Retrofit
- Kotlinx Serialization
- DataStore

### Async

- Kotlin Coroutines
- Flows
- StateFlow
- SharedFlow

---

## 📂 Project Structure

```
app/
 ├── data/
 │    ├── dao/
 │    ├── entities/
 │    ├── database/
 │    └── repositories/
 ├── domain/
 │    ├── usecases/
 │    └── models/
 ├── ui/
 │    ├── screens/
 │    ├── components/
 │    └── theme/
 ├── viewmodels/
 └── di/
```

---

## 📥 Installation

Clone the repo:

```bash
git clone https://github.com/YOUR_USERNAME/Sudoku.git
```

Open in Android Studio Hedgehog or later.

Minimum SDK: 24  
Target SDK: 36

---

## 🧪 Testing

- JUnit 5
- Turbine (for flows)
- kotlinx-coroutines-test
- MockK
- Test dispatchers with structured concurrency

Test coverage includes:

- Welcome screen
- Game logic
- Timer
- Undo/Redo
- Auto-check
- Reset flow
- Navigation

---

## 📄 License

This project is licensed under the Apache License 2.0.
