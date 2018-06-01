package com.halalface.powermeter2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.Toast;


import java.util.ArrayList;

//code for expandable view from https://github.com/bij-ace/dynamic-edittext-in-expandable-listview-android

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ArrayList<ListItemModel> arrayList;
    FloatingActionButton add;

    FloatingActionButton save;
    ExpandableListView elv;
    CustomListAdapter adapter;
    Dialog dialog;
    EditText new_name;

    MasterDbHelper mMasterDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialog = new Dialog(this);
        showAddNamePopUp();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Add Data!</font>"));

        drawerLayout = findViewById(R.id.drawer_layout);
        mMasterDbHelper = new MasterDbHelper(getApplicationContext(), "Exercise_Database");



        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        return nav(menuItem);
                    }
                });

        arrayList = new ArrayList<>();


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



        add = findViewById(R.id.add);
        add.show();
        add.setClickable(true);



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        save = (FloatingActionButton) dialog.findViewById(R.id.save);
        save.show();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMasterDbHelper = new MasterDbHelper(getApplicationContext(), "Exercise_Database");
                String new_name_to_add = new_name.getText().toString().replaceAll(" ", "_");
                if(!new_name_to_add.matches("")) {
                    AddData(new_name_to_add.replaceAll(" ", "_"));
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

    public void AddData(String newEntry){
        boolean insert = mMasterDbHelper.addData(newEntry);
        if(insert){
            toastM("Success");

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
        //used to populate the listview
        //ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
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
