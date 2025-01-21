package com.example.mymanager2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerConsumerAdapter extends RecyclerView.Adapter<RecyclerConsumerAdapter.ViewHolder> {
Context context;
ArrayList<consumer> consumerList;

    RecyclerConsumerAdapter(Context context, ArrayList<consumer> consumerList){
    this.context=context;
    this.consumerList=consumerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view = LayoutInflater.from(context).inflate(R.layout.consumer_card,parent,false);

    ViewHolder vh=new ViewHolder(view);

    return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.consumer_name.setText(consumerList.get(position).name);
        holder.amount.setText(consumerList.get(position).total_amount);
    }

    @Override
    public int getItemCount() {
        return consumerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView consumer_name,amount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            consumer_name=itemView.findViewById(R.id.consumer_name);
            amount=itemView.findViewById(R.id.amount);
        }
    }
}
