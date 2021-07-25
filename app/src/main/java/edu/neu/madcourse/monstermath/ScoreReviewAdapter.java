package edu.neu.madcourse.monstermath;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ScoreReviewAdapter extends RecyclerView.Adapter<ScoreReviewAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreReviewAdapter.ViewHolder holder, int position) {

        // Set item views based on views  and data model
        // To be completed
        holder.username.setText("username");
        holder.score.setText("100");
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.score_username);
            score = itemView.findViewById(R.id.score_marks);
        }

    }
}
