package com.hope.guanjiapo.activity

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.PrintDeviceAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.PrintDeviceModel
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_config_print.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity

class ConfigPrintActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {
    override fun onItemEvent(pos: Int) {
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

    private val allDevice: ArrayList<PrintDeviceModel> by lazy { ArrayList<PrintDeviceModel>() }
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private val adapter: PrintDeviceAdapter<PrintDeviceModel> by lazy { PrintDeviceAdapter<PrintDeviceModel>() }
    override fun initData() {
        tvTitle.setText(R.string.set_pring)
        ivBackup.setOnClickListener(this)
        btnCheck.setOnClickListener(this)
        btnSetType.setOnClickListener(this)

        rcvDevice.layoutManager = LinearLayoutManager(this)
        rcvDevice.adapter = adapter
        rcvDevice.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        adapter.itemEventListener = this

        getDeviceList()
    }

    protected fun getDeviceList() {
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(mFindBlueToothReceiver, filter)
        // Register for broadcasts when discovery has finished
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mFindBlueToothReceiver, filter)
        // Get the local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        // Get a set of currently paired devices
        val pairedDevices = mBluetoothAdapter?.getBondedDevices()
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
}
