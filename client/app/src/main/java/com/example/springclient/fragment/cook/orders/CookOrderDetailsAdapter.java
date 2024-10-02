package com.example.springclient.fragment.cook.orders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.springclient.R;
import com.example.springclient.fragment.waiter.order_details.WaiterOrdersDetailsViewModel;
import com.example.springclient.model.OrderDetails;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CookOrderDetailsAdapter extends ArrayAdapter<OrderDetails> {

    private Context context;
    private List<OrderDetails> orderDetailsList;
    private WaiterOrdersDetailsViewModel waiterOrdersDetailsViewModel;

    public CookOrderDetailsAdapter(Context context, List<OrderDetails> orderDetailsList, WaiterOrdersDetailsViewModel waiterOrdersDetailsViewModel) {
        super(context, R.layout.cook_order_details_item_list, orderDetailsList);
        this.context = context;
        this.orderDetailsList = orderDetailsList;
        this.waiterOrdersDetailsViewModel=waiterOrdersDetailsViewModel;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cook_order_details_item_list, parent, false);
        }

        OrderDetails orderDetails = orderDetailsList.get(position);

        TextView textViewDishName = convertView.findViewById(R.id.textViewDishName);
        TextView textViewQuantity = convertView.findViewById(R.id.textViewQuantity);
        TextView textViewStatus = convertView.findViewById(R.id.textViewStatus);
        MaterialButton buttonDelete = convertView.findViewById(R.id.buttonDelete);
        textViewDishName.setText(orderDetails.getDish().getName());
        textViewQuantity.setText("Кол-во: " + orderDetails.getAmount());
        textViewStatus.setText(orderDetails.getStroka().getStatusName());



        if ("Ожидает выдачи".equals(orderDetails.getStroka().getStatusName())) {
            textViewStatus.setOnClickListener(v -> {
                orderDetails.getStroka().setStatusName("Оплачен");
                waiterOrdersDetailsViewModel.updateOrderDetails(orderDetails);
                Toast.makeText(context, "Статус блюда изменён на 'Оплачен'", Toast.LENGTH_SHORT).show();
            });
        } else {
            textViewStatus.setOnClickListener(null);
        }

        return convertView;
    }

    public void updateOrderDetailsList(List<OrderDetails> newOrderDetailsList) {
        orderDetailsList.clear();
        orderDetailsList.addAll(newOrderDetailsList);
        notifyDataSetChanged();
    }

    public void addOrderDetails(OrderDetails orderDetails) {
        orderDetailsList.add(orderDetails);
        updateOrderDetailsList(orderDetailsList);
        notifyDataSetChanged();
    }

    public void removeOrderDetailsById(int id) {
        for (int i = 0; i < orderDetailsList.size(); i++) {
            if (orderDetailsList.get(i).getId() == id) {
                orderDetailsList.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public void updateOrderDetails(OrderDetails orderDetails) {
        int index = findOrderDetailsIndexById(orderDetails.getId());
        if (index != -1) {
            orderDetailsList.set(index, orderDetails);
        }

        notifyDataSetChanged();
    }

    private int findOrderDetailsIndexById(int id) {
        for (int i = 0; i < orderDetailsList.size(); i++) {
            if (orderDetailsList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }


}
