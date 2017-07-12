package dda.com.ticket12306.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageView;

import dda.com.ticket12306.R;

/**
 * Created by nuo on 2016-09-12.
 * Created by 16:10.
 * 描述:出发日期界面
 */
public class GoPlaceCalendarActivity extends Activity implements CalendarView.OnDateChangeListener {

    //日历控件
    private CalendarView calendarView;
    //返回主界面
    private ImageView back;
    //判断是否选择了城市
    private boolean isClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serach_calendar);

        initView();
        initEvent();
        isClick = false;
    }

    private void initEvent() {
        calendarView.setOnDateChangeListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化视图
     */
    private void initView() {
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        back = (ImageView) findViewById(R.id.back);
    }

    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
        isClick = true;
        String month1;
        String dayOfMonth1;
        Intent intent = new Intent();
        month = month + 1;
        if (month < 10) {
            month1 = "0" + month;
        } else {
            month1 = "" + month;
        }
        if (dayOfMonth < 10) {
            dayOfMonth1 = "0" + dayOfMonth;
        } else {
            dayOfMonth1 = "" + dayOfMonth;
        }
        String date = year + "-" + month1 + "-" + dayOfMonth1;
        intent.putExtra("sel_date", date);
        intent.putExtra("isClick", isClick);
        GoPlaceCalendarActivity.this.setResult(2, intent);
        GoPlaceCalendarActivity.this.finish();
    }
}
