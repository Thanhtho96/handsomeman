package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tt.handsomeman.databinding.SpinnerItemBlackTextBinding;
import com.tt.handsomeman.response.JobTransactionResponse;

import java.util.List;

public class SpinnerJobTransaction extends BaseAdapter {

    private final Context context;
    private final List<JobTransactionResponse> transactionResponses;
    private final LayoutInflater inflater;
    private SpinnerItemBlackTextBinding binding;

    public SpinnerJobTransaction(Context context,
                                 List<JobTransactionResponse> transactionResponses) {
        this.context = context;
        this.transactionResponses = transactionResponses;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return transactionResponses.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position,
                        View convertView,
                        ViewGroup parent) {
        binding = SpinnerItemBlackTextBinding.inflate(inflater, parent, false);
        convertView = binding.getRoot();
        TextView tvLastPayoutNumber = binding.textViewSpinnerItemName;
        tvLastPayoutNumber.setText(transactionResponses.get(position).getJobTitle());
        return convertView;
    }
}
