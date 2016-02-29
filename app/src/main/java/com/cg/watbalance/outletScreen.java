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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cg.watbalance.data.MenuListAdapter;
import com.cg.watbalance.data.OutletData;
import com.cg.watbalance.preferences.FileManager;
import com.cg.watbalance.preferences.Preferences;
import com.cg.watbalance.service.Service;

public class outletScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView lunchListView;
    ListView dinnerListView;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("RECEIVER", "NEW OUTLET DATA");
            updateView((OutletData) intent.getSerializableExtra("myOutletData"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_screen);
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

        lunchListView = (ListView) findViewById(R.id.lunch);
        dinnerListView = (ListView) findViewById(R.id.dinner);
    }

    @Override
    public void onResume() {
        super.onResume();
        FileManager myFM = new FileManager(this);
        myFM.openFileInput("myOutletData");
        OutletData myOutletData = (OutletData) myFM.readData();
        myFM.closeFileInput();

        updateView(myOutletData);
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
        } else if (id == R.id.nav_transactions) {
            Intent myIntent = new Intent(this, transactionScreen.class);
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

    public void updateView(OutletData myOutletData) {
        final OutletData.Menu REVMenu = myOutletData.findMenu(7);

        if (REVMenu != null) {
            TextView outletName = (TextView) findViewById(R.id.outletName1);
            outletName.setText(REVMenu.getOutletName());

            TextView outletStatus = (TextView) findViewById(R.id.outletStatus1);
            boolean isREVOpen = REVMenu.getOpen();
            if (isREVOpen) {
                outletStatus.setText("Open Now");
            } else {
                outletStatus.setText("Closed");
                outletStatus.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            }

            LinearLayout REV = (LinearLayout) findViewById(R.id.outlet1);
            REV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lunchListView.setAdapter(new MenuListAdapter(getApplicationContext(), REVMenu.getLunch().getFoodList()));
                    dinnerListView.setAdapter(new MenuListAdapter(getApplicationContext(), REVMenu.getDinner().getFoodList()));

                    lunchListView.setEmptyView(findViewById(R.id.empty_lunch));
                    dinnerListView.setEmptyView(findViewById(R.id.empty_dinner));
                }
            });

        }

        final OutletData.Menu V1Menu = myOutletData.findMenu(5);

        if (V1Menu != null) {
            TextView outletName = (TextView) findViewById(R.id.outletName2);
            outletName.setText(V1Menu.getOutletName());

            TextView outletStatus = (TextView) findViewById(R.id.outletStatus2);
            boolean isV1Open = V1Menu.getOpen();
            if (isV1Open) {
                outletStatus.setText("Open Now");
            } else {
                outletStatus.setText("Closed");
                outletStatus.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
            }

            LinearLayout V1 = (LinearLayout) findViewById(R.id.outlet2);

            V1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lunchListView.setAdapter(new MenuListAdapter(getApplicationContext(), V1Menu.getLunch().getFoodList()));
                    dinnerListView.setAdapter(new MenuListAdapter(getApplicationContext(), V1Menu.getDinner().getFoodList()));

                    lunchListView.setEmptyView(findViewById(R.id.empty_lunch));
                    dinnerListView.setEmptyView(findViewById(R.id.empty_dinner));
                }
            });

            lunchListView.setAdapter(new MenuListAdapter(this, V1Menu.getLunch().getFoodList()));
            dinnerListView.setAdapter(new MenuListAdapter(this, V1Menu.getDinner().getFoodList()));

            lunchListView.setEmptyView(findViewById(R.id.empty_lunch));
            dinnerListView.setEmptyView(findViewById(R.id.empty_dinner));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }
}
