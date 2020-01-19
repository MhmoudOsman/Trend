package mahmud.osman.trend.admin.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mahmud.osman.trend.LoginActivity;
import mahmud.osman.trend.Models.AdminModel;
import mahmud.osman.trend.Models.MenuModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.fragment.InterviewAdmenFragment;
import mahmud.osman.trend.presenters.adapter.ExpandableListAdaptor;


public class AdminActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    NavigationView navigationView;
    TextView name, email, logout;
    CircleImageView profil_pic;
    String name_text;

    FloatingActionButton add_new;

    ExpandableListAdaptor expandableListAdaptor;
    ExpandableListView expandableListView;
    List<MenuModel> header_list = new ArrayList<>();
    HashMap<MenuModel, List<MenuModel>> chiled_list = new HashMap<>();

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("admin");

        navigationView = findViewById(R.id.nev_veiw);
        View view = navigationView.getHeaderView(0);

        expandableListView = findViewById(R.id.expend_admin);


        name = view.findViewById(R.id.name_filed);
        profil_pic = view.findViewById(R.id.profile_image1);
        email = view.findViewById(R.id.email_filed);

        add_new = findViewById(R.id.add);

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent =new Intent(AdminActivity.this,CreateNews.class);
                intent.putExtra("edit", "creat");
                startActivity(intent);

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        drawer();
        reternData();
        prepareMenuData();
        populateExpandableList();

        Fragment news_fragment = new InterviewAdmenFragment();
        loadFragment(news_fragment);
        getSupportActionBar().setTitle(getString(R.string.interview));

    }

    private void prepareMenuData() {
        String interview, report, health, education, road, prices, unemployment;
        interview = getString(R.string.interview);
        report = getString(R.string.report);
        health = getString(R.string.health);
        education = getString(R.string.education);
        road = getString(R.string.road);
        prices = getString(R.string.prices);
        unemployment = getString(R.string.unemployment);
        MenuModel menuModel;


        menuModel = new MenuModel(interview, false, false, new InterviewAdmenFragment(), getApplicationContext().getResources().getDrawable(R.drawable.ic_interview_gold));
        header_list.add(menuModel);
        if (!menuModel.hasChildren) {
            chiled_list.put(menuModel, null);
        }

        menuModel = new MenuModel(report, true, true, null, getApplicationContext().getResources().getDrawable(R.drawable.ic_reporter_gold));
        header_list.add(menuModel);

        List<MenuModel> childModelsList = new ArrayList<>();
        MenuModel childModel = new MenuModel(health, false, false, null, null);
        childModelsList.add(childModel);
        childModel = new MenuModel(prices, false, false, null, null);
        childModelsList.add(childModel);
        childModel = new MenuModel(road, false, false, null, null);
        childModelsList.add(childModel);
        childModel = new MenuModel(education, false, false, null, null);
        childModelsList.add(childModel);
        childModel = new MenuModel(unemployment, false, false, null, null);
        childModelsList.add(childModel);
        if (menuModel.hasChildren) {
            chiled_list.put(menuModel, childModelsList);
        }
    }


    private void populateExpandableList() {

        expandableListAdaptor = new ExpandableListAdaptor(this, header_list, chiled_list);
        expandableListView.setAdapter(expandableListAdaptor);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override

            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (!header_list.get(groupPosition).isGroup) {
                    if (!header_list.get(groupPosition).hasChildren) {
                        if (header_list.get(groupPosition).fragment != null) {
                            getSupportActionBar().setTitle(header_list.get(groupPosition).menuName);
                            loadFragment(header_list.get(groupPosition).fragment);
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            getSupportActionBar().setTitle(header_list.get(groupPosition).menuName);
                            mDrawerLayout.closeDrawer(GravityCompat.START);
                        }

                    }
                }
                return false;
            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (chiled_list.get(header_list.get(groupPosition)) != null) {
                    MenuModel model = chiled_list.get(header_list.get(groupPosition)).get(childPosition);
                    if (model.fragment != null) {

                        getSupportActionBar().setTitle(model.menuName);
                        loadFragment(model.fragment);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    } else {
                        getSupportActionBar().setTitle(model.menuName);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }

                return false;
            }
        });

    }


    private void loadFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.addToBackStack(null);

        getFragmentManager().popBackStack();
        // Commit the transaction
        fragmentTransaction.commit();
    }

    public void drawer() {
        mDrawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nev_veiw);
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

                        Toast.makeText(getApplicationContext(), "can\'t fetch data", Toast.LENGTH_SHORT).show();

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
    public void onBackPressed() {
        finishAffinity();
    }


}