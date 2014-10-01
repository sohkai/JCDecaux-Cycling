package com.brettsun.jcdecauxcycling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StationAdapter extends ArrayAdapter<Station> {

    private static final String AVAILABLE_BIKES_TEXT = "Bikes: ";
    private static final String AVAILABLE_STANDS_TEXT = "Stands: ";
    private static final String STATUS_OPEN_TEXT = "Open";

    private Context mContext;
    private Station mStations[];

    StationAdapter(Context context, Station[] stations) {
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

        Station station = mStations[position];
        TextView textName = (TextView) rowView.findViewById(R.id.station_name);
        TextView textAddress = (TextView) rowView.findViewById(R.id.station_address);
        TextView textStatus = (TextView) rowView.findViewById(R.id.station_status);
        TextView textAvailableBikes = (TextView) rowView.findViewById(R.id.station_available_bikes);
        TextView textAvailableStands = (TextView) rowView.findViewById(R.id.station_available_stands);

        textName.setText(station.getName());
        textAddress.setText(station.getAddress());
        textAvailableBikes.setText(Integer.toString(station.getAvailableBikes()));
        textAvailableStands.setText(Integer.toString(station.getAvailableBikeStands()));
        // Change colour depending on if station is open or not
        String stationStatus = station.getStatus();
        if (stationStatus.equals(STATUS_OPEN_TEXT)) {
            textStatus.setTextColor(mContext.getResources().getColor(R.color.ForestGreen));
        } else {
            textStatus.setTextColor(mContext.getResources().getColor(R.color.Crimson));
        }
        textStatus.setText(stationStatus);

        return rowView;
    }
}
