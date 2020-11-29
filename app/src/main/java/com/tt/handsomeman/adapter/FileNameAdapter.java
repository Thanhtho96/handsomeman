package com.tt.handsomeman.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.BuildConfig;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemFileNameBinding;
import com.tt.handsomeman.response.BidFileResponse;
import com.tt.handsomeman.util.MD5;

import java.io.File;
import java.net.URLConnection;
import java.util.List;

public class FileNameAdapter extends RecyclerView.Adapter<FileNameAdapter.ViewHolder> {
    private final Context context;
    private final List<BidFileResponse> bidFileResponses;
    private final LayoutInflater inflater;
    private ItemFileNameBinding binding;
    private OnItemClickListener listener;

    public FileNameAdapter(Context context,
                           List<BidFileResponse> bidFileResponses) {
        this.context = context;
        this.bidFileResponses = bidFileResponses;
        inflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        binding = ItemFileNameBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,
                                 int position) {
        BidFileResponse bidFileResponse = bidFileResponses.get(position);

        holder.fileName.setText(bidFileResponse.getFileName());

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + bidFileResponse.getFileName());
        if (file.exists()) {
            if (MD5.checkMD5(bidFileResponse.getMd5(), file)) {
                holder.btnDownload.setVisibility(View.GONE);
                holder.pgDownload.setVisibility(View.GONE);
                holder.btnOpen.setVisibility(View.VISIBLE);

                holder.btnOpen.setOnClickListener(v -> {
                    String mimeType = URLConnection.guessContentTypeFromName(file.getName());
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_VIEW);
                    shareIntent.setDataAndType(FileProvider.getUriForFile(
                            context,
                            BuildConfig.APPLICATION_ID + ".fileprovider",
                            file), mimeType);
                    shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.open_with)));
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return bidFileResponses.size();
    }

    public interface OnItemClickListener {
        void onItemClickDownload(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ProgressBar pgDownload;
        private final TextView fileName;
        private final Button btnOpen;
        private final ImageButton btnDownload;

        public ViewHolder(@NonNull View itemView,
                          final OnItemClickListener listener) {
            super(itemView);
            fileName = binding.fileName;
            btnDownload = binding.downloadButton;
            pgDownload = binding.progressHorizontal;
            btnOpen = binding.buttonOpen;

            btnDownload.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        btnDownload.setVisibility(View.GONE);
                        pgDownload.setVisibility(View.VISIBLE);
                        listener.onItemClickDownload(position);
                    }
                }
            });
        }
    }
}
