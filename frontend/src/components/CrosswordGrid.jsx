import { useEffect } from 'react';
import useCrossword from '../hooks/useCrossword';
import CrosswordCell from './CrosswordCell';

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

  useEffect(() => {
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
      } else if (e.key === 'Enter' || e.key === ' ') {
        toggleDirection();
      } else if (e.key === 'Backspace') {
        if (userAnswers[selectedCell.row]?.[selectedCell.col]) {
          updateAnswer(selectedCell.row, selectedCell.col, '');
        } else {
          moveToPrevCell();
        }
      } else if (/^[a-zA-Z]$/.test(e.key)) {
        updateAnswer(selectedCell.row, selectedCell.col, e.key);
        moveToNextCell();
      }
    };
    
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [selectedCell, userAnswers, selectCell, updateAnswer, moveToNextCell, moveToPrevCell, toggleDirection]);

  if (!grid.length) return null;

  return (
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
