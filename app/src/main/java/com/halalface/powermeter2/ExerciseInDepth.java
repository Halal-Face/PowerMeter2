package com.halalface.powermeter2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
    TextView exercise_name_TextView;
    String exercise_name;
    Dialog dialog;
    PowerDbHelper mPowerDbHelper;
    MasterDbHelper mMasterDbHelper;
    EditText new_power_EditText;
    EditText new_notes_EditText;
    MaterialCalendarView CV;
    com.github.clans.fab.FloatingActionButton delete_name;
    com.github.clans.fab.FloatingActionButton edit_name;
    com.github.clans.fab.FloatingActionMenu menu;


    EditText newPower;
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

        final Drawable KEYBOARD_UP = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_up_black_24dp, null);
        final Drawable KEYBOARD_DOWN = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_keyboard_arrow_down_black_24dp, null);

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

        exercise_name_TextView = findViewById(R.id.name);

        Intent receiveIntent = getIntent();
        exercise_name = receiveIntent.getStringExtra("name").replace(" ", "_");
        exercise_name_TextView.setText(exercise_name.replaceAll("_", " "));
        exercise_name_TextView.setTextColor(ContextCompat.getColor(this, R.color.shade4com));

        mPowerDbHelper = new PowerDbHelper(getApplicationContext(),exercise_name);
        populateListView();

        final Toast mtoast = Toast.makeText(getApplicationContext(), exercise_name, Toast.LENGTH_LONG);

        power_entries_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
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

                    //Toast.makeText(ExerciseInDepth.this, update_date+"", Toast.LENGTH_LONG).show();

                }catch(ParseException o){
                    //Toast.makeText(ExerciseInDepth.this, date, Toast.LENGTH_LONG).show();
                }
                return false;
            }
        });
        FloatingActionButton updateItemBttn = dialog.findViewById(R.id.save);
        updateItemBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!newPower.getText().toString().equals("")){
                    new_power = Integer.parseInt(newPower.getText().toString());
                    if(new_power!=0) {
                        Toast.makeText(ExerciseInDepth.this, old_date+"", Toast.LENGTH_LONG).show();
                        if(mPowerDbHelper.updateItem(new_power, old_date, update_date, new_notes_EditText.getText().toString().replaceAll(" ", "_"))){
                            Toast.makeText(ExerciseInDepth.this, "Updates: " + new_power, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            dialog.hide();
                        }else{
                            Toast.makeText(ExerciseInDepth.this, "error", Toast.LENGTH_LONG).show();
                        }

                    }
                }else{
                    Toast.makeText(ExerciseInDepth.this, "Zero Power Detected", Toast.LENGTH_LONG).show();

                }


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

        edit_name = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.edit_name);
        edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ExerciseInDepth.this, "Edit", Toast.LENGTH_LONG).show();
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
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMasterDbHelper = new MasterDbHelper(getApplicationContext(), "Exercise_Database");
                String new_power_to_add = new_power_EditText.getText().toString().replaceAll(" ", "_");

                if(!new_power_to_add.matches("")) {

                    System.out.println("NAME: " + exercise_name);
                    int newItem = Integer.parseInt(new_power_to_add);

                    Cursor data = mPowerDbHelper.getItemID(date);
                    int itemID = -1;
                    if(data.moveToNext()){
                        itemID = data.getInt(0);
                    }
                    if(itemID>-1){
                        String TABLE_NAME = mPowerDbHelper.getTABLE_NAME();

                    }


                    //mPowerDbHelper.updateItem(newItem, itemID, new_notes_to_add);

                    dialog.dismiss();
                    dialog.hide();
                }
                else{
                    toastM("Please Enter a Valid name :(");
                }
            }
        });

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
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        update_date = Integer.parseInt(text.replace("-", "").replace("/", ""));

    }
}
