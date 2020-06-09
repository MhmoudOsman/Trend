package mahmud.osman.trend.user.app.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import mahmud.osman.trend.Covid19Fragment;
import mahmud.osman.trend.Models.CountryModel;
import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.api.ApiClient;
import mahmud.osman.trend.api.Covid19ApiInterface;
import mahmud.osman.trend.presenters.adapter.NewsAdaptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HealthUserFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback<CountryModel> {

      View view;

      private FirebaseDatabase firebaseDatabase;
      private DatabaseReference databaseReference;

      private RecyclerView recyclerView;
      private NewsAdaptor newsAdaptor;


      private SwipeRefreshLayout refreshLayout;
      private TextView all_cases, all_deaths, all_recovered;
      private CardView covid19_card;
      private Covid19ApiInterface apiInterface = ApiClient.getApiClient().create(Covid19ApiInterface.class);


      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container,
                               Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.fragment_health_user, container, false);
            recyclerView = view.findViewById(R.id.rv_health_user);
            refreshLayout = view.findViewById(R.id.srl_health_user);
            covid19_card = view.findViewById(R.id.cv_covid19_health_user);
            all_cases = view.findViewById(R.id.tv_all_cases_health_user);
            all_deaths = view.findViewById(R.id.tv_all_deaths_health_user);
            all_recovered = view.findViewById(R.id.tv_all_recovered_health_user);
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

            covid19_card.setOnClickListener(v -> {
                  FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                  FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                  fragmentTransaction.replace(R.id.fragment_container, new Covid19Fragment());
                  fragmentTransaction.addToBackStack(null);
                  fragmentTransaction.commit();
            });

            displayNews();

      }

      private void onLoadingSwipeRefresh() {
            refreshLayout.post(() -> {
                  if (newsAdaptor != null) {
                        newsAdaptor.startListening();
                  }
                  loudCovid(false);
            });
            refreshLayout.setRefreshing(false);
      }

      private void loudCovid(boolean b) {
            Call<CountryModel> call = apiInterface.getCovid19Stats(b);
            call.enqueue(this);
      }

      private void displayNews() {
            refreshLayout.setRefreshing(true);

            Query query = databaseReference
                    .child(getString(R.string.User_news))
                    .child(getString(R.string.health))
                    .orderByChild("date");

            FirebaseRecyclerOptions<NewsModel> options =
                    new FirebaseRecyclerOptions.Builder<NewsModel>()
                            .setQuery(query, NewsModel.class)
                            .build();
            newsAdaptor = new NewsAdaptor(options, getContext(), getString(R.string.health));
            recyclerView.setAdapter(newsAdaptor);

            onLoadingSwipeRefresh();
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

      @Override
      public void onResponse(Call<CountryModel> call, Response<CountryModel> response) {
            if (response.isSuccessful() && response.body().getTodayCases() != 0) {
                  CountryModel model = response.body();
                  all_cases.setText(String.valueOf(model.getCases()));
                  all_deaths.setText(String.valueOf(model.getDeaths()));
                  all_recovered.setText(String.valueOf(model.getRecovered()));
            } else {
                  loudCovid(true);
            }
      }

      @Override
      public void onFailure(Call<CountryModel> call, Throwable t) {
            Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Failure", t.getMessage());

      }

      @Override
      public void onRefresh() {
            if (newsAdaptor != null) {
                  newsAdaptor.startListening();
            }
            loudCovid(false);
            refreshLayout.setRefreshing(false);
      }
}


