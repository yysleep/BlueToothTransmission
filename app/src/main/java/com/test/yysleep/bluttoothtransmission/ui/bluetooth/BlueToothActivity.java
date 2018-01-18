package com.test.yysleep.bluttoothtransmission.ui.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;


import com.test.yysleep.bluttoothtransmission.R;
import com.test.yysleep.bluttoothtransmission.tool.adapter.BlueToothBondDevicesAdapter;
import com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth.BlueToothAcceptThread;
import com.test.yysleep.bluttoothtransmission.tool.thread.bluetooth.BlueToothConnectThread;
import com.test.yysleep.bluttoothtransmission.tool.adapter.BlueToothDevicesAdapter;
import com.test.yysleep.bluttoothtransmission.ui.base.BaseActivity;
import com.test.yysleep.bluttoothtransmission.util.LogUtil;
import com.test.yysleep.bluttoothtransmission.util.ToastUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by YySleep on 2018/1/17.
 *
 * @author YySleep
 */

public class BlueToothActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "BlueToothActivity";
    private static final int BLUETOOTH_REQUEST_CODE = 300;
    private static final String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    private EditText mChangeNameEdt;
    private Button mSaveNameBtn;
    private Switch mWasDetectedSwitch;

    private List<BluetoothDevice> mBondDevices;
    private List<BluetoothDevice> mDevices;

    private BluetoothAdapter mBlueAdapter;
    private BlueToothDevicesAdapter mDevicesAdapter;
    private BlueToothBondDevicesAdapter mBondDeviceAdapter;

    private BluetoothHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        init();
        initView();
    }

    private void init() {
        mHandler = new BluetoothHandler(this);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
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

        mDevicesAdapter = new BlueToothDevicesAdapter(mDevices);
        mBondDeviceAdapter = new BlueToothBondDevicesAdapter(mBondDevices);

        ListView mBondDevicesLv = findViewById(R.id.bluetooth_bond_devices_lv);
        mBondDevicesLv.setAdapter(mBondDeviceAdapter);
        mBondDevicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlert(position);
            }
        });
        ListView mDevicesLv = findViewById(R.id.bluetooth_devices_lv);
        mDevicesLv.setAdapter(mDevicesAdapter);
        mDevicesLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showAlert(position);
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
            new BlueToothAcceptThread(mBlueAdapter).start();
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
                    new BlueToothAcceptThread(mBlueAdapter).start();
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
        switch (requestCode) {

            case BLUETOOTH_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        LogUtil.d(TAG, "[onActivityResult] 蓝牙已打开 ");
                        break;

                    case RESULT_CANCELED:
                        LogUtil.d(TAG, "[onActivityResult] 蓝牙未打开 ");
                        break;

                    default:
                        LogUtil.d(TAG, "[onActivityResult] requestCode = " + requestCode + " --- resultCode = " + resultCode);
                        break;
                }
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

    private void showAlert(final int position) {
        AlertDialog alertDialog = new AlertDialog.Builder(BlueToothActivity.this, R.style.YMAlertDialogStyle).
                setTitle("是否连接该设备").
                setIcon(R.mipmap.ic_launcher).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothDevice device = mDevices.get(position);
                        new BlueToothConnectThread(mBlueAdapter, device).start();
                        //new BlueToothAcceptThread(mBlueAdapter).start();
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
        private final WeakReference<BlueToothActivity> w;

        public BluetoothHandler(BlueToothActivity activity) {
            w = new WeakReference<BlueToothActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BlueToothActivity activity = w.get();
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
                            ToastUtil.toast(BlueToothActivity.this, "蓝牙已关闭");
                            break;

                        case BluetoothAdapter.STATE_TURNING_ON:
                            ToastUtil.toast(BlueToothActivity.this, "正在打开蓝牙");
                            break;

                        case BluetoothAdapter.STATE_ON:
                            ToastUtil.toast(BlueToothActivity.this, "蓝牙已打开");
                            mWasDetectedSwitch.setVisibility(View.VISIBLE);
                            mBlueAdapter.startDiscovery();
                            mBondDevices.clear();
                            mBondDevices.addAll(mBlueAdapter.getBondedDevices());
                            mBondDeviceAdapter.notifyDataSetChanged();
                            new BlueToothAcceptThread(mBlueAdapter).start();
                            break;

                        case BluetoothAdapter.STATE_TURNING_OFF:
                            ToastUtil.toast(BlueToothActivity.this, "正在关闭蓝牙");
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

}
