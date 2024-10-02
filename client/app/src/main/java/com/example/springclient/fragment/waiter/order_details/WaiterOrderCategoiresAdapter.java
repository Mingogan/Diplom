package com.example.springclient.fragment.waiter.order_details;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.fragment.app.FragmentActivity;

import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.fragment.waiter.table_orders.WaiterTableOrdersFragment;
import com.example.springclient.model.Categories;
import com.example.springclient.model.Tables;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WaiterOrderCategoiresAdapter extends BaseAdapter {
    private Context context;
    private List<Categories> categoriesList;
    private LayoutInflater inflater;
    private WaiterOrdersDetailsViewModel waiterOrdersDetailsViewModel;
    private OnCategoryClickListener categoryClickListener;
    public WaiterOrderCategoiresAdapter(Context context, List<Categories> categoriesList, WaiterOrdersDetailsViewModel waiterOrdersDetailsViewModel) {
        this.context = context;
        this.categoriesList = categoriesList != null ? categoriesList : new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
        this.waiterOrdersDetailsViewModel = waiterOrdersDetailsViewModel;
    }

    @Override
    public int getCount() {
        return categoriesList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoriesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return categoriesList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.waiter_order_categories_item, parent, false);
        }

        Categories categories = categoriesList.get(position);
        MaterialButton categoryButton = convertView.findViewById(R.id.category_button);
        categoryButton.setText(String.valueOf(categories.getName()));

        // Установка слушателя нажатий
        categoryButton.setOnClickListener(v -> categoryClickListener.onCategoryClick(categories));

        return convertView;
    }
    public void updateCategoryList(List<Categories> newCategoryList) {
        categoriesList.clear();
        categoriesList.addAll(newCategoryList);
        notifyDataSetChanged();
    }
    public interface OnCategoryClickListener {
        void onCategoryClick(Categories category);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.categoryClickListener = listener;
    }
}
