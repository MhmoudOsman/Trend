package mahmud.osman.trend;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

import mahmud.osman.trend.Models.UserProfileModel;
import mahmud.osman.trend.admin.app.AdminActivity;
import mahmud.osman.trend.dialog.ReconnectDialog;
import mahmud.osman.trend.dialog.RegisterDialog;
import mahmud.osman.trend.presenters.adapter.TextInputLayoutAdapter;
import mahmud.osman.trend.user.app.UserActivity;

import static mahmud.osman.trend.utils.Utils.getUID;
import static mahmud.osman.trend.utils.Utils.isConnected;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener {

      @NotEmpty(messageResId = R.string.empty_email)
      @Email(messageResId = R.string.invalid_email)
      private TextInputLayout email;
      @NotEmpty(messageResId = R.string.empty_pass)
      private TextInputLayout pass;

      private Button login_btn, register_ben;
      private TextView join_to_us;


      private String email_text, pass_text, name_text;

      private Uri add_pic = null;

      private RotateLoading rotateLoading;
      //Firebase
      private FirebaseAuth mAuth;
      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;
      private StorageReference storageReference;

      private Validator validator;
      private boolean validated = false;

      private RegisterDialog dialog;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_activity);

            email = findViewById(R.id.email_filed);
            pass = findViewById(R.id.pass_filed);
            join_to_us = findViewById(R.id.join_to_us);
            login_btn = findViewById(R.id.login_btn);
            register_ben = findViewById(R.id.regiter_btn);

            rotateLoading = findViewById(R.id.rotateloading);

            mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            storageReference = FirebaseStorage.getInstance().getReference().child("user");

            validator = new Validator(this);
            validator.registerAdapter(TextInputLayout.class , new TextInputLayoutAdapter());
            validator.setViewValidatedAction(new Validator.ViewValidatedAction() {
                  @Override
                  public void onAllRulesPassed(View view) {
                        if (view instanceof TextInputLayout) {
                              ((TextInputLayout) view).setError("");
                              ((TextInputLayout) view).setErrorEnabled(false);
                        }
                  }
            });
            validator.setValidationListener(this);

            login_btn.setOnClickListener(v -> {
                  validator.validate();
                  if (validated) {
                        rotateLoading.start();
                        if (isConnected(this)) {
                              mLogin();
                        } else {
                              ReconnectDialog dialog = new ReconnectDialog(this);
                              dialog.getRetry().setOnClickListener(v1 -> {
                                    dialog.getError_massage().setVisibility(View.GONE);
                                    dialog.getReconnect_loading().start();

                                    if (isConnected(this)) {
                                          mLogin();
                                          dialog.dismiss();
                                    }else {
                                          dialog.getError_massage().setVisibility(View.VISIBLE);
                                          dialog.getReconnect_loading().stop();
                                    }
                              });
                              dialog.show();
                        }
                  }
            });

            register_ben.setOnClickListener(v -> openDialog());

            join_to_us.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,JoinToUsActivity.class)));

      }


      private void openDialog() {
            dialog = new RegisterDialog(this);
            dialog.create_btn.setOnClickListener(v -> {
                  dialog.getValidator().validate();
                  if (dialog.isValidated()) {
                        rotateLoading.start();
                        if (isConnected(this)) {
                              mRegister();
                        } else {
                              ReconnectDialog dialog = new ReconnectDialog(this);
                              dialog.getRetry().setOnClickListener(v1 -> {
                                    dialog.getError_massage().setVisibility(View.GONE);
                                    dialog.getReconnect_loading().start();

                                    if (isConnected(this)) {
                                          mRegister();
                                          dialog.dismiss();
                                    }else {
                                          dialog.getError_massage().setVisibility(View.VISIBLE);
                                          dialog.getReconnect_loading().stop();
                                    }
                              });
                              dialog.show();
                        }
                  }

            });

            dialog.cancel_btn.setOnClickListener(v -> dialog.dismiss());

            dialog.profile_pic.setOnClickListener(v -> CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .setAspectRatio(1080 , 1080)
                    .setAutoZoomEnabled(true)
                    .start(LoginActivity.this));

            dialog.show();
      }

      private void mRegister() {

            name_text = dialog.getUserName().getEditText().getText().toString();
            email_text = dialog.getEmail_reg().getEditText().getText().toString();
            pass_text = dialog.getPass_reg().getEditText().getText().toString();

            if (isConnected(this)) {
                  mAuth.fetchSignInMethodsForEmail(email_text).addOnCompleteListener(task -> {
                        boolean check = task.getResult().getSignInMethods().isEmpty();
                        if (check) {
                              mAuth.createUserWithEmailAndPassword(email_text, pass_text).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                          addUserPicDB(name_text, email_text);
                                    }
                              });
                        } else {
                              dialog.getEmail_reg().setError(getString(R.string.tacked_email));
                        }
                  });
            }

      }

      private void addUserPicDB(final String name , final String email) {
            rotateLoading.start();
            if (add_pic == null) {
                  addUserDB(new UserProfileModel(name, email));
            } else {
                  UploadTask uploadTask;
                  final StorageReference rf = storageReference.child("/UserPic" + add_pic.getLastPathSegment());

                  uploadTask = rf.putFile(add_pic);

                  uploadTask.continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                              throw task.getException();
                        }
                        return rf.getDownloadUrl();
                  }).addOnCompleteListener(task -> {
                        Uri downloadUri = task.getResult();
                        String user_Pic = downloadUri.toString();

                        addUserDB( new UserProfileModel(user_Pic, name, email));

                  }).addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                        rotateLoading.stop();
                  });

            }
      }

      private void addUserDB(UserProfileModel userProfileModel) {

            databaseReference.child("Users").child(getUID()).setValue(userProfileModel);
            updateUI(getUID());
            rotateLoading.stop();
      }

      private void mLogin() {

            email_text = email.getEditText().getText().toString();
            pass_text = pass.getEditText().getText().toString();

            if (isConnected(this)) {
                  mAuth.fetchSignInMethodsForEmail(email_text).addOnCompleteListener(task -> {
                        boolean check = task.getResult().getSignInMethods().isEmpty();
                        if (check) {
                              email.setError(getString(R.string.account_not_found));
                              rotateLoading.stop();
                        } else {
                              mAuth.signInWithEmailAndPassword(email_text, pass_text)
                                      .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {

                                                  updateUI(getUID());

                                            } else {
                                                  pass.setError(getString(R.string.wrong_pass));
                                                  rotateLoading.stop();
                                            }
                                      });

                        }
                  });
            }
      }

      private void updateUI(final String id) {

            databaseReference.child("Admin").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                          @Override
                          public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(id)) {
                                      Intent i = new Intent(getApplicationContext() , AdminActivity.class);
                                      startActivity(i);
                                      finish();
                                      rotateLoading.stop();
                                } else {
                                      databaseReference.child("Users").addListenerForSingleValueEvent(
                                              new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                          if (dataSnapshot.hasChild(id)) {
                                                                Intent i = new Intent(getApplicationContext() , UserActivity.class);
                                                                startActivity(i);
                                                                finish();
                                                                rotateLoading.stop();
                                                          }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                          Toast.makeText(getApplicationContext() , databaseError.getMessage() , Toast.LENGTH_SHORT).show();
                                                    }
                                              }
                                      );
                                }
                          }

                          @Override
                          public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext() , databaseError.getMessage() , Toast.LENGTH_SHORT).show();
                          }
                    });
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
                                      .into(dialog.profile_pic);

                        }
                  } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();
                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

                  }

            }
      }



      @Override
      public void onBackPressed() {
            moveTaskToBack(true);
      }

      @Override
      public void onValidationSucceeded() {
            validated = true;
      }

      @Override
      public void onValidationFailed(List<ValidationError> errors) {

            for (ValidationError error : errors) {
                  View view = error.getView();
                  String message = error.getCollatedErrorMessage(this);

                  if (view instanceof TextInputLayout) {
                        ((TextInputLayout) view).setError(message);
                  } else {
                        Toast.makeText(this , message , Toast.LENGTH_LONG).show();
                  }
            }
      }
}