import { useEffect } from 'react';
import useCrossword from '../hooks/useCrossword';
import CrosswordCell from './CrosswordCell';//uses crossword cell and custom useCrossword hook for state/context
//its the main grid that arranges all the cells
export default function CrosswordGrid() {
  const {
    grid,
    userAnswers,
    selectedCell,
    selectedDirection,
    highlightedCells,
    cellNumbers,
    selectCell,
    updateAnswer,
    moveToNextCell,
    moveToPrevCell,
    toggleDirection
  } = useCrossword();

  useEffect(() => {//sets up keyboard navigation for the grid
    const handleKeyDown = (e) => {
      if (!selectedCell.row && selectedCell.row !== 0) return;
      
      if (e.key === 'ArrowRight') {
        selectCell(selectedCell.row, selectedCell.col + 1);
      } else if (e.key === 'ArrowLeft') {
        selectCell(selectedCell.row, selectedCell.col - 1);
      } else if (e.key === 'ArrowUp') {
        selectCell(selectedCell.row - 1, selectedCell.col);
      } else if (e.key === 'ArrowDown') {
        selectCell(selectedCell.row + 1, selectedCell.col);
      } else if (e.key === 'Enter' || e.key === ' ') {//enter or splace toggles the direction
        toggleDirection();
      } else if (e.key === 'Backspace') {//backspace clears the current cell
        if (userAnswers[selectedCell.row]?.[selectedCell.col]) {
          updateAnswer(selectedCell.row, selectedCell.col, '');
        } else {
          moveToPrevCell();
        }
      } else if (/^[a-zA-Z]$/.test(e.key)) {//if a letter is pressed, fills the cell and moves to next
        updateAnswer(selectedCell.row, selectedCell.col, e.key);
        moveToNextCell();
      }
    };
    
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [selectedCell, userAnswers, selectCell, updateAnswer, moveToNextCell, moveToPrevCell, toggleDirection]);//dependecy array having states and functions used in the effect

  if (!grid.length) return null;

  return (//renders the grid with cells
    //the grid is a 2D array of cells, each cell is a crossword cell that takes care of its own rendering and state
    <div className="grid grid-flow-row auto-rows-auto gap-px bg-gray-300 p-px w-fit mx-auto">
      {grid.map((row, rowIndex) => (
        <div key={rowIndex} className="flex">
          {row.map((cell, colIndex) => (
            <CrosswordCell
              key={`${rowIndex}-${colIndex}`}
              cellContent={cell}
              userAnswer={userAnswers[rowIndex]?.[colIndex] || ''}
              rowIndex={rowIndex}
              colIndex={colIndex}
              isSelected={selectedCell.row === rowIndex && selectedCell.col === colIndex}
              isHighlighted={highlightedCells[`${rowIndex}-${colIndex}`]}
              cellNumber={cellNumbers[`${rowIndex}-${colIndex}`]}
              onClick={() => selectCell(rowIndex, colIndex)}
            />
          ))}
        </div>
      ))}
    </div>
  );
}
