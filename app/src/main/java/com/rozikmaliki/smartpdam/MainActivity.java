package com.rozikmaliki.smartpdam;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // variables
    LineChart lineChart;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView userName;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLineChart();
        getNavigationView();
    }

    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                recreate();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Berhasil Logout!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return false;
    }

    public NavigationView getNavigationView() {
        // navigation bar
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.userName);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            userEmail = user.getEmail();
        }
        navUsername.setText("Hi! "+userEmail);
        return navigationView;
    }

    public LineChart getLineChart() {
        lineChart = findViewById(R.id.lineChart);
        LineDataSet lineDataSet = new LineDataSet(lineChartDataSet(),"Riwayat Pemakaian Air Tahun Ini");

        ArrayList<ILineDataSet> iLineDataSet = new ArrayList<>();
        iLineDataSet.add(lineDataSet);

        LineData lineData = new LineData(iLineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();

        // if no data available
        lineChart.setNoDataText("Data Not Available!");

        // line dataset customization
        lineDataSet.setColor(Color.rgb(200, 0, 0));
        lineDataSet.setCircleColors(Color.rgb(200, 0, 0));
        lineDataSet.setLineWidth(2);

        // line chart customization
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);

        return lineChart;
    }

    private ArrayList<Entry> lineChartDataSet(){
        ArrayList<Entry> dataSet = new ArrayList<>();
        dataSet.add(new Entry(1,320));
        dataSet.add(new Entry(2,210));
        dataSet.add(new Entry(3,560));
        dataSet.add(new Entry(4,1204));
        dataSet.add(new Entry(5,342));
        dataSet.add(new Entry(6,0));
        dataSet.add(new Entry(7,0));
        dataSet.add(new Entry(8,0));
        dataSet.add(new Entry(9,0));
        dataSet.add(new Entry(10,0));
        dataSet.add(new Entry(11,0));
        dataSet.add(new Entry(12,0));
        return dataSet;
    }
}