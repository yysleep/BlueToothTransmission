package com.test.yysleep.bluetoothtransmission.util;

import android.os.Build;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class VersionUtil {

    public static boolean isLargeM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean isLargeN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    public static boolean isLargeO() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }
}
