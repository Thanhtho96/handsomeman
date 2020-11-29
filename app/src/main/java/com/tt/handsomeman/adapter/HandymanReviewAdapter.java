package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemReviewBinding;
import com.tt.handsomeman.response.HandymanReviewResponse;

import java.util.List;

public class HandymanReviewAdapter extends RecyclerView.Adapter<HandymanReviewAdapter.HandymanReviewViewHolder> {

    private final List<HandymanReviewResponse> handymanReviewResponseList;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final String authorizationCode;
    private ItemReviewBinding binding;

    public HandymanReviewAdapter(List<HandymanReviewResponse> handymanReviewResponseList,
                                 Context context,
                                 String authorizationCode) {
        this.handymanReviewResponseList = handymanReviewResponseList;
        this.context = context;
        this.authorizationCode = authorizationCode;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public HandymanReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        binding = ItemReviewBinding.inflate(layoutInflater, parent, false);
        View item = binding.getRoot();
        return new HandymanReviewViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull HandymanReviewViewHolder holder,
                                 int position) {
        HandymanReviewResponse handymanReviewResponse = handymanReviewResponseList.get(position);

        holder.customerName.setText(handymanReviewResponse.getCustomerName());
        holder.customerRating.setRating(handymanReviewResponse.getVote());
        holder.customerComment.setText(handymanReviewResponse.getComment());

        GlideUrl glideUrl = new GlideUrl((handymanReviewResponse.getAvatar()),
                new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .placeholder(R.drawable.custom_progressbar)
                .error(R.drawable.logo)
                .signature(new MediaStoreSignature("", handymanReviewResponse.getUpdateDate(), 0))
                .into(holder.customerAvatar);
    }

    @Override
    public int getItemCount() {
        return handymanReviewResponseList.size();
    }

    class HandymanReviewViewHolder extends RecyclerView.ViewHolder {

        private final ImageView customerAvatar;
        private final TextView customerName, customerComment;
        private final RatingBar customerRating;

        HandymanReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            customerAvatar = binding.reviewAvatar;
            customerName = binding.reviewNameItem;
            customerRating = binding.ratingBarReviewItem;
            customerComment = binding.reviewCommentItem;
        }
    }
}
