import CrosswordPuzzle from "../components/CrosswordPuzzle";

export default function Play() {
  return (
    <div>
      <h1 className="text-3xl font-bold text-blue-600 text-center mb-2">
        Daily Crossword Challenge
      </h1>
      <p className="text-center text-gray-600 mb-6">
        Complete the puzzle to solve it!
      </p>

      <CrosswordPuzzle />
    </div>
  );
}
