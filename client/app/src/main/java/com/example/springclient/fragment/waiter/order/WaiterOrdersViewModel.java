package com.example.springclient.fragment.waiter.order;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.springclient.model.OrderDetails;
import com.example.springclient.model.Orders;
import com.example.springclient.model.OrdersStatus;
import com.example.springclient.model.Tables;
import com.example.springclient.retrofit.ServerApi;
import com.example.springclient.MyApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaiterOrdersViewModel extends ViewModel {

    private static final String TAG = "WaiterOrdersFragment";
    private MutableLiveData<List<Orders>> orderList = new MutableLiveData<>();
    private MutableLiveData<List<Orders>> orderListById = new MutableLiveData<>();
    private MutableLiveData<List<Orders>> myOrderList = new MutableLiveData<>();
    private MutableLiveData<List<OrdersStatus>> statusesList = new MutableLiveData<>();
    private MutableLiveData<String> webSocketMessage = new MutableLiveData<>();
    private ServerApi serverApi;

    private Map<Integer, MutableLiveData<List<OrderDetails>>> orderDetailsMap = new HashMap<>();

    public WaiterOrdersViewModel() {
        serverApi = MyApplication.getServerAPI();
        loadMyOrders(MyApplication.getUser().getId());
        loadOrders();
        loadStatuses();
    }

    public LiveData<List<OrderDetails>> getOrderDetails(int orderId) {
        if (!orderDetailsMap.containsKey(orderId)) {
            orderDetailsMap.put(orderId, new MutableLiveData<>());
            loadOrderDetails(orderId);
        }
        return orderDetailsMap.get(orderId);
    }

    public LiveData<List<Orders>> getOrderList() {
        loadOrders();
        return orderList;
    }

    public LiveData<List<Orders>> getOrderListById() {
        return orderListById;
    }

    public LiveData<String> getWebSocketMessage() {
        return webSocketMessage;
    }

    public void setWebSocketMessage(String message) {
        webSocketMessage.setValue(message);
    }

    public LiveData<List<OrdersStatus>> getOrdersStatusesList() {
        return statusesList;
    }

    public void loadOrderDetails(int orderId) {
        serverApi.getOrderDetails(orderId).enqueue(new Callback<List<OrderDetails>>() {
            @Override
            public void onResponse(Call<List<OrderDetails>> call, Response<List<OrderDetails>> response) {
                if (response.isSuccessful()) {
                    MutableLiveData<List<OrderDetails>> liveData = orderDetailsMap.get(orderId);
                    if (liveData != null) {
                        liveData.postValue(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<OrderDetails>> call, Throwable t) {
            }
        });
    }

    public void removeOrderDetailsObserver(int orderId, Observer<List<OrderDetails>> observer) {
        MutableLiveData<List<OrderDetails>> liveData = orderDetailsMap.get(orderId);
        if (liveData != null) {
            liveData.removeObserver(observer);
        }
    }

    public void loadOrders() {
        serverApi.getAllOrders().enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.setValue(response.body());
                    Log.d(TAG, response.body().toString());
                } else {
                    Log.d(TAG, "Оишбка " + response.body().toString());
                    orderList.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading statuses: " + t.getMessage(), t);
                orderList.setValue(null);
            }
        });
    }

    public void loadMyOrders(int userId) {
        serverApi.getOrdersForUser(userId).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myOrderList.setValue(response.body());
                } else {
                    myOrderList.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                myOrderList.setValue(null);
            }
        });
    }

    public void loadOrdersById(int waiterId, int tableId) {
        serverApi.getOrders(waiterId, tableId).enqueue(new Callback<List<Orders>>() {
            @Override
            public void onResponse(Call<List<Orders>> call, Response<List<Orders>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Statuses loaded successfully");
                    orderListById.setValue(response.body());
                } else {
                    Log.d(TAG, "Statuses loaded successfully");
                    orderListById.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Orders>> call, Throwable t) {
                orderListById.setValue(null);
            }
        });
    }

    public void createOrder(int tableId, int waiterId) {
        serverApi.saveOrder(tableId, waiterId).enqueue(new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Orders order = response.body();
                } else {
                }
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {
            }
        });
    }

    public void loadStatuses() {
        serverApi.getAllOrdersStatus().enqueue(new Callback<List<OrdersStatus>>() {
            @Override
            public void onResponse(Call<List<OrdersStatus>> call, Response<List<OrdersStatus>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statusesList.setValue(response.body());
                    Log.d(TAG, "Statuses loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load statuses: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<OrdersStatus>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading order statuses: " + t.getMessage(), t);
            }
        });
    }

    public void updateOrderStatus(int orderId, int statusId) {
        serverApi.updateOrderStatus(orderId, statusId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public void deleteOrder(int orderId) {
        serverApi.deleteOrder(orderId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public LiveData<Response<Tables>> updateTable(Tables table) {
        MutableLiveData<Response<Tables>> result = new MutableLiveData<>();
        serverApi.tableUpdate(table.getId(), table).enqueue(new Callback<Tables>() {
            @Override
            public void onResponse(Call<Tables> call, Response<Tables> response) {
                if (response.isSuccessful()) {
                    // Обработка успешного обновления стола
                    Log.d("TablesFragment", "Table updated successfully");
                    result.setValue(response);
                } else {
                    // Обработка неудачного обновления стола
                    Log.e("TablesFragment", "Failed to update table: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<Tables> call, Throwable t) {
                result.setValue(null);
                Log.e("TablesFragment", "Error occurred: " + t.getMessage(), t);
            }
        });
        return result;
    }


    public MutableLiveData<List<Orders>> getMyOrderList() {
        return myOrderList;
    }

    public void setMyOrderList(MutableLiveData<List<Orders>> myOrderList) {
        this.myOrderList = myOrderList;
    }

}
