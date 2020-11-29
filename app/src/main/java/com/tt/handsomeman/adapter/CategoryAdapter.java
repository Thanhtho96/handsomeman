package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.databinding.ItemCategoryBinding;
import com.tt.handsomeman.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> categoryList;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private ItemCategoryBinding binding;

    private OnItemClickListener mListener;

    public CategoryAdapter(Context context,
                           List<Category> categoryList) {
        this.categoryList = categoryList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                 int viewType) {
        binding = ItemCategoryBinding.inflate(layoutInflater, parent, false);
        View item = binding.getRoot();
        return new CategoryViewHolder(item, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder,
                                 int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCategoryName;
        private final ImageButton btnCategoryDetail;

        CategoryViewHolder(@NonNull View itemView,
                           final OnItemClickListener listener) {
            super(itemView);

            tvCategoryName = binding.textViewJobName;
            btnCategoryDetail = binding.imageButtonCategoryDetail;

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            btnCategoryDetail.setOnClickListener(view -> {
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