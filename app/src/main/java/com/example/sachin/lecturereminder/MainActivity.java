package com.example.sachin.lecturereminder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.sachin.lecturereminder.dbModel.classData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<String> tabs;
    private int tabPosition,modifyButtonPosition;
    private ArrayList<ArrayList<classData>> fragmentData;
    private ArrayList<classData> pastData,todaysData,futureData;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**Check if user registered or not*/
        SharedPreferences preferences = getSharedPreferences("userData",MODE_PRIVATE);
        boolean isUserExist = preferences.getBoolean("userExist",false);
        if(!isUserExist) {
            if(savedInstanceState == null){
                Intent intent = new Intent(this,RegistrationActivity.class);
                startActivity(intent);
            }
        }

        /**set up toolbar*/
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home Screen");

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.navigation_view);
        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);

        /**set up Navigation drawer*/
        DrawerLayoutFragment fragment = new DrawerLayoutFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.drawer_fragment_layout,fragment);
        fragmentTransaction.addToBackStack("DrawerLayout");
        fragmentTransaction.commit();

        /**initial tab position*/
        tabPosition = 1;
        /**set up UI*/
        setUpUi(tabPosition);
    }
    /**Home screen UI Set up*/
    private void setUpUi(int position){
        pastData = new ArrayList<classData>();
        todaysData = new ArrayList<classData>();
        futureData = new ArrayList<classData>();

        getDataFromDatabase();

        fragmentData = new ArrayList<ArrayList<classData>>();
        fragmentData.add(pastData);
        fragmentData.add(todaysData);
        fragmentData.add(futureData);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabs = new ArrayList<String>();
        tabs.add("PAST CLASSES");
        tabs.add("TODAY CLASSES");
        tabs.add("FUTURE CLASSES");
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(),tabs);
        viewPager.setAdapter(adapter);
        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        viewPager.setCurrentItem(position);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPosition = tab.getPosition();
                //Toast.makeText(MainActivity.this,""+tabPosition,Toast.LENGTH_SHORT).show();
                super.onTabSelected(tab);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_screen_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.insert){
            Intent intent = new Intent(MainActivity.this,AddClassActivity.class);
            intent.putExtra("operation","add");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    /**get data from database*/
    public void getDataFromDatabase(){
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Please wait....");
        dialog.setMessage("Loading Data..");
        dialog.setIndeterminate(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        DatabaseAccess databaseAccess = DatabaseAccess.getDatabaseAccess(MainActivity.this);
        List<classData> alldata = databaseAccess.getAllData();
        /*Collections.sort(alldata, new Comparator<classData>() {
            public int compare(classData o1, classData o2) {
                if (o1.getDateTime() == null || o2.getDateTime() == null)
                    return 0;
                return o1.getDateTime().compareTo(o2.getDateTime());
            }
        });*/
        Date newDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (int i =0;i < alldata.size();i++){
            String currentDateFormat = simpleDateFormat.format(newDate);
            String unknownDateFormat = simpleDateFormat.format(alldata.get(i).getDateTime());
            Date currentDate = null;
            Date unknownDate = null;
            try {
                currentDate = simpleDateFormat.parse(currentDateFormat);
                unknownDate = simpleDateFormat.parse(unknownDateFormat);
            } catch (ParseException e) {
                Log.e("Date pasing Error",e.getLocalizedMessage(),e);
            }
            if(currentDate.after(unknownDate)){
                pastData.add(alldata.get(i));
            }else if(currentDate.before(unknownDate)){
                futureData.add(alldata.get(i));
            }else if(currentDate.equals(unknownDate)){
                todaysData.add(alldata.get(i));
            }
        }
        reversePastData(pastData);
        dialog.dismiss();
    }

    /**revers accending order data for past classes tab*/
    public void reversePastData(ArrayList<classData> classDatas){
        ArrayList<classData> pastDataReverse = new ArrayList<classData>();
        for(int i = (classDatas.size() - 1);i >= 0; i--){
            pastDataReverse.add(classDatas.get(i));
        }
        pastData = pastDataReverse;
    }

    public ArrayList<classData> getDataAt(int position){
        return fragmentData.get(position);
    }

    /**handling modify button clicks*/
    public void onModifyButtonClicked(View view,int recyclerViewPosition){
        showModifyPopup(view);
        modifyButtonPosition = recyclerViewPosition;
    }
    /**display pop up options when modify button clicked*/
    private void showModifyPopup(View v) {
        PopupMenu popup = new PopupMenu(MainActivity.this,v);
        popup.getMenuInflater().inflate(R.menu.button_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        if(tabPosition == 0){
                            Toast.makeText(MainActivity.this, "Sorry You can not edit In this Tab", Toast.LENGTH_LONG).show();
                        }
                        else{
                            callToModify(tabPosition,modifyButtonPosition);
                        }

                        return true;
                    case R.id.delete:
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Are You sure...want to delete");
                        builder.setTitle("Delete Record")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        callToDelete(tabPosition,modifyButtonPosition);
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(MainActivity.this, "delete cancelled", Toast.LENGTH_SHORT).show();
                                    }
                                }).create();
                        builder.show();
                        return true;
                    case R.id.share:
                        callToShare(tabPosition,modifyButtonPosition);
                        //Toast.makeText(MainActivity.this, "share:tabPos:"+tabPosition+"\nModifyButtonPosition:"+modifyButtonPosition, Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }
    /**Modify class Data */
    public void callToModify(int tabPosition, int modifyButtonPosition){
        ArrayList<classData> classDatas = fragmentData.get(tabPosition);
        classData data = classDatas.get(modifyButtonPosition);

        Date dateTime = data.getDateTime();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long milliDate = dateTime.getTime();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(milliDate);
        String dateAndTime = format.format(cal.getTime());

        String date = dateAndTime.substring(0,10);
        String formatedtime = dateAndTime.substring(11,16);

        Intent intent = new Intent(MainActivity.this,AddClassActivity.class);
        intent.putExtra("operation","edit");
        intent.putExtra("id",data.getId().toString());
        intent.putExtra("name",data.getName());
        intent.putExtra("topic",data.getTopic());
        intent.putExtra("professor",data.getProfessor());
        intent.putExtra("date",date);
        intent.putExtra("time",formatedtime);
        intent.putExtra("location",data.getLocation());
        startActivity(intent);
    }
    /**Delete class data*/
    public void callToDelete(int tabPosition, int modifyButtonPosition) {
        ArrayList<classData> classDatas = fragmentData.get(tabPosition);
        classData data = classDatas.get(modifyButtonPosition);
        DatabaseAccess access = DatabaseAccess.getDatabaseAccess(MainActivity.this);
        access.delete(data.getId());
        setUpUi(tabPosition);
        //Toast.makeText(MainActivity.this,"Record Deleted"+data.getId(),Toast.LENGTH_SHORT).show();

    }
    /**Share class data using other application*/
    public void callToShare(int tabPosition, int modifyButtonPosition){
        ArrayList<classData> classDatas = fragmentData.get(tabPosition);
        classData data = classDatas.get(modifyButtonPosition);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        String textMessage = "Class Name: " + data.getName() +
                "\nTopic: " + data.getTopic() +
                "\nLocation: " + data.getLocation() +
                "\nProfessor: " + data.getProfessor() +
                "\nDate Time: "+data.getDateTime();
        shareIntent.putExtra(Intent.EXTRA_TEXT,textMessage);
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent,"Share Class"));
    }
}
