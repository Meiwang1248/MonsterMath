package edu.neu.madcourse.monstermath;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



import edu.neu.madcourse.monstermath.Model.Player;

public class MatchingResultDialog extends AppCompatDialogFragment {
    private LinearLayout layoutMatchingRunning, layoutMatchingDone;
    private TextView tvMatchingResultTitle, tvMatchingOpponentInfo;
    private Button btnStartOnlineGame, btnCancelMatching;
    private String operation, level, opponentName, matchId;
    private boolean matchingDone;
    DatabaseReference rootFirebaseRef;

    //constructors
    public MatchingResultDialog(String operation, String level, String opponentName, boolean matchingDone, String matchId) {
        this.operation = operation;
        this.level = level;
        this.opponentName = opponentName;
        this.matchingDone = matchingDone;
        this.matchId = matchId;
    }

    public MatchingResultDialog(String operation, String level, boolean matchingDone, String matchId) {
        this.operation = operation;
        this.level = level;
        this.matchId = matchId;
        this.matchingDone = matchingDone;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_matching_result_dialog, null);

        rootFirebaseRef = FirebaseDatabase.getInstance().getReference();
        // set visibility for layouts
        layoutMatchingRunning = view.findViewById(R.id.layoutMatchingRunning);
        layoutMatchingDone = view.findViewById(R.id.layoutMatchingDone);
        tvMatchingOpponentInfo = view.findViewById(R.id.tvMatchingOpponentInfo);
        tvMatchingResultTitle = view.findViewById(R.id.tvMatchingResultTitle);
        btnStartOnlineGame = view.findViewById(R.id.btnStartOnlineGame);
        btnCancelMatching = view.findViewById(R.id.btnCancelMatching);

        if (matchingDone) {
            showMatchingDone();
        } else {
            showMatchingRunning();
        }

        // set start online game button
        btnStartOnlineGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameActivity();
            }
        });

        btnCancelMatching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelMatching();
            }
        });

        builder.setView(view);
        return builder.create();
    }

    private void cancelMatching() {
        rootFirebaseRef.child("Matches").child(matchId).removeValue();
        startActivity(new Intent(getContext(), GameSettingActivity.class));
    }

    private void showMatchingDone() {
        layoutMatchingDone.setVisibility(View.VISIBLE);
        layoutMatchingRunning.setVisibility(View.INVISIBLE);
        // set matching info
        tvMatchingResultTitle.setText("Match found!");
        tvMatchingOpponentInfo.setText("You are playing against: " + opponentName);
    }

    private void showMatchingRunning() {
        layoutMatchingDone.setVisibility(View.INVISIBLE);
        layoutMatchingRunning.setVisibility(View.VISIBLE);
        // show matching result as soon as matching done
        rootFirebaseRef.child("Matches")
                .child(matchId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("player1").exists()) {
                    // get opponent name
                    opponentName = snapshot.child("player1").getValue(Player.class).getUsername();
                    // show matching done
                    showMatchingDone();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void openGameActivity() {
        Intent intent = new Intent(getContext(), GameActivity.class);
        intent.putExtra("GAME_OPERATION", operation);
        intent.putExtra("GAME_LEVEL", level);
        intent.putExtra("GAME_MODE", false);
        startActivity(intent);
    }

}
