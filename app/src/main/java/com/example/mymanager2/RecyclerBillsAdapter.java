package com.example.mymanager2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerBillsAdapter extends RecyclerView.Adapter<RecyclerBillsAdapter.ViewHolder> {

    Context context;
    ArrayList<bills> billsArrayList;

    MyDBHelper db;

    RecyclerBillsAdapter(Context context, ArrayList<bills> billsList ) {
        this.context=context;
        this.billsArrayList=billsList;
        this.db=new MyDBHelper(context);
        updateBillList();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.biil_card,parent,false);
        ViewHolder vh= new ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.consumer_name.setText(db.fetchConsumerNamebyId(billsArrayList.get(position).consumer_id));
        holder.amount.setText(String.valueOf(billsArrayList.get(position).amount));
        holder.bill_time.setText(billsArrayList.get(position).timestamp.split(" ")[1].substring(0,5));
        holder.bill_date.setText(billsArrayList.get(position).timestamp.split(" ")[0]);
        if(billsArrayList.get(position).status==1){
            holder.amount.setTextColor(context.getResources().getColor(R.color.red));
        } else if (billsArrayList.get(position).status==0) {
            holder.amount.setTextColor(context.getResources().getColor(R.color.dark_green));

        }

    }

    @Override
    public int getItemCount() {
        return billsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView consumer_name,amount,bill_time,bill_date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            consumer_name=itemView.findViewById(R.id.consumer_name);
            amount=itemView.findViewById(R.id.bill_amount);
            bill_time=itemView.findViewById(R.id.bill_time);
            bill_date=itemView.findViewById(R.id.bill_date);
        }
    }
    public void updateBillList(){
        this.billsArrayList=db.fetchBills();
        notifyDataSetChanged();
    }
}
