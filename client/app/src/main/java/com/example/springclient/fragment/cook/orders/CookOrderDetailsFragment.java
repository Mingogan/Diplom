package com.example.springclient.fragment.cook.orders;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.springclient.MainActivity;
import com.example.springclient.R;
import com.example.springclient.fragment.waiter.order_details.WaiterOrderCategoriesFragment;
import com.example.springclient.fragment.waiter.order_details.WaiterOrderDetailsAdapter;
import com.example.springclient.fragment.waiter.order_details.WaiterOrdersDetailsViewModel;
import com.example.springclient.model.OrderDetails;
import com.example.springclient.websocket.*;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;


public class CookOrderDetailsFragment extends Fragment {

    private CookOrderDetailsAdapter waiterOrderDetailsAdapter;
    private WaiterOrdersDetailsViewModel waiterOrdersDetailsViewModel;
    private static final String ARG_ORDER_ID = "order_id";
    private int orderId;
    private static final String ARG_ORDER_STATUS_NAME = "statusName";
    private String orderStatusName;
    private ListView listViewOrderDetails;

    public static CookOrderDetailsFragment newInstance(int orderId, String orderStatusName) {
        CookOrderDetailsFragment fragment = new CookOrderDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        args.putString(ARG_ORDER_STATUS_NAME, orderStatusName);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getInt(ARG_ORDER_ID);
            orderStatusName = getArguments().getString(ARG_ORDER_STATUS_NAME);
        }
        waiterOrdersDetailsViewModel = new ViewModelProvider(this).get(WaiterOrdersDetailsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cook_fragment_order_details, container, false);
        listViewOrderDetails = view.findViewById(R.id.list_view_cook_details);
        Toolbar toolbar = view.findViewById(R.id.toolbar_cook_details);
        toolbar.setNavigationIcon(R.drawable.ic_back); // assuming you have this icon
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        toolbar.setTitle("Заказ №: " + orderId);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setSubtitle(orderStatusName);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setupToolbar(toolbar);
        }

        // Observe changes in the order details list
        waiterOrdersDetailsViewModel.getOrderDetailsList(orderId).observe(getViewLifecycleOwner(), new Observer<List<OrderDetails>>() {
            @Override
            public void onChanged(List<OrderDetails> orderDetails) {
                if (orderDetails != null) {
                    if (waiterOrderDetailsAdapter == null){
                        waiterOrderDetailsAdapter = new CookOrderDetailsAdapter(getContext(), orderDetails, waiterOrdersDetailsViewModel);
                        listViewOrderDetails.setAdapter(waiterOrderDetailsAdapter);
                    } else {
                        waiterOrderDetailsAdapter.updateOrderDetailsList(orderDetails);
                        listViewOrderDetails.setAdapter(waiterOrderDetailsAdapter);
                        Log.d("WaiterOrderFragment", "Adapter updated");
                    }
                }
            }
        });

        waiterOrdersDetailsViewModel.getWebSocketMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleWebSocketMessage(message);
            }
        });

        setupWebSocket();

        return view;
    }

    private void setupWebSocket() {
        GenericWebSocketClient webSocketClient = WebSocketClientSingleton.getInstance();
        webSocketClient.setWebSocketListener(new WebSocketListener() {
            @Override
            public void onMessage(String message) {
                Log.d("WaiterOrdersDetailsFragment", "WebSocket message received: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        waiterOrdersDetailsViewModel.setWebSocketMessage(message);
                        waiterOrdersDetailsViewModel.loadOrderDetails(orderId);
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
        String orderDetailsJson = parts[1];

        try {
            OrderDetails orderDetails = gson.fromJson(orderDetailsJson, OrderDetails.class);

            switch (messageType) {
                case "ORDER_DETAILS_DELETED":
                    waiterOrderDetailsAdapter.removeOrderDetailsById(orderDetails.getId());
                    break;
                case "ORDER_DETAILS_ADDED":
                    waiterOrdersDetailsViewModel.loadOrderDetails(orderId);
                    break;
                case "ORDER_DETAILS_UPDATED":
                    waiterOrderDetailsAdapter.updateOrderDetails(orderDetails);
                    break;
                default:
                    // Unknown message type
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
            ((MainActivity) getActivity()).setToolbarTitle("Order Details");
            waiterOrdersDetailsViewModel.loadOrderDetails(orderId);
        }
    }
}
