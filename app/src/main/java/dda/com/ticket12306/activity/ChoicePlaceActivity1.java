package dda.com.ticket12306.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dda.com.ticket12306.R;
import dda.com.ticket12306.adapter.ChoiceCityAdapter;
import dda.com.ticket12306.entity.City12306;
import dda.com.ticket12306.entity.HistoryCity;
import dda.com.ticket12306.utils.commonUtils;
import dda.com.ticket12306.view.QuickIndexBar;

/**
 * Created by nuo on 2016-09-12.
 * Created by 15:48.
 * 描述:目的地选择城市界面,包含历史城市，当前城市，热门城市等功能。
 */
public class ChoicePlaceActivity1 extends Activity implements QuickIndexBar.OnTouchLetterListener {

    private Context mContext;
    //布局加载器
    private LayoutInflater mInflater;
    //数据库中总的数据
    private ArrayList<City12306> mDataList;
    //过滤后的item的数据
    private ArrayList<City12306> fiterList;
    //设置一个锁的对象
    private final Object mLock = new Object();
    //侧边快速导航栏
    private QuickIndexBar quickIndexBar;
    //搜索输入框
    private AutoCompleteTextView autoCompleteTextView;
    //选择城市适配器
    private ChoiceCityAdapter mAdapter;
    //数据库引用对象
    private DataBase dataBase;
    //数据库中有关12306的信息
    private ArrayList<City12306> datas;
    private BaseAdapter adapter;
    //城市的ListView
    private ListView personList;
    // 所有城市列表
    private ArrayList<City12306> allCity_lists;
    // 城市列表
    private ArrayList<City12306> city_lists;
    //热门城市
    private ArrayList<City12306> city_hot;
    //历史城市
    private ArrayList<HistoryCity> city_history;
    //判断是否在滑动
    private boolean isScroll = false;
    //位置科幻的
    public LocationClient mLocationClient = null;
    //百度位置监听器
    public BDLocationListener myListener = new MyLocationListener();
    // 用于保存定位到的城市
    private String currentCity;
    // 记录当前定位的状态 正在定位-定位成功-定位失败
    private int locateProcess = 1;
    //是否需要刷新数据
    private boolean isNeedFresh;
    //意图
    private Intent intent;
    //返回键
    private ImageView imageView;
    //热门城市名
    private ArrayList<String> iconName;
    private String[] cityName = {"北京", "上海", "广州", "天津", "重庆", "杭州", "成都", "沈阳", "南京", "哈尔滨"
            , "武汉", "长沙", "郑州", "福州", "贵阳", "长春", "合肥", "呼和浩特", "海口", "济南"};
    //自动补全监听器
    private AutoCompleteAdapter autoCompleteAdapter;
    //显示最上方返回键旁边的文本
    private TextView tv_show;

    /**
     * handler负责对城市等信息进行初始化
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            cityInit();
            hotCityInit();
            hisCityInit();
            initDatas();
        }
    };

    /**
     * 自动补全控件的ViewHolder
     */
    static class ViewHolder {
        TextView autoItem;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_place);

        initDB();
        initView();
        initEvent();
        initData();
        initLocation();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        isNeedFresh = true;
        locateProcess = 1;
        iconName = new ArrayList<>();

        for (int i = 0; i < cityName.length; i++) {
            iconName.add(cityName[i]);
        }

        personList.setAdapter(adapter);
        setAdapter(allCity_lists, city_hot, city_history);
        tv_show.setText("选择目的城市");
    }

    /**
     * 初始化位置
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 10000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.disableCache(true);//禁用启用缓存定位数据
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    /**
     * 设置Adapter对象
     *
     * @param allCity_lists 所有城市
     * @param city_hot      热门城市
     * @param city_history  历史城市
     */
    private void setAdapter(ArrayList<City12306> allCity_lists, ArrayList<City12306> city_hot, ArrayList<HistoryCity> city_history) {
        adapter = new ListAdapter(this, allCity_lists, city_hot, city_history);
        personList.setAdapter(adapter);
    }

    /**
     * 历史城市初始化
     */
    private void hisCityInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                city_history = dataBase.query(HistoryCity.class);
            }
        }).start();

    }

    /**
     * 热门城市初始化
     */
    private void hotCityInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int j = 0; j < datas.size(); j++) {
                    for (int i = 0; i < iconName.size(); i++) {
                        if (datas.get(j).getSt_name().equals(iconName.get(i))) {
                            City12306 city12306 = datas.get(j);
                            city_hot.add(city12306);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 城市初始化
     */
    private void cityInit() {
        City12306 city = new City12306("定位", "0"); // 当前定位城市
        allCity_lists.add(city);
        city = new City12306("最近", "1"); // 最近访问的城市
        allCity_lists.add(city);
        city = new City12306("热门", "2"); // 热门城市
        allCity_lists.add(city);
        city_lists = datas;
        allCity_lists.addAll(city_lists);
    }

    /**
     * 整个列表的适配器
     */
    public class ListAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<City12306> list;
        private List<City12306> hotList;
        private List<HistoryCity> hisCity;
        final int VIEW_TYPE = 4;

        public ListAdapter(Context context, List<City12306> list,
                           List<City12306> hotList, List<HistoryCity> hisCity) {
            this.inflater = LayoutInflater.from(context);
            this.list = list;
            this.context = context;
            this.hotList = hotList;
            this.hisCity = hisCity;
        }

        @Override
        public int getViewTypeCount() {
            return VIEW_TYPE;
        }

        @Override
        public int getItemViewType(int position) {
            return position < 3 ? position : 3;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewHolder holder;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextView city;
            int viewType = getItemViewType(position);
            // 定位
            if (viewType == 0) {
                convertView = inflater.inflate(R.layout.frist_list_item, null);
                city = (TextView) convertView.findViewById(R.id.lng_city);
                city.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                City12306 city12306 = new City12306();
                                for (int i = 0; i < datas.size(); i++) {
                                    if (datas.get(i).getSt_name().equals(currentCity)) {
                                        city12306.setSt_name(datas.get(i).getSt_name());
                                        city12306.setSt_code(datas.get(i).getSt_code());
                                    }
                                }

                                backMain(city12306.getSt_name(), city12306.getSt_code(), intent);
                            }
                        }).start();
                    }
                });
                // 正在定位
                if (locateProcess == 1) {
                    city.setVisibility(View.GONE);
                    city.setText("正在获取当前城市");
                } else if (locateProcess == 2) { // 定位成功
                    city.setVisibility(View.VISIBLE);
                    city.setText(currentCity);
                    mLocationClient.stop();
                } else if (locateProcess == 3) {
                    city.setVisibility(View.VISIBLE);
                    city.setText("定位失败,请检查设置后点击重试");
                }
            }// 最近访问城市
            else if (viewType == 1) {
                convertView = inflater.inflate(R.layout.recent_city1, null);
                TextView recentHint = (TextView) convertView
                        .findViewById(R.id.recentHint);
                recentHint.setText("历史访问");
                TextView tv_his_1 = (TextView) convertView.findViewById(R.id.tv_his_1);
                TextView tv_his_2 = (TextView) convertView.findViewById(R.id.tv_his_2);
                TextView tv_his_3 = (TextView) convertView.findViewById(R.id.tv_his_3);
                TextView tv_his_4 = (TextView) convertView.findViewById(R.id.tv_his_4);
                TextView tv_his_5 = (TextView) convertView.findViewById(R.id.tv_his_5);
                TextView tv_his_6 = (TextView) convertView.findViewById(R.id.tv_his_6);

                tv_his_1.setText(city_history.get(0).st_name);
                tv_his_2.setText(city_history.get(1).st_name);
                tv_his_3.setText(city_history.get(2).st_name);
                tv_his_4.setText(city_history.get(3).st_name);
                tv_his_5.setText(city_history.get(4).st_name);
                tv_his_6.setText(city_history.get(5).st_name);

                tv_his_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backMain(city_history.get(0).st_name, city_history.get(0).st_code, intent);
                    }
                });
                tv_his_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backMain(city_history.get(1).st_name, city_history.get(1).st_code, intent);
                    }
                });
                tv_his_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backMain(city_history.get(2).st_name, city_history.get(2).st_code, intent);
                    }
                });
                tv_his_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backMain(city_history.get(3).st_name, city_history.get(3).st_code, intent);
                    }
                });
                tv_his_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backMain(city_history.get(4).st_name, city_history.get(4).st_code, intent);
                    }
                });
                tv_his_6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        backMain(city_history.get(5).st_name, city_history.get(5).st_code, intent);
                    }
                });

            }//热门城市
            else if (viewType == 2) {
                convertView = inflater.inflate(R.layout.recent_city, null);
                GridView hotCity = (GridView) convertView
                        .findViewById(R.id.recent_city);

                hotCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        backMain(city_hot.get(position).getSt_name(), city_hot.get(position).getSt_code(), intent);
                    }
                });
                hotCity.setAdapter(new HotCityAdapter(ChoicePlaceActivity1.this, this.hotList));
                TextView hotHint = (TextView) convertView
                        .findViewById(R.id.recentHint);
                hotHint.setText("热门城市");
            } else {
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.list_item, null);
                    holder = new ViewHolder();
                    holder.alpha = (TextView) convertView
                            .findViewById(R.id.alpha);
                    holder.name = (TextView) convertView
                            .findViewById(R.id.name);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.name.setText(list.get(position).getSt_name());
                if (position >= 3) {
                    String currentStr = list.get(position).getIndex().toUpperCase();
                    if (position - 1 == 2) {
                        holder.alpha.setVisibility(View.VISIBLE);
                        holder.alpha.setText(currentStr.toUpperCase());
                    } else if (position - 1 > 2) {
                        String previewStr = list.get(position - 1).getIndex().toUpperCase();
                        if (!previewStr.equals(currentStr)) {
                            holder.alpha.setVisibility(View.VISIBLE);
                            holder.alpha.setText(currentStr);
                        } else {
                            holder.alpha.setVisibility(View.GONE);
                        }
                    }
                }
            }
            return convertView;
        }

        private class ViewHolder {
            TextView alpha; // 首字母标题
            TextView name; // 城市名字
        }
    }

    /**
     * 返回主界面
     *
     * @param st_name 城市名
     * @param st_code 城市代码
     * @param intent
     */
    private void backMain(String st_name, String st_code, Intent intent) {
        intent = new Intent();

        Bundle bundle = new Bundle();
        bundle.putString("to_city", st_name);
        bundle.putString("to_code", st_code);

        commonUtils.setParam(ChoicePlaceActivity1.this, "city", st_name, "to_city");
        commonUtils.setParam(ChoicePlaceActivity1.this, "city_code", st_code, "to_city_code");
        intent.putExtras(bundle);

        ChoicePlaceActivity1.this.setResult(0, intent);
        ChoicePlaceActivity1.this.finish();
    }

    /**
     * 热门城市的Adapter
     */
    class HotCityAdapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater;
        private List<City12306> hotCitys;

        public HotCityAdapter(Context context, List<City12306> hotCitys) {
            this.context = context;
            inflater = LayoutInflater.from(this.context);
            this.hotCitys = hotCitys;
        }

        @Override
        public int getCount() {
            return hotCitys.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = inflater.inflate(R.layout.item_city, null);
            TextView city = (TextView) convertView.findViewById(R.id.city);
            city.setText(hotCitys.get(position).getSt_name());
            return convertView;
        }
    }

    /**
     * 初始化滑动监听
     */
    private void initEvent() {
        quickIndexBar.setOnTouchLetterListener(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        personList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position >= 3) {

                    intent = new Intent();
                    intent.putExtra("to_city", allCity_lists.get(position).getSt_name());
                    intent.putExtra("to_code", allCity_lists.get(position).getSt_code());

                    commonUtils.setParam(ChoicePlaceActivity1.this, "city", allCity_lists.get(position).getSt_name(), "to_city");
                    commonUtils.setParam(ChoicePlaceActivity1.this, "city_code", allCity_lists.get(position).getSt_code(), "to_city_code");

                    ChoicePlaceActivity1.this.setResult(0, intent);
                    ChoicePlaceActivity1.this.finish();
                }
            }
        });
    }

    /**
     * 初始化数据库
     */
    private void initDB() {
        if (dataBase == null) {
            // 创建数据库,传入当前上下文对象和数据库名称
            dataBase = LiteOrm.newSingleInstance(ChoicePlaceActivity1.this,
                    "station_16.sqlite");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                datas = dataBase.query(City12306.class);
                Collections.sort(datas);
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    private void initDatas() {

        mAdapter = new ChoiceCityAdapter(ChoicePlaceActivity1.this, datas);
        autoCompleteAdapter = new AutoCompleteAdapter(ChoicePlaceActivity1.this, datas);


        autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(ChoicePlaceActivity1.this, MainActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("to_city", fiterList.get(position).getSt_name());
                bundle.putString("to_code", fiterList.get(position).getSt_code());

                commonUtils.setParam(ChoicePlaceActivity1.this, "city", city_hot.get(position).getSt_name(), "to_city");
                commonUtils.setParam(ChoicePlaceActivity1.this, "city_code", city_hot.get(position).getSt_code(), "to_city_code");

                intent.putExtras(bundle);
                ChoicePlaceActivity1.this.setResult(0, intent);
                ChoicePlaceActivity1.this.finish();

                autoCompleteTextView.setText("");
            }
        });
    }

    /**
     * 初始化视图和控件
     */
    private void initView() {
        quickIndexBar = (QuickIndexBar) findViewById(R.id.quickIndexbar);
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.et_serach);
        imageView = (ImageView) findViewById(R.id.back);
        tv_show = (TextView) findViewById(R.id.tv_show);

        personList = (ListView) findViewById(R.id.list_view);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        allCity_lists = new ArrayList<>();
        city_hot = new ArrayList<>();
    }

    /**
     * 自定义QuickIndexBar所暴露的回调接口
     *
     * @param letter 字母
     * @param index  索引
     */
    @Override
    public void onTouchLetter(String letter, int index) {
        //根据当前触摸的字母，去集合中找那个item的首字母和letter一样，然后将对于的item放置到顶部
        isScroll = false;
        if (letter.equals("当前")) {
            personList.setSelection(0);
        } else if (letter.equals("历史")) {
            personList.setSelection(1);
        } else if (letter.equals("热门")) {
            personList.setSelection(2);
        } else {
            for (int i = 0; i < datas.size(); i++) {
                String firstWord = datas.get(i).getIndex().toUpperCase();
                if (firstWord.equals(letter)) {
                    personList.setSelection(3 + i);
                    break;
                }
            }
        }
    }

    /**
     * 自动补全控件的Adapter
     */
    public class AutoCompleteAdapter extends BaseAdapter implements Filterable {

        private ArrayFilter mFilter;

        public AutoCompleteAdapter(Context context, ArrayList<City12306> data) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            mDataList = data;
        }

        @Override
        public int getCount() {
            return fiterList.size();
        }

        @Override
        public Object getItem(int position) {
            return fiterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.auto_item, null, false);
                viewHolder = new ViewHolder();
                viewHolder.autoItem = (TextView) convertView.findViewById(R.id.tv_city);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            City12306 city12306 = fiterList.get(position);
            viewHolder.autoItem.setText(city12306.getSt_name());
            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new ArrayFilter();
            }
            return mFilter;
        }

        private class ArrayFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                FilterResults results = new FilterResults();

                if (prefix == null || prefix.length() == 0) {
                    synchronized (mLock) {
                        //Log.i("tag", "mOriginalValues.size=" + mDataList.size());
                        ArrayList<City12306> list = new ArrayList<>(mDataList);
                        results.values = list;
                        results.count = list.size();
                        return results;
                    }
                } else {
                    String prefixString = prefix.toString().toLowerCase();
                    final int count = mDataList.size();

                    final ArrayList<City12306> newValues = new ArrayList<>(count);

                    for (int i = 0; i < count; i++) {
                        City12306 city12306 = new City12306();

                        final String value = mDataList.get(i).getIndex();
                        // final String valueText = value.toLowerCase();
                        if (mDataList.get(i).getSt_name().contains(prefixString)
                                || mDataList.get(i).getSt_py_full().contains(prefixString.toLowerCase()) || mDataList.get(i).getSt_py_s2().contains(prefixString.toLowerCase())) {
                            city12306.setSt_name(mDataList.get(i).getSt_name());
                            city12306.setSt_code(mDataList.get(i).getSt_code());
                            newValues.add(city12306);
                        }
                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                fiterList = (ArrayList<City12306>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    /**
     * 接收百度定位信息的监听器
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            String city = location.getCity();
            if (!isNeedFresh) {
                return;
            }
            isNeedFresh = false;
            if (city == null) {
                locateProcess = 3; // 定位失败
                personList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return;
            }
            currentCity = city.substring(0,
                    city.length() - 1);
            locateProcess = 2; // 定位成功
            personList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}
