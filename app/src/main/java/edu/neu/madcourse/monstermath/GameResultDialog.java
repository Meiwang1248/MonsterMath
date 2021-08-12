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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


public class GameResultDialog extends AppCompatDialogFragment {
    private LinearLayout gameResultWaiting, gameResultDone;
    private TextView scoreMessageView, tvPersonalBestNotification;
    private Button btnBackToGameSetting;
    private int gameScore, opponentScore;
    private boolean personalBestFlag, gameMode, onlineGameFinished;
    private String opponentName;

    //constructor
    public GameResultDialog(boolean gameMode, int gameScore, boolean personalBestFlag) {
        this.gameMode = gameMode;
        this.gameScore = gameScore;
        this.personalBestFlag = personalBestFlag;
    }

    public GameResultDialog(int gameScore, boolean personalBestFlag, boolean gameMode, boolean onlineGameFinished, String opponentName) {
        this.gameScore = gameScore;
        this.personalBestFlag = personalBestFlag;
        this.gameMode = gameMode;
        this.opponentName = opponentName;
        this.onlineGameFinished = onlineGameFinished;
    }

    public GameResultDialog(int gameScore, boolean personalBestFlag, boolean gameMode, boolean onlineGameFinished, String opponentName, int oppoentScore) {
        this.gameScore = gameScore;
        this.personalBestFlag = personalBestFlag;
        this.gameMode = gameMode;
        this.opponentName = opponentName;
        this.onlineGameFinished = onlineGameFinished;
        this.opponentScore = oppoentScore;
    }

    @Override
    public AlertDialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_game_result_dialog, null);

        scoreMessageView = view.findViewById(R.id.scoreMessage);
        gameResultWaiting = view.findViewById(R.id.gameResultWaiting);
        gameResultDone = view.findViewById(R.id.gameResultDone);

        if (gameMode) {
            showSoloGameResult();
        } else {
            if (!onlineGameFinished) {
                onlineGameWaiting();
            } else {
                showOnlineGameResult();
            }
        }

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

    private void onlineGameWaiting() {
        gameResultWaiting.setVisibility(View.VISIBLE);
    }

    private void showOnlineGameResult() {
        String scoreMessage = "";
        if (gameScore > opponentScore) {
            scoreMessage = "You win!\n Your score: " + gameScore + "\n" + opponentName + "'s score: " + opponentScore;
        } else if (gameScore == opponentScore) {
            scoreMessage = "Tie!\n Your score: " + gameScore + "\n" + opponentName + "'s score: " + opponentScore;
        } else {
            scoreMessage = "You lose!\n Your score: " + gameScore + "\n" + opponentName + "'s score: " + opponentScore;
        }
        scoreMessageView.setText(scoreMessage);
        gameResultWaiting.setVisibility(View.INVISIBLE);
        gameResultDone.setVisibility(View.VISIBLE);
    }

    private void showSoloGameResult() {
        String scoreMessage = "";

        if (gameScore >= 100) {
            scoreMessage = "Bravo! Your Score is ";
        } else if (gameScore >= 90) {
            scoreMessage = "Good job! Your Score is ";
        } else {
            scoreMessage = "Nice! Your Score is ";
        }
        scoreMessageView.setText(scoreMessage + this.gameScore + ".");
        gameResultDone.setVisibility(View.VISIBLE);
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
