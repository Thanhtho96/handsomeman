package com.tt.handsomeman.di.component;


import com.tt.handsomeman.di.module.AppModule;
import com.tt.handsomeman.di.module.JobModule;
import com.tt.handsomeman.di.module.MessageModule;
import com.tt.handsomeman.di.module.NetworkModule;
import com.tt.handsomeman.di.module.UserModule;
import com.tt.handsomeman.di.module.ViewModelModule;
import com.tt.handsomeman.ui.CustomerProfileJobDetail;
import com.tt.handsomeman.ui.FilterResult;
import com.tt.handsomeman.ui.GroupByCategory;
import com.tt.handsomeman.ui.JobDetail;
import com.tt.handsomeman.ui.Login;
import com.tt.handsomeman.ui.OnBoardingSlidePagerActivity;
import com.tt.handsomeman.ui.Register;
import com.tt.handsomeman.ui.SignUp;
import com.tt.handsomeman.ui.SignUpAddPayout;
import com.tt.handsomeman.ui.Start;
import com.tt.handsomeman.ui.YourLocation;
import com.tt.handsomeman.ui.bid_job_detail.BidJobLetterReviewFragment;
import com.tt.handsomeman.ui.jobs.JobsChildJobsFragment;
import com.tt.handsomeman.ui.jobs.JobsChildWishListFragment;
import com.tt.handsomeman.ui.messages.Conversation;
import com.tt.handsomeman.ui.messages.MessagesChildContactsFragment;
import com.tt.handsomeman.ui.messages.MessagesChildMessagesFragment;
import com.tt.handsomeman.ui.more.MoreFragment;
import com.tt.handsomeman.ui.more.MyProfileReviewsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        JobModule.class,
        UserModule.class,
        NetworkModule.class,
        MessageModule.class,
        ViewModelModule.class})
public interface AppComponent {

    void inject(JobsChildJobsFragment fragment);

    void inject(JobsChildWishListFragment fragment);

    void inject(Login login);

    void inject(SignUp signUp);

    void inject(Start start);

    void inject(OnBoardingSlidePagerActivity activity);

    void inject(Register register);

    void inject(SignUpAddPayout signUpAddPayout);

    void inject(YourLocation yourLocation);

    void inject(GroupByCategory category);

    void inject(FilterResult filterResult);

    void inject(JobDetail jobDetail);

    void inject(CustomerProfileJobDetail customerProfileJobDetail);

    void inject(BidJobLetterReviewFragment reviewFragment);

    void inject(MessagesChildMessagesFragment messagesFragment);

    void inject(MessagesChildContactsFragment contactsFragment);

    void inject(Conversation conversation);

    void inject(MoreFragment moreFragment);

    void inject(MyProfileReviewsFragment profileReviewsFragment);
}
