package com.example.springclient.fragment.waiter.order_details;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.example.springclient.R;
import com.example.springclient.model.Dishes;

import java.util.List;

public class WaiterOrderDishesAdapter extends BaseAdapter {
    private Context context;
    private List<Dishes> dishesList;
    private LayoutInflater inflater;
    private  int orderId;
    private WaiterOrdersDetailsViewModel waiterOrdersDetailsViewModel;

    public WaiterOrderDishesAdapter(Context context, List<Dishes> dishesList,WaiterOrdersDetailsViewModel waiterOrdersDetailsViewModel, int orderId) {
        this.context = context;
        this.dishesList = dishesList;
        this.inflater = LayoutInflater.from(context);
        this.waiterOrdersDetailsViewModel = waiterOrdersDetailsViewModel;
        this.orderId = orderId;
    }

    @Override
    public int getCount() {
        return dishesList.size();
    }

    @Override
    public Object getItem(int position) {
        return dishesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dishesList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.waiter_order_dishes_item, parent, false);
        }

        Dishes dish = dishesList.get(position);
        Button dishButton = convertView.findViewById(R.id.dish_button);
        dishButton.setText(dish.getName());

        dishButton.setOnClickListener(v -> {
            Log.d("WaiterOrderDishesAdapter", String.valueOf(orderId));

            waiterOrdersDetailsViewModel.addOrderDetail(orderId,dish.getId(),1,"В очереди");
        });

        return convertView;
    }

    public void updateDishList(List<Dishes> newDishList) {
        dishesList.clear();
        dishesList.addAll(newDishList);
        notifyDataSetChanged();
    }
}
