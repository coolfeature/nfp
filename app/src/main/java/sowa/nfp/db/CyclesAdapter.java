package sowa.nfp.db;

import java.util.ArrayList;
import java.util.List;

import sowa.nfp.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;


public class CyclesAdapter extends ArrayAdapter<Cycle>  {
	
	List<Cycle> items;
	Context context;
	ObservationDataSource datasource;
	Dialog tagDialog;
	String newTag;
	EditText inputTag;
	
    public CyclesAdapter(Context context, int textViewResourceId, ArrayList<Cycle> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context = context;
            datasource = new ObservationDataSource(context);
    }
    
    static class ViewHolder {
        protected TextView tvId;
        protected TextView tvStartDate;
        protected TextView tvEndDate;
        protected TextView tvLength;

    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	ViewHolder viewHolder = null;
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.cycle_row, null);
                viewHolder = new ViewHolder();
                viewHolder.tvId = (TextView) v.findViewById(R.id.tvId);
                viewHolder.tvStartDate = (TextView) v.findViewById(R.id.tvStartDate);
                viewHolder.tvEndDate = (TextView) v.findViewById(R.id.tvEndDate);
                viewHolder.tvLength = (TextView) v.findViewById(R.id.tvLength);
                 
                v.setTag(viewHolder);
                v.setTag(R.id.tvId, viewHolder.tvId);
                v.setTag(R.id.tvStartDate, viewHolder.tvStartDate);
                v.setTag(R.id.tvEndDate, viewHolder.tvEndDate);
                v.setTag(R.id.tvLength, viewHolder.tvLength);
                } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            
            Cycle f = items.get(position);
            if (f != null) {

                    //cb.setOnCheckedChangeListener(mListener);
                if (viewHolder.tvId != null) {
                	viewHolder.tvId.setText(Long.toString(f.getId()));
                }
                    
                    if (viewHolder.tvStartDate != null) {
                    	viewHolder.tvStartDate.setText(f.getStartDate());
                    }
                    
                    if (viewHolder.tvEndDate != null) {
                    	String endDate = f.getEndDate();
                    	if (endDate != null || !"".equals(endDate))
                    		viewHolder.tvEndDate.setText(endDate);    
                    	else 
                    		viewHolder.tvEndDate.setText("ongoing");  
                    }
                    
                    if (viewHolder.tvLength != null) {
                    	viewHolder.tvLength.setText(Integer.toString(f.getCycleLength()));
                    }  

            }
            return v;
    }
    
    @Override
	public void notifyDataSetChanged() {
    	super.notifyDataSetChanged();
	}
}
