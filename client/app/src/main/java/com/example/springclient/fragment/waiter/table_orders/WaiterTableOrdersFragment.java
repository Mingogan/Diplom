package com.example.springclient.fragment.waiter.table_orders;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.springclient.CustomDateTypeAdapter;

import com.example.springclient.MainActivity;
import com.example.springclient.R;
import com.example.springclient.fragment.waiter.order.WaiterOrdersViewModel;
import com.example.springclient.fragment.waiter.tables.WaiterTablesFragment;
import com.example.springclient.model.Orders;
import com.example.springclient.model.Tables;
import com.example.springclient.model.TablesStatus;
import com.example.springclient.model.Users;
import com.example.springclient.websocket.GenericWebSocketClient;
import com.example.springclient.websocket.WebSocketClientSingleton;
import com.example.springclient.websocket.WebSocketListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.util.Date;
import java.util.List;

public class WaiterTableOrdersFragment extends Fragment {

    private GenericWebSocketClient webSocketClient;
    private WaiterTableOrdersAdapter waiterTableOrdersAdapter;
    private WaiterOrdersViewModel waiterOrdersViewModel;
    private static final String ARG_USER = "user";
    private static final String ARG_TABLE_ID = "tableId";
    private int tableId;
    private static final String ARG_TABLE_NUMBER = "tableNumber";
    private int tableNumber;
    private Users user;

    public static WaiterTableOrdersFragment newInstance(Users user, int tableId, int tableNumber) {
        WaiterTableOrdersFragment fragment = new WaiterTableOrdersFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        args.putString(ARG_USER, userJson);
        args.putInt(ARG_TABLE_ID, tableId);
        args.putInt(ARG_TABLE_NUMBER, tableNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String userJson = getArguments().getString(ARG_USER);
            Gson gson = new Gson();
            user = gson.fromJson(userJson, Users.class);
            tableId = getArguments().getInt(ARG_TABLE_ID);
            tableNumber = getArguments().getInt(ARG_TABLE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.waiter_fragment_table_orders, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar_table_Orders);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.drawable.ic_back); // assuming you have this icon
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        toolbar.setTitle("Стол №: " + tableNumber);
        ListView listViewOrders = view.findViewById(R.id.listViewTableOrders);
        Button buttonAddOrder = view.findViewById(R.id.buttonAddOrder);
        Button buttonCloseTable = view.findViewById(R.id.buttonCloseTable); // Add button to close table

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupToolbar(toolbar);
        }

        waiterOrdersViewModel = new ViewModelProvider(this).get(WaiterOrdersViewModel.class);

        buttonAddOrder.setOnClickListener(v -> {
            waiterOrdersViewModel.createOrder(tableId, user.getId());
        });

        buttonCloseTable.setOnClickListener(v -> {
            if (canCloseTable()) {
                closeTable();
            } else {
                Toast.makeText(getContext(), "Cannot close table. There are unpaid orders.", Toast.LENGTH_SHORT).show();
            }
        });

        waiterOrdersViewModel.getOrderListById().observe(getViewLifecycleOwner(), new Observer<List<Orders>>() {
            @Override
            public void onChanged(List<Orders> orders) {
                if (orders != null) {
                    Log.d("WaiterOrderFragment", "Orders list size: " + orders.size());
                } else {
                    Log.d("WaiterOrderFragment", "Orders list is null");
                }
                if (waiterTableOrdersAdapter == null) {
                    waiterTableOrdersAdapter = new WaiterTableOrdersAdapter(getContext(), orders, waiterOrdersViewModel);
                    listViewOrders.setAdapter(waiterTableOrdersAdapter);
                } else {
                    waiterTableOrdersAdapter.updateOrdersList(orders);
                    listViewOrders.setAdapter(waiterTableOrdersAdapter);
                    Log.d("WaiterOrderFragment", "Adapter updated");
                }
            }
        });

        waiterOrdersViewModel.getWebSocketMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleWebSocketMessage(message);
            }
        });

        setupWebSocket();

        waiterOrdersViewModel.loadOrdersById(user.getId(), tableId);

        return view;
    }

    private boolean canCloseTable() {
        List<Orders> orders = waiterTableOrdersAdapter.getOrdersList();
        for (Orders order : orders) {
            if (!"Оплачен".equals(order.getStatus().getStatusName())) {
                return false;
            }
        }
        return true;
    }

    private void closeTable() {
        List<Orders> orders = waiterTableOrdersAdapter.getOrdersList();
        for (Orders order : orders) {
            order.getStatus().setStatusName("Завершен"); // Assuming you have a Status class with this constructor
            waiterOrdersViewModel.updateOrderStatus(order.getId(),5);
        }
        Tables updateTable = new Tables();
        updateTable.setId(tableId);
        updateTable.setTableNumber(tableNumber);
        TablesStatus tablesStatus = new TablesStatus();
        tablesStatus.setId(1);
        tablesStatus.setStatusName("Свободен");
        updateTable.setStatus(tablesStatus);
        waiterOrdersViewModel.updateTable(updateTable);
        getActivity().onBackPressed();
    }

    private void setupWebSocket() {
        GenericWebSocketClient webSocketClient = WebSocketClientSingleton.getInstance();
        webSocketClient.setWebSocketListener(new WebSocketListener() {
            @Override
            public void onMessage(String message) {
                Log.d("WaiterTableOrdersFragment", "WebSocket message received: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        waiterOrdersViewModel.setWebSocketMessage(message);
                        waiterOrdersViewModel.loadOrdersById(user.getId(), tableId);
                    });
                } else {
                    Log.d("WaiterOrdersDetailsFragment", "getActivity() is null");
                }
            }
        });
    }

    private void handleWebSocketMessage(String message) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new CustomDateTypeAdapter())
                .create();

        String[] parts = message.split(":", 2);

        if (parts.length < 2) {
            return;
        }

        String messageType = parts[0];
        String orderJson = parts[1];
        try {
            Orders order = gson.fromJson(orderJson, Orders.class);
            switch (messageType) {
                case "ORDER_DELETED":
                    waiterTableOrdersAdapter.removeOrderById(order.getId());
                    break;
                case "ORDER_ADDED":
                    waiterTableOrdersAdapter.addOrder(order);
                    //waiterOrdersViewModel.loadOrdersById(user.getId(), tableId);
                    break;
                case "ORDER_UPDATED":
                    waiterTableOrdersAdapter.updateOrder(order);
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
            ((MainActivity) getActivity()).setToolbarTitle("Orders");
            waiterOrdersViewModel.loadOrdersById(user.getId(), tableId);
        }
    }
}