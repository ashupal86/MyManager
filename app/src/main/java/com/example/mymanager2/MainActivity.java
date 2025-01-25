package com.example.mymanager2;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {

    MyDBHelper db=new MyDBHelper(this);
    private TextView displayText, resultDisplay;


    private boolean isNewCalculation = true;


    private ArrayList<String> historyList;
    private SharedPreferences history;
    private LinearLayout historyText;
    private static final String HISTORY_PREF_KEY = "calculation_history";
    private static final int MAX_HISTORY_DISPLAY = 6;

    public StringBuilder expression = new StringBuilder();



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


        // Initialize SharedPreferences and history list
        history = getSharedPreferences("history", MODE_PRIVATE);
        historyText = findViewById(R.id.historyContainer);

        // Load existing history
        loadHistoryFromPreferences();




//        TODO: Calculator Logic
        displayText = findViewById(R.id.displayText);
        resultDisplay = findViewById(R.id.resultdisplay);

        // Number buttons setup
//        int[] numberButtonIds = {
//                R.id.btn0, R.id.btn00, R.id.btn1, R.id.btn2, R.id.btn3,
//                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
//        };
//        for (int id : numberButtonIds) {
//            findViewById(id).setOnClickListener(v -> {
//                onNumberClick(((Button) v).getText().toString());
//            });
//        }



        // Operator buttons setup
        int[] operatorButtonIds = {
                R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide, R.id.btnPercent
        };

        for (int id : operatorButtonIds) {
            findViewById(id).setOnClickListener(v -> {
                onOperatorClick(((Button)v).getText().toString());
            });
        }

        // Special buttons
        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            calculateFinalResult();
        });


        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            backspace();
        });






//        insertSampleData();
        Intent consumer=new Intent(this,AccountActivity.class);
            Intent intent=new Intent(this,HistoryActivity.class);


        Button cashin = findViewById(R.id.btnCashIn);
        Button cashout = findViewById(R.id.btnCashOut);
        String amount=resultDisplay.getText().toString();

        Button History=findViewById(R.id.btnHistory);
        History.setOnClickListener(v -> {
            startActivity(intent);
        });

        Button account=findViewById(R.id.btnAccount);
        account.setOnClickListener(v -> {
            startActivity(consumer);
        });

        cashin.setOnClickListener(v -> {
            String resultamount=resultDisplay.getText().toString();

            if (CheckForInt(resultamount) && Double.parseDouble(resultamount)!=0.0)  {
                int status=0;
                showListDialog(status,resultamount);

            }else{
                Toast.makeText(this,"Invalid Amount",Toast.LENGTH_SHORT).show();
            }

        });

        cashout.setOnClickListener(v -> {
            String resultamount=resultDisplay.getText().toString();

            if (CheckForInt(resultamount) && Double.parseDouble(resultamount)!=0.0) {
                int status=1;
                showListDialog(status,resultamount);
            }else {
                Toast.makeText(this,"Invalid Amount",Toast.LENGTH_SHORT).show();
            }

        });










    }



    public void calculateFinalResult() {
        String expressions = displayText.getText().toString();
        try {
            double result = Expression.evaluate(expressions);
            if(!resultDisplay.getText().toString().isEmpty()){

                saveCalculationToHistory(expressions + " = " + resultDisplay.getText().toString());
            }

            // Check if the result is an integer (no decimal part)
            if (result == (int) result) {
                // Display the result as an integer
                displayText.setText(String.format("%d", (int) result));
                resultDisplay.setText(String.format("%d", (int) result)); // Optional, if you want to set the result display too
            } else {
                // Display the result with 2 decimal places
                displayText.setText(String.format("%.2f", result));
                resultDisplay.setText(String.format("%.2f", result)); // Optional, if you want to set the result display too
            }

            isNewCalculation = true;

            expression.setLength(0);
            expression.append(String.format("%.2f", result)); // Keep the result as 2 decimal places for calculation purposes
            resultDisplay.setText("");
        } catch (Exception e) {
            resultDisplay.setText("Error");
        }
    }


    private void calculateAndUpdateResult() {
        String expressions = displayText.getText().toString();
        try {
            if (!expressions.isEmpty() && !isNewCalculation) {
                double result = Expression.evaluate(expressions);
                resultDisplay.setText(String.valueOf(result));

            } else {
                resultDisplay.setText(""); // Clear the result if no expression is present
            }
        } catch (Exception e) {

          // Show error if calculation fails
        }
    }



    private void backspace() {
        String currentText = displayText.getText().toString();

        // Check if the current text is not empty and we are not in a new calculation
        if (!currentText.isEmpty() && !isNewCalculation) {
            // Remove the last character from both display and expression
            displayText.setText(currentText.substring(0, currentText.length() - 1));
            expression.setLength(expression.length() - 1);  // Remove the last character from the expression

            calculateAndUpdateResult();  // Recalculate after backspace
        } else if (isNewCalculation) {
            // In case of a new calculation, reset the display and expression
            if (!currentText.isEmpty()) {
                displayText.setText(currentText.substring(0, currentText.length() - 1));
                expression.setLength(expression.length() - 1);  // Update expression to reflect backspace
            }

            calculateAndUpdateResult();  // Recalculate after backspace
        }
    }


    private void onOperatorClick(String operator) {
        String currentText = displayText.getText().toString();
        if (!currentText.isEmpty() && Character.isDigit(currentText.charAt(currentText.length() - 1))) {
            displayText.append(operator);
            expression.append(operator);
            isNewCalculation=false;
//            Toast.makeText(this,operator,Toast.LENGTH_SHORT).show();
        }
    }



    public void onButtonClick(View view) {
        Button button = (Button) view;
        String input = button.getText().toString();
//        Toast.makeText(this,input,Toast.LENGTH_SHORT).show();

        // Handle clear button
        if (input.equals("C")) {
            expression.setLength(0); // Clear the expression
            displayText.setText("");  // Clear the display text
            resultDisplay.setText(""); // Clear the result display
            return;
        }

        // Check if input is an operator
        String operators = "+-*/%";
        if (operators.contains(input)) {
            // Avoid consecutive operators
            if (expression.length() == 0 || operators.contains(String.valueOf(expression.charAt(expression.length() - 1)))) {
                return; // Ignore the new operator
            }
        }

        // Append input to the expression
        expression.append(input);
        displayText.setText(expression.toString()); // Update the display with the current expression

        // Calculate and display the result
        try {
            if(!isNewCalculation){
                double result = Expression.evaluate(expression.toString()); // Use your custom Expression class
                if(result==(int)result){
                    resultDisplay.setText(String.valueOf((int) result));
                }else {

                resultDisplay.setText(String.format("%.2f",result));
                }
//                isNewCalculation=true;
            }
        } catch (Exception e) {
            resultDisplay.setText("calculation Error"); // Handle invalid expression
        }
    }


    private void loadHistoryFromPreferences() {
        // Load history from SharedPreferences
        String savedHistory = history.getString(HISTORY_PREF_KEY, "");
        historyList = new ArrayList<>(Arrays.asList(savedHistory.split("\\|")));

        // Remove empty strings
        historyList.removeAll(Collections.singleton(""));

        // Display history
        updateHistoryDisplay();
    }

    private void saveCalculationToHistory(String calculation) {
        // Add to history list
        historyList.add(calculation);

        // Limit list size if needed
        if (historyList.size() > 20) {
            historyList.remove(0);
        }

        // Save to SharedPreferences
        String historyString = TextUtils.join("|", historyList);
        history.edit().putString(HISTORY_PREF_KEY, historyString).apply();

        // Update display
        updateHistoryDisplay();
    }

    private void updateHistoryDisplay() {
        // Clear previous history
        historyText.removeAllViews();

        if (historyList == null || historyList.isEmpty()) {
            return;
        }

        // Determine start index to show last 5 items
        int startIndex = Math.max(0, historyList.size() - MAX_HISTORY_DISPLAY);

        ArrayList<TextView> historyTextViews = new ArrayList<>();

        // Create TextViews for recent calculations in reverse order
        for (int i = historyList.size() - 1; i >= startIndex; i--) {
            TextView historyItem = new TextView(this);
            historyItem.setGravity(Gravity.END);
            historyItem.setText(historyList.get(i));
            historyItem.setTextSize(16);
            historyItem.setPadding(8, 8, 8, 8);
            historyTextViews.add(historyItem);
        }

        Collections.reverse(historyTextViews);
        for (TextView historyItem : historyTextViews) {
            historyText.addView(historyItem);
        }


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
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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