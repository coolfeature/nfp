package ionas.nfp.test;

import android.content.Context;

import org.joda.time.DateTime;

import ionas.nfp.db.Observation;
import ionas.nfp.db.ObservationDataSource;
import ionas.nfp.utils.Utils;

/**
 * Created by sczaja on 11/01/2015.
 */
public class Test {
    Context context = null;

    public Test (Context context,boolean run) {
        this.context = context;
        if (run) {
            insertTestData();
        }
    }

    private void insertTestData() {
        ObservationDataSource dataSource = new ObservationDataSource(context);
        dataSource.open();
        for (int i=30;i>0;i--) {
            Observation o = new Observation(new DateTime().plusDays(-i).getMillis(),
                    Utils.getRandomDouble(35.2, 38.8, 2), "blue",
                    (int)Utils.getRandomLong(1,7), "normal");
            dataSource.insertObservation(o, false);
        }
        dataSource.close();
    }

    private void testDate() {

    }
}
