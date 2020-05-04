/*
 * Copyright 2018 Rozdoum
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.conquislike.adancondori.main.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.conquislike.adancondori.Application;
import com.conquislike.adancondori.main.specialties.SpecialtyActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.conquislike.adancondori.R;
import com.conquislike.adancondori.main.base.BaseActivity;
import com.conquislike.adancondori.main.editProfile.createProfile.CreateProfileActivity;
import com.conquislike.adancondori.utils.GoogleApiHelper;
import com.conquislike.adancondori.utils.LogUtil;
import com.conquislike.adancondori.utils.LogoutHelper;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class LoginActivity extends BaseActivity<LoginView, LoginPresenter> implements LoginView,
        GoogleApiClient.OnConnectionFailedListener, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int SIGN_IN_GOOGLE = 9001;
    public static final int LOGIN_REQUEST_CODE = 10001;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;

    private CallbackManager mCallbackManager;
    private String profilePhotoUrlLarge;
    // STORAGE PERMMISSION

    private static final String[] STORAGE =
            {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    private static final int RC_WRITE_STORAGE_PERM = 1230;
    private static final int RC_READ_STORAGE_PERM = 1240;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initGoogleSignIn();
        initFirebaseAuth();
        initFacebookSignIn();
        writeStorageTask();
    }

    private void initGoogleSignIn() {
        mGoogleApiClient = GoogleApiHelper.createGoogleApiClient(this);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.googleSignInButton).setOnClickListener(view -> presenter.onGoogleSignInClick());
    }

    private void initFirebaseAuth() {
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            LogoutHelper.signOut(mGoogleApiClient, this);
        }

        mAuthListener = firebaseAuth -> {
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // Profile is signed in
                LogUtil.logDebug(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                presenter.checkIsProfileExist(user.getUid());
                setResult(RESULT_OK);
            } else {
                // Profile is signed out
                LogUtil.logDebug(TAG, "onAuthStateChanged:signed_out");
            }
        };
    }

    private void initFacebookSignIn() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                LogUtil.logDebug(TAG, "facebook:onSuccess:" + loginResult);
                presenter.handleFacebookSignInResult(loginResult);
            }

            @Override
            public void onCancel() {
                LogUtil.logDebug(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                LogUtil.logError(TAG, "facebook:onError", error);
                showSnackBar(error.getMessage());
            }
        });

        findViewById(R.id.facebookSignInButton).setOnClickListener(v -> presenter.onFacebookSignInClick());
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
    }

    @NonNull
    @Override
    public LoginPresenter createPresenter() {
        if (presenter == null) {
            return new LoginPresenter(this);
        }
        return presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            presenter.handleGoogleSignInResult(result);
        } else if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            String yes = "SI";
            String no = "NO";

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                    this,
                    getString(R.string.text_storage_write, hasWriteStorageTask() ? yes : no),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void startCreateProfileActivity() {
        Intent intent = new Intent(LoginActivity.this, CreateProfileActivity.class);
        intent.putExtra(CreateProfileActivity.LARGE_IMAGE_URL_EXTRA_KEY, profilePhotoUrlLarge);
        startActivity(intent);
    }

    @Override
    public void firebaseAuthWithCredentials(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    LogUtil.logDebug(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        presenter.handleAuthError(task);
                    }
                });
    }

    @Override
    public void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, SpecialtyActivity.class);
        startActivity(intent);
    }

    @Override
    public void setProfilePhotoUrl(String url) {
        profilePhotoUrlLarge = url;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        LogUtil.logDebug(TAG, "onConnectionFailed:" + connectionResult);
        showSnackBar(R.string.error_google_play_services);
        hideProgress();
    }

    @Override
    public void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, SIGN_IN_GOOGLE);
    }

    @Override
    public void signInWithFacebook() {
        if (hasWriteStorageTask()){
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
        } else {
            writeStorageTask();
        }

    }


    // PERMISSION STORAGE CELL PHONE
    @AfterPermissionGranted(RC_WRITE_STORAGE_PERM)
    public void writeStorageTask() {
        if (hasWriteStorageTask()) {
            // Have permission, do the thing!
            //Toast.makeText(this, "Ahora puede Ingresar con FaceBook", Toast.LENGTH_LONG).show();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_storage_write),
                    RC_WRITE_STORAGE_PERM,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
    private boolean hasWriteStorageTask() {
        return EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }


    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }
}

