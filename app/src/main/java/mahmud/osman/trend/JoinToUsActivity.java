package mahmud.osman.trend;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.mobsandgeeks.saripaar.annotation.Pattern;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mahmud.osman.trend.Models.ProfileModel;
import mahmud.osman.trend.admin.app.AdminActivity;
import mahmud.osman.trend.presenters.adapter.TextInputLayoutAdapter;

public class JoinToUsActivity extends AppCompatActivity implements Validator.ValidationListener {

      private CircleImageView profile_pic;

      @NotEmpty(messageResId = R.string.empty_first_name)
      @Pattern(regex = "[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z ]+[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z-_ ]", messageResId = R.string.invalid_name)
      private TextInputLayout first_name;

      @NotEmpty(messageResId = R.string.empty_last_name)
      @Pattern(regex = "[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z ]+[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z-_ ]", messageResId = R.string.invalid_name)
      private TextInputLayout last_name;

      @NotEmpty(messageResId = R.string.empty_email)
      @Pattern(regex ="[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+" ,messageResId = R.string.invalid_email)
      private TextInputLayout email;

      @NotEmpty(messageResId = R.string.empty_pass)
      @Password(min = 8, scheme = Password.Scheme.ALPHA_NUMERIC, messageResId = R.string.invalid_pass)
      private TextInputLayout pass;

      @NotEmpty(messageResId = R.string.empty_conf_pass)
      @ConfirmPassword(messageResId = R.string.conf_pass_not_matched)
      private TextInputLayout confirm_Pass;

      @NotEmpty(messageResId = R.string.empty_mobile)
      @Pattern(regex = "(010|011|012|015)[0-9]{8}", messageResId = R.string.invalid_mobile)
      private TextInputLayout mobile;

      private TextInputLayout code;
      private Button join_btn;
      private RotateLoading rotateLoading;
      private Toolbar toolbar;

      private FirebaseAuth mAuth;
      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;
      private StorageReference storageReference;

      private Validator validator;
      private boolean validated=false;

     private Uri add_pic;

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.join_to_us_activity);

            toolbar = findViewById(R.id.toolbar_join);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
            getSupportActionBar().setDisplayUseLogoEnabled(true);

            rotateLoading = findViewById(R.id.rl_join);
            profile_pic = findViewById(R.id.profile_image);
            first_name = findViewById(R.id.first_name_filed);
            last_name = findViewById(R.id.last_name_filed);
            email = findViewById(R.id.email_filed);
            pass = findViewById(R.id.pass_filed);
            confirm_Pass = findViewById(R.id.confrim_pass_filed);
            code = findViewById(R.id.code_filed);
            mobile = findViewById(R.id.mobile_filed);
            join_btn = findViewById(R.id.join_btn);


            validator = new Validator(this);
            validator.registerAdapter(TextInputLayout.class , new TextInputLayoutAdapter());
            validator.setValidationListener(this);
            validator.setViewValidatedAction(new Validator.ViewValidatedAction() {
                  @Override
                  public void onAllRulesPassed(View view) {
                        if (view instanceof TextInputLayout){
                              ((TextInputLayout) view).setError("");
                              ((TextInputLayout) view).setErrorEnabled(false);
                        }
                  }
            });

            profile_pic.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {

                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                                .setAspectRatio(1080 , 1080)
                                .setAutoZoomEnabled(true)
                                .start(JoinToUsActivity.this);

                  }
            });


            //firebase
            mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            storageReference = FirebaseStorage.getInstance().getReference().child("admin");

            join_btn.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        validator.validate();
                        if (validated & validateCode()){
                        mJoin();
                        rotateLoading.start();
                        }
                  }
            });
      }

      public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                  case android.R.id.home:
                        onBackPressed();
                        return true;

                  default:
                        return super.onOptionsItemSelected(item);
            }
      }


      private void mJoin() {


            String first_text = first_name.getEditText().getText().toString();
            String last_text = last_name.getEditText().getText().toString();
            final String name_text = first_text + " " + last_text;
            final String email_text = email.getEditText().getText().toString();
            final String pass_text = pass.getEditText().getText().toString();
            final String mobile_text = mobile.getEditText().getText().toString();


            mAuth.fetchSignInMethodsForEmail(email_text).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                  @Override
                  public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                        boolean check = false;
                        try {
                              check = task.getResult().getSignInMethods().isEmpty();
                        }catch (Exception e){
                              Toast.makeText(JoinToUsActivity.this, "Check Internet Connection", Toast.LENGTH_LONG).show();
                              rotateLoading.stop();
                              return;
                        }                        if (check) {

                              mAuth.createUserWithEmailAndPassword(email_text , pass_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                          if (task.isSuccessful()) {

                                                addUserPicDB(name_text , email_text , mobile_text);

                                          }
                                    }
                              });

                        } else {

                              email.setError(getString(R.string.tacked_email));
                              rotateLoading.stop();
                        }


                  }
            });

      }

      private void addUserPicDB(final String name , final String email , final String mobile) {

            rotateLoading.start();

            if (add_pic == null) {
                  addUserDB(name, email, mobile);
            } else {
                  UploadTask uploadTask;

                  final StorageReference rf = storageReference.child("/AdminPic" + add_pic.getLastPathSegment());

                  uploadTask = rf.putFile(add_pic);

                  Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                              if (!task.isSuccessful()) {
                                    throw task.getException();
                              }
                              return rf.getDownloadUrl();
                        }
                  }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                              Uri downloadUri = task.getResult();
                              String user_Pic = downloadUri.toString();

                              addUserDB(user_Pic, name, email, mobile);

                        }
                  }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                              Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                              rotateLoading.stop();

                        }
                  });

            }
      }

      private void addUserDB(String name, String email, String mobile) {
            rotateLoading.start();

            ProfileModel profileModel = new ProfileModel(name, email, mobile);

            databaseReference.child("Admin").child(getUID()).setValue(profileModel);

            rotateLoading.stop();

            Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
            startActivity(intent);

      }

      private void addUserDB(String user_Pic , String name , String email , String mobile) {
            rotateLoading.start();

            ProfileModel profileModel = new ProfileModel(user_Pic , name , email , mobile);

            databaseReference.child("Admin").child(getUID()).setValue(profileModel);

            rotateLoading.stop();

            Intent intent = new Intent(getApplicationContext() , AdminActivity.class);
            startActivity(intent);

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
                        }
                  } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();

                  }

            }
      }

      private boolean validateCode() {

            String name_code = first_name.getEditText().getText().toString();
            String code_text = code.getEditText().getText().toString();
            if (code_text.isEmpty()) {
                  code.setError(getString(R.string.empty_code));
                  return false;
            } else if (!code_text.equals(name_code + "trend")) {
                  code.setError(getString(R.string.code_not_match));
                  return false;
            } else {
                  code.setError(null);
                  return true;
            }
      }

      private String getUID() {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            return id;
      }

      @Override
      public void onBackPressed() {
            JoinToUsActivity.super.onBackPressed();
            return;
      }

      @Override
      public void onValidationSucceeded() {
            validated = true;
      }

      @Override
      public void onValidationFailed(List<ValidationError> errors) {
            validated = false;
            for (ValidationError error : errors){
                  View view = error.getView();
                  String message = error.getCollatedErrorMessage(this);

                  if (view instanceof TextInputLayout){
                        ((TextInputLayout) view).setError(message);
                  }else{
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                  }
            }

      }
}
