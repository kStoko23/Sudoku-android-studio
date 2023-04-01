package com.example.actualsudoku;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

public class MainActivity extends AppCompatActivity {
    //region Fields
    private GameBoard gameBoard;
     private Button hintButton;
     private Chronometer chronometer;
     private Button generateNewButton;
     private SudokuGenerator generator;
    //endregion
    @Override
    protected void onCreate(Bundle savedInstanceState) { //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNumpad();
        setupHint();
        setupChronometer();
        setupGenerateNewButton();
    }
    //region Setup methods
    // setupHint(): sets up the hint button & it's onClickListener
    private void setupHint() { //sets up the hint button & it's onClickListener
        hintButton = findViewById(R.id.hintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameBoard.showHint();
            }
        });
    }
    // setupNumpad(): sets up the numpad's buttons & their onClickListeners
    private void setupNumpad() { //sets up the numpad's buttons & their onClickListeners

        gameBoard = findViewById(R.id.gameBoard);

        int[] buttonIds = {
                R.id.button1, R.id.button2, R.id.button3,
                R.id.button4, R.id.button5, R.id.button6,
                R.id.button7, R.id.button8, R.id.button9
        };

        View.OnClickListener numpadClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = Integer.parseInt(((Button) view).getText().toString());
                gameBoard.setInputNumber(number);
            }
        };

        for (int id : buttonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(numpadClickListener);
        }
    }
    // setupChronometer(): sets up the chronometer
    private void setupChronometer() { //sets up the chronometer
        chronometer = findViewById(R.id.chronometer);
        chronometer.start();
    }
    // setupGenerateNewButton(): sets up the generate new button & it's onClickListener
    private void setupGenerateNewButton(){
        generateNewButton = findViewById(R.id.generateNewButton);
        generateNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameBoard.resetBoard();
            }
        });
    }
    //endregion
}