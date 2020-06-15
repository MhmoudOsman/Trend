package mahmud.osman.trend.admin.app;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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

import java.util.Calendar;
import java.util.List;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;

import static mahmud.osman.trend.utils.Utils.fieldToTimestamp;
import static mahmud.osman.trend.utils.Utils.getUID;
import static mahmud.osman.trend.utils.Utils.timestampToDateString;

public class CreateNews extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

      private String selected_type;
      private int type_id;
      private String exist_image;
      private String KEY, TYPE;
      @NotEmpty
      private EditText title, writer;
      private EditText subject;
      private EditText youtube_link;

      private CardView link_card;
      private CheckBox yt_cb;
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
            youtube_link = findViewById(R.id.ed_youtube_link);
            yt_cb = findViewById(R.id.cb_video_link);
            link_card = findViewById(R.id.cv_youtube_link);
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
            linkYoutubeActive();
      }

      private void linkYoutubeActive() {
            yt_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                  if (isChecked) {
                        link_card.setVisibility(View.VISIBLE);
                        youtube_link.setEnabled(true);
                  } else {
                        link_card.setVisibility(View.GONE);
                        youtube_link.setEnabled(false);
                  }
            });
      }

      private void openDateDialog() {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            datePickerDialog = new DatePickerDialog(CreateNews.this ,
                    (view, year1, month1, dayOfMonth) -> {

                          selected_date = fieldToTimestamp(year1, month1, dayOfMonth);
                          date.setText(timestampToDateString((long) selected_date));

                    }, year , month , day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
      }

      private void publishPost() {

            if (KEY.equals("create")) {
                  share_btn.setOnClickListener(v -> {
                        rotateLoading.start();
                        String title_text = title.getText().toString();
                        String subject_text = subject.getText().toString();
                        String writer_name = writer.getText().toString();
                        String youtube_link_text = youtube_link.getText().toString();
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
                        addNewsPicDB(title_text, subject_text, selected_date, writer_name, selected_type, type_id, youtube_link_text);


                  });

            } else {
                  share_btn.setText(getString(R.string.save_edit));
                  returnData(KEY , TYPE);
                  share_btn.setOnClickListener(v -> {

                        String title_text = title.getText().toString();
                        String subject_text = subject.getText().toString();
                        String writer_name = writer.getText().toString();
                        String youtube_link_text = youtube_link.getText().toString();
                        validator.validate();
                        if (!isValid) {
                              return;
                        }
                        if (selected_type.isEmpty() | selected_type.equals(getString(R.string.select_type))) {
                              Toast.makeText(getApplicationContext(), getString(R.string.select_type), Toast.LENGTH_SHORT).show();
                              return;

                        }
                        if (add_pic == null) {

                              if (yt_cb.isChecked()) {
                                    if (TextUtils.isEmpty(youtube_link_text)) {
                                          youtube_link.setError("Most Add Link");
                                    } else {
                                          if (TextUtils.isEmpty(subject_text)) {
                                                NewsModel newsModel = new NewsModel(exist_image, title_text, writer_name, type_id, selected_date, youtube_link_text, yt_cb.isChecked());
                                                updateNewsDB(newsModel, selected_type);
                                          } else {
                                                NewsModel newsModel = new NewsModel(exist_image, title_text, subject_text, writer_name, type_id, selected_date, youtube_link_text, yt_cb.isChecked());
                                                updateNewsDB(newsModel, selected_type);
                                          }
                                    }
                              } else {
                                    if (TextUtils.isEmpty(subject_text)) {
                                          this.subject.setError("Most Add Subject");
                                    } else {
                                          NewsModel newsModel = new NewsModel(exist_image, title_text, subject_text, writer_name, type_id, selected_date, yt_cb.isChecked());
                                          updateNewsDB(newsModel, selected_type);
                                    }
                              }
                              rotateLoading.stop();

                        } else {
                              updateNewsPicDB(title_text, subject_text, selected_date, writer_name, selected_type, type_id, youtube_link_text);
                        }
                  });

            }

      }

      private void addNewsPicDB(final String title, final String subject, final Object date, final String writer
              , final String selected_type, final int type_id, String youtube_link_text) {

            rotateLoading.start();

            UploadTask uploadTask;

            final StorageReference rf = storageReference.child("/NewsPic" + add_pic.getLastPathSegment());

            uploadTask = rf.putFile(add_pic);

            Task<Uri> uriTask = uploadTask.continueWithTask(task -> {
                  if (!task.isSuccessful()) {
                        throw task.getException();
                  }
                  return rf.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                  Uri downloadUri = task.getResult();
                  String news_Pic = downloadUri.toString();
                  if (yt_cb.isChecked()) {
                        if (TextUtils.isEmpty(youtube_link_text)) {
                              youtube_link.setError("Most Add Link");
                        } else {
                              if (TextUtils.isEmpty(subject)) {
                                    NewsModel newsModel = new NewsModel(news_Pic, title, writer, type_id, date, youtube_link_text, yt_cb.isChecked());
                                    addNewsDB(newsModel, selected_type);
                              } else {
                                    NewsModel newsModel = new NewsModel(news_Pic, title, subject, writer, type_id, date, youtube_link_text, yt_cb.isChecked());
                                    addNewsDB(newsModel, selected_type);
                              }
                        }
                  } else {
                        if (TextUtils.isEmpty(subject)) {
                              this.subject.setError("Most Add Link");
                        } else {
                              NewsModel newsModel = new NewsModel(news_Pic, title, subject, writer, type_id, date, yt_cb.isChecked());
                              addNewsDB(newsModel, selected_type);
                        }
                  }
                  rotateLoading.stop();

            }).addOnFailureListener(e -> {
                  Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                  rotateLoading.stop();

            });

      }

      private void updateNewsPicDB(final String title_text, final String subject_text, final Object date_text, final String writer_name
              , final String selected_type, final int type_id, String youtube_link_text) {

            rotateLoading.start();

            UploadTask uploadTask;

            final StorageReference rf = storageReference.child("/NewsPic" + add_pic.getLastPathSegment());

            uploadTask = rf.putFile(add_pic);

            uploadTask.continueWithTask(task -> {
                  if (!task.isSuccessful()) {
                        throw task.getException();
                  }
                  return rf.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                  Uri downloadUri = task.getResult();
                  String news_Pic = downloadUri.toString();
                  if (yt_cb.isChecked()) {
                        if (TextUtils.isEmpty(youtube_link_text)) {
                              youtube_link.setError("Most Add Link");
                        } else {
                              if (TextUtils.isEmpty(subject_text)) {
                                    NewsModel newsModel = new NewsModel(news_Pic, title_text, writer_name, type_id, date_text, youtube_link_text, yt_cb.isChecked());
                                    updateNewsDB(newsModel, selected_type);
                              } else {
                                    NewsModel newsModel = new NewsModel(news_Pic, title_text, subject_text, writer_name, type_id, date_text, youtube_link_text, yt_cb.isChecked());
                                    updateNewsDB(newsModel, selected_type);
                              }
                        }
                  } else {
                        if (TextUtils.isEmpty(subject_text)) {
                              this.subject.setError("Most Add Subject");
                        } else {
                              NewsModel newsModel = new NewsModel(news_Pic, title_text, subject_text, writer_name, type_id, date_text, yt_cb.isChecked());
                              updateNewsDB(newsModel, selected_type);
                        }
                  }
                  rotateLoading.stop();

            }).addOnFailureListener(e -> {
                  Toast.makeText(getApplicationContext(), "Can't Upload Photo", Toast.LENGTH_SHORT).show();
                  rotateLoading.stop();

            });

      }

      private void addNewsDB(NewsModel newsModel, String selected_type) {
            String key = databaseReference.child(getString(R.string.Admin_news)).push().getKey();

            databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child(selected_type).child(key).setValue(newsModel);
            databaseReference.child(getString(R.string.User_news)).child(selected_type).child(key).setValue(newsModel);
            databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child("ترندات").child(key).setValue(newsModel);
            databaseReference.child(getString(R.string.User_news)).child("ترندات").child(key).setValue(newsModel);


            onBackPressed();

      }

      private void updateNewsDB(NewsModel newsModel, String selected_type) {

            databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child(selected_type).child(KEY).setValue(newsModel);
            databaseReference.child(getString(R.string.User_news)).child(selected_type).child(KEY).setValue(newsModel);
            databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child("ترندات").child(KEY).setValue(newsModel);
            databaseReference.child(getString(R.string.User_news)).child("ترندات").child(KEY).setValue(newsModel);

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
                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();

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
                        if (newsModel != null) {
                              if (!TextUtils.isEmpty(newsModel.getSubject())) {
                                    subject.setText(newsModel.getSubject());
                              }
                              if (!TextUtils.isEmpty(newsModel.getVideo_url())) {
                                    youtube_link.setText(newsModel.getVideo_url());
                              }
                        }
                        yt_cb.setChecked(newsModel.isBox_checked());
                        title.setText(newsModel.getTitle());
                        selected_date = newsModel.getDate();
                        date.setText(timestampToDateString((long) newsModel.getDate()));
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

}
