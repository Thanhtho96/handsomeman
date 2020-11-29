package com.tt.handsomeman.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tt.handsomeman.R;
import com.tt.handsomeman.model.AddJobFirstFormState;

public class AddJobFirstFormViewModel extends ViewModel {

    private final MutableLiveData<AddJobFirstFormState> formStateMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<AddJobFirstFormState> getFormStateMutableLiveData() {
        return formStateMutableLiveData;
    }

    public void addJobFirmFormChanged(String budgetMin,
                                      String budgetMax,
                                      String title,
                                      String detail) {
        if (!isBudgetMinValid(budgetMin)) {
            formStateMutableLiveData.setValue(new AddJobFirstFormState(R.string.is_null, null, null, null));
        }
        if (!isBudgetMaxValid(budgetMax)) {
            formStateMutableLiveData.setValue(new AddJobFirstFormState(null, R.string.is_null, null, null));
        }
        if (!isBudgetRangeValid(budgetMin, budgetMax)) {
            formStateMutableLiveData.setValue(new AddJobFirstFormState(null, R.string.budget_max_smaller, null, null));
        }
        if (!isTitleValid(title)) {
            formStateMutableLiveData.setValue(new AddJobFirstFormState(null, null, R.string.title_error, null));
        }
        if (!isDetailValid(detail)) {
            formStateMutableLiveData.setValue(new AddJobFirstFormState(null, null, null, R.string.detail_error));
        }
        if (isBudgetMinValid(budgetMin) && isBudgetMaxValid(budgetMax) && isBudgetRangeValid(budgetMin, budgetMax) && isTitleValid(title) && isDetailValid(detail)) {
            formStateMutableLiveData.setValue(new AddJobFirstFormState(true));
        }
    }

    private boolean isBudgetMinValid(String budgetMin) {
        return budgetMin != null && !budgetMin.matches("");
    }

    private boolean isBudgetMaxValid(String budgetMax) {
        return budgetMax != null && !budgetMax.matches("");
    }

    private boolean isBudgetRangeValid(String budgetMin,
                                       String budgetMax) {
        boolean isValid = true;
        if (!budgetMin.matches("") && !budgetMax.matches("")) {
            isValid = Integer.parseInt(budgetMin) < Integer.parseInt(budgetMax);
        }
        return isValid;
    }

    private boolean isTitleValid(String title) {
        return title != null && 7 < title.trim().length() && title.trim().length() < 100;
    }

    private boolean isDetailValid(String detail) {
        return detail != null && 17 < detail.trim().length() && detail.trim().length() < 255;
    }
}
