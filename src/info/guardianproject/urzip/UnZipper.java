package info.guardianproject.urzip;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// http://stackoverflow.com/questions/5028421/android-unzip-a-folder
// Copyright 2011, rich.e http://stackoverflow.com/users/506584/rich-e
// licensed under Creative Commons Attribution-ShareAlike 3.0 Unported (CC BY-SA 3.0)

public class UnZipper extends Observable {

    private static final String TAG = "UnZip";
    private String mFileName;
    private String mDestinationPath;

    public UnZipper(String fileName, String destinationPath) {
        mFileName = fileName;
        mDestinationPath = destinationPath;
    }

    public String getFileName() {
        return mFileName;
    }

    public String getDestinationPath() {
        return mDestinationPath;
    }

    public void unzip() {
        Log.d(TAG, "unzipping " + mFileName + " to " + mDestinationPath);
        new UnZipTask().execute(mFileName, mDestinationPath);
    }

    private class UnZipTask extends AsyncTask<String, Void, Boolean> {

        @SuppressWarnings("rawtypes")
        @Override
        protected Boolean doInBackground(String... params) {
            String filePath = params[0];
            String destinationPath = params[1];

            File archive = new File(filePath);
            try {
                ZipFile zipfile = new ZipFile(archive);
                for (Enumeration e = zipfile.entries(); e.hasMoreElements();) {
                    ZipEntry entry = (ZipEntry) e.nextElement();
                    unzipEntry(zipfile, entry, destinationPath);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while extracting file " + archive, e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            setChanged();
            notifyObservers();
        }

        private void unzipEntry(ZipFile zipfile, ZipEntry entry,
                String outputDir) throws IOException {

            if (entry.isDirectory()) {
                createDir(new File(outputDir, entry.getName()));
                return;
            }

            File outputFile = new File(outputDir, entry.getName());
            if (!outputFile.getParentFile().exists()) {
                createDir(outputFile.getParentFile());
            }

            Log.v(TAG, "Extracting: " + entry);
            BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                    outputFile));

            try {
                IOUtils.copy(inputStream, outputStream);
            } finally {
                outputStream.close();
                inputStream.close();
            }
        }

        private void createDir(File dir) {
            if (dir.exists()) {
                return;
            }
            Log.v(TAG, "Creating dir " + dir.getName());
            if (!dir.mkdirs()) {
                throw new RuntimeException("Can not create dir " + dir);
            }
        }
    }
}
