package com.example.springclient.fragment.waiter.tables;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.springclient.MainActivity;
import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.fragment.admin.tables.AdminTablesAdapter;
import com.example.springclient.fragment.admin.tables.AdminTablesSaveDialog;
import com.example.springclient.fragment.admin.tables.AdminTablesViewModel;
import com.example.springclient.fragment.waiter.WaiterMainFragment;
import com.example.springclient.model.Tables;
import com.example.springclient.model.Users;
import com.example.springclient.websocket.GenericWebSocketClient;
import com.example.springclient.websocket.WebSocketClientSingleton;
import com.example.springclient.websocket.WebSocketListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class WaiterTablesFragment extends Fragment {

    private GenericWebSocketClient webSocketClient;
    private WaiterTablesAdapter waiterTablesAdapter;
    private WaiterTablesViewModel waiterTablesViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.waiter_fragment_tables, container, false);
        GridView gridViewTables = view.findViewById(R.id.grid_view_tables);
        waiterTablesViewModel = new ViewModelProvider(requireActivity()).get(WaiterTablesViewModel.class);

        waiterTablesViewModel.getTablesList().observe(getViewLifecycleOwner(), new Observer<List<Tables>>() {
            @Override
            public void onChanged(List<Tables> tables) {
                if (tables != null) {
                    Log.d("TablesFragment", "Tables list size: " + tables.size());
                } else {
                    Log.d("TablesFragment", "Tables list is null");
                }

                if (waiterTablesAdapter == null) {
                    waiterTablesAdapter = new WaiterTablesAdapter(getContext(), tables, waiterTablesViewModel, getViewLifecycleOwner());
                    gridViewTables.setAdapter(waiterTablesAdapter);
                } else {
                    waiterTablesAdapter.updateTablesList(tables);
                }
            }
        });

        setupWebSocket();

        waiterTablesViewModel.getWebSocketMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleWebSocketMessage(message);
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
                    Log.d("TablesFragment", "WebSocket message received: " + message);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        waiterTablesViewModel.setWebSocketMessage(message);
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
        String tableJson = parts[1];

        try {
            Tables table = gson.fromJson(tableJson, Tables.class);
            switch (messageType) {
                case "TABLE_DELETED":
                    waiterTablesAdapter.removeTableById(table.getId());
                    break;
                case "TABLE_ADDED":
                    waiterTablesAdapter.addTable(table);
                    break;
                case "TABLE_UPDATED":
                    waiterTablesAdapter.updateTable(table);
                    break;
                default:
                    // Неизвестный тип сообщения
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
            waiterTablesViewModel.loadTables();
        }
    }
}