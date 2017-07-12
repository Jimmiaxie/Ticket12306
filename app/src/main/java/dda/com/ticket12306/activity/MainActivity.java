package dda.com.ticket12306.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dda.com.ticket12306.R;
import dda.com.ticket12306.utils.commonUtils;
import dda.com.ticket12306.utils.httpUtils;

/**
 * 主界面展示界面
 */
public class MainActivity extends Activity implements View.OnClickListener {

    //OKhttpClient
    private OkHttpClient mHttpClient;
    //出发地界面
    private LinearLayout ll_1;
    //目的地界面
    private LinearLayout ll_2;
    //选择日历界面
    private LinearLayout ll_3;
    //查询
    private LinearLayout ll_4;
    //出发地
    private TextView tv_go_city;
    //意图
    private Intent intent = null;
    //目的地
    private TextView tv_to_city;
    //选择日期
    private TextView tv_date;
    //url地址
    private String url;
    //出发地的出发代码
    private String from_code = "CSQ";
    //目的地的出发代码
    private String to_code = "FZS";
    //当前日期
    private String sf;
    //是否能够跳转界面
    private boolean isCanRun = false;
    //日历所选择的日期
    private String sel_date;
    //箭头的选择
    private ImageView jiantou;
    //出发地
    private String from_loc = "长沙";
    //目的地
    private String to_loc = "福州";
    //最近查询的地方
    private TextView tv_last;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        copyDB();
        initNetwork();
        initView();
        initListener();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        from_loc = (String) commonUtils.getParam(MainActivity.this, "city", "", "city");
        to_loc = (String) commonUtils.getParam(MainActivity.this, "city", "", "to_city");
        tv_last.setText(commonUtils.getParam(MainActivity.this, "last", "", "last").toString());
        from_code = (String) commonUtils.getParam(MainActivity.this, "city_code", "CSQ", "city_code");
        to_code = (String) commonUtils.getParam(MainActivity.this, "city_code", "FZS", "to_city_code");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        if (TextUtils.isEmpty(from_loc) && TextUtils.isEmpty(to_loc)) {
            tv_go_city.setText("长沙");
            tv_to_city.setText("福州");
        } else {
            tv_go_city.setText(from_loc);
            tv_to_city.setText(to_loc);
        }

        //今天的当前日期
        sf = df.format(new Date());
        sel_date = sf;
        if (sel_date.equals(sf)) {
            isCanRun = true;
        }

        tv_date.setText(sf);
    }

    /**
     * 初始化网络环境
     */
    private void initNetwork() {
        mHttpClient = new OkHttpClient();
        try {
            httpUtils.setSSL(mHttpClient);
            initEnvironment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 最近查询功能
     */
    private void lastQuery() {
        tv_last.setText(tv_go_city.getText() + " - " + tv_to_city.getText());
        commonUtils.setParam(MainActivity.this, "last", tv_last.getText(), "last");
    }

    /**
     * 拷贝数据库到当前的/data/data/当前包名/databases的目录下
     */
    private void copyDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                commonUtils.copyFileFromAssets(MainActivity.this, "station_16.sqlite");
            }
        }).start();
    }


    /**
     * 初始化监听器
     */
    private void initListener() {
        ll_1.setOnClickListener(this);
        ll_2.setOnClickListener(this);
        ll_3.setOnClickListener(this);
        ll_4.setOnClickListener(this);
        jiantou.setOnClickListener(this);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        ll_1 = (LinearLayout) findViewById(R.id.ll_1);
        ll_2 = (LinearLayout) findViewById(R.id.ll_2);
        ll_3 = (LinearLayout) findViewById(R.id.ll_3);
        ll_4 = (LinearLayout) findViewById(R.id.ll_4);
        tv_go_city = (TextView) findViewById(R.id.tv_go_city);
        tv_to_city = (TextView) findViewById(R.id.tv_to_city);
        tv_date = (TextView) findViewById(R.id.tv_date);
        jiantou = (ImageView) findViewById(R.id.jiantou);
        tv_last = (TextView) findViewById(R.id.last);
    }

    /**
     * 点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //出发城市选择
            case R.id.ll_1:
                intent = new Intent(MainActivity.this, ChoicePlaceActivity.class);
                startActivityForResult(intent, 0);
                break;
            //目的城市选择
            case R.id.ll_2:
                intent = new Intent(MainActivity.this, ChoicePlaceActivity1.class);
                startActivityForResult(intent, 1);
                break;
            //日历选择
            case R.id.ll_3:
                intent = new Intent(MainActivity.this, GoPlaceCalendarActivity.class);
                startActivityForResult(intent, 2);
                break;
            //查询功能
            case R.id.ll_4:
                if (isCanRun == true) {
                    if (tv_go_city.getText().toString().equals(tv_to_city.getText())) {
                        Toast.makeText(MainActivity.this, "出发地和目的地不可相同，请重新选择!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    String date = tv_date.getText().toString();
                    url = "https://kyfw.12306.cn/otn/leftTicket/queryT?leftTicketDTO.train_date=" + date + "&leftTicketDTO.from_station=" + from_code + "&leftTicketDTO.to_station=" + to_code + "&purpose_codes=ADULT";
                    lastQuery();
                    Intent intent = new Intent(MainActivity.this, SerachTicketActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("date", date);
                    intent.putExtra("train_from", tv_go_city.getText());
                    intent.putExtra("train_to", tv_to_city.getText());
                    intent.putExtra("from_code", from_code);
                    intent.putExtra("to_code", to_code);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "请重新选择日期,日期不能选择当天以前的!", Toast.LENGTH_LONG).show();
                }
                break;
            //箭头交换(功能尚未实现)
            case R.id.jiantou:
                break;
        }
    }

    /**
     * 初始化订票环境
     */
    private void initEnvironment() {

        Request request = new Request.Builder()
                .url("https://kyfw.12306.cn/otn/leftTicket/log? leftTicketDTO.train_date=t&leftTicketDTO.from_station=S&leftTicketDTO.to_station=T&purpose_codes=ADULT")
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful())
                    throw new IOException("Unexpected code " + response);
                if (response.isSuccessful()) {

                }
            }
        });
    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        from_loc = data.getExtras().getString("from_city");//得到新Activity 关闭后返回的数据
        to_loc = data.getExtras().getString("to_city");//得到新Activity 关闭后返回的数据
        sel_date = data.getExtras().getString("sel_date");//得到新Activity 关闭后返回的数据
        from_code = data.getExtras().getString("from_code");//得到新Activity 关闭后返回的数据
        to_code = data.getExtras().getString("to_code");//得到新Activity 关闭后返回的数据

        //根据上面发送过去的请求吗来区别
        switch (requestCode) {
            //出发地
            case 0:
                to_code = (String) commonUtils.getParam(MainActivity.this, "city_code", "FZS", "to_city_code");
                tv_go_city.setText(from_loc);
                break;
            //目的地
            case 1:
                from_code = (String) commonUtils.getParam(MainActivity.this, "city_code", "CSQ", "city_code");
                tv_to_city.setText(to_loc);
                break;
            //日历选择
            case 2:
                //判断选择的日期是否是当天日期之前，是则跳转，否则弹出消息(不能跳转)
                int result = sel_date.compareTo(sf);
                if (result > 0) {
                    tv_date.setText(sel_date);
                    isCanRun = true;
                } else if (result < 0) {
                    tv_date.setText(sel_date);
                    isCanRun = false;
                } else if (result == 0) {
                    tv_date.setText(sel_date);
                    isCanRun = true;
                }
                from_code = (String) commonUtils.getParam(MainActivity.this, "city_code", "CSQ", "city_code");
                to_code = (String) commonUtils.getParam(MainActivity.this, "city_code", "FZS", "to_city_code");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
