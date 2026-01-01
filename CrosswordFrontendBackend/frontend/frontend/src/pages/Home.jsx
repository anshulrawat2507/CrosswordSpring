import React, { useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import { motion } from "framer-motion";
import { Canvas, useFrame } from "@react-three/fiber";
import { Text3D, Float, Stars, OrbitControls } from "@react-three/drei";
import * as THREE from "three";

// Floating 3D Letter Component
function FloatingLetter({ letter, position, color, delay = 0 }) {
  const meshRef = useRef();
  
  useFrame((state) => {
    if (meshRef.current) {
      meshRef.current.rotation.y = Math.sin(state.clock.elapsedTime * 0.5 + delay) * 0.1;
      meshRef.current.position.y = position[1] + Math.sin(state.clock.elapsedTime + delay) * 0.2;
    }
  });

  return (
    <Float speed={2} rotationIntensity={0.5} floatIntensity={0.5}>
      <mesh ref={meshRef} position={position}>
        <boxGeometry args={[1, 1, 0.3]} />
        <meshStandardMaterial color={color} metalness={0.3} roughness={0.4} />
        <mesh position={[0, 0, 0.2]}>
          <planeGeometry args={[0.8, 0.8]} />
          <meshBasicMaterial color="#ffffff" />
        </mesh>
      </mesh>
    </Float>
  );
}

// Animated 3D Scene for Hero
function HeroScene() {
  const letters = ["C", "R", "O", "S", "S"];
  const colors = ["#3b82f6", "#8b5cf6", "#ec4899", "#f59e0b", "#22c55e"];
  
  return (
    <>
      <ambientLight intensity={0.5} />
      <directionalLight position={[10, 10, 5]} intensity={1} />
      <pointLight position={[-10, -10, 5]} color="#8b5cf6" intensity={0.5} />
      
      <Stars radius={50} depth={50} count={500} factor={4} saturation={0} fade speed={1} />
      
      {letters.map((letter, i) => (
        <FloatingLetter
          key={i}
          letter={letter}
          position={[i * 1.5 - 3, 0, 0]}
          color={colors[i]}
          delay={i * 0.5}
        />
      ))}
      
      <OrbitControls enableZoom={false} enablePan={false} autoRotate autoRotateSpeed={0.5} />
    </>
  );
}

// Feature Card Component
function FeatureCard({ icon, title, description, delay }) {
  return (
    <motion.div
      initial={{ opacity: 0, y: 30 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay, duration: 0.5 }}
      whileHover={{ scale: 1.05, y: -5 }}
      className="bg-white/80 backdrop-blur-sm rounded-2xl p-6 shadow-xl hover:shadow-2xl transition-all"
    >
      <div className="text-4xl mb-4">{icon}</div>
      <h3 className="text-xl font-bold text-gray-800 mb-2">{title}</h3>
      <p className="text-gray-600">{description}</p>
    </motion.div>
  );
}

const Home = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-800 -m-8 -mt-8 overflow-hidden">
      {/* Hero Section */}
      <div className="relative h-screen flex flex-col items-center justify-center">
        {/* 3D Canvas Background */}
        <div className="absolute inset-0 z-0">
          <Canvas camera={{ position: [0, 0, 8], fov: 50 }}>
            <HeroScene />
          </Canvas>
        </div>
        
        {/* Content Overlay */}
        <div className="relative z-10 text-center px-6">
          <motion.h1
            initial={{ opacity: 0, y: -50 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8 }}
            className="text-5xl md:text-7xl font-bold text-white mb-6 drop-shadow-2xl"
          >
            <span className="bg-gradient-to-r from-blue-400 via-purple-400 to-pink-400 bg-clip-text text-transparent">
              Crossword
            </span>
            <br />
            <span className="text-white">Adventure</span>
          </motion.h1>

          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.3, duration: 0.8 }}
            className="text-xl md:text-2xl text-gray-200 mb-10 max-w-2xl mx-auto"
          >
            Experience puzzles like never before with stunning 3D visuals, 
            real-time scoring, and endless word challenges!
          </motion.p>

          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.6, duration: 0.5 }}
            className="flex flex-col sm:flex-row gap-4 justify-center"
          >
            <Link to="/play">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="px-10 py-4 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-full font-bold text-lg shadow-2xl hover:shadow-blue-500/25 transition-all"
              >
                🎮 Play Now
              </motion.button>
            </Link>
            <Link to="/how-to-play">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                className="px-10 py-4 bg-white/10 backdrop-blur-sm border-2 border-white/30 text-white rounded-full font-bold text-lg hover:bg-white/20 transition-all"
              >
                📖 How to Play
              </motion.button>
            </Link>
          </motion.div>
        </div>

        {/* Scroll Indicator */}
        <motion.div
          animate={{ y: [0, 10, 0] }}
          transition={{ duration: 1.5, repeat: Infinity }}
          className="absolute bottom-10 text-white/60"
        >
          <svg className="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V3" />
          </svg>
        </motion.div>
      </div>

      {/* Features Section */}
      <div className="relative py-20 px-6 bg-gradient-to-b from-transparent to-indigo-950">
        <motion.h2
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          className="text-4xl font-bold text-center text-white mb-16"
        >
          Why You'll Love It
        </motion.h2>

        <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-3 gap-8">
          <FeatureCard
            icon="🎨"
            title="Stunning 3D Graphics"
            description="Experience crosswords in a whole new dimension with beautiful Three.js powered visuals."
            delay={0.2}
          />
          <FeatureCard
            icon="🏆"
            title="Score & Streaks"
            description="Compete with yourself! Build combos, earn points, and track your progress."
            delay={0.4}
          />
          <FeatureCard
            icon="💡"
            title="Smart Hints"
            description="Stuck on a clue? Use hints wisely to keep your momentum going."
            delay={0.6}
          />
        </div>
      </div>

      {/* Stats Section */}
      <div className="py-20 px-6 bg-indigo-950">
        <div className="max-w-4xl mx-auto grid grid-cols-2 md:grid-cols-4 gap-8">
          {[
            { value: "500+", label: "Words" },
            { value: "10+", label: "Puzzles" },
            { value: "3D", label: "Experience" },
            { value: "∞", label: "Fun" },
          ].map((stat, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, scale: 0.5 }}
              whileInView={{ opacity: 1, scale: 1 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.1 }}
              className="text-center"
            >
              <div className="text-4xl md:text-5xl font-bold bg-gradient-to-r from-blue-400 to-purple-400 bg-clip-text text-transparent">
                {stat.value}
              </div>
              <div className="text-gray-400 mt-2">{stat.label}</div>
            </motion.div>
          ))}
        </div>
      </div>

      {/* CTA Section */}
      <div className="py-20 px-6 bg-gradient-to-r from-purple-900 to-pink-900">
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          className="max-w-2xl mx-auto text-center"
        >
          <h2 className="text-3xl md:text-4xl font-bold text-white mb-6">
            Ready to Challenge Your Mind?
          </h2>
          <p className="text-gray-300 mb-8 text-lg">
            Join thousands of puzzle enthusiasts and start your crossword journey today!
          </p>
          <Link to="/play">
            <motion.button
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.95 }}
              className="px-12 py-5 bg-white text-purple-900 rounded-full font-bold text-xl shadow-2xl hover:shadow-white/25 transition-all"
            >
              🚀 Start Playing Free
            </motion.button>
          </Link>
        </motion.div>
      </div>

      {/* Footer */}
      <footer className="py-8 px-6 bg-indigo-950 text-center text-gray-500">
        <p>Made with ❤️ for puzzle lovers everywhere</p>
      </footer>
    </div>
  );
};

export default Home;
