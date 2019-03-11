package com.projet3.polypaint;

import android.app.Fragment;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.projet3.polypaint.USER.UserManager;

import java.text.BreakIterator;
import java.util.ArrayList;


public class UsersListFragment extends Fragment {

    private ListView listView;
    private RelativeLayout usersTableRelativeLayout;
    private RelativeLayout hiddenTitleRelativeLayout;
    private View rootView;
    private TextView title;
    private Spinner filterSpinner;
    private ArrayList<User> users;
    private SearchView searchView;
    private UsersListAdapter adapter;

    private boolean isOpen;

    public static UsersListFragment newInstance(ArrayList<String> users_) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("USERS", users_);
        UsersListFragment fragobj = new UsersListFragment();
        fragobj.setArguments(bundle);
        return fragobj;
    }

    public UsersListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.users_list, container, false);
        usersTableRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.usersTable);
        hiddenTitleRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.hiddenTitleRelativeLayout);
        hiddenTitleRelativeLayout.setVisibility(View.GONE);
        title = (TextView) rootView.findViewById(R.id.usersTableTitle);
        searchView = (SearchView) rootView.findViewById(R.id.searchView);
        listView = (ListView) rootView.findViewById(R.id.listView);
        filterSpinner = (Spinner)rootView.findViewById(R.id.filterSpinner);
        users = new ArrayList<>();
        users.add(new User("Bob", true));
        users.add(new User("Bob2", true));
        users.add(new User("Bob3", true));
        users.add(new User("Bob4", true));
        users.add(new User("Bob5", true));
        users.add(new User("Bob6", true));
        users.add(new User("Bob7", true));
        users.add(new User("Bob8", true));
        users.add(new User("Bob9", true));
        users.add(new User("Bob10", true));
        users.add(new User("Bob11", true));
        users.add(new User("Bob12", true));

        users.add(new User("Alice", false));
        users.add(new User("Alice2", false));
        isOpen = true;

        setupListeners();
        setupSpinner();
        setupSearchView();
        return rootView;
    }
    private void setupSpinner() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Tous");
        strings.add("En ligne");
        strings.add("Hors ligne");
        android.widget.ArrayAdapter<String> spinnerArrayAdapter = new android.widget.ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item, strings);
        filterSpinner.setAdapter(spinnerArrayAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((UsersListAdapter)listView.getAdapter()).applyFilterState(position);
                ((UsersListAdapter) listView.getAdapter()).getFilter().filter(searchView.getQuery());
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



    private void setupListeners() {
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.requestLayout();
                if (isOpen) {
                    usersTableRelativeLayout.setVisibility(View.GONE);
                    hiddenTitleRelativeLayout.setVisibility(View.VISIBLE);
                    isOpen = false;
                }
            }
        });
        hiddenTitleRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen) {
                    usersTableRelativeLayout.setVisibility(View.VISIBLE);
                    hiddenTitleRelativeLayout.setVisibility(View.GONE);
                    isOpen = true;
                }
            }
        });
    }

    private void setupSearchView() {
        ArrayList<String> names = new ArrayList<>();
        for (User user : users) {
            names.add(user.getUsername());
        }
        adapter = new UsersListAdapter(getActivity(), names, users);
        listView.setAdapter(adapter);
        searchView.setIconified(false);
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createUserDropdownMenu(view,((User) listView.getAdapter().getItem(position)).isConnected());
            }
        });
    }

    private void createUserDropdownMenu(View view, boolean isConnected) {
        PopupMenu dropDownMenu = new PopupMenu(getActivity(), view);
        if (isConnected)
            dropDownMenu.getMenuInflater().inflate(R.menu.users_list_connected_entry_menu, dropDownMenu.getMenu());
        else
            dropDownMenu.getMenuInflater().inflate(R.menu.users_list_disconnected_entry_menu, dropDownMenu.getMenu());
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.inviteToFriend:
                        break;
                    case R.id.inviteToConversation:

                        break;
                    case R.id.inviteToDrawSession:

                        break;
                }
                return true;
            }
        });
        dropDownMenu.show();
    }
}












