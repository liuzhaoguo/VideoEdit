package com.sina.videoedit.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Provides a simplified way to show toast messages without having to create the toast, set the
 * desired gravity, etc.
 */
public class ToastUtils {
    public enum Duration {SHORT, LONG}

    static Toast mToast;

    private ToastUtils() {
        throw new AssertionError();
    }

    public static Toast showToast(Context context, int stringResId) {
        return showToast(context, stringResId, Duration.SHORT);
    }

    public static Toast showToast(Context context, int stringResId, Duration duration) {
        if (context == null) {
            return null;
        }
        return showToast(context, context.getString(stringResId), duration);
    }

    public static Toast showToast(Context context, String text) {
        return showToast(context, text, Duration.SHORT);
    }

    public static Toast showToast(Context context, String text, Duration duration) {
        if (context == null) {
            return null;
        }
        if (mToast == null) {
            mToast = Toast.makeText(context.getApplicationContext(), text,
                    (duration == Duration.SHORT ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG));
        }
        try {
            mToast.setText(text);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        mToast.setDuration(duration == Duration.SHORT ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
//        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
        return mToast;
    }
}
