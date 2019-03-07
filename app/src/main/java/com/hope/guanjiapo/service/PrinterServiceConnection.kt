package com.hope.guanjiapo.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import com.gprinter.aidl.GpService
import com.hope.guanjiapo.utils.Utils


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 03 07 11:55
 * 类说明:
 */
class PrinterServiceConnection : ServiceConnection {
    var mGpService: GpService? = null
    override fun onServiceDisconnected(name: ComponentName) {
        Utils.logs("tag", "onServiceDisconnected() called")
        mGpService = null
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        mGpService = GpService.Stub.asInterface(service)
    }

}