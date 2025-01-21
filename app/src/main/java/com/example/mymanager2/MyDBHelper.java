package com.example.mymanager2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class MyDBHelper extends SQLiteOpenHelper {
    // Database Information
    private static final String DATABASE_NAME = "cashflow.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_CONSUMER = "Consumer";
    private static final String TABLE_BILLS = "Bills";

    // Consumer Table Columns
    private static final String COLUMN_CONSUMER_ID = "id";
    private static final String COLUMN_CONSUMER_NAME = "name";
    private static final String COLUMN_CONSUMER_PHONE = "phone";
    private static final String COLUMN_CONSUMER_TIMESTAMP = "timestamp";
    private static final String COLUMN_CONSUMER_TOTAL_AMOUNT = "total_amount";

    // Bills Table Columns
    private static final String COLUMN_BILLS_ID = "id";
    private static final String COLUMN_BILLS_AMOUNT = "amount";
    private static final String COLUMN_BILLS_TIMESTAMP = "timestamp";
    private static final String COLUMN_BILLS_CONSUMER_ID = "consumer_id";
    private static final String COLUMN_BILLS_STATUS = "status"; // 0: cashout, 1: cashin

    public MyDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Consumer table
        String CREATE_TABLE_CONSUMER = "CREATE TABLE IF NOT EXISTS " + TABLE_CONSUMER + " ("
                + COLUMN_CONSUMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_CONSUMER_NAME + " TEXT NOT NULL, "
                + COLUMN_CONSUMER_PHONE + " TEXT UNIQUE NOT NULL, "
                + COLUMN_CONSUMER_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COLUMN_CONSUMER_TOTAL_AMOUNT + " REAL DEFAULT 0.00)";
        db.execSQL(CREATE_TABLE_CONSUMER);

        // Create Bills table
        String CREATE_TABLE_BILLS = "CREATE TABLE IF NOT EXISTS " + TABLE_BILLS + " ("
                + COLUMN_BILLS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_BILLS_AMOUNT + " REAL NOT NULL, "
                + COLUMN_BILLS_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COLUMN_BILLS_CONSUMER_ID + " INTEGER NOT NULL, "
                + COLUMN_BILLS_STATUS + " INTEGER NOT NULL CHECK (" + COLUMN_BILLS_STATUS + " IN (0, 1)), "
                + "FOREIGN KEY (" + COLUMN_BILLS_CONSUMER_ID + ") REFERENCES " + TABLE_CONSUMER + "(" + COLUMN_CONSUMER_ID + ") ON DELETE CASCADE)";
        db.execSQL(CREATE_TABLE_BILLS);

        // Create Trigger to update Consumer.total_amount
        String CREATE_TRIGGER_UPDATE_TOTAL_AMOUNT = "CREATE TRIGGER update_total_amount "
                + "AFTER INSERT ON " + TABLE_BILLS + " "
                + "BEGIN "
                + "   UPDATE " + TABLE_CONSUMER + " "
                + "   SET " + COLUMN_CONSUMER_TOTAL_AMOUNT + " = "
                + "       CASE WHEN NEW." + COLUMN_BILLS_STATUS + " = 0 THEN " // Cashout
                + "           " + COLUMN_CONSUMER_TOTAL_AMOUNT + " + NEW." + COLUMN_BILLS_AMOUNT
                + "       ELSE " // Cashin
                + "           " + COLUMN_CONSUMER_TOTAL_AMOUNT + " - NEW." + COLUMN_BILLS_AMOUNT
                + "       END "
                + "   WHERE " + COLUMN_CONSUMER_ID + " = NEW." + COLUMN_BILLS_CONSUMER_ID + "; "
                + "END;";
        db.execSQL(CREATE_TRIGGER_UPDATE_TOTAL_AMOUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BILLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONSUMER);
        onCreate(db);
    }

    // Method to insert a new consumer
    public long addConsumer(String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONSUMER_NAME, name);
        values.put(COLUMN_CONSUMER_PHONE, phone);
        return db.insert(TABLE_CONSUMER, null, values);
    }

    // Method to insert a new bill
    public long addBill(double amount, int consumerId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BILLS_AMOUNT, amount);
        values.put(COLUMN_BILLS_CONSUMER_ID, consumerId);
        values.put(COLUMN_BILLS_STATUS, status); // 0 for cashout, 1 for cashin
        return db.insert(TABLE_BILLS, null, values);
    }


   public ArrayList<consumer> fecthConsumer(){
        SQLiteDatabase db=this.getReadableDatabase();
        ArrayList<consumer> consumerList =new ArrayList<>();

       Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_CONSUMER,null);

       while (cursor.moveToNext()){
           consumer model =new consumer();
           model.id=cursor.getInt(0);
           model.name=cursor.getString(1);
           model.number=cursor.getString(2);
           model.timestamp=cursor.getString(3);
           model.total_amount=cursor.getInt(4);

           consumerList.add(model);
       }
       return consumerList;

   }

   public ArrayList<bills> fetchBills(){
        SQLiteDatabase db=this.getReadableDatabase();

        ArrayList<bills> billsList =new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_BILLS,null);
        while (cursor.moveToNext()){
            bills model =new bills();
            model.id=cursor.getInt(0);
            model.amount=cursor.getDouble(1);
            model.timestamp=cursor.getString(2);
            model.consumer_id=cursor.getInt(3);
            model.status=cursor.getInt(4);


            billsList.add(model);
        }
        return billsList;
   }

//   TODO: create function to fecth data for specific consumer:  DONE
public ArrayList<ContentValues> fetchConsumerName(Integer id) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<ContentValues> bills = new ArrayList<>();

    Cursor cursor = null;

    try {
        cursor = db.rawQuery("SELECT * FROM " + TABLE_BILLS + " WHERE " + COLUMN_BILLS_CONSUMER_ID + "=" + id, null);

        while (cursor.moveToNext()) {
            // Create a new ContentValues object for each row
            ContentValues bill = new ContentValues();

            Integer status = cursor.getInt(4); // Assuming 4 is the index for status
            Double amount = cursor.getDouble(1); // Assuming 1 is the index for amount
            String timestamp = cursor.getString(2); // Assuming 2 is the index for timestamp

            // Put the values into the ContentValues object
            bill.put("status", status);
            bill.put("amount", amount);
            bill.put("timestamp", timestamp);

            // Add the ContentValues object to the list
            bills.add(bill);
        }
    } catch (Exception e) {
        // Handle any exceptions, maybe log the error
        e.printStackTrace();
    } finally {
        if (cursor != null) {
            cursor.close(); // Always close the cursor
        }
        db.close(); // Close the database connection
    }

    return bills;
}


}
