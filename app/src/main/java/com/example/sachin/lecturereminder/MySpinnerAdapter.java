package com.example.sachin.lecturereminder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Sachin on 10/19/2016.
 */
/**spinner adapter*/
public class MySpinnerAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> arrayList;
    MySpinnerAdapter(Context context, ArrayList<String> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }
    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.spinner_layout,parent,false);
        TextView label = (TextView)view.findViewById(R.id.spinner_text);
        label.setText(arrayList.get(position));
        return view;
    }
}
