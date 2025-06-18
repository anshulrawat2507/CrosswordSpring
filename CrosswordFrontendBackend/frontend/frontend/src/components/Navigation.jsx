import { Link } from "react-router-dom";
// this component renders the nav bar at the top with links to Home,Play and HOW to play
export default function Navigation() {
  return (
    <nav className="bg-blue-600 text-white p-4">
      <div className="container mx-auto flex items-center justify-between">
        <Link to="/" className="text-xl font-bold">
          Crossword Puzzle
        </Link>
        <div className="flex space-x-4">
          <Link to="/" className="hover:text-blue-200">
            Home
          </Link>
          <Link to="/play" className="hover:text-blue-200">
            Play
          </Link>
          <Link to="/how-to-play" className="hover:text-blue-200">
            How to Play
          </Link>
        </div>
      </div>
    </nav>
  );
}
