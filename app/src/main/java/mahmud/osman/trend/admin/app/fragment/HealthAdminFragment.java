package mahmud.osman.trend.admin.app.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
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
public class HealthAdminFragment extends Fragment {

      View view;

      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;
      private FirebaseAuth mAuth = FirebaseAuth.getInstance();
      private NewsAdaptor newsAdaptor;
      private RecyclerView recyclerView;
      private LinearLayoutManager layoutManager;
      private RotateLoading rotateLoading;


      @Override
      public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                               Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_health_admin , container , false);
            recyclerView = view.findViewById(R.id.rv_health);
            rotateLoading = view.findViewById(R.id.rl_health);
            return view;
      }


      @Override
      public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            rotateLoading.start();
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference();
            databaseReference.keepSynced(true);


            layoutManager = new LinearLayoutManager(getContext() , LinearLayoutManager.VERTICAL , true);
            layoutManager.setStackFromEnd(true);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(layoutManager);


            displayInterviewNews();


      }

      private void displayInterviewNews() {
            String type = getString(R.string.health);
            Query query = databaseReference
                    .child(getString(R.string.Admin_news))
                    .child(getUID())
                    .child(type)
                    .limitToLast(10);

            FirebaseRecyclerOptions<NewsModel> options =
                    new FirebaseRecyclerOptions.Builder<NewsModel>()
                            .setQuery(query , NewsModel.class)
                            .build();

            newsAdaptor = new NewsAdaptor(options , getContext() , type , getUID());
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

      private String getUID() {
            String id = mAuth.getCurrentUser().getUid();
            return id;
      }


}
