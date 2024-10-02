package com.example.springclient.fragment.admin.dishes;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.springclient.MyApplication;
import com.example.springclient.model.Dishes;
import com.example.springclient.retrofit.ServerApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDishesViewModel extends ViewModel {
    private static final String TAG = "DishesFragment";
    private MutableLiveData<List<Dishes>> dishesList = new MutableLiveData<>();
    private MutableLiveData<String> webSocketMessage = new MutableLiveData<>();
    private ServerApi serverApi;

    public AdminDishesViewModel() {
        serverApi = MyApplication.getServerAPI();
    }

    public LiveData<List<Dishes>> getDishesList() {
        return dishesList;
    }

    public LiveData<String> getWebSocketMessage() {
        return webSocketMessage;
    }

    public void setWebSocketMessage(String message) {
        webSocketMessage.setValue(message);
    }

    public void loadDishes() {
        serverApi.getAllDishes().enqueue(new Callback<List<Dishes>>() {
            @Override
            public void onResponse(Call<List<Dishes>> call, Response<List<Dishes>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dishesList.setValue(response.body());
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(response.body());
                    Log.d("WaiterOrderFragmentView", "OrderDetails response: " + jsonResponse);
                    Log.d(TAG, "Dishes loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load dishes: " + response.message());
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonResponse = gson.toJson(response.body());
                    Log.d("WaiterOrderFragmentView", "OrderDetails response: " + jsonResponse);

                }
            }

            @Override
            public void onFailure(Call<List<Dishes>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading dishes: " + t.getMessage(), t);
            }
        });
    }

    public LiveData<Response<Dishes>> saveDish(Dishes dish) {
        MutableLiveData<Response<Dishes>> result = new MutableLiveData<>();
        serverApi.dishSave(dish).enqueue(new Callback<Dishes>() {
            @Override
            public void onResponse(Call<Dishes> call, Response<Dishes> response) {
                result.setValue(response);
            }

            @Override
            public void onFailure(Call<Dishes> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public void deleteDish(int dishId) {
        serverApi.dishDelete(dishId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Dishes> currentList = dishesList.getValue();
                    if (currentList != null) {
                        for (int i = 0; i < currentList.size(); i++) {
                            if (currentList.get(i).getId() == dishId) {
                                currentList.remove(i);
                                dishesList.setValue(currentList);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });
    }

    public LiveData<Response<Dishes>> updateDish(Dishes dish) {
        MutableLiveData<Response<Dishes>> result = new MutableLiveData<>();
        serverApi.dishyUpdate(dish.getId(), dish).enqueue(new Callback<Dishes>() {
            @Override
            public void onResponse(Call<Dishes> call, Response<Dishes> response) {
                if (response.isSuccessful()) {
                    Log.d("DishesFragment", "Dish updated successfully");
                    result.setValue(response);
                } else {
                    Log.e("DishesFragment", "Failed to update dish: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Dishes> call, Throwable t) {
                result.setValue(null);
                Log.e("DishesFragment", "Error occurred: " + t.getMessage(), t);
            }
        });
        return result;
    }

}
