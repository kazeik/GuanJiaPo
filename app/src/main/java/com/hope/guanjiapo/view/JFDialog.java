package com.hope.guanjiapo.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.hope.guanjiapo.R;


/**
 * @author kazeik.chen , QQ:77132995,email:kazeik@163.com
 *         2017 03 23 基类对话框 09:25
 *         类说明:
 */
public class JFDialog extends Dialog {
    private TextView tvTitle;
    private TextView tvContent;
    private Button btCancelNo;
    private Button btCancelYes;
    private View viewLine;

    private String cancelText;
    private String sureText;
    private String titleText;
    private String contextText;
    private boolean titleBold;
    private boolean cancel;
    private boolean sigleMenu;

    private OnCancelEventListener cancelListener;
    private OnSureEventListener sureListener;

    public JFDialog(Context context) {
        super(context, R.style.custom_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu_single);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        btCancelNo = (Button) findViewById(R.id.bt_cancel_no);
        btCancelYes = (Button) findViewById(R.id.bt_cancel_yes);
        viewLine = findViewById(R.id.viLine);
    }

    public void show() {
        super.show();
        show(this);
    }

    private void show(JFDialog dialog) {
        Window win = dialog.getWindow();
        win.getDecorView().setPadding(80, 0, 80, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        win.setGravity(Gravity.CENTER);

        if (!TextUtils.isEmpty(contextText)) {
            dialog.tvContent.setText(contextText);
        }
        if (!TextUtils.isEmpty(titleText)) {
            dialog.tvTitle.setText(titleText);
        }
        if (titleBold) {
            TextPaint tp = dialog.tvTitle.getPaint();
            tp.setFakeBoldText(titleBold);
        }
        if (!cancel) {
            dialog.setCanceledOnTouchOutside(cancel);
            dialog.setCancelable(cancel);
        }
        if (sigleMenu) {
            dialog.btCancelNo.setVisibility(View.GONE);
            dialog.viewLine.setVisibility(View.GONE);
        }
        if (null != dialog.btCancelNo) {
            dialog.btCancelNo.setOnClickListener(view -> {
                dismiss();
                if(cancelListener != null){
                    cancelListener.onCancelEvent(dialog);
                }
            });
        }

        if (!TextUtils.isEmpty(dialog.sureText)) {
            dialog.btCancelYes.setText(dialog.sureText);
        }

        if (!TextUtils.isEmpty(dialog.cancelText)) {
            dialog.btCancelNo.setText(dialog.cancelText);
        }
        if (null != sureListener) {
            dialog.btCancelYes.setOnClickListener(view -> {
                dismiss();
                if(sureListener != null){
                    sureListener.onSureEvent(dialog);
                }
            });
        }
    }

    public static class Builder {
        private JFDialog dialog;

        public Builder(Context context) {
            dialog = new JFDialog(context);
        }

        public Builder setDialogCancelListener(OnCancelEventListener clickListener) {
            dialog.cancelListener = clickListener;
            return this;
        }

        public Builder setDialogSureListener(OnSureEventListener clickListener) {
            dialog.sureListener = clickListener;
            return this;
        }

        public Builder setContentText(String text) {
            dialog.contextText = text;
            return this;
        }

        public Builder setTitleText(String text) {
            dialog.titleText = text;
            return this;
        }

        public Builder setTitleStyle(boolean bold) {
            dialog.titleBold = bold;
            return this;
        }

        public Builder setSigleMenu(boolean sigleMenu) {
            dialog.sigleMenu = sigleMenu;
            return this;
        }

        public Builder setIsCancelable(boolean isCancel) {
            dialog.cancel = isCancel;
            return this;
        }

        public Builder setCancelText(String cancelText) {
            dialog.cancelText = cancelText;
            return this;
        }

        public Builder setSureText(String sureText) {
            dialog.sureText = sureText;
            return this;
        }

        public JFDialog create() {
            return dialog;
        }
    }

    public interface OnCancelEventListener{
        public void onCancelEvent(Dialog dialog);
    }

    public interface OnSureEventListener{
        public void onSureEvent(Dialog dialog);
    }
}
