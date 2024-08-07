package com.example.kinga.ui.profiles.profileView;

import static com.example.kinga.core.NetworkConnection.VolleyUtils.RequestTimeOut.VIEW_DB_HANDLER_POST_DELAY_MS;
import static com.example.kinga.core.NetworkConnection.VolleyUtils.RequestTimeOut.VIEW_HANDLER_RETRIES;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.VolleyError;
import com.example.kinga.PrimaryNavigationActivity;
import com.example.kinga.R;
import com.example.kinga.core.ImageUtils.ImageCircularTransform;
import com.example.kinga.core.NetworkConnection.WebRequestHelpers.StatusCodeDescriptor;
import com.example.kinga.core.Dialogs.displayAlertToast;
import com.example.kinga.core.RedirectResolver;
import com.example.kinga.core.RemoveKeyboardFromView;
import com.example.kinga.core.Urls.SharedUrls;
import com.example.kinga.core.userUtils.SessionHandler;
import com.squareup.picasso.Picasso;

public class ProfileViewFragment extends Fragment {
    private ProfileViewViewModel mProfileViewViewModel;

    private ImageView mIvProfileViewImage;
    private TextView mTvName;

    private ImageView mIvProfileViewEmailIcon;
    private TextView mTvEmailTitle;
    private TextView mTvEmail;

    private ImageView mIvProfileViewPhoneIcon;
    private TextView mTvPhoneTitle;
    private TextView mTvPhone;

    private ImageView mIvProfileViewLocationIcon;
    private TextView mTvLocationTitle;
    private TextView mTvLocation;

    private ImageView mIvProfileViewJoinDateIcon;
    private TextView mTvJoinDateTitle;
    private TextView mTvJoinDate;

    private Button mEditProfileEditButton;
    private Button mEditProfileChangePasswordButton;

    private ProgressBar mProfileProgressBar;

    private SessionHandler mSession;
    private Context mContext;
    private int handlerRunCount = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // We get the context and store it in the mContext variable so that we can access it
        // throughout. This will prevent us from calling 'getContext' multiple times which may
        // produce a NullPointerException if the user has pressed the back button
        mContext = getContext();

        // Get user session
        mSession = new SessionHandler(this.mContext);

        // If user is not logged in, redirect them to the login page
        if(!mSession.isLoggedIn()){
            logoutUserAndRedirectToLogin();
        }

        //  Initialize root
        View root = inflater.inflate(R.layout.fragment_profiles_profile_view, container, false);

        // Remove keyboard from screen in case its there after coming
        // from notificationCreate fragment
        RemoveKeyboardFromView.clearKeyboard(root, getActivity());

        // *** Prepare Views
        mIvProfileViewImage = root.findViewById(R.id.profile_view_iv_profile_image);
        mTvName = root.findViewById(R.id.profile_view_tv_name);

        mIvProfileViewEmailIcon = root.findViewById(R.id.profile_view_iv_email_icon);
        mTvEmailTitle = root.findViewById(R.id.profile_view_tv_email_title);
        mTvEmail = root.findViewById(R.id.profile_view_tv_email);

        mIvProfileViewPhoneIcon = root.findViewById(R.id.profile_view_iv_phone_icon);
        mTvPhoneTitle = root.findViewById(R.id.profile_view_tv_phone_title);
        mTvPhone = root.findViewById(R.id.profile_view_tv_phone);

        mIvProfileViewLocationIcon = root.findViewById(R.id.profile_view_iv_location_icon);
        mTvLocationTitle = root.findViewById(R.id.profile_view_tv_location_title);
        mTvLocation = root.findViewById(R.id.profile_view_tv_location);

        mIvProfileViewJoinDateIcon = root.findViewById(R.id.profile_view_iv_join_date_icon);
        mTvJoinDateTitle = root.findViewById(R.id.profile_view_tv_join_date_title);
        mTvJoinDate = root.findViewById(R.id.profile_view_tv_join_date);

        mEditProfileEditButton = root.findViewById(R.id.profile_view_edit_btn);
        mEditProfileChangePasswordButton = root.findViewById(R.id.profile_view_change_password_btn);

        // *** Set progress bar
        mProfileProgressBar = root.findViewById(R.id.profile_view_progress_bar);

        // *** Hide profile content as data is being loaded
        hideProfileContent();

        // Get a new or existing ViewModel from the ViewModelProvider.
        getViewModelForLiveServer();

        // Add an observer on the LiveData returned by the repository.
        activateViewModelObserver();

        return root;
    }

    /**
     * Get a new or existing ViewModel from the ViewModelProvider.
     */
    public void getViewModelForLiveServer(){

        // Get the request url that will be used
        String requestUrl = getRequestUrl();

        // Get a new or existing ViewModel from the ViewModelProvider.
        ProfileViewModelFactory factory =
                new ProfileViewModelFactory(getActivity().getApplication(), requestUrl);

        mProfileViewViewModel =
                ViewModelProviders.of(this, factory).get(ProfileViewViewModel.class);
    }

    /**
     * Determines the correct request url depending on the current logged in user type
     * @return - Returns a string with the correct request url.
     */
    public String getRequestUrl(){

        boolean isDriver = mSession.getUserDetails().getUserType() == 1;

        return SharedUrls.profileViewUrl(isDriver);
    }

    /**
     * Hides profile content as data is being loaded
     */
    private void hideProfileContent() {

        mIvProfileViewImage.setVisibility(View.GONE);
        mTvName.setVisibility(View.GONE);

        mIvProfileViewEmailIcon.setVisibility(View.GONE);
        mTvEmailTitle.setVisibility(View.GONE);
        mTvEmail.setVisibility(View.GONE);

        mIvProfileViewPhoneIcon.setVisibility(View.GONE);
        mTvPhoneTitle.setVisibility(View.GONE);
        mTvPhone.setVisibility(View.GONE);

        mIvProfileViewLocationIcon.setVisibility(View.GONE);
        mTvLocationTitle.setVisibility(View.GONE);
        mTvLocation.setVisibility(View.GONE);

        mIvProfileViewJoinDateIcon.setVisibility(View.GONE);
        mTvJoinDateTitle.setVisibility(View.GONE);
        mTvJoinDate.setVisibility(View.GONE);

        mEditProfileEditButton.setVisibility(View.GONE);
        mEditProfileChangePasswordButton.setVisibility(View.GONE);
    }

    /**
     * Reveals profile content after data has been loaded
     */
    private void revealProfileContent() {

        mIvProfileViewImage.setVisibility(View.VISIBLE);
        mTvName.setVisibility(View.VISIBLE);

        mIvProfileViewEmailIcon.setVisibility(View.VISIBLE);
        mTvEmailTitle.setVisibility(View.VISIBLE);
        mTvEmail.setVisibility(View.VISIBLE);

        mIvProfileViewPhoneIcon.setVisibility(View.VISIBLE);
        mTvPhoneTitle.setVisibility(View.VISIBLE);
        mTvPhone.setVisibility(View.VISIBLE);

        mIvProfileViewLocationIcon.setVisibility(View.VISIBLE);
        mTvLocationTitle.setVisibility(View.VISIBLE);
        mTvLocation.setVisibility(View.VISIBLE);

        mIvProfileViewJoinDateIcon.setVisibility(View.VISIBLE);
        mTvJoinDateTitle.setVisibility(View.VISIBLE);
        mTvJoinDate.setVisibility(View.VISIBLE);

        mEditProfileEditButton.setVisibility(View.VISIBLE);
        mEditProfileChangePasswordButton.setVisibility(View.VISIBLE);
    }

    /**
     * Feeds views with profile data so that they can displayed
     */
    private void displayProfileContent(ProfileViewModel profileLiveData) {

        mTvName.setText(profileLiveData.getFullName());
        mTvEmail.setText(profileLiveData.getEmail());
        mTvPhone.setText(String.valueOf(profileLiveData.getPhone()));
        mTvLocation.setText(String.valueOf(profileLiveData.getLocation()));
        mTvJoinDate.setText(String.valueOf(profileLiveData.getJoinDate()));

        //Loading image using Picasso
        Picasso.get()
                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRP_kerHqsbqO2MKCk-B9DBaaFt_QC4R7kBDAI5p-_oY77injCiGG8mJzpON0pDjJ6c0qk&usqp=CAU")
                .transform(new ImageCircularTransform(getContext(), 2))
                .into(mIvProfileViewImage);


        // Add local image to picasso image view
//        Picasso.get()
//                .load(R.drawable.baseline_person_24)
////                .transform(new ImageCircularTransform(getContext(), 2))
//                .into(mIvProfileViewImage);

        // Reveals profile content after data has been loaded
        revealProfileContent();
    }

    /**
     * Activates view model's live data observer
     */
    private void activateViewModelObserver() {

        // Add an observer on the LiveData returned by the repository.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mProfileViewViewModel.getProfileLiveData().observe(getViewLifecycleOwner(),
                new Observer<ProfileViewModel>() {
                    @Override
                    public void onChanged(@Nullable final ProfileViewModel live_data) {

                        // This variable is used by change views to notify
                        // this view to reload its data
                        if (((PrimaryNavigationActivity)getActivity()).mReloadProfileView){

                            // Invalidate the data in the live data
                            live_data.setStatusCode(0);

                            // Deletes all data from the repository and then reloads new data from the
                            // server
                            mProfileViewViewModel.reloadRepository();

                            // Change to mReloadProductIndex false
                            ((PrimaryNavigationActivity)getActivity()).mReloadProfileView = false;
                        }

                        if (live_data.getStatusCode() == 200){

                            // If the live data's status code is 200, we don't call the handler

                            displayProfileContent(live_data);

                            mProfileProgressBar.setVisibility(View.GONE); // Remove mainProgressBar

                        }else {

                            // Since live data's status code is not 200, we call a handler that will listen
                            // continuously until the live data has been updated either with success or
                            // failure
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    // If live data has not been updated continue checking after every
                                    // "viewHandlerPostDelayMs" milliseconds, else process the  results
                                    if (live_data.getStatusCode() == 0) {

                                        boolean viewInForeground = isAdded() && isVisible();

                                        // Schedule a handler run if view is in foreground and handler run
                                        // count has not exceeded viewHandlerRetries.
                                        // By doing this, we prevent the handler from running indefinitely or
                                        // running when the view is not in the foreground
                                        if (handlerRunCount < VIEW_HANDLER_RETRIES && viewInForeground){

                                            handler.postDelayed(this, VIEW_DB_HANDLER_POST_DELAY_MS);

                                            handlerRunCount++;

                                        }else{

                                            handlerRunCount = 0;

                                            // Only do the following when the view is in the foreground
                                            if (viewInForeground){

                                                // Remove mainProgressBar
                                                mProfileProgressBar.setVisibility(View.GONE);

                                            }
                                        }

                                    }else{

                                        mProfileProgressBar.setVisibility(View.GONE); // Remove mainProgressBar

                                        // Check if the returned results has errors
                                        if (live_data.getStatusCode() == 200){

                                            displayProfileContent(live_data);

                                        }else if (live_data.getStatusCode() == 401){

                                            // Logout user and redirect to login page
                                            logoutUserAndRedirectToLogin();

                                        }else{

                                            int status_code = live_data.getStatusCode();

                                            showAlertDialog(StatusCodeDescriptor.codeMessage(status_code));
                                        }
                                    }
                                }
                            }, 2000);  //the time is in milliseconds
                        }
                    }
                });
    }

    /**
     * Logs out user and redirects them to the login view
     */
    private void logoutUserAndRedirectToLogin() {
        RedirectResolver.logoutUserAndRedirectToLogin(this.mContext, mSession);
    }

    /**
     * Displays a dialog with the an error message
     * @param error - The error message to be displayed in the dialog
     */
    private void showAlertDialog(String error){
        displayAlertToast.showLongDeterminedError(this.mContext, error);
    }
}