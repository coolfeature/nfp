package ionas.nfp.activities;

import java.util.ArrayList;
import java.util.List;

import ionas.nfp.ui.FlingDetector;
import ionas.nfp.ui.FlingListener;
import ionas.nfp.NfpPreferences;
import ionas.nfp.R;
import ionas.nfp.db.Cycle;
import ionas.nfp.db.CyclesAdapter;
import ionas.nfp.db.Observation;
import ionas.nfp.db.ObservationAdapter;
import ionas.nfp.db.ObservationDataSource;
import ionas.nfp.dialogs.ShareDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

	ObservationDataSource datasource;
	Dialog observationsDialog;
    ListView lvParent;
	ListView observationsView;
	ObservationAdapter observationsAdapter;
	CyclesAdapter cyclesAdapter;
	ArrayList<Observation> observationsSet;
    ArrayList<Cycle> cyclesSet;
	FlingDetector flingDetector;
	ImageButton btnGoBack;
	long cycleId;

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
                chooseFile();
			}
		});

		datasource = new ObservationDataSource(this);
		datasource.open();

		cyclesSet = datasource.getCycles();
		cyclesAdapter = new CyclesAdapter(this, R.layout.cycle_row, cyclesSet);

		lvParent = (ListView) findViewById(R.id.listView1);
		lvParent.setAdapter(cyclesAdapter);
		lvParent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

	private void openDialog() {
		observationsDialog = new Dialog(CyclesActivity.this);
		observationsDialog.setContentView(R.layout.cycles_dialog);
		Cycle cycle = datasource.getCycle(cycleId);
		observationsDialog.setTitle(cycle.getStartDateString() + " - " + cycle.getEndDateString());
		observationsDialog.show();

		observationsSet = datasource.getObservations(cycleId);
		observationsAdapter = new ObservationAdapter(this,R.layout.observation_row, observationsSet);
		observationsView = (ListView) observationsDialog.findViewById(R.id.listView1);
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
					Observation observation = (Observation) observationsView.getItemAtPosition(i);
					if (observation.isSelected()) {
						datasource.deleteObservation(observation);
						plural++;
					}
				}
				if (plural == 0) {
					msg = NfpPreferences.INFO_CHOOSE_WERSETY_FOR_DELETION;
				}
				Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
				observationsSet.clear();
				observationsSet.addAll(datasource.getObservations(cycleId));
				observationsAdapter.notifyDataSetChanged();
				if (observationsView.getCount() < 1) {
					observationsDialog.dismiss();
				}
                cyclesSet.clear();
                cyclesSet.addAll(datasource.getCycles());
                cyclesAdapter.notifyDataSetChanged();
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

    private void chooseFile() {
        Intent intent = new Intent(this, FileChooserActivity.class);
        startActivityForResult(intent, 171);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 171) && (resultCode == -1)) {
            String fileSelected = data.getStringExtra("fileSelected");
            Toast.makeText(this, fileSelected, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (flingDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
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
