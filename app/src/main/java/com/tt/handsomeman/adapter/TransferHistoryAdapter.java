package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemTransferHistoryBinding;
import com.tt.handsomeman.response.TransferHistoryResponse;
import com.tt.handsomeman.util.DecimalFormat;
import com.tt.handsomeman.util.TimeParseUtil;

import java.text.ParseException;
import java.util.List;

public class TransferHistoryAdapter extends RecyclerView.Adapter<TransferHistoryAdapter.TransferHistoryViewHolder> {

    private final List<TransferHistoryResponse> transferHistoryList;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private ItemTransferHistoryBinding binding;

    public TransferHistoryAdapter(List<TransferHistoryResponse> transferHistoryList,
                                  Context context) {
        this.transferHistoryList = transferHistoryList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public TransferHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                        int viewType) {
        binding = ItemTransferHistoryBinding.inflate(layoutInflater, parent, false);
        return new TransferHistoryViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull TransferHistoryViewHolder holder,
                                 int position) {
        TransferHistoryResponse transferHistory = transferHistoryList.get(position);
        holder.lastPayoutNumber.setText(HandymanApp.getInstance().getString(R.string.account_ends_with, transferHistory.getLastPayoutNumber()));
        try {
            holder.dateTransfer.setText(TimeParseUtil.setSendTimeManipulate(transferHistory.getDateTransfer()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.balanceTransfer.setText(HandymanApp.getInstance().getString(R.string.money_currency_string, DecimalFormat.format(transferHistory.getBalance())));
    }

    @Override
    public int getItemCount() {
        return transferHistoryList.size();
    }

    class TransferHistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView lastPayoutNumber, dateTransfer, balanceTransfer;

        TransferHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            lastPayoutNumber = binding.lastNumberPayout;
            dateTransfer = binding.dateTransfer;
            balanceTransfer = binding.transferBalance;
        }
    }
}
