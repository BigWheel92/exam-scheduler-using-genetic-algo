package exam_input_data_classes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

	static final SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");

	//the following question returns the previous date of the given date
	public static String getPreviousDate(String  curDate) {
		Date date;

		try {
			date = format.parse(curDate);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_YEAR, -1);
			return format.format(calendar.getTime());
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	//the following function returns the next date of the given date. (Date input Format in String: 18-Nov-2018)
	public static String getNextDate(String  curDate) {
		Date date;

		try {
			date = format.parse(curDate);
			final Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			return format.format(calendar.getTime());
		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	//the following function returns the day name of the given date.
	public static String getDayName(String  curDate) {

		try {
			Date date = format.parse(curDate);
			date = format.parse(curDate);
			SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE"); // the day of the week spelled out completely
			return dayFormat.format(date);

		}
		catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}
}
