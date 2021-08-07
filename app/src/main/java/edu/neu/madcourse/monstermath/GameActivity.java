package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;

import edu.neu.madcourse.monstermath.Model.Player;
import edu.neu.madcourse.monstermath.Model.User;

public class GameActivity extends AppCompatActivity {
    // multiple choice buttons
    private Button option1, option2, option3, option4, option5, homeButton;

    // images
    private ImageView m1, m2, m3, m4, m5;

    // textviews
    private TextView question, score, time;

    // game settings
    static String GAME_LEVEL, GAME_OPERATION;
    static boolean GAME_MODE;
    private Game game;

    // Firebase settings
    FirebaseUser currUser;
    DatabaseReference databaseReference;
    User user;
    String usernameStr;

    // Match settings
    String matchId;
    int playerNumber;
    Player curPlayer;
    Player opponentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_game);

        // get user name
        getUsername();
        // get game settings
        getGameSettings();
        // connect multiple choices and monsters to UI
        connectUIComponents();
        // install listeners to all multiple choices
        installListeners();

        homeButton = findViewById(R.id.btnCompeteHome);

        // connect TextViews and layout
        question = findViewById(R.id.tvQuestion);
        score = findViewById(R.id.tvScoreCount);
        time = findViewById(R.id.tvTimeCount);

        if (GAME_MODE == true) {
            initGame();
        } else {
            onlineGame();
        }
    }

    private void getGameSettings() {
        GAME_OPERATION = getIntent().getExtras().getString("GAME_OPERATION");
        GAME_LEVEL = getIntent().getExtras().getString("GAME_LEVEL");
        GAME_MODE = getIntent().getExtras().getBoolean("GAME_MODE");
    }

    private void getUsername() {
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").orderByChild("id")
                .equalTo(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                        usernameStr = user.getUsername();
                    }
                } catch (Exception e) {
                    Toast.makeText(GameActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void storeGameScore() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").child(usernameStr).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User.class);

                    // update real-time number of games played
                    user.numOfGamesPlayed++;
                    databaseReference.child("Users")
                            .child(usernameStr)
                            .child("numOfGamesPlayed").setValue(user.numOfGamesPlayed);

                    // add this round of score to current user's scores
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("score", score);
                    databaseReference.child("Users")
                            .child(usernameStr)
                            .child("scores")
                            .push()
                            .setValue(game.score);

                    // add this round of score to all scores
                    hashMap = new HashMap<>();
                    hashMap.put("level", GAME_LEVEL);
                    hashMap.put("score", game.score);
                    hashMap.put("username", usernameStr);
                    databaseReference.child("Scores")
                            .push()
                            .setValue(hashMap);

                    Toast.makeText(GameActivity.this, "Score stored successfully.", Toast.LENGTH_LONG).show();
                } else {
                    // pop message showing receiver does not exist
                    Toast.makeText(GameActivity.this, "Username does not exist.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void onlineGame() {
        // find the match
        databaseReference = FirebaseDatabase.getInstance().getReference("Matches");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    Player player0 = childSnapshot.child("player0").getValue(Player.class);
                    String player0Name = player0.getUsername();
                    Player player1 = childSnapshot.child("player1").getValue(Player.class);
                    String player1Name = player1.getUsername();

                    // check if the current user exists in the game
                    if (usernameStr.equals(player0Name)) {
                        matchId = childSnapshot.getKey();
                        playerNumber = 0;
                        game = childSnapshot.child("game").getValue(Game.class);
                        curPlayer = player0;
                    } else if (usernameStr.equals(player1Name)) {
                        matchId = childSnapshot.getKey();
                        game = childSnapshot.child("game").getValue(Game.class);
                        playerNumber = 1;
                        curPlayer = player1;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nextStage();
        // show current question
        showCurrentQuestion();
        // show current options
        showCurrentOptions();
    }

    private void initGame(){
        game = new Game(GAME_OPERATION, GAME_LEVEL,GAME_MODE,1,0);

        nextStage();
        // show current question
        showCurrentQuestion();
        // show current options
        showCurrentOptions();
    }

    private int getBonus() {
        // add bonus based on time
        int bonus;
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        long endTime = ts.getTime();
        long duration = (endTime - game.startTime) / 1000;
        if (duration <= 2) {
            bonus = 5;
        } else if (duration <= 5) {
            bonus = 2;
        } else {
            bonus = 0;
        }
        return bonus;
    }

    /*
    To be invoked when user clicks on an answer
     */
    private void validateAnswer(Button answer, ImageView monster) {
        if (Integer.parseInt(answer.getText().toString()) == game.curAnswer) {
            // To do: 加声效 加背景音乐
            // We do not reward answer if the correct answer picked lastly
            if (game.optionsQueue.size() > 1) {
                game.score += 10;
                game.score += getBonus();
            }

            score.setText("Score: " + game.score);
//            Toast toast = Toast.makeText(GameActivity.this, "Correct! You got 10 points along with " + getBonus() + " points of bonus!", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();

            // add current score to online game database
            if (GAME_MODE == false) {
                curPlayer.setScore(game.score);
                databaseReference = FirebaseDatabase.getInstance().getReference("Matches");
                databaseReference.child(matchId).child("player"+playerNumber).setValue(curPlayer);
            }

            if (game.curStage < 10) {
                nextStage();
            } else {
                // End Game
                endGame();
            }

        } else {
            game.curOptions.remove(answer);
            answer.setVisibility(View.INVISIBLE);
            monster.setVisibility(View.INVISIBLE);
//            Toast toast = Toast.makeText(GameActivity.this, "Oops! Try again", Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();
        }
    }

    private void showCurrentQuestion() {
        question.setText(game.curQuestion);
    }

    private void showCurrentOptions() {
        Iterator iterator = game.curOptions.iterator();
        option1.setText(String.valueOf(iterator.next()));
        option2.setText(String.valueOf(iterator.next()));
        option3.setText(String.valueOf(iterator.next()));
        option4.setText(String.valueOf(iterator.next()));
        option5.setText(String.valueOf(iterator.next()));
    }

    private void nextStage() {
        // get all monsters back
        showAllMonsters();
        game.generateOneStage();
        score.setText("Score: " + game.score);
        // show current question
        showCurrentQuestion();
        // show current options
        showCurrentOptions();
        game.curStage++;
    }

    private void hideAllMonsters() {
        option1.setVisibility(View.INVISIBLE);
        option2.setVisibility(View.INVISIBLE);
        option3.setVisibility(View.INVISIBLE);
        option4.setVisibility(View.INVISIBLE);
        option5.setVisibility(View.INVISIBLE);

        m1.setVisibility(View.INVISIBLE);
        m2.setVisibility(View.INVISIBLE);
        m3.setVisibility(View.INVISIBLE);
        m4.setVisibility(View.INVISIBLE);
        m5.setVisibility(View.INVISIBLE);
    }

    private void showAllMonsters() {
        option1.setVisibility(View.VISIBLE);
        option2.setVisibility(View.VISIBLE);
        option3.setVisibility(View.VISIBLE);
        option4.setVisibility(View.VISIBLE);
        option5.setVisibility(View.VISIBLE);

        m1.setVisibility(View.VISIBLE);
        m2.setVisibility(View.VISIBLE);
        m3.setVisibility(View.VISIBLE);
        m4.setVisibility(View.VISIBLE);
        m5.setVisibility(View.VISIBLE);
    }

    private void endGame() {
        // Set most UI components invisible
        hideAllMonsters();

        score.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        question.setVisibility(View.INVISIBLE);

        if (GAME_MODE) {
            // TODO: change to sticker with a back button linked to GameSettingActivity
            showSoloGameResult();
        } else {
            showOnlineGameResult();
        }

        m1.setVisibility(View.VISIBLE);
        m2.setVisibility(View.VISIBLE);
        m3.setVisibility(View.VISIBLE);
        m4.setVisibility(View.VISIBLE);
        m5.setVisibility(View.VISIBLE);
        storeGameScore();
    }

    private void showSoloGameResult() {
        if (game.getCurScore() >= 100) {
            Toast.makeText(GameActivity.this, "Bravo! Your Score is " + game.getCurScore(), Toast.LENGTH_LONG).show();
        } else if (game.getCurScore() >= 90) {
            Toast.makeText(GameActivity.this, "Good job! Your Score is " + game.getCurScore(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(GameActivity.this, "Nice! Your Score is " + game.getCurScore(), Toast.LENGTH_LONG).show();
        }
    }

    private void showOnlineGameResult() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Matches");
        databaseReference.child(matchId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                    opponentPlayer = childSnapshot.child("player" + (1 - playerNumber)).getValue(Player.class);

                    // check if the current player has higher score
                    if (!opponentPlayer.isGameOver()) {
                        Toast.makeText(GameActivity.this, "Waiting for your opponent to finish game!", Toast.LENGTH_LONG).show();
                    }
                    if (game.score > opponentPlayer.getScore()) {
                        Toast.makeText(GameActivity.this, "Your win!", Toast.LENGTH_LONG).show();
                    } else if (game.score == opponentPlayer.getScore()) {
                        Toast.makeText(GameActivity.this, "Tie!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GameActivity.this, "You lose!", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void connectUIComponents() {
        option1 = findViewById(R.id.btnAnswer1);
        m1 = findViewById(R.id.ivMonster1);

        option2 = findViewById(R.id.btnAnswer2);
        m2 = findViewById(R.id.ivMonster2);

        option3 = findViewById(R.id.btnAnswer3);
        m3 = findViewById(R.id.ivMonster3);

        option4 = findViewById(R.id.btnAnswer4);
        m4 = findViewById(R.id.ivMonster4);

        option5 = findViewById(R.id.btnAnswer5);
        m5 = findViewById(R.id.ivMonster5);
    }

    private void installListeners() {

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer(option1, m1);
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer(option2, m2);
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer(option3, m3);
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer(option4, m4);
            }
        });

        option5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAnswer(option5, m5);
            }
        });
    }
}