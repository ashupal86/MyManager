package com.example.mymanager2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MyDBHelper extends SQLiteOpenHelper {
    // Database Information
    private static final String DATABASE_NAME = "cashflow.db";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    private static final String TABLE_CONSUMER = "Consumer";
    private static final String TABLE_BILLS = "Bills";

    private static final String TABLE_DELETED_USERS="deleted_users";

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


        String CREATE_TABLE_DELETED_USERS = "CREATE TABLE IF NOT EXISTS deleted_users (" + COLUMN_CONSUMER_ID + " NOT NULL, " +
                COLUMN_CONSUMER_NAME + " TEXT NOT NULL, " + COLUMN_CONSUMER_PHONE + " TEXT UNIQUE NOT NULL, " + COLUMN_CONSUMER_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + COLUMN_CONSUMER_TOTAL_AMOUNT + " NOT NULL)";

        db.execSQL(CREATE_TABLE_DELETED_USERS);

        String CREATE_TABLE_DELETED_BILLS = "CREATE TABLE IF NOT EXISTS deleted_bills (" + COLUMN_BILLS_ID + " NOT NULL, " + COLUMN_BILLS_AMOUNT + " REAL NOT NULL, " + COLUMN_BILLS_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + COLUMN_BILLS_CONSUMER_ID + " NOT NULL)";
        db.execSQL(CREATE_TABLE_DELETED_BILLS);



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

        onCreate(db);



    }

    // Method to insert a new consumer
    public void addConsumer(String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CONSUMER_NAME, name);
        values.put(COLUMN_CONSUMER_PHONE, phone);
        db.insert(TABLE_CONSUMER, null, values);
        db.close();
    }

    // Method to insert a new bill
    public void addBill(double amount, int consumerId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BILLS_AMOUNT, amount);
        values.put(COLUMN_BILLS_CONSUMER_ID, consumerId);
        values.put(COLUMN_BILLS_STATUS, status); // 0 for cashout, 1 for cashin
        db.insert(TABLE_BILLS, null, values);
        db.close();
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
       Log.d("consumerlist_data",consumerList.toString());
       db.close();
       return consumerList;

   }

   public ArrayList<bills> fetchBills(){
        SQLiteDatabase db=this.getReadableDatabase();

        ArrayList<bills> billsList =new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_BILLS +" ORDER BY "+COLUMN_BILLS_TIMESTAMP+" DESC",null);
        while (cursor.moveToNext()){
            bills model =new bills();
            model.id=cursor.getInt(0);
            model.amount=cursor.getDouble(1);
            model.timestamp=cursor.getString(2);
            model.consumer_id=cursor.getInt(3);
            model.status=cursor.getInt(4);


            billsList.add(model);
        }
        Log.d("All_bills",billsList.toString());
        db.close();
        return billsList;
   }

//   TODO: create function to fecth data for specific consumer:  DONE
public ArrayList<bills> fetchConsumerBills(Integer id) {
    SQLiteDatabase db = this.getReadableDatabase();
    ArrayList<bills> arrayListbills = new ArrayList<>();

    Cursor cursor = null;

    try {
        cursor = db.rawQuery("SELECT * FROM " + TABLE_BILLS + " WHERE " + COLUMN_BILLS_CONSUMER_ID + "=" + id, null);

        while (cursor.moveToNext()) {
            // Create a new ContentValues object for each row
            Integer bill_id = cursor.getInt(0); // Assuming 0 is the index for id
            Integer consumer_id = cursor.getInt(3); // Assuming 3 is the index for consumer_id
            Integer status = cursor.getInt(4); // Assuming 4 is the index for status
            Double amount = cursor.getDouble(1); // Assuming 1 is the index for amount
            String timestamp = cursor.getString(2); // Assuming 2 is the index for timestamp

            // Put the values into the ContentValues object
            bills model = new bills();
            model.status = status;
            model.amount = amount;
            model.timestamp = timestamp;
            model.consumer_id = consumer_id;
            model.id = bill_id;
            arrayListbills.add(model);


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

    return arrayListbills;
}

    public String fetchConsumerNamebyId(Integer id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String consumerName = null;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_CONSUMER + " WHERE " + COLUMN_CONSUMER_ID + "=" + id, null);

            if (cursor.moveToFirst()) {
                consumerName = cursor.getString(1); // Assuming 1 is the index for name
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
        return consumerName;
    }

    public void DeleteConsumer(Integer id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor consumerCursor = null;
        Cursor billsCursor = null;

        try {
            // Start a transaction
            db.beginTransaction();

            // Retrieve consumer details
            consumerCursor = db.rawQuery("SELECT * FROM " + TABLE_CONSUMER + " WHERE " + COLUMN_CONSUMER_ID + " = ?", new String[]{String.valueOf(id)});
            if (consumerCursor.moveToFirst()) {
                String consumerName = consumerCursor.getString(consumerCursor.getColumnIndexOrThrow(COLUMN_CONSUMER_NAME));
                String consumerPhone = consumerCursor.getString(consumerCursor.getColumnIndexOrThrow(COLUMN_CONSUMER_PHONE));
                double consumerTotalAmount = consumerCursor.getDouble(consumerCursor.getColumnIndexOrThrow(COLUMN_CONSUMER_TOTAL_AMOUNT));

                // Insert the consumer details into the deleted_users table
                db.execSQL("INSERT INTO deleted_users (" +
                                COLUMN_CONSUMER_ID + ", " +
                                COLUMN_CONSUMER_NAME + ", " +
                                COLUMN_CONSUMER_PHONE + ", " +
                                COLUMN_CONSUMER_TOTAL_AMOUNT + ") VALUES (?, ?, ?, ?)",
                        new Object[]{id, consumerName, consumerPhone, consumerTotalAmount});
            }

            // Retrieve and archive bills associated with the consumer
            billsCursor = db.rawQuery("SELECT * FROM " + TABLE_BILLS + " WHERE " + COLUMN_BILLS_CONSUMER_ID + " = ?", new String[]{String.valueOf(id)});
            while (billsCursor.moveToNext()) {
                int billId = billsCursor.getInt(billsCursor.getColumnIndexOrThrow(COLUMN_BILLS_ID));
                double billAmount = billsCursor.getDouble(billsCursor.getColumnIndexOrThrow(COLUMN_BILLS_AMOUNT));

                // Insert the deleted bills into the deleted_bills table
                db.execSQL("INSERT INTO deleted_bills (" +
                                COLUMN_BILLS_ID + ", " +
                                COLUMN_BILLS_AMOUNT + ", " +
                                COLUMN_BILLS_CONSUMER_ID + ") VALUES (?, ?, ?)",
                        new Object[]{billId, billAmount, id});
            }

            // Delete only the bills from the bills table
            db.execSQL("DELETE FROM " + TABLE_BILLS + " WHERE " + COLUMN_BILLS_CONSUMER_ID + " = ?", new String[]{String.valueOf(id)});

            // Commit the transaction
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (consumerCursor != null) {
                consumerCursor.close();
            }
            if (billsCursor != null) {
                billsCursor.close();
            }
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<consumer> fetchActiveConsumers() {
        ArrayList<consumer> activeConsumers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Query to fetch consumers who are NOT in the deleted_users table
            String query = "SELECT * FROM " + TABLE_CONSUMER + " WHERE " + COLUMN_CONSUMER_ID +
                    " NOT IN (SELECT " + COLUMN_CONSUMER_ID + " FROM " + TABLE_DELETED_USERS + ")";

            cursor = db.rawQuery(query, null);

            // Populate the list of active consumers
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONSUMER_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONSUMER_NAME));
                String phone = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONSUMER_PHONE));
                double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CONSUMER_TOTAL_AMOUNT));

                consumer consumer = new consumer();
                consumer.id = id;
                consumer.name = name;
                consumer.number = phone;
                consumer.total_amount = (int) totalAmount;

                activeConsumers.add(consumer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return activeConsumers;
    }


}


