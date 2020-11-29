package com.tt.handsomeman.ui.handyman;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.tt.handsomeman.HandymanApp;
import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.SpinnerString;
import com.tt.handsomeman.databinding.ActivitySignUpAddPayoutBinding;
import com.tt.handsomeman.request.UserActivatingAccount;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.service.UserService;
import com.tt.handsomeman.ui.BaseAppCompatActivity;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.util.StatusCodeConstant;
import com.tt.handsomeman.util.StatusConstant;
import com.tt.handsomeman.viewmodel.SignUpAddPayoutViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpAddPayout extends BaseAppCompatActivity {
    private final Calendar myCalendar = Calendar.getInstance();
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    @Inject
    UserService userService;
    private String[] type, country;
    private EditText edtBirthday;
    private ImageButton ibBirthDay, ibCheck;
    private SignUpAddPayoutViewModel signUpAddPayoutViewModel;
    private ActivitySignUpAddPayoutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpAddPayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final EditText edtFirstName = binding.editTextFirstNameSignUpPayout;
        final EditText edtLastName = binding.editTextLastNameSignUpPayout;
        final EditText edtAddress = binding.editTextAddressSignUpPayout;
        final EditText edtPortalCode = binding.editTextPortalCodeSignUpPayout;
        final EditText edtEmail = binding.editTextEmailSignUpPayout;
        final EditText edtAccountNumber = binding.editTextAccountNumberSignUpPayout;
        final EditText edtAccountRouting = binding.editTextAccountRoutingSignUpPayout;
        edtBirthday = binding.editTextBirthdaySignUpPayout;
        FrameLayout progressBarHolder = binding.progressBarHolder;

        type = getResources().getStringArray(R.array.type_array);
        Spinner spinnerType = binding.spinnerTypePayout;
        country = getResources().getStringArray(R.array.countries_array);
        Spinner spinnerCountry = binding.spinnerCountryPayout;

        ibBirthDay = binding.imageButtonSignUpBirthday;
        ibCheck = binding.imageButtonCheckSignUpPayout;
        ibCheck.setEnabled(false);

        HandymanApp.getComponent().inject(this);
        signUpAddPayoutViewModel = new ViewModelProvider(this).get(SignUpAddPayoutViewModel.class);

        binding.signUpPayoutBackButton.setOnClickListener(view -> onBackPressed());

        observeSignUpAddPayoutState(edtFirstName, edtLastName, edtAddress, edtPortalCode, edtEmail, edtAccountNumber, edtAccountRouting);

        edtChangedListener(edtFirstName, edtLastName, edtAddress, edtPortalCode, edtEmail, edtAccountNumber, edtAccountRouting);

        final DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };
        showBirthdayPickerDialog(date);

        generateTypeSpinner(spinnerType);
        generateCountrySpinner(spinnerCountry);

        doAddPayout(edtFirstName, edtLastName, edtAddress, edtPortalCode, edtEmail, edtAccountNumber, edtAccountRouting, progressBarHolder, spinnerType, spinnerCountry);
    }

    private void doAddPayout(EditText edtFirstName,
                             EditText edtLastName,
                             EditText edtAddress,
                             EditText edtPortalCode,
                             EditText edtEmail,
                             EditText edtAccountNumber,
                             EditText edtAccountRouting,
                             FrameLayout progressBarHolder,
                             Spinner spinnerType,
                             Spinner spinnerCountry) {
        ibCheck.setOnClickListener(view -> {

            AlphaAnimation inAnimation;

            progressBarHolder.bringToFront();
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(300);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);

            ibCheck.setEnabled(false);

            String token = sharedPreferencesUtils.get("token", String.class);
            String locale = Constants.language.getValue();

            String myFormat = "yyyy-MM-dd"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

            String firstName = edtFirstName.getText().toString();
            String lastName = edtLastName.getText().toString();
            String address = edtAddress.getText().toString();
            Integer portalCode = Integer.parseInt(edtPortalCode.getText().toString());
            String birthday = sdf.format(myCalendar.getTime());

            String selectedType;
            switch (spinnerType.getSelectedItemPosition()) {
                case 0:
                    selectedType = "Individual";
                    break;
                case 1:
                    selectedType = "Business";
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + spinnerType.getSelectedItemPosition());
            }

            String email = edtEmail.getText().toString();
            String accountNumber = edtAccountNumber.getText().toString();
            String accountRouting = edtAccountRouting.getText().toString();
            String selectedCountry = country[spinnerCountry.getSelectedItemPosition()];

            String kindOfHandyman = "CPR";

            UserActivatingAccount userActivatingAccount = new UserActivatingAccount(firstName, lastName, address, portalCode, birthday, selectedType, email, accountNumber, accountRouting, selectedCountry);

            userService.doSignUpAddPayout(locale, token, userActivatingAccount, kindOfHandyman).enqueue(new Callback<StandardResponse>() {
                @Override
                public void onResponse(@NonNull Call<StandardResponse> call,
                                       @NonNull Response<StandardResponse> response) {
                    if (response.body().getStatus().equals(StatusConstant.OK) && response.body().getStatusCode().equals(StatusCodeConstant.OK)) {
                        Toast.makeText(SignUpAddPayout.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        sharedPreferencesUtils.put("state", Constants.STATE_REGISTER_ADDED_PAYOUT);
                        startActivity(new Intent(SignUpAddPayout.this, HandyManMainScreen.class));
                        finish();
                    } else {
                        AlphaAnimation outAnimation;

                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        progressBarHolder.setAnimation(outAnimation);
                        progressBarHolder.setVisibility(View.GONE);
                        ibCheck.setEnabled(true);
                        Toast.makeText(SignUpAddPayout.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<StandardResponse> call,
                                      @NonNull Throwable t) {
                    AlphaAnimation outAnimation;

                    outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    progressBarHolder.setAnimation(outAnimation);
                    progressBarHolder.setVisibility(View.GONE);
                    ibCheck.setEnabled(true);
                    Toast.makeText(SignUpAddPayout.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void showBirthdayPickerDialog(DatePickerDialog.OnDateSetListener date) {
        ibBirthDay.setOnClickListener(view -> showDatePickerDialog(date));

        edtBirthday.setOnClickListener(v -> showDatePickerDialog(date));
    }

    private void edtChangedListener(EditText edtFirstName,
                                    EditText edtLastName,
                                    EditText edtAddress,
                                    EditText edtPortalCode,
                                    EditText edtEmail,
                                    EditText edtAccountNumber,
                                    EditText edtAccountRouting) {
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence,
                                          int i,
                                          int i1,
                                          int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence,
                                      int i,
                                      int i1,
                                      int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                signUpAddPayoutViewModel.signUpPayOutDateChanged(edtFirstName.getText().toString(), edtLastName.getText().toString(), edtAddress.getText().toString(),
                        edtPortalCode.getText().toString(), edtEmail.getText().toString(), edtAccountNumber.getText().toString(),
                        edtAccountRouting.getText().toString(), edtBirthday.getText().toString());
            }
        };

        edtFirstName.addTextChangedListener(textWatcher);
        edtLastName.addTextChangedListener(textWatcher);
        edtAddress.addTextChangedListener(textWatcher);
        edtPortalCode.addTextChangedListener(textWatcher);
        edtEmail.addTextChangedListener(textWatcher);
        edtAccountNumber.addTextChangedListener(textWatcher);
        edtAccountRouting.addTextChangedListener(textWatcher);
    }

    private void observeSignUpAddPayoutState(EditText edtFirstName,
                                             EditText edtLastName,
                                             EditText edtAddress,
                                             EditText edtPortalCode,
                                             EditText edtEmail,
                                             EditText edtAccountNumber,
                                             EditText edtAccountRouting) {
        signUpAddPayoutViewModel.getUpAddPayoutFormState().observe(this, signUpAddPayoutFormState -> {
            if (signUpAddPayoutFormState == null) {
                return;
            }
            ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
            if (signUpAddPayoutFormState.getFirstNameError() != null) {
                ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
                edtFirstName.setError(getString(signUpAddPayoutFormState.getFirstNameError()));
            }
            if (signUpAddPayoutFormState.getLastNameError() != null) {
                ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
                edtLastName.setError(getString(signUpAddPayoutFormState.getLastNameError()));
            }
            if (signUpAddPayoutFormState.getAddressError() != null) {
                ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
                edtAddress.setError(getString(signUpAddPayoutFormState.getAddressError()));
            }
            if (signUpAddPayoutFormState.getPortalCodeError() != null) {
                ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
                edtPortalCode.setError(getString(signUpAddPayoutFormState.getPortalCodeError()));
            }
            if (signUpAddPayoutFormState.getEmailError() != null) {
                ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
                edtEmail.setError(getString(signUpAddPayoutFormState.getEmailError()));
            }
            if (signUpAddPayoutFormState.getAccountNumberError() != null) {
                ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
                edtAccountNumber.setError(getString(signUpAddPayoutFormState.getAccountNumberError()));
            }
            if (signUpAddPayoutFormState.getAccountRoutingError() != null) {
                ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
                edtAccountRouting.setError(getString(signUpAddPayoutFormState.getAccountRoutingError()));
            }
            if (signUpAddPayoutFormState.getBirthdayError() != null) {
                ibCheck.setEnabled(signUpAddPayoutFormState.isDataValid());
                edtBirthday.setError(getString(signUpAddPayoutFormState.getBirthdayError()));
            }
        });
    }

    private void generateTypeSpinner(Spinner spinnerType) {
        SpinnerString spinnerTypePayout = new SpinnerString(SignUpAddPayout.this, type);
        spinnerType.setAdapter(spinnerTypePayout);
    }

    private void generateCountrySpinner(Spinner spinnerCountry) {
        SpinnerString spinnerCountryPayout = new SpinnerString(SignUpAddPayout.this, country);
        spinnerCountry.setAdapter(spinnerCountryPayout);
    }

//    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener date) {
//        new DatePickerDialog(SignUpAddPayout.this, date, myCalendar
//                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//    }

    private void showDatePickerDialog(DatePickerDialog.OnDateSetListener date) {
        new DatePickerDialog(SignUpAddPayout.this, date, 1996, 11, 26).show();
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        edtBirthday.setText(sdf.format(myCalendar.getTime()));
        edtBirthday.setError(null);
    }
}
