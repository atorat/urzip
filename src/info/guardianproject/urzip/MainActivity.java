
package info.guardianproject.urzip;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class MainActivity extends Activity implements Observer {
    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null)
            unzipWebFile(uri.getPath());
        else
            Log.i(TAG, "onCreate doing nothing...");
        // finish();
    }

    private void unzipWebFile(String filename) {
        File file = new File(filename);
        String unzipLocation = getFilesDir() + "/" + stripExtension(file.getName()) + ".d";

        UnZipper unzipper = new UnZipper(filename, unzipLocation);
        unzipper.addObserver(this);
        unzipper.unzip();
    }

    String stripExtension(String filename) {
        final int lastPeriodPos = filename.lastIndexOf('.');
        if (lastPeriodPos <= 0)
            // No period after first character - return name as it was passed in
            return filename;
        else
            return filename.substring(0, lastPeriodPos);
    }

    @Override
    public void update(Observable observable, Object data) {
        Log.i(TAG, "update");
        // TODO share the file
        // TODO Delete the unzziped dir once the receiver is done with it
    }
}
