package com.example.springclient.fragment.waiter.order_details;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import androidx.appcompat.widget.Toolbar;

import com.example.springclient.R;
import com.example.springclient.model.Dishes;

import java.util.List;

public class WaiterOrderDishesFragment extends Fragment {

    private static final String ARG_CATEGORYID = "categoryId";
    private int categoryId;

    private static final String ARG_CATEGORYNAME = "categoryName";
    private String categoryName;
    private GridView gridViewDishes;
    private WaiterOrderDishesAdapter dishAdapter;
    private WaiterOrdersDetailsViewModel dishViewModel;

    private static final String ARG_ORDER_ID = "order_id";
    private int orderId;


    public static WaiterOrderDishesFragment newInstance(int id, String name,int orderId) {
        WaiterOrderDishesFragment fragment = new WaiterOrderDishesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CATEGORYID, id);
        args.putString(ARG_CATEGORYNAME, name);
        args.putInt(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId =  getArguments().getInt(ARG_CATEGORYID);
            categoryName = getArguments().getString(ARG_CATEGORYNAME);
            orderId = getArguments().getInt(ARG_ORDER_ID);
        }
        dishViewModel = new ViewModelProvider(this).get(WaiterOrdersDetailsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.waiter_order_dishes_fragment, container, false);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        gridViewDishes = view.findViewById(R.id.grid_view_dishes);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });
        toolbar.setTitle(categoryName);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));



        dishViewModel.loadDishesById(categoryId);

        dishViewModel.getDishesList().observe(getViewLifecycleOwner(), new Observer<List<Dishes>>() {
            @Override
            public void onChanged(List<Dishes> dishes) {
                if (dishes != null) {
                    Log.d("WaiterOrderDishesFragment", String.valueOf(dishes.size()));
                    if(dishAdapter == null) {
                        dishAdapter = new WaiterOrderDishesAdapter(getContext(), dishes, dishViewModel,orderId);
                        gridViewDishes.setAdapter(dishAdapter);
                    }else {
                        dishAdapter.updateDishList(dishes);
                        gridViewDishes.setAdapter(dishAdapter);
                    }
                }
            }
        });

        return view;
    }
}

