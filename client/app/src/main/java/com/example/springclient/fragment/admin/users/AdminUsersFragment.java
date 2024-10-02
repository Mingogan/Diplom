package com.example.springclient.fragment.admin.users;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.springclient.MainActivity;
import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.fragment.admin.users.adapters.AdminUsersAdapter;
import com.example.springclient.model.Users;
import com.example.springclient.websocket.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class AdminUsersFragment extends Fragment {

    private AdminUsersAdapter adminUsersAdapter;
    private AdminUsersViewModel adminUsersViewModel;
    private GenericWebSocketClient webSocketClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_users, container, false);
        ListView listViewUsers = view.findViewById(R.id.list_view_users);

        adminUsersViewModel = new ViewModelProvider(requireActivity()).get(AdminUsersViewModel.class);

        adminUsersViewModel.getUsersList().observe(getViewLifecycleOwner(), new Observer<List<Users>>() {
            @Override
            public void onChanged(List<Users> users) {
                if (users != null) {
                    Log.d("UsersFragment", "Users list size: " + users.size());
                } else {
                    Log.d("UsersFragment", "Users list is null");
                }

                if (adminUsersAdapter == null) {
                    adminUsersAdapter = new AdminUsersAdapter(getContext(), users, adminUsersViewModel);
                    listViewUsers.setAdapter(adminUsersAdapter);
                } else {
                    adminUsersAdapter.updateUsersList(users);
                    listViewUsers.setAdapter(adminUsersAdapter);
                }
            }
        });

        adminUsersViewModel.getWebSocketMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleWebSocketMessage(message);
            }
        });


        setupWebSocket();

        adminUsersViewModel.loadUsers();

        FloatingActionButton buttonSwitchToUserSave = view.findViewById(R.id.button_Switch_To_Users_add);
        buttonSwitchToUserSave.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new AdminUsersSaveFragment(), "Сохранить блюдо");
            }
        });

        return view;
    }

    private void setupWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://5.130.148.107:8080/ws");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        String accessToken = MyApplication.getSharedPreferences().getString("accessToken", null);

        if (accessToken != null) {
            webSocketClient = WebSocketClientSingleton.getInstance(uri, accessToken);
            webSocketClient.setWebSocketListener(new WebSocketListener() {
                @Override
                public void onMessage(String message) {
                    Log.d("CategoriesFragment", "WebSocket message received: " + message);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        adminUsersViewModel.setWebSocketMessage(message);
                    });

                }
            });
        }
    }

    private void handleWebSocketMessage(String message) {
        Gson gson = new Gson();
        String[] parts = message.split(":", 2);
        if (parts.length < 2) {
            return;
        }
        String messageType = parts[0];
        String userJson = parts[1];
        try {
            Users user = gson.fromJson(userJson, Users.class);
            switch (messageType) {
                case "USER_DELETED":
                    adminUsersAdapter.removeUserById(user.getId());
                    break;
                case "USER_ADDED":
                    adminUsersAdapter.addUser(user);
                    break;
                case "USER_UPDATED":
                    adminUsersAdapter.updateUser(user);
                    break;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Пользователи");
            adminUsersViewModel.loadUsers();
        }
    }
}
