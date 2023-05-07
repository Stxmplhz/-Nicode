package com.example.nicode.Gift;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nicode.R;

import java.util.ArrayList;
import java.util.List;

public class GiftAdapter extends RecyclerView.Adapter<GiftAdapter.ViewHolder> {
    ArrayList<GiftModel> giftModels;
    Context context;
    SelectedGift mselectedGift;

    public GiftAdapter(Context context, ArrayList<GiftModel> giftModels, SelectedGift selectedGift) {
        this.context = context;
        this.giftModels = giftModels;
        this.mselectedGift = selectedGift;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(giftModels.get(position).getgiftLogo());

        holder.itemView.setOnClickListener(view -> {
            mselectedGift.selectedGift(giftModels.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return giftModels.size();
    }

    public interface SelectedGift{
        void selectedGift(GiftModel giftModel);
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView imageView;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            linearLayout = itemView.findViewById(R.id.linear_layout_gift);
        }
    }
}
