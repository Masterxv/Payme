package com.example.myapplicati.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.myapplicati.R;
import com.example.myapplicati.ui.login.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    private static ApiClient mApi;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.app_bar)
    AppBarLayout appBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = view.findViewById(R.id.activity_content_holder);
        DataBindingUtil.inflate(getLayoutInflater(), getLayoutId(), activityContainer, true);
    }

    public ApiClient getApi() {
        if (mApi == null) {
            mApi = ApiService.getClient().create(ApiClient.class);
        }
        return mApi;
    }

    public abstract
    @LayoutRes
    int getLayoutId();

    @Override
    public void setContentView(final int layoutResID) {
        View view = getLayoutInflater().inflate(R.layout.activity_base, null);
        FrameLayout activityContainer = view.findViewById(R.id.activity_content_holder);
        DataBindingUtil.inflate(getLayoutInflater(), getLayoutId(), activityContainer, true);
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void toggleProgress(boolean isLoading) {
        if (isLoading)
            showProgress();
        else
            hideProgress();
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    public void handleError(Throwable throwable) {
        showErrorDialog(getString(R.string.msg_unknown));
    }

    public void handleUnknownError() {
        showErrorDialog(getString(R.string.msg_unknown));
    }

    public void handleError(ResponseBody responseBody) {
        String message = null;
        if (responseBody != null) {
            try {
                ErrorResponse errorResponse = new Gson().fromJson(responseBody.charStream(), ErrorResponse.class);
                message = errorResponse.error;
            } catch (JsonSyntaxException e) {
            } catch (JsonIOException e) {
            }
        }

        message = TextUtils.isEmpty(message) ? getString(R.string.msg_unknown) : message;
        showErrorDialog(message);
    }

    public void showErrorDialog(String message) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                })
                .show();
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void hideToolbar() {
        appBar.setVisibility(View.GONE);
    }

    public void changeStatusBarColor() {
        changeStatusBarColor(Color.WHITE);
    }

    public void changeStatusBarColor(int color) {
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            window.setStatusBarColor(color);
        }
    }

    public void makeFullScreen() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    public void enableToolbarUpNavigation() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void launchSplash(Activity activity) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void launchLogin(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void checkSession(Activity activity) {
        User user = AppDatabase.getUser();
        if (user == null) {
            Intent intent = new Intent(activity, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}