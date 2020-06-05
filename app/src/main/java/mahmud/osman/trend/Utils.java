package mahmud.osman.trend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static final String BASE_URL="https://disease.sh/";

    public static String timestampToDateString(long timestamp){
        SimpleDateFormat dateFormat = new SimpleDateFormat("E, dd-MMM-yyyy ",new Locale("ar"));
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }

}
