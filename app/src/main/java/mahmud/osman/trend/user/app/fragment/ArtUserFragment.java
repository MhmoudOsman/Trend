package mahmud.osman.trend.user.app.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.presenters.adapter.NewsAdaptor;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtUserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

      View view;

      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;

      private RecyclerView recyclerView;
      private NewsAdaptor newsAdaptor;
      private SwipeRefreshLayout refreshLayout;

      @Override
      public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                               Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_art_user , container , false);
            recyclerView = view.findViewById(R.id.rv_art_user);
            refreshLayout = view.findViewById(R.id.srl_art_user);
            return view;
      }

      @Override
      public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            databaseReference.keepSynced(true);

            recyclerView.setNestedScrollingEnabled(false);

            refreshLayout.setColorSchemeResources(R.color.gold);
            refreshLayout.setProgressBackgroundColorSchemeResource(R.color.background);
            refreshLayout.setOnRefreshListener(this);

            displayNews();

      }

      private void displayNews() {
            refreshLayout.setRefreshing(true);

            Query query = databaseReference
                    .child(getString(R.string.User_news))
                    .child(getString(R.string.art))
                    .orderByChild("date");

            FirebaseRecyclerOptions<NewsModel> options =
                    new FirebaseRecyclerOptions.Builder<NewsModel>()
                            .setQuery(query, NewsModel.class)
                            .build();

            newsAdaptor = new NewsAdaptor(options,getContext(),getString(R.string.art));

            recyclerView.setAdapter(newsAdaptor);
            refreshLayout.setRefreshing(false);

            onLoadingSwipeRefresh();

      }


      private void onLoadingSwipeRefresh() {
            refreshLayout.post(() -> {
                  if (newsAdaptor != null) {
                        newsAdaptor.startListening();
                  }
            });
      }

      @Override
      public void onRefresh() {
            if (newsAdaptor != null) {
                  newsAdaptor.startListening();
            }
            refreshLayout.setRefreshing(false);
      }}
