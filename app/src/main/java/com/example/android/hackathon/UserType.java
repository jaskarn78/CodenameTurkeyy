package com.example.android.hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.percent.PercentLayoutHelper;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.Manifest;

import com.facebook.FacebookSdk;

import pub.devrel.easypermissions.EasyPermissions;

/** Activity for selecting the User Type of the application. */
public class UserType extends AppCompatActivity {
    private Button userLoginBtn;
    private Button driverLoginBtn;
    private TextView tvDriverSigninInvoker;
    private LinearLayout llDriverSignin;
    private TextView tvUserSigninInvoker;
    private LinearLayout llUserSignin;

    private static final int RC_LOCATION_SERVICE = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_user_type);

        // Create permissions
        setupPermissions();

        // Create login buttons
        setupLoginButtons();

        // Create User Type Switching
        setupUserTypeSwitcher();
    }


    /** Setup user permissions */
    private void setupPermissions() {
        // Create Permissions for users (Access locational services, Camera, and Internet Access)
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            String perms[] = {  Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.INTERNET,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
            // Set message for requesting permissions
            EasyPermissions.requestPermissions(this, "This app requires location services",
                    RC_LOCATION_SERVICE, perms);
        }
    }


    /**
     * Creates the login buttons for General User and Truck Drivers
     */
    private void setupLoginButtons() {
        // Create User Login button, create an OnClickListener
        userLoginBtn=(Button)findViewById(R.id.userLogin);
        userLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });

        // Create Driver Login button, create an OnClickListener
        driverLoginBtn=(Button)findViewById(R.id.driverLogin);
        driverLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverLogin();
            }
        });
    }


    /** Creates an intent to move to DriverActivity and starts the activity */
    private void driverLogin(){
        Intent intent = new Intent(this, DriverActivity.class);
        startActivity(intent);
    }


    /** Creates an intent to move to UserActivity and starts the activity */
    private void userLogin(){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }


    /** Enables user to switch between Driver and User signins */
    private void setupUserTypeSwitcher(){
        // Create Linear Layout for User and Driver login views
        llDriverSignin = (LinearLayout)findViewById(R.id.ll_driver_signin);
        llUserSignin = (LinearLayout)findViewById(R.id.ll_user_signin);

        // Text View for Driver and User Signin
        tvDriverSigninInvoker = (TextView) findViewById(R.id.tv_driver_sigin_invoker);
        tvUserSigninInvoker = (TextView) findViewById(R.id.tv_user_signin_invoker);

        // Create onClickListeners for each user type view selection
        tvDriverSigninInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDriverForm();
            }
        });
        tvUserSigninInvoker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserForm();
            }
        });
        showUserForm();

    }


    /**
     *  On user selection of the Food Truck Driver panel, this method will create the driver view.
     */
    // TODO Create inline comments for showDriverForm
    private void showDriverForm() {
        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llUserSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.15f;
        llUserSignin.requestLayout();

        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llDriverSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.85f;
        llDriverSignin.requestLayout();

        tvDriverSigninInvoker.setVisibility(View.GONE);
        tvUserSigninInvoker.setVisibility(View.VISIBLE);
        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_right_to_left);
        llDriverSignin.startAnimation(translate);

        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
        userLoginBtn.startAnimation(clockwise);

    }


    /**
     *  On user selection of the User panel, this method will create the user view.
     */
    // TODO Create inline comments for showUserForm
    private void showUserForm() {
        PercentRelativeLayout.LayoutParams paramsLogin = (PercentRelativeLayout.LayoutParams) llUserSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoLogin = paramsLogin.getPercentLayoutInfo();
        infoLogin.widthPercent = 0.85f;
        llUserSignin.requestLayout();


        PercentRelativeLayout.LayoutParams paramsSignup = (PercentRelativeLayout.LayoutParams) llDriverSignin.getLayoutParams();
        PercentLayoutHelper.PercentLayoutInfo infoSignup = paramsSignup.getPercentLayoutInfo();
        infoSignup.widthPercent = 0.15f;
        llDriverSignin.requestLayout();

        Animation translate= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate_left_to_right);
        llUserSignin.startAnimation(translate);

        tvDriverSigninInvoker.setVisibility(View.VISIBLE);
        tvUserSigninInvoker.setVisibility(View.GONE);

        Animation clockwise= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_right_to_left);
        driverLoginBtn.startAnimation(clockwise);
    }
}
