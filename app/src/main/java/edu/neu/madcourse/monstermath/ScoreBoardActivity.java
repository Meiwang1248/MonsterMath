package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.monstermath.Model.Score;
import edu.neu.madcourse.monstermath.Model.ScoreAdapter;

public class ScoreBoardActivity extends AppCompatActivity {

    private String level;
    ArrayList<Score> scoreList = new ArrayList<>();
    RecyclerView rvScores;
    ScoreAdapter adapter;

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