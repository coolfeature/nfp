package ionas.nfp.storage;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by sczaja on 20/01/2015.
 */
public class Storage {

    public Storage() {

    }

    public void readIn() {
        //Find the directory for the SD Card using the API
        File sdcard = Environment.getExternalStorageDirectory();

        //Get the text file
        File file = new File(sdcard,"file.txt");

        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }

            } finally {
                if (br != null) br.close();
            }
        } catch (FileNotFoundException fnf) {
            //You'll need to add proper error handling here
        } catch (IOException ioe) {

        }

    }
}
