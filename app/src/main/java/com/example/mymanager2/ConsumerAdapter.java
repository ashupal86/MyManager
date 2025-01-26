package com.example.mymanager2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ConsumerAdapter extends ArrayAdapter<consumer> {
    private final Context context;
    private final ArrayList<consumer> consumers;

    private MyDBHelper db;

    public ConsumerAdapter(Context context, ArrayList<consumer> consumers) {
        super(context, R.layout.consumer_list_delete_item, consumers);
        this.context = context;
        this.consumers = consumers;
        this.db = new MyDBHelper(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the layout for each list item
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.consumer_list_delete_item, parent, false);
        }

        // Find the views in the layout
        TextView itemText = convertView.findViewById(R.id.item_text);
        ImageView deleteIcon = convertView.findViewById(R.id.delete_icon);

        // Set the consumer name to the TextView
        itemText.setText(consumers.get(position).name);

        // Set a click listener for the delete icon
        deleteIcon.setOnClickListener(v -> {
            // Remove the item from the list
            int id = consumers.get(position).id;
            consumer deletedConsumer = consumers.remove(position);
            db.DeleteConsumer(id);

            notifyDataSetChanged(); // Notify the adapter of the changes

            // Show a toast or perform any other action
            Toast.makeText(context, "Deleted " + deletedConsumer.name, Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
