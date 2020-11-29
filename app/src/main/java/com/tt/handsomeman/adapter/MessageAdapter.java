package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemLoadingProgressBinding;
import com.tt.handsomeman.databinding.ItemMessageReceiverBinding;
import com.tt.handsomeman.databinding.ItemMessageSenderBinding;
import com.tt.handsomeman.response.MessageResponse;
import com.tt.handsomeman.util.TimeParseUtil;

import java.text.ParseException;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SENDER = 1;
    private static final int RECEIVER = 2;
    private final List<MessageResponse> messageResponseList;
    private final LayoutInflater layoutInflater;
    private final Context context;
    private final String authorizationCode;
    private int RANDOM = 3;
    private ItemMessageSenderBinding senderBinding;
    private ItemMessageReceiverBinding receiverBinding;

    public MessageAdapter(List<MessageResponse> messageResponseList,
                          Context context,
                          String authorizationCode) {
        this.messageResponseList = messageResponseList;
        this.context = context;
        this.authorizationCode = authorizationCode;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View view;
        switch (viewType) {
            case SENDER:
                senderBinding = ItemMessageSenderBinding.inflate(layoutInflater, parent, false);
                view = senderBinding.getRoot();
                return new SenderViewHolder(view);
            case RECEIVER:
                receiverBinding = ItemMessageReceiverBinding.inflate(layoutInflater, parent, false);
                view = receiverBinding.getRoot();
                return new ReceiverViewHolder(view);
            default:
                ItemLoadingProgressBinding progressBinding = ItemLoadingProgressBinding.inflate(layoutInflater, parent, false);
                view = progressBinding.getRoot();
                return new ProgressViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position) {
        MessageResponse messageResponse = messageResponseList.get(position);

        switch (holder.getItemViewType()) {
            case SENDER:
                SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
                try {
                    senderViewHolder.tvSendTime.setText(TimeParseUtil.setSendTimeManipulate(messageResponse.getSendTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                senderViewHolder.tvMessageBody.setText(messageResponse.getBody());

                GlideUrl glideUrl = new GlideUrl((messageResponse.getAvatar()),
                        new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

                Glide.with(context)
                        .load(glideUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .placeholder(R.drawable.custom_progressbar)
                        .error(R.drawable.logo)
                        .signature(new MediaStoreSignature("", messageResponse.getUpdateDate(), 0))
                        .into(senderViewHolder.imgAvatar);
                break;
            case RECEIVER:
                ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
                try {
                    receiverViewHolder.tvSendTime.setText(TimeParseUtil.setSendTimeManipulate(messageResponse.getSendTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                receiverViewHolder.tvMessageBody.setText(messageResponse.getBody());

                GlideUrl glideUrl1 = new GlideUrl((messageResponse.getAvatar()),
                        new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

                Glide.with(context)
                        .load(glideUrl1)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .placeholder(R.drawable.custom_progressbar)
                        .error(R.drawable.logo)
                        .signature(new MediaStoreSignature("", messageResponse.getUpdateDate(), 0))
                        .into(receiverViewHolder.imgAvatar);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageResponseList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageResponseList.get(position).getType();
    }

    public void addLoading() {
        messageResponseList.add(new MessageResponse(RANDOM++));
        notifyItemInserted(messageResponseList.size() - 1);
    }

    public void removeLoading() {
        int position = messageResponseList.size() - 1;
        MessageResponse item = messageResponseList.get(position);
        if (item.getType() != SENDER && item.getType() != RECEIVER) {
            messageResponseList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgAvatar;
        private final TextView tvSendTime;
        private final TextView tvMessageBody;

        SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = senderBinding.avatarMessage;
            tvSendTime = senderBinding.sendTimeMessage;
            tvMessageBody = senderBinding.messageBody;
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgAvatar;
        private final TextView tvSendTime;
        private final TextView tvMessageBody;

        ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAvatar = receiverBinding.avatarMessage;
            tvSendTime = receiverBinding.sendTimeMessage;
            tvMessageBody = receiverBinding.messageBody;
        }
    }
}
