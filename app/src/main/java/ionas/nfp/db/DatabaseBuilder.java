package ionas.nfp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class create the underlying SQL tables.
 */
public class DatabaseBuilder extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ionas.nfp.db";
    private static final int DATABASE_VERSION = 1;

	public static final String TABLE_OBSERVATION = "observation";
	public static final String TABLE_CYCLE = "cycle";

	public static final String COLUMN_OBSERVATION_ID = "_id";
	public static final String COLUMN_OBSERVATION_EPOCH = "epoch";
	public static final String COLUMN_OBSERVATION_DATESTAMP = "datestamp";
    public static final String COLUMN_OBSERVATION_WEEKDAY = "weekday";
    public static final String COLUMN_OBSERVATION_TIMESTAMP = "timestamp";
	public static final String COLUMN_OBSERVATION_TEMPERATURE = "temperature";
	public static final String COLUMN_OBSERVATION_MUCUS_COLOR = "mucus_color";
	public static final String COLUMN_OBSERVATION_MUCUS_STRETCHABILITY = "mucus_stretchability";
	public static final String COLUMN_OBSERVATION_CIRCUMSTANCES = "circumstances";
    public static final String COLUMN_OBSERVATION_CYCLE_DAY = "cycle_day";
	public static final String COLUMN_OBSERVATION_CYCLE_STAGE = "cycle_stage";
	public static final String COLUMN_OBSERVATION_CYCLE_ID = "cycle_id";

	public static final String COLUMN_CYCLE_ID = "_id";
	public static final String COLUMN_CYCLE_START_DATE = "start_date";
	public static final String COLUMN_CYCLE_END_DATE = "end_date";
	public static final String COLUMN_CYCLE_LENGTH = "length";

	public DatabaseBuilder(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static final String CREATE_OBSERVATION_TABLE = "create table "
			+ TABLE_OBSERVATION + "("
            + COLUMN_OBSERVATION_ID + " integer primary key autoincrement, "
            + COLUMN_OBSERVATION_EPOCH + " integer not null, "
            + COLUMN_OBSERVATION_DATESTAMP + " text not null, "
            + COLUMN_OBSERVATION_WEEKDAY + " text not null, "
            + COLUMN_OBSERVATION_TIMESTAMP + " text not null, "
            + COLUMN_OBSERVATION_TEMPERATURE + " real not null, "
            + COLUMN_OBSERVATION_MUCUS_COLOR + " text not null, "
            + COLUMN_OBSERVATION_MUCUS_STRETCHABILITY + " integer not null, "
            + COLUMN_OBSERVATION_CIRCUMSTANCES + " text not null, "
            + COLUMN_OBSERVATION_CYCLE_DAY + " integer not null, "
            + COLUMN_OBSERVATION_CYCLE_STAGE + " integer not null, "
            + COLUMN_OBSERVATION_CYCLE_ID + " integer not null, " + " FOREIGN KEY ("
			+ COLUMN_OBSERVATION_CYCLE_ID + ") REFERENCES " + TABLE_CYCLE
			+ " (" + COLUMN_CYCLE_ID + ")" + ");";

	private static final String CREATE_CYCLE_TABLE = "create table "
			+ TABLE_CYCLE + "(" + COLUMN_CYCLE_ID + " integer primary key autoincrement, " 
			+ COLUMN_CYCLE_START_DATE + " text not null, " 
			+ COLUMN_CYCLE_END_DATE + " text null, " 
			+ COLUMN_CYCLE_LENGTH + " integer null" + ");";

	@Override
	public void onCreate(SQLiteDatabase database) {
		createTables(database);
	}
	
	public void createTables(SQLiteDatabase db) {
        //dropTables(db);
		db.execSQL(CREATE_OBSERVATION_TABLE);
		db.execSQL(CREATE_CYCLE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseBuilder.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
        dropTables(db);
		onCreate(db);
	}

    public void dropTables(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CYCLE);
    }
}
