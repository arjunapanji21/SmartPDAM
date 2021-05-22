package com.rozikmaliki.smartpdam;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // variables
    LineChart lineChart;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView txtAir, txtBiaya;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference root =  database.getReference();

    private String userID, currMonth, bulan;
    float bln1,bln2,bln3,bln4,bln5,bln6,bln7,bln8,bln9,bln10,bln11,bln12;
    float air1,air2,air3,air4,air5,air6,air7,air8,air9,air10,air11,air12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get component id
        txtAir = findViewById(R.id.air);
        txtBiaya = findViewById(R.id.biaya);

        if(user == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // get user id
        userID = user.getUid();

        // get current month
        DateFormat dateFormat = new SimpleDateFormat("M");
        Date date = new Date();
        currMonth = dateFormat.format(date);

        pemakaianBulanIni();

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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }
        return false;
    }

    public void pemakaianBulanIni() {
        // get database references
        DatabaseReference air =  root.child("users").child(userID).child("bulan").child(currMonth).child("air");
        DatabaseReference biaya =  root.child("users").child(userID).child("bulan").child(currMonth).child("biaya");

        air.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                txtAir.setText(value+" L");
                Toast.makeText(MainActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
            }
        });

        biaya.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                txtBiaya.setText("Rp. "+value);
                //Toast.makeText(MainActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getNavigationView() {
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
        userID = user.getUid();

        DatabaseReference userName = root.child("users").child(userID).child("nama");
        userName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                navUsername.setText("Hi! "+value);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getLineChart() {
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
    }

    private ArrayList<Entry> lineChartDataSet(){
        ArrayList<Entry> dataSet = new ArrayList<>();
        dataSet.add(new Entry(1,0));
        dataSet.add(new Entry(2,0));
        dataSet.add(new Entry(3,0));
        dataSet.add(new Entry(4,0));
        dataSet.add(new Entry(5,500));
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