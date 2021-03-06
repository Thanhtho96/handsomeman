package com.tt.handsomeman.ui.customer.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.PayoutAdapter;
import com.tt.handsomeman.databinding.FragmentMoreCustomerBinding;
import com.tt.handsomeman.model.Customer;
import com.tt.handsomeman.model.Payout;
import com.tt.handsomeman.ui.AddNewPayout;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.ui.ChangePassword;
import com.tt.handsomeman.ui.MyProfile;
import com.tt.handsomeman.ui.Start;
import com.tt.handsomeman.ui.ViewPayout;
import com.tt.handsomeman.ui.customer.CustomerMainScreen;
import com.tt.handsomeman.util.CustomDividerItemDecoration;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.YesOrNoDialog;
import com.tt.handsomeman.viewmodel.CustomerViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CustomerMoreFragment extends BaseFragment<CustomerViewModel, FragmentMoreCustomerBinding> {
    private static final Integer REQUEST_MORE_FRAGMENT_MY_PROFILE_EDIT_RESULT_CODE = 7;
    private static final Integer REQUEST_MORE_CHANGE_PASSWORD = 0;
    private final List<Payout> payoutList = new ArrayList<>();
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    private ConstraintLayout viewMyProfileLayout;
    private TextView tvLogout, tvAccountName, tvMakeTransaction, tvViewTransferHistory, changePassword;
    private ImageView imgAvatar;
    private ImageButton ibAddPayout;
    private PayoutAdapter payoutAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(CustomerViewModel.class);
        viewBinding = FragmentMoreCustomerBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgAvatar = viewBinding.accountAvatar;
        viewMyProfileLayout = viewBinding.viewMyProfileLayout;
        tvLogout = viewBinding.logoutMore;
        tvAccountName = viewBinding.accountNameMore;
        ibAddPayout = viewBinding.imageButtonAddPayout;
        tvMakeTransaction = viewBinding.textViewMakeTransaction;
        tvViewTransferHistory = viewBinding.viewTransferHistory;
        changePassword = viewBinding.textViewChangePassword;

        createPayoutRecyclerView();
        fetchCustomerInfo();
        viewMyProfile();
        addPayout();
        changePassword();
        logOut();

        tvMakeTransaction.setOnClickListener(v -> {
            if (payoutList.size() == 0) {
                Toast.makeText(getContext(), getString(R.string.need_payout_to_make_transaction), Toast.LENGTH_SHORT).show();
            } else {
                getActivity().startActivityForResult(new Intent(getContext(), CustomerMakeTransaction.class), CustomerMainScreen.MADE_TRANSACTION_CODE);
            }
        });

        tvViewTransferHistory.setOnClickListener(v -> startActivity(new Intent(getContext(), CustomerTransferHistory.class)));
    }

    private void addPayout() {
        ibAddPayout.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), AddNewPayout.class), REQUEST_MORE_FRAGMENT_MY_PROFILE_EDIT_RESULT_CODE));
    }

    private void viewMyProfile() {
        viewMyProfileLayout.setOnClickListener(v -> startActivityForResult(new Intent(getContext(), MyProfile.class), REQUEST_MORE_FRAGMENT_MY_PROFILE_EDIT_RESULT_CODE));
    }

    private void fetchCustomerInfo() {
        String authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel.fetchCustomerInfo();
        baseViewModel.getCustomerProfileResponseMutableLiveData().observe(getViewLifecycleOwner(), customerProfileResponse -> {
            Customer customer = customerProfileResponse.getCustomer();
            tvAccountName.setText(customer.getName());

            GlideUrl glideUrl = new GlideUrl((customerProfileResponse.getAvatar()),
                    new LazyHeaders.Builder().addHeader("Authorization", authorizationCode).build());

            Glide.with(CustomerMoreFragment.this)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .placeholder(R.drawable.custom_progressbar)
                    .error(R.drawable.logo)
                    .signature(new MediaStoreSignature("", customerProfileResponse.getUpdateDate(), 0))
                    .into(imgAvatar);

            payoutList.clear();
            payoutList.addAll(customer.getAccount().getPayoutList());
            payoutAdapter.notifyDataSetChanged();
        });
    }

    private void createPayoutRecyclerView() {
        RecyclerView rcvPayout = viewBinding.recyclerViewPayoutMore;
        payoutAdapter = new PayoutAdapter(payoutList, getContext());
        payoutAdapter.setOnItemClickListener(position -> {
            Payout payout = payoutList.get(position);
            String lastPayoutNumber = payout.getAccountNumber().substring(payout.getAccountNumber().length() - 4);

            Intent intent = new Intent(getContext(), ViewPayout.class);
            intent.putExtra("lastPayoutNumber", lastPayoutNumber);
            intent.putExtra("payoutId", payout.getId());
            startActivityForResult(intent, REQUEST_MORE_FRAGMENT_MY_PROFILE_EDIT_RESULT_CODE);
        });
        RecyclerView.LayoutManager layoutManagerPayout = new LinearLayoutManager(getContext());
        rcvPayout.setLayoutManager(layoutManagerPayout);
        rcvPayout.setItemAnimator(new DefaultItemAnimator());
        rcvPayout.addItemDecoration(new CustomDividerItemDecoration(getContext(), R.drawable.recycler_view_divider));
        rcvPayout.setAdapter(payoutAdapter);
    }

    private void logOut() {
        tvLogout.setOnClickListener(v -> new YesOrNoDialog(getActivity(), R.style.PauseDialog, HandymanApp.getInstance().getString(R.string.want_to_log_out), R.drawable.log_out, () -> {
            String userId = sharedPreferencesUtils.get("userId", String.class);
            FirebaseMessaging.getInstance().unsubscribeFromTopic("notification-" + userId);
            sharedPreferencesUtils.clear();
            startActivity(new Intent(getContext(), Start.class));
            getActivity().finish();
        }).show());
    }

    private void changePassword() {
        changePassword.setOnClickListener(view -> startActivityForResult(new Intent(getContext(), ChangePassword.class), REQUEST_MORE_CHANGE_PASSWORD));
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 @Nullable Intent data) {

        if (requestCode == REQUEST_MORE_FRAGMENT_MY_PROFILE_EDIT_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("isMoreFragmentEdit", false)) {
                fetchCustomerInfo();
            }
        }
        if (requestCode == REQUEST_MORE_CHANGE_PASSWORD && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getBooleanExtra("isChangePassword", false)) {
                String userId = sharedPreferencesUtils.get("userId", String.class);
                FirebaseMessaging.getInstance().unsubscribeFromTopic("notification-" + userId);
                sharedPreferencesUtils.clear();
                startActivity(new Intent(getContext(), Start.class));
                getActivity().finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
