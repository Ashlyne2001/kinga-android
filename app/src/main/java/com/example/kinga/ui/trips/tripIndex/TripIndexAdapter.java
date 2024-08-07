package com.example.kinga.ui.trips.tripIndex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kinga.R;
import com.example.kinga.core.userUtils.SessionHandler;

import java.util.ArrayList;
import java.util.List;


public class TripIndexAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private SessionHandler mSession;
    private final List<TripIndexInnerModel> mTrips = new ArrayList<>();

    // Bottom loading variables
    private boolean mIsLoadingAdded = false;
    private static final int ITEM = 0;
    private static final int LOADING = 1;

    TripIndexAdapter(Context context) {

        mContext = context;
        // Get user session
        mSession = new SessionHandler(context);
    }

    /**
     * Pass in the new trip data into the adapter
     * @param tripIndex - Holds a {@link TripIndexModel}
     */
    public void setTrip(TripIndexModel tripIndex) {

        this.clear();

        notifyDataSetChanged();

        mTrips.addAll(tripIndex.getTripIndexInnerModel());

        notifyDataSetChanged();

    }

    /**
     * Usually involves inflating a layout from XML and returning the holder
     * @return - Returns the holder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;

        // Create views
        if (viewType == LOADING){

            view = LayoutInflater.from(mContext).inflate(R.layout.item_loading, viewGroup,
                    false);

            return new LoadingViewHolder(view);
        }else {

            view = LayoutInflater.from(mContext).inflate(R.layout.item_trips_trip_index_view, viewGroup,
                    false);

            return new TripIndexViewHolder(view);
        }
    }

    /**
     * Determine the view holder that should be used depending on the current view type value
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        // If its a loading item do nothing
        if (getItemViewType(position) != LOADING){

            ((TripIndexViewHolder) viewHolder).setTrip(mTrips.get(position));
        }
    }

    /**
     * View holder for adapter's loading item
     */
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * View holder for viewing adapter's data
     */
    class TripIndexViewHolder extends RecyclerView.ViewHolder {

        private final TextView mLeanTripIndexFromLocation;
        private final TextView mLeanTripIndexStartTime;
        private final TextView mLeanTripIndexCost;
        public View mItemView;

        TripIndexViewHolder(@NonNull View itemView) {
            super(itemView);

            mItemView = itemView;

            mLeanTripIndexFromLocation = itemView.findViewById(R.id.trip_index_tv_from_location);
            mLeanTripIndexStartTime = itemView.findViewById(R.id.trip_index_tv_start_time);
            mLeanTripIndexCost = itemView.findViewById(R.id.trip_index_tv_cost);

        }

        /**
         * Pass new trip data to the current item view
         * @param trip- Hold a {@link TripIndexInnerModel}
         */
        void setTrip(TripIndexInnerModel trip) {

            // Set item name
            mLeanTripIndexFromLocation.setText(trip.getFromLocationName());
            mLeanTripIndexStartTime.setText(trip.getStartTime());
            mLeanTripIndexCost.setText(trip.getCost());

//            mItemView.setOnClickListener(v -> {
//
//                mSession.setTripReceiptSetting(trip.getReceiptSetting());
//
//                Intent intent=new Intent();
//                intent.putExtra("TripInnerModel", trip);
//                ((LeanTripActivity)mContext).setResult(Activity.RESULT_OK,intent);
//                ((LeanTripActivity)mContext).finish();//finishing activity
//
//            });
        }
    }

    /**
     * Clean all elements of the recycler
     */
    public void clear() {
        mTrips.clear();
        notifyDataSetChanged();
    }

    /**
     * getItemCount() is called many times, and when it is first called,
     * mLeanSupervisors has not been updated (means initially, it's null, and we can't return null).
     * @return - Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        if (mTrips != null)
            return mTrips.size();
        else return 0;
    }


    /**
     * Inserts a loading item at the bottom of the adapter.
     */
    public void setLoadingItem() {

        mIsLoadingAdded = true;
        mTrips.add(new TripIndexInnerModel());

        notifyItemInserted(mTrips.size() - 1);
    }

    /**
     *  Removes a loading item at the bottom of the adapter.
     */
    private void removeLoadingFooter() {

        if (!mIsLoadingAdded){
            return;
        }

        mIsLoadingAdded = false;

        int position = mTrips.size() - 1;
        TripIndexInnerModel item = getItem(position);

        if (item != null) {
            mTrips.remove(position);
        }
    }

    /**
     * Removes a loading item at the bottom of the adapter after a success response from the server.
     * After removing the loader, we don't need to do any extra work since the ModelObserver in the
     * index view will do the rest. (ie inserting data and notifying the adapter of data change)
     */
    public void removeLoadingFooterAfterSuccessResponse(){
        removeLoadingFooter();
    }

    /**
     * Gets the item at the provided position.
     * @param position - The position of the item that is being retrieved.
     * @return - Returns the retrieved item.
     */
    public TripIndexInnerModel getItem(int position) {
        return mTrips.get(position);
    }

    /**
     * Determines the view type for the item at the provided position.
     * @param position - The position of the item that is being analysed.
     * @return - Returns the view type of the retrieved item.
     */
    @Override
    public int getItemViewType(int position) {
        return (position == mTrips.size() - 1 && mIsLoadingAdded) ? LOADING : ITEM;
    }
}
