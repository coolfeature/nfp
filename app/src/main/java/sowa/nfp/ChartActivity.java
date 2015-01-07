package sowa.nfp;

import java.util.ArrayList;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import sowa.nfp.db.Cycle;
import sowa.nfp.db.DatabaseHelper;
import sowa.nfp.db.ObservationDataSource;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class ChartActivity extends Activity {

	final static int DEFAULT_PERIOD_LENGTH = 30;
	final static double MIN_TEMP = 34.00;
	final static double MAX_TEMP = 38.00;	
	final static String TEMPERATURE = "Temperature";
	final static String ELASTICITY = "Elasticity";

	final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
	final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);

	ObservationDataSource datasource;
	FlingDetector flingDetector;
	XYMultipleSeriesRenderer renderer;
	ImageButton btnGoBack;
	ImageButton btnPrevCycle;
	ImageButton btnNextCycle;
	GraphicalView mChartView;
	LinearLayout chartLayout;
	long cycleId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		datasource = new ObservationDataSource(ChartActivity.this);
		datasource.open();
		setContentView(R.layout.chart_activity);
		setControls();
		cycleId = datasource.getMaxId(DatabaseHelper.TABLE_CYCLE);

		// Chart
		buildRenderer();
		paintChart();
	}

	private void paintChart() {
		chartLayout = (LinearLayout) findViewById(R.id.chart);
		if (mChartView == null) {
			mChartView = ChartFactory.getLineChartView(this,getDataSet(), renderer);
			chartLayout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		} else {
			mChartView.repaint();
		}
	}
	
	private XYMultipleSeriesRenderer buildRenderer() {
		renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		// renderer2.setMargins(new int[] { 20, 30, 15, 20 });
		renderer.setMargins(new int[] { 25, 35, 10, 15 });
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.CIRCLE };
		int[] lineColours = new int[] { Color.BLUE, Color.RED  };
		for (int i = 0; i < lineColours.length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(lineColours[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
		}
		// renderer.setXLabels(12);
		renderer.setYLabels(10);
		renderer.setXLabels(0);
		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setMargins(new int[] { 25, 35, 10, 15 });
		// renderer.setZoomButtonsVisible(true);
		//renderer.setPanLimits(new double[] { 0, 20, 0, 40 });
		// renderer.setZoomLimits(new double[] { 0, 20, 0, 40 });
		//renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);
		renderer.setMarginsColor(Color.parseColor("#EEEDED"));
		renderer.setXLabelsColor(Color.BLACK);
		renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.parseColor("#FBFBFC"));
		renderer.setShowLegend(false);
		renderer.setAxesColor(Color.LTGRAY);
		renderer.setLabelsColor(Color.LTGRAY);
		renderer.setYAxisMin(MIN_TEMP);
		renderer.setYAxisMax(MAX_TEMP);
		renderer.setYTitle(TEMPERATURE + " and " + ELASTICITY);
		return renderer;
	}
	
	private XYMultipleSeriesDataset getDataSet() {
		// x SERIES
		Double[] x = null;
		// y SERIES
		Double[] y = null;
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		ArrayList<Double> temperature = datasource.getTemperature(cycleId);
		x = intXSeq(temperature.size());
		y = temperature.toArray(new Double[temperature.size()]);
		XYSeries series = new XYSeries(TEMPERATURE, 0);
		for (int k = 0; k < y.length; k++) {
			series.add(x[k], y[k]);
		}
		dataset.addSeries(series);
		ArrayList<Double> elasticity = datasource.getElasticity(cycleId);
		x = intXSeq(elasticity.size());
		y = mapElasticityToTemp(elasticity.toArray(new Double[elasticity.size()]));
		series = new XYSeries(ELASTICITY, 0);
		for (int k = 0; k < y.length; k++) {
			series.add(x[k], y[k]);
		}
		dataset.addSeries(series);
		
		ArrayList<String> cycleDates = datasource.getCycleDates(cycleId);
		String[] dates = supplementDates(cycleDates.toArray(new String[cycleDates.size()]));
		
		renderer.setXTitle("Date");
		for (int k = 0; k < x.length; k++) {
			if ((k % 5) == 0) {
				renderer.addXTextLabel(k, dates[k]);
			}
		}
		return dataset;
	}

	private void setControls() {
		flingDetector = new FlingDetector(new FlingListener() {
			@Override
			public void onRightToLeft() {
				//
			}

			@Override
			public void onLeftToRight() {
				//
			}
		});

		btnGoBack = (ImageButton) findViewById(R.id.btnGoBack);
		btnGoBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		btnPrevCycle = (ImageButton) findViewById(R.id.btnPrevCycle);
		btnPrevCycle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//
			}
		});
		
		btnNextCycle = (ImageButton) findViewById(R.id.btnNextCycle);
		btnNextCycle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//
			}
		});
	}

	
	private String[] supplementDates(String[] dates) {
		if (dates.length < DEFAULT_PERIOD_LENGTH) {
			String[] newDates = new String[DEFAULT_PERIOD_LENGTH];
			System.arraycopy(dates, 0, newDates, 0, dates.length);
			dates = newDates;
			for (int i=0;i<dates.length;i++) {
				if (dates[i] == null) {
					dates[i] = Cycle.shiftDay(dates[i-1],1,"yyyy-MM-dd");
				} 
			}
			for (int i=0;i<dates.length;i++) {
				dates[i] = Cycle.shiftDay(dates[i],0,"d-MM");
			}
			return dates;
		} else {
			return dates;
		}
	}
	
	private Double[] intXSeq(int length) {
		renderer.setXAxisMin(0);
		if (length > DEFAULT_PERIOD_LENGTH) {
			renderer.setXAxisMax(length);
			return intSeq(length);
		} else {
			renderer.setXAxisMax(DEFAULT_PERIOD_LENGTH);
			return intSeq(DEFAULT_PERIOD_LENGTH);
		}
	}

	private Double[] mapElasticityToTemp(Double[] elasticity) {
		Double[] elaticityConverted = new Double[elasticity.length];
		for (int i=0;i<elasticity.length;i++) {
			elaticityConverted[i] = (MIN_TEMP + (elasticity[i] * ((MAX_TEMP - MIN_TEMP)/10)));
		}
		return elaticityConverted;
	}
	
	private Double[] intSeq(int length) {
		Double[] array = new Double[length];
		for (int i = 0; i < length; i++) {
			array[i] = ((double) i) + 1;
		}
		return array;
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
