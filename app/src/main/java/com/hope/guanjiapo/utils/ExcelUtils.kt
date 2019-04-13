package com.hope.guanjiapo.utils

import android.content.Context
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Colour
import jxl.write.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 04 13 15:21
 * 类说明:
 */
object ExcelUtils {
    var arial14font: WritableFont? = null

    var arial14format: WritableCellFormat? = null
    var arial10font: WritableFont? = null
    var arial10format: WritableCellFormat? = null
    var arial12font: WritableFont? = null
    var arial12format: WritableCellFormat? = null

    val UTF8_ENCODING = "UTF-8"
    val GBK_ENCODING = "GBK"


    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    fun format() {
        try {
            arial14font = WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD)
            arial14font!!.colour = jxl.format.Colour.LIGHT_BLUE
            arial14format = WritableCellFormat(arial14font)
            arial14format!!.alignment = jxl.format.Alignment.CENTRE
            arial14format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN)
            arial14format!!.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW)

            arial10font = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)
            arial10format = WritableCellFormat(arial10font)
            arial10format!!.alignment = jxl.format.Alignment.CENTRE
            arial10format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN)
            arial10format!!.setBackground(Colour.GRAY_25)

            arial12font = WritableFont(WritableFont.ARIAL, 10)
            arial12format = WritableCellFormat(arial12font)
            arial10format!!.alignment = jxl.format.Alignment.CENTRE//对齐格式
            arial12format!!.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN) //设置边框

        } catch (e: WriteException) {
            e.printStackTrace()
        }

    }

    /**
     * 初始化Excel
     * @param fileName
     * @param colName
     */
    fun initExcel(fileName: String, colName: Array<String>, sheetName: String = "") {
        format()
        var workbook: WritableWorkbook? = null
        try {
            val file = File(fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
            workbook = Workbook.createWorkbook(file)
            val sheet = workbook!!.createSheet(sheetName, 0)
            //创建标题栏
            sheet.addCell(Label(0, 0, fileName, arial14format) as WritableCell)
            for (col in colName.indices) {
                sheet.addCell(Label(col, 0, colName[col], arial10format))
            }
            sheet.setRowView(0, 340) //设置行高

            workbook.write()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (workbook != null) {
                try {
                    workbook.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun <T> writeObjListToExcel(objList: List<T>?, fileName: String, c: Context): Boolean {
        var flag: Boolean = false
        if (objList != null && objList.isNotEmpty()) {
            var writebook: WritableWorkbook? = null
            var `in`: InputStream? = null
            try {
                val setEncode = WorkbookSettings()
                setEncode.encoding = UTF8_ENCODING
                `in` = FileInputStream(File(fileName))
                val workbook = Workbook.getWorkbook(`in`)
                writebook = Workbook.createWorkbook(File(fileName), workbook)
                val sheet = writebook!!.getSheet(0)
                for (j in objList.indices) {
                    val list = objList[j] as List<String>
                    for (i in 0 until list.size) {
                        sheet.addCell(Label(i, j + 1, list[i], arial12format))
                        if (list[i].length <= 5) {
                            sheet.setColumnView(i, list[i].length + 8) //设置列宽
                        } else {
                            sheet.setColumnView(i, list[i].length + 5) //设置列宽
                        }
                    }
                    sheet.setRowView(j + 1, 350) //设置行高
                }

                writebook.write()
                flag = true
            } catch (e: Exception) {
                e.printStackTrace()
                flag = false
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        flag = false
                    }
                }
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                        flag = false
                    }
                }
            }
        }
        return flag
    }
}