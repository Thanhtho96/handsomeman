package com.tt.handsomeman.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.tt.handsomeman.request.PageableRequest;
import com.tt.handsomeman.request.SendMessageRequest;
import com.tt.handsomeman.response.DataBracketResponse;
import com.tt.handsomeman.response.ListConversation;
import com.tt.handsomeman.response.ListMessage;
import com.tt.handsomeman.response.StandardResponse;
import com.tt.handsomeman.service.MessageService;
import com.tt.handsomeman.util.Constants;
import com.tt.handsomeman.util.SharedPreferencesUtils;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MessageViewModel extends BaseViewModel {
    private final MessageService messageService;
    private final MutableLiveData<DataBracketResponse<ListConversation>> listConversation = new MutableLiveData<>();
    private final MutableLiveData<DataBracketResponse<ListMessage>> listMessageResponse = new MutableLiveData<>();
    private final MutableLiveData<StandardResponse> standardResponseMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> messageResponse = new MutableLiveData<>();
    private final String locale = Constants.language.getValue();
    private final String authorization;

    @Inject
    MessageViewModel(@NonNull Application application,
                     MessageService messageService,
                     SharedPreferencesUtils sharedPreferencesUtils) {
        super(application);
        this.messageService = messageService;
        authorization = sharedPreferencesUtils.get("token", String.class);
    }

    public MutableLiveData<DataBracketResponse<ListMessage>> getListMessageResponse() {
        return listMessageResponse;
    }

    public MutableLiveData<DataBracketResponse<ListConversation>> getListConversation() {
        return listConversation;
    }

    public MutableLiveData<StandardResponse> getStandardResponseMutableLiveData() {
        return standardResponseMutableLiveData;
    }

    public MutableLiveData<String> getMessageResponse() {
        return messageResponse;
    }

    public void fetchAllConversationByAccountId(String type) {
        compositeDisposable.add(messageService.getAllConversationByAccountId(locale, this.authorization, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> listConversation.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void fetchMessagesWithAccount(Integer accountId,
                                         PageableRequest pageableRequest) {
        compositeDisposable.add(messageService.getAllMessagesWithAccount(locale, this.authorization, accountId, pageableRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> listMessageResponse.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void deleteConversationById(Integer conversationId) {
        compositeDisposable.add(messageService.deleteConversationById(locale, this.authorization, conversationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.body() != null) {
                                messageResponse.setValue(response.body().getMessage());
                            }
                        }, throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void sendMessageToConversation(SendMessageRequest sendMessageRequest) {
        compositeDisposable.add(messageService.sendMessageToConversation(locale, this.authorization, sendMessageRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> standardResponseMutableLiveData.setValue(response.body()), throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }
}
