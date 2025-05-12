package com.example.demo;

public class CrosswordPlacer {
        public static final int SIZE = 7;

        public static boolean placeWords(char[][] board, String[] words, int index) {
            if (index == words.length) return true;

            String word = words[index];
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (canPlaceHorizontally(board, word, row, col)) {
                        boolean[] placed = placeHorizontally(board, word, row, col);
                        char pre = setXBeforeAfterHorizontal(board, word, row, col, false);  // before
                        char post = setXBeforeAfterHorizontal(board, word, row, col, true);  // after

                        if (placeWords(board, words, index + 1)) return true;

                        unplaceHorizontally(board, placed, row, col);
                        unsetXBeforeAfterHorizontal(board, row, col, word.length(), pre, post);
                    }

                    if (canPlaceVertically(board, word, row, col)) {
                        boolean[] placed = placeVertically(board, word, row, col);
                        char pre = setXBeforeAfterVertical(board, word, row, col, false);   // before
                        char post = setXBeforeAfterVertical(board, word, row, col, true);   // after

                        if (placeWords(board, words, index + 1)) return true;

                        unplaceVertically(board, placed, row, col);
                        unsetXBeforeAfterVertical(board, row, col, word.length(), pre, post);
                    }
                }
            }
            return false;
        }

        private static boolean canPlaceHorizontally(char[][] board, String word, int row, int col) {
            if (col + word.length() > SIZE) return false;

            // Ensure no x in word path
            for (int i = 0; i < word.length(); i++) {
                char cell = board[row][col + i];
                if (cell == 'x' || (cell != '+' && cell != word.charAt(i))) return false;
            }

            // Check before and after cell
            if (col > 0 && board[row][col - 1] != '+') return false;
            if (col + word.length() < SIZE && board[row][col + word.length()] != '+') return false;

            return true;
        }

        private static boolean[] placeHorizontally(char[][] board, String word, int row, int col) {
            boolean[] placed = new boolean[word.length()];
            for (int i = 0; i < word.length(); i++) {
                if (board[row][col + i] == '+') {
                    board[row][col + i] = word.charAt(i);
                    placed[i] = true;
                }
            }
            return placed;
        }

        private static void unplaceHorizontally(char[][] board, boolean[] placed, int row, int col) {
            for (int i = 0; i < placed.length; i++) {
                if (placed[i]) board[row][col + i] = '+';
            }
        }

        private static char setXBeforeAfterHorizontal(char[][] board, String word, int row, int col, boolean after) {
            int pos = after ? col + word.length() : col - 1;
            if (pos >= 0 && pos < SIZE) {
                char old = board[row][pos];
                board[row][pos] = 'x';
                return old;
            }
            return ' ';
        }

        private static void unsetXBeforeAfterHorizontal(char[][] board, int row, int col, int len, char before, char after) {
            if (col - 1 >= 0 && before != ' ') board[row][col - 1] = before;
            if (col + len < SIZE && after != ' ') board[row][col + len] = after;
        }

        private static boolean canPlaceVertically(char[][] board, String word, int row, int col) {
            if (row + word.length() > SIZE) return false;

            for (int i = 0; i < word.length(); i++) {
                char cell = board[row + i][col];
                if (cell == 'x' || (cell != '+' && cell != word.charAt(i))) return false;
            }

            if (row > 0 && board[row - 1][col] != '+') return false;
            if (row + word.length() < SIZE && board[row + word.length()][col] != '+') return false;

            return true;
        }

        private static boolean[] placeVertically(char[][] board, String word, int row, int col) {
            boolean[] placed = new boolean[word.length()];
            for (int i = 0; i < word.length(); i++) {
                if (board[row + i][col] == '+') {
                    board[row + i][col] = word.charAt(i);
                    placed[i] = true;
                }
            }
            return placed;
        }

        private static void unplaceVertically(char[][] board, boolean[] placed, int row, int col) {
            for (int i = 0; i < placed.length; i++) {
                if (placed[i]) board[row + i][col] = '+';
            }
        }

        private static char setXBeforeAfterVertical(char[][] board, String word, int row, int col, boolean after) {
            int pos = after ? row + word.length() : row - 1;
            if (pos >= 0 && pos < SIZE) {
                char old = board[pos][col];
                board[pos][col] = 'x';
                return old;
            }
            return ' ';
        }

        private static void unsetXBeforeAfterVertical(char[][] board, int row, int col, int len, char before, char after) {
            if (row - 1 >= 0 && before != ' ') board[row - 1][col] = before;
            if (row + len < SIZE && after != ' ') board[row + len][col] = after;
        }
    }

