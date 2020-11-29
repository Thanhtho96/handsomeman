package com.tt.handsomeman.util;

import androidx.lifecycle.MutableLiveData;

public class Constants {
    public static final String BASE_URL = "https://handsomeman.herokuapp.com/";
    //    public static final String BASE_URL = "http://7.7.7.17:8080/";

    public static final String SIGN_UP_ADD_PAYOUT_SUFFIX = "api/user/active-account";
    public static final String CHANGE_PASSWORD = "api/user/changePassword";
    public static final String LOGIN_SUFFIX = "api/user/login";
    public static final String SIGN_UP_SUFFIX = "api/user/registration";
    public static final String FORGET_PASSWORD = "api/user/resetPassword";
    public static final String UPDATE_AVATAR = "api/user/updateAvatar";
    public static final String VIEW_AVATAR = "api/user/viewAvatar/";

    // Both handyman and customer can have payout
    public static final String USER_ADD_PAYOUT = "api/user/payout/add";
    public static final String USER_EDIT_PAYOUT = "api/user/payout/edit/{payoutId}";
    public static final String USER_REMOVE_PAYOUT = "api/user/payout/remove/{payoutId}";

    public static final String DELETE_CONVERSATION_ID = "api/user/message/deleteConversation/{conversationId}";
    public static final String GET_ALL_CONVERSATION_OF_ACCOUNT = "api/user/message/getAllConversationByAccountId";
    public static final String GET_ALL_MESSAGES_WITH_ACCOUNT = "api/user/message/getAllMessagesWithAccount/{accountId}";
    public static final String SEND_MESSAGE_TO_CONVERSATION = "api/user/message/sendMessageToAccount";

    public static final String HANDYMAN_GET_LIST_CATEGORY = "api/handyman/categories";
    public static final String HANDYMAN_VIEW_CUSTOMER_PROFILE = "api/handyman/customerProfile/{id}";
    public static final String GET_HANDYMAN_REVIEW = "api/handyman/handymanReview";
    public static final String GET_HANDYMAN_INFO = "api/handyman/info";
    public static final String HANDYMAN_VIEW_JOB_DETAIL = "api/handyman/job/{id}";
    public static final String HANDYMAN_ADD_JOB_BID = "api/handyman/job/addJobBid";
    public static final String HANDYMAN_GET_JOB_BY_CATEGORY_SUFFIX = "api/handyman/job/byCategory/{id}";
    public static final String HANDYMAN_GET_JOB_FILTER_SUFFIX = "api/handyman/job/filter";
    public static final String HANDYMAN_JOB_WISH_LIST = "api/handyman/job/jobWishList";
    public static final String HANDYMAN_GET_JOB_SEARCH_BY_WORD = "api/handyman/job/searchByWord";
    public static final String HANDYMAN_START_SCREEN_SUFFIX = "api/handyman/job/startScreen";
    public static final String HANDYMAN_YOUR_LOCATION_SUFFIX = "api/handyman/job/yourLocation";
    public static final String HANDYMAN_GET_LIST_PAYOUT = "api/handyman/listPayout";
    public static final String HANDYMAN_MY_PROJECT = "api/handyman/myProject";
    public static final String GET_HANDYMAN_PROFILE = "api/handyman/profile";
    public static final String GET_HANDYMAN_PROFILE_EDIT = "api/handyman/profile/edit";
    public static final String TRANSFER_TO_BANK = "api/handyman/transfer";
    public static final String VIEW_TRANSFER_HISTORY = "api/handyman/viewTransferHistory";
    public static final String VIEW_PAYMENT_TRANSACTION = "api/handyman/viewTransaction";
    public static final String HANDYMAN_LOAD_REVIEW = "api/handyman/loadReview";
    public static final String HANDYMAN_REVIEW_CUSTOMER = "api/handyman/reviewCustomer";

    public static final String CUSTOMER_GET_LIST_CATEGORY = "api/customer/categories";
    public static final String FIND_HANDYMAN_CATEGORY = "api/customer/byCategory/{id}";
    public static final String GET_CUSTOMER_REVIEW = "api/customer/customerReview";
    public static final String FIND_HANDYMAN = "api/customer/findHandyman";
    public static final String GET_HANDYMAN_DETAIL = "api/customer/handymanDetail";
    public static final String GET_CUSTOMER_INFO = "api/customer/info";
    public static final String CUSTOMER_VIEW_JOB_DETAIL = "api/customer/job/{id}";
    public static final String CUSTOMER_UPDATE_NEW_JOB = "api/customer/job/{id}";
    public static final String CUSTOMER_DELETE_NEW_JOB = "api/customer/job/{id}";
    public static final String CUSTOMER_ADD_NEW_JOB = "api/customer/job/add";
    public static final String CUSTOMER_MY_PROJECT = "api/customer/myProject";
    public static final String GET_CUSTOMER_PROFILE = "api/customer/profile";
    public static final String EDIT_CUSTOMER_PROFILE = "api/customer/profile/edit";
    public static final String FIND_HANDYMAN_NEARBY = "api/customer/yourLocation";
    public static final String CUSTOMER_VIEW_MAKE_TRANSACTION = "api/customer/viewMakeTransaction";
    public static final String CUSTOMER_MAKE_TRANSACTION = "api/customer/makeTheTransaction";
    public static final String CUSTOMER_VIEW_TRANSFER_HISTORY = "api/customer/viewTransferHistory";
    public static final String CUSTOMER_LOAD_REVIEW = "api/customer/loadReview";
    public static final String CUSTOMER_REVIEW_HANDYMAN = "api/customer/reviewHandyman";

    public static final String ACCEPT_BID = "api/user/notification/acceptBid";
    public static final String GET_ALL_NOTIFICATION = "api/user/notification/getAll";
    public static final String MARK_NOTIFICATION_AS_READ = "api/user/notification/markAsRead";
    public static final String READ_MADE_BID_NOTIFICATION = "api/user/notification/viewMadeABid";
    public static final String READ_PAID_PAYMENT_NOTIFICATION = "api/user/notification/viewPaidPayment";

    public static final Integer NOT_VERIFIED_ACCOUNT = 0;
    public static final Integer NOT_ACTIVE_ACCOUNT = 1;
    public static final Integer STATE_REGISTER_ADDED_PAYOUT = 3;

    public static final MutableLiveData<Double> Latitude = new MutableLiveData<>();
    public static final MutableLiveData<Double> Longitude = new MutableLiveData<>();
    public static final MutableLiveData<String> language = new MutableLiveData<>();
    public static final String dateTimeSecondFileNamePattern = "yyyyMMdd_HHmmss";
    public static final int MAX_UPLOAD_FILE_SIZE = 5;
    public static final String fileProviderExtension = ".fileprovider";

    public enum FILE_TYPE {
        IMAGE("IMG_"),
        PDF("PDF_");

        private final String fileSuffix;

        FILE_TYPE(String fileSuffix) {
            this.fileSuffix = fileSuffix;
        }

        public String getFileSuffix() {
            return fileSuffix;
        }
    }
}
