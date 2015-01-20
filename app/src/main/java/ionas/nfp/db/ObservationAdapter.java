package ionas.nfp.db;

import java.util.ArrayList;
import java.util.List;

import ionas.nfp.R;

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
        protected TextView tvCycleDay;
        protected TextView tvTemperature;
        protected TextView tvColor;
        protected TextView tvStretchability;
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
            viewHolder.tvCircumstances = (TextView) v.findViewById(R.id.tvCircumstancesVal);
            viewHolder.tvCycleDay = (TextView) v.findViewById(R.id.tvCycleDayVal);

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
            v.setTag(R.id.tvCircumstancesVal, viewHolder.tvCircumstances);
            v.setTag(R.id.tvCycleDayVal, viewHolder.tvCycleDay);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
  
        Observation o = items.get(position);
        if (o != null) {
            if (viewHolder.ckBox != null) {
                viewHolder.ckBox.setTag(Integer.valueOf(position));
                viewHolder.ckBox.setText(o.getDatestamp() + " " + o.getWeekday());
                viewHolder.ckBox.setChecked(items.get(position).isSelected());
            }

            if (viewHolder.tvTemperature != null) {
                viewHolder.tvTemperature.setText(Double.toString(o.getTemperature()));
            }

            if (viewHolder.tvColor != null) {
                viewHolder.tvColor.setText(o.getMucusColor());
            }

            if (viewHolder.tvStretchability != null) {
                viewHolder.tvStretchability.setText(Integer.toString(o.getMucusStretchability()));
            }

            if (viewHolder.tvCircumstances != null) {
                viewHolder.tvCircumstances.setText(o.getCircumstances());
            }

            if (viewHolder.tvCycleDay != null) {
                viewHolder.tvCycleDay.setText(Integer.toString(o.getCycleDay()));
            }

            viewHolder.ckBox.setTag(position); // This line is important.
        }
        return v;
    }
}
