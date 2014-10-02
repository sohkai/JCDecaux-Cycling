package com.brettsun.jcdecauxcycling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class StationAdapter extends ArrayAdapter<Station> {

    private static final String BIKE_STANDS_DIVIDER_TEXT = " / ";
    private static final String STATUS_OPEN_TEXT = "Open";

    private Context mContext;
    private List<Station> mStations;

    StationAdapter(Context context, List<Station> stations) {
        super(context, R.layout.station_list_item, stations);
        mContext = context;
        mStations = stations;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // Check first if we're recycling a view from before due to scrolling
        if (convertView == null) {
            // Otherwise inflate a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.station_list_item, parent, false);
        }

        TextView textName = (TextView) rowView.findViewById(R.id.station_name);
        TextView textAddress = (TextView) rowView.findViewById(R.id.station_address);
        TextView textStatus = (TextView) rowView.findViewById(R.id.station_status);
        TextView textAvailableBikes = (TextView) rowView.findViewById(R.id.station_bikes_available);
        TextView textTotalBikes = (TextView) rowView.findViewById(R.id.station_bikes);

        Station station = mStations.get(position);
        int availableBikes = station.getAvailableBikes();
        String stationStatus = station.getStatus();

        // Change available bikes colour if there are available bikes or not
        if (availableBikes > 0) {
            textAvailableBikes.setTextColor(mContext.getResources().getColor(R.color.ForestGreen));
        } else {
            textAvailableBikes.setTextColor(mContext.getResources().getColor(R.color.Crimson));
        }
        // Change status colour if station is open
        if (stationStatus.equals(STATUS_OPEN_TEXT)) {
            textStatus.setTextColor(mContext.getResources().getColor(R.color.ForestGreen));
        } else {
            textStatus.setTextColor(mContext.getResources().getColor(R.color.Crimson));
        }

        textName.setText(station.getName());
        textAddress.setText(station.getAddress());
        textAvailableBikes.setText(Integer.toString(availableBikes));
        textTotalBikes.setText(BIKE_STANDS_DIVIDER_TEXT + Integer.toString(station.getTotalBikeStands()));
        textStatus.setText(stationStatus);

        return rowView;
    }
}
