import { Routes, Route } from "react-router-dom";
import Navigation from "./components/Navigation";
import Home from "./pages/Home";
import Play from "./pages/Play";
import HowToPlay from "./pages/HowToPlay";
import { CrosswordProvider } from "./contexts/CrosswordContext";

function App() {
  return (
    <CrosswordProvider>
      <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-900">
        <Navigation />
        <div className="pt-16">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/play" element={<Play />} />
            <Route path="/how-to-play" element={<HowToPlay />} />
          </Routes>
        </div>
      </div>
    </CrosswordProvider>
  );
}

export default App;
