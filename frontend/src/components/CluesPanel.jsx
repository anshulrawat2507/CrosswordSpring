import { useState } from 'react';
import useCrossword from '../hooks/useCrossword';
import { cn } from '../lib/utils';

export default function CluesPanel() {
  const {
    clues,
    selectedDirection,
    selectedClueNumber,
    completedClues,
    selectClue
  } = useCrossword();
  
  const [activeTab, setActiveTab] = useState('across');//tab-->lets the user switch between across and down clues
  
  return (
    <div className="bg-white rounded-lg shadow-md overflow-hidden">
      <div className="flex">
        <button 
          className={cn(
            "flex-1 py-3 font-medium",
            activeTab === 'across' ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-600'
          )}
          onClick={() => setActiveTab('across')}
        >
          Across
        </button>
        <button 
          className={cn(
            "flex-1 py-3 font-medium",
            activeTab === 'down' ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-600'
          )}
          onClick={() => setActiveTab('down')}
        >
          Down
        </button>
      </div>
      
      <div className="p-4 max-h-[500px] overflow-y-auto">
        {activeTab === 'across' ? (
          <div className="space-y-2">
            {clues.across.map(clue => {
              const isSelected = selectedDirection === 'across' && selectedClueNumber === clue.number;
              const isCompleted = completedClues.across.includes(clue.number);
              
              return (
                <div 
                  key={`across-${clue.number}`}
                  className={cn(
                    "p-2 rounded cursor-pointer",
                    isSelected ? 'bg-blue-100' : 'hover:bg-gray-100',
                    isCompleted ? 'text-gray-400' : ''
                  )}
                  onClick={() => selectClue('across', clue.number)}
                >
                  <span className="font-bold mr-2">{clue.number}.</span>
                  {clue.clue}
                </div>
              );
            })}
          </div>
        ) : (
          <div className="space-y-2">
            {clues.down.map(clue => {
              const isSelected = selectedDirection === 'down' && selectedClueNumber === clue.number;
              const isCompleted = completedClues.down.includes(clue.number);
              
              return (
                <div 
                  key={`down-${clue.number}`}
                  className={cn(
                    "p-2 rounded cursor-pointer",
                    isSelected ? 'bg-blue-100' : 'hover:bg-gray-100',
                    isCompleted ? 'text-gray-400' : ''
                  )}
                  onClick={() => selectClue('down', clue.number)}
                >
                  <span className="font-bold mr-2">{clue.number}.</span>
                  {clue.clue}
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
