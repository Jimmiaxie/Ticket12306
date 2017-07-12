package dda.com.ticket12306.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import dda.com.ticket12306.R;
import dda.com.ticket12306.entity.SearchCityInfo;
import dda.com.ticket12306.utils.commonUtils;
import dda.com.ticket12306.utils.httpUtils;
import dda.com.ticket12306.view.CustomDialog;

/**
 * Created by nuo on 2016-09-12.
 * Created by 16:13.
 * 描述:搜索结果界面
 */
public class SerachTicketActivity extends Activity {

    //Gson类
    private Gson gson;
    //recyclerView类
    private RecyclerView recyclerView;
    //当无网络时显示此界面
    private LinearLayout ll_blank;
    //查票时间从早到晚或从晚到早显示
    private LinearLayout ll_by_time;
    //查票时间从短到长或者从长到短
    private LinearLayout ll_lishi_time;
    //查票信息适配器
    private HomeAdapter mAdapter;
    //搜索的城市信息
    private SearchCityInfo searchCityInfo;
    //返回键
    private ImageView back;
    private HashMap<String, String> hashMap = new HashMap<>();
    //时间排序数组
    private ArrayList<SearchCityInfo.DataBean.QueryLeftNewDTOBean> time_arr;
    //历时排序数组
    private ArrayList<SearchCityInfo.DataBean.QueryLeftNewDTOBean> lishi_arr;
    //G车次过滤
    private ArrayList<SearchCityInfo.DataBean.QueryLeftNewDTOBean> G_arr;
    //D车次过滤
    private ArrayList<SearchCityInfo.DataBean.QueryLeftNewDTOBean> D_arr;
    //T车次过滤
    private ArrayList<SearchCityInfo.DataBean.QueryLeftNewDTOBean> T_arr;
    //其他车次过滤
    private ArrayList<SearchCityInfo.DataBean.QueryLeftNewDTOBean> Other_arr;

    private String[] code = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H"
            , "I", "J", "K", "L", "M", "O", "P", "Q", "S"};

    private String[] seat_name = {"棚车", "硬座", "软座", "硬卧", "软卧", "包厢硬卧", "高级软卧", "一等软座", "二等软座"
            , "商务座", "鸳鸯软卧", "混编硬座", "混编硬卧", "包厢软座", "特等软座", "动卧", "二人软包", "一人软包",
            "一等双软", "二等双软", "混编软座", "混编软卧", "一等座", "二等座", "特等座", "观光座", "一等包座"};

    //座位类型
    private String seat_type;
    //一个余票信息中的总的座位类型
    private ArrayList<String> total_seat_type;
    //一个余票信息中的总的剩余票数
    private ArrayList<String> total_seat_count;
    //显示当前搜索的车次
    private TextView tv_show;
    //显示当前搜索车次的数量
    private TextView tv_show_train;
    //出发地
    private String from_city;
    //目的地
    private String to_city;
    //判断是否需要从早到晚或从晚到早排列
    private int by_time = -1;
    //排序时间的描述
    private TextView tv_desc_time;
    private ImageView iv_asc;
    //判断是否需要按照时间长短排序(-1:未排 0:从短到长 1:从长到短)
    private int lishi = -1;
    private TextView tv_lishi_time;
    private LinearLayout ll_shuaixuan;
    //弹出筛选对话框
    private CustomDialog dialog;
    //判断车次不限的情况
    private boolean isType = false;
    //是否高铁动车打钩了
    private boolean isG = false;
    //是否动车组打钩了
    private boolean isD = false;
    //是否空调特快打钩了
    private boolean isT = false;
    //是否是其他车次打钩了
    private boolean isOther = false;
    private Message message;
    private OkHttpClient mHttpClient;
    //网络读取条的加载
    private ImageView iv_loading;
    //网络加载对话框的实现
    private LinearLayout ll_network;
    //网络描述或者
    private TextView tv_desc;
    //前一天按钮
    private Button btn_pre;
    //后一天按钮
    private Button btn_next;
    //当前日期
    private TextView tv_date;
    //当前日期是什么l
    private String date;
    //前一天
    private String date_pre;
    //后一天
    private String date_next;
    //出发地的城市代码
    private String from_code;
    //目的地的城市代码
    private String to_code;
    //请求网络地址
    private String url;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            RecyclerView.LayoutManager layoutManager;
            switch (msg.what) {
                //当只有高速动车被点击的时候，只留下g开头的
                //G高铁过滤
                case 0:
                    for (int i = 0; i < lishi_arr.size(); i++) {
                        boolean station_train_code = lishi_arr.get(i).station_train_code.startsWith("G");

                        if (station_train_code) {
                            G_arr.add(lishi_arr.get(i));
                        }
                    }
                    layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    break;
                //D开头过滤
                case 1:
                    for (int i = 0; i < lishi_arr.size(); i++) {
                        boolean station_train_code = lishi_arr.get(i).station_train_code.startsWith("D");
                        if (station_train_code) {
                            D_arr.add(lishi_arr.get(i));
                        }
                    }
                    layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    break;
                //T开头过滤
                case 2:
                    for (int i = 0; i < lishi_arr.size(); i++) {
                        boolean station_train_code = lishi_arr.get(i).station_train_code.startsWith("T");
                        if (station_train_code) {
                            T_arr.add(lishi_arr.get(i));
                        }
                    }
                    layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    break;
                //其他过滤
                case 3:
                    for (int i = 0; i < lishi_arr.size(); i++) {
                        if (lishi_arr.get(i).station_train_code.startsWith("T") && lishi_arr.get(i).station_train_code.startsWith("G") && !lishi_arr.get(i).station_train_code.startsWith("D")) {
                            Other_arr.add(lishi_arr.get(i));
                        }
                    }
                    layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    break;
                //不限类型的过滤
                case 4:
                    layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    break;
                //重新初始化
                case 5:
                    ll_network.setVisibility(View.GONE);
                    ll_blank.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    initData();
                    initEvent();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serach_ticket);

        initView();
        init();
        initAnim();
        initNet();
    }

    /**
     * 初始化网络
     */
    private void initNet() {
        try {
            httpUtils.setSSL(mHttpClient);
            initEnvironment();
            initNetWork(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化动画
     */
    private void initAnim() {
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setRepeatMode(Animation.RESTART);//重复
        rotateAnimation.setRepeatCount(Animation.INFINITE);//无限次
        //旋转一次不停顿一下，需要以下两行代码
        LinearInterpolator lir = new LinearInterpolator();
        rotateAnimation.setInterpolator(lir);
        iv_loading.startAnimation(rotateAnimation);
        ll_network.setVisibility(View.VISIBLE);
    }

    /**
     * 初始化
     */
    private void init() {
        lishi_arr = new ArrayList<>();
        G_arr = new ArrayList<>();
        D_arr = new ArrayList<>();
        T_arr = new ArrayList<>();
        Other_arr = new ArrayList<>();
        time_arr = new ArrayList<>();

        url = getIntent().getStringExtra("url");
        from_city = getIntent().getStringExtra("train_from");
        to_city = getIntent().getStringExtra("train_to");
        date = getIntent().getStringExtra("date");
        from_code = getIntent().getStringExtra("from_code");
        to_code = getIntent().getStringExtra("to_code");
        tv_show.setText(from_city + "-" + to_city);
        tv_date.setText(date);
    }

    /**
     * 初始化网络界面
     *
     * @param url url地址
     */
    private void initNetWork(String url) {

        final Request request = new Request.Builder()
                .url(url)
                .build();

        mHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setVisibility(View.INVISIBLE);
                        ll_network.setVisibility(View.GONE);
                        ll_blank.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {

                if (response.isSuccessful()) {
                    parseData(response.body().string());
                }
            }
        });
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
            }
        });
    }

    /**
     * 初始化点击事件
     */
    private void initEvent() {

        //时间排序
        ll_by_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (by_time == -1 || by_time == 0) {
                    by_time = 1;
                    lishi = -1;
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    tv_desc_time.setText("时间晚到早");
                    iv_asc.setImageResource(R.drawable.icon_desc);
                } else if (by_time == 1) {
                    by_time = 0;
                    lishi = -1;
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    tv_desc_time.setText("时间早到晚");
                    iv_asc.setImageResource(R.drawable.icon_asc);
                }
            }
        });

        //历时排序
        ll_lishi_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lishi == -1 || lishi == 0) {
                    lishi = 1;
                    Collections.sort(lishi_arr, new Comparator<SearchCityInfo.DataBean.QueryLeftNewDTOBean>() {
                        @Override
                        public int compare(SearchCityInfo.DataBean.QueryLeftNewDTOBean o1, SearchCityInfo.DataBean.QueryLeftNewDTOBean o2) {
                            return o1.lishi.compareTo(o2.lishi);
                        }
                    });
                    by_time = -1;
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    tv_lishi_time.setText("历时短到长");

                } else if (lishi == 1) {
                    lishi = 0;
                    Collections.sort(lishi_arr, new Comparator<SearchCityInfo.DataBean.QueryLeftNewDTOBean>() {
                        @Override
                        public int compare(SearchCityInfo.DataBean.QueryLeftNewDTOBean o1, SearchCityInfo.DataBean.QueryLeftNewDTOBean o2) {
                            return o2.lishi.compareTo(o1.lishi);
                        }
                    });
                    by_time = -1;
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SerachTicketActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    mAdapter = new HomeAdapter();
                    recyclerView.setAdapter(mAdapter);
                    tv_lishi_time.setText("历时长到短");
                }
            }
        });

        ll_shuaixuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_pre = tv_date.getText().toString();
                int result = date_pre.compareTo(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                if (result == 0 || result < -1) {
                    Toast.makeText(SerachTicketActivity.this, "前一天日期不可小于当前日期!", Toast.LENGTH_SHORT).show();
                } else {
                    String specifiedDayBefore = commonUtils.getSpecifiedDayBefore(date_pre);
                    //今天的当前日期
                    tv_date.setText(specifiedDayBefore);
                    date_pre = specifiedDayBefore;

                    String url = "https://kyfw.12306.cn/otn/leftTicket/queryT?leftTicketDTO.train_date=" + date_pre + "&leftTicketDTO.from_station=" + from_code + "&leftTicketDTO.to_station=" + to_code + "&purpose_codes=ADULT";
                    initAnim();
                    initNetWork(url);
                }
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_next = tv_date.getText().toString();
                String specifiedDayAfter = commonUtils.getSpecifiedDayAfter(date_next);
                //今天的当前日期
                tv_date.setText(specifiedDayAfter);
                date_next = specifiedDayAfter;

                RotateAnimation rotateAnimation = new RotateAnimation(0, 350, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnimation.setDuration(2000);
                rotateAnimation.setRepeatMode(-1);
                iv_loading.setAnimation(rotateAnimation);
                initAnim();
                String url = "https://kyfw.12306.cn/otn/leftTicket/queryT?leftTicketDTO.train_date=" + date_next + "&leftTicketDTO.from_station=" + from_code + "&leftTicketDTO.to_station=" + to_code + "&purpose_codes=ADULT";
                initNetWork(url);
            }
        });
    }

    /**
     * 自定义弹窗界面
     */
    private void dialog() {

        dialog = new CustomDialog(SerachTicketActivity.this);
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isG && !isD && !isT && !isOther) {
                    G_arr.clear();
                    message = new Message();
                    message.what = 0;
                    handler.sendMessage(message);
                } else if (isD && !isG && !isT && !isOther) {
                    D_arr.clear();
                    message = new Message();
                    message.what = 1;
                    handler.sendMessage(message);
                } else if (isT && !isG && !isD && !isOther) {
                    T_arr.clear();
                    message = new Message();
                    message.what = 2;
                    handler.sendMessage(message);
                } else if (isOther && !isG && !isD && !isT) {
                    Other_arr.clear();
                    message = new Message();
                    message.what = 3;
                    handler.sendMessage(message);
                } else if (isType) {
                    T_arr.clear();
                    message = new Message();
                    message.what = 4;
                    handler.sendMessage(message);
                }
                dialog.dismiss();
            }
        });

        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setTrainTypeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (buttonView.isChecked()) {
                    isType = true;
                    dialog.setT(false);
                    dialog.setD(false);
                    dialog.setOther(false);
                    dialog.setG(false);
                } else if (!dialog.getG() && !dialog.getD() && !dialog.getT() && !dialog.getOther()) {
                    isType = true;
                    buttonView.setChecked(true);
                } else {
                    isType = false;
                }
            }
        });
        dialog.setGListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked() && !dialog.getD() && !dialog.getT() && !dialog.getOther()) {
                    isG = true;
                    dialog.setTrainType(false);
                } else if (!buttonView.isChecked() && !dialog.getD() && !dialog.getT() && !dialog.getOther() && !dialog.getType()) {
                    dialog.setTrainType(true);
                } else {
                    isG = false;
                }
            }
        });

        dialog.setDListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked() && !dialog.getG() && !dialog.getT() && !dialog.getOther()) {
                    isD = true;
                    dialog.setTrainType(false);
                } else if (!buttonView.isChecked() && !dialog.getD() && !dialog.getT() && !dialog.getOther() && !dialog.getType()) {
                    dialog.setTrainType(true);
                } else {
                    isD = false;
                }
            }
        });

        dialog.setTListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked() && !dialog.getD() && !dialog.getG() && !dialog.getOther()) {
                    isT = true;
                    dialog.setTrainType(false);
                } else if (!buttonView.isChecked() && !dialog.getD() && !dialog.getT() && !dialog.getOther() && !dialog.getType()) {
                    dialog.setTrainType(true);
                } else {
                    isT = false;
                }
            }
        });

        dialog.setOtherListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked() && !dialog.getD() && !dialog.getT() && !dialog.getG()) {
                    isOther = true;
                    dialog.setTrainType(false);
                } else if (!buttonView.isChecked() && !dialog.getD() && !dialog.getT() && !dialog.getOther() && !dialog.getType()) {
                    dialog.setTrainType(true);
                } else {
                    isOther = false;
                }
            }
        });

        dialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        dialog.getWindow().setAttributes(lp);
    }

    /**
     * 初始化数据
     */
    private void initData() {

        for (int i = 0; i < code.length; i++) {
            hashMap.put(code[i], seat_name[i]);
        }
        lishi_arr.clear();
        time_arr.clear();
        if (searchCityInfo != null && searchCityInfo.data.size() != 0) {
            for (int i = 0; i < searchCityInfo.data.size(); i++) {
                lishi_arr.add(searchCityInfo.data.get(i).queryLeftNewDTO);
                time_arr.add(searchCityInfo.data.get(i).queryLeftNewDTO);
            }
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            mAdapter = new HomeAdapter();
            recyclerView.setAdapter(mAdapter);

            tv_show.setText(from_city + " - " + to_city);
            if (searchCityInfo.data.size() != 0 && searchCityInfo != null) {
                tv_show_train.setText("(共" + searchCityInfo.data.size() + "趟列车)");
            } else {
                tv_show_train.setText("(共" + 0 + "趟列车)");
            }
        } else {
            tv_show.setText(from_city + " - " + to_city);
            tv_show_train.setText("(共" + 0 + "趟列车)");
            ll_blank.setVisibility(View.VISIBLE);
            tv_desc.setText("没有找到符合您的车次!请重新选择");
        }
    }

    /**
     * 初始化视图
     */

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        tv_show = (TextView) findViewById(R.id.tv_show);
        tv_show_train = (TextView) findViewById(R.id.tv_show_train);
        ll_blank = (LinearLayout) findViewById(R.id.ll_blank);
        ll_by_time = (LinearLayout) findViewById(R.id.ll_by_time);
        ll_lishi_time = (LinearLayout) findViewById(R.id.ll_lishi_time);
        tv_lishi_time = (TextView) findViewById(R.id.tv_lishi_time);
        tv_desc_time = (TextView) findViewById(R.id.tv_desc_time);
        iv_asc = (ImageView) findViewById(R.id.iv_asc);
        ll_shuaixuan = (LinearLayout) findViewById(R.id.ll_suaixuan);
        back = (ImageView) findViewById(R.id.back);
        ll_network = (LinearLayout) findViewById(R.id.ll_network);
        iv_loading = (ImageView) findViewById(R.id.iv_loading);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        btn_pre = (Button) findViewById(R.id.btn_front);
        btn_next = (Button) findViewById(R.id.btn_behind);
        tv_date = (TextView) findViewById(R.id.tv_date);

        mHttpClient = new OkHttpClient();
        mHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        mHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
    }

    /**
     * 解析Json格式字符串
     */
    private void parseData(String json) {
        boolean isCorrect = json.contains("data");
        gson = new Gson();
        if (isCorrect) {
            searchCityInfo = gson.fromJson(json, SearchCityInfo.class);
            Message message = new Message();
            message.what = 5;
            handler.sendMessage(message);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ll_network.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    ll_blank.setVisibility(View.VISIBLE);
                    tv_desc.setText("没有找到符合您的车次!请重新选择");
                }
            });
        }
    }

    /**
     * 搜索票数的适配器
     */
    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.serach_item, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);
            return new MyViewHolder(view);

        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            SearchCityInfo.DataBean.QueryLeftNewDTOBean queryLeftNewDTOBean = null;
            queryLeftNewDTOBean = searchCityInfo.data.get(position).queryLeftNewDTO;
            if (by_time == 0 && time_arr.size() > 0) {
                queryLeftNewDTOBean = time_arr.get(position);
            } else if (by_time == 1 && time_arr.size() > 0) {
                queryLeftNewDTOBean = time_arr.get(time_arr.size() - 1 - position);
            }
            if (lishi == 0) {
                queryLeftNewDTOBean = lishi_arr.get(position);
            } else if (lishi == 1) {
                queryLeftNewDTOBean = lishi_arr.get(position);
            }
            if (isG) {
                queryLeftNewDTOBean = G_arr.get(position);
            } else if (isD) {
                queryLeftNewDTOBean = D_arr.get(position);
            } else if (isT) {
                queryLeftNewDTOBean = T_arr.get(position);
            } else if (isOther) {
                queryLeftNewDTOBean = Other_arr.get(position);
            } else if (isType) {
                queryLeftNewDTOBean = searchCityInfo.data.get(position).queryLeftNewDTO;
            }

            holder.tv_train_id.setText(queryLeftNewDTOBean.station_train_code);
            holder.tv_from_time.setText(queryLeftNewDTOBean.start_time);
            holder.tv_to_time.setText(queryLeftNewDTOBean.arrive_time);
            holder.tv_from_location.setText(queryLeftNewDTOBean.from_station_name);
            holder.tv_to_location.setText(queryLeftNewDTOBean.to_station_name);
            String hour = queryLeftNewDTOBean.lishi.substring(0, 2);
            String minutes = queryLeftNewDTOBean.lishi.substring(3, 5);

            holder.seat_time.setText(hour + "小时" + minutes + "分");
            holder.seat_money.setText("");

            if (!queryLeftNewDTOBean.start_station_name.equals(holder.tv_from_location.getText())) {
                holder.iv_guo.setImageResource(R.drawable.guo);
            } else {
                holder.iv_guo.setImageResource(R.drawable.start_station);
            }

            if (!queryLeftNewDTOBean.end_station_name.equals(holder.tv_to_location.getText())) {
                holder.iv_guo1.setImageResource(R.drawable.guo);
            } else {
                holder.iv_guo1.setImageResource(R.drawable.end_startion);
            }


            //判断余票的信息
            total_seat_type = new ArrayList<>();
            total_seat_count = new ArrayList<>();

            //-- 余票信息的解析 --//
            //余票中总共有几个座位
            int len = queryLeftNewDTOBean.yp_info.length() / 10;
            //显示座位和余票
            String s = " ";

            int start = 0;//开始的初始化位置
            int end = 1;//结束的初始位置

            int start_wu = 6;//无票的初始化位置
            int end_wu = 7;//无票的结束的初始化位置
            int piao_shu_start = 7;//票价的开始的初始化位置
            int piao_shu_end = 10;//票价的结束的初始化位置

            int piao_shu_start_three = 6;//动车开始的初始化位置

            int piao_shu_end_three = 10;//动车结束的初始初始化位置

            for (int i = 0; i < len; i++) {
                String result = queryLeftNewDTOBean.yp_info.substring(start, end);
                String count = queryLeftNewDTOBean.yp_info.substring(piao_shu_start, piao_shu_end);
                String count1 = queryLeftNewDTOBean.yp_info.substring(piao_shu_start_three, piao_shu_end_three);

                if (len == 1) {
                    seat_type = hashMap.get(result);
                    total_seat_type.add(seat_type);
                    if (Integer.valueOf(count) == 0) {
                        total_seat_count.add("无");
                    } else {
                        total_seat_count.add(Integer.valueOf(count).toString());
                    }
                } else if (len == 2) {
                    seat_type = hashMap.get(result);
                    total_seat_type.add(seat_type);

                    if (Integer.valueOf(count) == 0) {
                        total_seat_count.add("无");
                    } else {
                        total_seat_count.add(Integer.valueOf(count).toString());
                    }
                } else if (len == 3) {

                    if (result.equals("1") && queryLeftNewDTOBean.yp_info.substring(start_wu, end_wu).equals("3")
                            || result.equals("O") && queryLeftNewDTOBean.yp_info.substring(start_wu, end_wu).equals("3")) {
                        seat_type = "无座";
                        total_seat_type.add(seat_type);
                        if (Integer.valueOf(count) == 0) {
                            total_seat_count.add("无");
                        } else {
                            total_seat_count.add(Integer.valueOf(count).toString());
                        }
                    } else if (result.equals("O") && !queryLeftNewDTOBean.yp_info.substring(start_wu, end_wu).equals("3")) {
                        seat_type = hashMap.get(result);
                        total_seat_type.add(seat_type);
                        if (Integer.valueOf(count1) == 0) {
                            total_seat_count.add("无");
                        } else {
                            total_seat_count.add(Integer.valueOf(count1).toString());
                        }
                    } else {
                        seat_type = hashMap.get(result);
                        total_seat_type.add(seat_type);
                        if (Integer.valueOf(count) == 0) {
                            total_seat_count.add("无");
                        } else {
                            total_seat_count.add(Integer.valueOf(count).toString());
                        }
                    }

                } else if (len == 4) {

                    if (result.equals("1") && queryLeftNewDTOBean.yp_info.substring(start_wu, end_wu).equals("3")) {
                        seat_type = "无座";
                        total_seat_type.add(seat_type);

                    } else {
                        seat_type = hashMap.get(result);
                        total_seat_type.add(seat_type);
                    }
                    if (Integer.valueOf(count) == 0) {
                        total_seat_count.add("无");
                    } else {
                        total_seat_count.add(Integer.valueOf(count).toString());
                    }
                } else if (len == 5) {
                    if (result.equals("1") && queryLeftNewDTOBean.yp_info.substring(start_wu, end_wu).equals("3")) {
                        seat_type = "无座";
                        total_seat_type.add(seat_type);
                    } else {
                        seat_type = hashMap.get(result);
                        total_seat_type.add(seat_type);
                    }
                    if (Integer.valueOf(count) == 0) {
                        total_seat_count.add("无");
                    } else {
                        total_seat_count.add(Integer.valueOf(count).toString());
                    }
                }
                start += 10;
                end += 10;
                start_wu += 10;
                end_wu += 10;
                piao_shu_start += 10;
                piao_shu_end += 10;
                s = s + total_seat_type.get(i) + "(" + total_seat_count.get(i) + ")" + "    ";

            }
            holder.seat_info.setText(s);
        }

        @Override
        public int getItemCount() {
            if (isG) {
                return G_arr.size();
            } else if (isD) {
                return D_arr.size();
            } else if (isT) {
                return T_arr.size();
            } else if (isOther) {
                return Other_arr.size();
            } else if (isType) {
                return searchCityInfo.data.size();
            } else if (by_time == 0 || by_time == -1 || by_time == 1) {
                return searchCityInfo.data.size();
            }
            return searchCityInfo.data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            //车次
            public TextView tv_train_id;
            //经过的图片
            public ImageView iv_guo;
            //车的起始时间
            public TextView tv_from_time;
            //车的起始位置
            public TextView tv_from_location;
            //经过的图片
            public ImageView iv_guo1;
            //到达时间
            public TextView tv_to_time;
            //到达位置
            public TextView tv_to_location;
            //历时多长时间
            public TextView seat_time;
            public TextView seat_money;
            //座位的余票信息
            public TextView seat_info;


            public MyViewHolder(View view) {
                super(view);
                tv_train_id = (TextView) view.findViewById(R.id.train_id);
                iv_guo = (ImageView) view.findViewById(R.id.iv_guo);
                tv_from_time = (TextView) view.findViewById(R.id.tv_from_time);
                tv_from_location = (TextView) view.findViewById(R.id.tv_from_location);
                iv_guo1 = (ImageView) view.findViewById(R.id.iv_guo1);
                tv_to_time = (TextView) view.findViewById(R.id.tv_to_time);
                tv_to_location = (TextView) view.findViewById(R.id.tv_to_location);
                seat_time = (TextView) view.findViewById(R.id.seat_time);
                seat_money = (TextView) view.findViewById(R.id.seat_money);
                seat_info = (TextView) view.findViewById(R.id.seat_info);
            }
        }
    }
}
