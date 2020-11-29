package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.databinding.ItemSkillBinding;
import com.tt.handsomeman.model.Skill;

import java.util.List;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.SkillViewHolder> {

    private final List<Skill> skillList;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private ItemSkillBinding binding;

    public SkillAdapter(List<Skill> skillList,
                        Context context) {
        this.skillList = skillList;
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SkillViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                              int viewType) {
        binding = ItemSkillBinding.inflate(layoutInflater, parent, false);
        View item = binding.getRoot();
        return new SkillAdapter.SkillViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull SkillViewHolder holder,
                                 int position) {
        Skill skill = skillList.get(position);

        holder.tvSkillName.setText(skill.getName());
    }

    @Override
    public int getItemCount() {
        return skillList.size();
    }

    class SkillViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvSkillName;

        SkillViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSkillName = binding.textViewItemSkillName;
        }
    }
}
