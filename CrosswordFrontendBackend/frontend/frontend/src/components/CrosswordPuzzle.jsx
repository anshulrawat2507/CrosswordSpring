import React, { useState, useEffect } from "react";
import useCrossword from "../hooks/useCrossword";
import CrosswordGrid from "./CrosswordGrid";
import CluesPanel from "./CluesPanel";
import VictoryModal from "./VictoryModal";
import toast from "react-hot-toast";

export default function CrosswordPuzzle() {
  const {
    loading,
    showVictoryModal,
    fillingWithSolution,
    userAnswers,
    solution,
    grid,
    showingSolution,
    // Solver
    solverRunning,
    solverStep,
    solverAction,
    startSolver,
    cancelSolver,
    resetPuzzle,
    // New additions for solution checking
    highlightedCells,
    setHighlightedCells,
    setShowVictoryModal,
  } = useCrossword();

  const [countdown, setCountdown] = useState(0);
  const [solverCountdown, setSolverCountdown] = useState(0);

  // Countdown timer when showing solution
  useEffect(() => {
    let timer;
    if (showingSolution) {
      setCountdown(15);
      timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    }

    return () => {
      if (timer) clearInterval(timer);
    };
  }, [showingSolution]);

  const handleCheckSolution = async () => {
    try {
      // First check if any answers are filled
      let hasAtLeastOneAnswer = false;
      for (let row = 0; row < grid.length; row++) {
        for (let col = 0; col < grid[row].length; col++) {
          if (grid[row][col] !== "" && userAnswers[row]?.[col]) {
            hasAtLeastOneAnswer = true;
            break;
          }
        }
        if (hasAtLeastOneAnswer) break;
      }

      if (!hasAtLeastOneAnswer) {
        toast.error("Please fill in some answers before checking!");
        return;
      }

      // Reset previous highlights
      setHighlightedCells({});

      // Check against the current solution grid
      let isComplete = true;
      let incorrectCells = [];
      let correctCells = [];

      for (let row = 0; row < grid.length; row++) {
        for (let col = 0; col < grid[row].length; col++) {
          if (grid[row][col] !== "") {
            const userAnswer = (userAnswers[row]?.[col] || "")
              .toUpperCase()
              .trim();
            const correctAnswer = solution[row][col].toUpperCase().trim();

            if (userAnswer === correctAnswer) {
              correctCells.push({ row, col, status: "correct" });
            } else if (userAnswer !== "") {
              isComplete = false;
              incorrectCells.push({ row, col, status: "incorrect" });
            } else {
              isComplete = false;
            }
          }
        }
      }

      // Update highlighted cells
      const newHighlighted = {};
      incorrectCells.forEach(({ row, col }) => {
        newHighlighted[`${row}-${col}`] = "incorrect";
      });
      correctCells.forEach(({ row, col }) => {
        newHighlighted[`${row}-${col}`] = "correct";
      });
      setHighlightedCells(newHighlighted);

      if (isComplete) {
        toast.success("Congratulations! You have completed the crossword!");
        setShowVictoryModal(true);
      } else {
        const incorrectCount = incorrectCells.length;
        const message =
          incorrectCount > 0
            ? `${incorrectCount} incorrect answer${
                incorrectCount !== 1 ? "s" : ""
              }. Please try again.`
            : "Some answers are still missing. Keep going!";
        toast.error(message);
      }
    } catch (error) {
      toast.error("Error checking solution.");
      console.error("Check solution error:", error);
    }
  };

  // ... rest of your component code remains the same ...

  const handleCloseSolution = () => {
    fillingWithSolution(); // This will reset the solution display
  };

  const handleCancelSolver = () => {
    cancelSolver();
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div>
      <div className="text-center mb-6">
        {showingSolution && (
          <div className="mb-4 p-3 bg-yellow-100 border border-yellow-300 rounded-md flex justify-between items-center">
            <p className="text-yellow-800 font-medium">
              Solution showing for {countdown} seconds...
            </p>
            <button
              onClick={handleCloseSolution}
              className="ml-4 px-3 py-1 bg-yellow-500 hover:bg-yellow-600 text-white rounded-md"
            >
              ✕ Close
            </button>
          </div>
        )}
        {solverRunning && (
          <div className="mb-4 p-3 bg-blue-100 border border-blue-300 rounded-md flex justify-between items-center">
            <p className="text-blue-800 font-medium">
              Solver is working... {solverAction === "solved" ? "Solved!" : ""}
            </p>
            <button
              onClick={handleCancelSolver}
              className="ml-4 px-3 py-1 bg-blue-500 hover:bg-blue-600 text-white rounded-md"
            >
              ✕ Close
            </button>
          </div>
        )}
        <div className="flex justify-center space-x-4 flex-wrap gap-2">
          <button
            className="px-6 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-md transition-colors"
            onClick={handleCheckSolution}
            disabled={showingSolution || solverRunning}
          >
            Check Solution
          </button>
          <button
            className="px-6 py-2 bg-white text-blue-600 border border-blue-200 rounded-md hover:bg-blue-50 transition-colors"
            onClick={fillingWithSolution}
            disabled={showingSolution || solverRunning}
          >
            {showingSolution ? `Solution (${countdown}s)` : "Show Solution"}
          </button>
          <button
            className="px-6 py-2 bg-green-600 hover:bg-green-700 text-white rounded-md transition-colors"
            onClick={startSolver}
            disabled={solverRunning || showingSolution}
          >
            Watch Solver
          </button>
          <button
            className="px-6 py-2 bg-purple-600 hover:bg-purple-700 text-white rounded-md transition-colors"
            onClick={resetPuzzle}
            disabled={solverRunning || showingSolution}
          >
            New Puzzle
          </button>
        </div>
      </div>

      <div className="flex flex-col lg:flex-row gap-8">
        <div className="lg:w-1/2">
          <CrosswordGrid />
        </div>
        <div className="lg:w-1/2">
          <CluesPanel />
        </div>
      </div>

      {showVictoryModal && <VictoryModal />}
    </div>
  );
}
