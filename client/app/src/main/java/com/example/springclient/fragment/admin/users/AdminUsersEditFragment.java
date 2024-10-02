package com.example.springclient.fragment.admin.users;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.springclient.R;
import com.example.springclient.fragment.admin.tables.AdminTablesSaveDialog;
import com.example.springclient.fragment.admin.users.adapters.AdminUsersRolesAdapter;
import com.example.springclient.model.Roles;
import com.example.springclient.model.Users;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AdminUsersEditFragment extends Fragment {

    private static final String TAG = "AdminUsersEditFragment";

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPatronymic;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private ListView listViewRoles;
    private CheckBox checkBoxActive;
    private AdminUsersViewModel adminUsersViewModel;
    private Users user;
    private AdminUsersRolesAdapter rolesAdapter;

    public void setUser(Users user) {
        this.user = user;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_users_edit, container, false);

        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextPatronymic = view.findViewById(R.id.editTextPatronymic);
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        listViewRoles = view.findViewById(R.id.listViewRoles);
        checkBoxActive = view.findViewById(R.id.checkBoxActive);
        Button buttonSwitchToUserRolesSave = view.findViewById(R.id.buttonSwitchToAddRole);
        Button buttonSave = view.findViewById(R.id.buttonSave);

        Toolbar toolbar = view.findViewById(R.id.toolbar_user);
        toolbar.setNavigationIcon(R.drawable.ic_back); // assuming you have this icon
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        toolbar.setTitle("Редактировать сотрудника ");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        adminUsersViewModel = new ViewModelProvider(requireActivity()).get(AdminUsersViewModel.class);

        if (user != null) {
            adminUsersViewModel.loadRolesByUserId(user.getId());
            adminUsersViewModel.getUserRolesList().observe(getViewLifecycleOwner(), new Observer<List<Roles>>() {
                @Override
                public void onChanged(List<Roles> roles) {
                    if (rolesAdapter == null) {
                        rolesAdapter = new AdminUsersRolesAdapter(getContext(), roles, adminUsersViewModel,user);
                        listViewRoles.setAdapter(rolesAdapter);
                    }
                    else
                    {
                        rolesAdapter.updateUsersList(roles);
                        listViewRoles.setAdapter(rolesAdapter);
                    }
                }
            });
        }

        if (user != null) {
            editTextFirstName.setText(user.getFirstName());
            editTextLastName.setText(user.getLastName());
            if (user.getPatronymic() != null) {
                editTextPatronymic.setText(user.getPatronymic());
            }
            if (user.getPhoneNumber() != null) {
                editTextPhoneNumber.setText(user.getPhoneNumber());
            }
            editTextEmail.setText(user.getEmail());
            Log.e(TAG, "актив = " + user.isActive());
            checkBoxActive.setChecked(user.isActive());
        } else {
            Log.e(TAG, "User is null");
        }

        buttonSave.setOnClickListener(v -> {
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String patronymic = editTextPatronymic.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            boolean isActive = checkBoxActive.isChecked();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user == null) {
                Log.e(TAG, "User is null when trying to save");
                Toast.makeText(getContext(), "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
                return;
            }

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPatronymic(patronymic);
            user.setPhoneNumber(phoneNumber);
            user.setEmail(email);
            user.setActive(isActive);

            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("user", gson.toJsonTree(user));
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

            adminUsersViewModel.updateUser(body).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Пользователь обновлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Ошибка при обновлении пользователя", Toast.LENGTH_SHORT).show();
                }
            });
        });

        buttonSwitchToUserRolesSave.setOnClickListener(v -> {
            AdminUsersSaveRoleDialog dialog = new AdminUsersSaveRoleDialog();
            dialog.setUser(user);
            dialog.show(getChildFragmentManager(), "AddUserRolesDialog");
        });

        return view;
    }
}
