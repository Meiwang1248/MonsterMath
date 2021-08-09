package edu.neu.madcourse.monstermath;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


public class Dialog extends AppCompatDialogFragment {

    private TextView scoreMessageView, tvPersonalBestNotification;
    private Button btnBackToGameSetting;
    private int gameScore;
    private boolean personalBestFlag;

    //constructor
    public Dialog(int gameScore, boolean personalBestFlag){

        this.gameScore = gameScore;
        this.personalBestFlag = personalBestFlag;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog, null);

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

        tvPersonalBestNotification = view.findViewById(R.id.tvPersonalBestNotification);

        if (personalBestFlag) {
            tvPersonalBestNotification.setVisibility(View.VISIBLE);
        }

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
