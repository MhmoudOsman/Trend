package mahmud.osman.trend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import mahmud.osman.trend.Models.AdminModel;
import mahmud.osman.trend.admin.app.AdminActivity;

public class JoinToUsActivity extends AppCompatActivity {
    CircleImageView profil_pic;
    TextInputLayout first_name, last_name, email, pass, confrim_Pass, code, mobile;
    Button join_btn;
    Uri add_pic ;
    RotateLoading rotateLoading ;



    //Validate Password Pattern
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            //"(?=.*[a-z])" +         //at least 1 lower case letter
            //"(?=.*[A-Z])" +         //at least 1 upper case letter
            "(?=.*[a-zA-Z])" +      //any letter
            // "(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{8,}" +               //at least 4 characters
            "$");

    private static final Pattern MOBILE_PATTERN = Pattern.compile("(01)[0-9]{9}");
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_to_us_activity);
        Toolbar toolbar = findViewById(R.id.toolbar_join);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_arrow_left);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        rotateLoading = findViewById(R.id.rl_trend);

        profil_pic = findViewById(R.id.profile_image);
        first_name = findViewById(R.id.first_name_filed);
        last_name = findViewById(R.id.last_name_filed);
        email = findViewById(R.id.email_filed);
        pass = findViewById(R.id.pass_filed);
        confrim_Pass = findViewById(R.id.confrim_pass_filed);
        code = findViewById(R.id.code_filed);
        mobile = findViewById(R.id.mobile_filed);

        join_btn = findViewById(R.id.join_btn);


        profil_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1080, 1080)
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
                mJoin();
                rotateLoading.start();
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
        if (!validateEmail() | !validatePassword() | !validateCPassword() | !validateName(first_name) | !validateName(last_name) | !validateCode() | !validateMobile()) {
            rotateLoading.stop();
            return;
        }


        String first_text = first_name.getEditText().getText().toString();
        String last_text = last_name.getEditText().getText().toString();
        final String name_text = first_text + " " + last_text;
        final String email_text = email.getEditText().getText().toString();
        final String pass_text = pass.getEditText().getText().toString();
        final String mobile_text = mobile.getEditText().getText().toString();


        mAuth.fetchProvidersForEmail(email_text).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                boolean check = task.getResult().getProviders().isEmpty();

                if (check) {

                    mAuth.createUserWithEmailAndPassword(email_text, pass_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                addUserPicDB(name_text, email_text, mobile_text);

                            }
                        }
                    });

                } else {

                    email.setError("Email is Already Tacken");
                    rotateLoading.stop();
                }


            }
        });

    }

    private void addUserPicDB(final String name, final String email, final String mobile) {

        rotateLoading.start();

        UploadTask uploadTask;

        if (add_pic == null) {
            add_pic = Uri.parse("android.resource://mahmud.osman.trend/drawable/ic_user");
        }

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

    private void addUserDB(String user_Pic, String name, String email,String mobile) {
        rotateLoading.start();

        AdminModel adminModel = new AdminModel(user_Pic, name, email, mobile);

        databaseReference.child("Admin").child(getUID()).setValue(adminModel);

        rotateLoading.stop();

        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
        startActivity(intent);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                if (result != null) {
                    add_pic = result.getUri();

                    Picasso.get()
                            .load(add_pic)
                            .placeholder(R.drawable.ic_user)
                            .error(R.drawable.ic_user)
                            .into(profil_pic);
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }

        }
    }


    private boolean validateName(TextInputLayout name) {

        String first_name_text = name.getEditText().getText().toString();

        if (first_name_text.isEmpty()) {
            name.setError(getString(R.string.empty_first_name));
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {

        String email_text = email.getEditText().getText().toString();

        if (email_text.isEmpty()) {
            email.setError(getString(R.string.empty_email));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
            email.setError(getString(R.string.valid_email));
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validateCPassword() {

        String pass_text = pass.getEditText().getText().toString();
        String conf_passtext = confrim_Pass.getEditText().getText().toString();
        if (conf_passtext.isEmpty()) {
            confrim_Pass.setError(getString(R.string.empty_conf_pass));
            return false;
        } else if (!conf_passtext.equals(pass_text)) {
            confrim_Pass.setError(getString(R.string.conf_pass_not_matched));
            return false;
        } else {
            confrim_Pass.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {

        String pass_text = pass.getEditText().getText().toString();
        if (pass_text.isEmpty()) {
            pass.setError(getString(R.string.empty_pass));
            return false;
        } else if (pass_text.length() < 8) {
            pass.setError(getString(R.string.less_than_8));
            return false;
        } else if (!PASSWORD_PATTERN.matcher(pass_text).matches()) {
            pass.setError(getString(R.string.must_be_mixed));
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    private boolean validateCode() {

        String name_code = first_name.getEditText().getText().toString();
        String code_text = code.getEditText().getText().toString();
        if (code_text.isEmpty()) {
            code.setError(getString(R.string.empty_code));
            return false;
        } else if (!code_text.equals(name_code+"trend")) {
            code.setError(getString(R.string.code_not_match));
            return false;
        } else {
            code.setError(null);
            return true;
        }
    }

    private boolean validateMobile() {

        String mobile_text = mobile.getEditText().getText().toString();

        if (mobile_text.isEmpty()) {
            mobile.setError(getString(R.string.empty_mobile));
            return false;
        } else if (mobile_text.length() != 11) {

            mobile.setError(getString(R.string.valid_mobile));
            return false;

        } else if (!MOBILE_PATTERN.matcher(mobile_text).matches()) {
            mobile.setError(getString(R.string.valid_mobile));
            return false;
        } else {
            mobile.setError(null);
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

}
