package com.hope.guanjiapo.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.RemoteException
import android.text.TextUtils
import android.util.Base64
import android.view.View
import android.widget.RadioGroup
import com.gprinter.command.EscCommand
import com.gprinter.command.GpCom
import com.gprinter.command.GpUtils
import com.gprinter.command.LabelCommand
import com.gprinter.service.GpPrintService
import com.hope.guanjiapo.R
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.model.ConsigneeModel
import com.hope.guanjiapo.model.DestinationModel
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.service.PrinterServiceConnection
import com.hope.guanjiapo.utils.ApiUtils
import com.hope.guanjiapo.utils.ApiUtils.connectionStatus
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.utils.ApiUtils.staffModel
import com.hope.guanjiapo.utils.PreferencesUtils
import com.hope.guanjiapo.utils.TimeUtil
import com.hope.guanjiapo.utils.Utils.digitUppercase
import com.hope.guanjiapo.utils.Utils.formatDouble
import com.hope.guanjiapo.utils.Utils.logs
import kotlinx.android.synthetic.main.activity_print_order_info.*
import kotlinx.android.synthetic.main.view_title.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import org.json.JSONObject


class PrintOrderInfoActivity : BaseActivity(), View.OnClickListener {
    override fun getLayoutView(): Int {
        return R.layout.activity_print_order_info
    }

    private val mPrinterIndex = 0
    private val items by lazy { resources.getStringArray(R.array.bzdw) }
    private var ccStr: String? = ""
    private var bzdwStr: Int? = 0
    private var recway: Int = 0
    private var shipfeepaytype: Int = 0
    private var fkStr: String? = ""
    private var hdfsStr: String? = ""
    private var tzfh: Int = 0
    private var pay: Int? = 0
    private val MAIN_QUERY_PRINTER_STATUS = 0xfe
    private val REQUEST_PRINT_LABEL = 0xfd
    private val REQUEST_PRINT_RECEIPT = 0xfc
    private var mTotalCopies = 0

    private val paytype: Array<String> by lazy { resources.getStringArray(R.array.paytype) }
    private val recwaytype: Array<String> by lazy { resources.getStringArray(R.array.recwaytype) }
    private val bzdw by lazy { resources.getStringArray(R.array.bzdw) }
    //    private var fhr :String ?= ""
    private var fhrModel: ConsigneeModel? = null //发货人
    private var shrModel: ConsigneeModel? = null //货人

    private var waybillModel: WaybillModel? = null

    private var conn: PrinterServiceConnection? = null

    private fun connection() {
        conn = PrinterServiceConnection()
        bindService(Intent(this, GpPrintService::class.java), conn, Context.BIND_AUTO_CREATE) // bindService
        logs("tag", "connection")
        checkGPprinter()
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            logs("TAG", action)
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
                            str += "开盖"
                        }
                        if ((status and GpCom.STATE_ERR_OCCURS).toByte() > 0) {
                            str += "出错"
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
                    logs("LABEL RESPONSE", d)

                    if (--mTotalCopies > 0 && d[1].toInt() == 0x00) {
//                        sendLabelWithResponse()
                    }
                } else if (action == GpCom.ACTION_CONNECT_STATUS) {
                    val status = intent.getStringExtra(GpPrintService.CONNECT_STATUS)
                    val printerId = intent.getStringExtra(GpPrintService.PRINTER_ID)

                    logs("tag", "连接状态 $status id = $printerId")
                }
            }
        }
    }

    /**
     * 公司名（大字号）
     * 发货点：            单号：   业务：18888888888（业务员的电话，中字号）
     * ------------------
     * 收货方：（大字号）                    电话：
     * 收货地址：
     * 发货方：                    电话：
     * 数量： （大字号）            ***************
     * 货物名称：                  ***************
     * 代收款：  中转：             ***************
     * 运费：      现付            ***************
     * 备注：
     * 开单时间：
     * --------------------
     * 谢谢惠顾
     * 打印程序：
     */
    private fun printBq() {
        val type = conn?.mGpService?.getPrinterCommandType(mPrinterIndex)
        logs("tag", "type = $type")
        if (type == GpCom.ESC_COMMAND) {
            toast("请切换到标签模式下打印")
            startActivityForResult<ConfigPrintActivity>(120)
            return
        }
        val xstart = 2
        val ystart = 140
        val item = waybillModel
        val tsc = LabelCommand()
        tsc.addSize(75, 100) // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0) // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.BACKWARD, LabelCommand.MIRROR.NORMAL)// 设置打印方向
        tsc.addReference(0, 10)// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON) // 撕纸模式开启
        tsc.addCls()// 清除打印缓冲区

        tsc.addText(
            200,
            ystart,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_2,
            LabelCommand.FONTMUL.MUL_2,
            "${ApiUtils.vehicleModel?.companyname}"
        )

        // 绘制简体中文
        tsc.addText(
            xstart,
            200,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_2,
            "${item?.senderaddress}=>${item?.receivepoint}   单号:${item?.id} 业务:${item?.operatorMobile}"
        )

        tsc.addBar(xstart, 250, 550, 2)
        tsc.addText(
            xstart,
            265,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_2,
            "收货方:${item?.receivername}"
        )
        tsc.addText(
            330,
            265,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "电话:${item?.receiverphone}"
        )
        tsc.addText(
            xstart,
            320,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "收货地址:${item?.receiveraddress}"
        )

        tsc.addText(
            xstart,
            360,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "发货方:${item?.sendername}           电话:${item?.senderphone}"
        )
        tsc.addText(
            xstart,
            400,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_2,
            "数量:${item?.productcount}"
        )
        tsc.addText(
            xstart,
            470,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "货物名称:${item?.productdescript}"
        )
        tsc.addText(
            xstart,
            520,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "代收款:${if (TextUtils.isEmpty(item?.agentmoney)) "0" else item?.agentmoney}  中转:${if (TextUtils.isEmpty(item?.shipfeesendpay)) "" else item?.shipfeesendpay}"
        )
        tsc.addText(
            xstart,
            570,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "运费:${item?.shipfee}  ${paytype[item?.shipfeepaytype!!]}"
        )

        tsc.addText(
            xstart,
            620,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "备注:${if (TextUtils.isEmpty(item.comment)) "" else item.comment}"
        )

        tsc.addText(
            xstart,
            660,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "开单时间:${TimeUtil.getDayByType(item.createDate!!, TimeUtil.DATE_YMD_HMS)} 扫描查询物流"
        )
//        寄件联要添加线
//        tsc.addBar(xstart, 350, 520, 2)
        // 绘制图片
//        val b = BitmapFactory.decodeResource(resources, R.drawable.gprinter)
//        tsc.addBitmap(20, 50, LabelCommand.BITMAP_MODE.OVERWRITE, b.width, b)

        tsc.addQRCode(
            320,
            400,
            LabelCommand.EEC.LEVEL_L,
            6,
            LabelCommand.ROTATION.ROTATION_0,
            "https://wl56.mmd520.cn/api/order/wlcustomersearch?orderid=${item.id}"
        )
//        // 绘制一维条码
//        tsc.add1DBarcode(
//            20,
//            250,
//            LabelCommand.BARCODETYPE.CODE128,
//            100,
//            LabelCommand.READABEL.EANBEL,
//            LabelCommand.ROTATION.ROTATION_0,
//            "SMARNET"
//        )
        tsc.addPrint(1, 1) // 打印标签
//        tsc.addSound(2, 100) // 打印标签后 蜂鸣器响
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255)
        val datas = tsc.command // 发送数据
        val bytes = GpUtils.ByteTo_byte(datas)
        val str = Base64.encodeToString(bytes, Base64.DEFAULT)
        val rel: Int
        try {
            rel = conn?.mGpService?.sendLabelCommand(mPrinterIndex, str)!!
            val r = GpCom.ERROR_CODE.values()[rel]
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                toast(GpCom.getErrorText(r))
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * @param pos
     *          打印小票
     *          公司名称
     *          现付 自提
     *   ---------------
     *   单号：     日期：
     *   广州 =》 江苏
     *   ----------------
     *   收货人：   电话：
     *   收货地址
     *   ----------------
     *   发货人：
     *   ----------------
     *   品名：  件数：   包装：
     *   -------------------
     *   收款方式：  提贷方式：  回单：
     *   -------------------------
     *   运费合计： 108  壹佰零捌元整
     *   代收款：
     *   ----------------
     *   备注
     *   -----------------
     *   谢谢惠顾
     *    **********
     *    **********
     *    **********
     *    **********
     *    **********
     *   扫码查询运单
     */
    private fun printXp() {
        val type = conn?.mGpService?.getPrinterCommandType(mPrinterIndex)
        if (type == GpCom.LABEL_COMMAND) {
            toast("请切换到小票模式下打印")
            startActivityForResult<ConfigPrintActivity>(120)
            return
        }
        val esc = EscCommand()
        esc.addInitializePrinter()
        esc.addPrintAndFeedLines(3.toByte())
        esc.addSetHorAndVerMotionUnits(0, 0)
        esc.addSelectDefualtLineSpacing()
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER)// 设置打印居中
        esc.addSelectPrintModes(
            EscCommand.FONT.FONTA,
            EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.ON,
            EscCommand.ENABLE.OFF,
            EscCommand.ENABLE.OFF
        )// 设置为倍高倍宽
        esc.addText("${ApiUtils.vehicleModel?.companyname}\n")
        esc.addText("${paytype[waybillModel?.shipfeepaytype!!]} ${recwaytype[waybillModel?.recway!!]}\n") // 打印文字
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
        esc.addText(
            "单号:${waybillModel?.id}  日期:${TimeUtil.getDayByType(
                waybillModel?.updateDate!!,
                TimeUtil.DATE_YMD_HMS
            )}\n"
        ) // 打印文字

        esc.addText("${waybillModel?.senderaddress} => ${waybillModel?.receivepoint}\n") // 打印文字
        esc.addText(ApiUtils.line)
        /* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText("收货人:${waybillModel?.receivername}  电话:${waybillModel?.receiverphone}\n")
        esc.addText("收货地址:${waybillModel?.receiveraddress}\n")
        esc.addText(ApiUtils.line)
        esc.addText("发货人:${waybillModel?.sendername}\n")
        esc.addText(ApiUtils.line)
        esc.addText("品名:${waybillModel?.productdescript} 件数:${waybillModel?.productcount}  包装:${bzdw[waybillModel?.recno!!]}\n")
        esc.addText(ApiUtils.line)
        esc.addText("付款方式:${paytype[waybillModel?.shipfeepaytype!!]} 提货方式:${recwaytype[waybillModel?.recway!!]}  回单:${waybillModel?.copycount}\n")
        esc.addText(ApiUtils.line)
        esc.addText("运费合计:${waybillModel?.shipfee} ${digitUppercase(if (TextUtils.isEmpty(waybillModel?.shipfee)) 0.0 else waybillModel?.shipfee?.toDouble()!!)}\n")
        esc.addText("代收款:${waybillModel?.agentmoney} \n")
        esc.addText(ApiUtils.line)
        esc.addText("备注:${waybillModel?.comment}\n")
        esc.addText(ApiUtils.line)
        /*
		 * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		 */
        val footer = PreferencesUtils.getString(this, "footer")
        esc.addText("$footer\n") // 打印文字
        esc.addSelectErrorCorrectionLevelForQRCode(0x31.toByte()) // 设置纠错等级
        esc.addSelectSizeOfModuleForQRCode(6.toByte())// 设置qrcode模块大小
        esc.addStoreQRCodeData("https://wl56.mmd520.cn/api/order/wlcustomersearch?orderid=${waybillModel?.id}")// 设置qrcode内容
        esc.addPrintQRCode()// 打印QRCode
        esc.addPrintAndLineFeed()

//        /* 打印文字 */
//        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER)// 设置打印左对齐
        esc.addText("扫码查询运单!\r\n") // 打印结束
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F5, 255.toByte(), 255.toByte())
        esc.addPrintAndFeedLines(8.toByte())

        // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addQueryPrinterStatus()

        val datas = esc.command // 发送数据
        val bytes = GpUtils.ByteTo_byte(datas)
        val sss = Base64.encodeToString(bytes, Base64.DEFAULT)
        val rs: Int
        try {
            rs = conn?.mGpService?.sendEscCommand(mPrinterIndex, sss)!!
            val r = GpCom.ERROR_CODE.values()[rs]
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                toast(GpCom.getErrorText(r))
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
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


    @SuppressLint("SetTextI18n")
    override fun initData() {
        tvTitle.setText(R.string.orderinfo)
        tvTitleRight.setText(R.string.save)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)
        tvFwhy.setOnClickListener(this)
        tvCc.setOnClickListener(this)
        tvBzdw.setOnClickListener(this)
        tvFhd.setOnClickListener(this)
        ivMdd.setOnClickListener(this)
        ivShr.setOnClickListener(this)
        ivGys.setOnClickListener(this)
        ivLxr.setOnClickListener(this)
        ivFhd.setOnClickListener(this)

        btnPrintXp.setOnClickListener(this)
        btnPrintBq.setOnClickListener(this)
        btnDelete.setOnClickListener(this)

        waybillModel = intent.getSerializableExtra("data") as WaybillModel

        etJbyf.setText(waybillModel?.baseshipfee)
        etOrderId.setText(waybillModel?.id!!)
        etMdd.setText(waybillModel?.receivepoint)
        etShr.setText(waybillModel?.receivername)
        etPsh.setText(waybillModel?.dispatchfee)
        etZzh.setText(waybillModel?.shipfeesendpay)
        etGys.setText(waybillModel?.serviceName)
        etFhr.setText(waybillModel?.sendername)
        etTj.setText(waybillModel?.productsize)
        etHwsl.setText(waybillModel?.productcount)
        etCb.setText(waybillModel?.costFee)
        etBz.setText(waybillModel?.comment)
        etKddh.setText(waybillModel?.productno)
        etHwmc.setText(waybillModel?.productdescript)
        etZl.setText(waybillModel?.productweight)
        etDsk.setText(waybillModel?.agentmoney)
        etFhd.setText(waybillModel?.senderaddress)
        etBf.setText(waybillModel?.insurancefee)
        tvCc.text = waybillModel?.carname

        val yf =
            getValue(waybillModel?.baseshipfee) + getValue(waybillModel?.dispatchfee) + getValue(waybillModel?.insurancefee)
        val al = yf + getValue(waybillModel?.agentmoney) + getValue(waybillModel?.shipfeesendpay)
        tvAllMoney.text =
            "运费：${formatDouble(yf)} 合计(含代收中转):${formatDouble(al)}"

        if (staffModel != null && staffModel?.isNotEmpty()!!) {
            val tempStaff = staffModel?.singleOrNull { it.mobile == waybillModel?.operatorMobile }
            etYwy.setText(tempStaff?.userName)
        }

        val stat = resources.getStringArray(R.array.orderstatus)
        tvOrderStatus.text = stat[waybillModel?.oderstate!!]

        tvBzdw.text = items[waybillModel?.recno!!]

        mPayTypeGroup.setOnCheckedChangeListener { _: RadioGroup, i: Int ->
            when (i) {
                R.id.rbXf -> shipfeepaytype = 0
                R.id.rbYj -> shipfeepaytype = 1
                R.id.rbTf -> shipfeepaytype = 2
            }
        }
        msgShfs.setOnCheckedChangeListener { _: RadioGroup, i: Int ->
            when (i) {
                R.id.rbZt -> recway = 0
                R.id.rbPs -> recway = 1
            }
        }

        payGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbQk -> pay = 0
                R.id.rbYf -> pay = 1
            }
        }

        if (waybillModel?.recway == 0) {
            rbZt.isChecked = true
        } else
            rbPs.isChecked = true

        when (waybillModel?.shipfeepaytype) {
            0 -> {
                rbXf.isChecked = true
            }
            1 -> {
                rbYj.isChecked = true
            }
            2 -> rbTf.isChecked = true
        }

        when (waybillModel?.shipfeestate) {
            0 -> {
                rbQk.isChecked = true
            }
            1 -> {
                rbYf.isChecked = true
            }
        }

        etJbyf.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
        etPsh.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
        etBf.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
        etZzh.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }
        etDsk.setOnFocusChangeListener { _, b ->
            if (!b) check()
        }

        fkStr = waybillModel?.returnmoney
        hdfsStr = waybillModel?.copycount
        tzfh = if (TextUtils.isEmpty(waybillModel?.waitnotify)) 0 else waybillModel?.waitnotify?.toInt()!!
        initDevice()
    }

    private fun checkGPprinter() {
        try {
            conn?.mGpService?.queryPrinterStatus(0, 500, MAIN_QUERY_PRINTER_STATUS)
        } catch (e1: RemoteException) {
            e1.printStackTrace()
        }
    }

    private fun getValue(value: String?): Double {
        return if (TextUtils.isEmpty(value)) 0.0 else value?.toDouble()!!
    }


    @SuppressLint("SetTextI18n")
    private fun check() {
        val item1 = etJbyf.text.toString()
        val item2 = etPsh.text.toString()
        val item3 = etBf.text.toString()
        val item4 = etZzh.text.toString()
        val item5 = etDsk.text.toString()
        val p0: Double? = if (TextUtils.isEmpty(item1)) 0.0 else item1.toDouble()
        val p1: Double? = if (TextUtils.isEmpty(item2)) 0.0 else item2.toDouble()
        val p2: Double? = if (TextUtils.isEmpty(item3)) 0.0 else item3.toDouble()
        val p3: Double? = if (TextUtils.isEmpty(item4)) 0.0 else item4.toDouble()
        val p4: Double? = if (TextUtils.isEmpty(item5)) 0.0 else item5.toDouble()
        val yf = p0!! + p1!! + p2!!
        val hj = p0 + p1 + p2 + p3!! + p4!!
        tvAllMoney.text = "运费：${formatDouble(yf)} 合计(含代收中转):${formatDouble(hj)}"
    }

    private fun showBzdwListDialog() {

        val listDialog = AlertDialog.Builder(this)
        listDialog.setTitle("请选择包装单位")
        listDialog.setItems(items) { _, which ->
            bzdwStr = which
            tvBzdw.text = items[which]
        }
        listDialog.show()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> createOrder()
            R.id.tvFwhy -> startActivityForResult<PremiumActivity>(
                200,
                "fk" to fkStr,
                "hdfs" to hdfsStr,
                "tzfh" to tzfh
            )
            R.id.tvCc -> startActivityForResult<VehicleActivity>(201, "a" to true)
            R.id.tvBzdw -> showBzdwListDialog()
            R.id.ivFhd -> startActivityForResult<ShipmentsActivity>(195)
            R.id.ivMdd -> startActivityForResult<DestinationActivity>(199)
            R.id.ivShr -> startActivityForResult<ConsigneeActivity>(198)
            R.id.ivGys -> startActivityForResult<SupplierActivity>(197, "a" to true)
            R.id.ivLxr -> startActivityForResult<ConsignerActivity>(196)
            R.id.btnPrintBq -> //sendLabel()
                if (connectionStatus)
                    printBq()
                else
                    startActivityForResult<ConfigPrintActivity>(120)
            R.id.btnPrintXp -> {
                if (!connectionStatus) {
                    startActivityForResult<ConfigPrintActivity>(120)
                    return
                }
                printXp()
            }
            R.id.btnDelete -> delete()
        }
    }

    private fun delete() {
        val map = hashMapOf<String, Any>(
            "clientCategory" to 4,
            "clientVersion" to "1.0",
            "id" to loginModel?.id!!,
            "isDel" to 1,
            "mobile" to loginModel?.mobile!!,
            "orderid" to waybillModel?.id!!,
            "sessionId" to sessionid!!
        )
        HttpNetUtils.getInstance().getManager()?.wleditOrDel(map)?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<String>>(this) {
                override fun onSuccess(data: BaseModel<String>?) {
                    toast(data?.msg!!)
                    setResult(99)
                    finish()
                }
            })
    }


    private fun createOrder() {
        val fhdStr = etFhd.text.toString()
        val kddhStr = etKddh.text.toString()
        val mddStr = etMdd.text.toString()
        val shrStr = etShr.text.toString()
        val hwmcStr = etHwmc.text.toString()
        val hwslStr = etHwsl.text.toString()
        val zlStr = etZl.text.toString()
        val tjStr = etTj.text.toString()
        val jbyfStr = etJbyf.text.toString()
        val psfStr = etPsh.text.toString()
        val bfStr = etBf.text.toString()
        val zzfStr = etZzh.text.toString()
        val dskStr = etDsk.text.toString()
        val cbStr = etCb.text.toString()
        val gysStr = etGys.text.toString()
        val fhrStr = etFhr.text.toString()
        val bzStr = etBz.text.toString()

        if (TextUtils.isEmpty(jbyfStr)) {
            toast("基本运费不能为空")
            return
        }

        val order = JSONObject(
            hashMapOf(
                "agentmoney" to dskStr,
                "copycount" to hdfsStr, //回单份数
                "shipfee" to 0, //运费 （总计
                "serviceName" to gysStr,//供应商
                "dispatchfee" to psfStr, //派送费
                "receivername" to shrStr,//收货人
                "productweight" to zlStr,//重量
                "receivepoint" to mddStr, //目的地
                "shipfeesendpay" to zzfStr,//中转费
                "costFee" to cbStr, //成本
                "senderphone" to fhrModel?.mobile,//发货人电话
                "shipfeepaytype" to shipfeepaytype, //支付方式
                "sendername" to fhrStr,//发货人
                "receiveraddress" to shrModel?.addr, //收货人的地址
                "waitnotify" to tzfh,//等通知放货，0否，1是
                "productno" to kddhStr,//快递单号
                "baseshipfee" to jbyfStr, //基本运费
                "insurancefee" to bfStr, //保费
                "recno" to bzdwStr, //包装单位
                "productcount" to hwslStr,//货物数量
                "recway" to recway,//收货方式，0自提，1派送
                "productsize" to tjStr,//体积
                "receiverphone" to shrModel?.mobile,//收货人电话
                "productdescript" to hwmcStr, //货物名称
                "returnmoney" to fkStr,//返款
                "carname" to ccStr!!,//车次
                "comment" to bzStr, //备注
                "shipfeestate" to pay,
                "senderaddress" to fhdStr,//发货人地址
                "shipFeeState" to waybillModel?.shipfeestate, //0未付，1付清
                "id" to waybillModel?.id!!
            )
        ).toString()
        val data =
            "clientCategory=4&clientVersion=1.0&id=${loginModel?.id}&isadd=1&mobile=${loginModel?.mobile}&sessionId=$sessionid&order=$order"
        val requestBody = RequestBody.create(
            MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), data
        )
        HttpNetUtils.getInstance().getManager()?.wladd(
            requestBody
        )?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<WaybillModel>>(this) {
                override fun onSuccess(data: BaseModel<WaybillModel>?) {
                    toast(data?.msg!!)
//                    if ("success" == data.code)
//                        finish()
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 120) checkGPprinter()
        if (null == data) return
        when (requestCode) {
            200 -> {
                fkStr = data.getStringExtra("fk")
                hdfsStr = data.getStringExtra("hdfs")
                tzfh = data.getIntExtra("tzfh", 0)
            }
            199 -> {
                val item = data.getSerializableExtra("data") as DestinationModel
                etMdd.setText(item.receivepoint)
            }
            198 -> {
                shrModel = data.getSerializableExtra("data") as ConsigneeModel
                etShr.setText(shrModel?.name)
            }
            197 -> {
                val itemStr = data.getStringExtra("data")
                etGys.setText(itemStr)
            }
            196 -> {
                fhrModel = data.getSerializableExtra("data") as ConsigneeModel
                etFhr.setText(fhrModel?.name)
            }
            195 -> {
//                val destinationModel = data.getSerializableExtra("data") as DestinationModel
                etFhd.setText(data.getStringExtra("data"))
            }

            201 -> {
                ccStr = data.getStringExtra("data")
                tvCc.text = ccStr
            }
            120 -> checkGPprinter()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (conn != null) {
            unbindService(conn)
        }
        unregisterReceiver(mBroadcastReceiver)
    }
}
