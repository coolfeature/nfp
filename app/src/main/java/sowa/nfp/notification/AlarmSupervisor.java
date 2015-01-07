package sowa.nfp.notification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import sowa.nfp.NfpPreferences;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class AlarmSupervisor {
	
	Context context;
	
	public AlarmSupervisor(Context context) {
		this.context = context;
	}

	//-----------------------------------------------------------------------------------------
	//----------------------------------- DAILY -----------------------------------------------
	//-----------------------------------------------------------------------------------------
	
	public void setDailyAlarm() {
		cancelAlarm();
		int intentRequestCode = NfpPreferences.TODAY_NOTIFICATION_REQUEST_CODE;
		AlarmManager dailyam = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent dailyi = new Intent(context, NfpService.class);
		PendingIntent dailypi = PendingIntent.getService(context, intentRequestCode, dailyi, PendingIntent.FLAG_UPDATE_CURRENT);
		int elapsed = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		Log.d("NFP SERVICE:", Integer.toString(elapsed));
		long wakeUp = calculateAlarmWakeUp(NfpPreferences.rtnPrefInt(context, NfpPreferences.HOUR_OF_DAY_KEY), NfpPreferences.rtnPrefInt(context, NfpPreferences.MINUTE_OF_DAY_KEY));
		Log.d("NFP SERVICE:", convertTime(wakeUp));
		int intervalKey = NfpPreferences.INTERVAL_DAY;
		Log.d("NFP SERVICE:", Integer.toString(intervalKey));
		dailyam.setRepeating(elapsed,wakeUp,intervalKey, dailypi);
		NfpPreferences.storePrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY,true);
	}
	
	public void cancelAlarm() {
		Intent intent = new Intent(context, NfpService.class);
		int intentRequestCode = NfpPreferences.TODAY_NOTIFICATION_REQUEST_CODE;
		PendingIntent pi = PendingIntent.getService(context, intentRequestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
		Log.d(NfpPreferences.SETTINGS_ACTIVITY_TAG, "Cancelling alarm with flag: " + 
		Integer.toString(intentRequestCode));
	}


	
	//-----------------------------------------------------------------------------------------
	//--------------------------------- HELPERS -----------------------------------------------
	//-----------------------------------------------------------------------------------------
	
	private long calculateAlarmWakeUp(int hrs, int min) {
		Calendar calendar = Calendar.getInstance();
		 
		calendar.set(Calendar.HOUR_OF_DAY, hrs);
		calendar.set(Calendar.MINUTE, min);
		calendar.set(Calendar.SECOND, 0);
		
		long dailyWakeUpTime = calendar.getTimeInMillis();
		Log.d("Wake up:", convertTime(dailyWakeUpTime));
		long currentTime = System.currentTimeMillis();
		Log.d("Current:", convertTime(currentTime));
		long timeSinceBoot = SystemClock.elapsedRealtime();
		Log.d("Since boot:", convertTime(timeSinceBoot));
		
		long nextDailyWakeUpTime;
		if (dailyWakeUpTime < currentTime) {
			nextDailyWakeUpTime = timeSinceBoot + (dailyWakeUpTime - currentTime) + AlarmManager.INTERVAL_DAY;
		} else {
			nextDailyWakeUpTime = timeSinceBoot + (dailyWakeUpTime - currentTime);
		}
		
		NfpPreferences.storePrefLong(context, NfpPreferences.NEXT_DAILY_WAKE_UP_KEY, nextDailyWakeUpTime);
		NfpPreferences.storePrefLong(context, NfpPreferences.DAILY_WAKE_UP_KEY, dailyWakeUpTime);
		
		return nextDailyWakeUpTime;
	}
	
	public static long calculateRandomLong(long aStart, long aEnd) {
		long range = aEnd - aStart + 1;
		long fraction = (long) (range * new Random().nextDouble());
		long randomNumber = fraction + aStart;
		return randomNumber;
	}
	
	public static long leftUntilMidnight() {
	    Calendar c = Calendar.getInstance();
        Date now = new Date();
        c.setTime(now);
        c.add(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long howMany = (c.getTimeInMillis()-now.getTime());	
		return howMany;
	}
	
	  @SuppressLint("SimpleDateFormat")
	public static String convertDate (long val) {
	    SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
	    return ft.format(val);  
	}
	  @SuppressLint("SimpleDateFormat")
	public static String convertTime (long val) {
		SimpleDateFormat ft = new SimpleDateFormat("HH:mm:ss");
		return ft.format(val);
	}
	  
	public static void storeMidnight(Context context) {
		Calendar c = Calendar.getInstance();
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    c.add(Calendar.DAY_OF_MONTH, 1);
	    NfpPreferences.storePrefLong(context, NfpPreferences.THIS_MIDNIGHT_KEY,c.getTimeInMillis());
	}
	    
	public static long thisMidnight() {
		Calendar c = Calendar.getInstance();
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    c.add(Calendar.DAY_OF_MONTH, 1);
	    return c.getTimeInMillis();
	}

}
