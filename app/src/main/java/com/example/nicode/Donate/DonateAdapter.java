package com.example.nicode.Donate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nicode.Gift.GiftAdapter;
import com.example.nicode.Gift.GiftModel;
import com.example.nicode.R;

import java.util.ArrayList;

public class DonateAdapter extends RecyclerView.Adapter<DonateAdapter.ViewHolder> {
    ArrayList<DonateModel> donateModels;
    Context context;
    SelectedDonation mselectedDonation;

    public DonateAdapter(Context context, ArrayList<DonateModel> donateModels,SelectedDonation selectedDonation) {
        this.context = context;
        this.donateModels = donateModels;
        this.mselectedDonation = selectedDonation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_item2,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(donateModels.get(position).getdonateLogo());

        holder.itemView.setOnClickListener(view -> {
            mselectedDonation.selectedDonation(donateModels.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return donateModels.size();
    }

    public interface SelectedDonation{
        void selectedDonation(DonateModel donateModel);
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view2);
        }
    }
}
