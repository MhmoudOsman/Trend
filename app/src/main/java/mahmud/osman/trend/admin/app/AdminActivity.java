package mahmud.osman.trend.admin.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import mahmud.osman.trend.LoginActivity;
import mahmud.osman.trend.Models.AdminModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.fragment.ArtAdminFragment;
import mahmud.osman.trend.admin.app.fragment.EducationAdminFragment;
import mahmud.osman.trend.admin.app.fragment.HealthAdminFragment;
import mahmud.osman.trend.admin.app.fragment.SportAdminFragment;
import mahmud.osman.trend.admin.app.fragment.TrendAdminFragment;


public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
      StorageReference storageReference;
      FirebaseAuth mAuth;


      @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.admin_activity);

            fragmentManager = getSupportFragmentManager();

            logout = findViewById(R.id.logout_btn);

            Toolbar toolbar = findViewById(R.id.toolbar_main);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_menu);

            mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            databaseReference.keepSynced(true);
            firebaseStorage = FirebaseStorage.getInstance();
            storageReference = firebaseStorage.getReference().child("admin");

            mDrawerLayout = findViewById(R.id.drawer);
            navigationView = findViewById(R.id.nev_veiw);
            View view = navigationView.getHeaderView(0);
            navigationView.setNavigationItemSelectedListener(this);


            name = view.findViewById(R.id.name_filed);
            profil_pic = view.findViewById(R.id.profile_image1);
            email = view.findViewById(R.id.email_filed);

            add_new = findViewById(R.id.add);

            add_new.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                        Intent intent = new Intent(AdminActivity.this , CreateNews.class);
                        intent.putExtra("edit" , "creat");
                        startActivity(intent);

                  }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(AdminActivity.this , LoginActivity.class);
                        startActivity(intent);

                  }
            });

            reternData();

            Fragment news_fragment = new TrendAdminFragment();
            loadFragment(news_fragment);
            getSupportActionBar().setTitle(getString(R.string.trends));
            navigationView.getMenu().getItem(0).setChecked(true);

      }


      private void loadFragment(Fragment fragment) {
            fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.content_frame , fragment);
            fragmentTransaction.addToBackStack(null);

            getFragmentManager().popBackStack();
            // Commit the transaction
            fragmentTransaction.commit();
      }



      public void reternData() {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.keepSynced(true);

            mDatabase.child("Admin").child(getUID()).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                                // Get user value
                                AdminModel adminModel = dataSnapshot.getValue(AdminModel.class);

                                name_text = adminModel.getName();
                                name.setText(name_text);
                                email.setText(adminModel.getEmail());
                                Picasso.get()
                                        .load(adminModel.getImageUri())
                                        .placeholder(R.drawable.ic_user)
                                        .error(R.drawable.ic_user)
                                        .into(profil_pic);

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
      public void onPointerCaptureChanged(boolean hasCapture) {

      }

      @Override
      public void onBackPressed() {
            finishAffinity();
      }

}