package mahmud.osman.trend.admin.app.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.admin.app.CreateNews;
import mahmud.osman.trend.admin.app.NewsItemActivity;
import mahmud.osman.trend.presenters.holders.NewsHolders;


public class InterviewAdmenFragment extends Fragment {
    View view;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String kay;



    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<NewsModel, NewsHolders> firebaseRecyclerAdapter;


    RotateLoading rotateLoading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.interview_admen_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        databaseReference.keepSynced(true);


        recyclerView = view.findViewById(R.id.recycle_news);
        rotateLoading = view.findViewById(R.id.rotate_loding);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        displayInterviewNews();
        rotateLoading.start();


    }

    private void displayInterviewNews() {
        Query query = databaseReference
                .child("Admin_news")
                .child(getUID())
                .child(getString(R.string.interview))
                .limitToLast(50);

        FirebaseRecyclerOptions<NewsModel> options =
                new FirebaseRecyclerOptions.Builder<NewsModel>()
                        .setQuery(query, NewsModel.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<NewsModel, NewsHolders>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NewsHolders newsholder, int position, @NonNull final NewsModel newsModel) {
                kay = getRef(position).getKey();

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
                        Intent i = new Intent(getContext(), NewsItemActivity.class);
                        i.putExtra("open",kay);
                        startActivity(i);
                    }
                });
                newsholder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(getContext(), newsholder.more);

                        popupMenu.getMenuInflater().inflate(R.menu.more_item, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.edit:
                                        Intent intent = new Intent(getContext(), CreateNews.class);
                                        intent.putExtra("edit",kay);
                                        startActivity(intent);
                                        return true;
                                    case R.id.delete:
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
