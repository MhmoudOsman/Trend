package mahmud.osman.trend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import mahmud.osman.trend.admin.app.AdminActivity;
import mahmud.osman.trend.user.app.UserActivity;

import static mahmud.osman.trend.Utils.getUID;


public class Splash extends AppCompatActivity {

    ImageView logo;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        logo = findViewById(R.id.logo);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
        logo.startAnimation(animation);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            category();
        }else{
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // go to the main activity
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                // kill current activity
                finish();
            }
        };
        // Show splash screen for 3 seconds
        new Timer().schedule(task, 4000);}

    }
    public void category() {
        final String id = getUID();
        FirebaseDatabase firebaseDatabase;
        final DatabaseReference databaseReference;

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        databaseReference.child("Admin").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(id)) {
                            updateUI(AdminActivity.class);
                        } else {
                            databaseReference.child("Users").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.hasChild(id)) {
                                                updateUI(UserActivity.class);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void updateUI(Class aClass) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // go to the main activity
                Intent i = new Intent(Splash.this,aClass );
                startActivity(i);
                // kill current activity
                finish();
            }
        };
        // Show splash screen for 3 seconds
        new Timer().schedule(task, 5000);
    }
    
}
