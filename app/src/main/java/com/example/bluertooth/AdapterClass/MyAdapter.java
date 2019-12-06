package com.example.bluertooth.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bluertooth.R;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private List<Info> appInfos;
    private Context context;

    public MyAdapter( Context context,List<Info> info){
        this.appInfos=info;
        this.context=context;
    }
    public int getCount() {
        return appInfos.size();
    }

    public Object getItem(int i) {
        return appInfos.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View item=view!=null? view:View.inflate(context,R.layout.item,null);
        ImageView i_img=item.findViewById(R.id.i_img);
        TextView name=item.findViewById(R.id.i_tv1);
        TextView adrress=item.findViewById(R.id.i_tv2);

        final  Info a=appInfos.get(i);
        i_img.setImageResource(a.getImg());
        name.setText(a.getName());
        adrress.setText(a.getAddress());
        return item;
    }
}
