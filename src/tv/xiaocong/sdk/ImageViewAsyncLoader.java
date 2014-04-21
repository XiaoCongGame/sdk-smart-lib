package tv.xiaocong.sdk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * 异步加载图片到指定的 {@link ImageView}。请用<code>load</code>方法。
 * 
 * @author yaoyuan
 * 
 */
public class ImageViewAsyncLoader extends AsyncTask<String, Void, Object> {

    private static final String LOG_TAG = ImageViewAsyncLoader.class.getSimpleName();

    private WeakReference<ImageView> imageViewRef;

    private File cacheDir;

    /**
     * 异步加载图片到指定的 {@link ImageView}。
     * 
     * @param imageView
     *            目标
     * @param cacheDir
     *            缓存目录
     * @param path
     *            图片下载路径（相对路径）
     */
    public static void load(ImageView imageView, File cacheDir, String path) {
        new ImageViewAsyncLoader(imageView, cacheDir).execute(path);
    }

    private ImageViewAsyncLoader(ImageView imageView, File cacheDir) {
        this.imageViewRef = new WeakReference<ImageView>(imageView);
        this.cacheDir = cacheDir;
    }

    @Override
    protected Object doInBackground(String... params) {
        String path = params[0];

        String cacheName = path.replaceAll("(/|\\\\)", "-");
        File cacheFile = null;
        if (cacheDir != null && cacheDir.exists()) {
            cacheFile = new File(cacheDir, cacheName);
        } else {
            Log.d(LOG_TAG, "no cache dir!");
        }

        try {
            if (cacheFile != null && cacheFile.exists()) {
                Log.d(LOG_TAG, "hit cache!");
                return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
            }

            URL url = new URL(XcServiceClient.SERVER_TVSTORE + path);

            InputStream is = null;
            try {
                is = url.openConnection().getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                // 缓存
                if (cacheFile != null) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(cacheFile);
                        bitmap.compress(CompressFormat.PNG, 100, fos);
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "caching fail!", e);
                    } finally {
                        fos.close();
                    }
                }
                return bitmap;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        } catch (MalformedURLException e) {
            return e;
        } catch (IOException e) {
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        ImageView imageView = imageViewRef.get();
        if (imageView == null) {
            return;
        }

        if (result instanceof Exception) {
            Log.e(LOG_TAG, "", (Throwable) result);
        } else {
            imageView.setImageBitmap((Bitmap) result);
        }
    }
}
