import { Link } from 'react-router-dom';

export default function Home() {
  return (
    <div className="flex flex-col items-center justify-center min-h-[80vh] p-4">
      <h1 className="text-4xl font-bold text-blue-600 mb-4">Crossword Puzzle</h1>
      <p className="text-lg text-center max-w-md mb-8">
        Challenge your vocabulary and problem-solving skills with our daily crossword puzzles!
      </p>
      <Link 
        to="/play" 
        className="px-8 py-4 bg-blue-600 text-white rounded-md font-medium hover:bg-blue-700 transition-colors text-lg"
      >
        Start Playing
      </Link>
    </div>
  );
}
