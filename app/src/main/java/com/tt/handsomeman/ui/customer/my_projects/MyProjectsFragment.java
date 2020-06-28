package com.tt.handsomeman.ui.customer.my_projects;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.databinding.FragmentMyProjectsBinding;
import com.tt.handsomeman.model.Job;
import com.tt.handsomeman.response.MyProjectList;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.util.List;

import javax.inject.Inject;

public class MyProjectsFragment extends BaseFragment<CustomerViewModel, FragmentMyProjectsBinding> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    MutableLiveData<List<Job>> inProgressList = new MutableLiveData<>();
    MutableLiveData<List<Job>> inPastList = new MutableLiveData<>();
    private Fragment childInProgressFragment = new CustomerMyProjectsChildInProgressFragment();
    private Fragment childInPastFragment = new MyProjectsChildInPastFragment();
    private Fragment active = childInProgressFragment;
    private EditText edtSearchByWord;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);
        viewBinding = FragmentMyProjectsBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RadioButton rdInProgress = viewBinding.radioButtonInProgress;
        RadioButton rdInPast = viewBinding.radioButtonInPast;
        edtSearchByWord = viewBinding.editTextSearchByWordMyProjectFragment;

        final FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().add(R.id.myProjectFragmentParent, childInPastFragment).hide(childInPastFragment).commit();
        fm.beginTransaction().add(R.id.myProjectFragmentParent, childInProgressFragment).commit();

        setEditTextHintTextAndIcon();

        rdInProgress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    fm.beginTransaction().hide(active).show(childInProgressFragment).commit();
                    active = childInProgressFragment;
                }
            }
        });

        rdInPast.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    fm.beginTransaction().hide(active).show(childInPastFragment).commit();
                    active = childInPastFragment;
                }
            }
        });

        fetchData();
    }

    void fetchData() {
        String authorization = sharedPreferencesUtils.get("token", String.class);

        baseViewModel.fetchJobsOfCustomer(authorization);
        baseViewModel.getMyProjectListMutableLiveData().observe(getViewLifecycleOwner(), new Observer<MyProjectList>() {
            @Override
            public void onChanged(MyProjectList myProjectList) {
                inProgressList.setValue(myProjectList.getInProgressList());
                inPastList.setValue(myProjectList.getInPastList());
            }
        });
    }

    private void setEditTextHintTextAndIcon() {
        ImageSpan imageHint = new ImageSpan(getContext(), R.drawable.ic_search_19dp);
        SpannableString spannableString = new SpannableString("    " + getResources().getString(R.string.search_by_word));
        spannableString.setSpan(imageHint, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        edtSearchByWord.setHint(spannableString);
    }
}
