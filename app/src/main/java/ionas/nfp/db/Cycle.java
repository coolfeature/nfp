package ionas.nfp.db;

import android.util.Log;
import org.joda.time.DateTime;
import java.io.Serializable;
import ionas.nfp.utils.Utils;

public class Cycle implements Serializable {

	private long id;
	private DateTime startDate;
	private DateTime endDate;
	private int cycleLength;

	public Cycle() {
		this.startDate = null;
		this.endDate = null;
		this.cycleLength = 1;
	}
	public Cycle( DateTime startDate, DateTime endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.cycleLength = getCycleLength();
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DateTime getStartDate() {
		return this.startDate;
	}

    public String getStartDateString() { return Utils.toDateString(this.startDate); }

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public DateTime getEndDate() { return this.endDate; }

    public String getEndDateString() { return Utils.toDateString(this.endDate); }

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}
	
	public int getCycleLength() {
        return Utils.daysSpanning(this.getStartDate(), this.getEndDate());
	}
	
	public void setCycleLength(int cycleLength) {
		this.cycleLength = cycleLength;
	}

    public void print() {
        Log.d("cycle","Cycle{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", cycleLength=" + cycleLength +
                '}');
    }

    @Override
    public String toString() {
        return "Cycle{" +
                "id=" + id +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", cycleLength=" + cycleLength +
                '}';
    }
}
