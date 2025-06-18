import { Link } from "react-router-dom";
import useCrossword from "../hooks/useCrossword";

export default function VictoryModal() {
  const { resetPuzzle, setShowVictoryModal } = useCrossword();

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
        <h2 className="text-2xl font-bold mb-4">Congratulations!</h2>
        <p className="mb-6">
          You've successfully completed the crossword puzzle!
        </p>

        <div className="flex flex-col sm:flex-row gap-3 justify-end">
          <button
            onClick={() => {
              resetPuzzle();
              setShowVictoryModal(false);
            }}
            className="px-4 py-2 bg-gray-200 rounded-lg font-medium hover:bg-gray-300 transition-colors"
          >
            Play Again
          </button>
          <Link
            to="/"
            className="px-4 py-2 bg-blue-600 text-white rounded-lg font-medium hover:bg-blue-700 transition-colors text-center"
            onClick={() => setShowVictoryModal(false)}
          >
            Back to Home
          </Link>
        </div>
      </div>
    </div>
  );
}
