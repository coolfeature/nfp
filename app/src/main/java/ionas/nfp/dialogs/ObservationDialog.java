package ionas.nfp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import ionas.nfp.R;
import ionas.nfp.db.Observation;

/**
 * Created by sczaja on 11/01/2015.
 */
public class ObservationDialog extends Dialog {

    Observation observation;
    Context context;

    TextView tvCycleDayVal;
    TextView tvCircumstancesVal;
    TextView tvColorVal;
    TextView tvStretchabilityVal;
    TextView tvTemperatureVal;

    public ObservationDialog(Context context, Observation observation) {
        super(context);
        this.context = context;
        this.observation = observation;
        this.setContentView(R.layout.observation_dialog);
        this.setTitle(observation.getDatestamp());

        tvCycleDayVal = (TextView) findViewById(R.id.tvCycleDayVal);
        tvCycleDayVal.setText(Integer.toString(observation.getCycleDay()));

        tvCircumstancesVal = (TextView) findViewById(R.id.tvCircumstancesVal);
        tvCircumstancesVal.setText(observation.getCircumstances());

        tvColorVal = (TextView) findViewById(R.id.tvColorVal);
        tvColorVal.setText(observation.getMucusColor());

        tvStretchabilityVal = (TextView) findViewById(R.id.tvStretchabilityVal);
        tvStretchabilityVal.setText(Integer.toString(observation.getMucusStretchability()));

        tvTemperatureVal = (TextView) findViewById(R.id.tvTemperatureVal);
        tvTemperatureVal.setText(Double.toString(observation.getTemperature()));
    }
}
