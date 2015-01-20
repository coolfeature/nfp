package ionas.nfp.db;

import java.util.ArrayList;

import ionas.nfp.utils.CycleNavigator;
import ionas.nfp.utils.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.joda.time.DateTime;

/**
 * This class bridges SQL and Java data structures.
 */

public class ObservationDataSource {
	// Database fields
	private static final String NO_ENTRIES = "Brak wpisów";
	private SQLiteDatabase database;
	private DatabaseBuilder dbHelper;

	private String[] observationSet = { 
        DatabaseBuilder.COLUMN_OBSERVATION_ID,
		DatabaseBuilder.COLUMN_OBSERVATION_EPOCH,
		DatabaseBuilder.COLUMN_OBSERVATION_DATESTAMP,
		DatabaseBuilder.COLUMN_OBSERVATION_WEEKDAY,
		DatabaseBuilder.COLUMN_OBSERVATION_TIMESTAMP,
		DatabaseBuilder.COLUMN_OBSERVATION_TEMPERATURE,
		DatabaseBuilder.COLUMN_OBSERVATION_MUCUS_COLOR,
		DatabaseBuilder.COLUMN_OBSERVATION_MUCUS_STRETCHABILITY,
		DatabaseBuilder.COLUMN_OBSERVATION_CIRCUMSTANCES,
        DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_DAY,
		DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_STAGE,
		DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_ID
	};

	private String[] cycleSet = { 
		DatabaseBuilder.COLUMN_CYCLE_ID,
		DatabaseBuilder.COLUMN_CYCLE_START_DATE,
		DatabaseBuilder.COLUMN_CYCLE_END_DATE,
		DatabaseBuilder.COLUMN_CYCLE_LENGTH
	};

	public ObservationDataSource(Context context) {
		dbHelper = new DatabaseBuilder(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

    /**
     * Inserts a new observation to the database.
     * <p>
     * @param observation
     * @param isNewCycle
     */
	public long insertObservation(Observation observation, boolean isNewCycle) {
        // Ensure the observation does not exist already
        if (getObservation(observation) == null) {
            // check if first ever entry
            if (getMaxId(DatabaseBuilder.TABLE_CYCLE) == 0) {
                isNewCycle = true;
            }
            // if cycle exists check if we are inserting an observation of a new cycle
            if (isNewCycle) {
                // insert a new cycle
                insertCycle(new Cycle(observation.getDateTime(), observation.getDateTime()));
            } else {
                // update the endDate and length of this cycle
                long cid = getMaxId(DatabaseBuilder.TABLE_CYCLE);
                String endDate = Utils.toDateString(observation.getDateTime());
                int length = Utils.daysBetween(getCycle(cid).getStartDate(), observation.getDateTime());
                String whereClause = DatabaseBuilder.COLUMN_CYCLE_ID + " = " + Long.toString(cid);
                ContentValues cv = new ContentValues();
                cv.put(DatabaseBuilder.COLUMN_CYCLE_END_DATE, endDate);
                cv.put(DatabaseBuilder.COLUMN_CYCLE_LENGTH, length);
                database.update(DatabaseBuilder.TABLE_CYCLE, cv, whereClause, null);
            }
            // insert the observation
            return insertObservation(observation);
        } else {
            Log.e("INSERTING NEW OBSERVATION: ", "ENTRY EXISTS " + observation.toString());
            return 0;
        }
    }
	
	private long insertObservation(Observation o) {
	    ContentValues values = new ContentValues();
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_EPOCH, o.getEpoch());
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_DATESTAMP, o.getDatestamp());
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_WEEKDAY,o.getWeekday());
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_TIMESTAMP, o.getTimestamp());
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_TEMPERATURE,o.getTemperature());
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_MUCUS_COLOR, o.getMucusColor());
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_MUCUS_STRETCHABILITY,o.getMucusStretchability());
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_CIRCUMSTANCES, o.getCircumstances());
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_STAGE,o.getCycleStage());
        long cid = getMaxId(DatabaseBuilder.TABLE_CYCLE);
		values.put(DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_ID,cid);
        o.setCycleDay(Utils.daysSpanning(getCycle(cid).getStartDate(), o.getDateTime()));
        values.put(DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_DAY,o.getCycleDay());
        Log.d("INSERTING NEW OBSERVATION: ",o.toString());
		return database.insert(DatabaseBuilder.TABLE_OBSERVATION, null,values);
    }

	private long insertCycle(Cycle cycle) {
		ContentValues values = new ContentValues();
        values.put(DatabaseBuilder.COLUMN_CYCLE_ID, getMaxId(DatabaseBuilder.TABLE_CYCLE)+1);
		values.put(DatabaseBuilder.COLUMN_CYCLE_START_DATE, cycle.getStartDateString());
		values.put(DatabaseBuilder.COLUMN_CYCLE_END_DATE, cycle.getEndDateString());
		values.put(DatabaseBuilder.COLUMN_CYCLE_LENGTH, cycle.getCycleLength());
		return database.insert(DatabaseBuilder.TABLE_CYCLE, null, values);
	}

    /*
     * Retrieves an observation that matches the date of the observation from the
     * parameter or returns null if there is not one.
     */
	public Observation getObservation(Observation observation) {
		String where = DatabaseBuilder.COLUMN_OBSERVATION_DATESTAMP
		    + " = '" + Utils.toDateString(observation.getEpoch()) + "'";
		Cursor cursor = database.query(DatabaseBuilder.TABLE_OBSERVATION,
			observationSet, where, null, null, null, null);
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

    /*
    * Retrieves an observation by id.
    */
    public Observation getObservation(long oid) {
        String where = DatabaseBuilder.COLUMN_OBSERVATION_ID + " = '" + Long.toString(oid) + "'";
        Cursor cursor = database.query(DatabaseBuilder.TABLE_OBSERVATION,
                observationSet, where, null, null, null, null);
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

    /*
     * Retrieves an observation by id.
     */
    public Observation getObservation(long cid, int cycleDay) {
        String where = DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_ID + " = '" + Long.toString(cid) + "'" +
                " AND " + DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_DAY + " = '" + Integer.toString(cycleDay) + "'";
        Log.d("Find observation: ",where);
        Cursor cursor = database.query(DatabaseBuilder.TABLE_OBSERVATION,
                observationSet, where, null, null, null, null);
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

	public Cycle getCycle(long cid) {
		String where = DatabaseBuilder.COLUMN_CYCLE_ID + " = " + Long.toString(cid);
		Cursor cursor = database.query(DatabaseBuilder.TABLE_CYCLE, cycleSet,
				where, null, null, null, null);
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

    public Observation getLastObservation(long cid) {
        String query = "SELECT * FROM " + DatabaseBuilder.TABLE_OBSERVATION
            + " WHERE " + DatabaseBuilder.COLUMN_OBSERVATION_EPOCH
            + " = (SELECT MAX(" +  DatabaseBuilder.COLUMN_OBSERVATION_EPOCH + ") FROM "
            + DatabaseBuilder.TABLE_OBSERVATION + " WHERE "
            + DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_ID + " = "
            + Long.toString(cid) + " )";
        Cursor cursor = database.rawQuery(query, null);
        Observation observation = null;
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            observation = cursorToObservation(cursor);
        }
        cursor.close();
        return observation;
    }

	public void deleteObservation(Observation observation) {
		long cid = observation.getCycleId();
        String where = DatabaseBuilder.COLUMN_OBSERVATION_ID+"="+Long.toString(observation.getId());
		database.delete(DatabaseBuilder.TABLE_OBSERVATION,where, null);
		Log.d("Deleted Observation: ", observation.toString());
        // check if that was the last observation
		if (getObservations(cid).size() == 0) {
            where = DatabaseBuilder.COLUMN_CYCLE_ID + " = " + Long.toString(cid);
			Log.d(" OBSERVATIONS size was  ", "0");
			database.delete(DatabaseBuilder.TABLE_CYCLE,where,null);
		} else {
            // update the cycle end date and length
            // get last observation for the cycle
            Observation lastObservation = getLastObservation(cid);
            String endDate = lastObservation.getDatestamp();
            int length = Utils.daysBetween(getCycle(cid).getStartDate(), lastObservation.getDateTime());
            String whereClause = DatabaseBuilder.COLUMN_CYCLE_ID + " = " + Long.toString(cid);
            ContentValues cv = new ContentValues();
            cv.put(DatabaseBuilder.COLUMN_CYCLE_END_DATE, endDate);
            cv.put(DatabaseBuilder.COLUMN_CYCLE_LENGTH, length);
            database.update(DatabaseBuilder.TABLE_CYCLE, cv, whereClause, null);
        }
	}

	private void deleteCycle(Cycle cycle) {
		database.delete(
			DatabaseBuilder.TABLE_OBSERVATION,
			DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_ID + " = "+ cycle.getId(), null);
		database.delete(DatabaseBuilder.TABLE_CYCLE,
			DatabaseBuilder.COLUMN_CYCLE_ID + " = " + Long.toString(cycle.getId()), null);
	}

    public ArrayList<Observation> getObservations() {
        ArrayList<Observation> observations = new ArrayList<Observation>();
        Cursor cursor = database.query(DatabaseBuilder.TABLE_OBSERVATION,
                observationSet, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Observation observation = cursorToObservation(cursor);
            observations.add(observation);
            cursor.moveToNext();
        }
        cursor.close();
        return observations;
    }


	public ArrayList<Observation> getObservations(long cycleId) {
		ArrayList<Observation> observations = new ArrayList<Observation>();
		String where = DatabaseBuilder.COLUMN_OBSERVATION_CYCLE_ID + " = " + Long.toString(cycleId);
		Cursor cursor = database.query(DatabaseBuilder.TABLE_OBSERVATION,
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
        ArrayList<Observation> observations = getObservations(cycleId);
		ArrayList<Double> temperatures = new ArrayList<Double>();
        for (Observation observation : observations) {
            temperatures.add(observation.getTemperature());
        }
		return temperatures;
	}
	
	public ArrayList<Double> getElasticity(long cycleId) {
        ArrayList<Observation> observations = getObservations(cycleId);
        ArrayList<Double> elasticity = new ArrayList<Double>();
        for (Observation observation : observations) {
            elasticity.add((double) observation.getMucusStretchability());
        }
        return elasticity;
	}
	
	public ArrayList<DateTime> getCycleDates(long cycleId) {
        ArrayList<Observation> observations = getObservations(cycleId);
        ArrayList<DateTime> dates = new ArrayList<DateTime>();
        for (Observation observation : observations) {
            dates.add(observation.getDateTime());
        }
        return dates;
	}
	
	private Observation cursorToObservation(Cursor cursor) {
		Observation o = new Observation();
		o.setId(cursor.getLong(0));
		o.setEpoch(cursor.getLong(1));
		o.setDatestamp(cursor.getString(2));
        o.setDateTime(Utils.stringDateToDateTime(o.getDatestamp()));
		o.setWeekday(cursor.getString(3));
		o.setTimestamp(cursor.getString(4));
		o.setTemperature(cursor.getDouble(5));
		o.setMucusColor(cursor.getString(6));
		o.setMucusStretchability(cursor.getInt(7));
		o.setCircumstances(cursor.getString(8));
        o.setCycleDay(cursor.getInt(9));
		o.setCycleStage(cursor.getInt(10));
		o.setCycleId(cursor.getInt(11));
		return o;
	}

	private Cycle cursorToCycle(Cursor cursor) {
		Cycle cycle = new Cycle();
		cycle.setId(cursor.getLong(0));
		cycle.setStartDate(Utils.stringDateToDateTime(cursor.getString(1)));
		cycle.setEndDate(Utils.stringDateToDateTime(cursor.getString(2)));
		cycle.setCycleLength(cursor.getInt(3));
		return cycle;
	}

	public ArrayList<Cycle> getCycles() {
		ArrayList<Cycle> cycles = new ArrayList<Cycle>();
		Cursor cursor = database.query(DatabaseBuilder.TABLE_CYCLE, cycleSet,
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

    public CycleNavigator getCycleNavigator(CycleNavigator cycleNavigator) {
        CycleNavigator navigator = cycleNavigator;
        navigator.setPrevious(null);
        navigator.setNext(null);
        if (navigator.getCurrent() == null) {
            long currentCycleId = getMaxId(DatabaseBuilder.TABLE_CYCLE);
            navigator.setCurrent(getCycle(currentCycleId));
        }
        Cursor cursor = database.query(DatabaseBuilder.TABLE_CYCLE, cycleSet,
                null, null, null, null, null);
        // The cursor starts before the first result row, so on the first iteration this moves
        // to the first result if it exists. If the cursor is empty, or the last row has already
        // been processed, then the loop exits neatly.
        Log.d(ObservationDataSource.class.getName(),navigator.toString());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.d("LOOPING - ",Integer.toString(cursor.getPosition()));
            Cycle c = cursorToCycle(cursor);
            if (c.getId() == navigator.getCurrent().getId()) {
                int position = cursor.getPosition();
                Log.d("CURSOR: MATCH " + Integer.toString(cursor.getPosition()),cursorToCycle(cursor).toString());
                if (cursor.moveToPosition(position+1)) {
                    Log.d("CURSOR: MOVE NEXT " + Integer.toString(cursor.getPosition()), cursorToCycle(cursor).toString());
                    if (!cursor.isAfterLast()) {
                        Log.d("CURSOR: NEXT " + Integer.toString(cursor.getPosition()), cursorToCycle(cursor).toString());
                        navigator.setNext(cursorToCycle(cursor));
                    }
                }
                if (cursor.moveToPosition(position-1)) {
                    Log.d("CURSOR: MOVE BACK " + Integer.toString(cursor.getPosition()), cursorToCycle(cursor).toString());
                    if (!cursor.isBeforeFirst()) {
                        Log.d("CURSOR: PREV " + Integer.toString(cursor.getPosition()), cursorToCycle(cursor).toString());
                        navigator.setPrevious(cursorToCycle(cursor));
                    }
                }
                cursor.moveToLast();
            }
            cursor.moveToNext();
        }
        cursor.close();
        return navigator;
    }

    public void dropTables() {
        dbHelper.dropTables(database);
    }
}
