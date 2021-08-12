package edu.neu.madcourse.monstermath;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


public class GameResultDialog extends AppCompatDialogFragment {

    private TextView scoreMessageView, tvPersonalBestNotification;
    private Button btnBackToGameSetting;
    private int gameScore;
    private boolean personalBestFlag;

    //constructor
    public GameResultDialog(int gameScore, boolean personalBestFlag) {

        this.gameScore = gameScore;
        this.personalBestFlag = personalBestFlag;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_game_result_dialog, null);

        // set score message
        scoreMessageView = view.findViewById(R.id.scoreMessage);

        String scoreMessage = "";

        if (gameScore >= 100) {
            scoreMessage = "Bravo! Your Score is ";
        } else if (gameScore >= 90) {
            scoreMessage = "Good job! Your Score is ";
        } else {
            scoreMessage = "Nice! Your Score is ";
        }

        scoreMessageView.setText(scoreMessage + this.gameScore + ".");

        // set personal best notification
        tvPersonalBestNotification = view.findViewById(R.id.tvPersonalBestNotification);

        if (personalBestFlag) {
            tvPersonalBestNotification.setVisibility(View.VISIBLE);
        }

        // set back to game setting button
        btnBackToGameSetting = view.findViewById(R.id.btnBackToGameSetting);
        btnBackToGameSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGameSettingActivity();
            }
        });

        builder.setView(view);
        return builder.create();
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void openGameSettingActivity() {
        Intent intent = new Intent(getContext(), GameSettingActivity.class);
        startActivity(intent);
    }



}
