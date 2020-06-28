package com.tt.handsomeman.ui.messages;

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
import com.tt.handsomeman.databinding.FragmentMessagesBinding;
import com.tt.handsomeman.response.Contact;
import com.tt.handsomeman.response.ConversationResponse;
import com.tt.handsomeman.response.DataBracketResponse;
import com.tt.handsomeman.response.ListConversation;
import com.tt.handsomeman.ui.BaseFragment;
import com.tt.handsomeman.util.SharedPreferencesUtils;
import com.tt.handsomeman.viewmodel.MessageViewModel;

import java.util.List;

import javax.inject.Inject;

public class MessagesFragment extends BaseFragment<MessageViewModel, FragmentMessagesBinding> {

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    SharedPreferencesUtils sharedPreferencesUtils;
    MutableLiveData<List<ConversationResponse>> conversationList = new MutableLiveData<>();
    MutableLiveData<List<Contact>> contactList = new MutableLiveData<>();
    String authorizationCode;
    private Fragment childMessagesFragment = new MessagesChildMessagesFragment();
    private Fragment childContactsFragment = new MessagesChildContactsFragment();
    private Fragment active = childMessagesFragment;
    private EditText edtSearchByWord;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        HandymanApp.getComponent().inject(this);
        authorizationCode = sharedPreferencesUtils.get("token", String.class);
        baseViewModel = new ViewModelProvider(this, viewModelFactory).get(MessageViewModel.class);
        viewBinding = FragmentMessagesBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioButton rdMessage = viewBinding.radioButtonMessages;
        RadioButton rdContact = viewBinding.radioButtonContacts;
        edtSearchByWord = viewBinding.editTextSearchByWordMessageFragment;

        final FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction().add(R.id.messageFragmentParent, childContactsFragment).hide(childContactsFragment).commit();
        fm.beginTransaction().add(R.id.messageFragmentParent, childMessagesFragment).commit();

        setEditTextHintTextAndIcon();

        rdMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    fm.beginTransaction().hide(active).show(childMessagesFragment).commit();
                    active = childMessagesFragment;
                }
            }
        });

        rdContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    fm.beginTransaction().hide(active).show(childContactsFragment).commit();
                    active = childContactsFragment;
                }
            }
        });

        fetchData();
    }

    private void fetchData() {
        String type = sharedPreferencesUtils.get("type", String.class);

        baseViewModel.fetchAllConversationByAccountId(authorizationCode, type);
        baseViewModel.getListConversation().observe(getViewLifecycleOwner(), new Observer<DataBracketResponse<ListConversation>>() {
            @Override
            public void onChanged(DataBracketResponse<ListConversation> listConversationDataBracketResponse) {
                conversationList.setValue(listConversationDataBracketResponse.getData().getConversationList());
                contactList.setValue(listConversationDataBracketResponse.getData().getContactList());
            }
        });
    }

    private void setEditTextHintTextAndIcon() {
        ImageSpan imageHint = new ImageSpan(getContext(), R.drawable.ic_search_19dp);
        SpannableString spannableString = new SpannableString("    " + getResources().getString(R.string.search_by_word));
        spannableString.setSpan(imageHint, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        edtSearchByWord.setHint(spannableString);
    }

    @Override
    public void onDestroyView() {
        viewBinding = null;
        super.onDestroyView();
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }
}