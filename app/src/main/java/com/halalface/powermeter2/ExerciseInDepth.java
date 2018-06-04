package com.halalface.powermeter2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    PowerDbHelper mPowerDbHelper;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise_in_depth);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Add Data!</font>"));

        drawerLayout = findViewById(R.id.drawer_layout);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                mtoast.show();
                return false;
            }
        });

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
            case R.id.home:
                //System.out.println("MENU ITEM CLICKED " +"home" );
                break;
            case R.id.update_add:
                //System.out.println("MENU ITEM CLICKED " +"update_add");
                intent = new Intent(getApplicationContext(),  MainActivity.class);
                break;

            case R.id.view_data:
                //System.out.println("MENU ITEM CLICKED " +"view_data");
                intent = new Intent(getApplicationContext(),  MainActivity.class);
                break;

            case R.id.edit:
                //System.out.println("MENU ITEM CLICKED " +"edit");
                intent = new Intent(getApplicationContext(),  MainActivity.class);
                break;

            case R.id.pr:
                //System.out.println("MENU ITEM CLICKED " +"pr");
                intent = new Intent(getApplicationContext(),  MainActivity.class);
                break;
            case R.id.atributions:
                //System.out.println("MENU ITEM CLICKED " +"pr");
                intent = new Intent(getApplicationContext(),  MainActivity.class);
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
