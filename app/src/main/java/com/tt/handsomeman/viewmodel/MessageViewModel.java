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
import com.tt.handsomeman.util.StatusConstant;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MessageViewModel extends BaseViewModel {
    private final MessageService messageService;
    private MutableLiveData<DataBracketResponse<ListConversation>> listConversation = new MutableLiveData<>();
    private MutableLiveData<DataBracketResponse<ListMessage>> listMessageResponse = new MutableLiveData<>();
    private MutableLiveData<StandardResponse> standardResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> messageResponse = new MutableLiveData<>();
    private String locale = Constants.language.getValue();

    @Inject
    MessageViewModel(@NonNull Application application,
                     MessageService messageService) {
        super(application);
        this.messageService = messageService;
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

    public void fetchAllConversationByAccountId(String authorization,
                                                String type) {
        compositeDisposable.add(messageService.getAllConversationByAccountId(locale, authorization, type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            listConversation.setValue(response.body());
                        }, throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void fetchMessagesWithAccount(String authorization,
                                         Integer accountId,
                                         PageableRequest pageableRequest) {
        compositeDisposable.add(messageService.getAllMessagesWithAccount(locale, authorization, accountId, pageableRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            listMessageResponse.setValue(response.body());
                        }, throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void deleteConversationById(String authorization,
                                       Integer conversationId) {
        compositeDisposable.add(messageService.deleteConversationById(locale, authorization, conversationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            if (response.body() != null) {
                                if (response.body().getStatus().equals(StatusConstant.OK)) {
                                    messageResponse.setValue(response.body().getMessage());
                                } else {
                                    messageResponse.setValue(response.body().getMessage());
                                }
                            }
                        }, throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }

    public void sendMessageToConversation(String authorization,
                                          SendMessageRequest sendMessageRequest) {
        compositeDisposable.add(messageService.sendMessageToConversation(locale, authorization, sendMessageRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            standardResponseMutableLiveData.setValue(response.body());
                        }, throwable -> Toast.makeText(getApplication(), throwable.getMessage(), Toast.LENGTH_SHORT).show()
                ));
    }
}
