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
import com.tt.handsomeman.response.CustomerReviewResponse;

import java.util.List;

public class CustomerReviewAdapter extends RecyclerView.Adapter<CustomerReviewAdapter.CustomerReviewViewHolder> {

    private final List<CustomerReviewResponse> customerReviewResponses;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final String authorizationCode;
    private ItemReviewBinding binding;

    public CustomerReviewAdapter(Context context,
                                 List<CustomerReviewResponse> customerReviewResponses,
                                 String authorizationCode) {
        this.customerReviewResponses = customerReviewResponses;
        this.context = context;
        this.authorizationCode = authorizationCode;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CustomerReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                       int viewType) {
        binding = ItemReviewBinding.inflate(layoutInflater, parent, false);
        View item = binding.getRoot();
        return new CustomerReviewViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerReviewViewHolder holder,
                                 int position) {
        CustomerReviewResponse customerReviewResponse = customerReviewResponses.get(position);

        GlideUrl glideUrl = new GlideUrl((customerReviewResponse.getAvatar()),
                new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .placeholder(R.drawable.custom_progressbar)
                .error(R.drawable.logo)
                .signature(new MediaStoreSignature("", customerReviewResponse.getUpdateDate(), 0))
                .into(holder.handymenAvatar);

        holder.handymenName.setText(customerReviewResponse.getHandymanName());
        holder.handymenRating.setRating(customerReviewResponse.getVote());
        holder.handymenComment.setText(customerReviewResponse.getComment());
    }

    @Override
    public int getItemCount() {
        return customerReviewResponses.size();
    }

    class CustomerReviewViewHolder extends RecyclerView.ViewHolder {

        private final ImageView handymenAvatar;
        private final TextView handymenName, handymenComment;
        private final RatingBar handymenRating;

        CustomerReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            handymenAvatar = binding.reviewAvatar;
            handymenName = binding.reviewNameItem;
            handymenRating = binding.ratingBarReviewItem;
            handymenComment = binding.reviewCommentItem;
        }
    }
}
