import { motion } from "framer-motion";

const InstructionCard = ({ icon, title, children, delay }) => (
  <motion.div
    initial={{ opacity: 0, y: 30 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ delay, duration: 0.5 }}
    className="bg-white/10 backdrop-blur-sm rounded-2xl p-6 border border-white/20"
  >
    <div className="flex items-center gap-3 mb-4">
      <span className="text-3xl">{icon}</span>
      <h2 className="text-xl font-bold text-white">{title}</h2>
    </div>
    <div className="text-gray-300">{children}</div>
  </motion.div>
);

const KeyBadge = ({ children }) => (
  <span className="inline-block px-3 py-1 bg-gradient-to-r from-blue-500 to-purple-500 text-white rounded-lg font-mono text-sm mx-1">
    {children}
  </span>
);

export default function HowToPlay() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-900 py-12 px-4">
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-4xl mx-auto"
      >
        <h1 className="text-4xl md:text-5xl font-bold text-center mb-4">
          <span className="bg-gradient-to-r from-blue-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
            How To Play
          </span>
        </h1>
        <p className="text-center text-gray-300 mb-12 text-lg">
          Master the art of crossword puzzles with these simple tips!
        </p>

        <div className="grid gap-6">
          <InstructionCard icon="🧩" title="Crossword Basics" delay={0.1}>
            <p className="mb-4">
              Crossword puzzles consist of a grid of white and black squares. Your
              goal is to fill in the white squares with letters to form words or
              phrases by solving the clues provided.
            </p>
            <ul className="space-y-2">
              <li className="flex items-center gap-2">
                <span className="text-green-400">➡️</span>
                Words read left-to-right (Across) or top-to-bottom (Down)
              </li>
              <li className="flex items-center gap-2">
                <span className="text-purple-400">⬛</span>
                Black squares separate words
              </li>
              <li className="flex items-center gap-2">
                <span className="text-blue-400">🔢</span>
                Each clue has a corresponding number in the grid
              </li>
            </ul>
          </InstructionCard>

          <InstructionCard icon="⌨️" title="Keyboard Controls" delay={0.2}>
            <div className="grid md:grid-cols-2 gap-4">
              <div className="space-y-3">
                <div className="flex items-center gap-2">
                  <KeyBadge>←</KeyBadge>
                  <KeyBadge>→</KeyBadge>
                  <KeyBadge>↑</KeyBadge>
                  <KeyBadge>↓</KeyBadge>
                  <span>Navigate cells</span>
                </div>
                <div className="flex items-center gap-2">
                  <KeyBadge>A-Z</KeyBadge>
                  <span>Enter letters</span>
                </div>
              </div>
              <div className="space-y-3">
                <div className="flex items-center gap-2">
                  <KeyBadge>Backspace</KeyBadge>
                  <span>Delete letter</span>
                </div>
                <div className="flex items-center gap-2">
                  <KeyBadge>Enter</KeyBadge>
                  <span>Toggle direction</span>
                </div>
              </div>
            </div>
          </InstructionCard>

          <InstructionCard icon="🎮" title="3D Mode Features" delay={0.3}>
            <ul className="space-y-3">
              <li className="flex items-center gap-3">
                <span className="text-2xl">⏱️</span>
                <div>
                  <strong className="text-white">Timer:</strong> Track how fast you can solve the puzzle
                </div>
              </li>
              <li className="flex items-center gap-3">
                <span className="text-2xl">🏆</span>
                <div>
                  <strong className="text-white">Scoring:</strong> Earn points for correct answers. Build streaks for bonus points!
                </div>
              </li>
              <li className="flex items-center gap-3">
                <span className="text-2xl">💡</span>
                <div>
                  <strong className="text-white">Hints:</strong> Use up to 3 hints per puzzle (costs 50 points each)
                </div>
              </li>
              <li className="flex items-center gap-3">
                <span className="text-2xl">🔥</span>
                <div>
                  <strong className="text-white">Streaks:</strong> Consecutive correct answers multiply your score!
                </div>
              </li>
            </ul>
          </InstructionCard>

          <InstructionCard icon="💡" title="Pro Tips" delay={0.4}>
            <ul className="space-y-3">
              <li className="flex items-start gap-3">
                <span className="text-yellow-400">⭐</span>
                <span>Start with the clues you're most confident about</span>
              </li>
              <li className="flex items-start gap-3">
                <span className="text-yellow-400">⭐</span>
                <span>Use crossing words to help solve difficult clues</span>
              </li>
              <li className="flex items-start gap-3">
                <span className="text-yellow-400">⭐</span>
                <span>Build streaks for maximum points - accuracy matters!</span>
              </li>
              <li className="flex items-start gap-3">
                <span className="text-yellow-400">⭐</span>
                <span>Save hints for truly challenging clues</span>
              </li>
            </ul>
          </InstructionCard>
        </div>

        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6 }}
          className="text-center mt-12"
        >
          <a href="/play">
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="px-10 py-4 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-full font-bold text-lg shadow-2xl hover:shadow-blue-500/25 transition-all"
            >
              🎮 Start Playing Now
            </motion.button>
          </a>
        </motion.div>
      </motion.div>
    </div>
  );
}
