package com.tt.handsomeman.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecycleViewAdapter extends RecyclerView.Adapter<BaseRecycleViewAdapter.MyViewHolder> {

    @NonNull
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, viewType, parent, false);
        return new MyViewHolder(binding);
    }

    public void onBindViewHolder(@NonNull MyViewHolder holder,
                                 int position) {
        Object obj = getObjForPosition(position);
        holder.itemView.setOnClickListener(getOnItemClickListener(position));
        holder.bind(obj);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdForPosition(position);
    }

    protected abstract Object getObjForPosition(int position);

    protected abstract int getLayoutIdForPosition(int position);

    protected abstract View.OnClickListener getOnItemClickListener(int position);

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        public MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Object obj) {
//            binding.setVariable(BR.obj, obj);
            binding.executePendingBindings();
        }
    }
}