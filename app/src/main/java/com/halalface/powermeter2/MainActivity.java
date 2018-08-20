package com.halalface.powermeter2;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.Toast;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

//code for expandable view from https://github.com/bij-ace/dynamic-edittext-in-expandable-listview-android

public class MainActivity extends AppCompatActivity implements OnDateSelectedListener {
    DrawerLayout drawerLayout;
    ArrayList<ListItemModel> arrayList;
    FloatingActionButton add_exercise;

    FloatingActionButton save_name;
    FloatingActionButton save_date;
    ExpandableListView elv;
    CustomListAdapter adapter;
    Dialog dialog;
    EditText new_name;

    MasterDbHelper mMasterDbHelper;
    PowerDbHelper mPowerDbHelper;
    Context activity_context;

    int calendar_date = 00000000;
    Button calendar_button;

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
    MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //passing layout to adapter
        activity_context = this;

        dialog = new Dialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Add New Entries</font>"));
        drawerLayout = findViewById(R.id.drawer_layout);
        mMasterDbHelper = new MasterDbHelper(getApplicationContext(), "Exercise_Database");
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        return nav(menuItem);
                    }
                });

        arrayList = new ArrayList<>();


        elv = (ExpandableListView)findViewById(R.id.listview);
        adapter = new CustomListAdapter(MainActivity.this, arrayList, findViewById(R.id.c_layout));
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        elv.setAdapter(adapter);

        Button save_data_to_database = (Button) findViewById(R.id.show);


        save_data_to_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(arrayList.size() ==0){
                    Toast.makeText(MainActivity.this, "Add Exercise first!", Toast.LENGTH_LONG).show();
                }

                for (int i=0; i<arrayList.size(); i++){
                    if (elv.isGroupExpanded(i)) {
                        //removes focus. NEEDED for updating
                        View current = getCurrentFocus();
                        if (current != null) current.clearFocus();
                        //removes soft keyboard
                        Activity activity = (Activity) activity_context;
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                        //Find the currently focused view, so we can grab the correct window token from it.
                        View view2 = activity.getCurrentFocus();
                        //If no view currently has focus, create a new one, just so we can grab a window token from it
                        if (view2 == null) {
                            view2 = new View(activity);
                        }
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


                        if( arrayList.get(i).getArrayList().get(0).getValue().matches("")
                                || arrayList.get(i).getArrayList().get(1).getValue().matches("")
                                || arrayList.get(i).getArrayList().get(2).getValue().matches("")){
                            Toast.makeText(MainActivity.this, "Enter Valid Entries", Toast.LENGTH_LONG).show();
                        }
                        else{
                         mPowerDbHelper = new PowerDbHelper(getApplicationContext(), arrayList.get(i).getTitle().replaceAll(" ", "_"));
                         int weight = Integer.parseInt(arrayList.get(i).getArrayList().get(0).getValue());
                         int rep = Integer.parseInt(arrayList.get(i).getArrayList().get(1).getValue());
                         int set = Integer.parseInt(arrayList.get(i).getArrayList().get(2).getValue());
                         String notes = arrayList.get(i).getArrayList().get(3).getValue().replaceAll(" ", "_");

                         int power = weight*rep*set;
                         if(power ==0 || power<0){
                             Toast.makeText(MainActivity.this, "Power is less then or equal to 0", Toast.LENGTH_LONG).show();
                         }
                         else{

                             if(calendar_date == 0){
                                 Toast.makeText(MainActivity.this, "Pick Date Using Button Below", Toast.LENGTH_LONG).show();

                             }else{
                                 //calender date gets updated automatically
                                 Toast.makeText(MainActivity.this, power+" "+ calendar_date, Toast.LENGTH_LONG).show();
                                 mPowerDbHelper.addData(power,calendar_date, notes);


                             }
                         }
                        }
                    }
                }
            }
        });


        calendar_button = findViewById(R.id.calendar_button);
        calendar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                showCalendar();
                dialog.show();
            }
        });


        add_exercise = findViewById(R.id.add_exercise);
        add_exercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddNamePopUp();
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
               dialog.show();

            }
        });

        populateArrayList();
    }

    public void showAddNamePopUp(){

        dialog.setContentView(R.layout.add_name_pop_up);
        new_name = dialog.findViewById(R.id.new_name);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        save_name = (FloatingActionButton) dialog.findViewById(R.id.save_name);
        save_name.show();
        save_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMasterDbHelper = new MasterDbHelper(getApplicationContext(), "Exercise_Database");
                String new_name_to_add = new_name.getText().toString().replaceAll(" ", "_");
                if(!new_name_to_add.matches("")) {
                    AddData(new_name_to_add.replaceAll(" ", "_"));
                    //create new power db for name
                    PowerDbHelper pPowerDbHelper = new PowerDbHelper(getApplicationContext(), new_name_to_add.replaceAll(" ", "_"));
                    dialog.dismiss();
                    dialog.hide();
                }
                else{
                    toastM("Please Enter a Valid name :(");
                }
            }
        });

    }

    public void showCalendar(){

        dialog.setContentView(R.layout.calendar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        calendarView = dialog.findViewById(R.id.calendarView);
        calendarView.setSelectedDate(new Date());

        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
        int temp_date = Integer.parseInt(f.format(new Date()));
        calendar_date = temp_date;

        calendarView.setOnDateChangedListener(this);
        save_date = (FloatingActionButton) dialog.findViewById(R.id.save_date);
        save_date.show();
        save_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    dialog.dismiss();
                    dialog.hide();
            }
        });
    }

    public void AddData(String newEntry){
        boolean insert = mMasterDbHelper.addData(newEntry);
        if(insert){
            toastM("Success");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }else{
            toastM("Exercise already exists");
        }
    }


    private void toastM(String m){
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    private void populateArrayList(){
        //get iterator for data
        Cursor data = mMasterDbHelper.getData();
        //add the data from to the arraylsit
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1));
            arrayList.add(new ListItemModel( data.getString(1).replaceAll("_", " ")));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean nav(MenuItem menuItem){
        // set item as selected to persist highlight
        menuItem.setChecked(true);
        // close drawer when item is tapped
        drawerLayout.closeDrawers();
        Intent intent;
        intent = new Intent(getApplicationContext(), MainActivity.class);
        switch (menuItem.getItemId()) {
            case R.id.add_data:
                //System.out.println("MENU ITEM CLICKED " +"home" );
                intent = new Intent(getApplicationContext(), MainActivity.class);
                break;
            case R.id.view_data:
                //System.out.println("MENU ITEM CLICKED " +"update_add");
                intent = new Intent(getApplicationContext(),  ViewData.class);
                break;

            case R.id.attributions:
                //System.out.println("MENU ITEM CLICKED " +"view_data");
                intent = new Intent(getApplicationContext(),  Attributions.class);
                break;

        }
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        return true;
    }

    @Override
    public void onDateSelected(
            @NonNull final MaterialCalendarView widget,
            @NonNull final CalendarDay date,
            final boolean selected) {
        final String text = selected ? FORMATTER.format(date.getDate()) : "No Selection";
        //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        calendar_date = Integer.parseInt(text.replace("-", ""));
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }


}
