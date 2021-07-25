package edu.neu.madcourse.monstermath;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreBoardActivity extends AppCompatActivity {

    private TextView textView;

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager rLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        // Connecting the textview, buttons, and recycler view to layout
        textView = findViewById(R.id.text_score);
        easyButton = findViewById(R.id.easy_score);
        mediumButton = findViewById(R.id.medium_score);
        hardButton = findViewById(R.id.hard_score);
        recyclerView = findViewById(R.id.recycler_view);

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mediumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}