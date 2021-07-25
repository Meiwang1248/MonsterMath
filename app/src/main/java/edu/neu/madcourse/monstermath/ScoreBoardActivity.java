package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.util.ArrayList;

import edu.neu.madcourse.monstermath.Model.Score;
import edu.neu.madcourse.monstermath.Model.ScoreAdapter;
import edu.neu.madcourse.monstermath.Model.User;

public class ScoreBoardActivity extends AppCompatActivity {
    static final String TAG = MainActivity.class.getSimpleName();
    private String level;
    private ArrayList<Score> scoreList = new ArrayList<>();
    private RecyclerView rvScores;
    private ScoreAdapter adapter;

    // personal best score settings
    private TextView tvPersonalBestScore, tvNumOfGamesPlayed;
    private int numOfGamesPlayed;
    String usernameStr;

    // Firebase settings
    FirebaseUser currUser;
    DatabaseReference databaseReference;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        // default level is easy
        level = "easy";

        // set up for RecyclerView
        rvScores = (RecyclerView) findViewById(R.id.rvScores);
        adapter = new ScoreAdapter(scoreList);
        rvScores.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvScores.setLayoutManager(layoutManager);

        // set up for toggle button group
        MaterialButtonToggleGroup toggleButtonGroupScore = findViewById(R.id.toggleBtnGrpScore);
        // add a default check state
        toggleButtonGroupScore.check(R.id.btnScoreEasy);
        // add on button checked listener
        toggleButtonGroupScore.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
                if (isChecked) {
                    switch (checkedId) {
                        case R.id.btnScoreEasy:
                            level = "easy";
                            break;
                        case R.id.btnScoreMedium:
                            level = "medium";
                            break;
                        case R.id.btnScoreHard:
                            level = "hard";
                            break;
                    }
                }
            }
        });

        // read personal best score and number of games played
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.orderByChild("id")
                .equalTo(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                        usernameStr = user.getUsername();

                        // get real-time number of games played
                        databaseReference.child(user.getUsername())
                                .child("numOfGamesPlayed").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                numOfGamesPlayed = snapshot.getValue(Integer.class);
                                tvNumOfGamesPlayed.setText(usernameStr + ", you have played" + numOfGamesPlayed + " rounds");
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // get personal best score and number of games played
                        databaseReference.child(usernameStr)
                                .child("scores")
                                .orderByChild("score")
                                .limitToLast(1)
                                .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    Toast.makeText(ScoreBoardActivity.this, "You have not played a game yet.", Toast.LENGTH_LONG).show();
                                } else {
                                    Integer personalBest = snapshot.getValue(Integer.class);
                                    tvPersonalBestScore.setText("your personal best score: " + personalBest);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        // get current user's token
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }
                                        String token = task.getResult();

                                        FirebaseDatabase
                                                .getInstance()
                                                .getReference()
                                                .child("Users")
                                                .child(usernameStr)
                                                .child("token")
                                                .setValue(token);
                                    }
                                });

                    }
                } catch(Exception e) {
                    Toast.makeText(ScoreBoardActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // read score rankings
        readScoreRanking();
    }

    private void readScoreRanking() {
        FirebaseDatabase.getInstance()
                .getReference("Scores")
                .child(level)
                .orderByChild("score")
                .limitToLast(50)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        scoreList.clear();
                        for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                            Score score = dataSnapshot.getValue(Score.class);
                            scoreList.add(0, score);
                        }
                        // update adapter
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}