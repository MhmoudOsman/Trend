package mahmud.osman.trend;

import android.content.Context;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static final String BASE_URL="https://disease.sh/";

    public static String getUID(){
        return  FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public static String timestampToDateString(long timestamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd-MMM-yyyy ",new Locale("ar"));
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }
    public static long fieldToTimestamp(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return  (calendar.getTimeInMillis() -1000);
    }
    public static void initialYouTubeVideo(YouTubePlayerFragment youtubeFragment, String video_link,String api_kay) {
        youtubeFragment.initialize(api_kay,
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                        youTubePlayer.cueVideo(video_link);
                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

}
