package mahmud.osman.trend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.daasuu.cat.CountAnimationTextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import mahmud.osman.trend.Models.CountryModel;
import mahmud.osman.trend.api.ApiClient;
import mahmud.osman.trend.api.Covid19ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static mahmud.osman.trend.utils.Utils.isConnected;
import static mahmud.osman.trend.utils.Utils.timestampToDateString;

public class Covid19Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, Callback<CountryModel> {

    private View view;
    private TextView new_cases, new_deaths, last_update;
    private CountAnimationTextView all_cases, all_deaths, recovered, active_cases;
    private SwipeRefreshLayout refreshLayout;
    private final Covid19ApiInterface apiInterface = ApiClient.getApiClient().create(Covid19ApiInterface.class);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_covid19, container, false);
        new_cases = view.findViewById(R.id.cov_new_cases);
        new_deaths = view.findViewById(R.id.cov_new_deaths);
        last_update = view.findViewById(R.id.cov_last_update);
        all_cases = view.findViewById(R.id.cov_all_cases);
        all_deaths = view.findViewById(R.id.cov_all_deaths);
        recovered = view.findViewById(R.id.cov_recovered);
        active_cases = view.findViewById(R.id.cov_active);
        refreshLayout = view.findViewById(R.id.srl_covid19);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshLayout.setColorSchemeResources(R.color.gold);
        refreshLayout.setProgressBackgroundColorSchemeResource(R.color.background);
        refreshLayout.setOnRefreshListener(this);
        onLoadingSwipeRefresh();
    }

    private void onLoadingSwipeRefresh() {
        if (isConnected(getContext())) {
            refreshLayout.post(() -> {
                loudCovid();
            });
            refreshLayout.setRefreshing(false);
        }
    }

    private void loudCovid() {
        Call<CountryModel> call = apiInterface.getCovid19Stats(false);
        call.enqueue(this);
    }

    @Override
    public void onRefresh() {
        if (isConnected(getContext())) {
            loudCovid();
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResponse(Call<CountryModel> call, Response<CountryModel> response) {
        if (response.isSuccessful() && response.body() != null) {
            if (response.body().getTodayCases() != 0) {
                CountryModel model = response.body();
                last_update.setText("اخر تحديث : " + timestampToDateString(model.getUpdated()));
                new_cases.setText("+" + model.getTodayCases());
                new_deaths.setText("+" + model.getTodayDeaths());
                all_cases.setInterpolator(new AccelerateInterpolator()).countAnimation(0, model.getCases());
                all_deaths.setInterpolator(new AccelerateInterpolator()).countAnimation(0, model.getDeaths());
                active_cases.setInterpolator(new AccelerateInterpolator()).countAnimation(0, model.getActive());
                recovered.setInterpolator(new AccelerateInterpolator()).countAnimation(0, model.getRecovered());
            } else {
                Call<CountryModel> get_lastupdate = apiInterface.getCovid19Stats(true);
                get_lastupdate.enqueue(new Callback<CountryModel>() {

                    @Override
                    public void onResponse(Call<CountryModel> call, Response<CountryModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CountryModel model = response.body();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd-MMM-yyyy ", new Locale("ar"));
                            Date date = new Date(model.getUpdated());
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);
                            calendar.add(Calendar.DATE, -1);
                            last_update.setText("اخر تحديث : " + dateFormat.format(calendar.getTime()));
                            new_cases.setText("+" + model.getTodayCases());
                            new_deaths.setText("+" + model.getTodayDeaths());
                            all_cases.setInterpolator(new AccelerateInterpolator()).countAnimation(0, model.getCases());
                            all_deaths.setInterpolator(new AccelerateInterpolator()).countAnimation(0, model.getDeaths());
                            active_cases.setInterpolator(new AccelerateInterpolator()).countAnimation(0, model.getActive());
                            recovered.setInterpolator(new AccelerateInterpolator()).countAnimation(0, model.getRecovered());
                        }
                    }

                    @Override
                    public void onFailure(Call<CountryModel> call, Throwable t) {

                    }
                });
            }
        }

    }

    @Override
    public void onFailure(Call<CountryModel> call, Throwable t) {

    }
}