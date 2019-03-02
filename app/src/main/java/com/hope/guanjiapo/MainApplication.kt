package com.hope.guanjiapo

import android.app.Activity
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.umeng.commonsdk.UMConfigure
import com.uuzuche.lib_zxing.activity.ZXingLibrary
import java.util.*


/**
 * @author hope.chen, QQ:77132995, email:kazeik@163.com
 * 2018 09 14 14:07
 * 类说明:
 */
class MainApplication : MultiDexApplication() {
    private var activityList: LinkedList<Activity>? = null

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this)
        activityList = LinkedList()
        ZXingLibrary.initDisplayOpinion(this)
//        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, null)
    }


    fun addActivity(activity: Activity?) {
        if (null != activity && activityList != null)
            activityList?.add(activity)
    }

    fun exitApp() {
        if (activityList != null && !activityList!!.isEmpty()) {
            for (temp in activityList!!) {
                temp.finish()
            }
            activityList?.clear()
            activityList = null
        }
    }
}