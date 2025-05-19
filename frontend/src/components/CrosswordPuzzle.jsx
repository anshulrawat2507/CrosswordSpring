//main component that orchestrated the puzzle with 3 child components--> grid , clues and victorymodal(optional if used)
import useCrossword from '../hooks/useCrossword';
import CrosswordGrid from './CrosswordGrid';
import CluesPanel from './CluesPanel';
import VictoryModal from './VictoryModal';
//this arranges the grid and clues side by side 
export default function CrosswordPuzzle() {
  const { loading, showVictoryModal, fillingWithSolution } = useCrossword();
  
  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }
  
  return (//show solution calls filingWithSolution from context to fill the grid with correct answers
    <div>
      <div className="text-center mb-6">
        <button 
          className="px-6 py-2 bg-white text-blue-600 border border-blue-200 rounded-md hover:bg-blue-50 transition-colors"
          onClick={fillingWithSolution}
        >
          Show Solution
        </button>
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
