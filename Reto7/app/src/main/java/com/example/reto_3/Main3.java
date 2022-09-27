/*
package com.example.reto_3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class MainActivity3 extends AppCompatActivity  {

    private TicTacToeGame mGame;

    // Various text displayed
    private TextView mInfoTextView;

    //Game over variable
    private boolean mGameOver;
    //Who starts game
    private boolean mHTurn;
    // Results text displayed
    private TextView mHumanWinTextView;
    private TextView mTieWinTextView;
    private TextView mAndroidWinTextView;

    private BoardView mBoardView;

    private int mResults;

    static final int DIALOG_DIFFICULTY_ID = 0;
    static final int DIALOG_RESET_ID = 1;
    static final int DIALOG_ABOUT_ID = 2;

    private SharedPreferences mPrefs;

    MediaPlayer mHumanMediaPlayer;
    MediaPlayer mComputerMediaPlayer;

    String playerName = "";
    String roomName = "";
    String role = "";

    FirebaseDatabase database;
    DatabaseReference boardRef;
    DatabaseReference winPRef;
    DatabaseReference turnRef;
    DatabaseReference msgRef;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", mGame.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mResults", mResults);
        outState.putCharSequence("info", mInfoTextView.getText());
        outState.putBoolean("mHTurn", mHTurn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHumanMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.xsound2);
        mComputerMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.osound);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHumanMediaPlayer.release();
        mComputerMediaPlayer.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                startNewGame();
                boardRef.setValue("         ");
                winPRef.setValue(mGameOver);
                if(mHTurn){
                    //mInfoTextView.setText(R.string.first_computer);
                    msgRef.setValue("Player 2 first");
                }else{
                    //mInfoTextView.setText(R.string.first_human);
                    msgRef.setValue("Player 1 first");
                }
                return true;
            case R.id.ai_difficulty:
                showDialog(DIALOG_DIFFICULTY_ID);
                return true;
            case R.id.reset:
                showDialog(DIALOG_RESET_ID);
                return true;
            case R.id.about:
                showDialog(DIALOG_ABOUT_ID);
                return true;
        }
        return false;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch(id) {
            case DIALOG_DIFFICULTY_ID:
                builder.setTitle(R.string.difficulty_choose);
                final CharSequence[] levels = {
                        getResources().getString(R.string.difficulty_easy),
                        getResources().getString(R.string.difficulty_harder),
                        getResources().getString(R.string.difficulty_expert)};

                // selected is the radio button that should be selected.
                int selected = 2;
                builder.setSingleChoiceItems(levels, selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                dialog.dismiss(); // Close dialog
                                // Display the selected difficulty level
                                if(item == 0)
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy);
                                else if(item == 1)
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder);
                                else
                                    mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert);
                                mGame.getDifficultyLevel();
                                Toast.makeText(getApplicationContext(), levels[item],
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog = builder.create();
                break;
            case DIALOG_RESET_ID:
                // Create the quit confirmation dialog
                builder.setMessage(R.string.reset_question)
                        .setCancelable(false)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mResults = 0;
                                displayScores();
                            }
                        })
                        .setNegativeButton(R.string.no, null);
                dialog = builder.create();
                break;
            case DIALOG_ABOUT_ID:
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.about_dialog, null);
                builder.setView(layout);
                builder.setPositiveButton("OK", null);
                dialog = builder.create();
        }
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        mInfoTextView = (TextView) findViewById(R.id.information);
        mHumanWinTextView = (TextView) findViewById(R.id.humanR);
        mTieWinTextView = (TextView) findViewById(R.id.tieR);
        mAndroidWinTextView = (TextView) findViewById(R.id.androidR);
        mResults = 0;

        mGame = new TicTacToeGame();
        mBoardView = (BoardView) findViewById(R.id.board);
        mBoardView.setGame(mGame);
        mBoardView.setOnTouchListener(mTouchListener);

        Random randomGenerator = new Random();
        mHTurn = randomGenerator.nextBoolean();

        mPrefs = getSharedPreferences("ttt_prefs", MODE_PRIVATE);

        // Restore the scores
        mResults = mPrefs.getInt("mResults", 0);

        if (savedInstanceState == null) {
            startNewGame();
        }else {
            // Restore the game's state
            mGame.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mInfoTextView.setText(savedInstanceState.getCharSequence("info"));
            mResults = savedInstanceState.getInt("mResults");
            mHTurn = savedInstanceState.getBoolean("mHTurn");
            //mGoFirst = savedInstanceState.getChar("mGoFirst");
        }
        displayScores();

        // TODO: turnos

        database = FirebaseDatabase.getInstance();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        playerName = preferences.getString("playerName", "");
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            roomName = extras.getString("roomName");
            if(roomName.equals(playerName)){
                role = "host";
            }else{
                role = "guest";
            }
        }

        */
/*mBoardView.setOnClickListener(view -> {

            String boardMessage = role + ":" + getSequence(mGame.getBoardState());
            boardRef.setValue(boardMessage);
        });*//*


        boardRef = database.getReference("rooms/" + roomName + "/message");

        winPRef = database.getReference("rooms/" + roomName + "/winC");
        winPRef.setValue(mGameOver);

        turnRef = database.getReference("rooms/"  + roomName + "/turn");
        msgRef = database.getReference("rooms/"  + roomName + "/text");
        if(role.equals("host")){
            turnRef.setValue(mHTurn);
            boardRef.setValue(getSequence(mGame.getBoardState()));
            if(mHTurn){
                //mInfoTextView.setText(R.string.first_computer);
                msgRef.setValue("Player 2 first");
            }else{
                //mInfoTextView.setText(R.string.first_human);
                msgRef.setValue("Player 1 first");
            }
        }

        makeMoveEventListener();
        changeTurnEventListener();
        endGameEventListener();
        updateMessageEventListener();

        //Toast.makeText(getApplicationContext(), String.valueOf(mHTurn), Toast.LENGTH_SHORT).show();
    }

    private void updateMessageEventListener(){
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mInfoTextView.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void endGameEventListener(){
        winPRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mGameOver = snapshot.getValue(boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void makeMoveEventListener(){
        boardRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Message received
                char[] board = new char[9];
                String a = snapshot.getValue(String.class);
                for(int i = 0; i<9;i++){
                    board[i] = a.charAt(i);
                }
                mGame.setBoardState(board);
                mBoardView.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeTurnEventListener(){
        turnRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean turn = snapshot.getValue(Boolean.class);
                if(!turn && role.equals("host")){
                    mBoardView.setEnabled(true);
                }else if(turn && role.equals("guest")){
                    mBoardView.setEnabled(true);
                }else{
                    mBoardView.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getSequence(char[] seq){
        String text = "";
        for(char let:seq){
            text += let;
        }
        return text;
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mResults", mResults);
        ed.commit();
    }

    private void displayScores() {
        mHumanWinTextView.setText("Human: " + (mResults/100));
        mTieWinTextView.setText("Tie: " + ((mResults%100)/10));
        mAndroidWinTextView.setText("Android: " + ((mResults%100)%10));
    }

    private void startNewGame() {
        mGame.clearBoard();
        mBoardView.invalidate(); // Redraw the board
        // Human goes first
        mGameOver = false;
    } // End of startNewGame

    private boolean setMove(char player, int location) {
        if (mGame.setMove(player, location)) {
            mBoardView.invalidate(); // Redraw the board
            return true;
        }
        return false;
    }

    // Listen for touches on the board
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            // Determine which cell was touched
            int col = (int) event.getX() / mBoardView.getBoardCellWidth();
            int row = (int) event.getY() / mBoardView.getBoardCellHeight();
            int pos = row * 3 + col;
            if(!mGameOver){
                if(role.equals("host")){
                    if (setMove(TicTacToeGame.HUMAN_PLAYER, pos)){
                        setMove(TicTacToeGame.HUMAN_PLAYER, pos);
                        mHumanMediaPlayer.start();
                        int winner = mGame.checkForWinner();
                        if (winner == 0) {
                            //mInfoTextView.setText(R.string.turn_computer);
                            msgRef.setValue("Player 2 turn");
                            mHTurn = true;
                        }else if (winner == 1) {
                            mResults = mResults + 10;
                            //mInfoTextView.setText(R.string.result_tie);
                            msgRef.setValue("It´s a tie");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }else if (winner == 2){
                            mResults = mResults + 1;
                            //mInfoTextView.setText(R.string.result_human_wins);
                            msgRef.setValue("Player 1 won");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }else{
                            mResults = mResults + 100;
                            //mInfoTextView.setText(R.string.result_computer_wins);
                            msgRef.setValue("Player 2 won");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }
                    }
                }else{
                    if (setMove(TicTacToeGame.COMPUTER_PLAYER, pos)) {
                        setMove(TicTacToeGame.COMPUTER_PLAYER, pos);
                        mComputerMediaPlayer.start();

                        int winner = mGame.checkForWinner();
                        if (winner == 0) {
                            //mInfoTextView.setText(R.string.turn_human);
                            msgRef.setValue("Player 1 turn");
                            mHTurn = false;
                        }else if (winner == 1) {
                            mResults = mResults + 10;
                            //mInfoTextView.setText(R.string.result_tie);
                            msgRef.setValue("It´s a tie");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }else if (winner == 2){
                            mResults = mResults + 1;
                            //mInfoTextView.setText(R.string.result_human_wins);
                            msgRef.setValue("Player 1 won");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }else{
                            mResults = mResults + 100;
                            //mInfoTextView.setText(R.string.result_computer_wins);
                            msgRef.setValue("Player 2 won");
                            mGameOver = true;
                            mHTurn = !mHTurn;
                        }
                    }
                }
                displayScores();
                turnRef.setValue(mHTurn);
                boardRef.setValue(getSequence(mGame.getBoardState()));
                winPRef.setValue(mGameOver);
            }
            // So we aren't notified of continued events when finger is moved
            return false;
        }
    };
}
*/
