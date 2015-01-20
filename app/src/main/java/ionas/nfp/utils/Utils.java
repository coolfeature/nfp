package ionas.nfp.utils;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by sczaja on 07/01/2015.
 */
public class Utils {

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String WEEKDAY_FORMAT = "EEEE";

    /**
     * Returns current date and time in milliseconds
     * <p>
     * This method uses JodaTime.
     *
     * @return current date in milliseconds.
     */
    public static long getNow() {
        return new DateTime().getMillis();
    }

    /**
     * Returns current date set to midnight
     * <p>
     * This method uses JodaTime.
     *
     * @return current midnight
     */
    public static DateTime getMidnight() { return getMidnight().plusDays(0); }


    /**
     * Returns a DateTime at midnight for the current date adjusted with accordance
     * to day parameter e.g. getMidnight(-1) would return a midnight for yesterday
     * <p>
     * This method uses JodaTime.
     *
     * @param day an int that can also be negative
     * @return a midnight in DateTime
     */
    public static DateTime getMidnight(int day) {
        return new DateTime().plusDays(day)
                .withHourOfDay(0).withMinuteOfHour(0)
                .withSecondOfMinute(0).withMillisOfSecond(0);
    }


    /**
     * Returns the number of milliseconds left to date.
     * <p>
     * This method uses JodaTime.
     *
     * @param dateMillis the date in milliseconds
     * @return number of milliseconds left
     */
    public static long leftUntil(long dateMillis) {
        long now = Utils.getNow();
        if (dateMillis < now) {
           return 0;
        } else {
            return (dateMillis - now);
        }
    }


    /**
     * Returns current DateTime
     * <p>
     * This method uses JodaTime.
     *
     * @return current date and time as LocalDateTime
     */
    public static DateTime getCurrentDateTime() {
        return new DateTime();
    }


    /**
     * Returns current date with time set with accordance to parameters.
     * <p>
     * This method uses JodaTime.
     *
     * @param hr Hour
     * @param min Minute
     *
     * @return current date and time set with accordance to parameters as LocalDateTime
     */
    public static DateTime getDateTime(int hr, int min) {
        return getCurrentDateTime().withHourOfDay(hr).withMinuteOfHour(min);
    }

    /**
     * Returns current date formatted with accordance to the format
     * specified DATE_FORMAT variable of this class.
     * <p>
     * This method uses JodaTime.
     *
     * @return current date as String
     */
    public static String getCurrentDate() {
        return DateTimeFormat.forPattern(DATE_FORMAT).print(getCurrentDateTime());
    }


    /**
     * Returns current weekday formatted with accordance to the format
     * specified in WEEKDAY_FORMAT variable of this class.
     * <p>
     * This method uses JodaTime.
     *
     * @return the weekday as String
     */
    public static String toWeekDay() {
        return DateTimeFormat.forPattern(WEEKDAY_FORMAT).print(getCurrentDateTime());
    }


    /**
     * Returns weekday formatted with accordance to the format
     * specified in WEEKDAY_FORMAT variable of this class.
     * <p>
     * This method uses JodaTime.
     *
     * @param millis a long with milliseconds representing a date
     * @return the weekday as String
     */
    public static String toWeekDay(long millis) {
        return DateTimeFormat.forPattern(WEEKDAY_FORMAT).print(new DateTime(millis));
    }


    /**
     * Converts milliseconds to date String formatted with accordance to the format
     * specified in DATE_FORMAT variable of this class.
     * <p>
     * This method uses JodaTime.
     *
     * @param millis a long with milliseconds
     * @return the date as String
     */
    public static String toDateString (long millis) {
        return DateTimeFormat.forPattern(DATE_FORMAT).print(new DateTime(millis));
    }


    /**
     * Converts a DateTime to date String formatted with accordance to the format
     * specified in DATE_FORMAT variable of this class.
     * <p>
     * This method uses JodaTime.
     *
     * @param date
     * @return the date as String
     */
    public static String toDateString (DateTime date) {
        return DateTimeFormat.forPattern(DATE_FORMAT).print(date);
    }


    /**
     * Converts milliseconds to time String formatted with accordance to the format
     * specified in TIME_FORMAT variable of this class.
     * <p>
     * This method uses JodaTime.
     *
     * @param millis a long with milliseconds
     * @return the time as String
     */
    public static String toTimeString (long millis) {
        return DateTimeFormat.forPattern(TIME_FORMAT).print(new DateTime(millis));
    }

    /**
     * Converts milliseconds to date and time String formatted with accordance to the format
     * specified in DATE_FORMAT and TIME_FORMAT variables of this class.
     * <p>
     * This method uses JodaTime.
     *
     * @param millis a long with milliseconds
     * @return the date and time as String
     */
    public static String toDateTimeString (long millis) {
        return DateTimeFormat.forPattern(DATE_FORMAT+" "+TIME_FORMAT).print(new DateTime(millis));
    }

    /**
     * Converts date string formatted in accordance to DATE_FORMAT to a DateTime.
     * <p>
     * This method uses JodaTime.
     *
     * @param date
     * @return the DateTime
     */
    public static DateTime stringDateToDateTime (String date) {
        DateTimeFormatter dtf = DateTimeFormat.forPattern(DATE_FORMAT);
        return dtf.parseDateTime(date);
    }

    /**
     * Selects a random value from a range
     * <p>
     * @param aStart a start value
     * @param aEnd an end value
     * @return a random number in between
     */
    public static long getRandomLong(long aStart, long aEnd) {
        long range = aEnd - aStart + 1;
        long fraction = (long) (range * new Random().nextDouble());
        return fraction + aStart;
    }

    /**
     * Selects a random value from a range
     * <p>
     * @param aStart a start value
     * @param aEnd an end value
     * @param precision the number of decimal places
     * @return a random number in between
     */
    public static double getRandomDouble(double aStart, double aEnd, int precision) {
        double range = aEnd - aStart + 1;
        double rand = aStart + (range * new Random().nextDouble());
        return new BigDecimal(rand).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * Calculates the number of days between two dates, including the day of the startDate and
     * inlcuding the day of the endDate.
     * <p>
     * @param startDate a start value
     * @param endDate an end value
     * @return the number of days as a whole number
     */
    public static int daysSpanning(DateTime startDate, DateTime endDate) {
        return daysBetween(startDate,endDate) + 1;
    }


    /**
     * Calculates the number of days between two dates, including the day of the startDate and
     * excluding the day of the endDate.
     * <p>
     * @param startDate a start value
     * @param endDate an end value
     * @return the number of days as a whole number
     */
    public static int daysBetween(DateTime startDate, DateTime endDate) {
        if (startDate != null && endDate != null) {
            return Days.daysBetween(startDate.toLocalDate(), endDate.toLocalDate()).getDays();
        } else {
            return 0;
        }
    }


    /**
     * Advance month day of a date.
     * <p>
     * @param date
     * @param dayShift number of days
     * @return the result Date
     */
    public static DateTime shiftDays(DateTime date, int dayShift) {
        return date.plusDays(dayShift);
    }

}
