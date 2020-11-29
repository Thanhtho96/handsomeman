package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemJobFilterBinding;
import com.tt.handsomeman.model.Job;

import java.util.List;

public class JobFilterAdapter extends RecyclerView.Adapter<JobFilterAdapter.JobFilterViewHolder> {

    private final List<Job> jobList;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private ItemJobFilterBinding binding;

    private OnItemClickListener mListener;

    public JobFilterAdapter(Context context,
                            List<Job> jobList) {
        this.jobList = jobList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public JobFilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                  int viewType) {
        binding = ItemJobFilterBinding.inflate(layoutInflater, parent, false);
        View item = binding.getRoot();
        return new JobFilterViewHolder(item, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull JobFilterViewHolder holder,
                                 int position) {
        Job job = jobList.get(position);

        holder.tvJobTitle.setText(job.getTitle());
        holder.tvCreateTime.setText(context.getResources().getQuantityString(R.plurals.numberOfHour, job.setCreateTimeBinding(), job.setCreateTimeBinding()));
        holder.tvDeadline.setText(context.getResources().getQuantityString(R.plurals.numberOfDay, job.setDeadlineBinding(), job.setDeadlineBinding()));
        holder.tvBudget.setText(job.setBudgetRange());
        holder.tvLocationName.setText(job.getLocation());
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    class JobFilterViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvJobTitle, tvCreateTime, tvBudget, tvLocationName, tvDeadline;
        private final ImageButton btnJobDetail;

        JobFilterViewHolder(@NonNull View itemView,
                            final OnItemClickListener listener) {
            super(itemView);

            tvJobTitle = binding.textViewJobName;
            tvCreateTime = binding.textViewCreateTime;
            tvBudget = binding.textViewBudgetRange;
            tvLocationName = binding.textViewLocationName;
            tvDeadline = binding.textViewDeadLine;

            btnJobDetail = binding.imageButtonJobDetail;

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });

            btnJobDetail.setOnClickListener(view -> {
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