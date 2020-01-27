package mahmud.osman.trend;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
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
import com.victor.loading.rotate.RotateLoading;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import mahmud.osman.trend.Models.UserModel;
import mahmud.osman.trend.admin.app.AdminActivity;
import mahmud.osman.trend.user.app.UserActivity;

public class LoginActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=.*[0-9])" +         //at least 1 digit
            //"(?=.*[a-z])" +         //at least 1 lower case letter
            //"(?=.*[A-Z])" +         //at least 1 upper case letter
            "(?=.*[a-zA-Z])" +      //any letter
            // "(?=.*[@#$%^&+=])" +    //at least 1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{8,}" +               //at least 4 characters
            "$");


    private TextInputLayout email, pass;
    private Button login_btn, register_ben;
    private TextView join_to_us;

    //Dialog
    private Button cancel_btn, create_btn;
    private TextInputLayout name, email_reg, pass_reg, confrem_pass;
    private CircleImageView profil_pic;
    private Uri add_pic = null;

    private RotateLoading rotateLoding;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        email = findViewById(R.id.email_filed);
        pass = findViewById(R.id.pass_filed);
        join_to_us = findViewById(R.id.join_to_us);
        login_btn = findViewById(R.id.login_btn);
        register_ben = findViewById(R.id.regiter_btn);

        rotateLoding = findViewById(R.id.rotateloading);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("user");


        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLogin();
                rotateLoding.start();
            }
        });

        register_ben.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        autoValidate();
    }

    private void autoValidate() {
        email.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateEmail(email);
            }
        });
        pass.getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validatePassword(pass);
            }
        });

        join_to_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), JoinToUsActivity.class);
                startActivity(i);
            }
        });

    }

    private void openDialog() {

        final Dialog dialog = new Dialog(LoginActivity.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.register_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes();
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        profil_pic = dialog.findViewById(R.id.pro_pic);

        cancel_btn = dialog.findViewById(R.id.cancel_btn);
        create_btn = dialog.findViewById(R.id.creat_btn);

        name = dialog.findViewById(R.id.name_filed);
        email_reg = dialog.findViewById(R.id.email_filed_r);
        pass_reg = dialog.findViewById(R.id.pass_filed_r);
        confrem_pass = dialog.findViewById(R.id.confrim_pass_filed_r);

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRegister();
                rotateLoding.start();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        profil_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1080, 1080)
                        .setAutoZoomEnabled(true)
                        .start(LoginActivity.this);

            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void mRegister() {
        rotateLoding.start();
        if (!validateEmail(email_reg) | !validatePassword(pass_reg) | !validateCPassword() | !validateName()) {
            rotateLoding.stop();
            return;
        }

        final String name_text = name.getEditText().getText().toString();
        final String email_reg_text = email_reg.getEditText().getText().toString();
        final String pass_reg_text = pass_reg.getEditText().getText().toString();

        mAuth.fetchProvidersForEmail(email_reg_text).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                boolean check = task.getResult().getProviders().isEmpty();

                if (check) {

                    mAuth.createUserWithEmailAndPassword(email_reg_text, pass_reg_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                addUserPicDB(name_text, email_reg_text);

                            }
                        }
                    });

                } else {
                    email_reg.setError(getString(R.string.tacken_email));
                }


            }
        });

    }

    private void addUserPicDB(final String name, final String email) {

        rotateLoding.start();

        UploadTask uploadTask;


        if (add_pic == null) {
            add_pic = Uri.parse("android.resource://mahmud.osman.trend/drawable/ic_user");
        }
        final StorageReference rf = storageReference.child("/UserPic" + add_pic.getLastPathSegment());

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

                addUserDB(user_Pic, name, email);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                rotateLoding.stop();
            }
        });

    }

    private void addUserDB(String user_Pic, String name, String email) {

        rotateLoding.start();

        UserModel userModel = new UserModel(user_Pic, name, email);

        databaseReference.child("Users").child(getUID()).setValue(userModel);

        updateUserUI();
        rotateLoding.stop();

    }

    private void mLogin() {
        if (!validateEmail(email) | !validatePassword(pass)) {
            rotateLoding.stop();
            return;
        }
        String email_text, pass_text;
        email_text = email.getEditText().getText().toString();
        pass_text = pass.getEditText().getText().toString();

        userLogin(email_text, pass_text);
    }

    private void userLogin(final String s_email, String password) {

        mAuth.signInWithEmailAndPassword(s_email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                                            updateAdminUI();
                                            rotateLoding.stop();
                                        } else {
                                            databaseReference.child("Users").addListenerForSingleValueEvent(
                                                    new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.hasChild(id)) {
                                                                updateUserUI();
                                                                rotateLoding.stop();
                                                            }else{
                                                                pass.setError("Password is Wrong");
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
                    }

                });
    }

    private void updateAdminUI() {

        Intent i = new Intent(getApplicationContext(), AdminActivity.class);
        startActivity(i);
        finish();

    }

    private void updateUserUI() {

        Intent i = new Intent(getApplicationContext(), UserActivity.class);
        startActivity(i);
        finish();

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

    private boolean validateEmail(TextInputLayout email) {

        String emailtext = email.getEditText().getText().toString();

        if (emailtext.isEmpty()) {
            email.setError(getString(R.string.empty_email));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailtext).matches()) {
            email.setError(getString(R.string.valid_email));
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatePassword(TextInputLayout pass) {

        String passtext = pass.getEditText().getText().toString();

        if (passtext.isEmpty()) {
            pass.setError(getString(R.string.empty_pass));
            return false;
        } else if (passtext.length() < 8) {
            pass.setError(getString(R.string.less_than_8));
            return false;
        } else if (!PASSWORD_PATTERN.matcher(passtext).matches()) {
            pass.setError(getString(R.string.must_be_mixed));
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    private boolean validateName() {

        String name_text = name.getEditText().getText().toString();

        if (name_text.isEmpty()) {
            name.setError(getString(R.string.empty_name));
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    private boolean validateCPassword() {

        String pass_text = pass_reg.getEditText().getText().toString();
        String conf_passtext = confrem_pass.getEditText().getText().toString();
        if (conf_passtext.isEmpty()) {
            confrem_pass.setError(getString(R.string.empty_conf_pass));
            return false;
        } else if (!conf_passtext.equals(pass_text)) {
            confrem_pass.setError(getString(R.string.conf_pass_not_matched));
            return false;
        } else {
            confrem_pass.setError(null);
            return true;
        }
    }

    private String getUID() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return id;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
