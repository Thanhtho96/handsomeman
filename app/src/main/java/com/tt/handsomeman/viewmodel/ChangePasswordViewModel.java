package com.tt.handsomeman.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tt.handsomeman.R;
import com.tt.handsomeman.model.ChangePasswordFormState;

public class ChangePasswordViewModel extends ViewModel {
    private final MutableLiveData<ChangePasswordFormState> changePasswordFormStateMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<ChangePasswordFormState> getChangePasswordFormStateMutableLiveData() {
        return changePasswordFormStateMutableLiveData;
    }

    public void changePasswordStateChanged(String currentPassword,
                                           String newPassword,
                                           String rePassword) {
        if (!isCurrentPasswordValid(currentPassword)) {
            changePasswordFormStateMutableLiveData.setValue(new ChangePasswordFormState(R.string.not_valid_password, null, null, false));
        }
        if (!isNewPasswordValid(newPassword)) {
            changePasswordFormStateMutableLiveData.setValue(new ChangePasswordFormState(null, R.string.not_valid_password, null, false));
        }
        if (!isNewPasswordDifferent(currentPassword, newPassword)) {
            changePasswordFormStateMutableLiveData.setValue(new ChangePasswordFormState(null, R.string.new_password_not_different, null, false));
        }
        if (!isMatchPassword(newPassword, rePassword)) {
            changePasswordFormStateMutableLiveData.setValue(new ChangePasswordFormState(null, null, R.string.password_not_match, false));
        }
        if (isCurrentPasswordValid(currentPassword) && isNewPasswordDifferent(currentPassword, newPassword) && isNewPasswordValid(newPassword) && isMatchPassword(newPassword, rePassword)) {
            changePasswordFormStateMutableLiveData.setValue(new ChangePasswordFormState(true));
        }
    }

    private boolean isCurrentPasswordValid(String currentPassword) {
        return currentPassword != null && currentPassword.trim().length() > 5;
    }

    private boolean isNewPasswordValid(String newPassword) {
        return newPassword != null && newPassword.trim().length() > 5;
    }

    private boolean isMatchPassword(String newPassword,
                                    String rePassword) {
        return rePassword == null || rePassword.equals(newPassword);
    }

    private boolean isNewPasswordDifferent(String currentPassword,
                                           String newPassword) {
        return newPassword != null && !newPassword.equals(currentPassword);
    }
}
