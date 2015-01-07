package sowa.nfp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import sowa.nfp.R;
import sowa.nfp.notification.AlarmSupervisor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

public class SettingsActivity extends Activity {
	
	public static SharedPreferences settings;
	
	private final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
	public static boolean pendingPublishReauthorization = false;
	
	//================================================= SETTINGS ===============================================
	CheckBox checkBox;
	boolean mIgnoreTimeSet;
	Button btnPickTime;
	TimePickerDialog timePickerDialog;
	Context context;
	TextView txtInfo;
	AlarmSupervisor alarmManager;
	//=========================================================================================================
	
	Intent intent;
	
	//=========================================================================================================
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
		context = this;
		fadeIn.setDuration(1200);
		fadeIn.setFillAfter(true);

		txtInfo = (TextView) findViewById(R.id.tvInfo);
		btnPickTime = (Button) findViewById(R.id.pickTime);
		checkBox = (CheckBox) findViewById(R.id.checkCircumstances);	
		btnPickTime = (Button) findViewById(R.id.pickTime);
		//SlowoPreferences.storePrefBool(context, TwitterActivity.PREF_KEY_TWITTER_LOGIN, false);
		alarmManager = new AlarmSupervisor(context);
		setInfo();
	}

	public void onStart() {
		
		super.onStart();
		printAllPrefValues();
		//------------ CHECKBOX ----------------------
		
		if (NfpPreferences.rtnPrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY)) {
			NfpPreferences.storePrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY,true);
			checkBox.setChecked(true);
		} else {
			NfpPreferences.storePrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY,false);
			checkBox.setChecked(false);
		}

		checkBox.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View v) {
				  
				if (((CheckBox) v).isChecked()) {
					NfpPreferences.storePrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY,true);
					alarmManager.setDailyAlarm();
					setInfo();
				} else {
					NfpPreferences.storePrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY,false);
					alarmManager.cancelAlarm();
					setInfo();
				}
			 }
		});
		
		btnPickTime.setOnClickListener(new View.OnClickListener() {	
		public void onClick(View v) {
			openTimePickerDialog(true);
			
		}} );
		
	}

	//=========================================================================================================
	//=========================================================================================================
	//=========================================================================================================
	//=========================================================================================================
	//=========================================================================================================
		
		 
	private void openTimePickerDialog(boolean is24r) {
		
		TimePickerDialog.OnTimeSetListener onTimeSetListener = new OnTimeSetListener() {
			  @Override
			  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

				  if (!mIgnoreTimeSet) {
					  NfpPreferences.storePrefInt(context, NfpPreferences.HOUR_OF_DAY_KEY,hourOfDay);
					  	NfpPreferences.storePrefInt(context, NfpPreferences.MINUTE_OF_DAY_KEY,minute); 
					  	alarmManager.setDailyAlarm();
					  	NfpPreferences.storePrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY,true);
						setInfo();
				  }
			  }
		 };
		 
		timePickerDialog = new TimePickerDialog(
				  SettingsActivity.this, onTimeSetListener, NfpPreferences.rtnPrefInt(context, NfpPreferences.HOUR_OF_DAY_KEY), 
				  NfpPreferences.rtnPrefInt(context, NfpPreferences.MINUTE_OF_DAY_KEY), true);

		timePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, NfpPreferences.BUTTON_USTAW, new DialogInterface.OnClickListener() {
		    @Override
			public void onClick(DialogInterface dialog, int which) {
		        if (which == DialogInterface.BUTTON_POSITIVE) {
		            mIgnoreTimeSet = false;
		            	timePickerDialog.onClick(dialog, which); 
		        }
		    }
		});
		timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, NfpPreferences.BUTTON_WYJDZ, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
		        if (which == DialogInterface.BUTTON_NEGATIVE) {
		            mIgnoreTimeSet = true;
		        }
		    }
		});

		timePickerDialog.setTitle(NfpPreferences.TIME_PICKER_TITLE + " " + 
		pad(NfpPreferences.rtnPrefInt(context, NfpPreferences.HOUR_OF_DAY_KEY)) + ":" + 
		pad(NfpPreferences.rtnPrefInt(context, NfpPreferences.MINUTE_OF_DAY_KEY)));  
		timePickerDialog.setMessage(NfpPreferences.TIME_PICKER_MSG);

		        
		timePickerDialog.setOnCancelListener(new OnCancelListener(){
		   @Override
		   public void onCancel(DialogInterface dialog) {
			   
		   }});
		        
		timePickerDialog.setOnDismissListener(new OnDismissListener(){
		   @Override
		   public void onDismiss(DialogInterface arg0) {
			   
		   }});
		        
		timePickerDialog.show();
	}
	
	  
	private void setInfo() {
		if (NfpPreferences.rtnPrefBool(context, NfpPreferences.SLOWO_CODZIENNIE_KEY)) {
			btnPickTime.setEnabled(true);
			String info = NfpPreferences.INFO_DAILY_VERSES_TIME + AlarmSupervisor.convertTime(NfpPreferences.rtnPrefLong(context, NfpPreferences.DAILY_WAKE_UP_KEY));
			txtInfo.setAnimation(fadeIn);
			txtInfo.setText(info);
		} else {
			txtInfo.setAnimation(fadeIn);
			txtInfo.setText(NfpPreferences.INFO_NOTIFICATIONS_OFF);
			btnPickTime.setEnabled(false);
		}
	}
	
	//===========================================================

    @SuppressLint("SimpleDateFormat")
	public static String dayOfWeek(String strDate){
    	String s = null;
    	try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(strDate);
			s = DateFormat.format("EEEE", date).toString();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return s;
    }
    
	//===========================================================
	
	
	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
	
	@SuppressWarnings("rawtypes")
	private void printAllPrefValues() {
		 Map<String, ?> m = NfpPreferences.getPrefs(context).getAll();
		    Iterator<?> it = m.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pairs = (Map.Entry)it.next();
		        Log.d(NfpPreferences.SETTINGS_ACTIVITY_TAG, pairs.getKey() + " = " + pairs.getValue());
		    }
	}

	@Override
	public void onBackPressed() {
		//Intent startingIntent = new Intent(SettingsActivity.this, StartingActivity.class);
		//startingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		//startingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//SettingsActivity.this.startActivity(startingIntent);
		super.onBackPressed();
		SettingsActivity.this.finish();
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
	    super.onPause();
	}
	
 
	@Override
	public void onDestroy() {
	    super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	}
	

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		SettingsActivity.this.overridePendingTransition (R.anim.ustawienia_fadein, R.anim.ustawienia_fadeout);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
	}

}
