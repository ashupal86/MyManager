package com.example.mymanager2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerConsumerAdapter extends RecyclerView.Adapter<RecyclerConsumerAdapter.ViewHolder> {
Context context;
ArrayList<consumer> consumerList;


MyDBHelper db;

    RecyclerConsumerAdapter(Context context, ArrayList<consumer> consumerList){
    this.context=context;
    this.consumerList=consumerList;
    this.db=new MyDBHelper(context);
    updateConsumerList();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view = LayoutInflater.from(context).inflate(R.layout.consumer_card,parent,false);

    ViewHolder vh= new ViewHolder(view);

    return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.consumer_name.setText(consumerList.get(position).name);
        holder.amount.setText(String.valueOf(Math.abs(consumerList.get(position).total_amount)));
        if(consumerList.get(position).total_amount<0){
            holder.amount.setTextColor(context.getResources().getColor(R.color.error));
        }else if(consumerList.get(position).total_amount>0){
            holder.amount.setTextColor(context.getResources().getColor(R.color.dark_green));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.activity_account);


                ArrayList<bills> billsList = db.fetchConsumerBills(consumerList.get(position).id);
                if(billsList.size()==0){
                    Toast.makeText(context, "No Bills Found", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Bills Found", Toast.LENGTH_SHORT).show();
                }



                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return consumerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView consumer_name,amount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            consumer_name=itemView.findViewById(R.id.consumer_name);
            amount=itemView.findViewById(R.id.amount);
        }
    }

    public void updateConsumerList() {
        this.consumerList = db.fecthConsumer();
        notifyDataSetChanged();
    }
}
