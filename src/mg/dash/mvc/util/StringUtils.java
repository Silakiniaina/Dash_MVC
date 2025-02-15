package mg.dash.mvc.util;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class StringUtils {
    
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDate(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            java.util.Date utilDate = dateFormat.parse(str);

            Date sqlDate = new Date(utilDate.getTime());

            return sqlDate != null;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isEmail(String str) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return str.matches(emailRegex);
    }

    public static boolean isNull(String str) {
        return str == null || str.isEmpty();
    }
}
