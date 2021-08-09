package edu.neu.madcourse.monstermath;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


public class Dialog extends AppCompatDialogFragment {

    private TextView scoreMessageView;
    private int gameScore;

    //constructor
    public Dialog(int gameScore){
        this.gameScore = gameScore;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_dialog, null);

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

        builder.setView(view)
                .setTitle("Result")
                .setPositiveButton("Back to game setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        openGameSettingActivity();
                    }
                });


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
