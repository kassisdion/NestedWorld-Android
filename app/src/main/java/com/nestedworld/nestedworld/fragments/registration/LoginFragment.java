package com.nestedworld.nestedworld.fragments.registration;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.nestedworld.nestedworld.R;
import com.nestedworld.nestedworld.activities.mainMenu.MainMenuActivity;
import com.nestedworld.nestedworld.api.http.errorHandler.RetrofitErrorHandler;
import com.nestedworld.nestedworld.api.http.implementation.NestedWorldHttpApi;
import com.nestedworld.nestedworld.api.http.models.response.users.auth.ForgotPasswordResponse;
import com.nestedworld.nestedworld.api.http.models.response.users.auth.SignInResponse;
import com.nestedworld.nestedworld.authenticator.UserManager;
import com.nestedworld.nestedworld.fragments.base.BaseFragment;
import com.rey.material.widget.ProgressView;

import butterknife.Bind;
import butterknife.OnClick;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginFragment extends BaseFragment {

    public final static String FRAGMENT_NAME = LoginFragment.class.getSimpleName();

    @Bind(R.id.editText_userEmail)
    EditText etEmail;
    @Bind(R.id.editText_userPassword)
    EditText etPassword;
    @Bind(R.id.progressView)
    ProgressView progressView;

    /*
    ** Public method
     */
    public static void load(@NonNull final FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
        fragmentTransaction.replace(R.id.container, new LoginFragment());
        fragmentTransaction.addToBackStack(FRAGMENT_NAME);
        fragmentTransaction.commit();
    }

    /*
    ** Life Cycle
     */
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_login;
    }

    @Override
    protected void init(View rootView, Bundle savedInstanceState) {

    }

    /*
    ** ButterKnife callback
     */
    @OnClick(R.id.nav_back)
    public void back() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.button_login)
    public void login() {

        progressView.start();

        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();

        if (mContext == null)
            return;
        NestedWorldHttpApi.getInstance(mContext).signIn(
                email,
                password,
                new com.nestedworld.nestedworld.api.http.callback.Callback<SignInResponse>() {
                    @Override
                    public void onSuccess(Response<SignInResponse> response, Retrofit retrofit) {
                        progressView.stop();

                        if (UserManager.get(mContext).setCurrentUser(mContext, email, password, response.body().token, null)) {
                            //display the mainMenu and stop the launchActivity
                            startActivity(MainMenuActivity.class);
                            ((FragmentActivity) mContext).finish();
                        } else {
                            Toast.makeText(mContext, R.string.error_create_account, Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<SignInResponse> response) {
                        progressView.stop();
                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_login), response);
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    @OnClick(R.id.textView_forgotPassword)
    public void forgotPassword() {
        final String email = etEmail.getText().toString();

        if (mContext == null)
            return;
        NestedWorldHttpApi.getInstance(mContext).forgotPassword(
                email,
                new com.nestedworld.nestedworld.api.http.callback.Callback<ForgotPasswordResponse>() {
                    @Override
                    public void onSuccess(Response<ForgotPasswordResponse> response, Retrofit retrofit) {
                        Toast.makeText(mContext, getString(R.string.password_send), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(@NonNull KIND errorKind, @Nullable Response<ForgotPasswordResponse> response) {
                        final String errorMessage = RetrofitErrorHandler.getErrorMessage(mContext, errorKind, getString(R.string.error_forgot_password), response);
                        Toast.makeText(mContext, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}