package com.example.theychat.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

import com.example.theychat.R;

public class InputDialog {
    private Dialog dialog;
    private View view;
    private String tagId; // 当前标识
    private int seqId; // 当前序号
    private String title;
    private InputCallbacks callbacks; // 回调监听器

    public InputDialog(Context context, String idt, int seq, String title, InputCallbacks callbacks) {
        tagId = idt;
        seqId = seq;
        this.title = title;
        this.callbacks = callbacks;

        view = LayoutInflater.from(context).inflate(R.layout.dialog_input, null);

        // 创建一个指定风格的对话框对象
        dialog = new Dialog(context, R.style.CustomDialog);
        TextView tv_title = view.findViewById(R.id.tv_title);
        EditText et_input = view.findViewById(R.id.et_input);
        tv_title.setText(this.title);

        view.findViewById(R.id.tv_cancel).setOnClickListener(view1 -> dismiss());
        view.findViewById(R.id.tv_confirm).setOnClickListener(view1 -> {
            dismiss();
            this.callbacks.onInput(tagId, et_input.getText().toString(), seqId);
        });
    }

    // 显示对话框
    public void show() {
        dialog.getWindow().setContentView(view);
        dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    // 关闭对话框
    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    // 判断对话框是否显示
    public boolean isShowing() {
        if (dialog != null) {
            return dialog.isShowing();
        } else {
            return false;
        }
    }

    public interface InputCallbacks {
        void onInput(String idt, String content, int seq);
    }

}
