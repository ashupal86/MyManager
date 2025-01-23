package com.example.mymanager2;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ListAllConsumers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_all_consumers);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        RecyclerView consumerList = findViewById(R.id.consumer_list);
        consumerList.setLayoutManager(new LinearLayoutManager(this));

        MyDBHelper db = new MyDBHelper(this);
        ArrayList<consumer> consumerlist = db.fecthConsumer();
        RecyclerConsumerAdapter adapter = new RecyclerConsumerAdapter(this, consumerlist);
        adapter.updateConsumerList();
        consumerList.setAdapter(adapter);
    }
}