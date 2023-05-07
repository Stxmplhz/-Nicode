package com.example.nicode.EventHomeSwipe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nicode.R;

import java.util.ArrayList;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {
    ArrayList<EventModel> eventModels;
    Context context;

    public EventAdapter(Context context, ArrayList<EventModel> eventModels) {
        this.context = context;
        this.eventModels = eventModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.homeevent_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.imageView.setImageResource(eventModels.get(position).geteventLogo());

    }

    @Override
    public int getItemCount() {
        return eventModels.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        ImageView imageView;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            linearLayout = itemView.findViewById(R.id.linear_layout_event);
        }
    }
}
