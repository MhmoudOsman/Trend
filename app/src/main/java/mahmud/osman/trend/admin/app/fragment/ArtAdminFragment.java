package mahmud.osman.trend.admin.app.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.presenters.adapter.NewsAdaptor;

import static mahmud.osman.trend.utils.Utils.getUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtAdminFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


      View view;

      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;
      private FirebaseAuth mAuth = FirebaseAuth.getInstance();
      private NewsAdaptor newsAdaptor;
      private RecyclerView recyclerView;
      private SwipeRefreshLayout refreshLayout;


      @Override
      public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                               Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_art_admin , container , false);
            recyclerView = view.findViewById(R.id.rv_art);
            refreshLayout = view.findViewById(R.id.srl_art_admin);
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

            String type = getString(R.string.art);
            Query query = databaseReference
                    .child(getString(R.string.Admin_news))
                    .child(getUID())
                    .child(type)
                    .orderByChild("date");

            FirebaseRecyclerOptions<NewsModel> options =
                    new FirebaseRecyclerOptions.Builder<NewsModel>()
                            .setQuery(query, NewsModel.class)
                            .build();

            newsAdaptor = new NewsAdaptor(options, getContext(), type, getUID());
            recyclerView.setAdapter(newsAdaptor);

            refreshLayout.setRefreshing(false);

            onLoadingSwipeRefresh();

      }

      @Override
      public void onStart() {
            super.onStart();
            if (newsAdaptor != null) {
                  refreshLayout.setRefreshing(true);
                  newsAdaptor.startListening();
            }
            refreshLayout.setRefreshing(false);
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

      }
}
