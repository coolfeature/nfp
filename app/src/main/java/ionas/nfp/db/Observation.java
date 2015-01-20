package ionas.nfp.db;

import android.util.Log;

import org.joda.time.DateTime;

import ionas.nfp.utils.Utils;

public class Observation {

	long id;
	long epoch;
    DateTime dateTime;
	String datestamp;
	String weekday;
	String timestamp;
	double temperature;
	String mucusColor;
	int mucusStretchability;
	String circumstances;
    int cycleDay;
	int cycleStage;
	long cycleId;
	boolean isSelected;
	
	public Observation() {
		this.epoch = 0;
        this.dateTime = null;
		this.datestamp = "";
		this.weekday = "";
		this.timestamp = "";
		this.temperature = 0.00;
		this.mucusColor = "transparent";
		this.mucusStretchability = 0;
		this.circumstances = "regular";
		this.cycleStage = 0;
        this.cycleDay = 0;
		this.cycleId = 0;
		this.isSelected = false;
	};
	
	public Observation(long epoch, double temperature, String mucusColor, 
			int mucusStretchability, String circumstances) {
		this.epoch = epoch;
        this.dateTime = new DateTime(epoch);
		this.datestamp = Utils.toDateString(epoch);
		this.weekday = Utils.toWeekDay(epoch);
		this.timestamp = Utils.toTimeString(epoch);
		this.temperature = temperature;
		this.mucusColor = mucusColor;
		this.mucusStretchability = mucusStretchability;
		this.circumstances = circumstances;
        this.cycleDay = 0;
		this.cycleStage = 0;
		this.isSelected = false;
	}

    @Override
    public String toString() {
        return "Observation{" +
                "id=" + id +
                ", epoch=" + epoch +
                ", dateTime=" + dateTime +
                ", datestamp='" + datestamp + '\'' +
                ", weekday='" + weekday + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", temperature=" + temperature +
                ", mucusColor='" + mucusColor + '\'' +
                ", mucusStretchability=" + mucusStretchability +
                ", circumstances='" + circumstances + '\'' +
                ", cycleDay=" + cycleDay +
                ", cycleStage=" + cycleStage +
                ", cycleId=" + cycleId +
                ", isSelected=" + isSelected +
                '}';
    }

    public long getId() {
		return id;
	}

    public DateTime getDateTime() { return dateTime; }

    public void setDateTime(DateTime dateTime) { this.dateTime = dateTime; }

    public void setId(long id) {
		this.id = id;
	}

	public long getEpoch() {
		return epoch;
	}

	public void setEpoch(long epoch) {
		this.epoch = epoch;
	}

	public String getDatestamp() {
		return datestamp;
	}

	public void setDatestamp(String datestamp) {
		this.datestamp = datestamp;
	}

	public String getWeekday() {
		return weekday;
	}
	
	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public double getTemperature() {
		return temperature;
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public String getMucusColor() {
		return mucusColor;
	}

	public void setMucusColor(String mucusColor) {
		this.mucusColor = mucusColor;
	}

	public int getMucusStretchability() {
		return mucusStretchability;
	}

	public void setMucusStretchability(int mucusStretchability) {
		this.mucusStretchability = mucusStretchability;
	}

	public String getCircumstances() {
		return circumstances;
	}

	public void setCircumstances(String circumstances) {
		this.circumstances = circumstances;
	}

	public int getCycleStage() {
		return cycleStage;
	}

	public void setCycleStage(int cycleStage) {
		this.cycleStage = cycleStage;
	}

	public long getCycleId() {
		return cycleId;
	}

	public void setCycleId(long cycleId) {
		this.cycleId = cycleId;
	}

    public int getCycleDay() {
        return cycleDay;
    }

    public void setCycleDay(int cycleDay) {
        this.cycleDay = cycleDay;
    }

    public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

    public void print() {
        Log.d("observation", "Observation{" +
                "id=" + id +
                ", epoch=" + epoch +
                ", dateTime=" + dateTime +
                ", datestamp='" + datestamp + '\'' +
                ", weekday='" + weekday + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", temperature=" + temperature +
                ", mucusColor='" + mucusColor + '\'' +
                ", mucusStretchability=" + mucusStretchability +
                ", circumstances='" + circumstances + '\'' +
                ", day='" + cycleDay + '\'' +
                ", cycleStage=" + cycleStage +
                ", cycleId=" + cycleId +
                ", isSelected=" + isSelected +
                '}');
    }
}

