package org.pursuit.heard.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;

import org.pursuit.heard.R;
import org.pursuit.heard.databinding.FragmentLoginBinding;
import org.pursuit.heard.viewmodel.UserViewModel;

import io.reactivex.disposables.Disposable;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private SharedPreferences preferences;
    private Disposable disposable;

    private String emailInput;
    private String passwordInput;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        checkPreferences();
        return binding.getRoot();
    }

    private void checkPreferences() {
        preferences = requireContext()
                .getSharedPreferences(
                        getString(R.string.shared_prefs_key),
                        Context.MODE_PRIVATE);

        if (preferences.getBoolean(
                getString(R.string.login_checkbox_key), true)) {
            if (preferences.contains(getString(R.string.user_name_key)) &&
                    preferences.contains(getString(R.string.password_key))) {
                
                String savedUser = preferences.getString(
                        getString(R.string.user_name_key), "");
                String savedPassword = preferences.getString(
                        getString(R.string.password_key), "");

                binding.emailEdittext.setText(savedUser);
                binding.passwordEdittext.setText(savedPassword);
            }
            binding.rememberMeCheckbox.setChecked(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        disposable = RxTextView
                .textChanges(binding.emailEdittext)
                .subscribe(input -> emailInput = input.toString(),
                        Throwable::printStackTrace);

        disposable = RxTextView
                .textChanges(binding.passwordEdittext)
                .subscribe(input -> passwordInput = input.toString(),
                        Throwable::printStackTrace);

        disposable = RxView
                .clicks(binding.rememberMeCheckbox)
                .subscribe(unit -> {
                    if (binding.rememberMeCheckbox.isChecked()) {
                        preferences.edit().putBoolean(
                                LoginFragment.this.getString(R.string.login_checkbox_key), true).apply();
                        preferences.edit().putString(
                                LoginFragment.this.getString(R.string.user_name_key), emailInput).apply();
                        preferences.edit().putString(
                                LoginFragment.this.getString(R.string.password_key), passwordInput).apply();

                    } else {
                        preferences.edit().putBoolean(
                                LoginFragment.this.getString(R.string.login_checkbox_key), false).apply();
                        preferences.edit().remove(LoginFragment.this.getString(R.string.user_name_key)).apply();
                        preferences.edit().remove(LoginFragment.this.getString(R.string.password_key)).apply();
                    }
                }, Throwable::printStackTrace);


        disposable = RxView
                .clicks(binding.signInButton)
                .subscribe(unit -> {
                    InputMethodManager mgr = (InputMethodManager) requireActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(binding.passwordEdittext.getWindowToken(), 0);

                    UserViewModel viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
                    if (viewModel.verifyLogin(emailInput, passwordInput)) {
                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mainUserFragment);
                    }
                }, Throwable::printStackTrace);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
