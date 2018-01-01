package com.jvdm.recruits.Helpers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Joske on 30/12/17.
 */

public class Helper {
    private static Toast toast;

    public static void showShortToast(Context context, String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLongToast(Context context, String text) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }
}
