package com.example.springclient.fragment.admin.statisticka;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.springclient.R;
import com.example.springclient.model.Orders;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderListAdapter extends ArrayAdapter<Orders> {

    private Context context;
    private List<Orders> ordersList;
    private SimpleDateFormat dateFormat;

    public OrderListAdapter(@NonNull Context context, List<Orders> ordersList) {
        super(context, 0, ordersList);
        this.context = context;
        this.ordersList = ordersList;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.revenue_orders_item_list, parent, false);
        }

        Orders order = ordersList.get(position);

        TextView textViewTableNumber = convertView.findViewById(R.id.textViewTableNumber);
        TextView textViewOrderTime = convertView.findViewById(R.id.textViewOrderTime);
        TextView textViewOrderNumber = convertView.findViewById(R.id.textViewOrderNumber);
        TextView textViewOrderStatus = convertView.findViewById(R.id.textViewOrderStatus);
        TextView textViewWaiterName = convertView.findViewById(R.id.textViewWaiterName);
        TextView textViewOrderDetails = convertView.findViewById(R.id.textViewOrderDetails);
        TextView textViewTotalCost = convertView.findViewById(R.id.textViewTotalCost);

        textViewTableNumber.setText("Стол №: " + String.valueOf(order.getTable().getTableNumber()));
        textViewOrderTime.setText(dateFormat.format(order.getDateOfCreation()));
        textViewOrderNumber.setText("Заказ №: " + order.getId());
        textViewOrderStatus.setText(order.getStatus().getStatusName());
        textViewWaiterName.setText("Официант: " + order.getWaiter().getLastName() + " "+ order.getWaiter().getFirstName());
        textViewTotalCost.setText(String.format(Locale.getDefault(), "%.2f", order.getTotalCost()));

        return convertView;
    }
}
