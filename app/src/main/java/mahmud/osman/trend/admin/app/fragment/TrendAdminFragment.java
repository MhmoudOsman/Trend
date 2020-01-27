package mahmud.osman.trend.admin.app.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.victor.loading.rotate.RotateLoading;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.CreateNews;
import mahmud.osman.trend.admin.app.AdminNewsActivity;
import mahmud.osman.trend.presenters.holders.NewsHolders;


public class TrendAdminFragment extends Fragment {
    View view;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button d_cancel,d_ok;

    String kay;



    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<NewsModel, NewsHolders> firebaseRecyclerAdapter;

    private RotateLoading rotateLoading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trend_admin , container, false);

          recyclerView = view.findViewById(R.id.rv_trend);
          rotateLoading = view.findViewById(R.id.rl_trend);

          return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rotateLoading.start();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);





        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);


        displayInterviewNews();



    }

    private void displayInterviewNews() {


        Query query = databaseReference
                .child(getString(R.string.Admin_news))
                .child(getUID())
                .child(getString(R.string.trends))
                .limitToLast(50);

        FirebaseRecyclerOptions<NewsModel> options =
                new FirebaseRecyclerOptions.Builder<NewsModel>()
                        .setQuery(query, NewsModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewsModel, NewsHolders>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NewsHolders newsholder, final int position, @NonNull final NewsModel newsModel) {


                Picasso.get()
                        .load(newsModel.getImage_uri())
                        .placeholder(R.drawable.newspaper)
                        .error(R.drawable.newspaper)
                        .into(newsholder.item_image);

                newsholder.title.setText(newsModel.getTitl());
                newsholder.subject.setText(newsModel.getSubject());
                newsholder.writer.setText(newsModel.getWriter());
                newsholder.date.setText(newsModel.getDate());

                newsholder.card_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        kay = getRef(position).getKey();
                        Intent i = new Intent(getContext(), AdminNewsActivity.class);
                        i.putExtra("open",kay);
                        i.putExtra("type",getString(R.string.trends));
                        startActivity(i);
                    }
                });
                newsholder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        kay = getRef(position).getKey();
                        PopupMenu popupMenu = new PopupMenu(getContext(), newsholder.more);

                        popupMenu.getMenuInflater().inflate(R.menu.more_item, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.edit:
                                        Intent intent = new Intent(getContext(), CreateNews.class);
                                        intent.putExtra("edit",kay);
                                        intent.putExtra("type",getString(R.string.trends));
                                        startActivity(intent);
                                        return true;
                                    case R.id.delete:
                                        showDeleteDialog(kay);
                                        return true;

                                }
                                return true;
                            }
                        });
                        popupMenu.show();

                    }
                });


            }

            @NonNull
            @Override
            public NewsHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                return new NewsHolders(view);

            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        rotateLoading.stop();
    }

    private void showDeleteDialog(final String kay) {
        final Dialog delete_dialog = new Dialog(getContext());
        delete_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        delete_dialog.setContentView(R.layout.delete_dialog);
        delete_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        delete_dialog.getWindow().getAttributes();
        delete_dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(delete_dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        d_cancel = delete_dialog.findViewById(R.id.d_cancel_btn);
        d_ok = delete_dialog.findViewById(R.id.d_yes_btn);

        d_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(getString(R.string.Admin_news)).child(getUID()).child(getString(R.string.trends)).child(kay).removeValue();
                databaseReference.child(getString(R.string.User_news)).child(getString(R.string.trends)).child(kay).removeValue();
                delete_dialog.dismiss();
            }
        });
        d_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_dialog.dismiss();
            }
        });
        delete_dialog.show();
        delete_dialog.getWindow().setAttributes(lp);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }

    }

    private String getUID() {
        String id = mAuth.getCurrentUser().getUid();
        return id;
    }

}
