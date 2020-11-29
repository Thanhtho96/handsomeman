package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.databinding.ItemSkillEditBinding;
import com.tt.handsomeman.model.Skill;

import java.util.List;

public class SkillEditAdapter extends RecyclerView.Adapter<SkillEditAdapter.SkillEditViewHolder> {

    private final List<Skill> skillList;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private ItemSkillEditBinding binding;

    private OnItemClickListener onItemClickListener;

    public SkillEditAdapter(List<Skill> skillList,
                            Context context) {
        this.skillList = skillList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public SkillEditAdapter.SkillEditViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                   int viewType) {
        binding = ItemSkillEditBinding.inflate(layoutInflater, parent, false);
        View item = binding.getRoot();
        return new SkillEditViewHolder(item, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillEditAdapter.SkillEditViewHolder holder,
                                 int position) {
        Skill skill = skillList.get(position);
        holder.tvSkillName.setText(skill.getName());
    }

    @Override
    public int getItemCount() {
        return skillList.size();
    }

    public interface OnItemClickListener {
        void deleteSkill(int position);
    }

    class SkillEditViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvSkillName;
        private final ImageButton ibDeleteSkill;

        SkillEditViewHolder(@NonNull View itemView,
                            OnItemClickListener listener) {
            super(itemView);

            tvSkillName = binding.textViewItemSkillNameEdit;
            ibDeleteSkill = binding.imageButtonDeleteSkillEdit;

            ibDeleteSkill.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.deleteSkill(position);
                    }
                }
            });
        }
    }
}
