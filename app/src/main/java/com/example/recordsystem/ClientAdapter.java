package com.example.recordsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recordsystem.databinding.CardClientBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.viewHolder> {
    Context context;
    ArrayList<Records> list;

    public ClientAdapter(Context context, ArrayList<Records> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ClientAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_client, parent, false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientAdapter.viewHolder holder, int position) {
        Records u = list.get(position);

        if (u != null){

            Glide.with(context)
                    .load(list.get(position)
                            .getImageProfile())
                    .into(holder.binding.cvImage);

            holder.binding.cardPVA.setText(list.get(position).getPva());
            holder.binding.cardpetName.setText(list.get(position).getPetName());
            holder.binding.cardDOB.setText(list.get(position).getDob());
            holder.binding.cardSex.setText(list.get(position).getSex());
            holder.binding.cardBreed.setText(list.get(position).getBreed());
            holder.binding.cardColor.setText(list.get(position).getColor());
            holder.binding.cardOwner.setText(list.get(position).getOwnerName());
            holder.binding.cardContact.setText(list.get(position).getContact());
            holder.binding.cardAddress.setText(list.get(position).getAddress());

            holder.binding.cardvet.setOnClickListener(v -> { // cardview
                Intent intent = new Intent(context, Details.class);
                intent.putExtra("Image", list.get(holder.getAdapterPosition()).getImageProfile());
                intent.putExtra("PVA", list.get(holder.getAdapterPosition()).getPva());
                intent.putExtra("Pet", list.get(holder.getAdapterPosition()).getPetName());
                intent.putExtra("Dob", list.get(holder.getAdapterPosition()).getDob());
                intent.putExtra("Sex", list.get(holder.getAdapterPosition()).getSex());
                intent.putExtra("Breed", list.get(holder.getAdapterPosition()).getBreed());
                intent.putExtra("Color", list.get(holder.getAdapterPosition()).getColor());
                intent.putExtra("Owner", list.get(holder.getAdapterPosition()).getOwnerName());
                intent.putExtra("Contact", list.get(holder.getAdapterPosition()).getContact());
                intent.putExtra("Address", list.get(holder.getAdapterPosition()).getAddress());

                context.startActivity(intent);
            });
        }else {
            list.clear();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    @SuppressLint("NotifyDataSetChanged")
    public void  searchDataList(ArrayList<Records> searchList){
        list = searchList;
        notifyDataSetChanged();
    }
    public class viewHolder extends RecyclerView.ViewHolder {
        CardClientBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CardClientBinding.bind(itemView);
        }
    }
}
