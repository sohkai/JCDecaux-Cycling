package com.brettsun.jcdecauxcycling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContractAdapter extends ArrayAdapter<Contract> {

    private static final String COMMERCIAL_NAME_TEXT = "Company: ";

    private List<Contract> mContracts;

    ContractAdapter(Context context, List<Contract> contracts) {
        super(context, R.layout.contract_list_item, contracts);
        mContracts = contracts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // Check first if we're recycling a view from before due to scrolling
        if (convertView == null) {
            // Otherwise inflate a new view
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            rowView = inflater.inflate(R.layout.contract_list_item, parent, false);
        }

        Contract contract = mContracts.get(position);
        TextView textLocation = (TextView) rowView.findViewById(R.id.contract_location);
        TextView textCommercialName = (TextView) rowView.findViewById(R.id.contract_commercial_name);
        textLocation.setText(contract.getName() + ", " + contract.getCountry());
        textCommercialName.setText(COMMERCIAL_NAME_TEXT + contract.getCommercialName());

        return rowView;
    }

}
