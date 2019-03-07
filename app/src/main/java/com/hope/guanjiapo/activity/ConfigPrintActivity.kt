package com.hope.guanjiapo.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.net.Uri
import android.os.IBinder
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.gprinter.aidl.GpService
import com.gprinter.command.GpCom
import com.gprinter.io.GpDevice
import com.gprinter.io.PortParameters
import com.gprinter.service.GpPrintService
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.PrintDeviceAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.PrintDeviceModel
import com.hope.guanjiapo.service.PrinterServiceConnection
import com.hope.guanjiapo.utils.Utils.logs
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_config_print.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class ConfigPrintActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
        connectOrDisconnetDevice(pos)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.btnCheck -> {
                val intent = Intent()
                intent.action = "android.intent.action.VIEW"
                intent.data = Uri.parse("https//h5.m.taobao.com/awp/core/detail.htm?id=552270159252")
                intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
                startActivity(intent)
            }
            R.id.btnSetType -> startActivity<ConfigPrintSetActivity>()
        }
    }

    override fun getLayoutView(): Int {
        return R.layout.activity_config_print
    }

    private var conn: PrinterServiceConnection? = null
    private val allDevice: ArrayList<PrintDeviceModel> by lazy { ArrayList<PrintDeviceModel>() }
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val adapter: PrintDeviceAdapter<PrintDeviceModel> by lazy { PrintDeviceAdapter<PrintDeviceModel>() }
    override fun initData() {
        tvTitle.setText(R.string.set_pring)
        ivBackup.setOnClickListener(this)
        btnCheck.setOnClickListener(this)
        btnSetType.setOnClickListener(this)

        registerBroadcast()

        rcvDevice.layoutManager = LinearLayoutManager(this)
        rcvDevice.adapter = adapter
        rcvDevice.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        adapter.itemEventListener = this

        val flag = initBluetooth()
        if (!flag)
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 100)
        else {
            getDeviceList()
            connection()
        }
    }

    private fun connection() {
        conn = PrinterServiceConnection()
        logs("tag", "connection")
        bindService(Intent(this, GpPrintService::class.java), conn, Context.BIND_AUTO_CREATE) // bindService
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(PrinterStatusBroadcastReceiver)
        if (conn != null) {
            unbindService(conn) // unBindService
        }
    }

    private fun registerBroadcast() {
        val filter = IntentFilter()
        filter.addAction(GpCom.ACTION_CONNECT_STATUS)
        registerReceiver(PrinterStatusBroadcastReceiver, filter)
    }

    private fun connectOrDisconnetDevice(index: Int) {
        try {
            conn?.mGpService?.closePort(0)
            val flag = conn?.mGpService?.openPort(0, PortParameters.BLUETOOTH, allDevice[index].address, 0)
            val r = GpCom.ERROR_CODE.values()[flag!!]
            logs("tag", "连接状态$flag & $r")
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                if (r == GpCom.ERROR_CODE.DEVICE_ALREADY_OPEN) {
                    allDevice[index].status = 3
                    adapter.setDataEntityList(allDevice)
                }
            } else {
                allDevice[index].status = 2
                adapter.setDataEntityList(allDevice)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private val PrinterStatusBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (GpCom.ACTION_CONNECT_STATUS == intent.action) {
                val type = intent.getIntExtra(GpPrintService.CONNECT_STATUS, 0)
                val id = intent.getIntExtra(GpPrintService.PRINTER_ID, 0)
                Log.d("tag", "connect status $type")
//                action.connect.status
                if (type == GpDevice.STATE_CONNECTING) {
//                    setProgressBarIndeterminateVisibility(true)
//                    SetLinkButtonEnable(ListViewAdapter.DISABLE)
//                    mPortParam[id].setPortOpenState(false)
//                    val map: MutableMap<String, Any>
//                    map = mList.get(id)
//                    map[ListViewAdapter.STATUS] = getString(R.string.connecting)
//                    mList.set(id, map)
//                    mListViewAdapter.notifyDataSetChanged()
//
                } else if (type == GpDevice.STATE_NONE) {
//                    setProgressBarIndeterminateVisibility(false)
//                    SetLinkButtonEnable(ListViewAdapter.ENABLE)
//                    mPortParam[id].setPortOpenState(false)
//                    val map: MutableMap<String, Any>
//                    map = mList.get(id)
//                    map[ListViewAdapter.STATUS] = getString(R.string.connect)
//                    mList.set(id, map)
//                    mListViewAdapter.notifyDataSetChanged()
                } else if (type == GpDevice.STATE_VALID_PRINTER) {
//                    setProgressBarIndeterminateVisibility(false)
//                    SetLinkButtonEnable(ListViewAdapter.ENABLE)
//                    mPortParam[id].setPortOpenState(true)
//                    val map: MutableMap<String, Any>
//                    map = mList.get(id)
//                    map[ListViewAdapter.STATUS] = getString(R.string.cut)
//                    mList.set(id, map)
//                    mListViewAdapter.notifyDataSetChanged()
                } else if (type == GpDevice.STATE_INVALID_PRINTER) {
//                    setProgressBarIndeterminateVisibility(false)
//                    SetLinkButtonEnable(ListViewAdapter.ENABLE)
//                    messageBox("Please use Gprinter!")
                }
            }
        }
    }

    private fun initBluetooth(): Boolean {
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(mFindBlueToothReceiver, filter)
        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mFindBlueToothReceiver, filter)
        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        return mBluetoothAdapter?.isEnabled!!
    }

    protected fun getDeviceList() {
        // Get a set of currently paired devices
        val pairedDevices = mBluetoothAdapter?.bondedDevices
        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices?.isNotEmpty()!!) {
            pairedDevices.forEach {
                val model = PrintDeviceModel()
                model.address = it.address
                model.name = it.name
                allDevice.add(model)
            }
            adapter.setDataEntityList(allDevice)
        } else {
            tvDeviceList.text = "未找到设备"
        }
    }

    // changes the title when discovery is finished
    private val mFindBlueToothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            // When discovery finds a device
//            if (BluetoothDevice.ACTION_FOUND == action) {
//                // Get the BluetoothDevice object from the Intent
//                val device = intent
//                    .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                // If it's already paired, skip it, because it's been listed
//                // already
//                if (device.bondState != BluetoothDevice.BOND_BONDED) {
//                    mNewDevicesArrayAdapter.add(
//                        device.name + "\n"
//                                + device.address
//                    )
//                }
//                // When discovery is finished, change the Activity title
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
//                setProgressBarIndeterminateVisibility(false)
//                setTitle(R.string.select_bluetooth_device)
//                Log.i("tag", "finish discovery" + mNewDevicesArrayAdapter.getCount())
//                if (mNewDevicesArrayAdapter.getCount() == 0) {
//                    val noDevices = resources.getText(
//                        R.string.none_bluetooth_device_found
//                    ).toString()
//                    mNewDevicesArrayAdapter.add(noDevices)
//                }
//            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            100 -> {
                getDeviceList()
                connection()
            }
        }
    }
}
