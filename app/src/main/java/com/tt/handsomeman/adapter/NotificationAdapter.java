package com.tt.handsomeman.adapter;

import android.content.Context;
import android.graphics.Typeface;
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
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.ItemNotificationBinding;
import com.tt.handsomeman.response.NotificationResponse;
import com.tt.handsomeman.util.NotificationType;
import com.tt.handsomeman.util.TimeParseUtil;

import java.text.ParseException;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int MADE_A_BID = 1;
    private static final int PAID_PAYMENT = 2;
    private static final int ACCEPT_BID = 3;

    private final Context context;
    private final List<NotificationResponse> notificationList;
    private final LayoutInflater layoutInflater;
    private final String authorizationCode;
    private OnItemClickListener listener;
    private ItemNotificationBinding binding;

    public NotificationAdapter(Context context,
                               List<NotificationResponse> notificationList,
                               String authorizationCode) {
        this.context = context;
        this.notificationList = notificationList;
        this.authorizationCode = authorizationCode;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        binding = ItemNotificationBinding.inflate(layoutInflater, parent, false);
        View view = binding.getRoot();
        switch (viewType) {
            case MADE_A_BID:
                return new MadeBidViewHolder(view, listener);
            case PAID_PAYMENT:
                return new PaidPaymentViewHolder(view, listener);
            case ACCEPT_BID:
                return new AcceptBidViewHolder(view, listener);
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position) {
        NotificationResponse notification = notificationList.get(position);

        switch (holder.getItemViewType()) {
            case MADE_A_BID:
                MadeBidViewHolder madeBidViewHolder = (MadeBidViewHolder) holder;
                madeBidViewHolder.tvNotificationBody.setText(HandymanApp.getInstance().getResources().getString(R.string.made_bid_notification, notification.getSenderName(), notification.getNotificationDescription()));
                try {
                    madeBidViewHolder.tvSendTime.setText(TimeParseUtil.setSendTimeManipulate(notification.getCreationTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!notification.getRead()) {
                    madeBidViewHolder.tvNotificationBody.setTypeface(madeBidViewHolder.tvNotificationBody.getTypeface(), Typeface.BOLD);
                }

                GlideUrl glideUrl = new GlideUrl((notification.getSenderAvatar()),
                        new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

                Glide.with(context)
                        .load(glideUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .placeholder(R.drawable.custom_progressbar)
                        .error(R.drawable.logo)
                        .signature(new MediaStoreSignature("", notification.getUpdateDate(), 0))
                        .into(madeBidViewHolder.accountAvatar);
                break;
            case ACCEPT_BID:
                AcceptBidViewHolder acceptBidViewHolder = (AcceptBidViewHolder) holder;
                acceptBidViewHolder.tvNotificationBody.setText(HandymanApp.getInstance().getResources().getString(R.string.accept_bid_notification, notification.getSenderName(), notification.getNotificationDescription()));
                try {
                    acceptBidViewHolder.tvSendTime.setText(TimeParseUtil.setSendTimeManipulate(notification.getCreationTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!notification.getRead()) {
                    acceptBidViewHolder.tvNotificationBody.setTypeface(acceptBidViewHolder.tvNotificationBody.getTypeface(), Typeface.BOLD);
                }

                GlideUrl glideUrl1 = new GlideUrl((notification.getSenderAvatar()),
                        new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

                Glide.with(context)
                        .load(glideUrl1)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .placeholder(R.drawable.custom_progressbar)
                        .error(R.drawable.logo)
                        .signature(new MediaStoreSignature("", notification.getUpdateDate(), 0))
                        .into(acceptBidViewHolder.accountAvatar);
                break;
            case PAID_PAYMENT:
                PaidPaymentViewHolder paidPaymentViewHolder = (PaidPaymentViewHolder) holder;
                int paymentMilestoneOrder = Integer.parseInt(notification.getNotificationDescription());
                String result;
                switch (paymentMilestoneOrder) {
                    case 1:
                        result = context.getString(R.string.first_milestone, paymentMilestoneOrder);
                        break;
                    case 2:
                        result = context.getString(R.string.second_milestone, paymentMilestoneOrder);
                        break;
                    case 3:
                        result = context.getString(R.string.third_milestone, paymentMilestoneOrder);
                        break;
                    default:
                        result = context.getString(R.string.default_milestone, paymentMilestoneOrder);
                        break;
                }
                paidPaymentViewHolder.tvNotificationBody.setText(HandymanApp.getInstance().getResources().getString(R.string.paid_payment_notification, notification.getSenderName(), result));
                try {
                    paidPaymentViewHolder.tvSendTime.setText(TimeParseUtil.setSendTimeManipulate(notification.getCreationTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!notification.getRead()) {
                    paidPaymentViewHolder.tvNotificationBody.setTypeface(paidPaymentViewHolder.tvNotificationBody.getTypeface(), Typeface.BOLD);
                }

                GlideUrl glideUrl2 = new GlideUrl((notification.getSenderAvatar()),
                        new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

                Glide.with(context)
                        .load(glideUrl2)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .placeholder(R.drawable.custom_progressbar)
                        .error(R.drawable.logo)
                        .signature(new MediaStoreSignature("", notification.getUpdateDate(), 0))
                        .into(paidPaymentViewHolder.accountAvatar);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    @Override
    public int getItemViewType(int position) {
        String notificationType = notificationList.get(position).getNotificationType();
        switch (NotificationType.valueOf(notificationType)) {
            case MADE_A_BID:
                return MADE_A_BID;
            case PAID_PAYMENT:
                return PAID_PAYMENT;
            case ACCEPT_BID:
                return ACCEPT_BID;
            default:
                return 0;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class MadeBidViewHolder extends RecyclerView.ViewHolder {
        private final ImageView accountAvatar;
        private final TextView tvSendTime;
        private final TextView tvNotificationBody;

        MadeBidViewHolder(@NonNull View itemView,
                          final OnItemClickListener listener) {
            super(itemView);
            accountAvatar = binding.accountAvatarNotification;
            tvSendTime = binding.pushTimeNotification;
            tvNotificationBody = binding.bodyNotification;

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public class AcceptBidViewHolder extends RecyclerView.ViewHolder {
        private final ImageView accountAvatar;
        private final TextView tvSendTime;
        private final TextView tvNotificationBody;

        AcceptBidViewHolder(@NonNull View itemView,
                            final OnItemClickListener listener) {
            super(itemView);

            accountAvatar = binding.accountAvatarNotification;
            tvSendTime = binding.pushTimeNotification;
            tvNotificationBody = binding.bodyNotification;

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }
    }

    public class PaidPaymentViewHolder extends RecyclerView.ViewHolder {
        private final ImageView accountAvatar;
        private final TextView tvSendTime;
        private final TextView tvNotificationBody;

        PaidPaymentViewHolder(@NonNull View itemView,
                              final OnItemClickListener listener) {
            super(itemView);

            accountAvatar = binding.accountAvatarNotification;
            tvSendTime = binding.pushTimeNotification;
            tvNotificationBody = binding.bodyNotification;

            itemView.setOnClickListener(view -> {
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
