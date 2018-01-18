package com.test.yysleep.bluttoothtransmission.ui.bluetooth;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.test.yysleep.bluttoothtransmission.ui.base.BaseActivity;

/**
 * Created by YySleep on 2018/1/18.
 * @author YySleep
 */

public class TestBluetoothActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "BlueToothActivity";
    private static final int BLUETOOTH_REQUEST_CODE = 300;
    private static final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private EditText mChangeNameEdt;
    private Button mSaveNameBtn;
    private Switch mWasDetectedSwitch;
    private TextView mTimeTv;

    /*private List<BluetoothDevice> mBondDevices;
    private List<BluetoothDevice> mDevices;

    private BluetoothAdapter mBlueAdapter;
    private BlueToothDevicesAdapter mDevicesAdapter;
    private BlueToothBondDevicesAdapter mBondDeviceAdapter;
    private Timer mTimer;
    private int mTime;

    private TestBluetoothActivity.BluetoothHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        init();
        initView();
    }

    private void init() {
        mHandler = new TestBluetoothActivity.BluetoothHandler(this);
        mBlueAdapter = BluetoothSys.getInstance().getBlueToothAdapter();
        if (mBlueAdapter == null) {
            finish();
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, filter);

        mDevices = new ArrayList<>();
        mBondDevices = new ArrayList<>();

    }

    private void initView() {
        mWasDetectedSwitch = findViewById(R.id.bluetooth_was_detected_switch);
        mWasDetectedSwitch.setOnCheckedChangeListener(this);
        mChangeNameEdt = findViewById(R.id.bluetooth_change_bluetooth_name_et);
        mSaveNameBtn = findViewById(R.id.bluetooth_change_bluetooth_name_save_btn);
        mTimeTv = findViewById(R.id.blue_time_tv);

        mDevicesAdapter = new BlueToothDevicesAdapter(mDevices);
        mBondDeviceAdapter = new BlueToothBondDevicesAdapter(mBondDevices);

        ListView mBondDevicesLv = findViewById(R.id.bluetooth_bond_devices_lv);
        mBondDevicesLv.setAdapter(mBondDeviceAdapter);
        mBondDevicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlert(mBondDevices, position);
            }
        });
        ListView mDevicesLv = findViewById(R.id.bluetooth_devices_lv);
        mDevicesLv.setAdapter(mDevicesAdapter);
        mDevicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlert(mDevices, position);
            }
        });
        if (mBlueAdapter.isEnabled()) {
            mWasDetectedSwitch.setVisibility(View.VISIBLE);
            mBlueAdapter.startDiscovery();
            mBondDevices.addAll(mBlueAdapter.getBondedDevices());
            mBondDeviceAdapter.notifyDataSetChanged();
            String name = null;
            if (mBondDevices.size() > 0) {
                for (BluetoothDevice device : mBondDevices) {
                    if (device == null)
                        continue;
                    name = device.getName();
                    name = name == null ? device.getAddress() : name;
                    LogUtil.d(TAG, "[initView] device = " + name);
                }
            }
            new BlueToothAcceptFileThread().start();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onBlueClick(View v) {
        if (mBlueAdapter == null) {
            ToastUtil.toast(this, "该设备不支持蓝牙设备");
        }
        switch (v.getId()) {
            case R.id.bluetooth_open_bluetooth_btn:

                if (!mBlueAdapter.isEnabled()) {
                    // mBlueAdapter.enable();
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLUETOOTH_REQUEST_CODE);
                } else {
                    ToastUtil.toast(this, "蓝牙处于开启状态");
                }
                break;

            case R.id.bluetooth_change_bluetooth_name_btn:
                if (mBlueAdapter.isEnabled()) {
                    mSaveNameBtn.setVisibility(View.VISIBLE);
                    mChangeNameEdt.setVisibility(View.VISIBLE);
                } else {
                    ToastUtil.toast(this, "请先开启蓝牙");
                }
                break;

            case R.id.bluetooth_accept_device_btn:
                if (mBlueAdapter.isEnabled()) {
                    new BlueToothAcceptFileThread().start();
                } else {
                    ToastUtil.toast(this, "请先开启蓝牙");
                }
                break;

            case R.id.bluetooth_change_bluetooth_name_save_btn:
                String newName = mChangeNameEdt.getText().toString().trim();
                if ("".equals(newName)) {
                    ToastUtil.toast(this, "名字不可以为空");
                    return;
                }
                LogUtil.d(TAG, "[onMainClick] the new name = " + newName);
                if (mBlueAdapter.isEnabled()) {
                    mBlueAdapter.setName(newName);
                    ToastUtil.toast(this, "修改成功 --- " + newName);
                    mChangeNameEdt.setText("");
                } else {
                    ToastUtil.toast(this, "请先开启蓝牙");
                }
                mSaveNameBtn.setVisibility(View.GONE);
                mChangeNameEdt.setVisibility(View.GONE);
                break;

            case R.id.bluetooth_close_bluetooth_btn:
                if (mBlueAdapter != null && mBlueAdapter.isEnabled()) {
                    mBlueAdapter.disable();
                    mWasDetectedSwitch.setChecked(false);
                } else {
                    ToastUtil.toast(this, "蓝牙处于关闭状态");
                }
                break;

            case R.id.bluetooth_show_connected_blue_devices_btn:
                if (mBlueAdapter.isEnabled()) {
                    mBondDevices.clear();
                    mBondDevices.addAll(mBlueAdapter.getBondedDevices());
                    mBondDeviceAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.toast(this, "请先开启蓝牙");
                }
                break;

            case R.id.bluetooth_scan_blue_devices_btn:
                if (mBlueAdapter.isEnabled()) {
                    if (mBlueAdapter.isDiscovering()) {
                        mBlueAdapter.cancelDiscovery();
                    }
                    mBlueAdapter.startDiscovery();
                } else {
                    ToastUtil.toast(this, "请先开启蓝牙");
                }
                break;

            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d(TAG, "[onActivityResult] requestCode = " + requestCode + " --- resultCode = " + resultCode);
        switch (requestCode) {

            case BLUETOOTH_REQUEST_CODE:
                switch (resultCode) {
                    case BluetoothConstant.RESULT_OPEN_BLUETOOTH_OK:
                        LogUtil.d(TAG, "[onActivityResult] 蓝牙已打开 ");
                        break;

                    case BluetoothConstant.RESULT_OPEN_BLUETOOTH_CANCELED:
                        LogUtil.d(TAG, "[onActivityResult] 蓝牙未打开 ");
                        break;

                    default:
                        break;
                }
                break;

            case BluetoothConstant.REQUEST_SEARCHED:
                if (resultCode == BluetoothConstant.RESULT_SEARCHED_CANCELED) {
                    mWasDetectedSwitch.setChecked(false);
                    return;
                }
                resetTimer();
                mTime = resultCode;
                mTimeTv.setVisibility(View.VISIBLE);
                mTimer.schedule(new TestBluetoothActivity.SearchedTimeTask(), 0, 1000);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private boolean isExist(BluetoothDevice device) {
        if (device == null || device.getAddress() == null || mDevices == null)
            return false;

        for (BluetoothDevice d : mDevices) {
            if (d.getAddress() == null)
                continue;
            if (d.getAddress().equals(device.getAddress()))
                return true;
        }
        mDevices.add(device);
        return false;
    }

    private void showAlert(final List<BluetoothDevice> list, final int position) {
        if (list == null || list.size() == 0)
            return;

        AlertDialog alertDialog = new AlertDialog.Builder(TestBluetoothActivity.this, R.style.YMAlertDialogStyle).
                setTitle("是否连接该设备").
                setIcon(R.mipmap.ic_launcher).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothDevice device = list.get(position);
                        new BlueToothConnectManager(mBlueAdapter, device).start();
                        //new BlueToothAcceptFileThread(mBlueAdapter).start();
                        // new BlueToothPairThread(device, true).start();
                    }
                }).
                setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).
                create();
        alertDialog.show();
    }

    public static class BluetoothHandler extends Handler {
        public final static int MESSAGE_READ = 300;
        private final WeakReference<TestBluetoothActivity> w;

        public BluetoothHandler(TestBluetoothActivity activity) {
            w = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            TestBluetoothActivity activity = w.get();
            if (activity == null)
                return;
            switch (msg.what) {
                case MESSAGE_READ:
                    break;
            }
        }
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null)
                return;

            switch (intent.getAction()) {

                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 1);
                    switch (state) {

                        case BluetoothAdapter.STATE_OFF:
                            mWasDetectedSwitch.setVisibility(View.GONE);
                            ToastUtil.toast(TestBluetoothActivity.this, "蓝牙已关闭");
                            break;

                        case BluetoothAdapter.STATE_TURNING_ON:
                            ToastUtil.toast(TestBluetoothActivity.this, "正在打开蓝牙");
                            break;

                        case BluetoothAdapter.STATE_ON:
                            ToastUtil.toast(TestBluetoothActivity.this, "蓝牙已打开");
                            mWasDetectedSwitch.setVisibility(View.VISIBLE);
                            mBlueAdapter.startDiscovery();
                            mBondDevices.clear();
                            mBondDevices.addAll(mBlueAdapter.getBondedDevices());
                            mBondDeviceAdapter.notifyDataSetChanged();
                            new BlueToothAcceptFileThread().start();
                            break;

                        case BluetoothAdapter.STATE_TURNING_OFF:
                            ToastUtil.toast(TestBluetoothActivity.this, "正在关闭蓝牙");
                            mDevices.clear();
                            mDevicesAdapter.notifyDataSetChanged();

                            if (mBlueAdapter.isDiscovering())
                                mBlueAdapter.cancelDiscovery();
                            break;

                        default:
                            break;
                    }
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    BluetoothDevice scannerDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (scannerDevice == null)
                        return;
                    if (scannerDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        LogUtil.d(TAG, "[onReceive] 已配对设备 ：" + scannerDevice.getName() + " --- " + scannerDevice.getAddress());

                    } else {
                        LogUtil.d(TAG, "[onReceive] 未配对设备 = " + scannerDevice.getName() + " --- " + scannerDevice.getAddress());
                    }
                    if (!isExist(scannerDevice)) {
                        mDevicesAdapter.notifyDataSetChanged();
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    LogUtil.d(TAG, "[onReceive] 扫描结束");
                    break;

                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    BluetoothDevice changeDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (changeDevice == null)
                        return;

                    switch (changeDevice.getBondState()) {
                        case BluetoothDevice.BOND_NONE:
                            LogUtil.d(TAG, "[onReceive] 取消配对");
                            mDevicesAdapter.notifyDataSetChanged();
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            LogUtil.d(TAG, "[onReceive] 配对中");
                            break;
                        case BluetoothDevice.BOND_BONDED:
                            LogUtil.d(TAG, "[onReceive] 配对成功");
                            mDevicesAdapter.notifyDataSetChanged();
                            break;
                    }
                    break;

                default:
                    break;

            }

        }
    };

    private class SearchedTimeTask extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mTime <= 0) {
                        mWasDetectedSwitch.setChecked(false);
                        return;
                    }
                    mTime--;
                    mTimeTv.setText(String.valueOf(mTime));

                }
            });
        }
    }

    private void resetTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        mTime = 0;
        mTimeTv.setVisibility(View.GONE);
    }*/

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /*if (isChecked) {
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
            startActivityForResult(discoverableIntent, BluetoothConstant.REQUEST_SEARCHED);
        } else {
            resetTimer();
        }*/
    }

}

