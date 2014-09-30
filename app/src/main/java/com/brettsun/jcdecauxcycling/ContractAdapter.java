package com.brettsun.jcdecauxcycling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ContractAdapter extends ArrayAdapter<Contract> {

    private Contract mContracts[];

    ContractAdapter(Context context, Contract[] contracts) {
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

        Contract contract = mContracts[position];
        TextView textLocation = (TextView)rowView.findViewById(R.id.contract_location);
        TextView textCommercialName = (TextView)rowView.findViewById(R.id.contract_commercial_name);
        textLocation.setText("City: " + contract.getName() + ", " + contract.getCountry());
        textCommercialName.setText("Company: " + contract.getCommercialName());

        return rowView;
    }
}
