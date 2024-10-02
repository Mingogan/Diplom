package com.example.springclient.fragment.admin.categories;

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
import com.example.springclient.model.Categories;


import com.example.springclient.websocket.*;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;



public class AdminCategoriesFragment extends Fragment {

    private AdminCategoriesAdapter adminCategoriesAdapter;
    private AdminCategoriesViewModel adminCategoriesViewModel;

    private GenericWebSocketClient webSocketClient;

    private MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_categories, container, false);
        ListView listViewCategories = view.findViewById(R.id.listViewCategories);

        adminCategoriesViewModel = new ViewModelProvider(this).get(AdminCategoriesViewModel.class);

        adminCategoriesViewModel.getCategoriesList().observe(getViewLifecycleOwner(), new Observer<List<Categories>>() {
            @Override
            public void onChanged(List<Categories> categories) {
                if (categories != null) {
                    Log.d("CategoriesFragment", "Categories list size: " + categories.size());
                } else {
                    Log.d("CategoriesFragment", "Categories list is null");
                }

                if (adminCategoriesAdapter == null) {
                    adminCategoriesAdapter = new AdminCategoriesAdapter(getContext(), categories, adminCategoriesViewModel);
                    listViewCategories.setAdapter(adminCategoriesAdapter);
                } else {
                    adminCategoriesAdapter.updateCategoriesList(categories);
                    listViewCategories.setAdapter(adminCategoriesAdapter);
                    Log.d("CategoriesFragment", "адаптер обновлен");
                }
            }
        });
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        mainViewModel.getWebSocketMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                handleWebSocketMessage(message);
            }
        });

        FloatingActionButton buttonSwitchToCategoriesSave = view.findViewById(R.id.button_Switch_To_Categories_add);
        buttonSwitchToCategoriesSave.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).loadFragment(new AdminCategoriesSaveFragment(), "Сохранить категорию");
            }
        });

        return view;
    }

    private void setupWebSocket() {
        GenericWebSocketClient webSocketClient = WebSocketClientSingleton.getInstance();
        Log.d("CategoriesFragment", "SETUP");
        webSocketClient.setWebSocketListener(new WebSocketListener() {
            @Override
            public void onMessage(String message) {
                Log.d("WaiterTableOrdersFragment", "WebSocket message received: " + message);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        adminCategoriesViewModel.setWebSocketMessage(message);
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
        String categoryJson = parts[1];
        try {
            Categories dish = gson.fromJson(categoryJson, Categories.class);
            switch (messageType) {
                case "CATEGORY_DELETED":
                    adminCategoriesAdapter.removeCategoryById(dish.getId());
                    break;
                case "CATEGORY_ADDED":
                    adminCategoriesAdapter.addCategoryById(dish);
                    break;
                case "CATEGORY_UPDATED":
                    adminCategoriesAdapter.updateCategory(dish);
                    break;
            }
        }catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Категории");
            adminCategoriesViewModel.loadCategories();
        }

    }
}