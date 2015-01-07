package sowa.nfp;

import java.util.ArrayList;
import java.util.List;

import sowa.nfp.db.Cycle;
import sowa.nfp.db.CyclesAdapter;
import sowa.nfp.db.Observation;
import sowa.nfp.db.ObservationAdapter;
import sowa.nfp.db.ObservationDataSource;
import sowa.nfp.sharing.ShareDialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;

import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class CyclesActivity extends Activity {

	final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
	final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

	private ObservationDataSource datasource;
	private Dialog observationsDialog;
	private ListView observationsView;
	private ObservationAdapter observationsAdapter;
	private CyclesAdapter cyclesAdapter;
	ArrayList<Observation> observationsSet;
	private FlingDetector flingDetector;
	private ImageButton btnGoBack;
	private long cycleId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cycle_activity);

		btnGoBack = (ImageButton) findViewById(R.id.btnPrevCycle);

		flingDetector = new FlingDetector(new FlingListener() {
			@Override
			public void onRightToLeft() {
				onBackPressed();
			}

			@Override
			public void onLeftToRight() {
			}
		});

		datasource = new ObservationDataSource(this);
		datasource.open();

		ArrayList<Cycle> cyclesList = datasource.getCycles();
		cyclesAdapter = new CyclesAdapter(this, R.layout.cycle_row, cyclesList);

		ListView lv = (ListView) findViewById(R.id.listView1);
		lv.setAdapter(cyclesAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {
				cycleId = ((Cycle) parent.getItemAtPosition(position)).getId();
				openDialog();
			}
		});

		btnGoBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (flingDetector.onTouchEvent(event)) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	private void openDialog() {
		observationsDialog = new Dialog(CyclesActivity.this);
		observationsDialog.setContentView(R.layout.cycles_dialog);
		Cycle cycle = datasource.selectCycle(cycleId);
		observationsDialog.setTitle(cycle.getStartDate() + " - "
				+ cycle.getEndDate());
		observationsDialog.show();

		observationsSet = datasource.getObservations(cycleId);
		observationsAdapter = new ObservationAdapter(this,
				R.layout.observation_row, observationsSet);
		observationsView = (ListView) observationsDialog
				.findViewById(R.id.listView1);
		observationsView.setAdapter(observationsAdapter);

		ImageButton deleteButton = (ImageButton) observationsDialog
				.findViewById(R.id.deleteBtnFavourites);
		deleteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int plural = 0;
				String msg = NfpPreferences.INFO_WERSETY_DELETED;
				int allFavs = observationsView.getCount();
				for (int i = 0; i < allFavs; i++) {
					Observation observation = (Observation) observationsView
							.getItemAtPosition(i);
					if (observation.isSelected()) {
						datasource.deleteObservation(observation);
						plural++;
					}
				}
				if (plural == 0) {
					msg = NfpPreferences.INFO_CHOOSE_WERSETY_FOR_DELETION;
				} else {
				}
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
						.show();
				observationsSet.clear();
				observationsSet.addAll(datasource.getObservations(cycleId));
				observationsAdapter.notifyDataSetChanged();
				if (observationsView.getCount() < 1) {
					observationsDialog.dismiss();
					cyclesAdapter.notifyDataSetChanged();
				}
			}
		});

		ImageButton shareButton = (ImageButton) observationsDialog
				.findViewById(R.id.shareBtnFavourites);
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int allObs = observationsView.getCount();
				int selected = 0;
				Observation favourite = null;
				List<Observation> favourites = new ArrayList<Observation>();

				for (int i = 0; i < allObs; i++) {
					favourite = (Observation) observationsView
							.getItemAtPosition(i);
					if (favourite.isSelected()) {
						selected++;
						favourites.add(favourite);
					}
				}

				if (selected != 0) {
					if (selected > 1) {
						ShareDialog sd = new ShareDialog(CyclesActivity.this,
								favourites.toArray(new Observation[favourites.size()]));
						sd.show();
					} else {
						ShareDialog sd = new ShareDialog(CyclesActivity.this,
								favourites.toArray(new Observation[favourites.size()]));
						sd.show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Please select an entry", Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		datasource.close();
		this.finish();
		this.overridePendingTransition(R.anim.starting_enter_left,
				R.anim.ulubione_leave);
		return;
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}
