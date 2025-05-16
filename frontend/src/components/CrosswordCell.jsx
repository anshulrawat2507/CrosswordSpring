import { cn } from '../lib/utils';

export default function CrosswordCell({
  cellContent,
  userAnswer,
  rowIndex,
  colIndex,
  isSelected,
  isHighlighted,
  cellNumber,
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
      onClick={isEmpty ? undefined : onClick}
    >
      {!isEmpty && (
        <>
          {cellNumber && (
            <div className="absolute top-0.5 left-0.5 text-xs text-gray-500">
              {cellNumber}
            </div>
          )}
          <div className="flex items-center justify-center h-full text-lg font-medium">
            {userAnswer}
          </div>
        </>
      )}
    </div>
  );
}
