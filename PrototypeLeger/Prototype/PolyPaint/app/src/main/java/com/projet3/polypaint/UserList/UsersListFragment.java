package com.projet3.polypaint.UserList;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.support.v7.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.projet3.polypaint.Network.RequestManager;
import com.projet3.polypaint.Network.SocketManager;
import com.projet3.polypaint.R;
import com.projet3.polypaint.Network.FetchManager;


public class UsersListFragment extends Fragment implements UsersListListener {

    private ListView listView;
    private RelativeLayout usersTableRelativeLayout;
    private RelativeLayout hiddenTitleRelativeLayout;
    private View rootView;
    private TextView title;
    private Spinner filterSpinner;
    //private ArrayList<User> users;
    private SearchView searchView;
    private UsersListAdapter adapter;

    private boolean isOpen;


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
        SocketManager.currentInstance.setupUsersListListener(this);
       // users = RequestManager.currentInstance.fetchUsers();
       /* users = new ArrayList<>();
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
        users.add(new User("Alice2", false));*/
        isOpen = true;

        setupListeners();
        setupSpinner();
        setupListView();
        setupSearchView();
        return rootView;
    }
    private void setupSpinner() {
        //ArrayList<String> strings = new ArrayList<>();
        //strings.add("Tous");
        //strings.add("En ligne");
        //strings.add("Hors ligne");
        android.widget.ArrayAdapter<String> spinnerArrayAdapter = new android.widget.ArrayAdapter<>
                (getContext(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.userListFilter));
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
    private void setupListView(){
       // ArrayList<String> names = new ArrayList<>();
        //for (User user : FetchManager.currentInstance.getUsers()) {
          //  names.add(user.getUsername());
        //}
        adapter = new UsersListAdapter(getActivity(),FetchManager.currentInstance.getUsersNames(), FetchManager.currentInstance.getUsers());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                createUserDropdownMenu(view,((User) listView.getAdapter().getItem(position)));
            }
        });
    }
    private void setupSearchView() {
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
    }

    private void createUserDropdownMenu(View view, final User user) {
        PopupMenu dropDownMenu = new PopupMenu(getActivity(), view);
        if (user.isConnected())
            dropDownMenu.getMenuInflater().inflate(R.menu.users_list_connected_entry_menu, dropDownMenu.getMenu());
        else
            return;
            //dropDownMenu.getMenuInflater().inflate(R.menu.users_list_disconnected_entry_menu, dropDownMenu.getMenu());
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.inviteToConversation:
                        LayoutInflater inflater =(LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true;
                        final View popupView = inflater.inflate(R.layout.invite_to, null);
                        //TextView text = (TextView)popupView.findViewById(R.id.inviteInTextView);
                        //text.setText("Entrez le nom de la conversation :");
                        final EditText editText = (EditText)popupView.findViewById(R.id.inviteInEditText);
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (v != popupView){
                                    popupWindow.dismiss() ;
                                }
                                return true;
                            }
                        });
                        Button acceptButton = (Button)popupView.findViewById(R.id.inviteToButton);
                        acceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (!editText.getText().toString().isEmpty()){
                                    SocketManager.currentInstance.sendInviteToConversation(user.getUsername(),editText.getText().toString());
                                    popupWindow.dismiss();
                                }
                                else
                                    Toast.makeText(getContext(),"veuillez entrer un nom de conversation valide",Toast.LENGTH_LONG).show();
                            }
                        });
                        break;
                    case R.id.inviteToDrawSession:
                        SocketManager.currentInstance.sendInviteToDrawingSession(user.getUsername());
                        break;
                }
                return true;
            }
        });
        dropDownMenu.show();
    }

    @Override
    public void onUserConnected(String username) {
        if (FetchManager.currentInstance.changeConnectedState(username, true)){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupListView();
                }
            });
        }


    }
}












