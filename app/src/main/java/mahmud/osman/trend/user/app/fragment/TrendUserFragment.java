package mahmud.osman.trend.user.app.fragment;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.victor.loading.rotate.RotateLoading;

import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.presenters.adapter.NewsAdaptor;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrendUserFragment extends Fragment {

      View view;



      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;

      private RecyclerView recyclerView;
      private LinearLayoutManager layoutManager;
      private NewsAdaptor newsAdaptor;

      RotateLoading rotateLoading;




      @Override
      public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                               Bundle savedInstanceState) {

            view = inflater.inflate(R.layout.fragment_trend_user , container , false);
            recyclerView = view.findViewById(R.id.rv_trend_user);
            rotateLoading = view.findViewById(R.id.rl_trend_user);

            rotateLoading.start();

            return view ;
      }

      @Override
      public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

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
                    .child(getString(R.string.User_news))
                    .child(getString(R.string.trends))
                    .limitToLast(10);

            FirebaseRecyclerOptions<NewsModel> options =
                    new FirebaseRecyclerOptions.Builder<NewsModel>()
                            .setQuery(query, NewsModel.class)
                            .build();

            newsAdaptor = new NewsAdaptor(options,getContext(),getString(R.string.trends));

            recyclerView.setAdapter(newsAdaptor);
            rotateLoading.stop();

      }

      @Override
      public void onStart() {
            super.onStart();
            if (newsAdaptor != null) {
                  newsAdaptor.startListening();
            }
      }

      @Override
      public void onStop() {
            super.onStop();
            if (newsAdaptor != null) {
                  newsAdaptor.startListening();
            }
      }

}
