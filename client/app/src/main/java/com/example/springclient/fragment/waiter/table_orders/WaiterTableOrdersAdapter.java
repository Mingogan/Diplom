package com.example.springclient.fragment.waiter.table_orders;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.springclient.R;
import com.example.springclient.fragment.waiter.order.WaiterOrdersViewModel;
import com.example.springclient.fragment.waiter.order_details.WaiterOrdersDetailsFragment;
import com.example.springclient.model.Orders;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class WaiterTableOrdersAdapter extends ArrayAdapter<Orders> {

    private Context context;
    private List<Orders> ordersList;
    private WaiterOrdersViewModel waiterOrderViewModel;
    private SimpleDateFormat originalFormat;
    private SimpleDateFormat timeFormat;



    public WaiterTableOrdersAdapter(Context context, List<Orders> ordersList, WaiterOrdersViewModel waiterOrderViewModel) {
        super(context, R.layout.waiter_table_order_item_list, ordersList);
        this.context = context;
        this.ordersList = ordersList;
        this.waiterOrderViewModel = waiterOrderViewModel;
        this.originalFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.ENGLISH);
        this.timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        this.originalFormat.setTimeZone(TimeZone.getTimeZone("Asia/Novosibirsk")); // Устанавливаем временную зону для Новосибирска
        this.timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Novosibirsk"));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.waiter_table_order_item_list, parent, false);
        }

        Orders order = ordersList.get(position);

        TextView textViewTableNumber = convertView.findViewById(R.id.textViewTableNumber);
        TextView textViewOrderNumber = convertView.findViewById(R.id.textViewOrderNumber);
        TextView textViewOrderStatus = convertView.findViewById(R.id.textViewOrderStatus);
        TextView textViewWaiterName = convertView.findViewById(R.id.textViewWaiterName);
        TextView textViewOrderTime = convertView.findViewById(R.id.textViewOrderTime);

        textViewTableNumber.setText("Стол №: " + order.getTable().getTableNumber());
        textViewOrderNumber.setText("Заказ №: " + order.getId());
        textViewOrderStatus.setText(order.getStatus().getStatusName());
        textViewWaiterName.setText("Официант: " + order.getWaiter().getFirstName()+" " + order.getWaiter().getLastName());

        try {
            String formattedTime = timeFormat.format(order.getDateOfCreation());
            textViewOrderTime.setText(formattedTime);
        } catch (Exception e) {
            textViewOrderTime.setText(order.getDateOfCreation().toString());
            Log.d("WaiterTableOrderFragment", order.getDateOfCreation().toString());// set the original string if formatting fails
        }

        MaterialButton deleteButton = convertView.findViewById(R.id.buttonDelete);

        textViewOrderStatus.setOnClickListener(v -> {
            if (!order.getStatus().getStatusName().equals("Оплачен")) {
                waiterOrderViewModel.updateOrderStatus(order.getId(), 4);
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (!order.getStatus().getStatusName().equals("Оплачен")) {
                new AlertDialog.Builder(context)
                        .setTitle("Удаление заказа")
                        .setMessage("Вы точно хотите удалить заказ?")
                        .setPositiveButton("Да", (dialog, which) -> waiterOrderViewModel.deleteOrder(order.getId()))
                        .setNegativeButton("Нет", null)
                        .show();
            } else {
                Toast.makeText(context, "Нельзя удалить оплаченный заказ", Toast.LENGTH_SHORT).show();
            }
        });


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

    public List<Orders> getOrdersList(){
        return ordersList;
    }

    public void updateOrdersList(List<Orders> newOrdersList) {
        ordersList.clear();
        ordersList.addAll(newOrdersList);
        notifyDataSetChanged();
    }

    public void removeOrderById(int orderId) {
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.get(i).getId() == orderId) {
                ordersList.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void addOrder(Orders order) {
        ordersList.add(order);
        notifyDataSetChanged();
    }

    public void updateOrder(Orders order) {
        int index = findOrderIndexById(order.getId());
        if (index != -1) {
            ordersList.set(index, order);
        }
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
}

