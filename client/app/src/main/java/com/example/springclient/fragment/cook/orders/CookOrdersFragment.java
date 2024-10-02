package com.example.springclient.fragment.cook.orders;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.springclient.MainActivity;
import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.fragment.waiter.order.WaiterOrdersViewModel;
import com.example.springclient.model.Orders;
import com.example.springclient.websocket.GenericWebSocketClient;
import com.example.springclient.websocket.WebSocketClientSingleton;
import com.example.springclient.websocket.WebSocketListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class CookOrdersFragment extends Fragment {

    private GenericWebSocketClient webSocketClient;
    private CookOrdersAdapter waiterOrderAdapter;
    private WaiterOrdersViewModel cookOrdersViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.waiter_my_orders_fragment, container, false);
        ListView listViewOrders = view.findViewById(R.id.list_view_orders);

        cookOrdersViewModel = new ViewModelProvider(this).get(WaiterOrdersViewModel.class);

        cookOrdersViewModel.loadOrders();

        cookOrdersViewModel.getOrderList().observe(getViewLifecycleOwner(), new Observer<List<Orders>>() {
            @Override
            public void onChanged(List<Orders> orders) {
                if (orders != null) {
                    Log.d("WaiterOrderFragment", "Orders list size: " + orders.size());
                    if (waiterOrderAdapter == null) {
                        waiterOrderAdapter = new CookOrdersAdapter(getContext(), orders, cookOrdersViewModel, getViewLifecycleOwner());
                        listViewOrders.setAdapter(waiterOrderAdapter);
                    } else {
                        waiterOrderAdapter.updateOrdersList(orders);
                        Log.d("WaiterOrderFragment", "Adapter updated");
                    }
                } else {
                    Log.d("WaiterOrderFragment", "Orders list is null");
                }
            }
        });

        cookOrdersViewModel.getWebSocketMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleWebSocketMessage(message);
            }
        });

        setupWebSocket();
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
                        cookOrdersViewModel.setWebSocketMessage(message);
                        cookOrdersViewModel.loadMyOrders(MyApplication.getUser().getId());
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
            Orders orders = gson.fromJson(userJson, Orders.class);
            switch (messageType) {
                case "USER_DELETED":
                    waiterOrderAdapter.removeOrderById(orders.getId());
                    break;
                case "ORDER_ADDED":
                    waiterOrderAdapter.addOrder(orders);
                    break;
                case "UPDATED_UPDATED":
                    waiterOrderAdapter.updateOrder(orders);
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
            ((MainActivity) getActivity()).setToolbarTitle("Orders");
            cookOrdersViewModel.loadOrders();
        }
    }
}
