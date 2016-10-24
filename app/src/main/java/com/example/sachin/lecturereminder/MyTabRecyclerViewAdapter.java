package com.example.sachin.lecturereminder;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sachin.lecturereminder.dbModel.classData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Sachin on 10/20/2016.
 */

/**tab layout recycler view adapter */
public class MyTabRecyclerViewAdapter extends RecyclerView.Adapter<MyTabRecyclerViewAdapter.ViewHolder> {
    private Context context;
    public ItemClickListener itemClickListener;
    private ArrayList<classData> recyclerViewData;
    MyTabRecyclerViewAdapter(Context context,ArrayList<classData> recyclerViewData) {
        this.context = context;
        this.recyclerViewData = recyclerViewData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(R.layout.tab_fragment_recycler_view_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = "Name:" + recyclerViewData.get(position).getName();
        holder.className.setText(name);

        Date dateTime = recyclerViewData.get(position).getDateTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long milliDate = dateTime.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliDate);
        String dateAndTime = format.format(cal.getTime());
        Log.i("Date and Time",dateAndTime+"::::"+dateTime);

        String date = dateAndTime.substring(0,10);
        String formatedtime = dateAndTime.substring(11,16);
        Log.i("Date,Time",date+","+formatedtime);
        String time = "Time: " + date + "  " + formatedtime;
        holder.classTime.setText(time);
        String topic = "Topic:" + recyclerViewData.get(position).getTopic();
        holder.classDescription.setText(topic);
        String professor = "Professor:" + recyclerViewData.get(position).getProfessor();
        holder.classProfessor.setText(professor);
        String location = "Location: " + recyclerViewData.get(position).getLocation();
        holder.location.setText(location);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return recyclerViewData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView className,classTime,classDescription,classProfessor,location;
        Button modifyClass;
        public ViewHolder(View itemView) {
            super(itemView);
            className = (TextView) itemView.findViewById(R.id.name);
            classTime = (TextView) itemView.findViewById(R.id.time);
            classDescription = (TextView) itemView.findViewById(R.id.description);
            classProfessor = (TextView) itemView.findViewById(R.id.professor);
            modifyClass = (Button) itemView.findViewById(R.id.modify);
            location = (TextView) itemView.findViewById(R.id.location);
            modifyClass.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view,getAdapterPosition());
        }
    }
}
