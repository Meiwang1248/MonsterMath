package edu.neu.madcourse.monstermath;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private TextView competitorScore;
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

        //connect buttons and monsters with layout
        // installing listener: we can pss the both the answer and monster to
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
        homeButton = findViewById(R.id.btnCompeteHome);

        //connect TextViews and layout
        question = findViewById(R.id.tvQuestion);
        score = findViewById(R.id.tvScoreCount);
        time = findViewById(R.id.tvTimeCount);

    }

    private void initGame(){
        game = new Game(GAME_OPERATION, GAME_LEVEL,true,1,0);

        for(int i=0; i<10; i++){
            game.generateOneStage();
            // update UI 10 times
            score.setText(game.score);
            question.setText(game.curNumber1+" "+game.operation+" "+game.curNumber2+" = ?");
        }
        // 1. need to create a pop up window for game over 2. row 58 still need modify 3. each button need a listener
    }

    /*
    To be invoked when user click on an answer
     */
    private void validateAnswer(int answer) {
        if (answer == game.curAnswer) {
            game.score += 10;
            game.score += game.bonus;
            if (game.curStage < 10) {
                nextStage();
            }

        } else {
            game.options.remove(answer);
            // 怪兽要disappear, 怎么知道要消失哪只呢？估计要把答案和怪兽connect起来
        }
    }

    private void nextStage() {

    }
}