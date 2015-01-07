package sowa.nfp.db;

public class ObservationEntry {

	private long observationId; 
	private String weekday;
	private String date;
	private long cycleId;
	
	public ObservationEntry(long observationId, String weekday, String date, long cycleId) {
		this.observationId = observationId;
		this.weekday = weekday;
		this.date = date;
		this.cycleId = cycleId;
	}

	public long getObservationId() {
		return observationId;
	}

	public void setObservationId(long observationId) {
		this.observationId = observationId;
	}

	public String getWeekday() {
		return weekday;
	}

	public void setWeekday(String weekday) {
		this.weekday = weekday;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getCycleId() {
		return cycleId;
	}

	public void setCycleId(long cycleId) {
		this.cycleId = cycleId;
	}

}
