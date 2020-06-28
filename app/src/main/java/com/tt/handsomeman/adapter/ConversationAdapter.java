package com.tt.handsomeman.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemConversationBinding;
import com.tt.handsomeman.response.ConversationResponse;
import com.tt.handsomeman.util.TimeParseUtil;

import java.text.ParseException;
import java.util.List;

public class ConversationAdapter extends RecyclerSwipeAdapter<ConversationAdapter.ConversationViewHolder> {

    private List<ConversationResponse> conversationResponsesList;
    private LayoutInflater layoutInflater;
    private Context context;
    private SwipeLayout deleteSwipeLayout;
    private ItemConversationBinding binding;
    private String authorizationCode;
    private OnItemClickListener mListener;

    public ConversationAdapter(List<ConversationResponse> conversationResponsesList,
                               Context context,
                               String authorizationCode) {
        this.conversationResponsesList = conversationResponsesList;
        this.context = context;
        this.authorizationCode = authorizationCode;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(ConversationAdapter.OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        binding = ItemConversationBinding.inflate(layoutInflater, parent, false);
        View item = binding.getRoot();
        return new ConversationViewHolder(item, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder,
                                 int position) {
        ConversationResponse conversationResponse = conversationResponsesList.get(position);

        holder.tvAccountName.setText(conversationResponse.getAccountName());
        holder.tvLatestMessage.setText(conversationResponse.getLatestMessage());
        try {
            holder.tvLatestMessageSendTime.setText(TimeParseUtil.setSendTimeManipulate(conversationResponse.getSendTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.conversationId = conversationResponse.getConversationId();

        mItemManger.bindView(holder.itemView, position);

        GlideUrl glideUrl = new GlideUrl((conversationResponse.getAvatar()),
                new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop()
                .placeholder(R.drawable.custom_progressbar)
                .error(R.drawable.logo)
                .signature(new MediaStoreSignature("", conversationResponse.getUpdateDate(), 0))
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return conversationResponsesList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipeLayoutMessage;
    }

    public void closeSwipeLayout() {
        mItemManger.closeAllItems();
    }

    public void deleteConversation(int position) {
        mItemManger.removeShownLayouts(deleteSwipeLayout);
        conversationResponsesList.remove(position);
        notifyItemRemoved(position);
        mItemManger.closeAllItems();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemDelete(int position);
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvAccountName, tvLatestMessage, tvLatestMessageSendTime;
        private final SwipeLayout swipeLayout;
        private final LinearLayout layoutConversation, layoutDelete;
        private final ImageView avatar;

        int conversationId;

        ConversationViewHolder(@NonNull View itemView,
                               final ConversationAdapter.OnItemClickListener listener) {
            super(itemView);
            tvAccountName = binding.accountNameConversation;
            tvLatestMessage = binding.latestMessageConversation;
            tvLatestMessageSendTime = binding.latestMessageSendTimeConversation;
            layoutConversation = binding.linearLayoutConversation;
            layoutDelete = binding.linearLayoutDeleteConversation;
            swipeLayout = binding.swipeLayoutMessage;
            avatar = binding.accountAvatarConversation;
            swipeLayout.setClickToClose(true);

            layoutConversation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeSwipeLayout();
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            layoutDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemDelete(position);
                            deleteSwipeLayout = swipeLayout;
                        }
                    }

                }
            });
        }
    }
}
