package com.example.springclient.fragment.cook;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import com.example.springclient.MainActivity;
import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.fragment.cook.orders.CookOrdersFragment;
import com.example.springclient.fragment.waiter.order.WaiterOrdersFragment;
import com.example.springclient.model.Categories;
import com.example.springclient.websocket.GenericWebSocketClient;
import com.example.springclient.websocket.WebSocketClientSingleton;
import com.example.springclient.websocket.WebSocketListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;

public class CookMainFragment extends Fragment {
    private GenericWebSocketClient webSocketClient;
    private static final String TAG = "Главная официантов";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cook_main_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.cook_main_toolbar);
        toolbar.inflateMenu(R.menu.menu_waiter_main); // Подключаем меню к Toolbar
        toolbar.setOnMenuItemClickListener(this::onOptionsItemSelected);

        toolbar.setTitle("Заказы");
        if (toolbar == null) {
            Log.e(TAG, "Toolbar is null");
        }
        Fragment selectedFragment = new CookOrdersFragment();
        String fragmentTag = "Заказы";
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, selectedFragment, fragmentTag)
                .commit();
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
                    Log.d("CookMainFragment", "WebSocket message received: " + message);
                    new Handler(Looper.getMainLooper()).post(() -> handleWebSocketMessage(message));
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
                    sendNotification("Категория", "Добавлена категория " + dish.getName());
                    break;
                case "CATEGORY_UPDATED":
                    break;
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String title, String message) {
        Context context = getContext();
        if (context != null) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification notification = new NotificationCompat.Builder(context, "websocket_channel")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            notificationManager.notify(0, notification);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle(TAG);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
}
