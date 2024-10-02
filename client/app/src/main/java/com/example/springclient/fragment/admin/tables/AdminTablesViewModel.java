package com.example.springclient.fragment.admin.tables;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.springclient.MyApplication;
import com.example.springclient.model.Tables;
import com.example.springclient.model.TablesStatus;
import com.example.springclient.retrofit.ServerApi;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTablesViewModel extends ViewModel {
    private static final String TAG = "TablesFragment";
    private MutableLiveData<List<Tables>> tablesList = new MutableLiveData<>();
    private MutableLiveData<List<TablesStatus>> statusesList = new MutableLiveData<>();
    private MutableLiveData<String> webSocketMessage = new MutableLiveData<>();
    private ServerApi serverApi;

    public AdminTablesViewModel() {
        serverApi = MyApplication.getServerAPI();
        loadTables();
        loadStatuses();
    }

    public LiveData<List<Tables>> getTablesList() {
        return tablesList;
    }

    public LiveData<List<TablesStatus>> getTablesStatusesList() {
        return statusesList;
    }

    public LiveData<String> getWebSocketMessage() {
        return webSocketMessage;
    }

    public void setWebSocketMessage(String message) {
        webSocketMessage.setValue(message);
    }

    public void loadTables() {
        serverApi.getAllTables().enqueue(new Callback<List<Tables>>() {
            @Override
            public void onResponse(Call<List<Tables>> call, Response<List<Tables>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tablesList.setValue(response.body());
                    Log.d(TAG, "Tables loaded successfully" + "List size: " + getTablesList().getValue().size());
                } else {
                    Log.e(TAG, "FaФiled to load tables: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Tables>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading tables: " + t.getMessage(), t);
            }
        });
    }

    public void loadStatuses() {
        serverApi.getAllTableStatuses().enqueue(new Callback<List<TablesStatus>>() {
            @Override
            public void onResponse(Call<List<TablesStatus>> call, Response<List<TablesStatus>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    statusesList.setValue(response.body());
                    Log.d(TAG, "Statuses loaded successfully");
                } else {
                    Log.e(TAG, "Failed to load statuses: " + response.message());
                }
            }
            @Override
            public void onFailure(Call<List<TablesStatus>> call, Throwable t) {
                Log.e(TAG, "Error occurred while loading statuses: " + t.getMessage(), t);
            }
        });
    }

    public LiveData<Response<Tables>> saveTable(Tables table) {
        MutableLiveData<Response<Tables>> result = new MutableLiveData<>();
        serverApi.tableSave(table).enqueue(new Callback<Tables>() {
            @Override
            public void onResponse(Call<Tables> call, Response<Tables> response) {
                result.setValue(response);
            }
            @Override
            public void onFailure(Call<Tables> call, Throwable t) {
                result.setValue(null);
            }
        });
        return result;
    }

    public void deleteTable(int tableId) {
        serverApi.tableDelete(tableId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<Tables> currentList = tablesList.getValue();
                    if (currentList != null) {
                        for (int i = 0; i < currentList.size(); i++) {
                            if (currentList.get(i).getId() == tableId) {
                                currentList.remove(i);
                                tablesList.setValue(currentList);
                                break;
                            }
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle error
            }
        });
    }

    public LiveData<Response<Tables>> updateTable(Tables table) {
        MutableLiveData<Response<Tables>> result = new MutableLiveData<>();
        serverApi.tableUpdate(table.getId(), table).enqueue(new Callback<Tables>() {
            @Override
            public void onResponse(Call<Tables> call, Response<Tables> response) {
                if (response.isSuccessful()) {
                    Log.d("TablesFragment", "Table updated successfully");
                    result.setValue(response);
                } else {
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
}
