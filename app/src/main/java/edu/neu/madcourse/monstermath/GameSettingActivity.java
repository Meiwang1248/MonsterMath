package edu.neu.madcourse.monstermath;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;

public class GameSettingActivity extends AppCompatActivity {
    MaterialButtonToggleGroup tgBtnGrpOperation, tgBtnGrpLevel, tgBtnGrpMode;
    Button btnLogout, btnSettingDone, btnScoreBoard;
    String gameOperation, gameLevel;
    boolean gameMode;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting);

        hideSystemUI();

        // set onClickListener for toggle button groups
        // setGameOperation();
        tgBtnGrpOperation = findViewById(R.id.toggleBtnGrpOperationSelection);
        tgBtnGrpOperation.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingGameAdd:
                            gameOperation = "+";
                            break;
                        case R.id.btnSettingGameDivide:
                            gameOperation = "÷";
                            break;
                        case R.id.btnSettingGameMultiply:
                            gameOperation = "×";
                            break;
                        case R.id.btnSettingGameSubtract:
                            gameOperation = "-";
                            break;
                    }
                }
            }
        });
        // setGameLevel();
        tgBtnGrpLevel = findViewById(R.id.toggleBtnGrpLevelSelection);
        tgBtnGrpLevel.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingLevelEasy:
                            gameLevel = "easy";
                            break;
                        case R.id.btnSettingLevelMedium:
                            gameLevel = "medium";
                            break;
                        case R.id.btnSettingLevelHard:
                            gameLevel = "hard";
                            break;
                    }
                }
            }
        });

        // setGameMode();
        tgBtnGrpMode = findViewById(R.id.toggleBtnGrpModeSelection);
        tgBtnGrpMode.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingModeSolo:
                            gameMode = true;
                            break;
                        case R.id.btnSettingModeComp:
                            gameMode = false;
                            break;
                    }
                }
            }
        });

        btnSettingDone = findViewById(R.id.btnSettingDone);
        btnSettingDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameMode) {
                    openGameActivity();
                } else {
                    openMatchingActivity();
                }
            }
        });

        btnScoreBoard = findViewById(R.id.btnScoreBoard);
        btnScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openScoreBoard();
            }
        });
        
        btnLogout = findViewById(R.id.btnSettingLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    private void setGameOperation() {
        tgBtnGrpOperation = findViewById(R.id.toggleBtnGrpOperationSelection);
        tgBtnGrpOperation.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingGameAdd:
                            gameOperation = "+";
                            break;
                        case R.id.btnSettingGameDivide:
                            gameOperation = "÷";
                            break;
                        case R.id.btnSettingGameMultiply:
                            gameOperation = "×";
                            break;
                        case R.id.btnSettingGameSubtract:
                            gameOperation = "-";
                            break;
                    }
                }
            }
        });
    }

    private void setGameLevel() {
        tgBtnGrpLevel = findViewById(R.id.toggleBtnGrpLevelSelection);
        tgBtnGrpLevel.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingLevelEasy:
                            gameLevel = "easy";
                            break;
                        case R.id.btnSettingLevelMedium:
                            gameLevel = "medium";
                            break;
                        case R.id.btnSettingLevelHard:
                            gameLevel = "hard";
                            break;
                    }
                }
            }
        });
    }

    private void setGameMode() {
        tgBtnGrpMode = findViewById(R.id.toggleBtnGrpModeSelection);
        tgBtnGrpMode.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnSettingModeSolo:
                            gameMode = true;
                            break;
                        case R.id.btnSettingModeComp:
                            gameMode = false;
                            break;
                    }
                }
            }
        });
    }

    private void logOut() {
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        openStartActivity();
    }

    private void openStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }
    private void openGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("GAME_OPERATION", gameOperation);
        intent.putExtra("GAME_LEVEL", gameLevel);
        intent.putExtra("GAME_MODE", gameMode);
        startActivity(intent);
    }

    private void openMatchingActivity() {
        Intent intent = new Intent(this, MatchingActivity.class);
        intent.putExtra("GAME_OPERATION", gameOperation);
        intent.putExtra("GAME_LEVEL", gameLevel);
        intent.putExtra("GAME_MODE", gameMode);
        startActivity(intent);
    }

    private void openScoreBoard() {
        Intent intent = new Intent(this, ScoreBoardActivity.class);
        startActivity(intent);
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
}