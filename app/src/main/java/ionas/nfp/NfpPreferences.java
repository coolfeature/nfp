package ionas.nfp;

import android.content.Context;
import android.content.SharedPreferences;

public class NfpPreferences {
	
	public static final String PREFS_NAME = "preferencesFile";
	
	public static final String STARTING_ACTIVITY_TAG = "StartingActivity";
	public static final String SETTINGS_ACTIVITY_TAG = "SettingsActivity";
	public static final String ALARMSUPERVISOR_ACTIVITY_TAG = "AlarmSupervisor";

	public static final int TODAY_NOTIFICATION_REQUEST_CODE = 1000;
	public static final String THIS_MIDNIGHT_KEY = "thisMidnight";
	public static final int INTERVAL_DAY = 86400000;
	
	public static final String NOTIFICATION_CODE_USED_KEY = "notiCode";
	public static final String NOTIFICATION_KEY = "notiRef";
	public static final String HOUR_OF_DAY_KEY = "hr";
	public static final String MINUTE_OF_DAY_KEY = "min";
	public static final String SLOWO_CODZIENNIE_KEY = "slowoCodziennie";
	public static final String NEXT_DAILY_WAKE_UP_KEY = "nextDailyWakeUpTime";
	public static final String DAILY_WAKE_UP_KEY = "dailyWakeUpTime";


	public static final int VERSE_FONT = 18;
	
	
	//------------- LOCALISATION -----------------
	public static final String APP_NAME = "NFP";
	public static final String LANG = "en";


	// SETTINGS
	public static final String TIME_PICKER_MSG = "Czas notyfikacji:";
	public static final String TIME_PICKER_TITLE = "Słowo na co dzień";
	public static final String BUTTON_WYJDZ = "Wyjdź";
	public static final String BUTTON_USTAW = "Ustaw";;
	public static final String INFO_NOTIFICATIONS_OFF = "Dzienne powiadomienia wyłączone.";

	public static final String INFO_RANDOM_NOTIFICATIONS_TITLE = "Losowe powiadomienia";
	public static final String INFO_DAILY_VERSES_TIME = "Słowo na co dzień - ";
	
/*	public static final String TIME_PICKER_MSG = "Notification time:";
	public static final String TIME_PICKER_TITLE = "Daily notifications";
	public static final String BUTTON_WYJDZ = "Quit";
	public static final String BUTTON_USTAW = "Set";
	public static final String INFO_SETTINGS = "The number of notifications about randomly selected verses delivered within 24 hour period:  ";
	public static final String INFO_NOTIFICATIONS_OFF = "Daily notifications are off";
	public static final String INFO_NO_FB_PERMISSION = "No permission to publish";
	public static final String INFO_FB_ERROR = "There has been an error, please log out and log back in.";
	public static final String INFO_BEEN_PUBLISHED = "The verse posted successfully";
	public static final String INFO_RANDOM_NOTIFICATIONS_TITLE = "Random notifications";
	public static final String INFO_DAILY_VERSES_TIME = "Daily notifications time: ";
	public static final String INFO_FB_CONNECTING = "Connecting to Facebook...";
	public static final String INFO_INFO_MAX_NOTIFICATIONS = "The maximum number is 96"; 
	public static final String INFO_ZERO_NOTIFICATIONS = "The number cannot be 0";
	public static final String INFO_TWITTER_CONNECTION_TITLE = "Internet Connection Error";
	public static final String INFO_TWITTER_CONNECTION_MSG = "Please connect to working Internet connection";
	public static final String INFO_TWITTER_OATH_TITLE = "Twitter oAuth tokens";
	public static final String INFO_TWITTER_OATH_MSG = "Please set your twitter oauth tokens first";
	public static final String INFO_TWITTER_UPDATE = "Updating to twitter...";
	public static final String INFO_LOGGED_OUT = "Please login";
	public static final String INFO_BEEN_TWEETED = "Tweet posted";
	public static final String INFO_ALREADY_LOGGED_IN = "Already Logged into twitter";*/
	// STARTING
	
	public static final String INFO_WAITING = "Czekam...";
	public static final String INFO_BEEN_SAVED = "Zapisano";
	
/*	public static final String INFO_WAITING = "Waiting...";
	public static final String INFO_BEEN_SAVED = "Saved";*/
	
	// ULUBIONE
	
	public static final String INFO_WERSETY_DELETED = "Wpis usunięty.";
	public static final String INFO_CHOOSE_WERSETY_FOR_DELETION = "Wybierz werset do usunięcia.";
	public static final String INFO_SELECT_WERSET = "Zaznacz werset.";
	
/*	public static final String INFO_WERSETY_DELETED = "Deleted";
	public static final String INFO_CHOOSE_WERSETY_FOR_DELETION = "Mark verse for deletion";
	public static final String INFO_SELECT_WERSET = "Mark verse";*/
	
	// SHARE DIALOG
	
	public static final String INFO_CHOOSE_EMAIL_CLIENT = "Wybierz klienta pocztowego :";
	public static final String INFO_VERSE_FOR_YOU = "Słowo dla Ciebie";
	
/*	public static final String INFO_CHOOSE_EMAIL_CLIENT = "Select email application :";
	public static final String INFO_VERSE_FOR_YOU = "Bible verse for you";*/
	
	// SEARCH ACTIVITY
	public static final String INFO_CHPATERS_COUNT_START = "Ta Księga ma ";
	public static final String INFO_CHPATERS_COUNT_END = " rozdziałow";
	public static final String INFO_VERSES_COUNT_START = "Ten Rozdział ma ";
	public static final String INFO_VERSES_COUNT_END = " wersety";
	
/*	public static final String INFO_CHPATERS_COUNT_START = "This Book has ";
	public static final String INFO_CHPATERS_COUNT_END = " chapters";
	public static final String INFO_VERSES_COUNT_START = "This chapter has ";
	public static final String INFO_VERSES_COUNT_END = " verses";*/
	
	//=================================================================================
	
    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFS_NAME, 0);
    }
    public static String rtnPrefString(Context context, String key) {
        return getPrefs(context).getString(key, "");
    }
    public static int rtnPrefInt(Context context, String key) {
        return getPrefs(context).getInt(key, 1);
    }
    public static boolean rtnPrefBool(Context context, String key) {
        return getPrefs(context).getBoolean(key, false);
    }
    public static long rtnPrefLong(Context context, String key) {
        return getPrefs(context).getLong(key, 0);
    }
 
    public static void storePrefString(Context context, String key, String value) {
        getPrefs(context).edit().putString(key, value).commit();
    }
    public static void storePrefInt(Context context, String key, int value) {
        getPrefs(context).edit().putInt(key, value).commit();
    }
    public static void storePrefLong(Context context, String key, long value) {
        getPrefs(context).edit().putLong(key, value).commit();
    }
    public static void storePrefBool(Context context, String key, boolean value) {
        getPrefs(context).edit().putBoolean(key, value).commit();
    }  
    public static void remove(Context context, String key) {
        getPrefs(context).edit().remove(key).commit();
    } 
    
}
