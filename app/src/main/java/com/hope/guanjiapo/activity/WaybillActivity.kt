package com.hope.guanjiapo.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.RemoteException
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import android.view.View
import com.gprinter.command.EscCommand
import com.gprinter.command.GpCom
import com.gprinter.command.GpUtils
import com.gprinter.command.LabelCommand
import com.gprinter.service.GpPrintService
import com.hope.guanjiapo.R
import com.hope.guanjiapo.adapter.WaybillAdapter
import com.hope.guanjiapo.base.BaseActivity
import com.hope.guanjiapo.base.BaseModel
import com.hope.guanjiapo.iter.OnItemEventListener
import com.hope.guanjiapo.model.WaybillModel
import com.hope.guanjiapo.net.HttpNetUtils
import com.hope.guanjiapo.net.NetworkScheduler
import com.hope.guanjiapo.net.ProgressSubscriber
import com.hope.guanjiapo.service.PrinterServiceConnection
import com.hope.guanjiapo.utils.ApiUtils.connectionStatus
import com.hope.guanjiapo.utils.ApiUtils.line
import com.hope.guanjiapo.utils.ApiUtils.loginModel
import com.hope.guanjiapo.utils.ApiUtils.sessionid
import com.hope.guanjiapo.utils.PreferencesUtils
import com.hope.guanjiapo.utils.TimeUtil
import com.hope.guanjiapo.utils.Utils
import com.hope.guanjiapo.utils.Utils.logs
import com.hope.guanjiapo.utils.Utils.toChinese
import com.hope.guanjiapo.view.RecycleViewDivider
import kotlinx.android.synthetic.main.activity_waybill.*
import kotlinx.android.synthetic.main.view_title.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast


class WaybillActivity : BaseActivity(), OnItemEventListener, View.OnClickListener {

    override fun onItemEvent(pos: Int) {
        startActivity<PrintOrderInfoActivity>("data" to allItem[pos])
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBackup -> finish()
            R.id.tvTitleRight -> startActivityForResult<SearchActivity>(119)
            R.id.btnPrintBq -> //sendLabel()
//                printBq(0)
                if (connectionStatus)
                    printBq(0)
                else
                    startActivityForResult<ConfigPrintActivity>(120)
            R.id.btnPrintXp -> {
                if (!connectionStatus) {
                    startActivityForResult<ConfigPrintActivity>(120)
                    return
                }
                val temp = getChooice()
                if (temp.isNullOrEmpty()) {
                    toast("请选择您要打印的数据")
                    return
                }
                printXp(getChooice()[0])
            }
            R.id.btnAllPrint -> {
                val choice = getChooice()
                if (choice.isEmpty() || choice.size == 0) {
                    toast("请选择你需要汇总打印的数据")
                    return
                }
                val tempArr = arrayListOf<WaybillModel>()
                choice.forEach {
                    tempArr.add(allItem[it])
                }
                startActivity<CollectActivity>("data" to tempArr)
            }
        }
    }

    /**
     * 获取已选中的项
     * @return ArrayList<Int>
     */
    private fun getChooice(): ArrayList<Int> {
        val arrayInt: ArrayList<Int> = arrayListOf()
        for (entry in adapter.itemsStatus.entries) {
            if (entry.value)
                arrayInt.add(entry.key)
        }
        return arrayInt
    }

    private val mPrinterIndex = 0
    private var mTotalCopies = 0
    private val MAIN_QUERY_PRINTER_STATUS = 0xfe
    private val REQUEST_PRINT_LABEL = 0xfd
    private val REQUEST_PRINT_RECEIPT = 0xfc
    private var conn: PrinterServiceConnection? = null

    private val paytype: Array<String> by lazy { resources.getStringArray(R.array.paytype) }
    private val recwaytype: Array<String> by lazy { resources.getStringArray(R.array.recwaytype) }

    private fun connection() {
        logs("tag", "connection")
        conn = PrinterServiceConnection()
        bindService(Intent(this, GpPrintService::class.java), conn, Context.BIND_AUTO_CREATE) // bindService
    }

    private val allItem: ArrayList<WaybillModel> by lazy { ArrayList<WaybillModel>() }
    private val adapter by lazy { WaybillAdapter<WaybillModel>(this) }
    override fun getLayoutView(): Int {
        return R.layout.activity_waybill
    }

    override fun initData() {
        tvTitle.text = "运单记录"
        tvTitleRight.setText(R.string.more)
        tvTitleRight.visibility = View.VISIBLE
        ivBackup.setOnClickListener(this)
        tvTitleRight.setOnClickListener(this)

        btnPrintBq.setOnClickListener(this)
        btnPrintXp.setOnClickListener(this)
        btnAllPrint.setOnClickListener(this)

        initDevice()

        rcvData.layoutManager = LinearLayoutManager(this)
        rcvData.adapter = adapter
        rcvData.addItemDecoration(RecycleViewDivider(this, LinearLayoutManager.VERTICAL))
        adapter.itemListener = this
        checkGPprinter()
    }

    override fun onResume() {
        super.onResume()
        getOrderList()
    }

    private fun getOrderList() {
        HttpNetUtils.getInstance().getManager()?.wlget(
            hashMapOf(
                "bossId" to loginModel?.bossId!!,
                "index" to 0,
                "pagesize" to 200,
                "id" to loginModel?.id!!,
                "clientCategory" to 4,
                "clientVersion" to 1.0,
                "mobile" to loginModel?.mobile!!,
                "sessionId" to sessionid!!
            )
        )
            ?.compose(NetworkScheduler.compose())
            ?.subscribe(object : ProgressSubscriber<BaseModel<List<WaybillModel>>>(this) {
                override fun onSuccess(data: BaseModel<List<WaybillModel>>?) {
                    if (data?.data == null) {
                        toast(data?.msg!!)
                        return
                    }
                    allItem.clear()
                    allItem.addAll(data.data!!)
                    adapter.setDataEntityList(allItem)
                }
            })
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

    private fun checkGPprinter() {
        try {
            conn?.mGpService?.queryPrinterStatus(0, 500, MAIN_QUERY_PRINTER_STATUS)
        } catch (e1: RemoteException) {
            e1.printStackTrace()
        }
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
    private fun printBq(pos: Int) {
        val type = conn?.mGpService?.getPrinterCommandType(mPrinterIndex)
        logs("tag", "type = $type")
        if (type == GpCom.ESC_COMMAND) {
            toast("请切换到标签模式下打印")
            return
        }
        val xstart = 2
        val ystart = 140
        val item = allItem[pos]
        val tsc = LabelCommand()
        tsc.addSize(75, 100) // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0) // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.BACKWARD, LabelCommand.MIRROR.NORMAL)// 设置打印方向
        tsc.addReference(0, 10)// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON) // 撕纸模式开启
        tsc.addCls()// 清除打印缓冲区
        // 绘制简体中文
        tsc.addText(
            xstart,
            ystart,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "${item.senderaddress}=>${item.receivepoint}   单号:${item.id} 业务:${item.operatorMobile}"
        )

        tsc.addBar(xstart, 175, 520, 2)
        tsc.addText(
            xstart,
            190,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "收货方:${item.receivername}           电话:${item.receiverphone}"
        )

        tsc.addText(
            xstart,
            230,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "发货方:${item.receivername}           电话:${item.senderphone}"
        )

        tsc.addText(
            xstart,
            270,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "名称:${item.productdescript}(${item.productcount})"
        )
        tsc.addText(
            xstart,
            310,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "代收款:${item.agentmoney}  中转:${item.agentmoney}"
        )
        tsc.addText(
            xstart,
            350,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "运费:${item.baseshipfee}  ${paytype[item.shipfeepaytype]}"
        )

        tsc.addText(
            xstart,
            390,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "备注:${item.comment}"
        )

        tsc.addText(
            xstart,
            455,
            LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE,
            LabelCommand.ROTATION.ROTATION_0,
            LabelCommand.FONTMUL.MUL_1,
            LabelCommand.FONTMUL.MUL_1,
            "开单时间:${TimeUtil.getDayByType(item.createDate, TimeUtil.DATE_YMD_HMS)} 扫描查询物流"
        )
//        寄件联要添加线
//        tsc.addBar(xstart, 350, 520, 2)
        // 绘制图片
//        val b = BitmapFactory.decodeResource(resources, R.drawable.gprinter)
//        tsc.addBitmap(20, 50, LabelCommand.BITMAP_MODE.OVERWRITE, b.width, b)

        tsc.addQRCode(350, 280, LabelCommand.EEC.LEVEL_L, 8, LabelCommand.ROTATION.ROTATION_0, " www.smarnet.cc")
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
        logs("tag", "str =$str")
        val rel: Int
        try {
            rel = conn?.mGpService?.sendLabelCommand(mPrinterIndex, str)!!
            Utils.logs("tag", "错误 =  $rel")
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
     *          现付 自提
     *   单号：     日期：
     *   广州 =》 江苏
     *   ----------------
     *   收货人：   电话：
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
     *   谢谢惠顾
     *    **********
     *    **********
     *    **********
     *    **********
     *    **********
     *   扫码查询运单
     */
    private fun printXp(pos: Int) {
        val waybillModel: WaybillModel = allItem[pos]
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
            EscCommand.ENABLE.ON,
            EscCommand.ENABLE.OFF
        )// 设置为倍高倍宽
        esc.addText("${paytype[waybillModel.shipfeepaytype]} ${recwaytype[waybillModel.recway]}\n") // 打印文字
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
            "单号:${waybillModel.id}  日期:${TimeUtil.getDayByType(
                waybillModel.updateDate,
                TimeUtil.DATE_YMD_HMS
            )}\n"
        ) // 打印文字

        esc.addText("${waybillModel.senderaddress} => ${waybillModel.receivepoint}\n") // 打印文字
        esc.addText(line)
        /* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText("收货人:${waybillModel.receivername}  电话:${waybillModel.receiverphone}\n")
        esc.addText(line)
        esc.addText("发货人:${waybillModel.sendername}\n")
        esc.addText(line)
        esc.addText("品名:${waybillModel.productdescript} 件数:${waybillModel.productcount}  包装:\n")
        esc.addText(line)
        esc.addText("付款方式:${paytype[waybillModel.shipfeepaytype]} 提货方式:${recwaytype[waybillModel.recway]}  回单:${waybillModel.copycount}\n")
        esc.addText(line)
        esc.addText("运费合计:${waybillModel.shipfee} ${toChinese(waybillModel.shipfee)}元整\n")
        esc.addText("代收款:${waybillModel.agentmoney} \n")
        esc.addText(line)
        /*
		 * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		 */
        val footer = PreferencesUtils.getString(this, "footer")
        esc.addText("$footer\n") // 打印文字
        esc.addSelectErrorCorrectionLevelForQRCode(0x31.toByte()) // 设置纠错等级
        esc.addSelectSizeOfModuleForQRCode(12.toByte())// 设置qrcode模块大小
        esc.addStoreQRCodeData("www.smarnet.cc")// 设置qrcode内容
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            119 -> {
                if (null == data) return
                val map = data.getSerializableExtra("data") as HashMap<String, Any>
                val dataMap = hashMapOf<String,Any>()
                map.entries.forEach {
                    if(it.value !="")
                        dataMap.put(it.key,it.value)
                }
                dataMap["onlyDriver"] = 0
                dataMap["clientCategory"] = 4
                dataMap["clientVersion"] = 1.0
                dataMap["mobile"] = loginModel?.mobile!!
                dataMap["sessionId"] = sessionid!!
                dataMap["id"] = loginModel?.id!!
                HttpNetUtils.getInstance().getManager()?.wlsearch(
                    dataMap
                )?.compose(NetworkScheduler.compose())
                    ?.subscribe(object : ProgressSubscriber<BaseModel<List<WaybillModel>>>(this) {
                        override fun onSuccess(data: BaseModel<List<WaybillModel>>?) {
                            if (data?.data == null) {
                                toast(data?.msg!!)
                                return
                            }
                            allItem.clear()
                            allItem.addAll(data.data!!)
                            adapter.setDataEntityList(allItem)
                        }
                    })
            }
            120 -> {
                checkGPprinter()
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (conn != null) {
            unbindService(conn) // unBindService
        }
        unregisterReceiver(mBroadcastReceiver)
    }
}
