package com.example.springclient.fragment.admin.dishes;

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
import com.example.springclient.MainViewModel;
import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.model.Dishes;
import com.example.springclient.websocket.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class AdminDishesFragment extends Fragment {

    private AdminDishesAdapter adminDishesAdapter;
    private AdminDishesViewModel adminDishesViewModel;
    private MainViewModel mainViewModel;

    private GenericWebSocketClient webSocketClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_dishes, container, false);
        ListView listViewDishes = view.findViewById(R.id.list_view_dishes);
        // Initialize ViewModel
        adminDishesViewModel = new ViewModelProvider(this).get(AdminDishesViewModel.class);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        // Observe changes in the dishes list
        adminDishesViewModel.getDishesList().observe(getViewLifecycleOwner(), new Observer<List<Dishes>>() {
            @Override
            public void onChanged(List<Dishes> dishes) {
                if (dishes != null) {
                    Log.d("DishesFragment", "Dishes list size: " + dishes.size());
                } else {
                    Log.d("DishesFragment", "Dishes list is null");
                }

                if (adminDishesAdapter == null) {
                    adminDishesAdapter = new AdminDishesAdapter(getContext(), dishes, adminDishesViewModel);
                    listViewDishes.setAdapter(adminDishesAdapter);
                } else {
                    adminDishesAdapter.updateDishesList(dishes);
                    Log.d("DishesFragment", "адаптер обновлен");
                    listViewDishes.setAdapter(adminDishesAdapter);
                }
            }
        });


        mainViewModel.getWebSocketMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                Log.d("DishFragment", "WebSocket message received: " + message);
                handleWebSocketMessage(message);
            }
        });

        // Setup WebSocket
        //setupWebSocket();

        // Initialize buttons
        FloatingActionButton buttonSwitchToDishesSave = view.findViewById(R.id.button_Switch_To_Dishes_add);
        buttonSwitchToDishesSave.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new AdminDishesSaveFragment(), "Сохранить блюдо");
            }
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
                        mainViewModel.setWebSocketMessage(message);
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
            // Неправильный формат сообщения
            return;
        }

        String messageType = parts[0];
        String dishJson = parts[1];

        try {
            Dishes dish = gson.fromJson(dishJson, Dishes.class);
            switch (messageType) {
                case "DISH_DELETED":
                    adminDishesAdapter.removeDishById(dish.getId());
                    adminDishesViewModel.loadDishes();
                    break;
                case "DISH_ADDED":
                    adminDishesAdapter.addDishById(dish);
                    break;
                case "DISH_UPDATED":
                    adminDishesAdapter.updateDish(dish);
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
            ((MainActivity) getActivity()).setToolbarTitle("Блюда");
            adminDishesViewModel.loadDishes();
        }
    }
}