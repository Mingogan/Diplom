package com.example.springclient.retrofit;

import com.example.springclient.model.AuthData;
import com.example.springclient.model.Categories;
import com.example.springclient.model.Dishes;
import com.example.springclient.model.OrderDetails;
import com.example.springclient.model.Orders;
import com.example.springclient.model.OrdersStatus;
import com.example.springclient.model.Roles;
import com.example.springclient.model.Tables;
import com.example.springclient.model.TablesStatus;
import com.example.springclient.model.Users;

import java.util.Date;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServerApi {

    @POST("/categories")
    Call<Categories> categorySave(@Body Categories categories);

    @GET("/categories/get-all")
    Call<List<Categories>> getAllCategories();

    @DELETE("/categories/{id}")
    Call<Void> categoryDelete(@Path("id") int categoryId);

    @PUT("/categories/{id}")
    Call<Categories> categotyUpdate(@Path("id") int categoryId, @Body Categories categories);

    @GET("/dishes/get-all")
    Call<List<Dishes>> getAllDishes();

    @POST("/dishes")
    Call<Dishes> dishSave(@Body Dishes dish);

    @DELETE("/dishes/{id}")
    Call<Void> dishDelete(@Path("id") int dishId);

    @PUT("/dishes/{id}")
    Call<Dishes> dishyUpdate(@Path("id") int dishId, @Body Dishes dish);

    @GET("/tables/get-all")
    Call<List<Tables>> getAllTables();

    @POST("/tables")
    Call<Tables> tableSave(@Body Tables table);

    @DELETE("/tables/{id}")
    Call<Void> tableDelete(@Path("id") int tableId);

    @PUT("/tables/{id}")
    Call<Tables> tableUpdate(@Path("id") int tableIdId, @Body Tables table);

    @GET("/tables/status/get-all")
    Call<List<TablesStatus>> getAllTableStatuses();

    @GET("/user/get-all")
    Call<List<Users>> getAllUsers();

    @POST("/user")
    Call<Users> userSave(@Body RequestBody body);

    @DELETE("/user/{id}")
    Call<Void> userDelete(@Path("id") int userId);

    @PUT("/user/{id}")
    Call<Users> userUpdate(@Body RequestBody body);

    @GET("/roles/get-all")
    Call<List<Roles>> getAllRoles();

    @GET("/roles/{userId}")
    Call<List<Roles>> getRolesByUserId(@Path("userId") int userId);

    @POST("/auth")
    Call<AuthData> addUserRole(@Body RequestBody body);

    @DELETE("/auth/delete-role")
    Call<Void> deleteUserRole(@Query("userId") int userId, @Query("roleId") int roleId);


    @FormUrlEncoded
    @POST("/auth/login")
    Call<Map<String, String>> login(@Field("username") String username, @Field("password") String password);

    @POST("/auth/refresh")
    Call<Map<String, String>> refreshToken(@Query("refreshToken") String refreshToken);

    @GET("/orders/get-all")
    Call<List<Orders>> getAllOrders();

    @GET("/orders/status/get-all")
    Call<List<OrdersStatus>> getAllOrdersStatus();

    @POST("/orders")
    Call<Orders> saveOrder(@Query("tableId") int tableId, @Query("waiterId") int waiterId);

    @GET("/orders/{userId}/{tableId}")
    Call<List<Orders>> getOrders(@Path("userId") int userId, @Path("tableId") int tableId);

    @GET("/ordersDetails/{orderId}")
    Call<List<OrderDetails>> getOrderDetails(@Path("orderId") int orderId);

    @GET("dishes/{categoryId}")
    Call<List<Dishes>> getAllDishesByCategoryId(@Path("categoryId") int categoryId);

    @POST("/ordersDetails")
    Call<OrderDetails> saveOrderDetail(@Query("orderId") int orderId, @Query("dishId") int dishId, @Query("quantity") int quantity, @Query("status") String status);

    @GET("/user/{tableId}")
    Call<Users> getWaiterByTableId(@Path("tableId") int tableId);

    @PUT("/orders/status/{orderId}/{statusId}")
    Call<Void> updateOrderStatus(@Path("orderId") int orderId, @Path("statusId") int statusId);

    @DELETE("/orders/{orderId}")
    Call<Void> deleteOrder(@Path("orderId") int orderId);

    @PUT("/order/orderdetails/update")
    Call<OrderDetails> updateOrderDetails(@Body OrderDetails orderDetails);

    @DELETE("/order/orderdetails/delete/{id}")
    Call<Void> deleteOrderDetails(@Path("id") int id);

    @GET("/orders/{userId}")
    Call<List<Orders>> getOrdersForUser(@Path("userId") int userId);

    @GET("orders/byDateRange")
    Call<List<Orders>> getOrdersByDateRange(@Query("startDate") String startDate, @Query("endDate") String endDate);
}
