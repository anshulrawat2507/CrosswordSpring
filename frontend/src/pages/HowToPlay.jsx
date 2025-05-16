export default function HowToPlay() {
  return (
    <div className="max-w-3xl mx-auto p-8 bg-white my-8 rounded-lg shadow-sm">
      <h1 className="text-3xl font-bold text-blue-600 text-center mb-8">How To Play</h1>
      
      <hr className="my-6 border-gray-200" />
      
      <div className="space-y-8">
        <section>
          <h2 className="text-2xl font-semibold text-blue-600 mb-4">Crossword Basics</h2>
          <p className="mb-4">
            Crossword puzzles consist of a grid of white and black squares. Your goal is to fill in the white squares with letters to form words or phrases by solving the clues provided.
          </p>
          <ul className="list-disc pl-8 space-y-2">
            <li>Words read left-to-right (Across) or top-to-bottom (Down)</li>
            <li>Black squares separate words</li>
            <li>Each clue has a corresponding number in the grid</li>
          </ul>
        </section>
        
        <section>
          <h2 className="text-2xl font-semibold text-blue-600 mb-4">How to Navigate</h2>
          <ul className="list-disc pl-8 space-y-2">
            <li><strong>Click a Square:</strong> Select a cell to enter a letter</li>
            <li><strong>Click a Clue:</strong> Highlights the corresponding cells in the grid</li>
            <li><strong>Tab Key:</strong> Move to the next cell in the current direction</li>
            <li><strong>Arrow Keys:</strong> Navigate between cells</li>
            <li><strong>Enter Key:</strong> Switch between Across and Down orientation</li>
          </ul>
        </section>
      </div>
    </div>
  );
}
