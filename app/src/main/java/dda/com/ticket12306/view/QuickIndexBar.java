package dda.com.ticket12306.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import dda.com.ticket12306.R;

/**
 * Created by nuo on 2016-08-24.
 * Created by 10:57.
 * 描述:
 */
public class QuickIndexBar extends View {

    //26个英文字母
    private String[] indexArr = {getResources().getString(R.string.app_now), getResources().getString(R.string.app_history), getResources().getString(R.string.app_hot), "A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z"};

    private Paint paint;
    private int width;
    private float cellHeight;

    public QuickIndexBar(Context context) {
        super(context);
        init();
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);//设置抗锯齿
        paint.setColor(getResources().getColor(R.color.blue));
        paint.setTextSize(getResources().getDimension(R.dimen.cell_text_size));
        paint.setTextAlign(Paint.Align.CENTER);//设置文本的
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        //得到一个格子的高度
        cellHeight = getMeasuredHeight() * 1f / indexArr.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < indexArr.length; i++) {
            float x = width / 2;
            float y = cellHeight / 2 + getTextHeight(indexArr[i]) / 2 + i * cellHeight;

            canvas.drawText(indexArr[i], x, y, paint);
        }

    }

    private int lastIndex = -1;//记录上次的字母触摸的索引

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                int index = (int) (y / cellHeight);//得到字母对应的索引

                if (lastIndex != index) {
                    //对index做安全性的检查
                    if (index >= 0 && index < indexArr.length) {
                        if (listener != null) {
                            listener.onTouchLetter(indexArr[index],index);
                        }
                    }
                }
                lastIndex = index;
                break;
            case MotionEvent.ACTION_UP:
                //重置lastIndex
                lastIndex = -1;
                break;
            default:
                break;
        }
        //引起重绘
        invalidate();

        return true;
    }

    /**
     * 获取文本的高度
     *
     * @param text
     * @return
     */
    private int getTextHeight(String text) {
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, 1, bounds);
        return bounds.height();
    }

    private OnTouchLetterListener listener;

    public void setOnTouchLetterListener(OnTouchLetterListener listener) {
        this.listener = listener;
    }

    /**
     * 触摸字母的监听器
     */
    public interface OnTouchLetterListener {
        void onTouchLetter(String letter,int index);
    }
}
