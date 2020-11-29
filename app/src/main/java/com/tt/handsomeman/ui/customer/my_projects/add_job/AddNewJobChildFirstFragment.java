package com.tt.handsomeman.ui.customer.my_projects.add_job;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.CategorySelectionAdapter;
import com.tt.handsomeman.adapter.SpinnerPercentage;
import com.tt.handsomeman.adapter.SpinnerString;
import com.tt.handsomeman.databinding.FragmentAddNewJobChildFirstBinding;
import com.tt.handsomeman.model.Category;
import com.tt.handsomeman.request.AddJobRequest;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.AddJobFirstFormViewModel;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class AddNewJobChildFirstFragment extends BaseFragment<CustomerViewModel, FragmentAddNewJobChildFirstBinding> {

    private final List<Category> categoryList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private EditText edtBudgetMin, edtBudgetMax, edtTitle, edtDetail;
    private ImageButton ibCheckFirst;
    private Spinner spPaymentMileStone, spFirstPaymentMilestone, spSecondPaymentMilestone;
    private RecyclerView rcvCategories;
    private ViewPager2 viewPager;
    private LinearLayout layoutSecondPaymentMilestone;
    private AddJobFirstFormViewModel formViewModel;
    private String[] paymentMilestone, paymentMilestonePercentages;
    private CategorySelectionAdapter categorySelectionAdapter;
    private AddJobRequest addJobRequest;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);
        formViewModel = new ViewModelProvider(this).get(AddJobFirstFormViewModel.class);
        viewBinding = FragmentAddNewJobChildFirstBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView();
        createCategoryRecyclerView();
        getListCategory();
        editChangeListener(edtBudgetMin, edtBudgetMax, edtTitle, edtDetail);
        observeFormState(edtBudgetMin, edtBudgetMax, edtTitle, edtDetail, ibCheckFirst);
        generateSpinnerMilestone(spPaymentMileStone);
        spinnerMilestoneItemSelectListener(spPaymentMileStone, spFirstPaymentMilestone, spSecondPaymentMilestone);
        sendData();
    }

    private void sendData() {
        ibCheckFirst.setOnClickListener(v -> {
            if (categorySelectionAdapter.getSelected() != null) {
                addJobRequest.setCategoryId(categorySelectionAdapter.getSelected().getCategory_id());
                addJobRequest.setTitle(edtTitle.getText().toString().trim());
                addJobRequest.setDetail(edtDetail.getText().toString().trim());
                addJobRequest.setBudgetMin(Integer.parseInt(edtBudgetMin.getText().toString().trim()));
                addJobRequest.setBudgetMax(Integer.parseInt(edtBudgetMax.getText().toString().trim()));
                addJobRequest.setCustomerId(Integer.parseInt(sharedPreferencesUtils.get("userId", String.class)));

                ArrayList<Integer> paymentMileStone = new ArrayList<>();

                if (paymentMilestonePercentages.length > 1) {
                    String firstPercentage = paymentMilestonePercentages[spFirstPaymentMilestone.getSelectedItemPosition()];
                    paymentMileStone.add(Integer.parseInt(firstPercentage));
                    String secondPercentage = paymentMilestonePercentages[spSecondPaymentMilestone.getSelectedItemPosition()];
                    paymentMileStone.add(Integer.parseInt(secondPercentage));
                } else {
                    String firstPercentage = paymentMilestonePercentages[spFirstPaymentMilestone.getSelectedItemPosition()];
                    paymentMileStone.add(Integer.parseInt(firstPercentage));
                }

                addJobRequest.setPercentages(paymentMileStone);

                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });
    }

    private void spinnerMilestoneItemSelectListener(Spinner spPaymentMileStone,
                                                    Spinner spFirstPaymentMilestone,
                                                    Spinner spSecondPaymentMilestone) {
        spPaymentMileStone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                if (position == 1) {
                    layoutSecondPaymentMilestone.setVisibility(View.VISIBLE);

                    paymentMilestonePercentages = getResources().getStringArray(R.array.percentages);
                    SpinnerPercentage spinnerAdapter = new SpinnerPercentage(getContext(), paymentMilestonePercentages);

                    spFirstPaymentMilestone.setAdapter(spinnerAdapter);
                    spFirstPaymentMilestone.setSelection(4);

                    spSecondPaymentMilestone.setAdapter(spinnerAdapter);
                    spSecondPaymentMilestone.setSelection(4);
                } else {
                    layoutSecondPaymentMilestone.setVisibility(View.GONE);

                    paymentMilestonePercentages = new String[]{"100"};
                    SpinnerPercentage spinnerTypePayout = new SpinnerPercentage(getContext(), paymentMilestonePercentages);
                    spFirstPaymentMilestone.setAdapter(spinnerTypePayout);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spFirstPaymentMilestone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                if (paymentMilestonePercentages.length > 1) {
                    spSecondPaymentMilestone.setSelection(paymentMilestonePercentages.length - 1 - position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spSecondPaymentMilestone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {
                if (paymentMilestonePercentages.length > 1) {
                    spFirstPaymentMilestone.setSelection(paymentMilestonePercentages.length - 1 - position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void generateSpinnerMilestone(Spinner spPaymentMileStone) {
        SpinnerString spinnerAdapter = new SpinnerString(getContext(), paymentMilestone);
        spPaymentMileStone.setAdapter(spinnerAdapter);
    }

    private void bindView() {
        edtBudgetMin = viewBinding.budgetFrom;
        edtBudgetMax = viewBinding.budgetTo;
        edtTitle = viewBinding.editTextTitle;
        edtDetail = viewBinding.editTextDetail;
        rcvCategories = viewBinding.recyclerCategories;
        spPaymentMileStone = viewBinding.spinnerPaymentMilestones;
        spFirstPaymentMilestone = viewBinding.spinnerFirstPaymentMilestones;
        spSecondPaymentMilestone = viewBinding.spinnerSecondPaymentMilestones;
        layoutSecondPaymentMilestone = viewBinding.layoutSecondPaymentMilestone;

        AddNewJob addNewJob = (AddNewJob) requireActivity();
        ibCheckFirst = addNewJob.viewBinding.imageButtonCheckFirst;
        viewPager = addNewJob.viewBinding.viewPager;
        addJobRequest = addNewJob.addJobRequest;

        paymentMilestone = new String[]{"1", "2"};
    }

    private void observeFormState(EditText edtBudgetMin,
                                  EditText edtBudgetMax,
                                  EditText edtTitle,
                                  EditText edtDetail,
                                  ImageButton ibCheckFirst) {
        formViewModel.getFormStateMutableLiveData().observe(getViewLifecycleOwner(), addJobFirstFormState -> {
            if (addJobFirstFormState == null) {
                return;
            }
            ibCheckFirst.setEnabled(addJobFirstFormState.isDataValid());
            if (addJobFirstFormState.getBudgetMinError() != null) {
                edtBudgetMin.setError(getString(addJobFirstFormState.getBudgetMinError()));
            }
            if (addJobFirstFormState.getBudgetMaxError() != null) {
                edtBudgetMax.setError(getString(addJobFirstFormState.getBudgetMaxError()));
            }
            if (addJobFirstFormState.getTitleError() != null) {
                edtTitle.setError(getString(addJobFirstFormState.getTitleError()));
            }
            if (addJobFirstFormState.getDetailError() != null) {
                edtDetail.setError(getString(addJobFirstFormState.getDetailError()));
            }
        });
    }

    private void editChangeListener(EditText edtBudgetMin,
                                    EditText edtBudgetMax,
                                    EditText edtTitle,
                                    EditText edtDetail) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                formViewModel.addJobFirmFormChanged(
                        edtBudgetMin.getText().toString().trim(),
                        edtBudgetMax.getText().toString().trim(),
                        edtTitle.getText().toString().trim(),
                        edtDetail.getText().toString().trim());
            }
        };

        edtBudgetMin.addTextChangedListener(textWatcher);
        edtBudgetMax.addTextChangedListener(textWatcher);
        edtTitle.addTextChangedListener(textWatcher);
        edtDetail.addTextChangedListener(textWatcher);
    }

    private void createCategoryRecyclerView() {
        categorySelectionAdapter = new CategorySelectionAdapter(getContext(), categoryList);
        RecyclerView.LayoutManager layoutManagerPayout = new LinearLayoutManager(getContext());
        rcvCategories.setLayoutManager(layoutManagerPayout);
        rcvCategories.setItemAnimator(new DefaultItemAnimator());
        rcvCategories.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider));
        rcvCategories.setAdapter(categorySelectionAdapter);
    }

    private void getListCategory() {
        baseViewModel.fetchListCategory();
        baseViewModel.getListCategoryMutableLiveData().observe(getViewLifecycleOwner(), listCategory -> {
            categoryList.clear();
            categoryList.addAll(listCategory.getCategoryList());
            categorySelectionAdapter.notifyDataSetChanged();
        });
    }
}
