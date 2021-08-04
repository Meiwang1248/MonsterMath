package edu.neu.madcourse.monstermath;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends AppCompatActivity {
    // multiple choice buttons
    private Button option1;
    private Button option2;
    private Button option3;
    private Button option4;
    private Button option5;

    // images
    private ImageView m1;
    private ImageView m2;
    private ImageView m3;
    private ImageView m4;
    private ImageView m5;


    private Button homeButton;

    // textviews
    private TextView question;
    private TextView score;
    private TextView time;

    // game settings
    static String GAME_LEVEL;
    static String GAME_OPERATION;
    static boolean GAME_MODE;

    private Game game;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        // connect multiple choices and monsters to UI
        connectUIComponents();
        // Install listeners to all multiple choices
        installListeners();

        homeButton = findViewById(R.id.btnCompeteHome);

        //connect TextViews and layout
        question = findViewById(R.id.tvQuestion);
        score = findViewById(R.id.tvScoreCount);
        time = findViewById(R.id.tvTimeCount);

        initGame();

    }





    private void initGame(){
        game = new Game(GAME_OPERATION, GAME_LEVEL,true,1,0);

        game.generateOneStage();
        score.setText(game.score);
        question.setText(game.curNumber1+" "+game.operation+" "+game.curNumber2+" = ?");

        // 1. need to create a pop up window for game over 2. row 58 still need modify 3. each button need a listener
    }

    /*
    To be invoked when user click on an answer
     */
    private void validateAnswer(Button answer, ImageView monster) {
        if (Integer.parseInt(answer.getText().toString()) == game.curAnswer) {
            // To do: 加声效 加背景音乐
            game.score += 10;
            game.score += game.bonus;
            score.setText(game.score);
            Toast toast = Toast.makeText(GameActivity.this, "Correct!", Toast.LENGTH_SHORT);
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