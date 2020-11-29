package com.tt.handsomeman.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.DialogYesOrNoBinding;

public class YesOrNoDialog extends Dialog implements android.view.View.OnClickListener {

    private final String dialogDescription;
    private final int imgDescriptionSource;
    private final OnItemClickListener mListener;
    private DialogYesOrNoBinding binding;

    public YesOrNoDialog(Activity activity,
                         int theme,
                         String dialogDescription,
                         int imgDescriptionSource,
                         OnItemClickListener listener) {
        super(activity, theme);
        this.dialogDescription = dialogDescription;
        this.imgDescriptionSource = imgDescriptionSource;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogYesOrNoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView tvDescription = binding.textViewDeleteDialogDescription;
        ImageView imgDescription = binding.imageViewDescription;
        Button yes = binding.deleteConversationButton;
        Button no = binding.doNotDeleteConversationButton;
        ImageButton ibClose = binding.deleteConversationBackButton;

        tvDescription.setText(dialogDescription);
        imgDescription.setImageResource(imgDescriptionSource);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        ibClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.deleteConversationButton:
                mListener.onItemClickYes();
                break;
            case R.id.doNotDeleteConversationButton:
            case R.id.deleteConversationBackButton:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public interface OnItemClickListener {
        void onItemClickYes();
    }
}
