package ionas.nfp.notification;

import ionas.nfp.NfpPreferences;
import ionas.nfp.utils.Utils;

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
		Log.d("NFP SERVICE:", Utils.toTimeString(wakeUp));
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
	
	private long calculateAlarmWakeUp(int hr, int min) {
		long wakeUp = Utils.getDateTime(hr,min).getMillis();
		Log.d("Wake up:", Utils.toDateTimeString(wakeUp));
		long now = Utils.getNow();
		Log.d("Current:", Utils.toDateTimeString(now));
		long sinceBoot = SystemClock.elapsedRealtime();
		Log.d("Since boot:", Long.toString(sinceBoot));
		long nextWakeUp = sinceBoot + (wakeUp - now);
		if (wakeUp < now) {
			nextWakeUp = nextWakeUp + AlarmManager.INTERVAL_DAY;
		}
		NfpPreferences.storePrefLong(context, NfpPreferences.NEXT_DAILY_WAKE_UP_KEY, nextWakeUp);
		NfpPreferences.storePrefLong(context, NfpPreferences.DAILY_WAKE_UP_KEY, wakeUp);
		return nextWakeUp;
	}

	public static void storeMidnight(Context context) {
	    NfpPreferences.storePrefLong(context,NfpPreferences.THIS_MIDNIGHT_KEY,Utils.getMidnight(1).getMillis());
	}

}
