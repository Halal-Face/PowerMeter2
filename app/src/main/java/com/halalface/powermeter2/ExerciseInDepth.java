package com.halalface.powermeter2;

import android.Manifest;
import android.animation.AnimatorSet;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExerciseInDepth extends AppCompatActivity implements OnDateSelectedListener {
    DrawerLayout drawerLayout;
    ListView power_entries_ListView;
    //TextView exercise_name_TextView;
    String exercise_name;
    Dialog dialog;
    PowerDbHelper mPowerDbHelper;
    MasterDbHelper mMasterDbHelper;
    EditText new_power_EditText;
    EditText new_notes_EditText;
    MaterialCalendarView CV;
    com.github.clans.fab.FloatingActionButton delete_name;
    com.github.clans.fab.FloatingActionButton edit_name;
    com.github.clans.fab.FloatingActionButton save_name;
    com.github.clans.fab.FloatingActionButton export;
    com.github.clans.fab.FloatingActionMenu menu;



    EditText newPower;
    EditText edit_name_edittext;
    int new_power = 0;
    int old_date = 0;
    private DateFormat FORMATTER =  new SimpleDateFormat("MMM, dd, yyyy");
    public SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

    int date;
    int update_date;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_in_depth);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final Drawable KEYBOARD_UP = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_up_white_24dp, null);
        final Drawable KEYBOARD_DOWN = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_down_white_24dp, null);

        mMasterDbHelper = new MasterDbHelper(getApplicationContext(), "Exercise_Database");

        dialog = new Dialog(this);
        showEditDelete();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Edit Entries</font>"));
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        return nav(menuItem);
                    }
                });


        power_entries_ListView = findViewById(R.id.l_view);

        edit_name_edittext = findViewById(R.id.edit_name_edittext);
        edit_name_edittext.setFocusable(false);
        edit_name_edittext.setClickable(false);
        //exercise_name_TextView = findViewById(R.id.name);

        Intent receiveIntent = getIntent();
        exercise_name = receiveIntent.getStringExtra("name").replace(" ", "_");
        //exercise_name_TextView.setText(exercise_name.replaceAll("_", " "));
        edit_name_edittext.setText(exercise_name.replaceAll("_", " "));
        //exercise_name_TextView.setTextColor(ContextCompat.getColor(this, R.color.shade4com));

        mPowerDbHelper = new PowerDbHelper(getApplicationContext(),exercise_name);
        populateListView();

        final Toast mtoast = Toast.makeText(getApplicationContext(), exercise_name, Toast.LENGTH_LONG);

        power_entries_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                int date = 0;
                try {
                    Date calendar_date = FORMATTER.parse(item.substring(9, 22));
                    DateFormat spf = new SimpleDateFormat("yyyyMMdd");
                    String newDateString = spf.format(calendar_date);
                    date = Integer.parseInt(newDateString);
                }
                catch (ParseException o){

                }
                Intent change_log_activity = new Intent(ExerciseInDepth.this, ChangeLog.class);
                change_log_activity.putExtra("change",mPowerDbHelper.getChangeLog(date));
                startActivity(change_log_activity);
                return true;
            }
        });

        power_entries_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(ExerciseInDepth.this, item.substring(9, 22), Toast.LENGTH_LONG).show();
                try{
                    Date calendar_date = FORMATTER.parse(item.substring(9, 22));
                    CV.setSelectedDate(calendar_date);
                    CV.isDynamicHeightEnabled();
                    DateFormat spf = new SimpleDateFormat("yyyyMMdd");
                    String newDateString = spf.format(calendar_date);
                    old_date = Integer.parseInt(newDateString);
                    update_date = Integer.parseInt(newDateString);
                }catch(ParseException o){
                }
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
        });
        FloatingActionButton updateItemBttn = dialog.findViewById(R.id.save);
        updateItemBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newPower.getText().toString().equals("")){
                    new_power = Integer.parseInt(newPower.getText().toString());
                    if(new_power!=0) {
                        if(mPowerDbHelper.updateItem(new_power, old_date, update_date, new_notes_EditText.getText().toString().replaceAll(" ", "_"))){
                            Toast.makeText(ExerciseInDepth.this, "Updates: " + new_power, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            dialog.hide();


                            Intent intent = new Intent(ExerciseInDepth.this, ExerciseInDepth.class);
                            intent.putExtra("name", exercise_name);
                            startActivity(intent);
                        }else{
                            Toast.makeText(ExerciseInDepth.this, "Error  Inserting Values", Toast.LENGTH_LONG).show();
                        }

                    }
                }else{
                    Toast.makeText(ExerciseInDepth.this, "Zero Power Detected", Toast.LENGTH_LONG).show();

                }
            }
        });

        FloatingActionButton delete_entry = dialog.findViewById(R.id.delete);
        delete_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    mPowerDbHelper.deleteItem(old_date);
                    Intent intent = new Intent(ExerciseInDepth.this, ExerciseInDepth.class);
                    intent.putExtra("name", exercise_name);
                    startActivity(intent);


            }
        });



        AnimatorSet set = new AnimatorSet();
        menu = findViewById(R.id.menu);
        menu.setIconAnimated(false);
        menu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menu.isOpened()) {
                    // We will change the icon when the menu opens, here we want to change to the previous icon
                    menu.close(true);
                    menu.getMenuIconView().setImageDrawable(KEYBOARD_UP);
                } else {
                    // Since it is closed, let's set our new icon and then open the menu
                    menu.getMenuIconView().setImageDrawable(KEYBOARD_DOWN);
                    menu.open(true);
                }
            }
        });

        delete_name = findViewById(R.id.delete_name);
        delete_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMasterDbHelper.deleteItem(exercise_name);
                ExerciseInDepth.this.deleteDatabase(exercise_name);
                Intent intent = new Intent(getApplicationContext(),  MainActivity.class);
                startActivity(intent);

            }
        });

        save_name = findViewById(R.id.save_name);
        save_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(ExerciseInDepth.this, "Edit", Toast.LENGTH_LONG).show();
                String newItem = edit_name_edittext.getText().toString().replaceAll(" ", "_");
                if(!newItem.matches("") && !newItem.isEmpty()){
                    if(!newItem.matches(exercise_name)){
                        toastM("Changing " + exercise_name +" to " + newItem);
                        Cursor data = mMasterDbHelper.getItemID(exercise_name);
                        int id = -1;
                        while(data.moveToNext()){
                            id = data.getInt(0);
                        }
                        if(mMasterDbHelper.updateItem(newItem, id, exercise_name)) {
                            mPowerDbHelper = new PowerDbHelper(getApplicationContext(), exercise_name);
                            mPowerDbHelper.updateDbName(newItem.replaceAll(" ", "_"));
                            ExerciseInDepth.this.deleteDatabase(exercise_name);

                        }
                    }
                    else{
                        toastM("New name is same as previous");
                    }

                    //startActivity(backToEditIntent);
                }
                else{
                    toastM("New Exercise name is empty :(");
                }
                save_name.setVisibility(View.GONE);
                edit_name_edittext.setFocusable(false);
                edit_name_edittext.setClickable(false);
                edit_name_edittext.setFocusableInTouchMode(false);
            }

        });



        edit_name = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.edit_name);
        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    edit_name_edittext.setFocusable(true);
                    edit_name_edittext.setClickable(true);
                    edit_name_edittext.setFocusableInTouchMode(true);
                    save_name.setVisibility(View.VISIBLE);



            }
        });

        export = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.export_database);
        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isStoragePermissionGranted()){
                    PowerDbHelper mPowerDbHelper = new PowerDbHelper(getApplicationContext(), exercise_name);
                    mPowerDbHelper.exportDB();
                }


            }
        });



    }

    public void showEditDelete(){

        dialog.setContentView(R.layout.edit_delete_popup);
        CV = dialog.findViewById(R.id.calendarView2);
        //this is for new poer
        newPower = dialog.findViewById(R.id.new_power);
        FloatingActionButton delete = dialog.findViewById(R.id.delete);
        new_power_EditText = dialog.findViewById(R.id.new_power);
        new_notes_EditText = dialog.findViewById(R.id.new_notes);
        new_power_EditText.setHint("New Power entry");
        new_notes_EditText.setHint("New Notes entry");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FloatingActionButton save = (FloatingActionButton) dialog.findViewById(R.id.save);
        save.show();
        delete.show();
    }

    private void toastM(String m){
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
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
        startActivity(intent);
        return true;
    }
    public void populateListView(){
        //get iterator for data
        Cursor data = mPowerDbHelper.getData();
        //add the data from to the arraylsit
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            try
            {
            Date date = new SimpleDateFormat("yyyyMMdd").parse(data.getString(2));
            String date_string = "Date:    "+ FORMATTER.format(date);
            listData.add( date_string + "\nPower: "+ data.getString(1) + "\nNotes:  " + data.getString(3).replaceAll("_", " "));
            }
            catch (ParseException o){
                Toast.makeText(ExerciseInDepth.this, "Parse Error", Toast.LENGTH_LONG).show();

            }

        }
        //used to populate the listview
        ListAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item_1, listData);
        power_entries_ListView.setAdapter(adapter);
    }
    @Override
    public void onDateSelected(
            @NonNull final MaterialCalendarView widget,
            @NonNull final CalendarDay date,
            final boolean selected) {

        String text = selected ?dateFormat.format(date.getDate()) : "No Selection";
        update_date = Integer.parseInt(text.replace("-", "").replace("/", ""));

    }
    public  boolean isStoragePermissionGranted() {

            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                Log.v("Exercise In Depth","Permission is granted");
                return true;
            } else {

                Log.v("Exercise In Depth","Permission is revoked");
                ActivityCompat.requestPermissions(ExerciseInDepth.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                ActivityCompat.requestPermissions(ExerciseInDepth.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                    return true;
                }
                else{
                    toastM("Please Enable Read/ Write Permissions");
                    return false;
                }
            }

    }
}
