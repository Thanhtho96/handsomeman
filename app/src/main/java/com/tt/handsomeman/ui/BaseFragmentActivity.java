package com.tt.handsomeman.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewbinding.ViewBinding;

import com.tt.handsomeman.BuildConfig;
import com.tt.handsomeman.viewmodel.BaseViewModel;

public abstract class BaseFragmentActivity<T extends BaseViewModel, Tx extends ViewBinding> extends FragmentActivity {
    public Tx viewBinding;
    protected T baseViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getWindow().getDecorView();
        view.setSystemUiVisibility
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public void onDestroy() {
        viewBinding = null;
        baseViewModel.clearSubscriptions(this.getClass().getName().replace(BuildConfig.APPLICATION_ID, ""));
        super.onDestroy();
    }
}
