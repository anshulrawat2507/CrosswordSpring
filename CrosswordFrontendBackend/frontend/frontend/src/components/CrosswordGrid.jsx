import { useEffect } from "react";
import useCrossword from "../hooks/useCrossword";
import CrosswordCell from "./CrosswordCell";

export default function CrosswordGrid() {
  const {
    grid,
    userAnswers,
    selectedCell,
    highlightedCells,
    cellNumbers,
    selectCell,
    updateAnswer,
    moveToNextCell,
    moveToPrevCell,
    toggleDirection,
    solverGrid,
    solverRunning,
  } = useCrossword();

  // Keyboard navigation
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (solverRunning) return;
      if (selectedCell.row === null || selectedCell.col === null) return;

      const { row, col } = selectedCell;

      // Handle arrow keys
      if (e.key === "ArrowRight") {
        selectCell(row, col + 1);
      } else if (e.key === "ArrowLeft") {
        selectCell(row, col - 1);
      } else if (e.key === "ArrowUp") {
        selectCell(row - 1, col);
      } else if (e.key === "ArrowDown") {
        selectCell(row + 1, col);
      }
      // Handle letter input
      else if (/^[a-zA-Z]$/.test(e.key)) {
        updateAnswer(row, col, e.key.toUpperCase());
        moveToNextCell();
      }
      // Handle backspace
      else if (e.key === "Backspace") {
        if (userAnswers[row]?.[col]) {
          updateAnswer(row, col, "");
        } else {
          moveToPrevCell();
        }
      }
      // Handle direction toggle
      else if (e.key === "Enter" || e.key === " ") {
        toggleDirection();
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [
    selectedCell,
    solverRunning,
    selectCell,
    updateAnswer,
    moveToNextCell,
    moveToPrevCell,
    toggleDirection,
    userAnswers,
  ]);

  if (!grid.length) return null;

  // Use solver grid if active, otherwise use regular grid
  // Use solverGrid if active, otherwise use regular grid
  const displayGrid = solverRunning ? solverGrid || grid : grid;
  const cellSize = 56; // px

  return (
    <div className="flex justify-center items-center w-full h-full min-h-[400px]">
      <div
        className="crossword-grid bg-white rounded-xl shadow-2xl p-6"
        style={{
          display: "inline-block",
          minWidth: displayGrid.length * cellSize,
          minHeight: displayGrid[0].length * cellSize,
        }}
        tabIndex={0} // Make focusable for keyboard events
      >
        {displayGrid.map((row, rowIdx) => (
          <div className="crossword-row flex" key={rowIdx}>
            {row.map((cell, colIdx) => (
              <CrosswordCell
                key={`${rowIdx}-${colIdx}`}
                cellContent={cell}
                userAnswer={userAnswers[rowIdx]?.[colIdx] || ""}
                rowIndex={rowIdx}
                colIndex={colIdx}
                isSelected={
                  selectedCell.row === rowIdx && selectedCell.col === colIdx
                }
                isHighlighted={!!highlightedCells[`${rowIdx}-${colIdx}`]}
                cellNumber={cellNumbers[`${rowIdx}-${colIdx}`] || ""}
                onClick={() => !solverRunning && selectCell(rowIdx, colIdx)}
                cellSize={cellSize}
                disabled={solverRunning}
              />
            ))}
          </div>
        ))}
      </div>
    </div>
  );
}
