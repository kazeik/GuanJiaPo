package com.hope.guanjiapo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.format.Formatter

import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap
import java.io.*


//import org.apache.http.util.EncodingUtils;

@SuppressLint("NewApi")
class SdcardUtils {

    fun saveBitmapAsFile(saveFile: File, bitmap: Bitmap): Boolean {
        var saved = false
        var os: FileOutputStream? = null
        try {
            os = FileOutputStream(saveFile)
            bitmap.compress(CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
            saved = true
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return saved
    }

    /**
     * 获取SD路径
     *
     * @return /sdcard
     */
    // 判断sd卡是否存在
    // 获取跟目录
    val sdPath: String
        get() {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val sdDir = Environment.getExternalStorageDirectory()
                return sdDir.path
            }
            return "/mnt/sdcard"
        }

    /**
     * 获取SD卡路径
     *
     * @return
     */
    val sdCardPath: String
        get() = Environment.getExternalStorageDirectory().toString() + "/"

    /**
     * 检查SD卡是否插好
     */
    fun SDCardIsOk(): Boolean {
        return if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            true
        } else {
            false
        }
    }

    /**
     * 创建文件夹
     *
     * @param dirName
     */
    fun createDir(dirName: String) {
        val destDir = File(dirName)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
    }


    /**
     * 在SD卡上创建文件
     *
     * @param fileName
     * @return
     */
    fun creatSDFile(fileName: String): File {
        val file = File(sdCardPath + fileName)
        try {
            file.createNewFile()
        } catch (e: IOException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

        return file
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     * @return
     */
    fun createSDDir(dirName: String): File {
        val dir = File(sdCardPath + dirName)
        dir.mkdir()
        return dir
    }

    /**
     * 检查SD卡上的文件夹是否存在
     *
     * @param fileName
     * @return
     */
    fun isFileExist(fileName: String): Boolean {
        val file = File(sdCardPath + fileName)
        return file.exists()
    }

    /**
     * 判断文件是否存在
     *
     * @param name 文件名
     * @return
     */
    fun fileExist(name: String): Boolean {
        val f = File(sdCardPath + name)
        return if (f.exists()) {
            true
        } else {
            false
        }
    }

    /**
     * 将InputStream里面的数据写入到SD卡中
     *
     * @param path     文件夹路径
     * @param fileName 文件名
     * @param input    输入流
     * @return
     */
    fun writeFileToSDCard(
        path: String, fileName: String,
        input: InputStream
    ): File? {
        var file: File? = null
        var ops: OutputStream? = null

        try {
            createSDDir(path)
            file = creatSDFile(path + fileName)
            ops = FileOutputStream(file)
            val buffer = ByteArray(4 * 1024)
            while (input.read(buffer) != -1) {
                ops.write(buffer)
            }
            ops.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                if (null != ops) {
                    ops.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return file
    }

    /**
     * 输入流转化成图片
     *
     * @param is          输入流
     * @param imgPathTemp 文件夹路径
     * @param fileName    文件名
     * @return
     */
    fun inputToFile(`is`: InputStream, imgPathTemp: String, fileName: String): File? {
        // String imgPathTemp = SDFileUtils.getSDPath()
        // + SlookConstant.userAvatarUrl;
        createDir(imgPathTemp)
        val file = File(imgPathTemp, fileName)// 保存文件
        // Logs.v(SDFileUtils.class, true, imgPathTemp + "  |  " + fileName);
        try {
            if (!file.exists() && !file.isDirectory) {
                // 可以在这里通过文件名来判断，是否本地有此图片
                val fos = FileOutputStream(file)
                var data = `is`.read()
                while (data != -1) {
                    fos.write(data)
                    data = `is`.read()
                }
                fos.close()
                `is`.close()
            }
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 根据byte数组，生成文件
     */
    fun getFile(bfile: ByteArray, imgPathTemp: String, fileName: String) {
        var bos: BufferedOutputStream? = null
        var fos: FileOutputStream? = null
        createDir(imgPathTemp)
        val file = File(imgPathTemp, fileName)// 保存文件
        try {
            fos = FileOutputStream(file)
            bos = BufferedOutputStream(fos)
            bos.write(bfile)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (bos != null) {
                try {
                    bos.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }
            if (fos != null) {
                try {
                    fos.close()
                } catch (e1: IOException) {
                    e1.printStackTrace()
                }

            }
        }
    }


    /**
     * 数据转化成文件
     *
     * @param datas       数据源
     * @param imgPathTemp 文件夹路径
     * @param fileName    文件名
     * @return
     */
    fun ByteToFile(datas: ByteArray, imgPathTemp: String, fileName: String): File? {
        createDir(imgPathTemp)
        val file = File(imgPathTemp, fileName)// 保存文件
        try {
            if (!file.exists() && !file.isDirectory) {
                // 可以在这里通过文件名来判断，是否本地有此图片
                val fos = FileOutputStream(file)
                val bais = ByteArrayInputStream(datas)
                var data = bais.read()
                while (data != -1) {
                    fos.write(data)
                    data = bais.read()
                }
                fos.close()
                bais.close()
            }
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 写文件到SD卡
     *
     * @param fileName 文件名
     * @param message  文件内容
     * @author ck
     * @date 2013-1-10 下午04:35:32
     */
    fun writeFileSdcard(fileName: String, message: String) {
        try {
            val fout = FileOutputStream(fileName)
            val bytes = message.toByteArray()
            fout.write(bytes)
            fout.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 删除文件
     *
     * @param path
     */
    fun deleteFile(path: String) {
        val file = File(path)
        file.delete()
    }

    // 获得系统可用内存信息
    fun getSystemAvaialbeMemorySize(ct: Context): String {
        // 获得ActivityManager服务的对象
        val mActivityManager = ct
            .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        // 获得MemoryInfo对象
        val memoryInfo = MemoryInfo()
        // 获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo)
        val memSize = memoryInfo.availMem

        // 字符类型转换
        return formateFileSize(memSize, ct)
    }

    // 调用系统函数，字符串转换 long -String KB/MB
    private fun formateFileSize(size: Long, ct: Context): String {
        return Formatter.formatFileSize(ct, size)
    }

    /**
     * 获取内存卡容量大小
     *
     * @param path
     * @return
     */
    fun getRoomSize(path: String): Long {
        val file = File(path)
        return file.length()
    }

    fun uriToFile(cont: Activity, uri: Uri): File {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val actualimagecursor = cont.managedQuery(uri, proj, null, null, null)
        val actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        actualimagecursor.moveToFirst()
        val img_path = actualimagecursor.getString(actual_image_column_index)
        actualimagecursor.close()
        return File(img_path)
    }

    companion object {

        /**
         * 写入日志
         *
         * @param msg
         * @author kazeik.chen QQ:77132995 2014-4-8下午2:33:21 TODO kazeik@163.com
         */
        fun writeLog(path: String, msg: String) {
            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                return
            }
            val file = File(path)
            var writer: FileWriter? = null
            try {
                // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
                writer = FileWriter(file.absolutePath, true)
                writer.write("$msg \n")
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    if (null != writer) {
                        writer.close()
                        writer = null
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        fun getCacheFile(imageUri: String): File? {
            var cacheFile: File? = null
            try {
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    val sdCardDir = Environment.getExternalStorageDirectory()
                    val fileName = getFileName(imageUri)
                    val dir = File(sdCardDir.canonicalPath + "cache")
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    cacheFile = File(dir, fileName)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return cacheFile
        }

        fun getFileName(path: String): String {
            val index = path.lastIndexOf("/")
            return path.substring(index + 1)
        }
    }


    //    //读SD中的文件
    //    public static String readFileSdcardFile(String fileName) throws IOException {
    //        String res = "";
    //        try {
    //            FileInputStream fin = new FileInputStream(fileName);
    //            int length = fin.available();
    //            byte[] buffer = new byte[length];
    //            fin.read(buffer);
    //            res = EncodingUtils.getString(buffer, "UTF-8");
    //            fin.close();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //        return res;
    //    }
}
