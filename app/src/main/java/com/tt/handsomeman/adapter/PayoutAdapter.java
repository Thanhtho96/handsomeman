package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemPayoutBinding;
import com.tt.handsomeman.model.Payout;

import java.util.List;

public class PayoutAdapter extends RecyclerView.Adapter<PayoutAdapter.PayoutViewHolder> {

    private final List<Payout> payoutList;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private ItemPayoutBinding binding;
    private OnItemClickListener mListener;

    public PayoutAdapter(List<Payout> payoutList,
                         Context context) {
        this.payoutList = payoutList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public PayoutAdapter.PayoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                             int viewType) {
        binding = ItemPayoutBinding.inflate(layoutInflater, parent, false);
        View item = binding.getRoot();
        return new PayoutAdapter.PayoutViewHolder(item, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PayoutAdapter.PayoutViewHolder holder,
                                 int position) {
        Payout payout = payoutList.get(position);
        String lastPayoutNumber = payout.getAccountNumber().substring(payout.getAccountNumber().length() - 4);
        holder.tvPayoutLastNumbers.setText(HandymanApp.getInstance().getString(R.string.account_ends_with, lastPayoutNumber));
    }

    @Override
    public int getItemCount() {
        return payoutList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class PayoutViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton btnPayout;
        private final TextView tvPayoutLastNumbers;

        PayoutViewHolder(@NonNull View itemView,
                         final OnItemClickListener listener) {
            super(itemView);
            btnPayout = binding.imageButtonItemPayout;
            tvPayoutLastNumbers = binding.payoutLastNumbers;

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            btnPayout.setOnClickListener(v -> {
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
