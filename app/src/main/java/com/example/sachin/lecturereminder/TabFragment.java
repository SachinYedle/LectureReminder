package com.example.sachin.lecturereminder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sachin.lecturereminder.dbModel.classData;

import java.util.ArrayList;

/**
 * Created by Sachin on 10/20/2016..
 */


/**tab layout fragment*/
public class TabFragment extends Fragment implements ItemClickListener {
    private RecyclerView recyclerView;
    private int position;
    MyTabRecyclerViewAdapter recyclerViewAdapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout,container,false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager( new LinearLayoutManager( getActivity()));
        ArrayList<classData> recyclerViewData = ((MainActivity)getActivity()).getDataAt(position);
        recyclerViewAdapter = new MyTabRecyclerViewAdapter(getActivity(),recyclerViewData);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setItemClickListener(this);
        return view;
    }

    public void setPosition(int position){
        this.position = position;
    }
    public int getPosition(){
        return position;
    }

    @Override
    public void onClick(View view, int position) {
        ((MainActivity)getActivity()).onModifyButtonClicked(view,position);
    }
}
