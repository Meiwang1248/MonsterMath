package edu.neu.madcourse.monstermath.Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.neu.madcourse.monstermath.R;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, score, rank;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvScoreUsername);
            score = itemView.findViewById(R.id.tvScoreMark);
            rank = itemView.findViewById(R.id.tvScoreRank);
        }
    }

    private List<Score> scoreList;

    public ScoreAdapter(List<Score> scoreList) {
        this.scoreList = scoreList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.score_item, parent, false);

        // Return a new holder instance
        ScoreAdapter.ViewHolder viewHolder = new ScoreAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        Score score = scoreList.get(position);

        // Set item views based on views and data model
        holder.username.setText(score.getUsername());
        holder.score.setText(String.valueOf(score.getScore()));
        holder.rank.setText(String.valueOf(holder.getAdapterPosition() - getItemCount() + 1));
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }


}
