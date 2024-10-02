package com.example.springclient.fragment.admin;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.example.springclient.MainActivity;
import com.example.springclient.MainViewModel;
import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.fragment.admin.categories.*;
import com.example.springclient.fragment.admin.dishes.*;
import com.example.springclient.fragment.admin.statisticka.OrderFilterFragment;
import com.example.springclient.fragment.admin.tables.*;
import com.example.springclient.fragment.admin.users.*;
import com.example.springclient.model.Categories;
import com.example.springclient.websocket.GenericWebSocketClient;
import com.example.springclient.websocket.WebSocketClientSingleton;
import com.example.springclient.websocket.WebSocketListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;

public class MainAdminFragment extends Fragment {

    private GenericWebSocketClient webSocketClient;
    private static final String TAG = "Главная";

    private MainViewModel mainViewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_main, container, false);

        Context context = getContext();
        if (context != null) {
            MainActivity.sendNotification(context, "Внимание!", "Добавлен новый заказ");
        }

        try {
            Toolbar toolbar = view.findViewById(R.id.admin_main_toolbar);
            toolbar.inflateMenu(R.menu.menu_waiter_main); // Подключаем меню к Toolbar
            toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);
            Spinner spinner = view.findViewById(R.id.admin_main_spinner);

            Menu menu = toolbar.getMenu();
            customizeMenu(menu);

            if (toolbar == null) {
                Log.e(TAG, "Toolbar is null");
            }
            if (spinner == null) {
                Log.e(TAG, "Spinner is null");
            }


            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.fragment_admin_titles, R.layout.spinner_item);
            adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinner.setAdapter(adapter);

            mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);


            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Fragment selectedFragment = null;
                    String fragmentTag = "";


                    switch (position) {
                        case 0:
                            selectedFragment = new AdminDishesFragment();
                            fragmentTag = "Блюда";
                            break;
                        case 1:
                            selectedFragment = new AdminUsersFragment();
                            fragmentTag = "Пользователи";
                            break;
                        case 2:
                            selectedFragment = new AdminTablesFragment();
                            fragmentTag = "Столы";
                            break;
                        case 3:
                            selectedFragment = new AdminCategoriesFragment();
                            fragmentTag = "Категории";
                            break;
                        case 4:
                            selectedFragment = new OrderFilterFragment();
                            fragmentTag = "Выручка";
                            break;
                    }

                    if (selectedFragment != null) {
                        getChildFragmentManager().beginTransaction()
                                .replace(R.id.fragment_admin_container, selectedFragment, fragmentTag)
                                .commit();
                    } else {
                        Log.e(TAG, "Selected fragment is null");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "вышло вебсокет твой", e);
        }
        try {
            setupWebSocket();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return view;
    }

    private void customizeMenu(Menu menu) {
        MenuItem firstMenuItem = menu.findItem(R.id.text_user_name);
        MenuItem logout = menu.findItem(R.id.action_logout);

        firstMenuItem.setTitle(MyApplication.getUser().getLastName() + " " + MyApplication.getUser().getFirstName());

        SpannableString s = new SpannableString(firstMenuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, s.length(), 0);
        firstMenuItem.setTitle(s);

        SpannableString p = new SpannableString(logout.getTitle());
        p.setSpan(new ForegroundColorSpan(Color.BLACK), 0, p.length(), 0);


        logout.setTitle(p);
    }

    private void setupWebSocket() throws URISyntaxException {
        URI uri = new URI("ws://5.130.148.107:8080/ws");
        String accessToken = MyApplication.getSharedPreferences().getString("accessToken", null);
        if (accessToken != null) {
            webSocketClient = WebSocketClientSingleton.getInstance(uri, accessToken);
            webSocketClient.setWebSocketListener(new WebSocketListener() {
                @Override
                public void onMessage(String message) {
                    Log.d("AdminMainFragment", "WebSocket message received: " + message);
                    mainViewModel.setWebSocketMessage(message);
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
        String categoryJson = parts[1];
        try {
            Categories dish = gson.fromJson(categoryJson, Categories.class);
            switch (messageType) {
                case "CATEGORY_DELETED":
                    break;
                case "CATEGORY_ADDED":
                    break;
                case "CATEGORY_UPDATED":
                    break;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        MyApplication.getSharedPreferences().edit().remove("accessToken").apply();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle(TAG);
        }
    }




}
