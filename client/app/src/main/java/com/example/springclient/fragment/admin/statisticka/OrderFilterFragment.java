package com.example.springclient.fragment.admin.statisticka;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.model.OrderDetails;
import com.example.springclient.model.Orders;
import com.example.springclient.retrofit.ServerApi;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFilterFragment extends Fragment {

    private TextView textStartDate, textEndDate;
    private TextView textViewResults;
    private ListView listViewOrders;
    private Calendar startDate, endDate;
    private SimpleDateFormat dateFormat;
    private ServerApi serverApi = MyApplication.getServerAPI();
    private OrderListAdapter orderListAdapter;
    private static final String TAG = "OrderFilterFragment";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_filter, container, false);

        textStartDate = view.findViewById(R.id.textStartDate);
        textEndDate = view.findViewById(R.id.textEndDate);
        Button buttonLoadOrders = view.findViewById(R.id.revenueСalculation);
        listViewOrders = view.findViewById(R.id.list_view_orders);
        textViewResults = view.findViewById(R.id.textViewResults);

        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
        textStartDate.setOnClickListener(v -> showDatePickerDialog(true));
        textEndDate.setOnClickListener(v -> showDatePickerDialog(false));
        buttonLoadOrders.setOnClickListener(v -> loadOrders());

        return view;
    }

    private void showDatePickerDialog(boolean isStartDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            if (isStartDate) {
                startDate = calendar;
                textStartDate.setText(dateFormat.format(startDate.getTime()));
            } else {
                endDate = calendar;
                textEndDate.setText(dateFormat.format(endDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void loadOrders() {
        if (startDate == null || endDate == null) {
            textViewResults.setText("Пожалуйста, выберите обе даты.");
            return;
        }

        String start = dateFormat.format(startDate.getTime());
        String end = dateFormat.format(endDate.getTime());

        serverApi.getOrdersByDateRange(start, end).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Orders> orders = response.body();
                    calculateOrderCosts(orders);
                } else {
                    textViewResults.setText("Ошибка загрузки заказов");
                }
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                textViewResults.setText("Ошибка загрузки заказов: " + t.getMessage());
            }
        });
    }

    private void calculateOrderCosts(List<Orders> orders) {
        List<Orders> updatedOrders = new ArrayList<>();
        final double[] totalRevenue = {0};

        for (Orders order : orders) {
            serverApi.getOrderDetails(order.getId()).enqueue(new Callback<List<OrderDetails>>() {
                @Override
                public void onResponse(Call<List<OrderDetails>> call, Response<List<OrderDetails>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        double orderTotalCost = 0;
                        for (OrderDetails details : response.body()) {
                            orderTotalCost += details.getDish().getCost() * details.getAmount();
                        }
                        order.setTotalCost(orderTotalCost); // Если у вас нет метода setTotalCost, добавьте его в класс Orders
                        totalRevenue[0] += orderTotalCost;
                        updatedOrders.add(order);

                        if (updatedOrders.size() == orders.size()) {
                            displayOrders(updatedOrders, totalRevenue[0]);
                        }
                    } else {
                        textViewResults.setText("Ошибка загрузки деталей заказа");
                    }
                }

                @Override
                public void onFailure(Call<List<OrderDetails>> call, Throwable t) {
                    textViewResults.setText("Ошибка загрузки деталей заказа: " + t.getMessage());
                }
            });
        }
    }

    private void displayOrders(List<Orders> orders, double totalRevenue) {
        orderListAdapter = new OrderListAdapter(getContext(), orders);
        listViewOrders.setAdapter(orderListAdapter);
        textViewResults.setText(String.format("Выручка за указанный период: %.2f", totalRevenue));
    }
}