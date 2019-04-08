package com.hope.guanjiapo.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.RemoteException
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.gprinter.command.EscCommand
import com.gprinter.command.GpCom
import com.gprinter.command.GpUtils
import com.gprinter.command.LabelCommand
import com.gprinter.service.GpPrintService
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.CollectAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.service.PrinterServiceConnection
import com.hope.guanjiapo.utils.ApiUtils.connectionStatus
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.PreferencesUtils
import com.hope.guanjiapo.utils.TimeUtil
import com.hope.guanjiapo.utils.Utils
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.util.*


/**
 * @author kazeik chen
 *         QQ:77132995 email:kazeik@163.com
 *         2019 03 12 10:17
 * 类说明:
 */
class CollectActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_collect
    }

    private val paytype: Array<String> by lazy { resources.getStringArray(R.array.paytype) }
    private val line: String =
        "------------------------------------------------------------------------------------------------------------"
    private var printTime: String = ""
    private var footer: String? = ""
    private val mPrinterIndex = 0
    private var mTotalCopies = 0
    private val MAIN_QUERY_PRINTER_STATUS = 0xfe
    private val REQUEST_PRINT_LABEL = 0xfd
    private val REQUEST_PRINT_RECEIPT = 0xfc
    private var allData: ArrayList<WaybillModel>? = null
    private val adapter: CollectAdapter<LinkedList<String>> by lazy { CollectAdapter<LinkedList<String>>(this) }
    private var conn: PrinterServiceConnection? = null

    private val tempList by lazy { LinkedList<String>() }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        tvTitle.text = "汇总交接单"
        ivBackup.setOnClickListener(this)
        tvTitleRight.visibility = View.VISIBLE
        tvTitleRight.setText(R.string.more)
        tvTitleRight.setOnClickListener(this)

        initDevice()

        allData = intent.getSerializableExtra("data") as ArrayList<WaybillModel>

        printTime = "打印时间:${TimeUtil.getDayByType(
            System.currentTimeMillis(),
            TimeUtil.DATE_YMS
        )}  操作员:${loginModel?.mobile} 总行数:${allData?.size}"
        tvPrintTime.text = printTime
        footer = PreferencesUtils.getString(this, "footer")
        tvInfo.text = footer

//        var allNum = 0.0
//        var allMoney = 0.0
//        allData?.forEach {
//            allMoney += it.baseshipfee
//            allNum += it.productcount
//        }
//
//        tvAllNum.text = "$allNum"
//        tvAllMoney.text = "$allMoney"
        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        rcvData.isNestedScrollingEnabled = false
        rcvData.adapter = adapter

        var temp = PreferencesUtils.getString(this, "prefer")
        if (!TextUtils.isEmpty(temp)) {
            temp = temp?.substring(0, temp.length - 1)
            val arr = temp?.split(",")
            val itemArr = resources.getStringArray(R.array.hzdata)
            val alltemp = ArrayList<LinkedList<String>>()

            tempList.clear()
            arr?.forEach {
                tempList.add(itemArr[it.toInt()])
            }
            alltemp.add(tempList)

            for (j in 0 until allData?.size!!) {
                val sublist = LinkedList<String>()
                for (i in 0 until arr?.size!!) {
                    val tempInt = arr[i].toInt()
                    when (tempInt) {
                        //序号
                        0 -> sublist.add("${j + 1}")
//                        单号
                        1 -> sublist.add(allData?.get(j)?.id!!)
//                        快递单号
                        2 -> sublist.add(allData?.get(j)?.productno!!)
//                        日期
                        3 -> sublist.add(TimeUtil.getDayByType(allData?.get(j)?.createDate!!, TimeUtil.DATE_YMS))
//                        修改时间
                        4 -> sublist.add(TimeUtil.getDayByType(allData?.get(j)?.updateDate!!, TimeUtil.DATE_YMS))
//                        车次
                        5 -> sublist.add(allData?.get(j)?.carname!!)
//                        发货点
                        6 -> sublist.add(allData?.get(j)?.senderaddress!!)
//                        发货人
                        7 -> sublist.add(allData?.get(j)?.sendername!!)
//                        目的地
                        8 -> sublist.add(allData?.get(j)?.receivepoint!!)
//                        收货人
                        9 -> sublist.add(allData?.get(j)?.receivername!!)
//                        货物名称
                        10 -> sublist.add(allData?.get(j)?.productdescript!!)
                        //件数
                        11 -> sublist.add(allData?.get(j)?.id!!)
//                        重重
                        12 -> sublist.add(allData?.get(j)?.productweight!!)
//                        体积
                        13 -> sublist.add(allData?.get(j)?.productsize!!)
//                        中转费
                        14 -> sublist.add(allData?.get(j)?.shipfeesendpay!!)
//                        保费
                        15 -> sublist.add(allData?.get(j)?.insurancefee!!)
//                        应收运费
                        16 -> sublist.add("${allData?.get(j)?.baseshipfee!!}")
//                        成本
                        17 -> sublist.add(allData?.get(j)?.costFee!!)
//                        利润
                        18 -> sublist.add(allData?.get(j)?.id!!)
//                        供应商
                        19 -> sublist.add(allData?.get(j)?.serviceName!!)
//                        支付
                        20 -> sublist.add(allData?.get(j)?.id!!)
//                        已付
                        21 -> sublist.add(allData?.get(j)?.shipfeestate!!)
//                        欠款
                        22 -> sublist.add(allData?.get(j)?.shipfeestate!!)
//                        代收款
                        23 -> sublist.add(allData?.get(j)?.id!!)
//                        小计
                        24 -> sublist.add(allData?.get(j)?.id!!)
//                        业务员
                        25 -> sublist.add(allData?.get(j)?.operatorMobile!!)
//                        返款
                        26 -> sublist.add(allData?.get(j)?.id!!)
//                        回单份数
                        27 -> sublist.add(allData?.get(j)?.copycount!!)
//                        通知放货
                        28 -> sublist.add(allData?.get(j)?.waitnotify!!)
//                        备注
                        29 -> sublist.add(allData?.get(j)?.comment!!)
//                        提货人签名
                        30 -> sublist.add(allData?.get(j)?.id!!)
                    }

                }
                alltemp.add(sublist)
            }
            adapter.setDataEntityList(alltemp)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> showListDialog()
        }
    }

    private fun showListDialog() {
        val items = arrayOf("打印设置", "打印小票")
        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择")
        listDialog.setItems(items) { dialog, which ->
            when (which) {
                0 -> startActivity<ConfigPrintActivity>()
                1 -> {
                    if (connectionStatus)
                        sendReceipt()
                    else
                        startActivityForResult<ConfigPrintActivity>(120)
                }
            }
        }
        listDialog.show()
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Utils.logs("TAG", action)
            // GpCom.ACTION_DEVICE_REAL_STATUS 为广播的IntentFilter
            if (action == GpCom.ACTION_DEVICE_REAL_STATUS) {
                // 业务逻辑的请求码，对应哪里查询做什么操作
                val requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1)
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {
                    val status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16)
                    var str: String
                    if (status == GpCom.STATE_NO_ERR) {
                        connectionStatus = true
                        str = "打印机正常"
                    } else {
                        str = "打印机 "
                        if ((status and GpCom.STATE_OFFLINE).toByte() > 0) {
                            str += "脱机"
                        }
                        if ((status and GpCom.STATE_PAPER_ERR).toByte() > 0) {
                            str += "缺纸"
                        }
                        if ((status and GpCom.STATE_COVER_OPEN).toByte() > 0) {
                            str += "打印机开盖"
                        }
                        if ((status and GpCom.STATE_ERR_OCCURS).toByte() > 0) {
                            str += "打印机出错"
                        }
                        if ((status and GpCom.STATE_TIMES_OUT).toByte() > 0) {
                            str += "查询超时"
                        }
                    }

                    toast(str)
                } else if (requestCode == REQUEST_PRINT_LABEL) { //标签
                    val status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16)
                    if (status == GpCom.STATE_NO_ERR) {
//                        sendLabel()
                    } else {
                        toast("query printer status error")
                    }
                } else if (requestCode == REQUEST_PRINT_RECEIPT) { // 票据
                    val status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16)
                    if (status == GpCom.STATE_NO_ERR) {
//                        sendReceipt()
                    } else {
                        toast("query printer status error")
                    }
                } else if (action == GpCom.ACTION_RECEIPT_RESPONSE) {
                    if (--mTotalCopies > 0) {
//                    sendReceiptWithResponse()
                    }
                } else if (action == GpCom.ACTION_LABEL_RESPONSE) {
                    val data = intent.getByteArrayExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE)
                    val cnt = intent.getIntExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE_CNT, 1)
                    val d = String(data, 0, cnt)
                    /**
                     * 这里的d的内容根据RESPONSE_MODE去判断返回的内容去判断是否成功，具体可以查看标签编程手册SET
                     * RESPONSE指令
                     * 该sample中实现的是发一张就返回一次,这里返回的是{00,00001}。这里的对应{Status,######,ID}
                     * 所以我们需要取出STATUS
                     */
                    Utils.logs("LABEL RESPONSE", d)

                    if (--mTotalCopies > 0 && d[1].toInt() == 0x00) {
//                        sendLabelWithResponse()
                    }
                } else if (action == GpCom.ACTION_CONNECT_STATUS) {
                    val status = intent.getStringExtra(GpPrintService.CONNECT_STATUS)
                    val printerId = intent.getStringExtra(GpPrintService.PRINTER_ID)

                    Utils.logs("tag", "连接状态 $status id = $printerId")
                }
            }
        }
    }

    private fun initDevice() {
        // 注册实时状态查询广播
        registerReceiver(mBroadcastReceiver, IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS))
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE))
        /**
         * 标签模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus(RESPONSE_MODE mode)
         * ，在打印完成后会接收到，action为GpCom.ACTION_LABEL_RESPONSE的广播，特别用于连续打印，
         * 可参照该sample中的sendLabelWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, IntentFilter(GpCom.ACTION_LABEL_RESPONSE))

        registerReceiver(mBroadcastReceiver, IntentFilter(GpCom.ACTION_CONNECT_STATUS))

        connection()
    }

    private fun connection() {
        Utils.logs("tag", "connection")
        conn = PrinterServiceConnection()
        bindService(Intent(this, GpPrintService::class.java), conn, Context.BIND_AUTO_CREATE) // bindService
    }

    /**
     * 打印票据
     */
    private fun sendReceipt() {
        val esc = EscCommand()
        esc.addInitializePrinter()
        esc.addPrintAndFeedLines(3.toByte())
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT)// 设置打印居中
        esc.addSelectPrintModes(
            EscCommand.FONT.FONTA,
            EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.ON,
            EscCommand.ENABLE.ON,
            EscCommand.ENABLE.OFF
        )// 设置为倍高倍宽
        esc.addText("汇总交接单\n") // 打印文字
        esc.addPrintAndLineFeed()

        /* 打印文字 */
        esc.addSelectPrintModes(
            EscCommand.FONT.FONTA,
            EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF
        )// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT)// 设置打印左对齐
        esc.addText("打印时间:$printTime\n") // 打印文字
        esc.addText("$line\n")
        esc.addText("序号")
        esc.addSetHorAndVerMotionUnits(4.toByte(), 0.toByte())
        esc.addText("发货点")
        esc.addSetAbsolutePrintPosition(8.toShort())
        esc.addText("发货人")
        esc.addSetAbsolutePrintPosition(16.toShort())
        esc.addText("目的地")
        esc.addSetAbsolutePrintPosition(24.toShort())
        esc.addText("收货人")
        esc.addSetAbsolutePrintPosition(32.toShort())
        esc.addText("货物名称")
        esc.addSetAbsolutePrintPosition(44.toShort())
        esc.addText("件数")
        esc.addSetAbsolutePrintPosition(52.toShort())
        esc.addText("应收运费")
        esc.addSetAbsolutePrintPosition(64.toShort())
        esc.addText("支付")
        esc.addSetAbsolutePrintPosition(72.toShort())
        esc.addText("代收款")
        esc.addSetAbsolutePrintPosition(82.toShort())
        esc.addText("小计")
        esc.addSetAbsolutePrintPosition(90.toShort())
        esc.addText("备注")
        esc.addSetAbsolutePrintPosition(98.toShort())
        esc.addPrintAndLineFeed()
        esc.addText("$line\n")
        var allNum = 0.0
        var allMoney = 0.0
        var allbaseMoney = 0
        for (i in 0 until tempList.size) {
            esc.addText(tempList[i])
            esc.addSetAbsolutePrintPosition((8*i).toShort())

//            val item = allData?.get(i)
//            esc.addText("${i + 1}")
//            esc.addSetHorAndVerMotionUnits(4.toByte(), 0.toByte())
//            esc.addText("${item?.senderaddress}")
//            esc.addSetAbsolutePrintPosition(8.toShort())
//            esc.addText("${item?.sendername}")
//            esc.addSetAbsolutePrintPosition(16.toShort())
//            esc.addText("${item?.receivepoint}")
//            esc.addSetAbsolutePrintPosition(24.toShort())
//            esc.addText("${item?.receivername}")
//            esc.addSetAbsolutePrintPosition(32.toShort())
//            esc.addText("${item?.productdescript}")
//            esc.addSetAbsolutePrintPosition(44.toShort())
//            esc.addText("${item?.productcount}")
//            esc.addSetAbsolutePrintPosition(52.toShort())
//            esc.addText("${item?.agentmoney}")
//            esc.addSetAbsolutePrintPosition(64.toShort())
//            esc.addText(paytype[item?.shipfeepaytype!!])
//            esc.addSetAbsolutePrintPosition(72.toShort())
//            esc.addText(item.agentmoney)
//            esc.addSetAbsolutePrintPosition(82.toShort())
//            esc.addText("${item.baseshipfee}")
//            esc.addSetAbsolutePrintPosition(90.toShort())
//            esc.addText(item.comment)
//            esc.addSetAbsolutePrintPosition(98.toShort())
//            esc.addPrintAndLineFeed()
//            esc.addText("$line\n")
//
//            allNum += item.productcount
//            allMoney += item.baseshipfee
        }

//        esc.addText("合计")
//        esc.addSetHorAndVerMotionUnits(4.toByte(), 0.toByte())
//        esc.addText("$allNum")
//        esc.addSetAbsolutePrintPosition(82.toShort())
//        esc.addText("$allMoney")
//        esc.addSetAbsolutePrintPosition(90.toShort())
//        esc.addPrintAndLineFeed()
//        esc.addText("$line\n")
        esc.addPrintAndLineFeed()

//        /* 绝对位置 具体详细信息请查看GP58编程手册 */
//        esc.addText("智汇")
//        esc.addSetHorAndVerMotionUnits(7.toByte(), 0.toByte())
//        esc.addSetAbsolutePrintPosition(6.toShort())
//        esc.addText("网络")
//        esc.addSetAbsolutePrintPosition(10.toShort())
//        esc.addText("设备")
//        esc.addPrintAndLineFeed()


        /* 打印文字 */
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER)// 设置打印左对齐
        esc.addText("$footer\r\n") // 打印结束
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F5, 255.toByte(), 255.toByte())
        esc.addPrintAndFeedLines(8.toByte())

        val datas = esc.command // 发送数据
        val bytes = GpUtils.ByteTo_byte(datas)
        val sss = Base64.encodeToString(bytes, Base64.DEFAULT)
        val rs: Int
        try {
            rs = conn?.mGpService?.sendEscCommand(mPrinterIndex, sss)!!
            val r = GpCom.ERROR_CODE.values()[rs]
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(applicationContext, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show()
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        toast("已发送打印")
    }

    private fun checkGPprinter() {
        try {
            conn?.mGpService?.queryPrinterStatus(0, 500, MAIN_QUERY_PRINTER_STATUS)
        } catch (e1: RemoteException) {
            e1.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            120 -> {
                checkGPprinter()
            }
        }
    }
}