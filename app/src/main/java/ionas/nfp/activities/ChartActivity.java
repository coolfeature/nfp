package ionas.nfp.activities;

import ionas.nfp.R;
import ionas.nfp.db.Observation;
import ionas.nfp.db.ObservationDataSource;
import ionas.nfp.dialogs.ObservationDialog;
import ionas.nfp.utils.CycleNavigator;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class ChartActivity extends Activity implements OnChartValueSelectedListener {

    public final static String KEY_CYCLE_NAVIGATOR = "CYCLE_NAVIGATOR";

    CycleNavigator cycleNavigator = null;

	final static int DEFAULT_CYCLE_LENGTH = 30;
	final static double MIN_TEMP = 34.00;
	final static double MAX_TEMP = 40.00;
	final static String TEMPERATURE = "Temperature";
	final static String ELASTICITY = "Elasticity";

	final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
	final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

	ObservationDataSource datasource;
    ArrayList<Observation> observations;

	ImageButton btnGoBack;
	ImageButton btnPrevCycle;
	ImageButton btnNextCycle;
	LinearLayout chartLayout;
    LineChart chart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.chart_activity);

        datasource = new ObservationDataSource(ChartActivity.this);
        datasource.open();

        setUpNavigator();
        setControls();

        chartLayout = (LinearLayout) findViewById(R.id.chart);
        chart = new LineChart(this);
        chart.setYRange((float)ChartActivity.MIN_TEMP,(float)ChartActivity.MAX_TEMP,true);
        chartLayout.addView(chart);
        //chartLayout.addView(chart, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        chart.setOnChartValueSelectedListener(this);
        chart.setDrawGridBackground(false);
        // mChart.setStartAtZero(true);
        // disable the drawing of values into the chart
        chart.setDrawYValues(false);
        // enable value highlighting
        chart.setHighlightEnabled(true);
        // enable touch gestures
        chart.setTouchEnabled(true);
        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        if (cycleNavigator.getCurrent() != null) {
            paintChart();
        } else {
            Toast.makeText(ChartActivity.this,
                    "No observations saved yet.", Toast.LENGTH_SHORT).show();
        }

	}

    public void paintChart() {
        observations = datasource.getObservations(cycleNavigator.getCurrent().getId());
        int xSize = observations.size();
        Log.d(ChartActivity.class.getName(),"SIZE: " + Integer.toString(xSize));
        //Determine the size of the X axis
        if (xSize <= ChartActivity.DEFAULT_CYCLE_LENGTH) {
            xSize = ChartActivity.DEFAULT_CYCLE_LENGTH;
        }
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 1; i <= xSize; i++) {
            xVals.add((i) + "");
        }

        // Create the data sets
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        // ------------------------------------------------------------------------------
        // Create the temperature data set
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (Observation o : observations) {
            /*
             * The goal here is to map the elasticity value fromm the array of
             * Strings specified on the man Activity for the user UI (Spinner)
             * stored in R.array.mucus_degree to a temperature so that the value of
             * constant MAX_TEMP maps to the highest value from the mucus_degree array.
             *
             */
            float elasticityRange = (float)getElasticityArray().length;
            float stretchability = (float)o.getMucusStretchability();
            float tempRange = (float)(ChartActivity.MAX_TEMP - ChartActivity.MIN_TEMP);
            float proportion = ((stretchability * tempRange) / elasticityRange);
            float mappingTemp = proportion + (float)ChartActivity.MIN_TEMP;

            /*Log.d("Mapping Temp: ","Elasticity Range - " + elasticityRange
                    + " Stretchability - " + stretchability
                    + " temp range - " +  tempRange
                    + "RESULT - " + mappingTemp);*/

            values.add(new Entry(mappingTemp, o.getCycleDay()));
        }
        LineDataSet d = new LineDataSet(values,ChartActivity.ELASTICITY);
        d.setDrawCubic(true);
        d.setCubicIntensity(0.2f);
        d.setDrawFilled(true);
        d.setDrawCircles(false);
        d.setLineWidth(2f);
        d.setCircleSize(4f);
        int color = mColors[1 % mColors.length];
        d.setColor(color);
        d.setCircleColor(color);
        dataSets.add(d);
        // ------------------------------------------------------------------------------

        // Create the temperature data set
        values = new ArrayList<Entry>();
        for (Observation o : observations) {
            values.add(new Entry((float) o.getTemperature(), o.getCycleDay()));
        }
        d = new LineDataSet(values,ChartActivity.TEMPERATURE);
        d.setLineWidth(4f);
        d.setCircleSize(5f);
        color = mColors[0 % mColors.length];
        d.setColor(color);
        d.setCircleColor(color);
        dataSets.add(d);
        // ------------------------------------------------------------------------------

        // dataSets.get(0).enableDashedLine(10, 10, 0);
        //dataSets.get(0).setColors(ColorTemplate.VORDIPLOM_COLORS);
        //dataSets.get(0).setCircleColors(ColorTemplate.VORDIPLOM_COLORS);
        LineData data = new LineData(xVals, dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    private int[] mColors = new int[] {
        ColorTemplate.JOYFUL_COLORS[0],
        ColorTemplate.VORDIPLOM_COLORS[1],
        ColorTemplate.VORDIPLOM_COLORS[2]
    };

    public int[] getElasticityArray() {
        Resources res = getResources();
        String[] strDegrees = res.getStringArray(R.array.mucus_degree);
        int[] degrees = new int[strDegrees.length];
        for (int i=0;i<strDegrees.length;i++) {
            degrees[i] = Integer.parseInt(strDegrees[i]);
        }
        return degrees;
    }

	private void setControls() {
		btnGoBack = (ImageButton) findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

        btnPrevCycle = (ImageButton) findViewById(R.id.btnPrevCycle);
        if (cycleNavigator.getPrevious() != null) {
            btnPrevCycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cycleNavigator.setCurrent(cycleNavigator.getPrevious());
                    finish();
                    startActivity(getIntent().putExtra(
                            ChartActivity.KEY_CYCLE_NAVIGATOR,cycleNavigator));
                }
            });
        } else {
            btnPrevCycle.setVisibility(View.GONE);
        }

        btnNextCycle = (ImageButton) findViewById(R.id.btnNextCycle);
        if (cycleNavigator.getNext() != null) {
            btnNextCycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cycleNavigator.setCurrent(cycleNavigator.getNext());
                    finish();
                    startActivity(getIntent().putExtra(
                            ChartActivity.KEY_CYCLE_NAVIGATOR,cycleNavigator));
                }
            });
        } else {
            btnNextCycle.setVisibility(View.GONE);
        }
	}


    private void setUpNavigator() {
        cycleNavigator = new CycleNavigator();
        if (getIntent().hasExtra(ChartActivity.KEY_CYCLE_NAVIGATOR)) {
            Log.d(ChartActivity.class.getName(),"INTENT DOES HAVE THE NAVIGATOR MAP!");
            cycleNavigator = (CycleNavigator)
                    getIntent().getSerializableExtra(ChartActivity.KEY_CYCLE_NAVIGATOR);
        } else {
            Log.d(ChartActivity.class.getName(),"INTENT DOES NOT HAVE THE NAVIGATOR MAP!");
        }
        cycleNavigator = datasource.getCycleNavigator(cycleNavigator);
        Log.d(ChartActivity.class.getName(),cycleNavigator.toString());
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex) {
        long cid = cycleNavigator.getCurrent().getId();
        Observation o = datasource.getObservation(cid,e.getXIndex());
        if (o != null) {
            Log.i("VAL SELECTED", o.toString());
            ObservationDialog dialog = new ObservationDialog(this,o);
            dialog.show();
        } else {
            Log.i("Val","val: " + e.getVal() + " X index: " + e.getXIndex() + " data set index  " + dataSetIndex);
        }
    }

    @Override
    public void onNothingSelected() {

    }

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
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
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
	}

}
