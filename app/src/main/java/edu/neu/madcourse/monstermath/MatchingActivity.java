package edu.neu.madcourse.monstermath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    final String NONE = "none";

    Button btnShakeToJoin, btnCreateNewGame;

    // Firebase settings
    FirebaseUser currUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mMatchmaker = database.getReference("Matches");
//    DatabaseReference mGamesReference = database.getReference("Games");
    DatabaseReference mUsers = database.getReference("Users");

    User user;
    String usernameStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        btnCreateNewGame = findViewById(R.id.btnCreateNewGame);
        btnShakeToJoin = findViewById(R.id.btnShakeToJoin);

        btnShakeToJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinExistingGame();
            }
        });

        btnCreateNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewGame();
            }
        });
    }

    private void createNewGame() {
        String matchmaker;

        // create a new child
        final DatabaseReference dbReference = mMatchmaker.push();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("player0", new Player(usernameStr, 0));
        hashMap.put("game", new Game(GameActivity.GAME_OPERATION, GameActivity.GAME_LEVEL, false, 1));
        dbReference.setValue(hashMap);

        matchmaker = dbReference.getKey();
        final String newMatchmaker = matchmaker;

        mMatchmaker.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue(String.class).equals(NONE)) {
                    mutableData.setValue(newMatchmaker);
                    return Transaction.success(mutableData);
                }
                // someone beat us to posting a game, so fail and retry later
                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commit, DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(),
                        commit ? "transaction success" : "transaction failed",
                        Toast.LENGTH_SHORT).show();
                if (!commit) {
                    // we failed to post the game, so destroy the game so we don't leave trash.
                    dbReference.removeValue();
                }
            }
        });
    }

    private void joinExistingGame() {
    }

    private void makeMatch() {
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

        mMatchmaker.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String secondPlayer = "";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.getValue(String.class).equals("available") && snapshot.getKey() != usernameStr) {
                        secondPlayer = snapshot.getKey();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * This function is the callback of the "Find Match" button.   This function reads the current
     * value of the matchmaker storage location to determine if it thinks that we're the first arriver
     * or the second.
     * @param view
     */
    public void findMatch(View view) {
        mMatchmaker.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String matchmaker = dataSnapshot.getValue(String.class);
                Log.d(TAG, "mMatchmaker: " + matchmaker);

                if (matchmaker.equals(NONE)) {
                    findMatchFirstArriver();
                } else {
                    findMatchSecondArriver(matchmaker);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * The first arriver needs to create the game, add themselves to it, and then atomically
     * (i.e., using a transaction) verify that no one else has posted a game yet and post the game.
     * If it fails to post the game, it destroys the game.
     */
    private void findMatchFirstArriver() {
        String matchmaker;

        // create a new child
        final DatabaseReference dbReference = mMatchmaker.push();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("player0", new Player(usernameStr, 0));
        //hashMap.put("game", new Game())
        dbReference.push().setValue("player0");
        matchmaker = dbReference.getKey();
        final String newMatchmaker = matchmaker;

        mMatchmaker.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue(String.class).equals(NONE)) {
                    mutableData.setValue(newMatchmaker);
                    return Transaction.success(mutableData);
                }
                // someone beat us to posting a game, so fail and retry later
                return Transaction.abort();
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean commit, DataSnapshot dataSnapshot) {
                Toast.makeText(getApplicationContext(),
                        commit ? "transaction success" : "transaction failed",
                        Toast.LENGTH_SHORT).show();
                if (!commit) {
                    // we failed to post the game, so destroy the game so we don't leave trash.
                    dbReference.removeValue();
                }
            }
        });
    }

    /**
     * The second arriver needs atomically (i.e., with a transcation) verify that the game is
     * still available to join and then remove the game from the matchmaker.  It then adds
     * itself to the game, so that player0 gets a notification that the game was joined.
     * @param matchmaker
     */
    private void findMatchSecondArriver(final String matchmaker) {
        mMatchmaker.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
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
                    DatabaseReference gameReference = mMatchmaker.child(matchmaker);
                    // add the second player to Matching database
                    gameReference.push().setValue("player1");
                    mMatchmaker.setValue(NONE);
                }
            }
        });


    }
}