package com.rozikmaliki.smartpdam;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.github.mikephil.charting.components.Legend;
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
    ProgressDialog progressDialog;

    TextView txtAir, txtBiaya;
    ListView listView;
    List<DataAir> dataAirList;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference root =  database.getReference();

    private String userID, currMonth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize
        progressDialog = new ProgressDialog(MainActivity.this);
        // show progress dialog
        progressDialog.show();
        // set content view
        progressDialog.setContentView(R.layout.progress_dialog);
        // set transparant background
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // get component id
        txtAir = findViewById(R.id.air);
        txtBiaya = findViewById(R.id.biaya);
        listView = findViewById(R.id.listView);

        dataAirList = new ArrayList<>();

        if(user == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{
            // get user id
            userID = user.getUid();
            // get user data
            DatabaseReference dataUser = root.child("users").child(userID).child("data");

            dataUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    dataAirList.clear();
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        DataAir dataAir = dataSnapshot.getValue(DataAir.class);
                        dataAirList.add(dataAir);
                    }
                    ListAdapter adapter = new ListAdapter(MainActivity.this, dataAirList);
                    listView.setAdapter(adapter);
                    // dismis progress dialog
                    progressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
                    // dismis progress dialog
                    progressDialog.dismiss();
                }
            });

            // get current month
            DateFormat dateFormat = new SimpleDateFormat("M");
            Date date = new Date();
            currMonth = dateFormat.format(date);

            pemakaianBulanIni();
            getLineChart();
            getNavigationView();
        }
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
        DatabaseReference air =  root.child("users").child(userID).child("data").child(currMonth).child("air");
        DatabaseReference biaya =  root.child("users").child(userID).child("data").child(currMonth).child("biaya");

        air.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                txtAir.setText(value+" L");
                //Toast.makeText(MainActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
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

    public void getLineChart(){
        DatabaseReference chartData = root.child("users").child(userID).child("chart");
        chartData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ArrayList<Entry> dataSet = new ArrayList<>();
                if(snapshot.hasChildren()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        DataChart dataChart = dataSnapshot.getValue(DataChart.class);
                        dataSet.add(new Entry(Float.parseFloat(dataChart.getX()), Float.parseFloat(dataChart.getY())));
                    }
                    showChart(dataSet);
                }else{
                    lineChart.clear();
                    lineChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showChart(ArrayList<Entry> dataSet) {
        lineChart = findViewById(R.id.lineChart);
        LineDataSet lineDataSet = new LineDataSet(dataSet,"Riwayat Pemakaian Air Tahun Ini");
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

        lineChart.getLegend().setEnabled(false);
    }
}