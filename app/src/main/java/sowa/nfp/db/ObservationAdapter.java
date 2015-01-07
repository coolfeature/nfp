package sowa.nfp.db;

import java.util.ArrayList;
import java.util.List;

import sowa.nfp.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

public class ObservationAdapter extends ArrayAdapter<Observation>  {
	private List<Observation> items;
	private Context context;

    public ObservationAdapter(Context context, int textViewResourceId, ArrayList<Observation> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context = context;
    }

    static class ViewHolder {
    	protected CheckBox ckBox;
        protected TextView tvTemperature;
        protected TextView tvColor;
        protected TextView tvStretchability;
        protected TextView tvDensity;
        protected TextView tvCircumstances;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ViewHolder viewHolder = null;
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.observation_row, null);
                viewHolder = new ViewHolder();
                viewHolder.ckBox = (CheckBox) v.findViewById(R.id.ckBox);
                viewHolder.tvTemperature = (TextView) v.findViewById(R.id.tvTemperatureVal);
                viewHolder.tvColor = (TextView) v.findViewById(R.id.tvColorVal);
                viewHolder.tvStretchability = (TextView) v.findViewById(R.id.tvStretchabilityVal);
                viewHolder.tvDensity = (TextView) v.findViewById(R.id.tvDensityVal);
                viewHolder.tvCircumstances = (TextView) v.findViewById(R.id.tvCircumstancesVal);
 
                viewHolder.ckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.
                        items.get(getPosition).setSelected(buttonView.isChecked()); // Set the value of checkbox to maintain its state.
                    }
                });
                v.setTag(viewHolder);
                v.setTag(R.id.ckBox, viewHolder.ckBox);
                v.setTag(R.id.tvTemperatureVal, viewHolder.tvTemperature);
                v.setTag(R.id.tvColorVal, viewHolder.tvColor);
                v.setTag(R.id.tvStretchabilityVal, viewHolder.tvStretchability);
                v.setTag(R.id.tvDensityVal, viewHolder.tvDensity);
                v.setTag(R.id.tvCircumstancesVal, viewHolder.tvCircumstances);
                } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
  
            Observation f = items.get(position);
            if (f != null) {

                    //cb.setOnCheckedChangeListener(mListener);
                    
                    if (viewHolder.ckBox != null) {
                    	viewHolder.ckBox.setTag(Integer.valueOf(position));
                    	viewHolder.ckBox.setText(f.getDatestamp() + " " + f.getWeekday());           
                    	viewHolder.ckBox.setChecked(items.get(position).isSelected());
                    }
                    
                    if (viewHolder.tvTemperature != null) {
                    	viewHolder.tvTemperature.setText(Double.toString(f.getTemperature()));                            
                    }
                    
                    if (viewHolder.tvColor != null) {
                    	viewHolder.tvColor.setText(f.getMucusColor());                            
                    }
                    
                    if (viewHolder.tvStretchability != null) {
                    	viewHolder.tvStretchability.setText(Integer.toString(f.getMucusStretchability()));                            
                    }
                    
                    if (viewHolder.tvCircumstances != null) {
                    	viewHolder.tvCircumstances.setText(f.getCircumstances());                            
                    } 
                    
                    viewHolder.ckBox.setTag(position); // This line is important.    
            }
            return v;
    }
}
