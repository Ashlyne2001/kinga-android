package com.example.kinga.ui.trips.tripIndex;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kinga.core.NetworkConnection.WebRequestHelpers.StatusCodeDescriptor;
import com.example.kinga.core.RedirectResolver;
import com.example.kinga.core.RemoveKeyboardFromView;
import com.example.kinga.core.Urls.SharedUrls;
import com.example.kinga.core.userUtils.SessionHandler;
import com.example.kinga.core.userUtils.User;
import com.example.kinga.core.ViewUtils.WrapContentLinearLayoutManager;
import com.example.kinga.PrimaryNavigationActivity;
import com.example.kinga.R;
import com.example.kinga.databinding.FragmentTripsTripIndexBinding;

import static com.example.kinga.core.NetworkConnection.VolleyUtils.RequestTimeOut.VIEW_DB_HANDLER_POST_DELAY_MS;
import static com.example.kinga.core.NetworkConnection.VolleyUtils.RequestTimeOut.VIEW_HANDLER_RETRIES;


public class TripIndexFragment extends Fragment {

    private final String FRAGMENT_FOR_RESULT_REQUEST_KEY = "TripIndexFragmentToLiveBarcodeScanningFragment";

    private SessionHandler mSession;
    private User mCurrentUser;

    public TripIndexAdapter mAdapter;
//    private MenuItem mSearchItem;
    private TripIndexViewModel mTripIndexViewModel;
    private TripIndexModel tripIndex;

    private Context mContext;
    private int handlerRunCount = 0;

    private FragmentTripsTripIndexBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        // We get the context and store it in the mContext variable so that we can access it
        // throughout. This will prevent us from calling 'getContext' multiple times which may
        // produce a NullPointerException if the user has pressed the back button
        mContext = getContext();

        // Get user session
        mSession = new SessionHandler(mContext);

        // Get current user
        mCurrentUser = mSession.getUserDetails();


        // If user is not logged in, redirect them to the login page
        if (!mSession.isLoggedIn()) {
            logoutUserAndRedirectToLogin();
        }

        binding = FragmentTripsTripIndexBinding.inflate(inflater, container, false);
        View mRoot = binding.getRoot();

        // We disable swipe refresh
        binding.itemViewScrollView.setEnabled(false);

        // Remove keyboard from screen in case its there after coming
        // from tripCreate fragment
        RemoveKeyboardFromView.clearKeyboard(mRoot, getActivity());


        // *** Prepare Views, adapter nad view model
        RecyclerView mRvTripIndex = binding.tripIndexRv;
        mAdapter = new TripIndexAdapter(getActivity());
        mRvTripIndex.setAdapter(mAdapter);
        LinearLayoutManager mLinearLayoutManager = new WrapContentLinearLayoutManager(getActivity());
        mRvTripIndex.setLayoutManager(mLinearLayoutManager);

        // Add horizontal divider to recycle view
        DividerItemDecoration horizontalDecoration =
                new DividerItemDecoration(mRvTripIndex.getContext(),
                        DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(mContext,
                R.drawable.rv_horizontal_divider);
        assert horizontalDivider != null;
        horizontalDecoration.setDrawable(horizontalDivider);
        mRvTripIndex.addItemDecoration(horizontalDecoration);

        // Get a new or existing ViewModel from the ViewModelProvider.
        TripIndexViewModelFactory factory = new TripIndexViewModelFactory(requireActivity()
                .getApplication(), SharedUrls.tripIndexUrl());

        // Get a new or existing ViewModel from the ViewModelProvider.
        mTripIndexViewModel = new ViewModelProvider(this, factory)
                .get(TripIndexViewModel.class);

        // Add an observer on the LiveData returned by TripIndexRepository's getTripsList.
        activateViewModelObserver();

        // Create fab button listener and displays
        if (mCurrentUser.hasManageItemsPerm()) {
            binding.tripIndexCreateFab.setVisibility(View.VISIBLE);
            binding.tripIndexCreateFab.setOnClickListener((View v) -> redirectToTripCreate());
        }

        return mRoot;
    }

    /**
     * Displays a dialog notifying the user that the trip was not found
     */
    private void showNoTripWithBarcodeFoundAlertDialog(String value) {

        // Before displaying the dialog, we check if the Activity is finishing so that we
        // avoid Error  "BinderProxy@45d459c0 is not valid; is your activity running?"
        if (!requireActivity().isFinishing()) {

            AlertDialog.Builder builder;

            builder = new AlertDialog.Builder(mContext);

            //Setting message manually and performing action on button click
            builder.setMessage(getResources().getString(R.string.trip_not_found_message, value))
                    .setTitle(getResources().getString(R.string.trip_not_found_title))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.field_ok_title), (dialog, id) -> {
                                // Do nothing
                            }
                    );

            //Creating dialog box
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * Redirects to trip create
     */
    private void redirectToTripCreate() {
//        NavHostFragment.findNavController(this).navigate(R.id.nav_trip_create);
    }

    /**
     * Removes the loading screen
     */
    private void removeLoadingPage() {
        binding.getRoot().findViewById(R.id.included_loading_page_layout).setVisibility(View.GONE);
    }

    /**
     * Upadates the adapter with new data
     *
     * @param tripIndex - New data that will be used to update the adapter
     */
    private void updateAdapter(TripIndexModel tripIndex) {

        this.tripIndex = tripIndex;

        mAdapter.setTrip(tripIndex);

        // When we pop back to this view, sometime mSearchItem is no initialized yet so we get a
        // null error. So we give the view a little time to load everything
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {

            if (mAdapter.getItemCount() > 0) {
                if (binding != null) {
                    binding.getRoot().findViewById(R.id.included_empty_list_layout).setVisibility(View.GONE);
                }
//                mSearchItem.setVisible(true);
            } else {
                if (binding != null) {
                    binding.getRoot().findViewById(R.id.included_empty_list_layout).setVisibility(View.VISIBLE);
                }
//                mSearchItem.setVisible(false);
                mAdapter.clear();
            }

        }, 50);  //the time is in milliseconds

    }

    /**
     * Activates view model's live data observer
     */
    private void activateViewModelObserver() {

        // Add an observer on the LiveData returned by the repository.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mTripIndexViewModel.getTripIndexLiveData().observe(getViewLifecycleOwner(),
                new Observer<TripIndexModel>() {
                    @Override
                    public void onChanged(@Nullable final TripIndexModel live_data) {

                        assert live_data != null;

                        // This variable is used by ReportDeleteFragment to notify
                        // ReportIndex to reload its data
                        if (((PrimaryNavigationActivity) requireActivity()).mReloadTripIndex) {

                            // Invalidate the data in the live data
                            live_data.setStatusCode(0);

                            // Deletes all data from the repository and then reloads new data from the
                            // server
                            mTripIndexViewModel.reloadRepository();

                            // Change to mReloadTripIndex false
                            ((PrimaryNavigationActivity) requireActivity()).mReloadTripIndex = false;
                        }

                        if (live_data.getStatusCode() == 200) {

                            // If the live data's status code is 200, we don't call the handler
                            updateAdapter(live_data);

                            // Remove loading screen
                            removeLoadingPage();

                        } else {

                            // Since live data's status code is not 200, we call a handler that will listen
                            // continuously until the live data has been updated either with success or
                            // failure
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    // If live data has not been updated continue checking after every
                                    // "viewHandlerPostDelayMs" milliseconds, else process the  results
                                    if (live_data.getStatusCode() == 0) {

                                        boolean viewInForeground = isAdded() && isVisible();

                                        // Schedule a handler run if view is in foreground and handler run
                                        // count has not exceeded viewHandlerRetries.
                                        // By doing this, we prevent the handler from running indefinitely
                                        // or running when the view is not in the foreground
                                        if (handlerRunCount < VIEW_HANDLER_RETRIES && viewInForeground) {

                                            handler.postDelayed(this, VIEW_DB_HANDLER_POST_DELAY_MS);

                                            handlerRunCount++;

                                        } else {

                                            handlerRunCount = 0;

                                            // Only do the following when the view is in the foreground
                                            if (viewInForeground) {
                                                // Remove loading screen
                                                removeLoadingPage();
                                            }
                                        }

                                    } else {

                                        // Remove loading screen
                                        removeLoadingPage();

                                        // Check if the returned results has errors
                                        if (live_data.getStatusCode() == 200) {
                                            updateAdapter(live_data);

                                        } else if (live_data.getStatusCode() == 204) {

                                            // Clear adapter just in case it has items
                                            mAdapter.clear();

                                            // Show add new dialog
                                            showAddNewTripMessage();

                                        } else {

                                            int status_code = live_data.getStatusCode();

                                            showErrorMessage(StatusCodeDescriptor.codeMessage(status_code));
                                        }
                                    }
                                }
                            }, VIEW_DB_HANDLER_POST_DELAY_MS);  //the time is in milliseconds
                        }
                    }
                });
    }

    /**
     * Logs out user and redirects them to the login view
     */
    private void logoutUserAndRedirectToLogin() {
        RedirectResolver.logoutUserAndRedirectToLogin(mContext, mSession);
    }

    /**
     * Displays an error message to the user
     *
     * @param error - The error message to be displayed in the dialog
     */
    private void showErrorMessage(String error) {

        TextView errorMessage = binding.getRoot().findViewById(
                R.id.included_error_result_layout).findViewById(R.id.error_result_message);
        errorMessage.setText(error);

        binding.getRoot().findViewById(R.id.included_error_result_layout).setVisibility(View.VISIBLE);
    }

    /**
     * Displays a empty list layout notifying the user that no item was found and an option for
     */
    private void showAddNewTripMessage() {

        showErrorMessage(
                getResources().getString(
                        R.string.not_found_message_with_add_option,
                        "trip")
        );
    }

    // ==================================================
    // Message delivery straight into the view model methods
    // ==================================================


    // ==================================================
    // View cycle methods
    // ==================================================

    /**
     * Activates data delivery
     */
    @Override
    public void onResume() {
        super.onResume();

        // Reload just in case db was changed to sync
        mTripIndexViewModel.reloadRepository();


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Unregisters new data delivery
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}