package com.example.springclient.fragment.waiter.order;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.springclient.R;
import com.example.springclient.fragment.waiter.order_details.WaiterOrdersDetailsFragment;
import com.example.springclient.model.OrderDetails;
import com.example.springclient.model.Orders;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WaiterOrdersAdapter extends ArrayAdapter<Orders> {

    private final SimpleDateFormat originalFormat;
    private final SimpleDateFormat timeFormat;
    private Context context;
    private List<Orders> ordersList;
    private WaiterOrdersViewModel waiterOrderViewModel;
    private LifecycleOwner lifecycleOwner;
    private Map<Integer, Observer<List<OrderDetails>>> observerMap = new HashMap<>();

    public WaiterOrdersAdapter(Context context, List<Orders> ordersList, WaiterOrdersViewModel waiterOrderViewModel, LifecycleOwner lifecycleOwner) {
        super(context, R.layout.waiter_order_item_list, ordersList);
        this.context = context;
        this.ordersList = ordersList;
        this.waiterOrderViewModel = waiterOrderViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.originalFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.ENGLISH);
        this.timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        this.originalFormat.setTimeZone(TimeZone.getTimeZone("Asia/Novosibirsk")); // Устанавливаем временную зону для Новосибирска
        this.timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Novosibirsk"));
        sortOrdersList();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.waiter_order_item_list, parent, false);
        }

        Orders order = ordersList.get(position);

        TextView textViewTableNumber = convertView.findViewById(R.id.textViewTableNumber);
        TextView textViewOrderNumber = convertView.findViewById(R.id.textViewOrderNumber);
        TextView textViewOrderStatus = convertView.findViewById(R.id.textViewOrderStatus);
        TextView textViewWaiterName = convertView.findViewById(R.id.textViewWaiterName);
        TextView textViewOrderDetails = convertView.findViewById(R.id.textViewOrderDetails);
        TextView textViewTotalCost = convertView.findViewById(R.id.textViewTotalCost);

        textViewTableNumber.setText("Стол №: " + order.getTable().getTableNumber());
        textViewOrderNumber.setText("Заказ №: " + order.getId());
        textViewOrderStatus.setText(order.getStatus().getStatusName());
        textViewWaiterName.setText("Официант: " + order.getWaiter().getFirstName()+" " + order.getWaiter().getLastName());
        TextView textViewOrderTime = convertView.findViewById(R.id.textViewOrderTime);
        textViewTotalCost.setText("Стоимость заказа: ");
        try {
            String formattedTime = timeFormat.format(order.getDateOfCreation());
            textViewOrderTime.setText(formattedTime);
        } catch (Exception e) {
            textViewOrderTime.setText(order.getDateOfCreation().toString());
            Log.d("WaiterTableOrderFragment", order.getDateOfCreation().toString());// set the original string if formatting fails
        }

        if (observerMap.containsKey(order.getId())) {
            waiterOrderViewModel.getOrderDetails(order.getId()).removeObserver(observerMap.get(order.getId()));
        }

        Observer<List<OrderDetails>> observer = orderDetails -> {
            if (orderDetails != null) {
                StringBuilder details = new StringBuilder();
                double totalCost = 0;
                int limit = Math.min(3, orderDetails.size());
                for (int i = 0; i < limit; i++) {
                    OrderDetails detail = orderDetails.get(i);
                    details.append(detail.getDish().getName());
                    if (i < limit - 1) {
                        details.append(", ");
                    }
                    totalCost += detail.getDish().getCost() * detail.getAmount();
                }
                textViewOrderDetails.setText(details.toString());

                textViewTotalCost.setText(textViewTotalCost.getText().toString()+ totalCost);
            }
        };

        // Store the observer in the map
        observerMap.put(order.getId(), observer);

        // Attach the observer to the LiveData
        if (lifecycleOwner != null) {
            waiterOrderViewModel.getOrderDetails(order.getId()).observe(lifecycleOwner, observer);
        } else {
            Log.e("WaiterOrdersAdapter", "LifecycleOwner is null");
        }


        convertView.setOnClickListener(v -> {
            FragmentActivity activity = (FragmentActivity) context;
            WaiterOrdersDetailsFragment fragment = WaiterOrdersDetailsFragment.newInstance(order.getId(),order.getStatus().getStatusName());
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return convertView;
    }

    public void updateOrdersList(List<Orders> newOrdersList) {
        ordersList.clear();
        ordersList.addAll(newOrdersList);
        sortOrdersList();
        notifyDataSetChanged();
    }

    public void removeOrderById(int orderId) {
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.get(i).getId() == orderId) {
                ordersList.remove(i);
                sortOrdersList();
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void addOrder(Orders order) {
        ordersList.add(order);
        sortOrdersList();
        notifyDataSetChanged();
    }

    public void updateOrder(Orders order) {
        int index = findOrderIndexById(order.getId());
        if (index != -1) {
            ordersList.set(index, order);
        }
        sortOrdersList();
        notifyDataSetChanged();
    }

    private int findOrderIndexById(int id) {
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void sortOrdersList() {
        Collections.sort(ordersList, new Comparator<Orders>() {
            @Override
            public int compare(Orders o1, Orders o2) {
                return o1.getDateOfCreation().compareTo(o2.getDateOfCreation());
            }
        });
    }
}
