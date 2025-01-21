package com.example.mymanager2;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        insertSampleData();


        FloatingActionButton addConsumerButton = findViewById(R.id.add_consumer);
        addConsumerButton.setOnClickListener(v -> {
            Dialog addConsumerDialog = new Dialog(this);
            addConsumerDialog.setContentView(R.layout.add_consumer);
            addConsumerDialog.show();
        });


        RecyclerView consumerList = findViewById(R.id.consumer_list);
        consumerList.setLayoutManager(new LinearLayoutManager(this));

        MyDBHelper db = new MyDBHelper(this);
        ArrayList<consumer> consumerlist = db.fecthConsumer();
        RecyclerConsumerAdapter adapter = new RecyclerConsumerAdapter(this, consumerlist);
        consumerList.setAdapter(adapter);

        for (int i = 0; i < consumerlist.size(); i++) {
            Log.d("consumer", "name" + consumerlist.get(i).name);
        }

    }


    public void insertSampleData() {

        MyDBHelper db = new MyDBHelper(this);


        // Sample data for 10 consumers
        String[] names = {
                "John Doe", "Jane Smith", "Alice Johnson", "Bob Brown", "Charlie Davis",
                "David Miller", "Eva Wilson", "Frank Moore", "Grace Taylor", "Hank Clark"
        };

        String[] phones = {
                "555-1234", "555-5678", "555-8765", "555-4321", "555-2468",
                "555-1357", "555-9876", "555-6543", "555-3210", "555-4321"
        };

        for (int i = 0; i < names.length; i++) {
            // Insert each consumer into the database
            db.addConsumer(names[i], phones[i]);
        }
    }
}