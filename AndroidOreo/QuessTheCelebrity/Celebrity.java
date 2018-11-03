package com.samlinz.androidcourse.quesstheceleb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class Celebrity {
    private final String LogName = Celebrity.class.getSimpleName();

    private String name;
    private Bitmap image;
    private boolean imageLoaded;
    private String imageUrl;
    private ImageLoadTask imageLoadTask;
    private int rank;

    public Celebrity(String name, String imageUrl, int rank) {
        this.name = name;
        this.rank = rank;
        this.imageUrl = imageUrl;
        this.imageLoaded = false;
    }

    public String getName() {
        return name;
    }

    public void getImage(ImageLoadTaskCallback callback) {
        if (this.image != null && this.imageLoaded) {
            callback.onComplete(this.image);
        } else if (!this.imageLoaded) {
            if (this.imageLoadTask == null) {
                // Start async task to load image.
                this.imageLoadTask = new ImageLoadTask(this);
                this.imageLoadTask.execute(this.imageUrl);
            }

            this.imageLoadTask.addCallback(() -> callback.onComplete(image));
        }

        // Could not download, ignore image.
    }

    public interface ImageLoadTaskCallback {
        void onComplete(Bitmap bitmap);
    }

    private static class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
        private final String LogName = ImageLoadTask.class.getSimpleName();

        private Celebrity celebrity;
        private List<Runnable> callbacks;

        public ImageLoadTask(Celebrity celebrity) {
            this.celebrity = celebrity;
            this.callbacks = new LinkedList<>();
        }

        public void addCallback(Runnable runnable) {
            callbacks.add(runnable);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            String imageUrl = strings[0];

            try {
                final URL urlObj = new URL(imageUrl);
                final URLConnection urlConnection = urlObj.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.connect();
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                Bitmap loadedBitmap = BitmapFactory.decodeStream(inputStream);
                return loadedBitmap;
            } catch (MalformedURLException e) {
                Log.e(LogName, "Invalid URL for image " + imageUrl);
                // Mark image as loaded so it will not be attempted again.
                celebrity.imageLoaded = true;
                return null;
            } catch (IOException e) {
                Log.e(LogName, "Error while loading image " + imageUrl);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) return;

            celebrity.image = bitmap;
            celebrity.imageLoaded = true;

            callbacks.forEach(Runnable::run);
        }
    }
}
