package com.example.springclient.fragment.login;

import com.example.springclient.fragment.admin.MainAdminFragment;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.springclient.R;
import com.example.springclient.fragment.admin.MainAdminFragment;
import com.example.springclient.fragment.admin.tables.AdminTablesViewModel;
import com.example.springclient.fragment.cook.CookMainFragment;
import com.example.springclient.fragment.waiter.WaiterMainFragment;
import com.example.springclient.model.Users;
import com.google.gson.Gson;

import java.util.Map;


public class LoginFragment extends Fragment {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;
    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);

        usernameEditText = view.findViewById(R.id.username);
        passwordEditText = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login);
        loadingProgressBar = view.findViewById(R.id.loading);

        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(username, password);
        });

        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult != null) {
                handleLoginSuccess(loginResult);
            } else {

            }
        });
        return view;
    }

    private void handleLoginSuccess(Map<String, String> loginResult) {
        String role = loginResult.get("role");
        Gson gson = new Gson();
        String userJson = loginResult.get("user");

        Users user = gson.fromJson(userJson, Users.class);

        Log.d("LoginFragment",  role + " " + user.getId());
        Fragment fragment = null;
        if ("Администратор".equals(role)) {
            fragment = new MainAdminFragment();
        } else if ("Официант".equals(role)) {
            Log.d("LoginFragment",  "офик");
            fragment = new WaiterMainFragment();
        } else if ("Повар".equals(role) || "Бармен".equals(role)) {
            Log.d("LoginFragment", "офик");
            fragment = new CookMainFragment();
        }
        else {
            Toast.makeText(getContext(), "Unknown role", Toast.LENGTH_SHORT).show();
            return;
        }
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}



