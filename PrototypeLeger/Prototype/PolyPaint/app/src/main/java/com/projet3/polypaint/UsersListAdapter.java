package com.projet3.polypaint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class UsersListAdapter extends BaseAdapter implements Filterable {
    private final Context context;
    private final ArrayList<User> values;
    private ArrayList<User> filteredValues;
    private int filterState;
    private boolean filterChanged;

    //Two data sources, the original data and filtered data


    public UsersListAdapter(Context context, ArrayList stringValues, ArrayList<User> values) {
        //super(context, -1, stringValues);
        super();
        this.context = context;
        this.values = values;
        this.filteredValues = values;
        this.filterState = 0;
        this.filterChanged = false;
    }
    public int getCount()
    {
        return filteredValues.size();
    }

    //This should return a data object, not an int
    public User getItem(int position)
    {
        return filteredValues.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public void applyFilterState(int filterState_){
        filterState = filterState_;
        filterChanged = true;
        //this.getFilter();
        //filterChanged = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.user_entry, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.usernameTextView);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.usernameIsConnectedImage);
        User user = filteredValues.get(position);
        textView.setText(user.getUsername());
        if (user.isConnected())
            imageView.setBackgroundResource(R.drawable.ic_connected);
        else
            imageView.setBackgroundResource(R.drawable.ic_disconnected);
        return rowView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if ((charSequence == null || charSequence.length() == 0) && !filterChanged) {
                    results.values = values;
                    results.count = values.size();
                } else {
                    ArrayList<User> filterResultsData = new ArrayList();

                    for (User data : values) {
                        boolean cond = data.getUsername().toLowerCase().contains(charSequence.toString().toLowerCase());
                        switch(filterState){
                            case 0:
                                break;
                            case 1:
                                cond = cond && data.isConnected();
                                break;
                            case 2:
                                cond = cond && !data.isConnected();
                                break;
                        }
                        if (cond) {
                            filterResultsData.add(data);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredValues = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
                filterChanged = false;
            }
        };
    }
}

