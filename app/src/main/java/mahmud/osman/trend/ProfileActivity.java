package mahmud.osman.trend;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import mahmud.osman.trend.Models.ProfileModel;
import mahmud.osman.trend.Models.UserProfileModel;
import mahmud.osman.trend.dialog.EditDialog;

import static mahmud.osman.trend.Utils.getUID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

      private TextView tv_email, tv_name, tv_mobile;
      private CardView cardView;
      private CircleImageView profile_pic;
      private ImageButton edit_name_btn, edit_mobile_btn;

      //Firebase
      private FirebaseAuth mAuth;
      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;
      private StorageReference storageReference, rf;

      private Uri add_pic;

      private String name, mobile, email, profile, exist_pic;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.profile_activity);

            Bundle extra = getIntent().getExtras();
            profile = extra.getString("profile");

            mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            databaseReference.keepSynced(true);

            tv_email = findViewById(R.id.tv_email);
            tv_mobile = findViewById(R.id.tv_mobile);
            tv_name = findViewById(R.id.tv_name);
            profile_pic = findViewById(R.id.civ_profile_pic);
            cardView = findViewById(R.id.cv_mobile);
            edit_mobile_btn = findViewById(R.id.edit_mobile_btn);
            edit_name_btn = findViewById(R.id.edit_name_btn);

            returnData();

            edit_name_btn.setOnClickListener(this);
            edit_mobile_btn.setOnClickListener(this);
            profile_pic.setOnClickListener(this);


      }

      private void returnData() {
            if (profile.equals("Users")) {
                  cardView.setVisibility(View.GONE);
                  databaseReference.child(profile).child(getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              ProfileModel model = dataSnapshot.getValue(ProfileModel.class);
                              if (model != null) {
                                    Picasso.get()
                                            .load(model.getImageUri())
                                            .placeholder(R.drawable.ic_user)
                                            .error(R.drawable.ic_user)
                                            .into(profile_pic);
                                    exist_pic = model.getImageUri();
                                    email = model.getEmail();
                                    name = model.getName();
                                    tv_email.setText(email);
                                    tv_name.setText( name);
                              }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                  });

            } else {
                  databaseReference.child(profile).child(getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                              ProfileModel model = dataSnapshot.getValue(ProfileModel.class);
                              if (model != null) {
                                    Picasso.get()
                                            .load(model.getImageUri())
                                            .placeholder(R.drawable.ic_user)
                                            .error(R.drawable.ic_user)
                                            .into(profile_pic);

                                    exist_pic = model.getImageUri();
                                    email = model.getEmail();
                                    name = model.getName();
                                    mobile = model.getMobile();

                                    tv_name.setText(name);
                                    tv_mobile.setText(mobile);
                                    tv_email.setText(email);


                              }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                  });
            }
      }


      public void onClick(View view) {

            switch (view.getId()) {
                  case R.id.edit_name_btn:
                        final EditDialog nameDialog = new EditDialog(this , "Enter your name");
                        nameDialog.setCancelable(true);
                        nameDialog.d_edit.setText(name);

                        nameDialog.d_ok.setOnClickListener(v -> {
                              if (!nameDialog.isNameValid()) {
                                    return;
                              }
                              name = nameDialog.d_edit.getText().toString();
                              saveInfoDB(exist_pic,name,email,mobile);
                              returnData();
                              nameDialog.dismiss();
                        });

                        nameDialog.d_cancel.setOnClickListener(v -> nameDialog.dismiss());
                        nameDialog.show();
                        break;
                  case R.id.edit_mobile_btn:
                        final EditDialog mobileDialog = new EditDialog(this , "Enter your mobile");
                        mobileDialog.setCancelable(true);
                        mobileDialog.d_edit.setText(mobile);
                        mobileDialog.d_ok.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                    if (!mobileDialog.isMobileValid()) {
                                          return;
                                    }
                                    mobile = mobileDialog.d_edit.getText().toString();
                                    saveInfoDB(exist_pic,name,email,mobile);
                                    returnData();
                                    mobileDialog.dismiss();
                              }
                        });
                        mobileDialog.d_cancel.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                    mobileDialog.dismiss();
                              }
                        });
                        mobileDialog.show();
                        break;
                  case R.id.civ_profile_pic:
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                                .setAutoZoomEnabled(true)
                                .start(this);
                        break;
            }
      }


      private void updateInfo(final String name , final String mobile , final String email , Uri add_pic) {

            UploadTask uploadTask;

            if (add_pic == null) {
                  saveInfoDB(name,email,mobile);
            }
            if (profile.equals("Users")) {
                  storageReference = FirebaseStorage.getInstance().getReference().child("user");
                  rf = storageReference.child("/UserPic" + add_pic.getLastPathSegment());

            } else {
                  storageReference = FirebaseStorage.getInstance().getReference().child("admin");
                  rf = storageReference.child("/AdminPic" + add_pic.getLastPathSegment());
            }
            uploadTask = rf.putFile(add_pic);

            uploadTask.continueWithTask(task -> {
                  if (!task.isSuccessful()) {
                        throw task.getException();
                  }
                  return rf.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                  Uri downloadUri = task.getResult();
                  String user_Pic = downloadUri.toString();

                  saveInfoDB(user_Pic, name, email, mobile);

            }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show());


      }

      private void saveInfoDB(String user_pic , String name , String email , String mobile) {
            if (profile.equals("Users")) {

                  UserProfileModel userModel = new UserProfileModel(user_pic, name, email);

                  databaseReference.child("Users").child(getUID()).setValue(userModel);

            } else {

                  ProfileModel profileModel = new ProfileModel(user_pic , name , email , mobile);

                  databaseReference.child("Admin").child(getUID()).setValue(profileModel);

            }
      }

      private void saveInfoDB(String name, String email, String mobile) {
            if (profile.equals("Users")) {

                  UserProfileModel userModel = new UserProfileModel(name, email);

                  databaseReference.child("Users").child(getUID()).setValue(userModel);

            } else {

                  ProfileModel profileModel = new ProfileModel(name, email, mobile);

                  databaseReference.child("Admin").child(getUID()).setValue(profileModel);

            }
      }

      public void onActivityResult(int requestCode , int resultCode , Intent data) {
            super.onActivityResult(requestCode , resultCode , data);

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                  CropImage.ActivityResult result = CropImage.getActivityResult(data);
                  if (resultCode == Activity.RESULT_OK) {
                        if (result != null) {
                              add_pic = result.getUri();

                              Picasso.get()
                                      .load(add_pic)
                                      .placeholder(R.drawable.ic_user)
                                      .error(R.drawable.ic_user)
                                      .into(profile_pic);
                              updateInfo(name,mobile,email,add_pic);
                              returnData();
                        }
                  } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();

                  }

            }
      }

      @Override
      protected void onStart() {
            super.onStart();
            returnData();
      }

      @Override
      protected void onResume() {
            super.onResume();
            returnData();
      }


}
