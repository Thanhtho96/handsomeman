package com.tt.handsomeman.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tt.handsomeman.R;
import com.tt.handsomeman.model.SignUpAddPayoutFormState;

public class SignUpAddPayoutViewModel extends ViewModel {
    private final MutableLiveData<SignUpAddPayoutFormState> liveData = new MutableLiveData<>();

    public LiveData<SignUpAddPayoutFormState> getUpAddPayoutFormState() {
        return liveData;
    }

    public void signUpPayOutDateChanged(String firstName,
                                        String lastName,
                                        String address,
                                        String portalCode,
                                        String email,
                                        String accountNumber,
                                        String accountRouting,
                                        String birthday) {
        if (!(isFirstNameValid(firstName))) {
            liveData.setValue(new SignUpAddPayoutFormState(R.string.not_valid_first_name, null, null, null, null, null, null, null));
        }
        if (!(isLastNameValid(lastName))) {
            liveData.setValue(new SignUpAddPayoutFormState(null, R.string.not_valid_last_name, null, null, null, null, null, null));
        }
        if (!(isAddressValid(address))) {
            liveData.setValue(new SignUpAddPayoutFormState(null, null, R.string.not_valid_address, null, null, null, null, null));
        }
        if (!(isPortalCodeValid(portalCode))) {
            liveData.setValue(new SignUpAddPayoutFormState(null, null, null, R.string.not_valid_portal_code, null, null, null, null));
        }
        if (!(isEmailValid(email))) {
            liveData.setValue(new SignUpAddPayoutFormState(null, null, null, null, R.string.not_valid_email, null, null, null));
        }
        if (!(isAccountNumberValid(accountNumber))) {
            liveData.setValue(new SignUpAddPayoutFormState(null, null, null, null, null, R.string.not_valid_account_number, null, null));
        }
        if (!(isAccountRoutingValid(accountRouting))) {
            liveData.setValue(new SignUpAddPayoutFormState(null, null, null, null, null, null, R.string.not_valid_account_routing, null));
        }
        if (!(isBirthdayValid(birthday))) {
            liveData.setValue(new SignUpAddPayoutFormState(null, null, null, null, null, null, null, R.string.not_valid_birthday));
        }
        if (isFirstNameValid(firstName) && isLastNameValid(lastName) && isAddressValid(address) && isPortalCodeValid(portalCode) && isEmailValid(email) && isAccountNumberValid(accountNumber) && isAccountRoutingValid(accountRouting) && isBirthdayValid(birthday)) {
            liveData.setValue(new SignUpAddPayoutFormState(true));
        }
    }

    private boolean isFirstNameValid(String firstName) {
        return firstName != null && firstName.trim().length() > 2;
    }

    private boolean isLastNameValid(String lastName) {
        return lastName != null && lastName.trim().length() > 2;
    }

    private boolean isAddressValid(String address) {
        return address != null && address.trim().length() > 3;
    }

    private boolean isPortalCodeValid(String portalCode) {
        return portalCode != null && portalCode.trim().length() == 6;
    }

    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS.matcher(email).matches())) {
            return false;
        } else {
            return !email.trim().isEmpty();
        }
    }

    private boolean isAccountNumberValid(String accountNumber) {
        return accountNumber != null && accountNumber.trim().length() > 11;
    }

    private boolean isAccountRoutingValid(String accountRouting) {
        return accountRouting != null && accountRouting.trim().length() == 9;
    }

    private boolean isBirthdayValid(String birthday) {
        return birthday != null && birthday.trim().length() > 6;
    }
}
