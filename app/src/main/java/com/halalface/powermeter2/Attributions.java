package com.halalface.powermeter2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
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
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Attr;

public class Attributions extends AppCompatActivity {
    DrawerLayout drawerLayout;
    TextView mpandroid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attributions);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>About</font>"));

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


        mpandroid = findViewById(R.id.mpandroid);
        String linkText1 = "PowerMeter is an app designed to track the relative power exerted over time when exercising. " +
                "Currently only exercises that are quantifiable by weight and sets are supported. " +
                "This app is open sourced and can be found at <a href='https://github.com/Halal-Face/PowerMeter'>github.com/Halal-Face</a> <br><br>"+
                "Graphs are created via the  <a href='https://github.com/PhilJay/MPAndroidChart'>MPAndroidChart</a> library.<br><br>"+
                "Expandable listview code from <a href='https://github.com/bij-ace/dynamic-edittext-in-expandable-listview-android'> bij-ace </a><br><br>"+
                "Material Calendar created via the <a href='https://github.com/prolificinteractive/material-calendarview'>material-calendarview </a> library.<br><br>"
                ;
        mpandroid.setText(setHtml(linkText1));
        mpandroid.setMovementMethod(LinkMovementMethod.getInstance());
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
    @SuppressWarnings("deprecation")
    public Spanned setHtml(String html){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
