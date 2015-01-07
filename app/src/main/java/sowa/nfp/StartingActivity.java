package sowa.nfp;

import sowa.nfp.R;
import sowa.nfp.db.Observation;
import sowa.nfp.db.ObservationDataSource;
import sowa.nfp.notification.AlarmSupervisor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @seSystemUiHider
 */
public class StartingActivity extends Activity {

	final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
	final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

	ObservationDataSource datasource;
	FlingDetector flingDetector;
	Context context;

	ImageButton tvGoToCycles;
	ImageButton tvGoToChart;
	ImageButton tvGoToUstawienia;

	TextView tvDate;
	EditText editTemperature;
	EditText editColor;
	Spinner spStretchability;
	CheckBox ckCircumstances;
	Button btnSave;
	Button btnSaveNewCycle;

	Observation saveObservation;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.starting_activity);
		datasource = new ObservationDataSource(StartingActivity.this);
		datasource.open();
		context = this;
		editTemperature = (EditText) findViewById(R.id.editTemperature);
		editColor = (EditText) findViewById(R.id.editColor);
		spStretchability = (Spinner) findViewById(R.id.spinnerStretchability);
		ckCircumstances = (CheckBox) findViewById(R.id.checkCircumstances);
		btnSave = (Button) findViewById(R.id.buttonSave);
		btnSaveNewCycle = (Button) findViewById(R.id.buttonSaveNewCycle);
		tvGoToCycles = (ImageButton) findViewById(R.id.ibGoToCycles);
		tvGoToUstawienia = (ImageButton) findViewById(R.id.ibGoToSettings);
		tvGoToChart = (ImageButton) findViewById(R.id.ibGoToChart);
		tvDate = (TextView) findViewById(R.id.textViewDate);
		setTitleDatestamp();

		fadeIn.setDuration(1200);
		fadeIn.setFillAfter(true);
		fadeOut.setDuration(1200);
		fadeOut.setFillAfter(true);

		addListenerOnSpinnerItemSelection();

		flingDetector = new FlingDetector(new FlingListener() {
			@Override
			public void onRightToLeft() {
				tvGoToChart.performClick();
			}

			@Override
			public void onLeftToRight() {
				tvGoToCycles.performClick();
			}
		});
		Log.d(NfpPreferences.STARTING_ACTIVITY_TAG, "ON CREATE ");


        boolean development = true;
        DateTime today = new DateTime();

        if (development) {

            for (int i=0;i<30;i++) {
                Observation o = new Observation(today.plusDays(i).getMillis(), 36.6, "blue", 7, "normal");
                datasource.insertObservation(o, false);

            }
        }

	}

	private void setTitleDatestamp() {
		long epoch = System.currentTimeMillis();
		String date = AlarmSupervisor.convertDate(epoch);
		String[] dateSplit = date.split("-");
		String weekday = SettingsActivity.dayOfWeek(date);
		tvDate.setText(dateSplit[2] + "/" + dateSplit[1] + "/" + dateSplit[0]
				+ " - " + weekday);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (flingDetector.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	public void addListenerOnSpinnerItemSelection() {
		spStretchability = (Spinner) findViewById(R.id.spinnerStretchability);
		spStretchability.setOnItemSelectedListener(new SpinnerListener());
	}

	public void onStart() {
		super.onStart();
		Log.d(NfpPreferences.STARTING_ACTIVITY_TAG, "ON START");
		// Toast.makeText(getApplicationContext(), "Tapnij mnie...",
		// Toast.LENGTH_SHORT).show();

		tvGoToUstawienia.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				tvGoToUstawienia.startAnimation(fadeOut);
				Intent settingsIntent = new Intent(StartingActivity.this,
						SettingsActivity.class);
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				StartingActivity.this.startActivity(settingsIntent);
				StartingActivity.this.overridePendingTransition(
						R.anim.ustawienia_fadein, R.anim.ustawienia_fadeout);
				tvGoToUstawienia.startAnimation(fadeIn);

			}
		});

		tvGoToChart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// ustawieniaButton.startAnimation(fadeOut);
				// tvVerse.startAnimation(fadeOut);
				// tvGoToSearch.startAnimation(fadeOut);
				Intent settingsIntent = new Intent(StartingActivity.this,
						ChartActivity.class);
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				StartingActivity.this.startActivity(settingsIntent);
				StartingActivity.this.overridePendingTransition(
						R.anim.settings_enter, R.anim.starting_leave);
				// tvGoToSearch.startAnimation(fadeIn);
				// settingsIntent.putExtra("key", value);

			}
		});

		tvGoToCycles.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				// ustawieniaButton.startAnimation(fadeOut);
				// tvVerse.startAnimation(fadeOut);
				// tvGoToUlubione.startAnimation(fadeOut);
				Intent settingsIntent = new Intent(StartingActivity.this,
						CyclesActivity.class);
				settingsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				StartingActivity.this.startActivity(settingsIntent);
				StartingActivity.this.overridePendingTransition(
						R.anim.ulubione_enter, R.anim.starting_leave_right);
				// tvGoToUlubione.startAnimation(fadeIn);
				// settingsIntent.putExtra("key", value);

			}
		});

		// SAVE
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (createObservation()) {
					saveObservation(false);
				}
			}
		});

		// add button listener
		btnSaveNewCycle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);

				// set title
				alertDialogBuilder.setTitle("New cycle");

				// set dialog message
				alertDialogBuilder
						.setMessage("Do you want to start a new cycle?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										if (createObservation()) {
											saveObservation(true);
										}
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});
		// -----------------------------------------------------------------

	}

	public void saveObservation(boolean isNewCycle) {
		if (datasource.selectObservation(saveObservation) != null) {
			Toast.makeText(StartingActivity.this,
					"Entry for today already exists", Toast.LENGTH_SHORT).show();
			emptyInputs();
		} else {
			datasource.insertObservation(saveObservation, isNewCycle);
			Toast.makeText(StartingActivity.this, "Saved", Toast.LENGTH_SHORT).show();
			emptyInputs();
		}
	}

	private boolean createObservation() {
		double temp = 0;
		try {
			temp = Double.parseDouble(editTemperature.getText().toString().trim());
		} catch (NumberFormatException nfe) {
			Toast.makeText(StartingActivity.this, "Wrong temperature input",Toast.LENGTH_SHORT).show();
			editTemperature.setText("");
		}
		String color = editColor.getText().toString().trim();
		if ("".equals(color)) {
			Toast.makeText(StartingActivity.this, "Wrong color input",Toast.LENGTH_SHORT).show();
			editColor.setText("");
		}
		
		int strechability = Integer.parseInt(spStretchability.getSelectedItem().toString());
		String circumstances;
		if (ckCircumstances.isChecked()) {
			circumstances = "special";
		} else {
			circumstances = "normal";
		}
		long epoch = System.currentTimeMillis();
		if (temp != 0 && !"".equals(color)) {
			saveObservation = new Observation(epoch, temp, color, strechability, circumstances);
			return true;
		} else {
			return false;
		}
	}

	private void emptyInputs() {
		editTemperature.setText("");
		editColor.setText("");
		spStretchability.setSelection(0);
		ckCircumstances.setChecked(false);
		saveObservation = null;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		datasource.close();
		this.finish();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d(NfpPreferences.STARTING_ACTIVITY_TAG, "ON RESUME ");
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.d(NfpPreferences.STARTING_ACTIVITY_TAG, "ON NEW INTENT");
		setIntent(intent);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

}