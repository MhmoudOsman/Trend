package mahmud.osman.trend.api;

import mahmud.osman.trend.Models.CountryModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Covid19ApiInterface {
    @GET("v2/countries/egypt")
    Call<CountryModel> getCovid19Stats(
            @Query("yesterday") boolean yesterday);
}
