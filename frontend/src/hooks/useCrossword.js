import { useContext } from 'react';
import { CrosswordContext } from '../contexts/CrosswordContext';
//defines a custom hook that allows components to access the crossword context
export default function useCrossword() {
  return useContext(CrosswordContext);
}
