package sowa.nfp.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Cycle {

	private long id;
	private String startDate;
	private String endDate;
	private int cycleLength;
	
	
	public Cycle() {
		this.startDate = "";
		this.endDate = "";
		this.cycleLength = 1;
	}
	public Cycle( String startDate, String endDate) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.cycleLength = getCycleLength();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		if(endDate == null) {
			return "ongoing";
		} else {
			return endDate;
		}
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public int getCycleLength() {
		cycleLength = calculateDayDiff(getStartDate(),getEndDate());
		return cycleLength;
	}
	
	public void setCycleLength(int cycleLength) {
		this.cycleLength = cycleLength;
	}
	
	public static int calculateDayDiff(String date1, String date2) {
		if (date1 != null && date2 != "" && date2 != null && date1 != "" ) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date date1Parsed = null;
			Date date2Parsed = null;
			try {
				date1Parsed = df.parse(date1);
				date2Parsed = df.parse(date2);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			cal1.setTime(date1Parsed);
			cal2.setTime(date2Parsed);
			int numberOfDays = 0;
			while (cal1.before(cal2)) {
				numberOfDays++;
				cal1.add(Calendar.DATE, 1);
			}
			return numberOfDays;
		} else {
			return 0;
		}
	}
	
	public static String shiftDay(String date,int dayShift,String outFormat) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date1Parsed = null;
		try {
			date1Parsed = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1Parsed);
		cal.add(Calendar.DATE, dayShift);
		SimpleDateFormat ft = new SimpleDateFormat(outFormat);
		return ft.format(cal.getTime());
	}
}
