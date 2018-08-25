package com.halalface.powermeter2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewData extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    MasterDbHelper mMasterDbHelper;
    ListView mListView;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Select Your Exercise</font>"));

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

        mMasterDbHelper = new MasterDbHelper(this, "Exercise_Database");
        mListView = findViewById(R.id.listView);
        populateListView();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString().replace(" ", "_");
                Cursor data = mMasterDbHelper.getItemID(item);
                int itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                if(itemID>-1){
                    Intent editDbIntent = new Intent(ViewData.this, GraphData.class);
                    editDbIntent.putExtra("TABLE_NAME", item);
                    startActivity(editDbIntent);

                }
            }
        });
    }
    public void populateListView(){
        //get iterator for data
        Cursor data = mMasterDbHelper.getData();
        //add the data from to the arraylsit
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(1).replaceAll("_", " "));
        }
        //used to populate the listview
        ListAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item_1, listData);
        mListView.setAdapter(adapter);
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

}
