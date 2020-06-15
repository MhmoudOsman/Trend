package mahmud.osman.trend.user.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import mahmud.osman.trend.Covid19Fragment;
import mahmud.osman.trend.LoginActivity;
import mahmud.osman.trend.Models.UserProfileModel;
import mahmud.osman.trend.ProfileActivity;
import mahmud.osman.trend.R;
import mahmud.osman.trend.user.app.fragment.ArtUserFragment;
import mahmud.osman.trend.user.app.fragment.EducationUserFragment;
import mahmud.osman.trend.user.app.fragment.HealthUserFragment;
import mahmud.osman.trend.user.app.fragment.SportUserFragment;
import mahmud.osman.trend.user.app.fragment.TrendUserFragment;

import static mahmud.osman.trend.utils.Utils.getUID;

public class UserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    TextView name, email, logout;
    CircleImageView profil_pic;
    String name_text;

    FloatingActionButton add_new;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    //firebase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        add_new = findViewById(R.id.add);
        add_new.setVisibility(View.GONE);

        logout = findViewById(R.id.logout_btn_user);


        fragmentManager = getSupportFragmentManager();


        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
        firebaseStorage = FirebaseStorage.getInstance();

        mDrawerLayout = findViewById(R.id.drawer_user);
        navigationView = findViewById(R.id.nev_veiw_user);
        View view = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        name = view.findViewById(R.id.name_filed);
        profil_pic = view.findViewById(R.id.profile_image1);
        email = view.findViewById(R.id.email_filed);
        view.setOnClickListener(v -> {
            Intent intent = new Intent(UserActivity.this, ProfileActivity.class);
            intent.putExtra("profile", "Users");
            startActivity(intent);
        });

        logout.setOnClickListener(v -> {

            FirebaseAuth.getInstance().signOut();

            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        });

        returnData();

        loadFragment( new TrendUserFragment());
        getSupportActionBar().setTitle(getString(R.string.trends));
        navigationView.getMenu().getItem(0).setChecked(true);
    }


    private void loadFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);

        getFragmentManager().popBackStack();
        // Commit the transaction
        fragmentTransaction.commit();
    }


    private void returnData() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mDatabase.child("Users").child(getUID()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        UserProfileModel userModel = dataSnapshot.getValue(UserProfileModel.class);

                        name_text = userModel.getName();
                        name.setText(name_text);
                        email.setText(userModel.getEmail());

                            Picasso.get()
                                    .load(userModel.getImageUri())
                                    .placeholder(R.drawable.ic_user)
                                    .error(R.drawable.ic_user)
                                    .into(profil_pic);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "can't fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.trend:
                getSupportActionBar().setTitle(getString(R.string.trends));
                loadFragment(new TrendUserFragment());
                break;
            case R.id.health:
                getSupportActionBar().setTitle(getString(R.string.health));
                loadFragment(new HealthUserFragment());
                break;
            case R.id.education:
                getSupportActionBar().setTitle(getString(R.string.education));
                loadFragment(new EducationUserFragment());
                break;
            case R.id.sport:
                getSupportActionBar().setTitle(getString(R.string.sport));
                loadFragment(new SportUserFragment());
                break;
            case R.id.art:
                getSupportActionBar().setTitle(getString(R.string.art));
                loadFragment(new ArtUserFragment());
                break;
            case R.id.cov19:
                getSupportActionBar().setTitle("احصائيات كرونا");
                loadFragment(new Covid19Fragment());
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        returnData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        returnData();
    }


}
