package com.tt.handsomeman.ui.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tt.handsomeman.R;
import com.tt.handsomeman.adapter.ContactAdapter;
import com.tt.handsomeman.databinding.FragmentMessagesChildContactsBinding;
import com.tt.handsomeman.response.Contact;
import com.tt.handsomeman.util.ContactDivider;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class MessagesChildContactsFragment extends Fragment {

    private final List<Contact> contactList = new ArrayList<>();
    private ContactAdapter contactAdapter;
    private FragmentMessagesChildContactsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMessagesChildContactsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MessagesFragment messagesFragment = (MessagesFragment) getParentFragment();

        createContactRecyclerView(messagesFragment.getAuthorizationCode());

        messagesFragment.contactList.observe(getViewLifecycleOwner(), contacts -> {
            contactList.clear();
            contactList.addAll(contacts);
            contactAdapter.notifyItemRangeInserted(1, contactList.size());
        });
    }

    private void createContactRecyclerView(String authorizationCode) {
        RecyclerView rcvContact = binding.recycleViewContacts;
        contactAdapter = new ContactAdapter(contactList, getContext(), authorizationCode);

        RecyclerView.LayoutManager layoutManagerMessage = new LinearLayoutManager(getContext());
        rcvContact.setLayoutManager(layoutManagerMessage);
        rcvContact.setItemAnimator(new FadeInLeftAnimator());

        ContactDivider contactDivider = new ContactDivider(getResources().getDrawable(R.drawable.recycler_view_divider));
        rcvContact.addItemDecoration(contactDivider);

        rcvContact.setAdapter(contactAdapter);
    }
}
