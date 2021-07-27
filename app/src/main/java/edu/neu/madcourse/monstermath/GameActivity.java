package edu.neu.madcourse.monstermath;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity {
    // multiple choice buttons
    private Button option1;
    private Button option2;
    private Button option3;
    private Button option4;
    private Button option5;

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

        //connect button and layout
        option1 = findViewById(R.id.button);
        option2 = findViewById(R.id.button2);
        option3 = findViewById(R.id.button3);
        option4 = findViewById(R.id.button4);
        option5 = findViewById(R.id.button5);
        homeButton = findViewById(R.id.btnCompeteHome);

        //connect TextViews and layout
        question = findViewById(R.id.tvQuestion);
        score = findViewById(R.id.tvScoreCount);
        competitorScore = findViewById(R.id.tvCompetitorScoreCount);
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
}