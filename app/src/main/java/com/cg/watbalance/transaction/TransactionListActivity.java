package com.cg.watbalance.transaction;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.cg.watbalance.R;
import com.cg.watbalance.data.WatCardData;
import com.cg.watbalance.preferences.FileManager;

public class TransactionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        FileManager myFM = new FileManager(this);
        myFM.openFileInput("lastData");
        WatCardData myData = myFM.readData();
        myFM.closeFileInput();

        ListView tranListView = (ListView) findViewById(R.id.tranListView);
        tranListView.setAdapter(new TransactionListAdapter(getApplicationContext(), myData.getTransHistory()));
    }
}
