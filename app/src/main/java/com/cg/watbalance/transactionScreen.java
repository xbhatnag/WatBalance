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
import android.widget.ListView;
import android.widget.TextView;

import com.cg.watbalance.data.transaction.TransactionData;
import com.cg.watbalance.data.transaction.TransactionListAdapter;
import com.cg.watbalance.preferences.FileManager;
import com.cg.watbalance.preferences.Preferences;
import com.cg.watbalance.service.Service;

import org.joda.time.DateTime;

import lecho.lib.hellocharts.model.SelectedValue;
import lecho.lib.hellocharts.view.LineChartView;

public class transactionScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RECEIVER", "NEW TRANSACTION DATA");
            updateView((TransactionData) intent.getSerializableExtra("myTransData"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Refreshing...", Snackbar.LENGTH_LONG).show();
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
    public void onResume() {
        super.onResume();
        FileManager myFM = new FileManager(this);
        myFM.openFileInput("myTransData");
        TransactionData myTransData = (TransactionData) myFM.readData();
        myFM.closeFileInput();

        updateView(myTransData);
        IntentFilter myFilter = new IntentFilter("com.cg.WatBalance.newData");
        registerReceiver(myReceiver, myFilter);
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
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void switchScreen(int id) {
        if (id == R.id.nav_balance) {
            Intent myIntent = new Intent(this, balanceScreen.class);
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
            dialog.setTitle("WatBalance");
            dialog.show();
        }
    }

    public void updateView(TransactionData myTransData) {
        LineChartView transChart = (LineChartView) findViewById(R.id.transChart);
        transChart.setLineChartData(myTransData.makeTransChartData());
        transChart.setZoomEnabled(false);
        transChart.setValueSelectionEnabled(true);

        SelectedValue sv = new SelectedValue(0, 0, SelectedValue.SelectedValueType.NONE);
        transChart.selectValue(sv);

        TextView month = (TextView) findViewById(R.id.month);
        month.setText(DateTime.now().monthOfYear().getAsText());

        ListView transList = (ListView) findViewById(R.id.transList);
        transList.setAdapter(new TransactionListAdapter(getApplicationContext(), myTransData.getTransList()));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }
}
