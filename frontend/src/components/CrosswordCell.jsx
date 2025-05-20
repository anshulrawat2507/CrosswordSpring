import { cn } from '../lib/utils';

export default function CrosswordCell({
  cellContent,//correct letter for this cell
  userAnswer,//answer user has typed
  rowIndex,//positon int the grid
  colIndex,
  isSelected,//whether the cell is currently selected
  isHighlighted,//whether this cell is part of current clue
  cellNumber,//clue no.
  onClick
}) {
  const isEmpty = cellContent === '';
  
  return (
    <div
      className={cn(
        'w-10 h-10 relative',
        isEmpty ? 'bg-gray-900' : 'bg-white border border-gray-300',
        isSelected && 'ring-2 ring-blue-500',
        isHighlighted && !isSelected && 'bg-blue-100'
      )}
      onClick={isEmpty ? undefined : onClick}//if thee cell is empty,it shows dark background and does not allow clicking
    >
      {!isEmpty && (
        <>
          {cellNumber && (
            <div className="absolute top-0.5 left-0.5 text-xs text-gray-500">
              {cellNumber}//if there's a clue no. , it shows it in top left corner in small gray text
            </div>
          )}
          <div className="flex items-center justify-center h-full text-lg font-medium">
            {userAnswer}//shows user answer if cell is not empty
          </div>
        </>
      )}
    </div>
  );
}
