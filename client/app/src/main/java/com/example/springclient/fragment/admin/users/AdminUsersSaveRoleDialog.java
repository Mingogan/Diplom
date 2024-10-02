package com.example.springclient.fragment.admin.users;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.springclient.R;
import com.example.springclient.fragment.admin.users.adapters.RolesAdapter;
import com.example.springclient.model.AuthData;
import com.example.springclient.model.Roles;
import com.example.springclient.model.Users;

import java.io.Serializable;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AdminUsersSaveRoleDialog extends DialogFragment {

    private Spinner spinnerUserRoles;
    private AdminUsersViewModel adminUsersViewModel;
    private Users user;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_dialog_users_role_save, container, false);

        spinnerUserRoles = view.findViewById(R.id.spinnerUserRoles);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);

        adminUsersViewModel = new ViewModelProvider(requireActivity()).get(AdminUsersViewModel.class);

        if (getArguments() != null) {
            user = (Users) getArguments().getSerializable("user");
        }

        adminUsersViewModel.getRolesList().observe(getViewLifecycleOwner(), new Observer<List<Roles>>() {
            @Override
            public void onChanged(List<Roles> roles) {
                if (roles != null) {
                    RolesAdapter adapter = new RolesAdapter(getContext(), roles);
                    spinnerUserRoles.setAdapter(adapter);
                }
            }
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        buttonAdd.setOnClickListener(v -> {
            if (user==null){
                Log.d("UsersFragment", "имя пользователя" + user.getFirstName());
            }
            Roles selectedRole = (Roles) spinnerUserRoles.getSelectedItem();
            if (selectedRole != null) {
                List<Roles> userRoles = adminUsersViewModel.getUserRolesList().getValue();
                if (userRoles != null && userRoles.contains(selectedRole)) {
                    Toast.makeText(getContext(), "Роль уже назначена пользователю", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("user", new Gson().toJsonTree(user));
                    jsonObject.add("role", new Gson().toJsonTree(selectedRole));
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

                    adminUsersViewModel.saveUserRole(body).observe(getViewLifecycleOwner(), response -> {
                        if (response != null && response.isSuccessful()) {
                            AuthData savedAuthData = response.body();
                            adminUsersViewModel.loadRolesByUserId(user.getId());
                            Toast.makeText(getContext(), "Роль добавлена", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else if (response != null && response.code() == 409) {
                            adminUsersViewModel.loadRolesByUserId(user.getId());
                            Toast.makeText(getContext(), "Такая роль уже существует для данного пользователя", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Ошибка при добавлении роли", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(getContext(), "Пожалуйста, выберите роль", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public void setUser(Users user){
        this.user=user;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Добавить роль");
        return dialog;
    }
}
