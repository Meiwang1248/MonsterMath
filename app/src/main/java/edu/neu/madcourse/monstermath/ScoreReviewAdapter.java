package edu.neu.madcourse.monstermath;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScoreReviewAdapter extends RecyclerView.Adapter<ScoreReviewAdapter.ViewHolder> {

    private ArrayList<ScoreItem> itemList;

    // Constructor
    public ScoreReviewAdapter(ArrayList<ScoreItem> scoreItemList) {
        this.itemList = scoreItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View scoreItemView = inflater.inflate(R.layout.score_item, parent, false);

        // return a new holder instance
        ScoreReviewAdapter.ViewHolder viewHolder = new ViewHolder(scoreItemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreReviewAdapter.ViewHolder holder, int position) {
        // Get the data model based on position
        ScoreItem scoreItem = itemList.get(position);

        // Set item views based on views  and data model
        holder.username.setText(scoreItem.getUsername());
        holder.score.setText(scoreItem.getScore());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
