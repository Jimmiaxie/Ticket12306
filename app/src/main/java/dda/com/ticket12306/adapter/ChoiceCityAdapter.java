package dda.com.ticket12306.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dda.com.ticket12306.R;
import dda.com.ticket12306.entity.City12306;

/**
 * Created by nuo on 2016-09-13.
 * Created by 9:08.
 * 描述:选择城市的适配器
 */
public class ChoiceCityAdapter extends RecyclerView.Adapter<MyViewHoder> {
    //用于每个item的布局
    private LayoutInflater mInflater;
    private Context mContext;
    protected ArrayList<City12306> mDatas;

    @Override
    //创建ViewHoler
    public MyViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        //item布局
        View view = mInflater.inflate(R.layout.city_item, parent, false);
        //传入item布局
        MyViewHoder viewHoder = new MyViewHoder(view);
        return viewHoder;
    }

    public void addData(int position) {
        //不会去刷新所有的View 就不会重置position
        notifyItemInserted(position);
    }

    public void delData(int position) {
        notifyItemRemoved(position);
    }

    @Override
    //绑定ViewHolder
    public void onBindViewHolder(final MyViewHoder holder, int position) {

        City12306 city12306 = mDatas.get(position);
        holder.tv_show_city.setText(mDatas.get(position).getSt_name());

        //获取当前城市的序列
        String currentWord = city12306.getIndex();

        if (position > 0) {
            //获取上一个item的首字母
            String lastWord = mDatas.get(position - 1).getIndex();
            //拿当前的首字母和上一个首字母比较
            if (currentWord.equals(lastWord)) {
                //说明首字母相同，需要隐藏当前item的first_word
                holder.textView.setVisibility(View.GONE);
            } else {
                //不一样，需要显示当前的首字母
                //由于布局是复用的，所以在需要显示的时候，再次将first_word设置为可见
                holder.textView.setVisibility(View.VISIBLE);
                holder.textView.setText(currentWord.toUpperCase());
            }
        } else {
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(currentWord.toUpperCase());
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public ChoiceCityAdapter(Context context, ArrayList<City12306> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(context);
    }
}

class MyViewHoder extends RecyclerView.ViewHolder {
    public TextView textView;
    public TextView tv_show_city;
    public ImageView imageView;

    public MyViewHoder(View itemView) {
        super(itemView);
        textView = (TextView) itemView.findViewById(R.id.tv_show_letter);
        tv_show_city = (TextView) itemView.findViewById(R.id.tv_show_city);
        imageView = (ImageView) itemView.findViewById(R.id.iv_duigou);
    }

}
