package mahmud.osman.trend.admin.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;
import com.squareup.picasso.Picasso;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.dialog.DeleteDialog;


public class AdminNewsActivity extends AppCompatActivity {

      String KAY, TYPE;
      DeleteDialog deleteDialog;
      private ImageView news_image;
      private TextView subject, ex_title, cl_title;
      private SpeedDialView fab;
      private DatabaseReference databaseReference;
      private CardView card_title;
      private AppBarLayout appBarLayout;


      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.news_item_activity);

            Bundle extra = getIntent().getExtras();
            Toolbar toolbar = findViewById(R.id.toolbar_collapsing);
            news_image = findViewById(R.id.expand_image);
            subject = findViewById(R.id.subject_scroll);
            ex_title = findViewById(R.id.tv_title);
            cl_title = findViewById(R.id.tb_title);
            fab = findViewById(R.id.fab);
            card_title = findViewById(R.id.cv_title);

             appBarLayout = findViewById(R.id.c_app_bar);

             subject.setMovementMethod(LinkMovementMethod.getInstance());



            KAY = extra.getString("open");
            TYPE = extra.getString("type");

            fabMenu();

            returnData(KAY , TYPE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);

            ex_title.setSelected(true);
            ex_title.requestFocus();
            cl_title.setSelected(true);
            cl_title.requestFocus();




      }

      private void fabMenu() {
            fab.addActionItem(new SpeedDialActionItem.Builder(R.id.edit , R.drawable.ic_action_edit)
                    .setFabBackgroundColor(ResourcesCompat.getColor(getResources() , R.color.background , getTheme()))
                    .setLabel(R.string.edit)
                    .setLabelColor(ResourcesCompat.getColor(getResources() , R.color.gold , getTheme()))
                    .setLabelBackgroundColor(ResourcesCompat.getColor(getResources() , R.color.background , getTheme()))
                    .setLabelClickable(false)
                    .create());
            fab.addActionItem(new SpeedDialActionItem.Builder(R.id.delete , R.drawable.ic_action_delete)
                    .setFabBackgroundColor(ResourcesCompat.getColor(getResources() , R.color.background , getTheme()))
                    .setLabel(R.string.delete)
                    .setLabelColor(ResourcesCompat.getColor(getResources() , R.color.gold , getTheme()))
                    .setLabelBackgroundColor(ResourcesCompat.getColor(getResources() , R.color.background , getTheme()))
                    .setLabelClickable(false)
                    .create());

            fab.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
                  @Override
                  public boolean onActionSelected(SpeedDialActionItem actionItem) {
                        switch (actionItem.getId()) {
                              case R.id.edit:
                                    Intent intent = new Intent(AdminNewsActivity.this , CreateNews.class);
                                    intent.putExtra("edit" , KAY);
                                    intent.putExtra("type" , TYPE);

                                    startActivity(intent);
                                    return true;
                              case R.id.delete:
                                    deleteDialog = new DeleteDialog(AdminNewsActivity.this);
                                    deleteDialog.d_ok.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                                databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child(TYPE).child(KAY).removeValue();
                                                databaseReference.child(getString(R.string.User_news)).child(TYPE).child(KAY).removeValue();
                                                deleteDialog.dismiss();
                                                onBackPressed();
                                                finish();
                                          }
                                    });
                                    deleteDialog.d_cancel.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                                deleteDialog.dismiss();
                                          }
                                    });
                                    deleteDialog.show();
                                    return true;
                        }
                        return false;
                  }
            });


      }

      @Override
      public boolean onCreateOptionsMenu(final Menu menu) {
            getMenuInflater().inflate(R.menu.more_item , menu);
            menu.setGroupVisible(R.id.more,false);
            final MenuItem item = menu.findItem(R.id.more).setVisible(false);

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                  boolean isShow = false;
                  int scrollRange = -1;

                  @Override
                  public void onOffsetChanged(AppBarLayout appBarLayout , int i) {
                        if (scrollRange == -1) {

                              scrollRange = appBarLayout.getTotalScrollRange();
                        }
                        if (scrollRange + i == 0) {
                              isShow = true;
                              fab.close();
                              item.setVisible(true);
                              fab.setVisibility(View.GONE);
                              card_title.setVisibility(View.GONE);
                              cl_title.setVisibility(View.VISIBLE);

                        } else {
                              isShow = false;
                              item.setVisible(false);
                              fab.setVisibility(View.VISIBLE);
                              card_title.setVisibility(View.VISIBLE);
                              cl_title.setVisibility(View.GONE);

                        }


                  }
            });

            return super.onCreateOptionsMenu(menu);
      }

      public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {

                  case android.R.id.home:
                        onBackPressed();
                        return true;

                  case R.id.edit:
                        Intent intent = new Intent(AdminNewsActivity.this , CreateNews.class);
                        intent.putExtra("edit" , KAY);
                        intent.putExtra("type" , TYPE);
                        startActivity(intent);
                        return true;

                  case R.id.delete:
                        deleteDialog = new DeleteDialog(this);
                        deleteDialog.d_ok.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                    databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child(TYPE).child(KAY).removeValue();
                                    databaseReference.child(getString(R.string.User_news)).child(TYPE).child(KAY).removeValue();
                                    deleteDialog.dismiss();
                                    onBackPressed();
                                    finish();
                              }
                        });
                        deleteDialog.d_cancel.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                    deleteDialog.dismiss();
                              }
                        });
                        deleteDialog.show();
                        return true;

                  default:
                        return super.onOptionsItemSelected(item);
            }
      }


      private void returnData(String key , String type) {

            databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.keepSynced(true);
            databaseReference.child(getString(R.string.Admin_news))
                    .child(getUID())
                    .child(type)
                    .child(key).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);

                                ex_title.setText(newsModel.getTitl());
                                cl_title.setText(newsModel.getTitl());
                                subject.setText(newsModel.getSubject());
                                Picasso.get()
                                        .load(newsModel.getImage_uri())
                                        .placeholder(R.drawable.defult_pic)
                                        .error(R.drawable.defult_pic)
                                        .into(news_image);

                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                    });


      }


      private String getUID() {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            return id;
      }

      @Override
      protected void onStart() {
            super.onStart();
            returnData(KAY , TYPE);
      }

      @Override
      protected void onResume() {
            super.onResume();
            returnData(KAY , TYPE);
      }

      @Override
      public void onBackPressed() {
            AdminNewsActivity.super.onBackPressed();
      }

}
