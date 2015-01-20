package ionas.nfp.db;

import java.util.ArrayList;
import java.util.List;

import ionas.nfp.R;
import ionas.nfp.utils.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class CyclesAdapter extends ArrayAdapter<Cycle>  {
	
	List<Cycle> items;
	Context context;
	
    public CyclesAdapter(Context context, int textViewResourceId, ArrayList<Cycle> items) {
            super(context, textViewResourceId, items);
            this.items = items;
            this.context = context;
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
            
        Cycle cycle = items.get(position);
        if (cycle != null) {
            //cb.setOnCheckedChangeListener(mListener);
            if (viewHolder.tvId != null) {
                viewHolder.tvId.setText(Long.toString(cycle.getId()));
            }

            if (viewHolder.tvStartDate != null) {
                viewHolder.tvStartDate.setText(Utils.toDateString(cycle.getStartDate()));
            }

            if (viewHolder.tvEndDate != null) {
                viewHolder.tvEndDate.setText(Utils.toDateString(cycle.getEndDate()));
            }

            if (viewHolder.tvLength != null) {
                viewHolder.tvLength.setText(Integer.toString(cycle.getCycleLength()));
            }
        }
        return v;
    }
}
