package edu.neu.madcourse.monstermath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class GameSettingActivity extends AppCompatActivity {
    MaterialButtonToggleGroup tgBtnGrpOperation, tgBtnGrpLevel, tgBtnGrpMode;
    Button btnSettingDone, btnScoreBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting);

        // set onClickListener for toggle button groups
        tgBtnGrpOperation = findViewById(R.id.toggleBtnGrpOperationSelection);
        tgBtnGrpOperation.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingGameAdd:
                            MainActivity.GAME_OPERATION = "add";
                            break;
                        case R.id.btnSettingGameDivide:
                            MainActivity.GAME_OPERATION = "divide";
                            break;
                        case R.id.btnSettingGameMultiply:
                            MainActivity.GAME_OPERATION = "multiply";
                            break;
                        case R.id.btnSettingGameSubtract:
                            MainActivity.GAME_OPERATION = "subtract";
                            break;
                    }
                }
            }
        });

        tgBtnGrpLevel = findViewById(R.id.toggleBtnGrpLevelSelection);
        tgBtnGrpLevel.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingLevelEasy:
                            MainActivity.GAME_LEVEL = "easy";
                            break;
                        case R.id.btnSettingLevelMedium:
                            MainActivity.GAME_LEVEL = "medium";
                            break;
                        case R.id.btnSettingLevelHard:
                            MainActivity.GAME_LEVEL = "hard";
                            break;
                    }
                }
            }
        });

        tgBtnGrpMode = findViewById(R.id.toggleBtnGrpModeSelection);
        tgBtnGrpMode.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingModeSolo:
                            MainActivity.GAME_MODE = "solo";
                            break;
                        case R.id.btnSettingModeComp:
                            MainActivity.GAME_MODE = "competition";
                            break;
                    }
                }
            }
        });

        btnSettingDone = findViewById(R.id.btnSettingDone);
        btnSettingDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        btnScoreBoard = findViewById(R.id.btnScoreBoard);
        btnScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScoreBoard();
            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openScoreBoard() {
        Intent intent = new Intent(this, ScoreBoardActivity.class);
        startActivity(intent);
    }
}