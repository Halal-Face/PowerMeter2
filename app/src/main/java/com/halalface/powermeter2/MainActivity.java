package com.halalface.powermeter2;

import android.content.Context;
import android.content.Intent;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
//code for expandable view from https://github.com/bij-ace/dynamic-edittext-in-expandable-listview-android

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ArrayList<ListItemModel> arrayList;

    ExpandableListView elv;
    CustomListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        arrayList = new ArrayList<>();
        arrayList.add(new ListItemModel("Curls"));
        arrayList.add(new ListItemModel("Bench"));
        arrayList.add(new ListItemModel("Deadlift"));
        arrayList.add(new ListItemModel("Squats"));
        arrayList.add(new ListItemModel("Other stuff"));

        elv = (ExpandableListView)findViewById(R.id.listview);
        adapter = new CustomListAdapter(MainActivity.this, arrayList);
        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
        elv.setAdapter(adapter);

        Button btn = (Button)findViewById(R.id.show);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String all="";
                for (int i=0; i<arrayList.size(); i++){
                    if (arrayList.get(i).getArrayList().size()==3) {
                        all += arrayList.get(i).getTitle()+"\n";
                        all += arrayList.get(i).getArrayList().get(0).getValue() +"\n";
                        all += arrayList.get(i).getArrayList().get(1).getValue() +"\n";
                        all += arrayList.get(i).getArrayList().get(2).getValue() +"\n";
                    }
                }
                Toast.makeText(MainActivity.this, all, Toast.LENGTH_LONG).show();
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

}
