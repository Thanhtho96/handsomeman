package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemCustomerTransferHistoryBinding;
import com.tt.handsomeman.response.CustomerTransferHistoryResponse;
import com.tt.handsomeman.util.DecimalFormat;
import com.tt.handsomeman.util.TimeParseUtil;

import java.text.ParseException;
import java.util.List;

public class CustomerTransferHistoryAdapter extends RecyclerView.Adapter<CustomerTransferHistoryAdapter.ViewHolder> {

    private final List<CustomerTransferHistoryResponse> historyResponseList;
    private final Context context;
    private final LayoutInflater inflater;
    private final String authorizationCode;
    private ItemCustomerTransferHistoryBinding binding;

    public CustomerTransferHistoryAdapter(List<CustomerTransferHistoryResponse> historyResponseList,
                                          Context context,
                                          String authorizationCode) {
        this.historyResponseList = historyResponseList;
        this.context = context;
        this.authorizationCode = authorizationCode;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CustomerTransferHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                        int viewType) {
        binding = ItemCustomerTransferHistoryBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerTransferHistoryAdapter.ViewHolder holder,
                                 int position) {
        CustomerTransferHistoryResponse historyResponse = historyResponseList.get(position);
        holder.jobTitle.setText(historyResponse.getJobTitle());
        holder.lastPayoutNumber.setText(context.getString(R.string.account_ends_with, historyResponse.getLastPayoutNumber()));
        try {
            holder.dateTransfer.setText(TimeParseUtil.setSendTimeManipulate(historyResponse.getDateTransfer()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        switch (historyResponse.getPaymentMilestoneOrder()) {
            case 1:
                holder.paymentMilestoneOrder.setText(context.getString(R.string.first_milestone, historyResponse.getPaymentMilestoneOrder()));
                break;
            case 2:
                holder.paymentMilestoneOrder.setText(context.getString(R.string.second_milestone, historyResponse.getPaymentMilestoneOrder()));
                break;
            case 3:
                holder.paymentMilestoneOrder.setText(context.getString(R.string.third_milestone, historyResponse.getPaymentMilestoneOrder()));
                break;
            default:
                holder.paymentMilestoneOrder.setText(context.getString(R.string.default_milestone, historyResponse.getPaymentMilestoneOrder()));
                break;
        }
        holder.handymanName.setText(historyResponse.getHandymanName());
        holder.transferBalance.setText(context.getString(R.string.money_currency_string, DecimalFormat.format(historyResponse.getBalance())));

        GlideUrl glideUrl = new GlideUrl((historyResponse.getHandymanAvatar()),
                new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .placeholder(R.drawable.custom_progressbar)
                .error(R.drawable.logo)
                .signature(new MediaStoreSignature("", historyResponse.getUpdateDate(), 0))
                .into(holder.handymanAvatar);
    }

    @Override
    public int getItemCount() {
        return historyResponseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView jobTitle, lastPayoutNumber, dateTransfer, paymentMilestoneOrder, handymanName, transferBalance;
        private final ImageView handymanAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transferBalance = binding.transferBalance;
            handymanName = binding.handymanName;
            paymentMilestoneOrder = binding.paymentMileStoneOrder;
            dateTransfer = binding.dateTransfer;
            lastPayoutNumber = binding.lastNumberPayout;
            jobTitle = binding.jobTitle;
            handymanAvatar = binding.handymanAvatar;
        }
    }
}
