package com.test.yysleep.bluttoothtransmission;

import android.os.Environment;

import java.util.UUID;

/**
 * Created by Administrator on 2018/1/17.
 *
 * @author yysleep
 */

public class Constant {

    public final static String PACKAGE_NAME = "com.test.yysleep.bluttoothtransmission";

    /**
     * 蓝牙模块
     */
    public final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Todo
    public final static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ahao.jpg";
 }
