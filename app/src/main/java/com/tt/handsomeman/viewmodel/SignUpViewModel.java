package com.tt.handsomeman.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tt.handsomeman.R;
import com.tt.handsomeman.model.SignUpFormState;

public class SignUpViewModel extends ViewModel {

    private final MutableLiveData<SignUpFormState> signUpFormState = new MutableLiveData<>();

    public LiveData<SignUpFormState> getSignUpFormState() {
        return signUpFormState;
    }

    public void signUpDateChanged(String name,
                                  String mail,
                                  String password,
                                  String rePassword) {
        if (!(isNameValid(name))) {
            signUpFormState.setValue(new SignUpFormState(R.string.not_valid_name, null, null, null));
        }
        if (!(isMailValid(mail))) {
            signUpFormState.setValue(new SignUpFormState(null, R.string.not_valid_mail, null, null));
        }
        if (!(isPasswordValid(password))) {
            signUpFormState.setValue(new SignUpFormState(null, null, R.string.not_valid_password, null));
        }
        if (!(isMatchPassword(password, rePassword))) {
            signUpFormState.setValue(new SignUpFormState(null, null, null, R.string.password_not_match));
        }
        if (isNameValid(name) && isMailValid(mail) && isPasswordValid(password) && isMatchPassword(password, rePassword)) {
            signUpFormState.setValue(new SignUpFormState(true));
        }
    }

    private boolean isNameValid(String name) {
        return name != null && name.trim().length() > 4;
    }

    private boolean isMailValid(String mail) {
        if (mail == null) {
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS.matcher(mail).matches())) {
            return false;
        } else {
            return !mail.trim().isEmpty();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean isMatchPassword(String password,
                                    String rePassword) {
        return rePassword == null || rePassword.equals(password);
    }
}
