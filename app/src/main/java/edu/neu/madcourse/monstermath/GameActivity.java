package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.sql.Timestamp;
import java.util.HashMap;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_game);

        // connect multiple choices and monsters to UI
        connectUIComponents();
        // Install listeners to all multiple choices
        installListeners();

        homeButton = findViewById(R.id.btnCompeteHome);

        //connect TextViews and layout
        question = findViewById(R.id.tvQuestion);
        score = findViewById(R.id.tvScoreCount);
        time = findViewById(R.id.tvTimeCount);

        // if the user chooses solo mode


        // if the user chooses online mode

        // initialize game
        initGame();

        // store game scores to Firebase
        storeGameScore();
    }

    private void storeGameScore() {
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").orderByChild("id")
                .equalTo(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                        String usernameStr = user.getUsername();

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
                                .setValue(hashMap);

                        // add this round of score to all scores
                        hashMap.put("level", GAME_LEVEL);
                        hashMap.put("score", score);
                        hashMap.put("username", usernameStr);
                        databaseReference.child("Scores")
                                .push()
                                .setValue(hashMap);
                    }
                } catch(Exception e) {
                    Toast.makeText(GameActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initGame(){
        game = new Game(GAME_OPERATION, GAME_LEVEL,true,1,0);

        game.generateOneStage();
        score.setText(game.score);
        question.setText(game.curNumber1+" "+game.operation+" "+game.curNumber2+" = ?");

    }

    /*
    To be invoked when user clicks on an answer
     */
    private void validateAnswer(Button answer, ImageView monster) {
        if (Integer.parseInt(answer.getText().toString()) == game.curAnswer) {
            // To do: 加声效 加背景音乐

            game.score += 10;
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
            game.score += bonus;

            score.setText(game.score);
            Toast toast = Toast.makeText(GameActivity.this, "Correct! You got 10 points along with " + bonus + " points of bonus!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            if (game.curStage < 10) {
                nextStage();
            } else {
                // End Game
                endGame();
            }

        } else {
            game.options.remove(answer);
            answer.setVisibility(View.INVISIBLE);
            monster.setVisibility(View.INVISIBLE);
            Toast toast = Toast.makeText(GameActivity.this, "Oops! Try again", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    private void nextStage() {
        // Start the timer here
        game.generateOneStage();
        score.setText(game.score);
        question.setText(game.curNumber1 +game.operation + game.curNumber2 +" = ?");
    }

    private void endGame() {
        // Set most UI components invisible
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

        score.setVisibility(View.INVISIBLE);
        time.setVisibility(View.INVISIBLE);
        question.setVisibility(View.INVISIBLE);

        if (game.getCurScore() >= 100) {
            Toast.makeText(GameActivity.this, "Bravo! Your Score is " + game.getCurScore(), Toast.LENGTH_LONG).show();
        } else if (game.getCurScore() >= 90) {
            Toast.makeText(GameActivity.this, "Good job! Your Score is " + game.getCurScore(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(GameActivity.this, "Nice! Your Score is " + game.getCurScore(), Toast.LENGTH_LONG).show();
        }

        m1.setVisibility(View.VISIBLE);
        m2.setVisibility(View.VISIBLE);
        m3.setVisibility(View.VISIBLE);
        m4.setVisibility(View.VISIBLE);
        m5.setVisibility(View.VISIBLE);
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
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, GameSettingActivity.class);
                startActivity(intent);
            }
        });

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