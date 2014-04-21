package tv.xiaocong.sdk;

import java.io.ByteArrayOutputStream;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

/**
 * UI工具类
 * 
 * @author yaoyuan
 * 
 */
public final class XcAndroidUtils {

    private XcAndroidUtils() {
    }

    public static AnimatorSet loadAnimatorSet(int id, View target, Context context) {
        final AnimatorSet animatorSet = (AnimatorSet) AnimatorInflater.loadAnimator(context, id);
        animatorSet.setTarget(target);

        return animatorSet;
    }

    /**
     * Convert {@link Bitmap} to a byte array.
     * 
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        // No need to close ByteArrayOutputStream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void showInfoDialog(Context context, int titleResId, int messageResid, int okResId) {
        if (context == null) {
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(context).setTitle(titleResId)
                .setMessage(messageResid).setPositiveButton(okResId, null).create();

        dialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();

        Button btn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        btn.setFocusableInTouchMode(true); // 没有这句不起效！
        btn.requestFocus();
        btn.requestFocusFromTouch();
    }

    public static void installApk(Context context, String filePath) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + filePath),
                "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
