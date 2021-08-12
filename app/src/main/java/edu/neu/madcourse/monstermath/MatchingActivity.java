package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;

import edu.neu.madcourse.monstermath.Model.Player;
import edu.neu.madcourse.monstermath.Model.User;

public class MatchingActivity extends AppCompatActivity {
    static final String TAG = AppCompatActivity.class.getSimpleName();
    static String GAME_OPERATION, GAME_LEVEL;
    final String NONE = "none";

    Button btnShakeToJoin, btnCreateNewGame;

    // Firebase settings
    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mMatchmaker = database.getReference("Matches");
    DatabaseReference mUsers = database.getReference("Users");

    User user;
    String usernameStr= "";

    // Sensor settings
    SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity

    private final SensorEventListener mSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = mAccel * 0.9f + delta; // perform low-cut filter
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);
        hideSystemUI();

        // link buttons
        btnCreateNewGame = findViewById(R.id.btnCreateNewGame);
        btnShakeToJoin = findViewById(R.id.btnShakeToJoin);

        getUsername();
        getGameSettings();

        btnCreateNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGame();
            }
        });

        btnShakeToJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinExistingGame();
            }
        });

        // Sensor settings
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;

        onShake();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    private void createNewGame() {
        String matchmaker;

        // create a new match
        final DatabaseReference dbReference = mMatchmaker.push();
        HashMap<String, Object> hashMap = new HashMap<>();
        Game newGame = new Game(GAME_OPERATION, GAME_LEVEL, false, 1);
        hashMap.put("player0", new Player(usernameStr, 0));
        hashMap.put("game", newGame);
        dbReference.setValue(hashMap);

        // put questions, options, and correct answers in game
        DatabaseReference dbGame = dbReference.child("game");
        Queue<String> questionQueue = newGame.questionQueue;
        Queue<HashSet<Integer>> optionsQueue = newGame.optionsQueue;
        Queue<Integer> correctOptionQueue = newGame.correctOptionQueue;
        for (int i = 1; i <= 10; i++) {
            dbGame.child("questions")
                    .child("question" + i)
                    .setValue(questionQueue.remove());
            dbGame.child("correctOptions")
                    .child("correctOption" + i)
                    .setValue(correctOptionQueue.remove());
            Iterator iterator = optionsQueue.remove().iterator();
            for (int j = 0; j < 5; j++) {
                dbGame.child("options")
                        .child("options" + i)
                        .child("option" + j)
                        .setValue(iterator.next());
            }
        }

        // get match id
        matchmaker = dbReference.getKey();

        // open matching result dialog
        openMatchingResultDialog(false, matchmaker);
    }

    private void onShake() {
        if (mAccel > 12) {
            Toast toast = Toast.makeText(getApplicationContext(), "Shake activity detected!", Toast.LENGTH_LONG);
            toast.show();
            joinExistingGame();
        }
    }


    private void getGameSettings() {
        GAME_OPERATION = getIntent().getExtras().getString("GAME_OPERATION");
        GAME_LEVEL = getIntent().getExtras().getString("GAME_LEVEL");
    }

    /**
     * Joins an existing game waiting for others to join.
     */
    private void joinExistingGame() {
        mMatchmaker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    // if match is not done
                    if (!snapshot.child("player1").exists()) {
                        // get match id
                        final String matchmaker = snapshot.getKey();
                        // get opponent player
                        String opponentName = snapshot.child("player0").getValue(Player.class).getUsername();
                        // create new player
                        mMatchmaker.child(matchmaker).child("player1").setValue(new Player(usernameStr, 0));
                        // get game settings
                        GAME_LEVEL = snapshot.child("game").child("difficultyLevel").getValue(String.class);
                        GAME_OPERATION = snapshot.child("game").child("operation").getValue(String.class);
                        // open matching result dialog
                        openMatchingResultDialog(true, opponentName, matchmaker);
                    } else {
                        // if no match is found
                        Toast.makeText(getApplicationContext(), "Sorry, currently there is no match available.", Toast.LENGTH_LONG).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openMatchingResultDialog(boolean matchingDone, String opponentName, String matchId) {
        MatchingResultDialog matchingResultDialog = new MatchingResultDialog(GAME_OPERATION,
                GAME_LEVEL,
                opponentName,
                matchingDone,
                matchId);
        matchingResultDialog.show(getSupportFragmentManager(), "matching");
    }

    private void openMatchingResultDialog(boolean matchingDone, String matchId) {
        MatchingResultDialog matchingResultDialog = new MatchingResultDialog(GAME_OPERATION,
                GAME_LEVEL,
                matchingDone,
                matchId);
        matchingResultDialog.show(getSupportFragmentManager(), "matching");
    }

    private void getUsername() {
        // get username
        mUsers.orderByChild("id")
                .equalTo(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                        usernameStr = user.getUsername();
                    }
                } catch(Exception e) {
                    Toast.makeText(MatchingActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

}