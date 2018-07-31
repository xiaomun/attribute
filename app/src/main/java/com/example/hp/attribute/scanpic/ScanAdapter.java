package com.example.hp.attribute.scanpic;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.attribute.R;

import java.io.File;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * Created by shxioyang on 2018/7/31.
 */

public class ScanAdapter extends BaseAdapter {
    List<MediaBean> list;
    Context context;
    public ScanAdapter(Context context,List<MediaBean> list) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(list == null){
            return 0;
        }
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view;
        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_pic, parent, false);
             viewHolder = new ViewHolder();
             viewHolder.imgPic = view.findViewById(R.id.img_pic);
            viewHolder.tvName = view.findViewById(R.id.tv_name);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.imgPic.setImageURI(Uri.fromFile(new File(list.get(position).paht)));
        viewHolder.tvName.setText(list.get(position).displayname);

        return view;
    }
    class ViewHolder {
        ImageView imgPic;
        TextView tvName;
    }
}
