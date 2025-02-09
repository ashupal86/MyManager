package com.example.mymanager2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {
    MyDBHelper db;

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
        db=new MyDBHelper(AccountActivity.this);
        findViewById(R.id.addnewconsumer).setOnClickListener(v -> {
            addnewConsumer();
        });

        RecyclerView consumer_list = findViewById(R.id.all_consumer);
        consumer_list.setLayoutManager(new LinearLayoutManager(this));

        MyDBHelper db = new MyDBHelper(this);
        ArrayList<consumer> consumerArrayList = db.fecthConsumer();

        RecyclerConsumerAdapter adapter = new RecyclerConsumerAdapter(this, consumerArrayList);
        adapter.updateConsumerList();
        consumer_list.setHasFixedSize(true);
        consumer_list.setAdapter(adapter);



    }
    public void addnewConsumer() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_consumer);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
        EditText name = dialog.findViewById(R.id.consumer_name);
        EditText number = dialog.findViewById(R.id.consumer_number);
        MaterialButton add = dialog.findViewById(R.id.add_consumer);
        add.setOnClickListener(v -> {
            String name1 = name.getText().toString();
            String number1 = number.getText().toString();
            db.addConsumer(name1, number1);
            Toast.makeText(this,name1+" added successfully",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish();
            Intent intent=new Intent(this,AccountActivity.class);
            startActivity(intent);

        });
    }

}