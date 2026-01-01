import { createContext, useState, useEffect, useCallback, useRef } from "react";
import { api } from "../services/api";
import toast from "react-hot-toast";

export const CrosswordContext = createContext();

export function CrosswordProvider({ children }) {
  // State declarations
  const [grid, setGrid] = useState([]);
  const [solution, setSolution] = useState([]);
  const [clues, setClues] = useState({ across: [], down: [] });
  const [userAnswers, setUserAnswers] = useState({});
  const [selectedCell, setSelectedCell] = useState({ row: null, col: null });
  const [selectedDirection, setSelectedDirection] = useState("across");
  const [selectedClueNumber, setSelectedClueNumber] = useState(null);
  const [highlightedCells, setHighlightedCells] = useState({});
  const [cellNumbers, setCellNumbers] = useState({});
  const [completedClues, setCompletedClues] = useState({
    across: [],
    down: [],
  });
  const [showVictoryModal, setShowVictoryModal] = useState(false);
  const [loading, setLoading] = useState(true);
  const [showingSolution, setShowingSolution] = useState(false);
  const [solverGrid, setSolverGrid] = useState(null);
  const [solverStep, setSolverStep] = useState(null);
  const [solverRunning, setSolverRunning] = useState(false);
  const [solverAction, setSolverAction] = useState(null);

  const solverUnsubscribeRef = useRef(null);
  const solutionTimerRef = useRef(null);

  // Helper functions
  const checkCompletedClues = useCallback(
    (answers) => {
      const newCompleted = { across: [], down: [] };

      clues.across.forEach((clue) => {
        // Use row/col directly from clue if available
        const startRow = clue.row !== undefined ? clue.row : null;
        const startCol = clue.col !== undefined ? clue.col : null;
        
        if (startRow === null || startCol === null) {
          // Fallback to cellNumbers lookup
          for (const [key, value] of Object.entries(cellNumbers)) {
            if (value === clue.number) {
              const [r, c] = key.split("-").map(Number);
              checkAcrossClue(clue, r, c, answers, newCompleted);
              break;
            }
          }
        } else {
          checkAcrossClue(clue, startRow, startCol, answers, newCompleted);
        }
      });

      clues.down.forEach((clue) => {
        const startRow = clue.row !== undefined ? clue.row : null;
        const startCol = clue.col !== undefined ? clue.col : null;
        
        if (startRow === null || startCol === null) {
          for (const [key, value] of Object.entries(cellNumbers)) {
            if (value === clue.number) {
              const [r, c] = key.split("-").map(Number);
              checkDownClue(clue, r, c, answers, newCompleted);
              break;
            }
          }
        } else {
          checkDownClue(clue, startRow, startCol, answers, newCompleted);
        }
      });

      function checkAcrossClue(clue, startRow, startCol, answers, newCompleted) {
        let isComplete = true;
        let col = startCol;
        let answerIndex = 0;
        const wordLength = clue.length || clue.answer.length;

        while (answerIndex < wordLength && col < grid[startRow]?.length) {
          if (grid[startRow][col] === "") break;
          const userAnswer = answers[startRow]?.[col];
          if (
            !userAnswer ||
            userAnswer.toUpperCase() !== solution[startRow][col].toUpperCase()
          ) {
            isComplete = false;
            break;
          }
          col++;
          answerIndex++;
        }

        if (isComplete && answerIndex === wordLength) {
          newCompleted.across.push(clue.number);
        }
      }

      function checkDownClue(clue, startRow, startCol, answers, newCompleted) {
        let isComplete = true;
        let row = startRow;
        let answerIndex = 0;
        const wordLength = clue.length || clue.answer.length;

        while (answerIndex < wordLength && row < grid.length) {
          if (grid[row]?.[startCol] === "") break;
          const userAnswer = answers[row]?.[startCol];
          if (
            !userAnswer ||
            userAnswer.toUpperCase() !== solution[row][startCol].toUpperCase()
          ) {
            isComplete = false;
            break;
          }
          row++;
          answerIndex++;
        }

        if (isComplete && answerIndex === wordLength) {
          newCompleted.down.push(clue.number);
        }
      }

      setCompletedClues(newCompleted);
    },
    [clues, cellNumbers, grid, solution]
  );

  const checkVictory = useCallback(
    (answers) => {
      for (let row = 0; row < solution.length; row++) {
        for (let col = 0; col < solution[row].length; col++) {
          if (solution[row][col] !== "") {
            if (
              !answers[row]?.[col] ||
              answers[row][col].toUpperCase() !==
                solution[row][col].toUpperCase()
            ) {
              setShowVictoryModal(false);
              return;
            }
          }
        }
      }
      setShowVictoryModal(true);
    },
    [solution]
  );

  // Core functions
  const updateHighlightedCells = useCallback(
    (row, col) => {
      if (row === null || col === null) return;

      const newHighlighted = {};
      if (selectedDirection === "across") {
        let startCol = col;
        while (startCol > 0 && grid[row][startCol - 1] !== "") startCol--;
        let c = startCol;
        while (c < grid[row].length && grid[row][c] !== "") {
          newHighlighted[`${row}-${c}`] = true;
          c++;
        }
        for (const [key, value] of Object.entries(cellNumbers)) {
          const [r, c] = key.split("-").map(Number);
          if (r === row && c === startCol) {
            const clue = clues.across.find((cl) => cl.number === value);
            if (clue) setSelectedClueNumber(value);
            break;
          }
        }
      } else {
        let startRow = row;
        while (startRow > 0 && grid[startRow - 1][col] !== "") startRow--;
        let r = startRow;
        while (r < grid.length && grid[r][col] !== "") {
          newHighlighted[`${r}-${col}`] = true;
          r++;
        }
        for (const [key, value] of Object.entries(cellNumbers)) {
          const [r, c] = key.split("-").map(Number);
          if (r === startRow && c === col) {
            const clue = clues.down.find((cl) => cl.number === value);
            if (clue) setSelectedClueNumber(value);
            break;
          }
        }
      }
      setHighlightedCells(newHighlighted);
    },
    [grid, selectedDirection, cellNumbers, clues]
  );

  const selectCell = useCallback(
    (row, col) => {
      if (
        row < 0 ||
        row >= grid.length ||
        col < 0 ||
        col >= grid[0]?.length ||
        grid[row][col] === "" ||
        solverRunning
      ) {
        return;
      }
      setSelectedCell({ row, col });
      updateHighlightedCells(row, col);
    },
    [grid, solverRunning, updateHighlightedCells]
  );

  const updateAnswer = useCallback(
    (row, col, value) => {
      if (solverRunning) return;

      setUserAnswers((prev) => {
        const newAnswers = { ...prev };
        if (!newAnswers[row]) newAnswers[row] = {};
        newAnswers[row][col] = value.toUpperCase();
        checkCompletedClues(newAnswers);
        checkVictory(newAnswers);
        return newAnswers;
      });
    },
    [checkCompletedClues, checkVictory, solverRunning]
  );

  const moveToNextCell = useCallback(() => {
    if (solverRunning) return;
    const { row, col } = selectedCell;
    if (selectedDirection === "across") {
      if (col + 1 < grid[row].length && grid[row][col + 1] !== "") {
        selectCell(row, col + 1);
      }
    } else {
      if (row + 1 < grid.length && grid[row + 1][col] !== "") {
        selectCell(row + 1, col);
      }
    }
  }, [selectedCell, selectedDirection, grid, selectCell, solverRunning]);

  const moveToPrevCell = useCallback(() => {
    if (solverRunning) return;
    const { row, col } = selectedCell;
    if (selectedDirection === "across") {
      if (col > 0 && grid[row][col - 1] !== "") {
        selectCell(row, col - 1);
      }
    } else {
      if (row > 0 && grid[row - 1][col] !== "") {
        selectCell(row - 1, col);
      }
    }
  }, [selectedCell, selectedDirection, grid, selectCell, solverRunning]);

  const toggleDirection = useCallback(() => {
    if (solverRunning) return;
    setSelectedDirection((prev) => (prev === "across" ? "down" : "across"));
  }, [solverRunning]);

  const fillingWithSolution = useCallback(() => {
    if (solverRunning) return;

    // Toggle solution display
    if (showingSolution) {
      setShowingSolution(false);
      clearTimeout(solutionTimerRef.current);

      // Reset to empty answers
      const emptyAnswers = {};
      for (let row = 0; row < solution.length; row++) {
        emptyAnswers[row] = {};
        for (let col = 0; col < solution[row].length; col++) {
          emptyAnswers[row][col] = "";
        }
      }
      setUserAnswers(emptyAnswers);
      return;
    }

    // Show solution
    setShowingSolution(true);
    setUserAnswers(solution);

    solutionTimerRef.current = setTimeout(() => {
      setShowingSolution(false);
      const emptyAnswers = {};
      for (let row = 0; row < solution.length; row++) {
        emptyAnswers[row] = {};
        for (let col = 0; col < solution[row].length; col++) {
          emptyAnswers[row][col] = "";
        }
      }
      setUserAnswers(emptyAnswers);
    }, 15000);
  }, [solution, solverRunning, showingSolution]);

  const startSolver = useCallback(() => {
    if (solverRunning) return;

    console.log("Starting solver...");
    setSolverRunning(true);
    setSolverStep(0);
    setSolverAction("starting");

    // Initialize with empty grid
    const emptyGrid = grid.map((row) =>
      row.map((cell) => (cell === "" ? "" : "-"))
    );
    setSolverGrid(emptyGrid);

    // Clear any existing subscription
    if (solverUnsubscribeRef.current) {
      solverUnsubscribeRef.current();
    }

    solverUnsubscribeRef.current = api.subscribeToSolverSteps((data) => {
      console.log("Solver update:", data);

      // Handle grid conversion safely
      let parsedGrid = data.grid;
      if (Array.isArray(parsedGrid)) {
        if (typeof parsedGrid[0] === "string") {
          parsedGrid = parsedGrid.map((row) =>
            row.split("").map((cell) => (cell === "-" ? "" : cell))
          );
        } else {
          parsedGrid = parsedGrid.map((row) =>
            row.map((cell) => (cell === "-" ? "" : cell))
          );
        }
      }

      setSolverGrid(parsedGrid);
      setSolverStep(data.step);
      setSolverAction(data.action);

      if (data.action === "solved") {
        console.log("Solver completed!");
        setSolverRunning(false);
        // Keep the solverGrid as the final solution
      }
    });
  }, [solverRunning, grid]);

  // [Rest of the context implementation remains the same...]

  const cancelSolver = useCallback(() => {
    if (solverUnsubscribeRef.current) {
      solverUnsubscribeRef.current();
      solverUnsubscribeRef.current = null;
    }
    setSolverRunning(false);
    setSolverGrid(null);
    setSolverStep(null);
    setSolverAction(null);
  }, []);

  const resetPuzzle = useCallback(async () => {
    setLoading(true);
    try {
      // Use the new puzzle endpoint
      const data = await api.getPuzzle();
      
      if (data.error) {
        console.error("Error loading puzzle:", data.error);
        toast.error("Failed to load puzzle");
        return;
      }

      // Grid is already a 2D array from the new API
      const gridData = data.grid.map((row) =>
        row.map((cell) => (cell === "" ? "" : cell))
      );
      setGrid(gridData);
      
      // Create solution grid (same as grid but for checking answers)
      const solutionData = data.grid.map((row) =>
        row.map((cell) => (cell === "" ? "" : cell))
      );
      setSolution(solutionData);

      // Format clues from the new API structure
      const formattedClues = {
        across: (data.across || []).map((item) => ({
          number: item.number,
          clue: item.clue,
          answer: item.answer,
          row: item.row,
          col: item.col,
          length: item.length,
        })),
        down: (data.down || []).map((item) => ({
          number: item.number,
          clue: item.clue,
          answer: item.answer,
          row: item.row,
          col: item.col,
          length: item.length,
        })),
      };
      setClues(formattedClues);

      // Initialize empty user answers
      const initialAnswers = {};
      for (let row = 0; row < gridData.length; row++) {
        initialAnswers[row] = {};
        for (let col = 0; col < gridData[row].length; col++) {
          initialAnswers[row][col] = "";
        }
      }
      setUserAnswers(initialAnswers);

      // Calculate cell numbers based on clue positions
      const numbers = {};
      const allCluePositions = new Map();

      // Collect all clue starting positions
      formattedClues.across.forEach((clue) => {
        const key = `${clue.row}-${clue.col}`;
        allCluePositions.set(key, clue.number);
      });
      formattedClues.down.forEach((clue) => {
        const key = `${clue.row}-${clue.col}`;
        if (!allCluePositions.has(key)) {
          allCluePositions.set(key, clue.number);
        }
      });

      // Set cell numbers
      allCluePositions.forEach((number, key) => {
        numbers[key] = number;
      });
      setCellNumbers(numbers);

      setSelectedCell({ row: null, col: null });
      setSelectedDirection("across");
      setSelectedClueNumber(null);
      setHighlightedCells({});
      setCompletedClues({ across: [], down: [] });
      setShowVictoryModal(false);
      setShowingSolution(false);
      setSolverStep(null);
      setSolverRunning(false);
      setSolverAction(null);

      // Cancel any active solver
      if (solverUnsubscribeRef.current) {
        solverUnsubscribeRef.current();
        solverUnsubscribeRef.current = null;
      }
    } catch (error) {
      console.error("Error resetting puzzle:", error);
      toast.error("Failed to load new puzzle");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    resetPuzzle();
    return () => {
      if (solverUnsubscribeRef.current) solverUnsubscribeRef.current();
      if (solutionTimerRef.current) clearTimeout(solutionTimerRef.current);
    };
  }, [resetPuzzle]);

  const selectClue = useCallback(
    (direction, number) => {
      if (solverRunning) return;
      setSelectedDirection(direction);
      setSelectedClueNumber(number);
      
      // Find the clue directly from the clues array (new structure has row/col)
      const clueList = direction === "across" ? clues.across : clues.down;
      const clue = clueList.find((c) => c.number === number);
      
      if (clue && clue.row !== undefined && clue.col !== undefined) {
        setSelectedCell({ row: clue.row, col: clue.col });
        updateHighlightedCells(clue.row, clue.col);
      } else {
        // Fallback to cellNumbers lookup
        for (const [key, value] of Object.entries(cellNumbers)) {
          if (value === number) {
            const [row, col] = key.split("-").map(Number);
            setSelectedCell({ row, col });
            updateHighlightedCells(row, col);
            break;
          }
        }
      }
    },
    [clues, cellNumbers, updateHighlightedCells, solverRunning]
  );
  const clearSolverSolution = useCallback(() => {
    setSolverGrid(null);
    setSolverStep(null);
    setSolverAction(null);
  }, []);
  const value = {
    grid: solverGrid || grid,
    solution,
    clues,
    userAnswers,
    selectedCell,
    selectedDirection,
    selectedClueNumber,
    highlightedCells,
    cellNumbers,
    completedClues,
    clearSolverSolution,
    showVictoryModal,
    loading,
    showingSolution,
    solverGrid,
    setHighlightedCells,
    solverStep,
    solverRunning,
    solverAction,
    selectCell,
    selectClue,
    updateAnswer,
    fillingWithSolution,
    moveToNextCell,
    moveToPrevCell,
    toggleDirection,
    resetPuzzle,
    setSelectedDirection,
    setSelectedCell,
    setSelectedClueNumber,
    setShowVictoryModal,
    startSolver,
    cancelSolver,
  };

  return (
    <CrosswordContext.Provider value={value}>
      {children}
    </CrosswordContext.Provider>
  );
}
