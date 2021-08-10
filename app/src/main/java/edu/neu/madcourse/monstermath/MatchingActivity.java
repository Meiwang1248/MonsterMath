package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

import edu.neu.madcourse.monstermath.Model.Player;
import edu.neu.madcourse.monstermath.Model.User;

public class MatchingActivity extends AppCompatActivity {
    static final String TAG = AppCompatActivity.class.getSimpleName();
    static String GAME_OPERATION, GAME_LEVEL;
    final String NONE = "none";

    Button btnShakeToJoin, btnCreateNewGame;

    // Firebase settings
    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mMatchmaker = database.getReference("Matches");
    DatabaseReference mUsers = database.getReference("Users");

    User user;
    String usernameStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        btnCreateNewGame = findViewById(R.id.btnCreateNewGame);
        btnShakeToJoin = findViewById(R.id.btnShakeToJoin);

        getUsername();
        getGameSettings();

        hideSystemUI();

        btnCreateNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGame();
            }
        });

        btnShakeToJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinExistingGame();
            }
        });

    }

    private void createNewGame() {
        String matchmaker;

        // create a new child
        final DatabaseReference dbReference = mMatchmaker.push();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("player0", new Player(usernameStr, 0));
        hashMap.put("game", new Game(GAME_OPERATION, GAME_LEVEL, false, 1));
        hashMap.put("player1", null);
        dbReference.setValue(hashMap);

        matchmaker = dbReference.getKey();
        final String newMatchmaker = matchmaker;


        openMatchingResultDialog(false, "", matchmaker);

//        mMatchmaker.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                if (mutableData.getKey().equals(NONE)) {
//                    return Transaction.success(mutableData);
//                }
//                // someone beat us to posting a game, so fail and retry later
//                return Transaction.abort();
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean commit, DataSnapshot dataSnapshot) {
//                Toast.makeText(getApplicationContext(),
//                        commit ? "transaction success" : "transaction failed",
//                        Toast.LENGTH_SHORT).show();
//                if (!commit) {
//                    // we failed to post the game, so destroy the game so we don't leave trash.
//                    dbReference.removeValue();
//                }
//            }
//        });
    }

    private void getGameSettings() {
        GAME_OPERATION = getIntent().getExtras().getString("GAME_OPERATION");
        GAME_LEVEL = getIntent().getExtras().getString("GAME_LEVEL");
    }

    /**
     * Joins an existing game waiting for others to join.
     */
    private void joinExistingGame() {
        mMatchmaker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if (!snapshot.child("player1").exists()) {
                        final String matchmaker = dataSnapshot.getKey();
                        String opponentName = snapshot.child("player0").getValue(Player.class).getUsername();
                        Log.d(TAG, "mMatchmaker: " + matchmaker);
                        findMatchSecondArriver(matchmaker, opponentName);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void openMatchingResultDialog(boolean matchingDone, String opponentName, String matchId) {
        MatchingResultDialog matchingResultDialog = new MatchingResultDialog(GAME_OPERATION,
                GAME_LEVEL,
                opponentName,
                matchingDone,
                matchId);
        matchingResultDialog.show(getSupportFragmentManager(), "matching");
    }

    private void getUsername() {
        // get username
        mUsers.orderByChild("id")
                .equalTo(currUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        user = child.getValue(User.class);
                        usernameStr = user.getUsername();
                    }
                } catch(Exception e) {
                    Toast.makeText(MatchingActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * The second arriver needs atomically (i.e., with a transcation) verify that the game is
     * still available to join and then remove the game from the matchmaker.  It then adds
     * itself to the game, so that player0 gets a notification that the game was joined.
     * @param matchmaker
     */
    private void findMatchSecondArriver(final String matchmaker, String opponentName) {
        // get game settings
        mMatchmaker.child(matchmaker).child("game").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Game newGame = snapshot.getValue(Game.class);
                GAME_LEVEL = newGame.getDifficultyLevel();
                GAME_OPERATION = newGame.getOperation();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mMatchmaker.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                String test = mutableData.getValue(String.class);
                if (mutableData.getValue(String.class).equals(matchmaker)) {
                    mutableData.setValue(NONE);
                    return Transaction.success(mutableData);
                }
                // someone beat us to joining this game, so fail and retry later
                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot dataSnapshot) {
                if (committed) {
                    // add the second player to Matching database
                    mMatchmaker.child(matchmaker).child("player1").setValue(new Player(usernameStr, 0));
                    openMatchingResultDialog(true, opponentName, matchmaker);
                }
            }
        });
    }

    private void openGameActivity() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("GAME_OPERATION", GAME_OPERATION);
        intent.putExtra("GAME_LEVEL", GAME_LEVEL);
        intent.putExtra("GAME_MODE", false);
        startActivity(intent);
    }


    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}