package com.cg.watbalance;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cg.watbalance.data.BalanceData;
import com.cg.watbalance.preferences.FileManager;
import com.cg.watbalance.preferences.Preferences;
import com.cg.watbalance.service.Service;

public class balanceScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RECEIVER", "NEW BALANCE DATA");
            updateView((BalanceData) intent.getSerializableExtra("myBalData"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(findViewById(R.id.rootView), "Refreshing...", Snackbar.LENGTH_LONG).show();
                sendBroadcast(new Intent(getApplicationContext(), Service.class));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.Name);
        TextView id = (TextView) navigationView.getHeaderView(0).findViewById(R.id.IDText);

        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        name.setText(myPreferences.getString("Name", "User"));
        id.setText("ID# " + myPreferences.getString("IDNum", "Unknown"));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        FileManager myFM = new FileManager(this);
        myFM.openFileInput("myBalData");
        BalanceData myBalData = (BalanceData) myFM.readData();
        myFM.closeFileInput();

        updateView(myBalData);
        IntentFilter myFilter = new IntentFilter("com.cg.WatBalance.newData");
        registerReceiver(myReceiver, myFilter);

        Snackbar.make(findViewById(R.id.rootView), "Refreshing...", Snackbar.LENGTH_LONG).show();
        sendBroadcast(new Intent(getApplicationContext(), Service.class));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final int id = item.getItemId();

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                switchScreen(id);
                drawer.setDrawerListener(null);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        drawer.closeDrawers();
        return true;
    }

    public void switchScreen(int id) {
        if (id == R.id.nav_transactions) {
            Intent myIntent = new Intent(this, transactionScreen.class);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_outlets) {
            Intent myIntent = new Intent(this, outletScreen.class);
            startActivity(myIntent);
            finish();
        } else if (id == R.id.nav_settings) {
            Intent myIntent = new Intent(this, Preferences.class);
            startActivity(myIntent);
        } else if (id == R.id.nav_about) {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.about_dialog);
            dialog.setTitle("About");
            dialog.show();
        }
    }

    public void updateView(BalanceData myBalData) {
        TextView total = (TextView) findViewById(R.id.Total);
        total.setText(myBalData.getTotalString());

        TextView mp = (TextView) findViewById(R.id.mealPlanData);
        mp.setText(myBalData.getMPString());

        TextView fd = (TextView) findViewById(R.id.flexDollarsData);
        fd.setText(myBalData.getFDString());

        TextView other = (TextView) findViewById(R.id.otherData);
        other.setText(myBalData.getOtherString());

        TextView dailyTot = (TextView) findViewById(R.id.dayBalance);
        TextView todaySpent = (TextView) findViewById(R.id.todaySpent);
        TextView todayLeft = (TextView) findViewById(R.id.todayLeft);

        TextView updateText = (TextView) findViewById(R.id.updateText);
        updateText.setText(myBalData.getDateString());

        RelativeLayout todaySpentRow = (RelativeLayout) findViewById(R.id.spentTodayRow);
        RelativeLayout todayLeftRow = (RelativeLayout) findViewById(R.id.todayLeftRow);
        RelativeLayout datePassed = (RelativeLayout) findViewById(R.id.datePassed);


        if (myBalData.getDatePassed()) {
            dailyTot.setVisibility(View.GONE);
            todaySpentRow.setVisibility(View.GONE);
            todayLeftRow.setVisibility(View.GONE);
            datePassed.setVisibility(View.VISIBLE);
        } else {
            dailyTot.setVisibility(View.VISIBLE);
            todaySpentRow.setVisibility(View.VISIBLE);
            todayLeftRow.setVisibility(View.VISIBLE);
            datePassed.setVisibility(View.GONE);

            dailyTot.setText(myBalData.getDailyBalanceString());
            todaySpent.setText(myBalData.getTodaySpentString());
            todayLeft.setText(myBalData.getTodayLeftString());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }
}
