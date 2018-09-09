package com.halalface.powermeter2;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class GraphData extends AppCompatActivity {

    DrawerLayout drawerLayout;
    String TABLE_NAME;
    PowerDbHelper pPowerDbHelper;
    BarChart barChart;
    ArrayList<Integer> xData;
    ArrayList<Integer> yData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        actionbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>Your Data... But Graphed!</font>"));

        drawerLayout = findViewById(R.id.drawer_layout);




        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        return nav(menuItem);
                    }
                });

        Intent receiveIntent = getIntent();
        TABLE_NAME = receiveIntent.getStringExtra("TABLE_NAME");
        pPowerDbHelper = new PowerDbHelper(GraphData.this, TABLE_NAME);
        barChart = findViewById(R.id.barChart);

        xData = pPowerDbHelper.getXData();
        yData = pPowerDbHelper.getYData();
        addData();

    }

    private void addData(){

        List<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < yData.size(); i++) {
            entries.add(new BarEntry(xData.get(i), yData.get(i)));
        }
        System.out.println("YDATA: "+yData.size());
        System.out.println("xDATA: "+xData.size());
        BarDataSet dataSet = new BarDataSet(entries, "Relative Power");
        dataSet.setColor(Color.parseColor("#FF9683"));
        dataSet.setValueTextColor(Color.parseColor("#FF775F"));
        dataSet.setDrawValues(true);
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.setTouchEnabled(false);
        barChart.invalidate();
        Description description = new Description();

        description.setText("");
        barChart.setDescription(description);

        YAxis yLeft = barChart.getAxisLeft();
        YAxis yRight = barChart.getAxisRight();
        XAxis xAxis = barChart.getXAxis();

        yLeft.setDrawLabels(false);
        yLeft.setDrawGridLines(false);
        yLeft.setDrawZeroLine(false);
        yLeft.setDrawAxisLine(false);
        yLeft.setAxisMinimum(0f);

        yRight.setDrawLabels(false);
        yRight.setDrawGridLines(false);
        yRight.setDrawZeroLine(false);
        yRight.setDrawAxisLine(false);

        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);


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
