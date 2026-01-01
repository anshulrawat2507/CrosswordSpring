import { Link } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { useEffect } from "react";
import confetti from "canvas-confetti";
import useCrossword from "../hooks/useCrossword";

export default function VictoryModal() {
  const { resetPuzzle, setShowVictoryModal } = useCrossword();

  useEffect(() => {
    // Trigger confetti on mount
    const duration = 3000;
    const end = Date.now() + duration;

    const frame = () => {
      confetti({
        particleCount: 3,
        angle: 60,
        spread: 55,
        origin: { x: 0 },
        colors: ["#fbbf24", "#22c55e", "#3b82f6", "#ef4444", "#8b5cf6"]
      });
      confetti({
        particleCount: 3,
        angle: 120,
        spread: 55,
        origin: { x: 1 },
        colors: ["#fbbf24", "#22c55e", "#3b82f6", "#ef4444", "#8b5cf6"]
      });

      if (Date.now() < end) {
        requestAnimationFrame(frame);
      }
    };
    frame();
  }, []);

  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 p-4"
        onClick={() => setShowVictoryModal(false)}
      >
        <motion.div
          initial={{ scale: 0.5, rotateY: -90 }}
          animate={{ scale: 1, rotateY: 0 }}
          exit={{ scale: 0.5, rotateY: 90 }}
          transition={{ type: "spring", damping: 15 }}
          className="bg-gradient-to-br from-white to-purple-50 rounded-3xl shadow-2xl p-8 max-w-md w-full text-center"
          onClick={e => e.stopPropagation()}
        >
          {/* Animated emoji */}
          <motion.div
            animate={{ 
              scale: [1, 1.2, 1],
              rotate: [0, 10, -10, 0]
            }}
            transition={{ duration: 0.5, repeat: Infinity, repeatDelay: 1 }}
            className="text-7xl mb-6"
          >
            🎉
          </motion.div>
          
          <h2 className="text-3xl font-bold mb-2">
            <span className="bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent">
              Congratulations!
            </span>
          </h2>
          
          <p className="text-gray-600 mb-6 text-lg">
            You've successfully completed the crossword puzzle!
          </p>

          {/* Stars animation */}
          <div className="flex justify-center gap-2 mb-6">
            {[0, 1, 2].map((i) => (
              <motion.span
                key={i}
                initial={{ scale: 0, rotate: -180 }}
                animate={{ scale: 1, rotate: 0 }}
                transition={{ delay: 0.2 + i * 0.1, type: "spring" }}
                className="text-4xl"
              >
                ⭐
              </motion.span>
            ))}
          </div>

          <div className="flex flex-col sm:flex-row gap-3 justify-center">
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => {
                resetPuzzle();
                setShowVictoryModal(false);
              }}
              className="px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-full font-bold shadow-lg hover:shadow-xl transition-all"
            >
              🎲 Play Again
            </motion.button>
            <Link
              to="/"
              onClick={() => setShowVictoryModal(false)}
            >
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="px-6 py-3 bg-gray-100 text-gray-700 rounded-full font-bold hover:bg-gray-200 transition-all w-full"
              >
                🏠 Home
              </motion.button>
            </Link>
          </div>
        </motion.div>
      </motion.div>
    </AnimatePresence>
  );
}
