package mahmud.osman.trend.admin.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.victor.loading.rotate.RotateLoading;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;

public class CreateNews extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

      private String selected_type;
      private int type_id;
      private String exist_image;
      private String KEY, TYPE;
      @NotEmpty
      private EditText title, subject, writer;
      private Spinner type_spinner;
      private ImageView news_image;
      private RotateLoading rotateLoading;
      private Button share_btn, cancel_btn;
      private DatePickerDialog datePickerDialog;

      private FirebaseAuth mAuth;
      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;
      private StorageReference storageReference;
      private Uri add_pic = null;
      @NotEmpty
      private TextView date;
      private Object selected_date;
      private Validator validator;
      private Boolean isValid = false;
      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.create_news_activity);

            Bundle extra = getIntent().getExtras();

            assert extra != null;
            KEY = extra.getString("edit");
            TYPE = extra.getString("type");

            title = findViewById(R.id.titel_news);
            subject = findViewById(R.id.subject_filed);
            writer = findViewById(R.id.ed_writer);
            date = findViewById(R.id.date_pick);
            type_spinner = findViewById(R.id.chose_type);
            news_image = findViewById(R.id.image_news);
            share_btn = findViewById(R.id.add_btn);
            cancel_btn = findViewById(R.id.cancel_btn);
            rotateLoading = findViewById(R.id.rl_trend);

            //firebase
            mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            storageReference = FirebaseStorage.getInstance().getReference().child("NewsPic");

            validator = new Validator(this);
            validator.setValidationListener(this);
            cancel_btn.setOnClickListener(this);

            news_image.setOnClickListener(this);

            date.setOnClickListener(this);


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
            datePickerDialog = new DatePickerDialog(CreateNews.this ,
                    new DatePickerDialog.OnDateSetListener() {
                          @Override
                          public void onDateSet(DatePicker view , int year , int month , int dayOfMonth) {

                                selected_date = fieldToTimestamp(year,month,dayOfMonth);
                                date.setText(timestampToDateString((long) selected_date));

                          }
                    } , year , month , day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
      }


      private void publishPost() {


            if (KEY.equals("create")) {
                  share_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                              rotateLoading.start();
                              String title_text = title.getText().toString();
                              String subject_text = subject.getText().toString();
                              String writer_name = writer.getText().toString();
                              validator.validate();
                              if (!isValid) {
                                    return;
                              }
                              if (selected_type.isEmpty() | selected_type.equals(getString(R.string.select_type))) {
                                    Toast.makeText(getApplicationContext() , getString(R.string.select_type) , Toast.LENGTH_SHORT).show();
                                    return;
                              }
                              if (add_pic == null) {
                                    Toast.makeText(getApplicationContext() , "pleas add News Photo...!" , Toast.LENGTH_SHORT).show();
                                    return;
                              }
                              rotateLoading.start();
                              addNewsPicDB(title_text , subject_text , selected_date , writer_name , selected_type,type_id);


                        }
                  });

            } else {
                  share_btn.setText(getString(R.string.save_edit));
                  returnData(KEY , TYPE);
                  share_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                              String title_text = title.getText().toString();
                              String subject_text = subject.getText().toString();
                              String writer_name = writer.getText().toString();
                              validator.validate();
                              if (!isValid) {
                                    return;
                              }
                              if (selected_type.isEmpty() | selected_type.equals(getString(R.string.select_type))) {
                                    Toast.makeText(getApplicationContext() , getString(R.string.select_type) , Toast.LENGTH_SHORT).show();
                                    return;

                              }
                              if (add_pic == null) {

                                    updateNewsDB(exist_image , title_text , subject_text , selected_date , writer_name , selected_type,type_id);


                              } else {

                                    updateNewsPicDB(title_text , subject_text , selected_date , writer_name , selected_type,type_id);
                              }
                        }
                  });

            }

      }

      private void updateNewsPicDB(final String title_text, final String subject_text, final Object date_text, final String writer_name, final String selected_type, final int type_id) {

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

                        updateNewsDB(news_Pic , title_text , subject_text , date_text , writer_name , selected_type, type_id);

                  }
            }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext() , "Can't Upload Photo" , Toast.LENGTH_SHORT).show();
                        rotateLoading.stop();

                  }
            });

      }

      private void updateNewsDB(String exist_image, String title_text, String subject_text, Object date_text, String writer_name, String selected_type, int type_id) {
            rotateLoading.start();

            NewsModel newsModel = new NewsModel(exist_image , title_text , subject_text , date_text , writer_name , type_id);

            databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child(selected_type).child(KEY).setValue(newsModel);
            databaseReference.child(getString(R.string.User_news)).child(selected_type).child(KEY).setValue(newsModel);
            databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child("ترندات").child(KEY).setValue(newsModel);
            databaseReference.child(getString(R.string.User_news)).child("ترندات").child(KEY).setValue(newsModel);

            rotateLoading.stop();
            onBackPressed();

      }

      private void addNewsPicDB(final String title, final String subject, final Object date, final String writer, final String selected_type, final int type_id) {

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

                        addNewsDB(news_Pic , title , subject , date , writer , selected_type,type_id);

                  }
            }).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext() , "Can't Upload Photo" , Toast.LENGTH_SHORT).show();
                        rotateLoading.stop();

                  }
            });

      }

      private void addNewsDB(String news_pic, String title, String subject, Object date, String writer, String selected_type, int type_id) {
            rotateLoading.start();

            NewsModel newsModel = new NewsModel(news_pic , title , subject , date , writer , type_id);
            String key = databaseReference.child(getString(R.string.Admin_news)).push().getKey();

            databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child(selected_type).child(key).setValue(newsModel);
            databaseReference.child(getString(R.string.User_news)).child(selected_type).child(key).setValue(newsModel);
            databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child("ترندات").child(key).setValue(newsModel);
            databaseReference.child(getString(R.string.User_news)).child("ترندات").child(key).setValue(newsModel);

            rotateLoading.stop();

            onBackPressed();

      }


      public void selectSpinner() {


            type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent , View view , int position , long id) {

                        selected_type = type_spinner.getSelectedItem().toString();
                        type_id = type_spinner.getSelectedItemPosition();

                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) {

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
                                      .placeholder(R.drawable.ic_add_photo)
                                      .error(R.drawable.ic_add_photo)
                                      .into(news_image);
                        }
                  } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Exception error = result.getError();

                  }

            }
      }



      private void returnData(String key , String type) {

            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.keepSynced(true);
            mDatabase.child(getString(R.string.Admin_news))
                    .child(getUID())
                    .child(type)
                    .child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        NewsModel newsModel = dataSnapshot.getValue(NewsModel.class);

                        title.setText(newsModel.getTitle());
                        subject.setText(newsModel.getSubject());
                        selected_date = newsModel.getDate();
                        date.setText(timestampToDateString((long)newsModel.getDate()));
                        writer.setText(newsModel.getWriter());
                        type_spinner.setSelection(newsModel.getType());
                        exist_image = newsModel.getImage_uri();

                        Picasso.get().load(newsModel.getImage_uri())
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
            String id = mAuth.getCurrentUser().getUid();
            return id;
      }

      @Override
      public void onValidationSucceeded() {
            isValid = true;
      }

      @Override
      public void onValidationFailed(List<ValidationError> errors) {
            isValid = false;
            for (ValidationError error : errors) {
                  View view = error.getView();
                  String message = error.getCollatedErrorMessage(this);

                  // Display error messages ;)
                  if (view instanceof EditText) {
                        ((EditText) view).setError(message);
                  } else if (view instanceof TextView) {
                        ((TextView) view).setError(message);
                  }else {
                        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                  }
            }
      }
      private long fieldToTimestamp(int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            return  (calendar.getTimeInMillis() -1000);
      }
      public static String timestampToDateString(long timestamp){
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date(timestamp);
            return dateFormat.format(date);
      }

}
