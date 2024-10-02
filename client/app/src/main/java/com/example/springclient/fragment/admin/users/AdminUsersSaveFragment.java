package com.example.springclient.fragment.admin.users;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.springclient.R;
import com.example.springclient.fragment.admin.users.adapters.RolesAdapter;
import com.example.springclient.model.Roles;
import com.example.springclient.model.Users;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class AdminUsersSaveFragment extends Fragment {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextPatronymic;
    private EditText editTextPhoneNumber;
    private EditText editTextEmail;
    private Spinner spinnerRoles;
    private AdminUsersViewModel adminUsersViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_fragment_users_save, container, false);

        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextPatronymic = view.findViewById(R.id.editTextPatronymic);
        editTextPhoneNumber = view.findViewById(R.id.editTextPhoneNumber);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        spinnerRoles = view.findViewById(R.id.spinnerRoles);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);
        Button buttonAdd = view.findViewById(R.id.buttonAdd);
        Toolbar toolbar = view.findViewById(R.id.toolbar_user);
        toolbar.setNavigationIcon(R.drawable.ic_back); // assuming you have this icon
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        toolbar.setTitle("Добавить сотрудника ");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));


        adminUsersViewModel = new ViewModelProvider(requireActivity()).get(AdminUsersViewModel.class);

        adminUsersViewModel.getRolesList().observe(getViewLifecycleOwner(), new Observer<List<Roles>>() {
            @Override
            public void onChanged(List<Roles> roles) {
                if (roles != null) {
                    RolesAdapter adapter = new RolesAdapter(getContext(), roles);
                    spinnerRoles.setAdapter(adapter);
                }
            }
        });


        buttonAdd.setOnClickListener(v -> {
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();
            String patronymic = editTextPatronymic.getText().toString().trim();
            String phoneNumber = editTextPhoneNumber.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            Roles selectedRole = (Roles) spinnerRoles.getSelectedItem();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
                Toast.makeText(getContext(), "Пожалуйста, заполните все обязательные поля", Toast.LENGTH_SHORT).show();
                return;
            }

            Users newUser = new Users();
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPatronymic(patronymic);
            newUser.setPhoneNumber(phoneNumber);
            newUser.setEmail(email);

            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("user", gson.toJsonTree(newUser));
            jsonObject.add("role", gson.toJsonTree(selectedRole));
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

            adminUsersViewModel.saveUser(body).observe(getViewLifecycleOwner(), response -> {
                if (response != null && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Пользователь добавлен", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Ошибка при добавлении пользователя", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}
