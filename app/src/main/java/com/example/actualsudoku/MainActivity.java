package com.example.actualsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
     private GameBoard gameBoard;
     private Button hintButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupNumpad();
        setupHint();
    }
    private void setupHint() { //sets up the hint button & it's onClickListener
        hintButton = findViewById(R.id.hintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gameBoard.showHint();
            }
        });
    }
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

}