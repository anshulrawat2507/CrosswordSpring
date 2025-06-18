import React, { useState, useEffect } from "react";
import useCrossword from "../hooks/useCrossword";
import CrosswordGrid from "./CrosswordGrid";
import CluesPanel from "./CluesPanel";
import VictoryModal from "./VictoryModal";
import toast from "react-hot-toast";
import { api } from "../services/api";

//this arranges the grid and clues side by side
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
  } = useCrossword();

  const [countdown, setCountdown] = useState(0);

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
      const backend = await api.getRandomCrosswordWithWords();
      const backendGrid = backend.grid.map((row) =>
        row.split("").map((cell) => (cell === "-" ? "" : cell))
      );
      let isComplete = true;
      let hasAtLeastOneAnswer = false;
      for (let row = 0; row < backendGrid.length; row++) {
        for (let col = 0; col < backendGrid[row].length; col++) {
          if (backendGrid[row][col] !== "") {
            const userAnswer = userAnswers[row]?.[col];
            if (userAnswer) {
              hasAtLeastOneAnswer = true;
            }
            if (
              !userAnswer ||
              userAnswer.toUpperCase() !== backendGrid[row][col].toUpperCase()
            ) {
              isComplete = false;
            }
          }
        }
      }
      if (!hasAtLeastOneAnswer) {
        toast.error("Please fill in some answers before checking!");
        return;
      }
      if (isComplete) {
        toast.success("Congratulations! You have completed the crossword!");
        setTimeout(() => {
          fillingWithSolution();
        }, 1500);
      } else {
        toast.error("Some answers are incorrect or missing.");
      }
    } catch (error) {
      toast.error("Error checking solution.");
    }
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
          <div className="mb-4 p-3 bg-yellow-100 border border-yellow-300 rounded-md">
            <p className="text-yellow-800 font-medium">
              Solution showing for {countdown} seconds...
            </p>
          </div>
        )}
        {solverRunning && (
          <div className="mb-4 p-3 bg-blue-100 border border-blue-300 rounded-md">
            <p className="text-blue-800 font-medium">
              Solver is working...{" "}
              {solverStep !== null ? `Step ${solverStep}` : ""}{" "}
              {solverAction === "solved" ? "Solved!" : ""}
            </p>
          </div>
        )}
        <div className="flex justify-center space-x-4">
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
