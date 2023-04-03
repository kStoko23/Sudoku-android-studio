package com.example.actualsudoku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;
import androidx.annotation.Nullable;
import java.util.List;
//This class serves as the view for the Sudoku game board and manages all the drawing and touch events.
// It interacts with the SudokuGenerator class to generate and display a Sudoku puzzle on a 9x9 grid,
// divided into 3x3 subgrids.
// The game board is represented using a 2D array.
// By overriding the onTouchEvent method,
// this class provides visual feedback to the user by highlighting the selected row, column, and corresponding 3x3 subgrid.
// Additionally, it handles user input, game progress, and implements game features such as hints and checking for game completion.
//
// Resources:
// https://www.youtube.com/watch?v=lYjSl_ou05Q -- for the base of the drawing,
// very helpful video on how to draw a sudoku board
// https://www.youtube.com/watch?v=o6P05m0E9z4&list=PLJSII25WrAz72NhnBitybKMMX0_f1UEym -- this one as well

public class GameBoard extends View {
    //region Constants
    private final int boardColor;
    //endregion
    //region Fields
    private int cellSize;
    private int selectedRow = -1;
    private int selectedCol = -1;
    private List<RemovedNumber> removedNumbers;
    private RemovedNumber hint;
    private boolean showHint = false;
    private int hintsLeft = 3;
    private int inputNumber = 0;
    private boolean hintRequested = false;
    private int[][] sudokuBoard;
    private OnGameOverListener gameOverListener;
    private SudokuGenerator generator;
    private boolean[][] editableCells;

    //endregion
    //region Paints
    private final Paint cellHighlightPaint = new Paint();
    private final Paint highlightPaint = new Paint();
    private final Paint hintPaint = new Paint();
    private final Paint squareHighlightPaint = new Paint();
    private final Paint boardPaint = new Paint();
    private final Paint textPaint = new Paint();
    private final Paint inputTextPaint = new Paint();
    //endregion
    private interface OnGameOverListener {
        void onGameOver();
    }
    public GameBoard(Context context, @Nullable AttributeSet attrs) {
        //constructor
        //gets the attributes from the xml file

        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.GameBoard,
                0, 0);
        try {
            boardColor = a.getInteger(R.styleable.GameBoard_boardColor, 0);
        }
        finally {
            a.recycle();
        }
        //initialization of the paints
        //region PaintsInitiation

        highlightPaint.setColor(Color.parseColor("#88AAB1FF"));
        highlightPaint.setStyle(Paint.Style.FILL);

        cellHighlightPaint.setColor(Color.parseColor("#886200EE"));
        cellHighlightPaint.setStyle(Paint.Style.FILL);

        squareHighlightPaint.setColor(Color.parseColor("#88D1D7FF"));
        squareHighlightPaint.setStyle(Paint.Style.FILL);

        hintPaint.setColor(Color.YELLOW);
        hintPaint.setStyle(Paint.Style.FILL);
        //endregion
        //generating the sudoku board & removing numbers (more==harder)
        generator = new SudokuGenerator();
        sudokuBoard = generator.generate();
        removedNumbers = generator.removeNumbers(3);

        //initialize editableCells
        editableCells = new boolean[9][9];
        for(RemovedNumber removedNumber : removedNumbers) {
            editableCells[removedNumber.row][removedNumber.col] = true;
        }
    }
    //onMeasure() Measures and sets the dimensions of the game board, ensuring it fits the screen properly
    @Override
    protected void onMeasure(int widthMeasure, int heightMeasure) {
        super.onMeasure(widthMeasure, heightMeasure);

        int dimension = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        cellSize = dimension / 9;

        setMeasuredDimension(dimension, dimension);

    }
    //onDraw(): Draws the game board, including highlighted cells, hints, and input numbers
    @Override
    protected void onDraw(Canvas canvas){
        //honestly not sure why they can't be in the constructor
        //but it doesn't work if they are

        //region PaintsInitiation
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setStrokeWidth(16);
        boardPaint.setColor(boardColor);
        boardPaint.setAntiAlias(true);

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(cellSize * 0.7f);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setSubpixelText(true);
        textPaint.setLinearText(true);

        inputTextPaint.setStyle(Paint.Style.FILL);
        inputTextPaint.setTextSize(cellSize * 0.7f);
        inputTextPaint.setColor(Color.parseColor("#FFA500"));
        inputTextPaint.setTextAlign(Paint.Align.CENTER);
        inputTextPaint.setAntiAlias(true);
        inputTextPaint.setSubpixelText(true);
        inputTextPaint.setLinearText(true);
        //endregion

        canvas.drawRect(0,0,getWidth(),getHeight(),boardPaint);

        if (selectedRow != -1 && selectedCol != -1) {
            canvas.drawRect(0, selectedRow * cellSize, getWidth(), (selectedRow + 1) * cellSize, highlightPaint);
            canvas.drawRect(selectedCol * cellSize, 0, (selectedCol + 1) * cellSize, getHeight(), highlightPaint);

            int areaStartRow = selectedRow / 3 * 3;
            int areaStartCol = selectedCol / 3 * 3;
            canvas.drawRect(areaStartCol * cellSize, areaStartRow * cellSize, (areaStartCol + 3) * cellSize, (areaStartRow + 3) * cellSize, squareHighlightPaint);

            canvas.drawRect(selectedCol * cellSize, selectedRow * cellSize, (selectedCol + 1) * cellSize, (selectedRow + 1) * cellSize, cellHighlightPaint);
        }

        drawBoard(canvas);
        drawHint(canvas);
        drawNumbers(canvas,textPaint);
        drawInputNumbers(canvas,inputTextPaint);
    }
    //region Drawing board methods
    //drawBoard(): Draws the board lines, including thin and thick lines, creating a 9x9 grid with 3x3 squares
    private void drawBoard(Canvas canvas){
        for (int i = 0; i < 9; i++){
            if (i % 3 == 0){
                drawThickLines(canvas);
            }
            else{
                drawThinLines(canvas);
            }
            canvas.drawLine(0, i * cellSize, getWidth(), i * cellSize, boardPaint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, getHeight(), boardPaint);
        }
    }
    //drawThickLines(): Draws the thick lines around the board and every third column & row
    private void drawThickLines(Canvas canvas){
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setStrokeWidth(8);
        boardPaint.setColor(boardColor);
    }
    //drawThinLines(): Draws the thin lines in the 3x3 subgrids
    private void drawThinLines(Canvas canvas){
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setStrokeWidth(4);
        boardPaint.setColor(boardColor);
    }
    //drawNumbers(): Draws the initial numbers on the game board
    private void drawNumbers(Canvas canvas, Paint textPaint) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int number = sudokuBoard[i][j];
                if (number != 0) {
                    float x = j * cellSize + cellSize / 2;
                    float y = i * cellSize + cellSize / 2 + (textPaint.getTextSize() / 2) - textPaint.descent();
                    canvas.drawText(String.valueOf(number), x, y, textPaint);
                }
            }
        }
    }
    //drawHint(): Draws a hint on the board if the showHint flag is true and hints are available
    private void drawHint(Canvas canvas) {
        if(hintsLeft==0){
            showHint=false;
            if (hintRequested) {
                Toast.makeText(getContext(), "You have no more hints", Toast.LENGTH_SHORT).show();
                hintRequested = false;
            }
        }
        if (showHint&&hintsLeft>0) {
            int row = hint.row;
            int col = hint.col;
            int x = col * cellSize;
            int y = row * cellSize;

            canvas.drawRect(x, y, x + cellSize, y + cellSize, hintPaint);

            boardPaint.setColor(Color.BLACK);
            boardPaint.setTextSize(cellSize / 2);
            boardPaint.setTextAlign(Paint.Align.CENTER);
            boardPaint.setAntiAlias(true);
            boardPaint.setSubpixelText(true);
            boardPaint.setLinearText(true);

            float textX = x + cellSize / 2;
            float textY = y + (cellSize / 2) - ((boardPaint.descent() + boardPaint.ascent()) / 2);

            canvas.drawText(String.valueOf(hint.value), textX, textY, boardPaint);
            hintsLeft--;

            //just checking if it works, TODO: delete later
            System.out.println(hintsLeft);
        }
    }
    //drawInputNumbers(): Draws the user-input numbers on the game board
    private void drawInputNumbers(Canvas canvas, Paint inputTextPaint) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (editableCells[i][j]) {
                    int number = sudokuBoard[i][j];
                    if (number != 0) {
                        float x = j * cellSize + cellSize / 2;
                        float y = i * cellSize + cellSize / 2 + (inputTextPaint.getTextSize() / 2) - inputTextPaint.descent();
                        canvas.drawText(String.valueOf(number), x, y, inputTextPaint);
                    }
                }
            }
        }
    }
    //endregion
    //onTouchEvent(): Handles touch events, selecting a cell or filling it with the input number
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) (event.getX() / cellSize);
            int y = (int) (event.getY() / cellSize);

            if (x >= 0 && x < 9 && y >= 0 && y < 9) {
                selectedRow = y;
                selectedCol = x;
                if (inputNumber != 0) {
                    fillCell(inputNumber);
                    inputNumber = 0;
                } else if (showHint && hint.row == selectedRow && hint.col == selectedCol) {
                    showHint = false;
                }
                invalidate();
            }
        }
        return true;
    }
    //The showHint() method provides a hint by revealing a hidden cell value on the game board.
    public void showHint() {
        if (!removedNumbers.isEmpty()) {
            hint = removedNumbers.remove(0);
            showHint = true;
            hintRequested = true;
            invalidate();
        }
    }
    //setInputNumber(): Sets the input number to be filled in the selected cell
    public void setInputNumber(int number) {
        inputNumber = number;
        if (selectedRow != -1 && selectedCol != -1) {
            fillCell(inputNumber);
            inputNumber = 0;
        }
    }
    //fillCell(): Fills the selected cell with the input number and checks for game over
    private void fillCell(int number) {
        if (selectedRow != -1 && selectedCol != -1 && editableCells[selectedRow][selectedCol]) {
            sudokuBoard[selectedRow][selectedCol] = number;
            invalidate();

            if (isGameOver() && gameOverListener != null) {
                gameOverListener.onGameOver();
            }
        }
    }
    //region isGameOver() methods
    //isGameOver(): Checks if the game is over, considering both board fullness and validity
    private boolean isGameOver() {
        if (isBoardFull()) {
            if (isBoardValid()) {
                System.out.println("Board is valid");
                startGameOverActivity();
                Chronometer chronometer = findViewById(R.id.chronometer);
                return true;
            } else {
                System.out.println("Board is not valid");
                return false;
            }
        }
        return false;
    }
    //isBoardFull(): Checks if the game board is completely filled with numbers
    private boolean isBoardFull() {
        for (int row = 0; row < generator.getBoardSize(); row++) {
            for (int col = 0; col < generator.getBoardSize(); col++) {
                if (getCell(row, col) == 0) {
                    return false;
                }
            }
        }
        return true;
    }
    //isBoardValid(): Checks if the filled game board is valid according to Sudoku rules
    private boolean isBoardValid() {
        for (int row = 0; row < generator.getBoardSize(); row++) {
            for (int col = 0; col < generator.getBoardSize(); col++) {
                int currentValue = getCell(row, col);
                setCell(row, col, 0);
                if (!generator.isSafe(row, col, currentValue)) {
                    setCell(row, col, currentValue);
                    return false;
                }
                setCell(row, col, currentValue);
            }
        }
        return true;
    }
    //getCell(): Returns the value of a cell at the given row and column
    private int getCell(int row, int col) {
        return sudokuBoard[row][col];
    }
    //setCell(): Sets the value of a cell at the given row and column
    private void setCell(int row, int col, int value) {
        sudokuBoard[row][col] = value;
    }
    //endregion
    //startGameOverActivity(): Starts the Game Over activity when the game is over
    private void startGameOverActivity() {
        Context context = getContext();
        if (context instanceof Activity) {
            Intent intent = new Intent(context, GameOverActivity.class);
            context.startActivity(intent);
        }
    }
    //region New Game methods
    //resetBoard(): Resets the game board by generating a new Sudoku puzzle
    public void resetBoard() {
        sudokuBoard = generator.generate();
        removedNumbers = generator.removeNumbers(3);
        updateEditableCells();
        hintsLeft = 3;
        hintRequested = false;
        showHint = false;
        invalidate();
    }
    //updateEditableCells(): Updates the editable cells based on the new Sudoku puzzle
    public void updateEditableCells() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                editableCells[row][col] = false;
            }
        }
        for (RemovedNumber removedNumber : removedNumbers) {
            editableCells[removedNumber.row][removedNumber.col] = true;
        }
    }
    //endregion
}
