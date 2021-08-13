package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.monstermath.Model.User;

public class GameSettingActivity extends AppCompatActivity {
    MaterialButtonToggleGroup tgBtnGrpOperation, tgBtnGrpLevel, tgBtnGrpMode;
    Button btnLogout, btnSettingDone, btnScoreBoard;
    String gameOperation, gameLevel;
    boolean gameMode;
    FirebaseAuth auth;
    String usernameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setting);

        hideSystemUI();
        // set onClickListener for toggle button groups
        setGameOperation();
        setGameLevel();
        setGameMode();

        btnSettingDone = findViewById(R.id.btnSettingDone);
        btnSettingDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("id")
                        .equalTo(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            usernameString = user.getUsername();
                        }
                        if (gameMode) {
                            openGameActivity();
                        } else {
                            openMatchingActivity();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        btnScoreBoard = findViewById(R.id.btnScoreBoard);
        btnScoreBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("id")
                        .equalTo(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            User user = child.getValue(User.class);
                            usernameString = user.getUsername();
                        }
                        openScoreBoard();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

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
                } else {
                    Toast.makeText(GameSettingActivity.this, "Button selection required.", Toast.LENGTH_SHORT).show();
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
                } else {
                    Toast.makeText(GameSettingActivity.this, "Button selection required.", Toast.LENGTH_SHORT).show();
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
                } else {
                    Toast.makeText(GameSettingActivity.this, "Button selection required.", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("USERNAME", usernameString);
        startActivity(intent);
    }

    private void openMatchingActivity() {
        Intent intent = new Intent(this, MatchingActivity.class);
        intent.putExtra("GAME_OPERATION", gameOperation);
        intent.putExtra("GAME_LEVEL", gameLevel);
        intent.putExtra("GAME_MODE", gameMode);
        intent.putExtra("USERNAME", usernameString);
        startActivity(intent);
    }

    private void openScoreBoard() {
        Intent intent = new Intent(this, ScoreBoardActivity.class);
        intent.putExtra("USERNAME", usernameString);
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