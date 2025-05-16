import { useContext } from 'react';
import { CrosswordContext } from '../contexts/CrosswordContext';

export default function useCrossword() {
  return useContext(CrosswordContext);
}
