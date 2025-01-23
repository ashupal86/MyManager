package com.example.mymanager2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView allbills = findViewById(R.id.all_bills);
        allbills.setLayoutManager(new LinearLayoutManager(this));

        MyDBHelper db = new MyDBHelper(this);
        ArrayList<bills> billslist = db.fetchBills();

        RecyclerBillsAdapter adapter = new RecyclerBillsAdapter(this, billslist);
        adapter.updateBillList();
        allbills.setHasFixedSize(true);
        allbills.setAdapter(adapter);

        Intent ListAllConsumer=new Intent(this,ListAllConsumers.class);
        AppCompatButton listAllConsumer=findViewById(R.id.listAllConsumer);
        listAllConsumer.setOnClickListener(v -> startActivity(ListAllConsumer));


    }
}