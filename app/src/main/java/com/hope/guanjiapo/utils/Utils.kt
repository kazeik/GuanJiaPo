package com.hope.guanjiapo.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.hope.guanjiapo.BuildConfig
import java.math.RoundingMode
import java.text.NumberFormat
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

    fun formatDouble(d: Double): String {
        val nf = NumberFormat.getNumberInstance()
        nf.maximumFractionDigits = 2
        nf.roundingMode = RoundingMode.UP
        return nf.format(d)
    }

    fun digitUppercase(value: Double): String {
        var n = value
        val fraction = arrayOf("角", "分")
        val digit = arrayOf("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖")
        val unit = arrayOf(arrayOf("元", "万", "亿"), arrayOf("", "拾", "佰", "仟"))

        val head = if (n < 0) "负" else ""
        n = Math.abs(n)

        var s = ""
        for (i in fraction.indices) {
            s += (digit[(Math.floor(
                n * 10.0 * Math.pow(
                    10.0,
                    i.toDouble()
                )
            ) % 10).toInt()] + fraction[i]).replace("(零.)+".toRegex(), "")
        }
        if (s.length < 1) {
            s = "整"
        }
        var integerPart = Math.floor(n).toInt()

        var i = 0
        while (i < unit[0].size && integerPart > 0) {
            var p = ""
            var j = 0
            while (j < unit[1].size && n > 0) {
                p = digit[integerPart % 10] + unit[1][j] + p
                integerPart = integerPart / 10
                j++
            }
            s = p.replace("(零.)*零$".toRegex(), "").replace("^$".toRegex(), "零") + unit[0][i] + s
            i++
        }
        return head + s.replace("(零.)*零元".toRegex(), "元").replaceFirst("(零.)+".toRegex(), "").replace(
            "(零.)+".toRegex(),
            "零"
        ).replace(
            "^整$".toRegex(), "零元整"
        )
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