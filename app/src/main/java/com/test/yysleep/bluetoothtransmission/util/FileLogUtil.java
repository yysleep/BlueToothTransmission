package com.test.yysleep.bluetoothtransmission.util;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class FileLogUtil {

    private static FileWriter sFileWriter;
    private static BufferedWriter sBufferedWriter;
    private static DateFormat sFormat;

    public static void writeLog(String log) {
        if (log == null)
            return;

        if (sFileWriter == null || sBufferedWriter == null) {
            init();
        }

        if (sFileWriter == null || sBufferedWriter == null) {
            return;
        }

        try {
            String date = sFormat.format(new Date()) + " : ";
            sBufferedWriter.append(date);
            sBufferedWriter.newLine();
            sBufferedWriter.append(log);
            sBufferedWriter.newLine();
            sBufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static synchronized void init() {
        if (sBufferedWriter == null) {
            try {
                sFileWriter = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test02.txt", true);
                sBufferedWriter = new BufferedWriter(sFileWriter);
                sFormat = SimpleDateFormat.getDateTimeInstance();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized void resetFileContent() {
        if (sBufferedWriter != null) {
            try {
                sBufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            sFileWriter = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test02.txt");
            sBufferedWriter = new BufferedWriter(sFileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
