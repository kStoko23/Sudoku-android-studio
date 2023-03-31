package com.example.actualsudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

//Class for generating a sudoku board, using backtracking algorithm
//which was made for solving a sudoku board, but i decided to use it for generating a board
//resources:
//https://www.geeksforgeeks.org/sudoku-backtracking-7/
//https://www.youtube.com/watch?v=G_UYXzGuqvM
//also has a method for removing numbers from the board

public class SudokuGenerator {
    private static final int BOARD_SIZE = 9;
    private int[][] board;
    public int getBoardSize() {
        return BOARD_SIZE;
    }

    public int[][] generate() { //generates a sudoku board
        board = new int[BOARD_SIZE][BOARD_SIZE];
        if (fillBoard(0, 0)) {
            return board;
        }
        return null;
    }
    private boolean fillBoard(int row, int col) {
        return solveSudoku(board, row, col);
    }

    protected boolean isSafe(int row, int col, int num) { //checks if the number is safe to put in the cell
        return isRowSafe(row, num) && isColSafe(col, num) && isBoxSafe(row - row % 3, col - col % 3, num);
    }
    private boolean isRowSafe(int row, int num) { //checks if the number is already in the row
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }
    private boolean isColSafe(int col, int num) { //checks if the number is already in the column
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][col] == num) {
                return false;
            }
        }
        return true;
    }
    private boolean isBoxSafe(int rowStart, int colStart, int num) { //checks if the number is already in the 3x3 box
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board[rowStart + row][colStart + col] == num) {
                    return false;
                }
            }
        }
        return true;
    }
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
    public int[][] getSolution() {//gets the solution of the board
        int[][] solution = copyBoard(board);
        if (solveSudoku(solution, 0, 0)) {
            return solution;
        }
        return null;
    }
    private int[][] copyBoard(int[][] originalBoard) {
        int[][] newBoard = new int[BOARD_SIZE][BOARD_SIZE];
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                newBoard[row][col] = originalBoard[row][col];
            }
        }
        return newBoard;
    }
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
