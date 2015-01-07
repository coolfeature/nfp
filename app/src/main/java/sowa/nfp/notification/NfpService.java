package sowa.nfp.notification;


import java.lang.ref.WeakReference;

import sowa.nfp.R;
import sowa.nfp.StartingActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NfpService extends Service {

	private WakeLock mWakeLock;
	public static String action = "";
	NotificationManager notificationManager;
	Handler serviceHandler;

	static class IncomingHandler extends Handler {
		WeakReference<NfpService> serviceFrag;

		IncomingHandler(NfpService aFragment) {
			serviceFrag = new WeakReference<NfpService>(aFragment);
		}

		@Override
		public void handleMessage(Message msg) {
			NfpService theService = serviceFrag.get();
			Log.d("NFP SERVICE: GET WHEN - ", Long.toString(msg.getWhen()));
			if (msg.getData().containsKey("randomVerse")) {
				theService.fireNotification();
			}
		}
	}

	/**
	 * When binding to the service, we return an interface to our messenger for
	 * sending messages to the service.
	 */

	public class ServiceBinder extends Binder {
		public NfpService getService() {
			return NfpService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private final IBinder mBinder = new ServiceBinder();

	private void handleIntent(Intent intent) {
		// obtain the wake lock
		Log.d("NFP SERVICE: ", "Handling intent");
		PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"SlowoServiceTag");
		mWakeLock.acquire();
		// do the actual work, in a separate thread
		doStuff();
	}

	private void doStuff() {
/*		serviceHandler = new IncomingHandler(this);
		vr = new VerseReference(this.getResources());
		try {
			vr.getRandomReference();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb = new SlowoBoze(serviceHandler, this.getResources(), vr);
		Thread background = new Thread(sb);

		if (!background.isAlive()) {
			background.start();
		}*/
		fireNotification();
	}

	private void fireNotification() {
		Intent intent = new Intent(NfpService.this, StartingActivity.class);
		// API > 7 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_CLEAR_TOP);

/*		Bundle bundle = new Bundle();
		bundle.putInt("book", bookId);
		bundle.putInt("chapter", chapId);
		bundle.putInt("verse", verseId);
		intent.putExtra(NfpPreferences.NOTIFICATION_KEY, bundle);*/

		PendingIntent pIntent = PendingIntent.getActivity(NfpService.this, 0,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
				NfpService.this)
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Dzien dobry!")
				.setContentText("Hej Migotko!:) Masz termometr pod reka?")
				.setDefaults(Notification.DEFAULT_LIGHTS
								| Notification.DEFAULT_VIBRATE)
				.setAutoCancel(true).setContentIntent(pIntent);

		notificationManager.notify(0, mNotifyBuilder.build());
		stopSelf();
	}

	/**
	 * * This is deprecated, but you have to implement it if you're planning on
	 * * supporting devices with an API level lower than 5 (Android 2.0).
	 */

	@Override
	public void onStart(Intent intent, int startId) {
		handleIntent(intent);
	}

	/**
	 * * This is called on 2.0+ (API level 5 or higher). Returning *
	 * START_NOT_STICKY tells the system to not restart the service if it is *
	 * killed because of poor resource (memory/cpu) conditions.
	 * */
	@Override
	public int onStartCommand(Intent intent, int flags, int StartId) {
		handleIntent(intent);
		return Service.START_STICKY;
	}

	/**
	 * * In onDestroy() we release our wake lock. This ensures that whenever the
	 * * Service stops (killed for resources, stopSelf() called, etc.), the wake
	 * * lock will be released.
	 * */

	public void onDestroy() {
		super.onDestroy();
		mWakeLock.release();
	}

}
