package mahmud.osman.trend.admin.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.github.fabtransitionactivity.SheetLayout;
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
import mahmud.osman.trend.LoginActivity;
import mahmud.osman.trend.Models.ProfileModel;
import mahmud.osman.trend.ProfileActivity;
import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.fragment.ArtAdminFragment;
import mahmud.osman.trend.admin.app.fragment.EducationAdminFragment;
import mahmud.osman.trend.admin.app.fragment.HealthAdminFragment;
import mahmud.osman.trend.admin.app.fragment.SportAdminFragment;
import mahmud.osman.trend.admin.app.fragment.TrendAdminFragment;


public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

      private DrawerLayout mDrawerLayout;
      private NavigationView navigationView;
      private TextView name, email, logout;
      private CircleImageView profile_pic;
      private String name_text;

      private FloatingActionButton add_new;
      private FragmentManager fragmentManager;
      private FragmentTransaction fragmentTransaction;
      private SheetLayout sheetLayout;

//      CircularReveal.Builder builder;

      //firebase
      FirebaseDatabase firebaseDatabase;
      DatabaseReference databaseReference;
      FirebaseStorage firebaseStorage;
      FirebaseAuth mAuth;

      private static final int REQUEST_CODE = 1;


      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.admin_activity);

            fragmentManager = getSupportFragmentManager();

            logout = findViewById(R.id.logout_btn);
            sheetLayout = findViewById(R.id.expand_fab);

            Toolbar toolbar = findViewById(R.id.toolbar_main);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);

            mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            databaseReference.keepSynced(true);
            firebaseStorage = FirebaseStorage.getInstance();

            mDrawerLayout = findViewById(R.id.drawer);
            navigationView = findViewById(R.id.nev_view);
            View view = navigationView.getHeaderView(0);
            navigationView.setNavigationItemSelectedListener(this);


            name = view.findViewById(R.id.name_filed);
            profile_pic = view.findViewById(R.id.profile_image1);
            email = view.findViewById(R.id.email_filed);
            view.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        Intent intent = new Intent(AdminActivity.this , ProfileActivity.class);
                        intent.putExtra("profile" , "Admin");
                        startActivity(intent);
                  }
            });

            add_new = findViewById(R.id.add);
            sheetLayout.setFab(add_new);
            sheetLayout.setFabAnimationEndListener(new SheetLayout.OnFabAnimationEndListener() {
                  @Override
                  public void onFabAnimationEnd() {

                        Intent intent = new Intent(AdminActivity.this , CreateNews.class);
                        intent.putExtra("edit" , "creat");
                        startActivityForResult(intent , REQUEST_CODE);

                  }
            });


            add_new.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        sheetLayout.expandFab();
                  }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(AdminActivity.this , LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                  }
            });

            returnData();

            Fragment news_fragment = new TrendAdminFragment();
            loadFragment(news_fragment);
            getSupportActionBar().setTitle(getString(R.string.trends));
            navigationView.getMenu().getItem(0).setChecked(true);

      }


      private void loadFragment(Fragment fragment) {
            fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.fragment_container , fragment);
            fragmentTransaction.addToBackStack(null);

            getFragmentManager().popBackStack();
            // Commit the transaction
            fragmentTransaction.commit();
      }


      private void returnData() {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.keepSynced(true);

            mDatabase.child("Admin").child(getUID()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user value
                                ProfileModel profileModel = dataSnapshot.getValue(ProfileModel.class);

                                name_text = profileModel.getName();
                                name.setText(name_text);
                                email.setText(profileModel.getEmail());
                                Picasso.get()
                                        .load(profileModel.getImageUri())
                                        .placeholder(R.drawable.ic_user)
                                        .error(R.drawable.ic_user)
                                        .into(profile_pic);

                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                Toast.makeText(getApplicationContext() , "can\'t fetch data" , Toast.LENGTH_SHORT).show();

                          }
                    });
      }

      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                  case android.R.id.home:
                        mDrawerLayout.openDrawer(GravityCompat.START);
                        return true;
            }
            return super.onOptionsItemSelected(item);
      }

      private String getUID() {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            return id;
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


      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                  case R.id.trend:
                        getSupportActionBar().setTitle(getString(R.string.trends));
                        loadFragment(new TrendAdminFragment());
                        break;
                  case R.id.health:
                        getSupportActionBar().setTitle(getString(R.string.health));
                        loadFragment(new HealthAdminFragment());
                        break;
                  case R.id.education:
                        getSupportActionBar().setTitle(getString(R.string.education));
                        loadFragment(new EducationAdminFragment());

                        break;
                  case R.id.sport:
                        getSupportActionBar().setTitle(getString(R.string.sport));
                        loadFragment(new SportAdminFragment());
                        break;
                  case R.id.art:
                        getSupportActionBar().setTitle(getString(R.string.art));
                        loadFragment(new ArtAdminFragment());
                        break;
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);

            return true;
      }

      @Override
      public void onBackPressed() {
            moveTaskToBack(false);
      }

      @Override
      protected void onActivityResult(int requestCode , int resultCode , @Nullable Intent data) {
            super.onActivityResult(requestCode , resultCode , data);
            if (requestCode == REQUEST_CODE) {
                  sheetLayout.contractFab();
            }
      }
}