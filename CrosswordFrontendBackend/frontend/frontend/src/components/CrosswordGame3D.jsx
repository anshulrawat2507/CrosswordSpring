import React, { useState, useEffect, useRef, useMemo } from "react";
import { Canvas, useFrame, useThree } from "@react-three/fiber";
import { Text, RoundedBox, Float, Stars, Environment } from "@react-three/drei";
import { motion, AnimatePresence } from "framer-motion";
import confetti from "canvas-confetti";
import useCrossword from "../hooks/useCrossword";
import * as THREE from "three";

// 3D Cell Component
function Cell3D({ 
  position, 
  letter, 
  userLetter, 
  isSelected, 
  isHighlighted, 
  cellNumber, 
  isEmpty,
  onClick,
  highlightStatus,
  isAnimating
}) {
  const meshRef = useRef();
  const [hovered, setHovered] = useState(false);
  
  // Animation state
  const [popScale, setPopScale] = useState(1);
  
  useFrame((state) => {
    if (meshRef.current) {
      // Gentle floating animation for selected cell
      if (isSelected) {
        meshRef.current.position.z = Math.sin(state.clock.elapsedTime * 3) * 0.05 + 0.1;
      } else {
        meshRef.current.position.z = THREE.MathUtils.lerp(meshRef.current.position.z, 0, 0.1);
      }
      
      // Pop animation
      if (isAnimating) {
        meshRef.current.scale.setScalar(THREE.MathUtils.lerp(meshRef.current.scale.x, 1.2, 0.1));
      } else {
        meshRef.current.scale.setScalar(THREE.MathUtils.lerp(meshRef.current.scale.x, 1, 0.1));
      }
    }
  });

  // Determine color
  let baseColor = "#ffffff";
  let emissiveColor = "#000000";
  let emissiveIntensity = 0;
  
  if (isEmpty) {
    baseColor = "#1a1a2e";
  } else if (highlightStatus === "correct") {
    baseColor = "#4ade80";
    emissiveColor = "#22c55e";
    emissiveIntensity = 0.3;
  } else if (highlightStatus === "incorrect") {
    baseColor = "#f87171";
    emissiveColor = "#ef4444";
    emissiveIntensity = 0.3;
  } else if (isSelected) {
    baseColor = "#fbbf24";
    emissiveColor = "#f59e0b";
    emissiveIntensity = 0.5;
  } else if (isHighlighted) {
    baseColor = "#60a5fa";
    emissiveColor = "#3b82f6";
    emissiveIntensity = 0.2;
  }

  if (isEmpty) {
    return (
      <RoundedBox 
        position={position} 
        args={[0.9, 0.9, 0.2]} 
        radius={0.08} 
        smoothness={4}
      >
        <meshStandardMaterial color="#1a1a2e" roughness={0.8} />
      </RoundedBox>
    );
  }

  return (
    <group position={position}>
      <RoundedBox
        ref={meshRef}
        args={[0.9, 0.9, 0.2]}
        radius={0.08}
        smoothness={4}
        onClick={onClick}
        onPointerOver={() => setHovered(true)}
        onPointerOut={() => setHovered(false)}
      >
        <meshStandardMaterial 
          color={hovered ? "#93c5fd" : baseColor}
          emissive={emissiveColor}
          emissiveIntensity={emissiveIntensity}
          roughness={0.3}
          metalness={0.1}
        />
      </RoundedBox>
      
      {/* Cell number */}
      {cellNumber && (
        <Text
          position={[-0.3, 0.3, 0.15]}
          fontSize={0.2}
          color="#666666"
          anchorX="left"
          anchorY="top"
        >
          {cellNumber}
        </Text>
      )}
      
      {/* User's letter */}
      {userLetter && (
        <Text
          position={[0, 0, 0.15]}
          fontSize={0.5}
          color="#1e293b"
          anchorX="center"
          anchorY="middle"
          font="/fonts/inter-bold.woff"
        >
          {userLetter}
        </Text>
      )}
    </group>
  );
}

// 3D Grid Component
function Grid3D({ 
  grid, 
  userAnswers, 
  selectedCell, 
  highlightedCells, 
  cellNumbers, 
  onCellClick,
  highlightStatuses 
}) {
  const gridSize = grid.length;
  const offset = (gridSize - 1) / 2;
  
  return (
    <group>
      {grid.map((row, rowIdx) =>
        row.map((cell, colIdx) => {
          const isSelected = selectedCell.row === rowIdx && selectedCell.col === colIdx;
          const cellKey = `${rowIdx}-${colIdx}`;
          const isHighlighted = highlightedCells[cellKey];
          const highlightStatus = highlightStatuses?.[cellKey];
          
          return (
            <Cell3D
              key={cellKey}
              position={[colIdx - offset, -(rowIdx - offset), 0]}
              letter={cell}
              userLetter={userAnswers[rowIdx]?.[colIdx] || ""}
              isSelected={isSelected}
              isHighlighted={isHighlighted}
              cellNumber={cellNumbers[cellKey]}
              isEmpty={cell === ""}
              onClick={() => onCellClick(rowIdx, colIdx)}
              highlightStatus={highlightStatus}
            />
          );
        })
      )}
    </group>
  );
}

// Camera controller for smooth animations
function CameraController() {
  const { camera } = useThree();
  
  useFrame((state) => {
    // Subtle camera movement
    camera.position.x = Math.sin(state.clock.elapsedTime * 0.1) * 0.3;
    camera.position.y = Math.cos(state.clock.elapsedTime * 0.1) * 0.3;
    camera.lookAt(0, 0, 0);
  });
  
  return null;
}

// Particle system for celebrations
function CelebrationParticles({ active }) {
  const particlesRef = useRef();
  const count = 100;
  
  const positions = useMemo(() => {
    const pos = new Float32Array(count * 3);
    for (let i = 0; i < count; i++) {
      pos[i * 3] = (Math.random() - 0.5) * 20;
      pos[i * 3 + 1] = Math.random() * 10 - 5;
      pos[i * 3 + 2] = (Math.random() - 0.5) * 10;
    }
    return pos;
  }, []);
  
  const colors = useMemo(() => {
    const cols = new Float32Array(count * 3);
    const palette = [
      [1, 0.84, 0], // gold
      [0.29, 0.78, 0.51], // green
      [0.24, 0.51, 0.96], // blue
      [0.95, 0.26, 0.21], // red
      [0.61, 0.35, 0.71], // purple
    ];
    for (let i = 0; i < count; i++) {
      const color = palette[Math.floor(Math.random() * palette.length)];
      cols[i * 3] = color[0];
      cols[i * 3 + 1] = color[1];
      cols[i * 3 + 2] = color[2];
    }
    return cols;
  }, []);
  
  useFrame((state, delta) => {
    if (particlesRef.current && active) {
      particlesRef.current.rotation.y += delta * 0.1;
      const positions = particlesRef.current.geometry.attributes.position.array;
      for (let i = 0; i < count; i++) {
        positions[i * 3 + 1] -= delta * 2;
        if (positions[i * 3 + 1] < -5) {
          positions[i * 3 + 1] = 5;
        }
      }
      particlesRef.current.geometry.attributes.position.needsUpdate = true;
    }
  });
  
  if (!active) return null;
  
  return (
    <points ref={particlesRef}>
      <bufferGeometry>
        <bufferAttribute
          attach="attributes-position"
          count={count}
          array={positions}
          itemSize={3}
        />
        <bufferAttribute
          attach="attributes-color"
          count={count}
          array={colors}
          itemSize={3}
        />
      </bufferGeometry>
      <pointsMaterial size={0.15} vertexColors transparent opacity={0.8} />
    </points>
  );
}

// Main 3D Crossword Scene
function CrosswordScene({ 
  grid, 
  userAnswers, 
  selectedCell, 
  highlightedCells, 
  cellNumbers, 
  onCellClick,
  highlightStatuses,
  celebrating 
}) {
  return (
    <>
      <ambientLight intensity={0.5} />
      <directionalLight position={[10, 10, 10]} intensity={1} castShadow />
      <pointLight position={[-10, -10, 5]} intensity={0.5} color="#60a5fa" />
      
      <Stars radius={100} depth={50} count={1000} factor={4} saturation={0} fade speed={1} />
      
      <Grid3D
        grid={grid}
        userAnswers={userAnswers}
        selectedCell={selectedCell}
        highlightedCells={highlightedCells}
        cellNumbers={cellNumbers}
        onCellClick={onCellClick}
        highlightStatuses={highlightStatuses}
      />
      
      <CelebrationParticles active={celebrating} />
      <CameraController />
    </>
  );
}

// Timer Component
function Timer({ isRunning, onTimeUpdate }) {
  const [seconds, setSeconds] = useState(0);
  
  useEffect(() => {
    let interval;
    if (isRunning) {
      interval = setInterval(() => {
        setSeconds(s => {
          const newTime = s + 1;
          onTimeUpdate?.(newTime);
          return newTime;
        });
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [isRunning, onTimeUpdate]);
  
  const formatTime = (totalSeconds) => {
    const mins = Math.floor(totalSeconds / 60);
    const secs = totalSeconds % 60;
    return `${mins.toString().padStart(2, "0")}:${secs.toString().padStart(2, "0")}`;
  };
  
  return (
    <motion.div 
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      className="text-2xl font-mono font-bold text-blue-600 bg-white/90 backdrop-blur-sm px-4 py-2 rounded-lg shadow-lg"
    >
      ⏱️ {formatTime(seconds)}
    </motion.div>
  );
}

// Score Component
function ScoreDisplay({ score, streak }) {
  return (
    <motion.div 
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      className="flex gap-4"
    >
      <div className="text-xl font-bold text-green-600 bg-white/90 backdrop-blur-sm px-4 py-2 rounded-lg shadow-lg">
        🏆 Score: {score}
      </div>
      {streak > 1 && (
        <motion.div 
          initial={{ scale: 0 }}
          animate={{ scale: 1 }}
          className="text-xl font-bold text-orange-500 bg-white/90 backdrop-blur-sm px-4 py-2 rounded-lg shadow-lg"
        >
          🔥 Streak: {streak}x
        </motion.div>
      )}
    </motion.div>
  );
}

// Hint System
function HintButton({ onHint, hintsRemaining }) {
  return (
    <motion.button
      whileHover={{ scale: 1.05 }}
      whileTap={{ scale: 0.95 }}
      onClick={onHint}
      disabled={hintsRemaining === 0}
      className={`px-6 py-3 rounded-lg font-bold shadow-lg transition-all ${
        hintsRemaining > 0 
          ? "bg-gradient-to-r from-amber-400 to-orange-500 text-white hover:shadow-xl" 
          : "bg-gray-300 text-gray-500 cursor-not-allowed"
      }`}
    >
      💡 Hint ({hintsRemaining} left)
    </motion.button>
  );
}

// Progress Bar
function ProgressBar({ progress }) {
  return (
    <div className="w-full bg-gray-200 rounded-full h-4 overflow-hidden shadow-inner">
      <motion.div
        initial={{ width: 0 }}
        animate={{ width: `${progress}%` }}
        transition={{ duration: 0.5, ease: "easeOut" }}
        className="h-full bg-gradient-to-r from-green-400 via-blue-500 to-purple-500 rounded-full"
      />
    </div>
  );
}

// Main 3D Crossword Game Component
export default function CrosswordGame3D() {
  const {
    grid,
    userAnswers,
    selectedCell,
    highlightedCells,
    cellNumbers,
    selectCell,
    updateAnswer,
    moveToNextCell,
    moveToPrevCell,
    toggleDirection,
    solution,
    clues,
    completedClues,
    showVictoryModal,
    setShowVictoryModal,
    resetPuzzle,
    loading,
    solverRunning,
    solverGrid,
    solverStep,
    solverAction,
    startSolver,
    cancelSolver,
    selectedDirection,
    setSelectedDirection,
    setHighlightedCells,
  } = useCrossword();

  const [score, setScore] = useState(0);
  const [streak, setStreak] = useState(0);
  const [hintsRemaining, setHintsRemaining] = useState(3);
  const [celebrating, setCelebrating] = useState(false);
  const [timerRunning, setTimerRunning] = useState(true);
  const [finalTime, setFinalTime] = useState(0);
  const [highlightStatuses, setHighlightStatuses] = useState({});
  
  const containerRef = useRef();

  // Calculate progress
  const calculateProgress = () => {
    if (!grid.length || !solution.length) return 0;
    let total = 0;
    let correct = 0;
    
    for (let row = 0; row < grid.length; row++) {
      for (let col = 0; col < grid[row].length; col++) {
        if (grid[row][col] !== "") {
          total++;
          if (userAnswers[row]?.[col]?.toUpperCase() === solution[row][col]?.toUpperCase()) {
            correct++;
          }
        }
      }
    }
    
    return total > 0 ? Math.round((correct / total) * 100) : 0;
  };

  // Handle keyboard input
  useEffect(() => {
    const handleKeyDown = (e) => {
      if (solverRunning) return;
      if (selectedCell.row === null || selectedCell.col === null) return;

      const { row, col } = selectedCell;

      if (e.key === "ArrowRight") {
        selectCell(row, col + 1);
      } else if (e.key === "ArrowLeft") {
        selectCell(row, col - 1);
      } else if (e.key === "ArrowUp") {
        selectCell(row - 1, col);
      } else if (e.key === "ArrowDown") {
        selectCell(row + 1, col);
      } else if (/^[a-zA-Z]$/.test(e.key)) {
        const letter = e.key.toUpperCase();
        updateAnswer(row, col, letter);
        
        // Check if correct
        if (solution[row]?.[col]?.toUpperCase() === letter) {
          setStreak(s => s + 1);
          setScore(s => s + (10 * Math.max(1, streak)));
          
          // Update highlight status
          setHighlightStatuses(prev => ({
            ...prev,
            [`${row}-${col}`]: "correct"
          }));
        } else {
          setStreak(0);
          setHighlightStatuses(prev => ({
            ...prev,
            [`${row}-${col}`]: "incorrect"
          }));
        }
        
        moveToNextCell();
      } else if (e.key === "Backspace") {
        if (userAnswers[row]?.[col]) {
          updateAnswer(row, col, "");
          setHighlightStatuses(prev => {
            const newStatus = { ...prev };
            delete newStatus[`${row}-${col}`];
            return newStatus;
          });
        } else {
          moveToPrevCell();
        }
      } else if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        toggleDirection();
      }
    };

    window.addEventListener("keydown", handleKeyDown);
    return () => window.removeEventListener("keydown", handleKeyDown);
  }, [selectedCell, solverRunning, selectCell, updateAnswer, moveToNextCell, moveToPrevCell, toggleDirection, userAnswers, solution, streak]);

  // Check for victory
  useEffect(() => {
    if (showVictoryModal) {
      setTimerRunning(false);
      setCelebrating(true);
      
      // Trigger confetti
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
      
      setTimeout(() => setCelebrating(false), duration);
    }
  }, [showVictoryModal]);

  // Handle check solution
  const handleCheckSolution = () => {
    let hasAtLeastOneAnswer = false;
    let allCorrect = true;
    let incorrectCount = 0;
    const newHighlightStatuses = {};

    for (let row = 0; row < grid.length; row++) {
      for (let col = 0; col < grid[row].length; col++) {
        if (grid[row][col] !== "") {
          const userAnswer = (userAnswers[row]?.[col] || "").toUpperCase();
          const correctAnswer = solution[row][col].toUpperCase();
          
          if (userAnswer) {
            hasAtLeastOneAnswer = true;
            if (userAnswer === correctAnswer) {
              newHighlightStatuses[`${row}-${col}`] = "correct";
            } else {
              newHighlightStatuses[`${row}-${col}`] = "incorrect";
              allCorrect = false;
              incorrectCount++;
            }
          } else {
            allCorrect = false;
          }
        }
      }
    }

    setHighlightStatuses(newHighlightStatuses);

    if (!hasAtLeastOneAnswer) {
      // Show message - no answers filled
      return;
    }

    if (allCorrect) {
      setShowVictoryModal(true);
      setTimerRunning(false);
      setCelebrating(true);
    }
  };

  // Handle watch solver
  const handleWatchSolver = () => {
    if (solverRunning) {
      cancelSolver();
    } else {
      startSolver();
      setTimerRunning(false);
    }
  };

  // Handle hint
  const handleHint = () => {
    if (hintsRemaining <= 0) return;
    
    // Find an empty or incorrect cell and fill it
    for (let row = 0; row < grid.length; row++) {
      for (let col = 0; col < grid[row].length; col++) {
        if (grid[row][col] !== "" && 
            userAnswers[row]?.[col]?.toUpperCase() !== solution[row][col]?.toUpperCase()) {
          updateAnswer(row, col, solution[row][col]);
          setHighlightStatuses(prev => ({
            ...prev,
            [`${row}-${col}`]: "correct"
          }));
          setHintsRemaining(h => h - 1);
          setScore(s => Math.max(0, s - 50)); // Penalty for using hint
          return;
        }
      }
    }
  };

  // Handle cell click from 3D scene
  const handleCellClick = (row, col) => {
    selectCell(row, col);
  };

  // Handle new puzzle
  const handleNewPuzzle = async () => {
    if (solverRunning) {
      cancelSolver();
    }
    await resetPuzzle();
    setScore(0);
    setStreak(0);
    setHintsRemaining(3);
    setCelebrating(false);
    setTimerRunning(true);
    setHighlightStatuses({});
    setShowVictoryModal(false);
  };

  // Determine which grid to display (solver grid or regular)
  const displayGrid = solverRunning && solverGrid ? solverGrid : grid;
  const displayAnswers = solverRunning && solverGrid ? 
    // Convert solver grid to answers format
    solverGrid.reduce((acc, row, rowIdx) => {
      acc[rowIdx] = {};
      row.forEach((cell, colIdx) => {
        acc[rowIdx][colIdx] = cell === "" ? "" : cell;
      });
      return acc;
    }, {}) : userAnswers;

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-900">
        <motion.div
          animate={{ rotate: 360 }}
          transition={{ duration: 2, repeat: Infinity, ease: "linear" }}
          className="w-16 h-16 border-4 border-white border-t-transparent rounded-full"
        />
      </div>
    );
  }

  return (
    <div ref={containerRef} className="min-h-screen bg-gradient-to-br from-indigo-900 via-purple-900 to-pink-900 p-4">
      {/* Header with stats */}
      <motion.div 
        initial={{ opacity: 0, y: -50 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex justify-between items-center mb-4 flex-wrap gap-4"
      >
        <Timer isRunning={timerRunning} onTimeUpdate={setFinalTime} />
        <ScoreDisplay score={score} streak={streak} />
      </motion.div>

      {/* Progress bar */}
      <motion.div 
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="mb-4"
      >
        <div className="flex justify-between text-white text-sm mb-1">
          <span>Progress</span>
          <span>{calculateProgress()}%</span>
        </div>
        <ProgressBar progress={calculateProgress()} />
      </motion.div>

      {/* Main game area */}
      <div className="flex flex-col lg:flex-row gap-6">
        {/* 3D Canvas */}
        <motion.div 
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          className="lg:w-2/3 h-[500px] bg-black/20 rounded-2xl overflow-hidden shadow-2xl backdrop-blur-sm relative"
        >
          {/* Solver status overlay */}
          {solverRunning && (
            <div className="absolute top-4 left-4 right-4 z-10 bg-blue-600/90 backdrop-blur-sm text-white p-3 rounded-lg flex justify-between items-center">
              <div className="flex items-center gap-2">
                <motion.div
                  animate={{ rotate: 360 }}
                  transition={{ duration: 1, repeat: Infinity, ease: "linear" }}
                  className="w-5 h-5 border-2 border-white border-t-transparent rounded-full"
                />
                <span className="font-medium">
                  {solverAction === "solved" ? "✅ Solved!" : `Solving... Step ${solverStep || 0}`}
                </span>
              </div>
              <button
                onClick={cancelSolver}
                className="px-3 py-1 bg-white/20 hover:bg-white/30 rounded-md text-sm font-medium transition-all"
              >
                ✕ Close
              </button>
            </div>
          )}
          <Canvas
            camera={{ position: [0, 0, 15], fov: 50 }}
            gl={{ antialias: true }}
          >
            <CrosswordScene
              grid={displayGrid}
              userAnswers={displayAnswers}
              selectedCell={selectedCell}
              highlightedCells={highlightedCells}
              cellNumbers={cellNumbers}
              onCellClick={handleCellClick}
              highlightStatuses={highlightStatuses}
              celebrating={celebrating}
            />
          </Canvas>
        </motion.div>

        {/* Clues Panel */}
        <motion.div 
          initial={{ opacity: 0, x: 50 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:w-1/3 bg-white/90 backdrop-blur-sm rounded-2xl shadow-2xl overflow-hidden"
        >
          {/* Clue tabs */}
          <div className="flex">
            <button
              className={`flex-1 py-4 font-bold text-lg transition-all ${
                selectedDirection === "across"
                  ? "bg-gradient-to-r from-blue-500 to-purple-500 text-white"
                  : "bg-gray-100 text-gray-600 hover:bg-gray-200"
              }`}
              onClick={() => setSelectedDirection("across")}
            >
              ➡️ Across
            </button>
            <button
              className={`flex-1 py-4 font-bold text-lg transition-all ${
                selectedDirection === "down"
                  ? "bg-gradient-to-r from-blue-500 to-purple-500 text-white"
                  : "bg-gray-100 text-gray-600 hover:bg-gray-200"
              }`}
              onClick={() => setSelectedDirection("down")}
            >
              ⬇️ Down
            </button>
          </div>

          {/* Clues list */}
          <div className="p-4 max-h-[350px] overflow-y-auto">
            <AnimatePresence mode="wait">
              <motion.div
                key={selectedDirection}
                initial={{ opacity: 0, x: 20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -20 }}
                className="space-y-2"
              >
                {(selectedDirection === "across" ? clues.across : clues.down).map((clue) => {
                  const isCompleted = completedClues[selectedDirection].includes(clue.number);
                  
                  return (
                    <motion.div
                      key={clue.number}
                      whileHover={{ scale: 1.02, x: 5 }}
                      className={`p-3 rounded-lg cursor-pointer transition-all ${
                        isCompleted 
                          ? "bg-green-100 text-green-700 line-through" 
                          : "bg-gray-50 hover:bg-blue-50"
                      }`}
                      onClick={() => selectCell(clue.row, clue.col)}
                    >
                      <span className="font-bold text-blue-600 mr-2">{clue.number}.</span>
                      {clue.clue}
                      {isCompleted && <span className="ml-2">✅</span>}
                    </motion.div>
                  );
                })}
              </motion.div>
            </AnimatePresence>
          </div>
        </motion.div>
      </div>

      {/* Action buttons */}
      <motion.div 
        initial={{ opacity: 0, y: 50 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex justify-center gap-4 mt-6 flex-wrap"
      >
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={handleCheckSolution}
          disabled={solverRunning}
          className={`px-6 py-3 rounded-lg font-bold shadow-lg transition-all ${
            solverRunning 
              ? "bg-gray-400 cursor-not-allowed" 
              : "bg-gradient-to-r from-blue-500 to-cyan-500 text-white hover:shadow-xl"
          }`}
        >
          ✅ Check Solution
        </motion.button>

        <HintButton onHint={handleHint} hintsRemaining={hintsRemaining} />

        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={handleWatchSolver}
          className={`px-6 py-3 rounded-lg font-bold shadow-lg transition-all ${
            solverRunning
              ? "bg-gradient-to-r from-red-500 to-orange-500 text-white hover:shadow-xl"
              : "bg-gradient-to-r from-green-500 to-emerald-500 text-white hover:shadow-xl"
          }`}
        >
          {solverRunning ? "⏹️ Stop Solver" : "🤖 Watch Solver"}
        </motion.button>
        
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={handleNewPuzzle}
          className="px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-lg font-bold shadow-lg hover:shadow-xl transition-all"
        >
          🎲 New Puzzle
        </motion.button>
      </motion.div>

      {/* Victory Modal */}
      <AnimatePresence>
        {showVictoryModal && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50"
            onClick={() => setShowVictoryModal(false)}
          >
            <motion.div
              initial={{ scale: 0.5, rotateY: -90 }}
              animate={{ scale: 1, rotateY: 0 }}
              exit={{ scale: 0.5, rotateY: 90 }}
              transition={{ type: "spring", damping: 15 }}
              className="bg-white rounded-3xl p-8 max-w-md text-center shadow-2xl"
              onClick={e => e.stopPropagation()}
            >
              <motion.div
                animate={{ 
                  scale: [1, 1.2, 1],
                  rotate: [0, 10, -10, 0]
                }}
                transition={{ duration: 0.5, repeat: Infinity, repeatDelay: 1 }}
                className="text-6xl mb-4"
              >
                🎉
              </motion.div>
              
              <h2 className="text-3xl font-bold bg-gradient-to-r from-purple-600 to-pink-600 bg-clip-text text-transparent mb-4">
                Congratulations!
              </h2>
              
              <p className="text-gray-600 mb-4">You solved the puzzle!</p>
              
              <div className="flex justify-center gap-6 mb-6">
                <div className="text-center">
                  <div className="text-2xl font-bold text-blue-600">{score}</div>
                  <div className="text-sm text-gray-500">Score</div>
                </div>
                <div className="text-center">
                  <div className="text-2xl font-bold text-green-600">
                    {Math.floor(finalTime / 60)}:{(finalTime % 60).toString().padStart(2, "0")}
                  </div>
                  <div className="text-sm text-gray-500">Time</div>
                </div>
              </div>
              
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleNewPuzzle}
                className="px-8 py-3 bg-gradient-to-r from-purple-500 to-pink-500 text-white rounded-full font-bold shadow-lg"
              >
                Play Again 🚀
              </motion.button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
