package com.example.mymanager2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    MyDBHelper db=new MyDBHelper(this);

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
        Intent consumer=new Intent(this,AccountActivity.class);


        Button cashin = findViewById(R.id.btnCashIn);
        Button cashout = findViewById(R.id.btnCashOut);
        TextView result = findViewById(R.id.displayText);
        String amount=result.getText().toString();


        Button account=findViewById(R.id.btnAccount);
        account.setOnClickListener(v -> {
            startActivity(consumer);
        });

        cashin.setOnClickListener(v -> {
            if (CheckForInt(amount))  {
                int status=0;
                showListDialog(status,amount);



            }

        });

        cashout.setOnClickListener(v -> {
            if (CheckForInt(amount)) {
                int status=1;
                showListDialog(status,amount);

            }});










    }

    private void showListDialog(int status, String amount) {
        
        // Data for the ListView
        ArrayList<consumer> consumers =db.fecthConsumer();
        String[] items = new String[consumers.size()];
        for (int i = 0; i < consumers.size(); i++) {
            items[i] = consumers.get(i).name;
        }

        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.consumer_list_dialog, null);

        // Find the ListView in the custom layout
        ListView listView = dialogView.findViewById(R.id.dialogListView);

        // Set an adapter for the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adapter);

        // Set up the AlertDialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Select an Item")
                .setPositiveButton("Add New Consumer", (dialog1, which) -> {
                    // Handle positive button click
                    dialog1.dismiss();
                    addConsumer();

                })
                .setView(dialogView) // Use the custom view
                .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
                .create();

        // Handle item clicks in the ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            int consumerId = consumers.get(position).id;
            db.addBill(Double.parseDouble(amount),consumerId,status);
            Toast.makeText(this, "Bill added successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Close the dialog
        });

        // Show the dialog
        dialog.show();


    }

    public void addConsumer() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_consumer);
        dialog.show();
        EditText name = dialog.findViewById(R.id.consumer_name);
        EditText number = dialog.findViewById(R.id.consumer_number);
        Button add = dialog.findViewById(R.id.add_consumer);
        add.setOnClickListener(v -> {
            String name1 = name.getText().toString();
            String number1 = number.getText().toString();
            db.addConsumer(name1, number1);
            Toast.makeText(this,name1+" added successfully",Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }


    public boolean CheckForInt(String result) {
        if (result == null || result.isEmpty()) {


            return false; // Return false for null or empty strings
        }

        try {
            Double.parseDouble(result); // Try parsing as a double
            return true; // If successful, return true
        } catch (NumberFormatException e) {
            return false; // If parsing fails, return false
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