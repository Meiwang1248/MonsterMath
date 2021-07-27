package edu.neu.madcourse.monstermath;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    // game settings
    static String GAME_LEVEL;
    static String GAME_OPERATION;
    static String GAME_MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);
    }
}