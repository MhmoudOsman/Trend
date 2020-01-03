package mahmud.osman.trend.admin.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import java.util.Calendar;

import mahmud.osman.trend.Models.AdminModel;
import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;

public class CreateNews extends AppCompatActivity implements View.OnClickListener {

    String selected_type;
    String exist_image;
    String writer_name;
    String KEY;
    private EditText title, supject;
    private Spinner type_spinner;
    private RoundedImageView news_image;
    private RotateLoading rotateLoading;
    private Button share_btn, cancel_btn;
    private DatePickerDialog datePickerDialog;

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri add_pic = null;
    private TextView date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_news_activity);


        Bundle extra = getIntent().getExtras();

        KEY = extra.getString("edit");


        title = findViewById(R.id.titel_news);
        supject = findViewById(R.id.subject_filed);
        date = findViewById(R.id.date_pick);
        type_spinner = findViewById(R.id.chose_type);
        news_image = findViewById(R.id.image_news);
        share_btn = findViewById(R.id.add_btn);
        cancel_btn = findViewById(R.id.cancel_btn);
        rotateLoading = findViewById(R.id.rotate_loding);

        //firebase
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("NewsPic");

        cancel_btn.setOnClickListener(CreateNews.this);

        news_image.setOnClickListener(CreateNews.this);

        date.setOnClickListener(CreateNews.this);


        publishPost();

        selectSpinner();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                onBackPressed();
                break;
            case R.id.image_news:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1920, 1080)
                        .setAutoZoomEnabled(true)
                        .start(CreateNews.this);
                break;
            case R.id.date_pick:
                openDateDialog();

        }
    }

    private void openDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(CreateNews.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        date.setText(dayOfMonth + " / " + month  + " / " + year);


                    }
                }, year, month, day);
        datePickerDialog.show();
    }


    private void publishPost() {


        if (KEY.equals("creat")) {
            reternData();
            share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String title_text = title.getText().toString();
                    String supject_text = supject.getText().toString();
                    String date_text = date.getText().toString();

                    if (selected_type.isEmpty() | selected_type.equals(getString(R.string.select_type))) {
                        Toast.makeText(getApplicationContext(), getString(R.string.select_type), Toast.LENGTH_SHORT).show();
                        rotateLoading.stop();
                        return;

                    }
                    if (add_pic == null) {
                        Toast.makeText(getApplicationContext(), "pleas add News Photo...!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        addNewaPicDB(title_text, supject_text, date_text, writer_name, selected_type);
                        rotateLoading.start();
                    }

                }
            });

        } else {
            share_btn.setText(getString(R.string.edit));
            reternData(KEY);
            share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String title_text = title.getText().toString();
                    String supject_text = supject.getText().toString();
                    String date_text = date.getText().toString();

                    if (selected_type.isEmpty() | selected_type.equals(getString(R.string.select_type))) {
                        Toast.makeText(getApplicationContext(), getString(R.string.select_type), Toast.LENGTH_SHORT).show();
                        return;

                    }
                    if (add_pic == null) {
                        Toast.makeText(getApplicationContext(), "pleas add News Photo...!", Toast.LENGTH_SHORT).show();

                        updateNewsDB(exist_image, title_text, supject_text, date_text, writer_name, selected_type);

                        rotateLoading.start();

                    } else {

                        updateNewaPicDB(title_text, supject_text, date_text, writer_name, selected_type);
                        rotateLoading.start();
                    }
                }
            });

        }

    }

    private void updateNewaPicDB(final String title_text, final String supject_text, final String date_text, final String writer_name, final String selected_type) {

        rotateLoading.start();

        UploadTask uploadTask;

        final StorageReference rf = storageReference.child("/NewsPic" + add_pic.getLastPathSegment());

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
                String news_Pic = downloadUri.toString();

                addNewsDB(news_Pic, title_text, supject_text, date_text, writer_name, selected_type);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                rotateLoading.stop();

            }
        });

    }

    private void updateNewsDB(String exist_image, String title_text, String supject_text, String date_text, String writer_name, String selected_type) {

        NewsModel newsModel = new NewsModel(exist_image, title_text, supject_text, date_text, writer_name, selected_type);

        databaseReference.child("Admin_news").child(getUID()).child(selected_type).child(KEY).setValue(newsModel);
        databaseReference.child("User_news").child(selected_type).child(KEY).setValue(newsModel);

        onBackPressed();

    }

    private void addNewaPicDB(final String titel, final String subject, final String date, final String writer, final String selected_type) {

        rotateLoading.start();

        UploadTask uploadTask;

        final StorageReference rf = storageReference.child("/NewsPic" + add_pic.getLastPathSegment());

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
                String news_Pic = downloadUri.toString();

                addNewsDB(news_Pic, titel, subject, date, writer, selected_type);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                rotateLoading.stop();

            }
        });

    }

    private void addNewsDB(String news_pic, String titel, String subject, String date, String writer, String selected_type) {

        NewsModel newsModel = new NewsModel(news_pic, titel, subject, date, writer, selected_type);
        String key = databaseReference.child("Admin_news").push().getKey();


        databaseReference.child("Admin_news").child(getUID()).child(selected_type).child(key).setValue(newsModel);
        databaseReference.child("User_news").child(selected_type).child(key).setValue(newsModel);

        onBackPressed();

    }


    public void selectSpinner() {

        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.type_of_news, android.R.layout.simple_spinner_dropdown_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_spinner.setAdapter(type_adapter);

        type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_type = String.valueOf(parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
                            .placeholder(R.drawable.ic_add_a_photo_black_48dp)
                            .error(R.drawable.ic_add_a_photo_black_48dp)
                            .into(news_image);
                }
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

            }

        }
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

                        writer_name = adminModel.getName();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(getApplicationContext(), "can\'t fetch data", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void reternData(String key) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);
        mDatabase.child("Admin_news")
                .child(getUID())
                .child(getString(R.string.interview))
                .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);

                title.setText(newsModel.getTitl());
                supject.setText(newsModel.getSubject());
                date.setText(newsModel.getDate());
                selected_type = newsModel.getType();
                exist_image = newsModel.getImage_uri();
                writer_name = newsModel.getWriter();
                Picasso.get().load(newsModel.getImage_uri())
                        .placeholder(R.drawable.newspaper)
                        .error(R.drawable.newspaper)
                        .into(news_image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private String getUID() {
        String id = mAuth.getCurrentUser().getUid();
        return id;
    }

    @Override
    public void onBackPressed() {
        CreateNews.super.onBackPressed();
        return;
    }

}
