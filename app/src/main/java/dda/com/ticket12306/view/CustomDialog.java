package dda.com.ticket12306.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.TextView;

import dda.com.ticket12306.R;

/**
 * 自定义对话框
 */
public class CustomDialog extends Dialog {
    //取消按钮
    private TextView cancel;
    //确定按钮
    private TextView ok;
    //总共的车次
    private TextView tv_total_train;
    //车次类型-不限
    private CheckBox chx_train_type;
    //G-高速动车
    private CheckBox chx_train_g;
    //D-动车组
    private CheckBox chx_train_d;
    //T-空调特快
    private CheckBox chx_train_t;
    //其他类型的车次
    private CheckBox chx_train_qita;

    public CustomDialog(Context context) {
        super(context, R.style.Dialog);
        setCustomDialog();
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dialog, null);

        cancel = (TextView) mView.findViewById(R.id.tv_cancel);
        ok = (TextView) mView.findViewById(R.id.tv_ok);
        tv_total_train = (TextView) mView.findViewById(R.id.tv_total_train);
        chx_train_type = (CheckBox) mView.findViewById(R.id.chx_train_type);
        chx_train_g = (CheckBox) mView.findViewById(R.id.chx_train_g);
        chx_train_d = (CheckBox) mView.findViewById(R.id.chx_train_d);
        chx_train_t = (CheckBox) mView.findViewById(R.id.chx_train_t);
        chx_train_qita = (CheckBox) mView.findViewById(R.id.chx_train_qita);

        super.setContentView(mView);
    }


    @Override
    public void setContentView(int layoutResID) {
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
    }

    @Override
    public void setContentView(View view) {
    }

    /**
     * 确定键监听器
     *
     * @param listener
     */
    public void setOnPositiveListener(View.OnClickListener listener) {
        ok.setOnClickListener(listener);
    }

    /**
     * 取消键监听器
     *
     * @param listener
     */
    public void setOnNegativeListener(View.OnClickListener listener) {
        cancel.setOnClickListener(listener);
    }

    /**
     * 总共的车次设置文字
     */
    public void setText(String text) {
        tv_total_train.setText(text);
    }

    /**
     * 设置车次不限的监听器
     *
     * @param listener
     */
    public void setTrainTypeListener(CheckBox.OnCheckedChangeListener listener) {
        chx_train_type.setChecked(true);
        chx_train_type.setOnCheckedChangeListener(listener);
    }

    /**
     * 设置车次不限的选中
     *
     * @param bol
     */
    public void setTrainType(boolean bol) {
        chx_train_type.setChecked(bol);
    }

    /**
     * 设置G车次不限的选中
     *
     * @param bol
     */
    public void setG(boolean bol) {
        chx_train_g.setChecked(bol);
    }

    /**
     * 设置T车次不限的选中
     *
     * @param bol
     */
    public void setT(boolean bol) {
        chx_train_t.setChecked(bol);
    }

    /**
     * 设置D车次不限的选中
     *
     * @param bol
     */
    public void setD(boolean bol) {
        chx_train_d.setChecked(bol);
    }

    /**
     * 设置其他车次不限的选中
     *
     * @param bol
     */
    public void setOther(boolean bol) {
        chx_train_qita.setChecked(bol);
    }

    /**
     * 设置G-高速动车的监听器
     *
     * @param listener
     */
    public void setGListener(CheckBox.OnCheckedChangeListener listener) {
        chx_train_g.setChecked(false);
        chx_train_g.setOnCheckedChangeListener(listener);
    }

    /**
     * 设置D-动车组的监听器
     *
     * @param listener
     */
    public void setDListener(CheckBox.OnCheckedChangeListener listener) {
        chx_train_d.setChecked(false);
        chx_train_d.setOnCheckedChangeListener(listener);
    }

    /**
     * 设置T-空调特快的监听器
     *
     * @param listener
     */
    public void setTListener(CheckBox.OnCheckedChangeListener listener) {
        chx_train_t.setChecked(false);
        chx_train_t.setOnCheckedChangeListener(listener);
    }

    /**
     * 设置其他车次的监听器
     *
     * @param listener
     */
    public void setOtherListener(CheckBox.OnCheckedChangeListener listener) {
        chx_train_qita.setChecked(false);
        chx_train_qita.setOnCheckedChangeListener(listener);
    }

    public boolean getG() {
        return chx_train_g.isChecked();
    }

    public boolean getT() {
        return chx_train_t.isChecked();
    }

    public boolean getD() {
        return chx_train_d.isChecked();
    }

    public boolean getOther() {
        return chx_train_qita.isChecked();
    }

    public boolean getType() {
        return chx_train_type.isChecked();
    }
}