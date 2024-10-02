package com.example.springclient.fragment.admin.categories;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.springclient.MyApplication;
import com.example.springclient.model.Categories;
import com.example.springclient.retrofit.ServerApi;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoriesViewModel extends ViewModel {
    private static final String TAG = "CategoriesFragment";
    private MutableLiveData<List<Categories>> categoriesList = new MutableLiveData<>();
    private MutableLiveData<String> webSocketMessage = new MutableLiveData<>();
    private ServerApi serverApi;

    public AdminCategoriesViewModel() {
        serverApi = MyApplication.getServerAPI();
        loadCategories();
    }

    public LiveData<List<Categories>> getCategoriesList() {
        return categoriesList;
    }

    public LiveData<String> getWebSocketMessage() {
        return webSocketMessage;
    }

    public void setWebSocketMessage(String message) {
        webSocketMessage.setValue(message);
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

    public LiveData<Response<Categories>> saveCategory(Categories category) {
        MutableLiveData<Response<Categories>> result = new MutableLiveData<>();
        serverApi.categorySave(category).enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Category saved successfully: " + response.body());
                } else {
                    Log.e(TAG, "Failed to save category. Response code: " + response.code() + ", Response message: " + response.message());
                }
                result.setValue(response);
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                Log.e(TAG, "Error saving category: " + t.getMessage(), t);
                result.setValue(null);
            }
        });
        return result;
    }

    public void deleteCategory(int categoryId) {
        serverApi.categoryDelete(categoryId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Categories> currentList = categoriesList.getValue();
                    if (currentList != null) {
                        for (int i = 0; i < currentList.size(); i++) {
                            if (currentList.get(i).getId() == categoryId) {
                                currentList.remove(i);
                                categoriesList.setValue(currentList);
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

    public LiveData<Response<Categories>> updateCategory(Categories category) {
        MutableLiveData<Response<Categories>> result = new MutableLiveData<>();
        serverApi.categotyUpdate(category.getId(), category).enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                if (response.isSuccessful()) {
                    Log.d("CategoriesFragment", "Category updated successfully");
                    result.setValue(response);
                } else {
                    Log.e("CategoriesFragment", "Failed to update category: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable t) {
                result.setValue(null);
                Log.e("CategoriesFragment", "Error occurred: " + t.getMessage(), t);
            }
        });
        return result;
    }

    public void addOrUpdateCategory(Categories category) {
        List<Categories> currentList = categoriesList.getValue();
        if (currentList != null) {
            boolean found = false;
            for (int i = 0; i < currentList.size(); i++) {
                if (currentList.get(i).getId() == category.getId()) {
                    currentList.set(i, category);
                    found = true;
                    break;
                }
            }
            if (!found) {
                currentList.add(category);
            }
            categoriesList.setValue(currentList);
        }
    }
}