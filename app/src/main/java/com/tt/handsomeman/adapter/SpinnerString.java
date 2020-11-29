package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tt.handsomeman.databinding.SpinnerItemStandardBinding;

public class SpinnerString extends BaseAdapter {

    private final Context context;
    private final String[] str;
    private final LayoutInflater layoutInflater;
    private SpinnerItemStandardBinding binding;

    public SpinnerString(Context context,
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
        binding = SpinnerItemStandardBinding.inflate(layoutInflater, viewGroup, false);
        view = binding.getRoot();
        TextView itemName = binding.textViewSpinnerItemName;
        itemName.setText(str[position]);
        return view;
    }
}
