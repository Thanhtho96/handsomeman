package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.databinding.ItemPlaceBinding;
import com.tt.handsomeman.model.PlaceResponse;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {

    private final Context context;
    private final List<PlaceResponse> placeResponseList;
    private final LayoutInflater inflater;
    private ItemPlaceBinding binding;
    private OnItemClickListener listener;

    public PlaceAdapter(Context context,
                        List<PlaceResponse> placeResponseList) {
        this.context = context;
        this.placeResponseList = placeResponseList;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        binding = ItemPlaceBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new PlaceViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder,
                                 int position) {
        PlaceResponse placeResponse = placeResponseList.get(position);

        holder.primaryPlaceName.setText(placeResponse.getPrimaryPlaceName());
        holder.secondaryPlaceName.setText(placeResponse.getSecondaryPlaceName());
    }

    @Override
    public int getItemCount() {
        return placeResponseList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        private final TextView primaryPlaceName, secondaryPlaceName;

        PlaceViewHolder(@NonNull View itemView,
                        final OnItemClickListener listener) {
            super(itemView);

            primaryPlaceName = binding.primaryPlaceName;
            secondaryPlaceName = binding.secondaryPlaceName;

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }
}
