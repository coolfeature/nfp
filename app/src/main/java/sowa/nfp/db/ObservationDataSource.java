package sowa.nfp.db;


import java.util.ArrayList;
import sowa.nfp.notification.AlarmSupervisor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ObservationDataSource {
	// Database fields
	private static final String NO_ENTRIES = "Brak wpisów";
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] observationSet = { 
			DatabaseHelper.COLUMN_OBSERVATION_ID,
			DatabaseHelper.COLUMN_OBSERVATION_EPOCH,
			DatabaseHelper.COLUMN_OBSERVATION_DATESTAMP,
			DatabaseHelper.COLUMN_OBSERVATION_WEEKDAY,
			DatabaseHelper.COLUMN_OBSERVATION_TIMESTAMP,
			DatabaseHelper.COLUMN_OBSERVATION_TEMPERATURE,
			DatabaseHelper.COLUMN_OBSERVATION_MUCUS_COLOR,
			DatabaseHelper.COLUMN_OBSERVATION_MUCUS_STRETCHABILITY,
			DatabaseHelper.COLUMN_OBSERVATION_CIRCUMSTANCES,
			DatabaseHelper.COLUMN_OBSERVATION_CYCLE_STAGE,
			DatabaseHelper.COLUMN_OBSERVATION_CYCLE_ID 
	};

	private String[] cycleSet = { 
			DatabaseHelper.COLUMN_CYCLE_ID,
			DatabaseHelper.COLUMN_CYCLE_START_DATE,
			DatabaseHelper.COLUMN_CYCLE_END_DATE,
			DatabaseHelper.COLUMN_CYCLE_LENGTH
	};

	public ObservationDataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public void insertObservation(Observation observation, boolean isNewCycle) {
		if (isNewCycle) {
			updateCycleEndDate(observation);
			insertCycle(new Cycle(observation.getDatestamp(),observation.getDatestamp()));
			insertObservation(observation);
		} else {
			insertObservation(observation);
		}
	}
	
	private long insertObservation(Observation observation) { 
		if (selectObservation(observation) == null) {
			// check if first ever
			if (getMaxId(DatabaseHelper.TABLE_CYCLE) == 0) {
				insertCycle(new Cycle(observation.getDatestamp(),observation.getDatestamp()));
			}
			updateCycleLength(observation);
			ContentValues values = new ContentValues();
			values.put(DatabaseHelper.COLUMN_OBSERVATION_EPOCH,
					observation.getEpoch());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_DATESTAMP,
					observation.getDatestamp());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_WEEKDAY,
					observation.getWeekday());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_TIMESTAMP,
					observation.getTimestamp());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_TEMPERATURE,
					observation.getTemperature());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_MUCUS_COLOR,
					observation.getMucusColor());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_MUCUS_STRETCHABILITY,
					observation.getMucusStretchability());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_CIRCUMSTANCES,
					observation.getCircumstances());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_CYCLE_STAGE,
					observation.getCycleStage());
			values.put(DatabaseHelper.COLUMN_OBSERVATION_CYCLE_ID,
					getMaxId(DatabaseHelper.TABLE_CYCLE));
			return database.insert(DatabaseHelper.TABLE_OBSERVATION, null,
					values);
		} else {
			return 0;
		}
	}

	private long insertCycle(Cycle cycle) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.COLUMN_CYCLE_START_DATE, cycle.getStartDate());
		values.put(DatabaseHelper.COLUMN_CYCLE_END_DATE, cycle.getEndDate());
		values.put(DatabaseHelper.COLUMN_CYCLE_LENGTH, cycle.getCycleLength());
		return database.insert(DatabaseHelper.TABLE_CYCLE, null, values);
	}
	
	
	
	private int updateCycleLength(Observation observation) {
		long id = getMaxId(DatabaseHelper.TABLE_CYCLE);
		int length = Cycle.calculateDayDiff(selectCycle(id).getStartDate(),observation.getDatestamp());	
		String whereClouse = DatabaseHelper.COLUMN_CYCLE_ID + " = " + Long.toString(id);
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.COLUMN_CYCLE_LENGTH, length);
		return database.update(DatabaseHelper.TABLE_CYCLE,cv,whereClouse,null);	
	}
	
	private int updateCycleEndDate(Observation observation) {
		long id = getMaxId(DatabaseHelper.TABLE_CYCLE);
		String endDate = Cycle.shiftDay(observation.getDatestamp(),-1,"yyyy-MM-dd");
		int length = Cycle.calculateDayDiff(selectCycle(id).getStartDate(),observation.getDatestamp());	
		String whereClouse = DatabaseHelper.COLUMN_CYCLE_ID + " = " + Long.toString(id);
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.COLUMN_CYCLE_END_DATE, endDate);
		cv.put(DatabaseHelper.COLUMN_CYCLE_LENGTH, length);
		return database.update(DatabaseHelper.TABLE_CYCLE,cv,whereClouse,null);	
	}

	public Observation selectObservation(Observation observation) {
		String whereClouse = DatabaseHelper.COLUMN_OBSERVATION_DATESTAMP
				+ " = '" + AlarmSupervisor.convertDate(observation.getEpoch())
				+ "'";
		Cursor cursor = database.query(DatabaseHelper.TABLE_OBSERVATION,
				observationSet, whereClouse, null, null, null, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			Observation newObservation = cursorToObservation(cursor);
			cursor.close();
			return newObservation;
		} else {
			cursor.close();
			return null;
		}
	}

	private Cycle selectCycle(Cycle cycle) {
		String whereClouse = DatabaseHelper.COLUMN_CYCLE_START_DATE + " = '"
				+ cycle.getStartDate() + " AND "
				+ DatabaseHelper.COLUMN_CYCLE_END_DATE + " = '"
				+ cycle.getEndDate() + "'";
		Cursor cursor = database.query(DatabaseHelper.TABLE_CYCLE, cycleSet,
				whereClouse, null, null, null, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			Cycle newCycle = cursorToCycle(cursor);
			cursor.close();
			return newCycle;
		} else {
			cursor.close();
			return null;
		}
	}
	
	public Cycle selectCycle(long id) {
		String whereClouse = DatabaseHelper.COLUMN_CYCLE_ID + " = " + Long.toString(id);
		Cursor cursor = database.query(DatabaseHelper.TABLE_CYCLE, cycleSet,
				whereClouse, null, null, null, null);
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			Cycle newCycle = cursorToCycle(cursor);
			cursor.close();
			return newCycle;
		} else {
			cursor.close();
			return null;
		}
	}

	public long getMaxId(String table) {
		String query = "SELECT MAX(_id) AS max_id FROM " + table;
		Cursor cursor = database.rawQuery(query, null);
		long id = 0;
		if (cursor != null) {
			cursor.moveToFirst();
			if (cursor.getLong(0) != 0) {
				id = cursor.getLong(0);
			} 
		} 
		cursor.close();
		return id;
	}

	public void deleteObservation(Observation observation) {
		long cycleId = observation.getCycleId();
		database.delete(
				DatabaseHelper.TABLE_OBSERVATION,
				DatabaseHelper.COLUMN_OBSERVATION_ID + " = "
						+ Long.toString(observation.getId()), null);	
		Log.d("ObservationID: ", observation.toString());
		if (getObservations(cycleId).size() == 0) {
			Log.d(" OBSERVATIONS size was  ", "0");
			database.delete(DatabaseHelper.TABLE_CYCLE,
					DatabaseHelper.COLUMN_CYCLE_ID + " = " + Long.toString(cycleId), null);
		}
		
	}

	private void deleteCycle(Cycle cycle) {
		database.delete(
				DatabaseHelper.TABLE_OBSERVATION,
				DatabaseHelper.COLUMN_OBSERVATION_CYCLE_ID + " = "
						+ cycle.getId(), null);
		// Log.d("CycleID: ", Long.toString(cycle.getId()));
		database.delete(DatabaseHelper.TABLE_CYCLE,
				DatabaseHelper.COLUMN_CYCLE_ID + " = " + Long.toString(cycle.getId()), null);
	}

	public ArrayList<String> getObservationDates() {
		ArrayList<String> dates = new ArrayList<String>();
		String orderBy = DatabaseHelper.COLUMN_OBSERVATION_EPOCH + " DESC";
		String groupBy = DatabaseHelper.COLUMN_OBSERVATION_DATESTAMP;
		Cursor cursor = database.query(DatabaseHelper.TABLE_OBSERVATION,
				new String[] { DatabaseHelper.COLUMN_OBSERVATION_DATESTAMP,
						DatabaseHelper.COLUMN_OBSERVATION_EPOCH }, null, null,
				groupBy, null, orderBy);
		if (cursor.getCount() == 0) {
			dates.add(NO_ENTRIES);
		} else {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String date = cursor.getString(1);
				dates.add(date);
				cursor.moveToNext();
			}
		}
		cursor.close();
		return dates;
	}

	public ArrayList<Observation> getObservations(long id) {
		ArrayList<Observation> observations = new ArrayList<Observation>();
		String where = DatabaseHelper.COLUMN_OBSERVATION_CYCLE_ID + " = " + Long.toString(id);
		Cursor cursor = database.query(DatabaseHelper.TABLE_OBSERVATION,
				observationSet, where, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Observation observation = cursorToObservation(cursor);
			observations.add(observation);
			cursor.moveToNext();
		}
		cursor.close();
		return observations;
	}

	public ArrayList<Double> getTemperature(long cycleId) {
		ArrayList<Double> temperatures = new ArrayList<Double>();
		String where = DatabaseHelper.COLUMN_OBSERVATION_CYCLE_ID + " = " + Long.toString(cycleId);
		Cursor cursor = database.query(DatabaseHelper.TABLE_OBSERVATION,
				observationSet, where, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Observation observation = cursorToObservation(cursor);
			temperatures.add(observation.getTemperature());
			cursor.moveToNext();
		}
		cursor.close();
		return temperatures;
	}
	
	public ArrayList<Double> getElasticity(long cycleId) {
		ArrayList<Double> elasticity = new ArrayList<Double>();
		String where = DatabaseHelper.COLUMN_OBSERVATION_CYCLE_ID + " = " + Long.toString(cycleId);
		Cursor cursor = database.query(DatabaseHelper.TABLE_OBSERVATION,
				observationSet, where, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Observation observation = cursorToObservation(cursor);
			elasticity.add((double) observation.getMucusStretchability());
			cursor.moveToNext();
		}
		cursor.close();
		return elasticity;
	}
	
	public ArrayList<String> getCycleDates(long cycleId) {
		ArrayList<String> dates = new ArrayList<String>();
		String where = DatabaseHelper.COLUMN_OBSERVATION_CYCLE_ID + " = " + Long.toString(cycleId);
		Cursor cursor = database.query(DatabaseHelper.TABLE_OBSERVATION,
				observationSet, where, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Observation observation = cursorToObservation(cursor);
			dates.add(observation.getDatestamp());
			cursor.moveToNext();
		}
		cursor.close();
		return dates;
	}
	
	public ArrayList<Long> getCycleIds() {
		ArrayList<Long> ids = new ArrayList<Long>();
		Cursor cursor = database.query(DatabaseHelper.TABLE_CYCLE,
				cycleSet, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Cycle cycle = cursorToCycle(cursor);
			ids.add(cycle.getId());
			cursor.moveToNext();
		}
		cursor.close();
		return ids;
	}
	
	private Observation cursorToObservation(Cursor cursor) {
		Observation observation = new Observation();
		observation.setId(cursor.getLong(0));
		observation.setEpoch(cursor.getLong(1));
		observation.setDatestamp(cursor.getString(2));
		observation.setWeekday(cursor.getString(3));
		observation.setTimestamp(cursor.getString(4));
		observation.setTemperature(cursor.getDouble(5));
		observation.setMucusColor(cursor.getString(6));
		observation.setMucusStretchability(cursor.getInt(7));
		observation.setCircumstances(cursor.getString(8));
		observation.setCycleStage(cursor.getInt(9));
		observation.setCycleId(cursor.getInt(10));
		return observation;
	}

	private Cycle cursorToCycle(Cursor cursor) {
		Cycle cycle = new Cycle();
		cycle.setId(cursor.getLong(0));
		cycle.setStartDate(cursor.getString(1));
		cycle.setEndDate(cursor.getString(2));
		cycle.setCycleLength(cursor.getInt(3));
		return cycle;
	}

	public ArrayList<Cycle> getCycles() {
		ArrayList<Cycle> cycles = new ArrayList<Cycle>();
		Cursor cursor = database.query(DatabaseHelper.TABLE_CYCLE, cycleSet,
				null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Cycle cycle = cursorToCycle(cursor);
			cycles.add(cycle);
			cursor.moveToNext();
		}
		cursor.close();
		return cycles;
	}
}
