package cn.rjgc.voicechat.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.rjgc.voicechat.R;
import cn.rjgc.voicechat.View.MainActivity;

/**
 * Created by Forever on 2015/6/7.
 */
public class RecorderAdapter extends ArrayAdapter<MainActivity.Recorder> {




    private int mMinItemWidth;
    private int mMaxItemWidth;

    private LayoutInflater mInflater;

    public RecorderAdapter(Context context, List<MainActivity.Recorder> datas) {
        super(context, -1,datas);


        mInflater=LayoutInflater.from(context);

        //»ñÈ¡ÆÁÄ»¿í¶È
        WindowManager wm=(WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);


        mMaxItemWidth=(int)(outMetrics.widthPixels*0.7f);
        mMinItemWidth=(int)(outMetrics.widthPixels*0.15f);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            convertView=mInflater.inflate(R.layout.item_recorder,parent,false);
            holder=new ViewHolder();
            holder.seconds=(TextView)convertView.findViewById(R.id.id_recorder_time);
            holder.length=convertView.findViewById(R.id.id_recorder_length);

            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        holder.seconds.setText(Math.round(getItem(position).getTime())+"\"");
        ViewGroup.LayoutParams ip=holder.length.getLayoutParams();
        ip.width=(int)(mMinItemWidth+(mMaxItemWidth/60f*getItem(position).getTime()));
        return convertView;
    }

    private class ViewHolder{
        TextView seconds;
        View length;
    }
}

