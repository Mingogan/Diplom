package com.example.springclient.fragment.admin.tables;

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
import android.widget.GridView;

import com.example.springclient.MainActivity;
import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.model.Tables;
import com.example.springclient.websocket.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class AdminTablesFragment extends Fragment {

    private GenericWebSocketClient webSocketClient;
    private AdminTablesAdapter adminTablesAdapter;
    private AdminTablesViewModel adminTablesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_tables, container, false);
        GridView gridViewTables = view.findViewById(R.id.grid_view_tables);
        adminTablesViewModel = new ViewModelProvider(requireActivity()).get(AdminTablesViewModel.class);

        adminTablesViewModel.getTablesList().observe(getViewLifecycleOwner(), new Observer<List<Tables>>() {
            @Override
            public void onChanged(List<Tables> tables) {
                if (tables != null) {
                    Log.d("TablesFragment", "Tables list size: " + tables.size());
                } else {
                    Log.d("TablesFragment", "Tables list is null");
                }

                if (adminTablesAdapter == null) {
                    adminTablesAdapter = new AdminTablesAdapter(getContext(), tables,adminTablesViewModel);
                    gridViewTables.setAdapter(adminTablesAdapter);
                } else {
                    adminTablesAdapter.updateTablesList(tables);
                }
            }
        });


        adminTablesViewModel.getWebSocketMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleWebSocketMessage(message);
            }
        });

        setupWebSocket();

        FloatingActionButton buttonSwitchToTableSave = view.findViewById(R.id.button_Switch_To_Tables_add);
        buttonSwitchToTableSave.setOnClickListener(v -> {
            new AdminTablesSaveDialog().show(getChildFragmentManager(), "AddTableDialog");
        });

        return view;
    }

    private void setupWebSocket() {
        GenericWebSocketClient webSocketClient = WebSocketClientSingleton.getInstance();
        webSocketClient.setWebSocketListener(new WebSocketListener() {
            @Override
            public void onMessage(String message) {
                Log.d("WaiterTableOrdersFragment", "WebSocket message received: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        adminTablesViewModel.setWebSocketMessage(message);
                    });
                } else {
                    Log.d("WaiterOrdersDetailsFragment", "getActivity() is null");
                }
            }
        });
    }

    private void handleWebSocketMessage(String message) {
        Gson gson = new Gson();
        String[] parts = message.split(":", 2);

        if (parts.length < 2) {
            return;
        }

        String messageType = parts[0];
        String tableJson = parts[1];

        try {
            Tables table = gson.fromJson(tableJson, Tables.class);
            switch (messageType) {
                case "TABLE_DELETED":
                    adminTablesAdapter.removeTableById(table.getId());
                    break;
                case "TABLE_ADDED":
                    adminTablesAdapter.addTable(table);
                    break;
                case "TABLE_UPDATED":
                    adminTablesAdapter.updateTable(table);
                    break;
                default:
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
            ((MainActivity) getActivity()).setToolbarTitle("Столы");
            adminTablesViewModel.loadTables();
        }
    }
}