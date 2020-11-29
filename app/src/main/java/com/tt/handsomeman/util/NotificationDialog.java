package com.tt.handsomeman.util;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.DialogNotificationBinding;

public class NotificationDialog extends Dialog implements View.OnClickListener {

    private final String dialogDescription;
    private final OnItemClickListener mListener;
    private DialogNotificationBinding binding;

    public NotificationDialog(Activity activity,
                              int theme,
                              String dialogDescription,
                              OnItemClickListener listener) {
        super(activity, theme);
        this.dialogDescription = dialogDescription;
        mListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView tvDescription = binding.textViewNotificationDialogDescription;
        Button ok = binding.okDialogButton;

        tvDescription.setText(dialogDescription);
        ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.okDialogButton) {
            mListener.onItemClickOk();
        }
        dismiss();
    }

    @Override
    public void onBackPressed() {
        mListener.onItemClickOk();
    }

    public interface OnItemClickListener {
        void onItemClickOk();
    }
}
