import { createContext, useState, useEffect, useCallback } from 'react';//creating a context for sharing crossword state and actions across the components
import { mockCrosswordData } from '../data/mockCrosswordData';
//mock data is imported from mockCrosswordData.js
export const CrosswordContext = createContext();

export function CrosswordProvider({ children }) {
  const [grid, setGrid] = useState([]);
  const [solution, setSolution] = useState([]);
  const [clues, setClues] = useState({ across: [], down: [] });
  const [userAnswers, setUserAnswers] = useState({});
  const [selectedCell, setSelectedCell] = useState({ row: null, col: null });
  const [selectedDirection, setSelectedDirection] = useState('across');
  const [selectedClueNumber, setSelectedClueNumber] = useState(null);
  const [highlightedCells, setHighlightedCells] = useState({});
  const [cellNumbers, setCellNumbers] = useState({});
  const [completedClues, setCompletedClues] = useState({ across: [], down: [] });
  const [showVictoryModal, setShowVictoryModal] = useState(false);
  const [loading, setLoading] = useState(true);//all state variables are intialized here
//loading is set to true until the crossword data is fetched and processed


  // Initialize with mock data
  useEffect(() => {
    // In a real app, you would fetch from API
    const data = mockCrosswordData;
    setGrid(data.grid);
    setSolution(data.solution);
    setClues(data.clues);
    
    // Calculate cell numbers
    const numbers = {};
    const acrossStarts = new Set();
    const downStarts = new Set();
    
    // Find starting cells for across clues
    data.clues.across.forEach(clue => {
      for (let row = 0; row < data.grid.length; row++) {
        for (let col = 0; col < data.grid[row].length; col++) {
          if (data.grid[row][col] !== '' && (col === 0 || data.grid[row][col-1] === '')) {
            // Check if there's enough space for the word
            let wordLength = 0;
            let c = col;
            while (c < data.grid[row].length && data.grid[row][c] !== '') {
              wordLength++;
              c++;
            }
            
            if (wordLength >= 2) {
              acrossStarts.add(`${row}-${col}`);
            }
          }
        }
      }
    });
    
    // Find starting cells for down clues
    data.clues.down.forEach(clue => {
      for (let row = 0; row < data.grid.length; row++) {
        for (let col = 0; col < data.grid[row].length; col++) {
          if (data.grid[row][col] !== '' && (row === 0 || data.grid[row-1][col] === '')) {
            // Check if there's enough space for the word
            let wordLength = 0;
            let r = row;
            while (r < data.grid.length && data.grid[r][col] !== '') {
              wordLength++;
              r++;
            }
            
            if (wordLength >= 2) {
              downStarts.add(`${row}-${col}`);
            }
          }
        }
      }
    });
    
    // Assign numbers to cells and update state
    let numberCounter = 1;
    for (let row = 0; row < data.grid.length; row++) {
      for (let col = 0; col < data.grid[row].length; col++) {
        const key = `${row}-${col}`;
        if ((acrossStarts.has(key) || downStarts.has(key)) && data.grid[row][col] !== '') {
          numbers[key] = numberCounter++;
        }
      }
    }
    
    setCellNumbers(numbers);
    setLoading(false);
  }, []);

  const selectCell = useCallback((row, col) => {
    if (row < 0 || row >= grid.length || col < 0 || col >= grid[0]?.length || grid[row][col] === '') {
      return;
    }
    
    setSelectedCell({ row, col });
    
    // Find the clue for this cell
    updateHighlightedCells(row, col);
  }, [grid]);

  const updateHighlightedCells = useCallback((row, col) => {
    const newHighlighted = {};
    
    // Highlight cells in the current direction
    if (selectedDirection === 'across') {
      // Find the start of the word
      let startCol = col;
      while (startCol > 0 && grid[row][startCol - 1] !== '') {
        startCol--;
      }
      
      // Highlight all cells in the word
      let c = startCol;
      while (c < grid[row].length && grid[row][c] !== '') {
        newHighlighted[`${row}-${c}`] = true;
        c++;
      }
      
      // Find the clue number for this word
      for (const [key, value] of Object.entries(cellNumbers)) {
        const [r, c] = key.split('-').map(Number);
        if (r === row && c === startCol) {
          const clue = clues.across.find(cl => cl.number === value);
          if (clue) {
            setSelectedClueNumber(value);
          }
          break;
        }
      }
    } else {
      // Find the start of the word
      let startRow = row;
      while (startRow > 0 && grid[startRow - 1][col] !== '') {
        startRow--;
      }
      
      // Highlight all cells in the word
      let r = startRow;
      while (r < grid.length && grid[r][col] !== '') {
        newHighlighted[`${r}-${col}`] = true;
        r++;
      }
      
      // Find the clue number for this word
      for (const [key, value] of Object.entries(cellNumbers)) {
        const [r, c] = key.split('-').map(Number);
        if (r === startRow && c === col) {
          const clue = clues.down.find(cl => cl.number === value);
          if (clue) {
            setSelectedClueNumber(value);
          }
          break;
        }
      }
    }
    
    setHighlightedCells(newHighlighted);
  }, [grid, selectedDirection, cellNumbers, clues]);

  const selectClue = useCallback((direction, number) => {
    setSelectedDirection(direction);
    setSelectedClueNumber(number);
    
    // Find the starting cell for this clue
    for (const [key, value] of Object.entries(cellNumbers)) {
      if (value === number) {
        const [row, col] = key.split('-').map(Number);
        setSelectedCell({ row, col });
        updateHighlightedCells(row, col);
        break;
      }
    }
  }, [cellNumbers, updateHighlightedCells]);

  const updateAnswer = useCallback((row, col, value) => {
    const newAnswers = { ...userAnswers };
    if (!newAnswers[row]) newAnswers[row] = {};
    newAnswers[row][col] = value.toUpperCase();
    setUserAnswers(newAnswers);
    
    // Check if any clues are completed
    checkCompletedClues(newAnswers);
    
    // Check if the puzzle is completed
    checkVictory(newAnswers);
  }, [userAnswers]);

  const moveToNextCell = useCallback(() => {
    const { row, col } = selectedCell;
    
    if (selectedDirection === 'across') {
      // Move right
      if (col + 1 < grid[row].length && grid[row][col + 1] !== '') {
        selectCell(row, col + 1);
      }
    } else {
      // Move down
      if (row + 1 < grid.length && grid[row + 1][col] !== '') {
        selectCell(row + 1, col);
      }
    }
  }, [selectedCell, selectedDirection, grid, selectCell]);

  const moveToPrevCell = useCallback(() => {
    const { row, col } = selectedCell;
    
    if (selectedDirection === 'across') {
      // Move left
      if (col > 0 && grid[row][col - 1] !== '') {
        selectCell(row, col - 1);
      }
    } else {
      // Move up
      if (row > 0 && grid[row - 1][col] !== '') {
        selectCell(row - 1, col);
      }
    }
  }, [selectedCell, selectedDirection, grid, selectCell]);

  const toggleDirection = useCallback(() => {
    setSelectedDirection(prev => prev === 'across' ? 'down' : 'across');
    const { row, col } = selectedCell;
    updateHighlightedCells(row, col);
  }, [selectedCell, updateHighlightedCells]);

  const checkCompletedClues = useCallback((answers) => {
    const newCompleted = { across: [], down: [] };
    
    // Check across clues
    clues.across.forEach(clue => {
      let isComplete = true;
      
      // Find the starting cell for this clue
      let startCell = null;
      for (const [key, value] of Object.entries(cellNumbers)) {
        if (value === clue.number) {
          startCell = key;
          break;
        }
      }
      
      if (startCell) {
        const [startRow, startCol] = startCell.split('-').map(Number);
        
        // Check if all cells in this word are filled correctly
        let col = startCol;
        let answerIndex = 0;
        
        while (col < grid[startRow].length && grid[startRow][col] !== '' && answerIndex < clue.answer.length) {
          const userAnswer = answers[startRow]?.[col];
          if (!userAnswer || userAnswer !== solution[startRow][col]) {
            isComplete = false;
            break;
          }
          col++;
          answerIndex++;
        }
        
        if (isComplete) {
          newCompleted.across.push(clue.number);
        }
      }
    });
    
    // Check down clues
    clues.down.forEach(clue => {
      let isComplete = true;
      
      // Find the starting cell for this clue
      let startCell = null;
      for (const [key, value] of Object.entries(cellNumbers)) {
        if (value === clue.number) {
          startCell = key;
          break;
        }
      }
      
      if (startCell) {
        const [startRow, startCol] = startCell.split('-').map(Number);
        
        // Check if all cells in this word are filled correctly
        let row = startRow;
        let answerIndex = 0;
        
        while (row < grid.length && grid[row][startCol] !== '' && answerIndex < clue.answer.length) {
          const userAnswer = answers[row]?.[startCol];
          if (!userAnswer || userAnswer !== solution[row][startCol]) {
            isComplete = false;
            break;
          }
          row++;
          answerIndex++;
        }
        
        if (isComplete) {
          newCompleted.down.push(clue.number);
        }
      }
    });
    
    setCompletedClues(newCompleted);
  }, [clues, cellNumbers, grid, solution]);

  const checkVictory = useCallback((answers) => {
    // Check if all cells are filled correctly
    for (let row = 0; row < grid.length; row++) {
      for (let col = 0; col < grid[row].length; col++) {
        if (grid[row][col] !== '') {
          const userAnswer = answers[row]?.[col];
          if (!userAnswer || userAnswer !== solution[row][col]) {
            return false;
          }
        }
      }
    }
    
    // If we got here, all cells are correct!
    setShowVictoryModal(true);
    return true;
  }, [grid, solution]);

  const resetPuzzle = useCallback(() => {
    setUserAnswers({});
    setCompletedClues({ across: [], down: [] });
    setShowVictoryModal(false);
  }, []);

  const fillingWithSolution = useCallback(() => {
    const newAnswers = {};
    for (let row = 0; row < solution.length; row++) {
      newAnswers[row] = {};
      for (let col = 0; col < solution[row].length; col++) {
        if (solution[row][col] !== '') {
          newAnswers[row][col] = solution[row][col];
        }
      }
    }
    setUserAnswers(newAnswers);
    
    setTimeout(() => setShowVictoryModal(true), 500);
  }, [solution]);

  return (
    <CrosswordContext.Provider value={{
      grid,
      clues,
      userAnswers,
      selectedCell,
      selectedDirection,
      selectedClueNumber,
      highlightedCells,
      cellNumbers,
      completedClues,
      showVictoryModal,
      loading,
      selectCell,
      selectClue,
      updateAnswer,
      moveToNextCell,
      moveToPrevCell,
      toggleDirection,
      resetPuzzle,
      fillingWithSolution,
      setShowVictoryModal
    }}>
      {children}
    </CrosswordContext.Provider>
  );
}
