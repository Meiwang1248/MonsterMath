package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ScoreBoardActivity extends AppCompatActivity {

    private TextView textView;

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;

    // Set up for RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager rLayoutManager;
    private ArrayList<ScoreItem> scoreItemArrayList = new ArrayList<>();
    private ScoreReviewAdapter reviewAdapter;

    // Set up for Firebase
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        // Connecting the textview, buttons, and recycler view to layout
        textView = findViewById(R.id.text_score);
        easyButton = findViewById(R.id.easy_score);
        mediumButton = findViewById(R.id.medium_score);
        hardButton = findViewById(R.id.hard_score);

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecyclerView("easy");
            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecyclerView("medium");
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRecyclerView("hard");
            }
        });
    }



    private void createRecyclerView(String difficulty) {

        recyclerView = findViewById(R.id.recycler_view);
        rLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        reviewAdapter = new ScoreReviewAdapter(scoreItemArrayList);
        recyclerView.setAdapter(reviewAdapter);
        recyclerView.setLayoutManager(rLayoutManager);


        showScore(difficulty);
    }

    private void showScore(String difficulty) {
        // Scoreboard显示的东西跟current user是没有关系
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // 要遍历每个user的特定difficulty的分数，但是上周的代码是遍历current user的某一个child
        // 这里需要弄一下

        // if there is no chat message
        databaseReference.orderByKey().equalTo("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(ScoreBoardActivity.this, "No score available", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        readScore(difficulty);
    }

    private void readScore(String difficulty) {

        FirebaseDatabase.getInstance()
                .getReference("Score")
                .child(difficulty)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        scoreItemArrayList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            ScoreItem scoreItem = dataSnapshot.getValue(ScoreItem.class);
                            scoreItemArrayList.add(scoreItem);
                        }
                        // Sort the ArrayList by score
                        // to be done

                        // update adapter
                        reviewAdapter.notifyDataSetChanged();;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                })
    }
}