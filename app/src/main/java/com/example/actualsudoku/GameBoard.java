package com.example.actualsudoku;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;
//This class is the view for the game board
//It handles all the drawing and touch events
//Sudoku is generated in the SudokuGenerator class
//The game board is a 9x9 grid, with 3x3 squares
//The game board is drawn using a 2d array
//Overriding the onTouchEvent method makes the rows and columns
//highlighted when the user touches the screen, as well as a 3x3 subgrid
//resources:
//https://www.youtube.com/watch?v=lYjSl_ou05Q -- for the base of the drawing,
// very helpful video on how to draw a sudoku board
//https://www.youtube.com/watch?v=o6P05m0E9z4&list=PLJSII25WrAz72NhnBitybKMMX0_f1UEym -- this one as well

public class GameBoard extends View {
    //region Constants
    private final int boardColor;

    //endregion
    //region Variables
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
    //endregion
    //region Paints
    private final Paint cellHighlightPaint = new Paint();
    private final Paint highlightPaint = new Paint();
    private final Paint hintPaint = new Paint();
    private final Paint squareHighlightPaint = new Paint();
    private final Paint boardPaint = new Paint();
    private final Paint textPaint = new Paint();
    //endregion

    public interface OnGameOverListener {
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

        //for checking if it works, appears it does, TODO: remove later
        /*for (RemovedNumber removedNumber : removedNumbers) {
            System.out.println(removedNumber.row + " " + removedNumber.col + " " + removedNumber.value);
        }*/
    }
    @Override
    protected void onMeasure(int widthMeasure, int heightMeasure) { //gets the size of the screen so the board is drawn properly
        super.onMeasure(widthMeasure, heightMeasure);

        int dimension = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        cellSize = dimension / 9;

        setMeasuredDimension(dimension, dimension);

    }
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

    }
    //region Drawing board methods
    private void drawBoard(Canvas canvas){ //draws the board using drawThickLines() and drawThinLines()
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
    private void drawThickLines(Canvas canvas){ //draws the thick lines around the board and every third column&row
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setStrokeWidth(8);
        boardPaint.setColor(boardColor);
    }
    private void drawThinLines(Canvas canvas){ //draws the thin lines in 3x3 subgrids
        boardPaint.setStyle(Paint.Style.STROKE);
        boardPaint.setStrokeWidth(4);
        boardPaint.setColor(boardColor);
    }
    private void drawNumbers(Canvas canvas, Paint textPaint) { //draws the initial numbers on the board
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
    private void drawHint(Canvas canvas) { //draws a hint on the board if showHint() is true and hintsLeft > 0
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

            float textX = x + cellSize / 2;
            float textY = y + (cellSize / 2) - ((boardPaint.descent() + boardPaint.ascent()) / 2);

            canvas.drawText(String.valueOf(hint.value), textX, textY, boardPaint);
            hintsLeft--;

            //just checking if it works, TODO: delete later
            System.out.println(hintsLeft);
        }
    }
    //endregion
    @Override
    public boolean onTouchEvent(MotionEvent event) { //handles the touch events
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
     public void showHint() { //gets the number for a hint
        if (!removedNumbers.isEmpty()) {
            hint = removedNumbers.remove(0);
            showHint = true;
            hintRequested = true;
            invalidate();
        }
    }
    public void setInputNumber(int number) { //sets the number to be filled in the cell
        inputNumber = number;
        if (selectedRow != -1 && selectedCol != -1) {
            fillCell(inputNumber);
            inputNumber = 0;
        }
    }
    private void fillCell(int number) {
        if (selectedRow != -1 && selectedCol != -1 && sudokuBoard[selectedRow][selectedCol] == 0) {
            sudokuBoard[selectedRow][selectedCol] = number;
            invalidate();

            if (isGameOver() && gameOverListener != null) {
                gameOverListener.onGameOver();
            }
        }
    }
    public void setOnGameOverListener(OnGameOverListener listener) {
        this.gameOverListener = listener;
    }
    //region isGameOver() methods
    private boolean isGameOver() {
        if (isBoardFull()) {
            if (isBoardValid()) {
                System.out.println("Board is valid");
                return true;
            } else {
                System.out.println("Board is not valid");
                return false;
            }
        }
        return false;
    }
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
    private int getCell(int row, int col) {
        return sudokuBoard[row][col];
    }
    private void setCell(int row, int col, int value) {
        sudokuBoard[row][col] = value;
    }
    //endregion
}
