package com.example.reto_3;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TicTacToeGame mGame;

    // Buttons making up the board
    private Button mBoardButtons[];
    // Various text displayed
    private TextView mInfoTextView;

    //Game over variable
    private boolean mGameOver;
    //Who starts game
    private Random mGameStartsH;
    // Results text displayed
    private TextView mHumanWinTextView;
    private TextView mTieWinTextView;
    private TextView mAndroidWinTextView;

    private int mResults[];

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add("New Game");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startNewGame();
        return true;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBoardButtons = new Button[TicTacToeGame.BOARD_SIZE];
        mBoardButtons[0] = (Button) findViewById(R.id.one);
        mBoardButtons[1] = (Button) findViewById(R.id.two);
        mBoardButtons[2] = (Button) findViewById(R.id.three);
        mBoardButtons[3] = (Button) findViewById(R.id.four);
        mBoardButtons[4] = (Button) findViewById(R.id.five);
        mBoardButtons[5] = (Button) findViewById(R.id.six);
        mBoardButtons[6] = (Button) findViewById(R.id.seven);
        mBoardButtons[7] = (Button) findViewById(R.id.eight);
        mBoardButtons[8] = (Button) findViewById(R.id.nine);
        mInfoTextView = (TextView) findViewById(R.id.information);

        mHumanWinTextView = (TextView) findViewById(R.id.humanR);
        mTieWinTextView = (TextView) findViewById(R.id.tieR);
        mAndroidWinTextView = (TextView) findViewById(R.id.androidR);
        mResults = new int[3];
        for(int i=0;i<3;i++){
            mResults[i] = 0;
        }

        mGame = new TicTacToeGame();

        mGameStartsH = new Random();

        startNewGame();
    }

    // Set up the game board.
    private void startNewGame() {
        mGame.clearBoard();
        // Reset all buttons
        for (int i = 0; i < mBoardButtons.length; i++) {
            mBoardButtons[i].setText("");
            mBoardButtons[i].setEnabled(true);
            mBoardButtons[i].setOnClickListener(new ButtonClickListener(i));
        }
        // Human goes first
        mGameOver = false;
        boolean t = mGameStartsH.nextBoolean();
        if(!mGameStartsH.nextBoolean()){
            mInfoTextView.setText(R.string.turn_computer);
            int move = mGame.getComputerMove();
            setMove(TicTacToeGame.COMPUTER_PLAYER, move);
            mInfoTextView.setText(R.string.first_computer);
        }else{
            mInfoTextView.setText(R.string.first_human);
        }
    } // End of startNewGame

    private void setMove(char player, int location) {
        mGame.setMove(player, location);
        mBoardButtons[location].setEnabled(false);
        mBoardButtons[location].setText(String.valueOf(player));
        if (player == TicTacToeGame.HUMAN_PLAYER)
            mBoardButtons[location].setTextColor(Color.rgb(0, 200, 0));
        else
            mBoardButtons[location].setTextColor(Color.rgb(200, 0, 0));
    }

    private class ButtonClickListener implements View.OnClickListener {

        int location;
        public ButtonClickListener(int location) {
            this.location = location;
        }

        public void onClick(View view) {
            if (mBoardButtons[location].isEnabled() && !mGameOver) {
                setMove(TicTacToeGame.HUMAN_PLAYER, location);
                // If no winner yet, let the computer make a move
                int winner = mGame.checkForWinner();
                if (winner == 0) {
                    mInfoTextView.setText(R.string.turn_computer);
                    int move = mGame.getComputerMove();
                    setMove(TicTacToeGame.COMPUTER_PLAYER, move);
                    winner = mGame.checkForWinner();
                }
                if (winner == 0)
                    mInfoTextView.setText(R.string.turn_human);
                else if (winner == 1) {
                    mResults[1] = mResults[1] + 1;
                    mInfoTextView.setText(R.string.result_tie);
                    mGameOver = true;
                }else if (winner == 2){
                    mResults[0] = mResults[0] + 1;
                    mInfoTextView.setText(R.string.result_human_wins);
                    mGameOver = true;
                }else{
                    mResults[2] = mResults[2] + 1;
                    mInfoTextView.setText(R.string.result_computer_wins);
                    mGameOver = true;
                }
            }
            mHumanWinTextView.setText("Human: " + (mResults[0]));
            mTieWinTextView.setText("Tie: " + (mResults[1]));
            mAndroidWinTextView.setText("Android: " + (mResults[2]));
        }
    }
}