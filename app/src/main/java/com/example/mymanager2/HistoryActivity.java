package com.example.mymanager2;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView bills_list = findViewById(R.id.all_bills);
        bills_list.setLayoutManager(new LinearLayoutManager(this));

        MyDBHelper db = new MyDBHelper(this);
        ArrayList<bills> billsArrayList = db.fetchBills();

        RecyclerBillsAdapter adapter = new RecyclerBillsAdapter(this, billsArrayList);
        adapter.updateBillList();
        bills_list.setHasFixedSize(true);
        bills_list.setAdapter(adapter);

    }
}