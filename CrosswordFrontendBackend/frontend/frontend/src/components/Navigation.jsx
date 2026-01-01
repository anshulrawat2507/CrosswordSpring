import { Link, useLocation } from "react-router-dom";
import { motion } from "framer-motion";

// this component renders the nav bar at the top with links to Home,Play and HOW to play
export default function Navigation() {
  const location = useLocation();
  
  const navLinks = [
    { to: "/", label: "Home", icon: "🏠" },
    { to: "/play", label: "Play", icon: "🎮" },
    { to: "/how-to-play", label: "How to Play", icon: "📖" },
  ];

  return (
    <motion.nav 
      initial={{ y: -100 }}
      animate={{ y: 0 }}
      className="fixed top-0 left-0 right-0 z-50 bg-gradient-to-r from-indigo-900/90 via-purple-900/90 to-pink-900/90 backdrop-blur-md text-white shadow-lg"
    >
      <div className="container mx-auto px-4 py-3 flex items-center justify-between">
        <Link to="/" className="flex items-center gap-2">
          <motion.span 
            animate={{ rotate: [0, 10, -10, 0] }}
            transition={{ duration: 2, repeat: Infinity }}
            className="text-2xl"
          >
            🧩
          </motion.span>
          <span className="text-xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
            Crossword 3D
          </span>
        </Link>
        
        <div className="flex items-center gap-1">
          {navLinks.map((link) => {
            const isActive = location.pathname === link.to;
            return (
              <Link key={link.to} to={link.to}>
                <motion.div
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  className={`px-4 py-2 rounded-full font-medium transition-all ${
                    isActive
                      ? "bg-white/20 text-white"
                      : "text-gray-300 hover:text-white hover:bg-white/10"
                  }`}
                >
                  <span className="mr-1">{link.icon}</span>
                  <span className="hidden sm:inline">{link.label}</span>
                </motion.div>
              </Link>
            );
          })}
        </div>
      </div>
    </motion.nav>
  );
}
