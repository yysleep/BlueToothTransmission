package com.test.yysleep.bluttoothtransmission.constant;

import java.util.UUID;

/**
 * Created by YySleep on 2018/1/18.
 *
 * @author YySleep
 */

public class BluetoothConstant {

    public final static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public final static UUID FILE_TRANSPORT_UUID = UUID.fromString("00001106-0000-1000-8000-00805F9B34FB");

    public final static int REQUEST_SEARCHED = 800;
    public final static int RESULT_SEARCHED_CANCELED = 0;

    public final static int RESULT_OPEN_BLUETOOTH_OK = -1;
    public final static int RESULT_OPEN_BLUETOOTH_CANCELED = 0;

    public final static String EXTRA_TRANSPORT_SEND_SERVICE = "send_job";

    public final static int MESSAGE_BLUETOOTH_SERVER_SOCKET_FAILED = 403;

    public final static int MESSAGE_UPDATE_SEND_NOTIFICATION = 500;
    public final static int MESSAGE_FINISH_SEND_NOTIFICATION = 501;
    public final static int MESSAGE_UPDATE_ACCEPT_NOTIFICATION = 502;
    public final static int MESSAGE_FINISH_ACCEPT_NOTIFICATION = 503;

    public final static String FLAG_ACCEPT_FILE_INFO_SUCCESS = "accept_file_info_success";

    public final static String FLAG_ACCEPT_FILE_SUCCESS = "accept_file_success";




}
