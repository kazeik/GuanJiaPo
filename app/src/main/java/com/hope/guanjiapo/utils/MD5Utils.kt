package com.hope.guanjiapo.utils

import android.text.TextUtils
import java.security.MessageDigest

/**
 * @author kazeik chen
 * QQ:77132995 email:kazeik@163.com
 * 2019 01 03 21:41
 * 类说明:
 */
object MD5Utils {
    private val hexDigIts = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f")

    /**
     * MD5加密
     *
     * @param origin      字符
     * @param charsetname 编码
     */
    fun MD5Encode(origin: String, charsetname: String): String {
        var resultString: String? = null
        try {
            resultString = origin
            val md = MessageDigest.getInstance("MD5")
            if (TextUtils.isEmpty(charsetname)) {
                resultString = byteArrayToHexString(md.digest(resultString.toByteArray()))
            } else {
                resultString = byteArrayToHexString(md.digest(resultString.toByteArray(charset(charsetname))))
            }
        } catch (ignored: Exception) {
        }
        return resultString!!
    }

    private fun byteArrayToHexString(b: ByteArray): String {
        val resultSb = StringBuilder()
        for (aB in b) {
            resultSb.append(byteToHexString(aB))
        }
        return resultSb.toString()
    }

    private fun byteToHexString(b: Byte): String {
        var n = b.toInt()
        if (n < 0) {
            n += 256
        }
        val d1 = n / 16
        val d2 = n % 16
        return hexDigIts[d1] + hexDigIts[d2]
    }
}
