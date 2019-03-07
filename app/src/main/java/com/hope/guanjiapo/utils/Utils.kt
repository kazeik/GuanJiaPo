package com.hope.guanjiapo.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.util.Log
import com.google.gson.Gson
import com.hope.guanjiapo.BuildConfig
import java.util.regex.Pattern

/**
 * @author kazeik.chen, QQ:77132995, email:kazeik@163.com
 * 2018 09 14 20:59
 * 类说明:
 */
object Utils {

    fun logs(cls: Class<*>, msg: String) {
        logs(cls.simpleName, msg)
    }

    fun logs(tag: String = "", msg: String) {
        if (BuildConfig.LOG_DEBUG)
            Log.e(tag, msg)
    }

    inline fun <reified T : Any> parserJson(data: String): T {
        return Gson().fromJson(data, T::class.java)
    }


    fun isPhone(phone: String): Boolean {
        val regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$"
        if (phone.length != 11) {
            return false
        } else {
            val p = Pattern.compile(regex)
            val m = p.matcher(phone)
            return m.matches()
        }
    }

    fun toChinese(str: String): String {
        val s1: Array<String> = arrayOf("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖")
        val s2: Array<String> = arrayOf("拾", "佰", "仟", "萬", "拾", "佰", "仟", "亿", "拾", "佰", "仟")
        var result: String = ""
        val n: Int = str.length
        for (i in 0 until n) {
            val num: Int = str.get(i) - '0'
            if (i != n - 1 && num != 0) {
                result += s1[num] + s2[n - 2 - i]
            } else {
                result += s1[num]
            }
        }
        return result
    }


    /**
     * 检测程序是否安装
     *
     * @param packageName
     * @return
     */
    fun isInstalled(mContext: Context, packageName: String): Boolean {
        val installedPackages: List<PackageInfo> = mContext.packageManager.getInstalledPackages(0)
        return installedPackages.any { it.packageName.equals(packageName) }
    }
}