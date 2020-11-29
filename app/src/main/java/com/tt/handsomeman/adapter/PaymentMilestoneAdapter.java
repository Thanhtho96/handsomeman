package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemPaymentMilestoneBinding;
import com.tt.handsomeman.response.PaymentPaid;
import com.tt.handsomeman.util.DecimalFormat;

import java.util.List;

public class PaymentMilestoneAdapter extends RecyclerView.Adapter<PaymentMilestoneAdapter.ViewHolder> {

    private final Context context;
    private final List<PaymentPaid> paymentPaidList;
    private final LayoutInflater inflater;
    private ItemPaymentMilestoneBinding binding;

    public PaymentMilestoneAdapter(Context context,
                                   List<PaymentPaid> paymentPaidList) {
        this.context = context;
        this.paymentPaidList = paymentPaidList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        binding = ItemPaymentMilestoneBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new PaymentMilestoneAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        PaymentPaid paymentPaid = paymentPaidList.get(position);

        int paymentMilestoneOrder = paymentPaid.getPaymentMilestoneOrder();
        String result;
        switch (paymentMilestoneOrder) {
            case 1:
                result = context.getString(R.string.first_milestone, paymentMilestoneOrder);
                break;
            case 2:
                result = context.getString(R.string.second_milestone, paymentMilestoneOrder);
                break;
            case 3:
                result = context.getString(R.string.third_milestone, paymentMilestoneOrder);
                break;
            default:
                result = context.getString(R.string.default_milestone, paymentMilestoneOrder);
                break;
        }

        holder.paymentMilestoneOrder.setText(result);
        holder.paymentMilestonePercentage.setText(context.getString(R.string.money_currency_string, DecimalFormat.format(paymentPaid.getBalance())));
    }

    @Override
    public int getItemCount() {
        return paymentPaidList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView paymentMilestoneOrder, paymentMilestonePercentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            paymentMilestoneOrder = binding.paymentMileStoneOrder;
            paymentMilestonePercentage = binding.paymentMileStonePercentage;
        }
    }
}
