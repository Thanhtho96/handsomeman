package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.databinding.ItemFileAddBinding;
import com.tt.handsomeman.model.FileRequest;

import java.util.List;

public class FileDisplayAdapter extends RecyclerView.Adapter<FileDisplayAdapter.ViewHolder> {

    private final Context context;
    private final List<FileRequest> fileRequestNameList;
    private final LayoutInflater inflater;
    private ItemFileAddBinding binding;

    public FileDisplayAdapter(Context context,
                              List<FileRequest> fileRequestNameList) {
        this.context = context;
        this.fileRequestNameList = fileRequestNameList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FileDisplayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                            int viewType) {
        binding = ItemFileAddBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new FileDisplayAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileDisplayAdapter.ViewHolder holder,
                                 int position) {
        String fileName = fileRequestNameList.get(position).getFileName();
        holder.tvFileName.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return fileRequestNameList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvFileName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = binding.fileName;
            ImageButton ibDelete = binding.deleteButton;
            ibDelete.setVisibility(View.GONE);
        }
    }
}
