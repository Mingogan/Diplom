package com.example.springclient.fragment.waiter.order_details;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.springclient.MyApplication;
import com.example.springclient.model.Categories;
import com.example.springclient.model.Dishes;
import com.example.springclient.model.OrderDetails;
import com.example.springclient.retrofit.ServerApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WaiterOrdersDetailsViewModel extends ViewModel {
    private MutableLiveData<List<OrderDetails>> orderDetailsList = new MutableLiveData<>();
    private MutableLiveData<String> webSocketMessage = new MutableLiveData<>();
    private MutableLiveData<List<Categories>> categoriesList = new MutableLiveData<>();
    private MutableLiveData<List<Dishes>> dishesList = new MutableLiveData<>();
    private ServerApi serverApi;


    public WaiterOrdersDetailsViewModel() {
        serverApi = MyApplication.getServerAPI();
    }

    public LiveData<List<Categories>> getCategoriesList() {
        loadCategories();
        return categoriesList;
    }

    public LiveData<List<Dishes>> getDishesList() {
        loadCategories();
        return dishesList;
    }

    public LiveData<List<OrderDetails>> getOrderDetailsList(int orderId) {
        loadOrderDetails(orderId);
        return orderDetailsList;
    }

    public LiveData<String> getWebSocketMessage() {
        return webSocketMessage;
    }

    public void setWebSocketMessage(String message) {
        webSocketMessage.setValue(message);
    }

    public void loadOrderDetails(int orderId) {
        serverApi.getOrderDetails(orderId).enqueue(new Callback<List<OrderDetails>>() {
            @Override
            public void onResponse(Call<List<OrderDetails>> call, Response<List<OrderDetails>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Type listType = new TypeToken<List<OrderDetails>>() {}.getType();
                    List<OrderDetails> orderDetailsListTest = new Gson().fromJson(new Gson().toJson(response.body()), listType);

                    orderDetailsList.setValue(orderDetailsListTest);


                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(response.body());
                    Log.d("WaiterOrderFragmentView", "OrderDetails response: " + jsonResponse);
                    for (OrderDetails details : orderDetailsListTest) {
                        //if (details.getStId() == null) {
                            Log.e("OrderDetails", "Status is null for orderDetail with ID: " + details.getId());
                        //}
                    }

                } else {
                    orderDetailsList.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<OrderDetails>> call, Throwable t) {
                orderDetailsList.setValue(null);
            }
        });
    }

    public void loadCategories() {
        serverApi.getAllCategories().enqueue(new Callback<List<Categories>>() {
            @Override
            public void onResponse(Call<List<Categories>> call, Response<List<Categories>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoriesList.setValue(response.body());
                    Log.d(TAG, "Categories loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load categories: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Categories>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading categories: " + t.getMessage(), t);
            }
        });
    }

    public void loadDishesById(int categoryId) {
        serverApi.getAllDishesByCategoryId(categoryId).enqueue(new Callback<List<Dishes>>() {
            @Override
            public void onResponse(Call<List<Dishes>> call, Response<List<Dishes>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dishesList.setValue(response.body());
                    Log.d(TAG, "Dishes loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load dishes: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Dishes>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading dishes: " + t.getMessage(), t);
            }
        });
    }

    public LiveData<Response<OrderDetails>> addOrderDetail(int orderId, int dishId, int quantity, String status) {
        MutableLiveData<Response<OrderDetails>> result = new MutableLiveData<>();
        serverApi.saveOrderDetail(orderId, dishId, quantity, status).enqueue(new Callback<OrderDetails>() {
            @Override
            public void onResponse(Call<OrderDetails> call, Response<OrderDetails> response) {
                result.setValue(response);
            }

            @Override
            public void onFailure(Call<OrderDetails> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public void updateOrderDetails(OrderDetails orderDetails) {
        serverApi.updateOrderDetails(orderDetails).enqueue(new Callback<OrderDetails>() {
            @Override
            public void onResponse(Call<OrderDetails> call, Response<OrderDetails> response) {
                if (response.isSuccessful()) {
                    loadOrderDetails(orderDetails.getOrder().getId()); // Reload order details
                }
            }

            @Override
            public void onFailure(Call<OrderDetails> call, Throwable t) {
            }
        });
    }

    public void deleteOrderDetails(int orderDetailsId) {
        serverApi.deleteOrderDetails(orderDetailsId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    loadOrderDetails(orderDetailsId); // Reload order details
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

}
