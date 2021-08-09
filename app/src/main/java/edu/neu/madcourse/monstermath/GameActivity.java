package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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
import java.util.Timer;
import java.util.TimerTask;

import edu.neu.madcourse.monstermath.Model.Player;
import edu.neu.madcourse.monstermath.Model.User;

public class GameActivity extends AppCompatActivity {
    // multiple choice buttons
    private Button option1, option2, option3, option4, option5, homeButton;

    // images
    private ImageView m1, m2, m3, m4, m5, partyPopper;

    // textviews
    private TextView question, score, time;
    private int seconds;

    // game settings
    static String GAME_LEVEL, GAME_OPERATION;
    static boolean GAME_MODE;
    private Game game;
    private boolean personalBestFlag = false;

    // Firebase settings
    FirebaseUser firebaseUser;
    DatabaseReference rootDatabaseRef;
    User user;
    String usernameStr;

    // Match settings
    String matchId;
    int playerNumber;
    Player curPlayer;
    Player opponentPlayer;

    // sound effects
    private SoundEffects sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_game);
        hideSystemUI();

        // set root database reference
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference();
        // get user
        getUser();
        // get game settings
        getGameSettings();
        // connect multiple choices and monsters to UI
        connectUIComponents();
        // install listeners to all multiple choices
        installListeners();

        homeButton = findViewById(R.id.btnCompeteHome);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GameActivity.this, GameSettingActivity.class));
            }
        });

        // connect TextViews and layout
        question = findViewById(R.id.tvQuestion);
        score = findViewById(R.id.tvScoreCount);
        time = findViewById(R.id.tvTimeCount);

        if (GAME_MODE == true) {
            initGame();
        } else {
            onlineGame();
        }

        //sound effects
        sound = new SoundEffects(this);
    }


    private void getGameSettings() {
        GAME_OPERATION = getIntent().getExtras().getString("GAME_OPERATION");
        GAME_LEVEL = getIntent().getExtras().getString("GAME_LEVEL");
        GAME_MODE = getIntent().getExtras().getBoolean("GAME_MODE");
    }

    private void getUser() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootDatabaseRef.child("Users").orderByChild("id")
                .equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        rootDatabaseRef.child("Users").child(usernameStr).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // update real-time number of games played
                updateNumOfGamesPlayed();

                // update user's personal best score if user gets a higher score
                updaterPersonalBestScore();

                // add this round of score to all scores
                addToScores();

                Toast.makeText(GameActivity.this, "Score stored successfully.", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateNumOfGamesPlayed() {
        user.numOfGamesPlayed++;
        rootDatabaseRef.child("Users")
                .child(usernameStr)
                .child("numOfGamesPlayed").setValue(user.numOfGamesPlayed);
    }

    private void addToScores() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("score", game.score);
        hashMap.put("username", usernameStr);
        rootDatabaseRef.child("Scores")
                .child(GAME_LEVEL)
                .push()
                .setValue(hashMap);
    }

    private void updaterPersonalBestScore() {
        switch (GAME_LEVEL) {
            case "easy":
                if (game.score > user.personalBestScoreEasy) {
                    rootDatabaseRef.child("Users")
                            .child(usernameStr)
                            .child("personalBestScoreEasy")
                            .setValue(game.score);
                    personalBestFlag = true;
                }
                break;
            case "medium":
                if (game.score > user.personalBestScoreMedium) {
                    rootDatabaseRef.child("Users")
                            .child(usernameStr)
                            .child("personalBestScoreMedium")
                            .setValue(game.score);
                    personalBestFlag = true;
                }
                break;
            case "hard":
                if (game.score > user.personalBestScoreHard) {
                    rootDatabaseRef.child("Users")
                            .child(usernameStr)
                            .child("personalBestScoreHard")
                            .setValue(game.score);
                    personalBestFlag = true;
                }
                break;

        };
    }

    private void onlineGame() {
        // find the match
        rootDatabaseRef.child("Matches").addListenerForSingleValueEvent(new ValueEventListener() {
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
        // Start the timer
        turnOnTimer();
        nextStage();
        // show current question
        showCurrentQuestion();
        // show current options
        showCurrentOptions();
    }

    private int getBonus() {
        // add bonus based on response time
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

            //sound effect and shows a party popper
            sound.playHappySound();

            //TODO: add firework indicating user gets the right answer
            fadeOutAndHideImage((ImageView) findViewById(R.id.party_popper));


            // We do not reward answer if the correct answer picked lastly
            if (game.curOptions.size() > 1) {
                game.score += 10;
                game.score += getBonus();
            }

            score.setText("Score: " + game.score);

            // add current score to online game database
            if (GAME_MODE == false) {
                curPlayer.setScore(game.score);
                rootDatabaseRef = FirebaseDatabase.getInstance().getReference("Matches");
                rootDatabaseRef.child(matchId).child("player"+playerNumber).setValue(curPlayer);
            }

            if (game.curStage < 10) {
                nextStage();
            } else {
                // End Game
                endGame();
                //sound effect
                sound.playEndSound();
            }

        } else {
            game.curOptions.remove(Integer.valueOf(answer.getText().toString()));
            answer.setVisibility(View.INVISIBLE);
            monster.setVisibility(View.INVISIBLE);
            //sound effect
            sound.playSadSound();
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
        // get all monsters back and renew the time
        seconds = 0;
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
        openDialog(game.getCurScore());
    }

    private void openDialog(int gameScore) {
        Dialog dialog = new Dialog(gameScore, personalBestFlag);
        dialog.show(getSupportFragmentManager(), "result");
    }

    private void showOnlineGameResult() {
        rootDatabaseRef = FirebaseDatabase.getInstance().getReference("Matches");
        rootDatabaseRef.child(matchId).addListenerForSingleValueEvent(new ValueEventListener() {
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

        partyPopper = findViewById(R.id.party_popper);
        partyPopper.setVisibility(View.INVISIBLE);
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


    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void fadeOutAndHideImage(final ImageView img)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.GONE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }

    private void turnOnTimer() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time.setText("TIME: "+ seconds);
                seconds++;
            }
        }, 500, 1000);
    }

}