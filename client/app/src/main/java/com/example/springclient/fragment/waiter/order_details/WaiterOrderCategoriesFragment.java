package com.example.springclient.fragment.waiter.order_details;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.springclient.MainActivity;
import com.example.springclient.MyApplication;
import com.example.springclient.R;
import com.example.springclient.fragment.waiter.tables.WaiterTablesAdapter;
import com.example.springclient.fragment.waiter.tables.WaiterTablesViewModel;
import com.example.springclient.model.Categories;
import com.example.springclient.model.Tables;
import com.example.springclient.websocket.GenericWebSocketClient;
import com.example.springclient.websocket.WebSocketClientSingleton;
import com.example.springclient.websocket.WebSocketListener;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class WaiterOrderCategoriesFragment extends Fragment implements WaiterOrderCategoiresAdapter.OnCategoryClickListener{

    private GenericWebSocketClient webSocketClient;
    private WaiterOrderCategoiresAdapter waiterOrderCategoiresAdapter;
    private WaiterOrdersDetailsViewModel waiterOrdersDetailsViewModel;
    private static final String ARG_ORDER_ID = "order_id";
    private int orderId;

    public static WaiterOrderCategoriesFragment newInstance(int orderId) {
        WaiterOrderCategoriesFragment fragment = new WaiterOrderCategoriesFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getInt(ARG_ORDER_ID);
        }
        waiterOrdersDetailsViewModel = new ViewModelProvider(this).get(WaiterOrdersDetailsViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.waiter_order_categories_fragment, container, false);
        GridView gridViewTables = view.findViewById(R.id.grid_view_categories);
        Toolbar toolbar = view.findViewById(R.id.toolbar_order_details_categories);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        }

        waiterOrdersDetailsViewModel = new ViewModelProvider(requireActivity()).get(WaiterOrdersDetailsViewModel.class);

        waiterOrderCategoiresAdapter = new WaiterOrderCategoiresAdapter(getContext(), waiterOrdersDetailsViewModel.getCategoriesList().getValue(), waiterOrdersDetailsViewModel);
        waiterOrderCategoiresAdapter.setOnCategoryClickListener(new WaiterOrderCategoiresAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Categories category) {
                Log.d("WaiterOrderCategoriesFragment", category.getName() + " " + category.getId());
                WaiterOrderDishesFragment dishFragment = WaiterOrderDishesFragment.newInstance(category.getId(), category.getName(), orderId);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_order_details_container, dishFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        gridViewTables.setAdapter(waiterOrderCategoiresAdapter);

        waiterOrdersDetailsViewModel.getCategoriesList().observe(getViewLifecycleOwner(), new Observer<List<Categories>>() {
            @Override
            public void onChanged(List<Categories> categories) {
                waiterOrderCategoiresAdapter.updateCategoryList(categories);
            }
        });

        return view;
    }

    @Override
    public void onCategoryClick(Categories category) {
        WaiterOrderDishesFragment dishFragment = WaiterOrderDishesFragment.newInstance(category.getId(), category.getName(), orderId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.fragment_order_details_container, dishFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolbarTitle("Категории");
            waiterOrdersDetailsViewModel.loadCategories(); // Убедитесь, что категории загружаются заново
        }
    }
}