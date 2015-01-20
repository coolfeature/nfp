package ionas.nfp.notification;

import ionas.nfp.NfpPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	public void onReceive(Context context, Intent intent) {
		setInfo(context);
	}
	
	private void setInfo(Context context) {	
		Log.d("BOOT RECEIVER: ", "GOT BOOTTY:)");
		if (NfpPreferences.SLOWO_CODZIENNIE_KEY != null) {
			AlarmSupervisor alarmManager = new AlarmSupervisor(context);
			if (NfpPreferences.rtnPrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY)) {
				Log.d("BOOT RECEIVER: ", "GOT SERVICE ON)");
				alarmManager.setDailyAlarm();
			}
		} 
	}	
}
