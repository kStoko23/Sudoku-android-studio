package com.example.actualsudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

// The SudokuGenerator class is responsible for generating a Sudoku puzzle board using a backtracking algorithm that is typically used for solving a Sudoku board.
// The class contains methods that check if a number is safe to put in a cell, checks if a number is already in a row, column, or 3x3 box,
// generates a Sudoku board, and removes numbers from the board.
// The class also has a method to get the solution of the generated board, although it is not currently used.
// The generated board is represented using a 2D array and has a size of 9x9, divided into 3x3 subgrids.
// This class serves as a utility for the Sudoku game board to create a new Sudoku puzzle to play.
//
// Resources:
// https://www.geeksforgeeks.org/sudoku-backtracking-7/
// https://www.youtube.com/watch?v=G_UYXzGuqvM

public class SudokuGenerator {
    //region Fields
    private static final int BOARD_SIZE = 9;
    private int[][] board;
    //endregion
    public int getBoardSize() {
        return BOARD_SIZE;
    }
    // generate(): Generates a valid sudoku board
    public int[][] generate() {
        board = new int[BOARD_SIZE][BOARD_SIZE];
        if (fillBoard(0, 0)) {
            return board;
        }
        return null;
    }
    // fillBoard(): Fills the board using the backtracking algorithm
    private boolean fillBoard(int row, int col) {
        return solveSudoku(board, row, col);
    }
    // isSafe(): Checks if a number is safe to put in the cell by checking row, column, and box
    protected boolean isSafe(int row, int col, int num) {
        return isRowSafe(row, num) && isColSafe(col, num) && isBoxSafe(row - row % 3, col - col % 3, num);
    }
    // isRowSafe(): Checks if a number is safe to put in the row
    private boolean isRowSafe(int row, int num) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }
    // isColSafe(): Checks if a number is safe to put in the column
    private boolean isColSafe(int col, int num) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }
    // isBoxSafe(): Checks if a number is safe to put in the 3x3 box
    private boolean isBoxSafe(int rowStart, int colStart, int num) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[rowStart + row][colStart + col] == num) {
                    return false;
                }
            }
        }
        return true;
    }
    // removeNumber(): Removes a specified number of cells from the board and returns a list of removed numbers
    public List<RemovedNumber> removeNumbers(int  count) {
        Random random = new Random();
        List<RemovedNumber> removedNumbers = new ArrayList<>();

        Set<Integer> removedPositions = new HashSet<>();
        while (removedNumbers.size() < count) {
            int position = random.nextInt(BOARD_SIZE * BOARD_SIZE);
            if (!removedPositions.contains(position)) {
                removedPositions.add(position);
                int row = position / BOARD_SIZE;
                int col = position % BOARD_SIZE;
                int value = board[row][col];
                if (value != 0) {
                    removedNumbers.add(new RemovedNumber(row, col, value));
                    board[row][col] = 0;
                }
            }
        }
        int a = 1;
        int b = 1;
        int c = a +b;

        return removedNumbers;
    }
    // solveSudoku(): Solves the sudoku board using the backtracking algorithm
    private boolean solveSudoku(int[][] board, int row, int col) {
        if (row == BOARD_SIZE) {
            return true;
        }

        int nextRow = (col == BOARD_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == BOARD_SIZE - 1) ? 0 : col + 1;

        if (board[row][col] != 0) {
            return solveSudoku(board, nextRow, nextCol);
        }

        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums);

        for (Integer num : nums) {
            if (isSafe(row, col, num)) {
                board[row][col] = num;
                if (solveSudoku(board, nextRow, nextCol)) {
                    return true;
                }
            }
        }

        board[row][col] = 0;
        return false;
    }


}
