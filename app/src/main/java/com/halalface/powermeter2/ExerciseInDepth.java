package com.halalface.powermeter2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ExerciseInDepth extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ListView power_entries_ListView;
    TextView exercise_name_TextView;
    String exercise_name;
    Dialog dialog;
    PowerDbHelper mPowerDbHelper;
    MasterDbHelper mMasterDbHelper;
    EditText new_name_EditText;
    FloatingActionButton add;
    FloatingActionButton delete;
    int date;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_in_depth);

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


        System.out.println("DATE: substring !!" + date +"!!");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        NavigationView navigationView = findViewById(R.id.nav_view);
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
        exercise_name = receiveIntent.getStringExtra("name");
        exercise_name_TextView.setText(exercise_name.replaceAll("_", " "));

        mPowerDbHelper = new PowerDbHelper(getApplicationContext(),exercise_name);
        populateListView();

        final Toast mtoast = Toast.makeText(getApplicationContext(), exercise_name, Toast.LENGTH_LONG);

        power_entries_ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(mtoast!=null){
                    mtoast.show();
                }
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
                mtoast.show();
                String item = parent.getItemAtPosition(position).toString();
                date = Integer.parseInt(item.substring(6, 14));
                return false;
            }
        });

        add = findViewById(R.id.add);
        delete = findViewById(R.id.delete);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMasterDbHelper.deleteItem(exercise_name);
                ExerciseInDepth.this.deleteDatabase(exercise_name);
                Intent intent = new Intent(getApplicationContext(),  MainActivity.class);
                startActivity(intent);

            }
        });

    }

    public void showEditDelete(){

        dialog.setContentView(R.layout.edit_delete_popup);

        Button delete = dialog.findViewById(R.id.delete);
        new_name_EditText = dialog.findViewById(R.id.new_name);
        new_name_EditText.setHint("New Power entry");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        FloatingActionButton save = (FloatingActionButton) dialog.findViewById(R.id.save);
        save.show();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMasterDbHelper = new MasterDbHelper(getApplicationContext(), "Exercise_Database");
                String new_name_to_add = new_name_EditText.getText().toString().replaceAll(" ", "_");
                if(!new_name_to_add.matches("")) {

                    System.out.println("NAME: " + exercise_name);
                    int newItem = Integer.parseInt(new_name_to_add);

                    Cursor data = mPowerDbHelper.getItemID(date);
                    int itemID = -1;
                    if(data.moveToNext()){
                        itemID = data.getInt(0);
                    }
                    if(itemID>-1){
                        String TABLE_NAME = mPowerDbHelper.getTABLE_NAME();

                    }


                    mPowerDbHelper.updateItem(newItem, itemID);

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
            listData.add( "Date: " + data.getString(2)+ " Power: "+ data.getString(1));
        }
        //used to populate the listview
        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        power_entries_ListView.setAdapter(adapter);
    }
}
