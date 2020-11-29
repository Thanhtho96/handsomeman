package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.SpinnerItemBlackTextBinding;

public class SpinnerPercentage extends BaseAdapter {

    private final Context context;
    private final String[] str;
    private final LayoutInflater layoutInflater;
    private SpinnerItemBlackTextBinding binding;

    public SpinnerPercentage(Context context,
                             String[] str) {
        this.context = context;
        this.str = str;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return str.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position,
                        View view,
                        ViewGroup viewGroup) {
        binding = SpinnerItemBlackTextBinding.inflate(layoutInflater, viewGroup, false);
        view = binding.getRoot();
        TextView countryName = binding.textViewSpinnerItemName;
        countryName.setText(HandymanApp.getInstance().getResources().getString(R.string.percentage, Integer.parseInt(str[position])));
        return view;
    }
}
