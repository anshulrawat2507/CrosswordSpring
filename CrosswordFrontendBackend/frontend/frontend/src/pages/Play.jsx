import { useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import CrosswordPuzzle from "../components/CrosswordPuzzle";
import CrosswordGame3D from "../components/CrosswordGame3D";

export default function Play() {
  const [is3DMode, setIs3DMode] = useState(true);

  return (
    <div className={is3DMode ? "min-h-screen -m-8 -mt-8" : ""}>
      {/* Mode Toggle */}
      {!is3DMode && (
        <motion.div 
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="text-center mb-6"
        >
          <h1 className="text-3xl font-bold text-blue-600 mb-2">
            Daily Crossword Challenge
          </h1>
          <p className="text-gray-600 mb-4">
            Complete the puzzle to solve it!
          </p>
        </motion.div>
      )}
      
      {/* 3D/2D Toggle Button */}
      <div className={`flex justify-center mb-4 ${is3DMode ? 'absolute top-4 right-4 z-50' : ''}`}>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => setIs3DMode(!is3DMode)}
          className={`px-6 py-3 rounded-full font-bold shadow-lg transition-all ${
            is3DMode 
              ? "bg-white/90 backdrop-blur-sm text-purple-600 hover:bg-white" 
              : "bg-gradient-to-r from-purple-500 to-pink-500 text-white"
          }`}
        >
          {is3DMode ? "🎮 Switch to Classic 2D" : "✨ Switch to 3D Mode"}
        </motion.button>
      </div>

      {/* Game View */}
      <AnimatePresence mode="wait">
        {is3DMode ? (
          <motion.div
            key="3d"
            initial={{ opacity: 0, rotateY: -90 }}
            animate={{ opacity: 1, rotateY: 0 }}
            exit={{ opacity: 0, rotateY: 90 }}
            transition={{ duration: 0.5 }}
          >
            <CrosswordGame3D />
          </motion.div>
        ) : (
          <motion.div
            key="2d"
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.9 }}
            transition={{ duration: 0.3 }}
          >
            <CrosswordPuzzle />
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
