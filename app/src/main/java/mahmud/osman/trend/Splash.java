package mahmud.osman.trend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import mahmud.osman.trend.admin.app.AdminActivity;
import mahmud.osman.trend.dialog.ReconnectDialog;
import mahmud.osman.trend.user.app.UserActivity;

import static mahmud.osman.trend.utils.Utils.getUID;
import static mahmud.osman.trend.utils.Utils.isConnected;


public class Splash extends AppCompatActivity {

    ImageView logo;

    Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        logo = findViewById(R.id.logo);

        animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        logo.startAnimation(animation);
        if (isConnected(Splash.this)) {
            Handler handler =new Handler();
            handler.postDelayed(this::checkUser,4000);
        }else{
            ReconnectDialog dialog = new ReconnectDialog(Splash.this);
            dialog.getRetry().setOnClickListener(v -> {
                dialog.getError_massage().setVisibility(View.GONE);
                dialog.getReconnect_loading().start();

                if (isConnected(Splash.this)) {
                    Handler handler =new Handler();
                    handler.postDelayed(this::checkUser,4000);
                    dialog.dismiss();
                }else {
                    dialog.getError_massage().setVisibility(View.VISIBLE);
                    dialog.getReconnect_loading().stop();
                }
            });
            dialog.show();
        }


    }



    private void checkUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            category();
        } else {
            updateUI(LoginActivity.class);
        }
    }


    public void category() {

        if (isConnected(this)) {
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
        } else {
            Toast.makeText(this, "Connection Filed", Toast.LENGTH_SHORT).show();

        }
    }

    public void updateUI(Class aClass) {
        // go to the main activity
        Intent i = new Intent(Splash.this, aClass);
        startActivity(i);
        // kill current activity
        finish();
    }

}
