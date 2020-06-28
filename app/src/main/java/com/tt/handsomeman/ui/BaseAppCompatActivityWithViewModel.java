package com.tt.handsomeman.ui;

import com.tt.handsomeman.BuildConfig;
import com.tt.handsomeman.viewmodel.BaseViewModel;

public abstract class BaseAppCompatActivityWithViewModel<T extends BaseViewModel> extends BaseAppCompatActivity {
    protected T baseViewModel;

    @Override
    protected void onDestroy() {
        baseViewModel.clearSubscriptions(this.getClass().getName().replace(BuildConfig.APPLICATION_ID, ""));
        super.onDestroy();
    }
}
