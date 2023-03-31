package com.example.actualsudoku;


//Simple data class for storing numbers and their corresponding row and column
//this class is used in the SudokuGenerator class
public class RemovedNumber {
    public int row;
    public int col;
    public int value;

    public RemovedNumber(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
}
