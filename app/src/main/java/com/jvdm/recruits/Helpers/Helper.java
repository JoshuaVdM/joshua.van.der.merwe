package com.jvdm.recruits.Helpers;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Joske on 30/12/17.
 */

public class Helper {
    public static void showShortToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
