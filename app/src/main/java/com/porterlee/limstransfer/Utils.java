package com.porterlee.limstransfer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import static android.content.Context.VIBRATOR_SERVICE;

public class Utils {

    public static boolean vibrate(@NotNull Context context) {
        final Vibrator vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (vibrator != null) {
                vibrator.vibrate(VibrationEffect.createOneShot(300L, VibrationEffect.DEFAULT_AMPLITUDE));
                return true;
            }
        } else {
            if (vibrator != null) {
                vibrator.vibrate(300L);
                return true;
            }
        }
        return false;
    }

    public static void saveSignature(Context context, Bitmap signatureBitmap, File file) throws IOException {
        if (!file.getParentFile().mkdirs() && !file.getParentFile().exists())
            throw new IOException("Could not create signatures directory");

        try {
            saveBitmapToPNG(signatureBitmap, file);
            refreshExternalPath(context, file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Unable to save", e);
        }
    }

    public static void saveBitmapToPNG(Bitmap bitmap, File file) throws IOException {
        OutputStream stream = null;
        try {
            //Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            //Canvas canvas = new Canvas(newBitmap);
            //canvas.drawColor(Color.WHITE);
            //canvas.drawBitmap(bitmap, 0, 0, null);
            stream = new FileOutputStream(file);
            //newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) 
                stream.close();
        }
    }

    public static void refreshExternalPath(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file)));
        }
    }

    public static String cursorToString(Cursor cursor) {
        if (cursor.moveToFirst()) {
            StringBuilder result = new StringBuilder("Columns:");
            final String[] columnNames = cursor.getColumnNames();
            final int[] columnIndicies = new int[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                columnIndicies[i] = cursor.getColumnIndex(columnNames[i]);
                result.append('\"');
                result.append(columnNames[i]);
                result.append('\"');
                if (i < columnNames.length - 1)
                    result.append(',');
            }
            result.append("\r\n");

            while (!cursor.isAfterLast()) {
                for (int i = 0; i < columnIndicies.length; i++) {
                    result.append('\"');
                    result.append(cursor.getString(columnIndicies[i]));
                    result.append('\"');
                    if (i < columnIndicies.length - 1)
                        result.append(',');
                }
                result.append("\r\n");
                cursor.moveToNext();
            }
            return result.toString();
        }
        return null;
    }

    public static boolean csvContainsInt(@NotNull String csv, int i) {
        final String regex = "(^" + i + ",.*)|(.*," + i + ",.*)|(.*," + i + "$)";
        return csv.replace(" ", "").matches(regex);
    }

    public static class Toaster {
        protected WeakReference<Activity> activityWeakReference;

        Toaster(Activity activity) { activityWeakReference = new WeakReference<>(activity); }

        public boolean toast(String s) {
            if (activityWeakReference.get() != null) {
                activityWeakReference.get().runOnUiThread(() -> Toast.makeText(activityWeakReference.get(), s, Toast.LENGTH_SHORT).show());
                return true;
            }
            return false;
        }
    }

    public static class Holder <T> {
        private T object;

        public T get() {
            return object;
        }

        public void set(T object) {
            this.object = object;
        }
    }
    
    public interface OnProgressUpdateListener {
        void onProgressUpdate(float progress);
    }
    
    public static class QueryHolder {
        private SQLiteDatabase mDatabase;
        private String mQuery;
        private String[] mArgs;
        
        public QueryHolder(SQLiteDatabase database, String query, String... args) {
            mDatabase = database;
            mQuery = query;
            mArgs = args;
        }
        
        public Cursor query() {
            return mDatabase.rawQuery(mQuery, mArgs);
        }
    }
}