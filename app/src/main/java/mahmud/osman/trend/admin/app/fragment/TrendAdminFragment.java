package mahmud.osman.trend.admin.app.fragment;


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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mahmud.osman.trend.Models.CountryModel;
import mahmud.osman.trend.Models.NewsModel;
import mahmud.osman.trend.R;
import mahmud.osman.trend.api.ApiClient;
import mahmud.osman.trend.api.Covid19ApiInterface;
import mahmud.osman.trend.presenters.adapter.NewsAdaptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TrendAdminFragment extends Fragment implements Callback<CountryModel>, SwipeRefreshLayout.OnRefreshListener {
    View view;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private NewsAdaptor newsAdaptor;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private TextView all_cases, all_deaths, all_recovered;
    private CardView covid19_card;
    private Covid19ApiInterface apiInterface = ApiClient.getApiClient().create(Covid19ApiInterface.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_trend_admin, container, false);
        recyclerView = view.findViewById(R.id.rv_trend);
        refreshLayout = view.findViewById(R.id.srl_trend_admin);
        covid19_card = view.findViewById(R.id.cv_covid19_trend);
        all_cases = view.findViewById(R.id.tv_all_cases_trend);
        all_deaths = view.findViewById(R.id.tv_all_deaths_trend);
        all_recovered = view.findViewById(R.id.tv_all_recovered_trend);
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
    }

    private void loudCovid(boolean b) {
        Call<CountryModel> call = apiInterface.getCovid19Stats(b);
        call.enqueue(this);
    }

    private void displayNews() {
        refreshLayout.setRefreshing(true);
        Query query = databaseReference
                .child(getString(R.string.Admin_news))
                .child(getUID())
                .child(getString(R.string.trends))
                .orderByChild("date")
                .limitToLast(10);


        FirebaseRecyclerOptions<NewsModel> options =
                new FirebaseRecyclerOptions.Builder<NewsModel>()
                        .setQuery(query, NewsModel.class)
                        .build();

        newsAdaptor = new NewsAdaptor(options, getContext(), getString(R.string.trends), getUID());
        recyclerView.setAdapter(newsAdaptor);
        refreshLayout.setRefreshing(false);

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
    public void onResume() {
        super.onResume();
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

    @Override
    public void onResponse(Call<CountryModel> call, Response<CountryModel> response) {
        if (response.isSuccessful() && response.body().getTodayCases() != 0) {
            CountryModel model = response.body();

            SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd-MMM-yyyy ", new Locale("ar"));
            Date date = new Date(model.getUpdated());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, -1);
//                  updated_at.setText("اخر تحديث : " +dateFormat.format(calendar.getTime()) );
//                  new_cases.setText(String.valueOf(model.getTodayCases()));
            all_cases.setText(String.valueOf(model.getCases()));
//                  new_deaths.setText(String.valueOf(model.getTodayDeaths()));
            all_deaths.setText(String.valueOf(model.getDeaths()));
//                  active_cases.setText(String.valueOf(model.getActive()));
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
