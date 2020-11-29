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
import com.tt.handsomeman.databinding.ItemContactBinding;
import com.tt.handsomeman.databinding.ItemContactHeaderBinding;
import com.tt.handsomeman.response.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int HEADER = 0;
    public static final int ITEM = 1;

    private final List<Contact> contactList;
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final String authorizationCode;
    private ItemContactBinding contactBinding;
    private ItemContactHeaderBinding headerBinding;

    public ContactAdapter(List<Contact> contactList,
                          Context context,
                          String authorizationCode) {
        this.contactList = contactList;
        this.context = context;
        this.authorizationCode = authorizationCode;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                      int viewType) {
        View view;
        switch (viewType) {
            case HEADER:
                headerBinding = ItemContactHeaderBinding.inflate(layoutInflater, parent, false);
                view = headerBinding.getRoot();
                return new ContactAdapter.HeaderViewHolder(view);
            case ITEM:
                contactBinding = ItemContactBinding.inflate(layoutInflater, parent, false);
                view = contactBinding.getRoot();
                return new ContactAdapter.ContactViewHolder(view);
            default:
                throw new IllegalStateException("unsupported item type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,
                                 int position) {
        Contact contact = contactList.get(position);

        switch (holder.getItemViewType()) {
            case HEADER:
                ContactAdapter.HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.tvHeaderCharacter.setText(contact.getFirstCharacter(contact.getName()));
                headerViewHolder.tvContactNameHeader.setText(contact.getName());

                GlideUrl glideUrl = new GlideUrl((contact.getAvatar()),
                        new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

                Glide.with(context)
                        .load(glideUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .placeholder(R.drawable.custom_progressbar)
                        .error(R.drawable.logo)
                        .signature(new MediaStoreSignature("", contact.getUpdateDate(), 0))
                        .into(headerViewHolder.imgContactAvatarHeader);
                break;
            case ITEM:
                ContactAdapter.ContactViewHolder contactViewHolder = (ContactViewHolder) holder;
                contactViewHolder.tvContactName.setText(contact.getName());

                GlideUrl glideUrl1 = new GlideUrl((contact.getAvatar()),
                        new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

                Glide.with(context)
                        .load(glideUrl1)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .placeholder(R.drawable.custom_progressbar)
                        .error(R.drawable.logo)
                        .signature(new MediaStoreSignature("", contact.getUpdateDate(), 0))
                        .into(contactViewHolder.imgContactAvatar);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (contactList.get(position).getIsHeader()) {
            return HEADER;
        } else {
            return ITEM;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvHeaderCharacter;
        private final ImageView imgContactAvatarHeader;
        private final TextView tvContactNameHeader;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvHeaderCharacter = headerBinding.textViewHeaderCharacter;
            imgContactAvatarHeader = headerBinding.imageViewContactAvatarHeader;
            tvContactNameHeader = headerBinding.textViewContactNameHeader;
        }
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgContactAvatar;
        private final TextView tvContactName;

        ContactViewHolder(@NonNull View itemView) {
            super(itemView);

            imgContactAvatar = contactBinding.imageViewContactAvatar;
            tvContactName = contactBinding.textViewContactName;
        }
    }
}
