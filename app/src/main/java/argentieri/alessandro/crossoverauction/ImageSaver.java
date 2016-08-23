package argentieri.alessandro.crossoverauction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageSaver {
    private String directoryName = "images";
    private String fileName = "shoes1adminuser@gmail.png";
    private Context context;

    public ImageSaver(Context context) {
        this.context = context;
    }

    public ImageSaver setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ImageSaver setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
        return this;
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("IMAGESAVER","save: " + e.toString());
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IMAGESAVER","save-finally: " + e.toString());
            }
        }

    }

    @NonNull
    private File createFile() {
        File directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        return new File(directory, fileName);
    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("IMAGESAVER","load: " + e.toString());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IMAGESAVER","load-finally: " + e.toString());
            }
        }
        return null;
    }

}
