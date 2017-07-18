package com.google.samples.quickstart.signin;

/**
 * Created by SChau on 7/14/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.TextView;


import com.microsoft.aad.adal.AuthenticationCallback;
import com.microsoft.aad.adal.AuthenticationContext;
import com.microsoft.aad.adal.AuthenticationResult;

import com.google.samples.quickstart.signin.helpers.AzureConstants;
import com.google.samples.quickstart.signin.helpers.AzureInMemoryCacheStore;



import java.util.UUID;


public class AzureSignInActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "AzureSignInActivity";


    private AuthenticationContext mAuthContext;
    private ProgressDialog mProgressDialog;
    private TextView mStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);
        TextView title = (TextView) findViewById(R.id.title_text);
        title.setText(R.string.azure_title_text);

        ImageView logo = (ImageView) findViewById(R.id.google_icon);
        logo.setImageResource(R.drawable.microsoft_logo);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        showProgressDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideProgressDialog();
    }


    // [START onActivityResult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mAuthContext != null) {
            mAuthContext.onActivityResult(requestCode, resultCode, data);
        }
    }
    // [END onActivityResult]

    // [START signIn]
    private void signIn() {

        // Ask for token and provide callback
        try {
            //  create the authentication context
            mAuthContext = new AuthenticationContext(AzureSignInActivity.this, AzureConstants.AUTHORITY_URL,
                    false, AzureInMemoryCacheStore.getInstance());
            mAuthContext.getCache().removeAll();

            if(AzureConstants.CORRELATION_ID != null &&
                    AzureConstants.CORRELATION_ID.trim().length() !=0){
                mAuthContext.setRequestCorrelationId(UUID.fromString(AzureConstants.CORRELATION_ID));
            }

            // ask for token, using the defined callback
            mAuthContext.acquireToken(AzureSignInActivity.this, AzureConstants.RESOURCE_ID,
                    AzureConstants.CLIENT_ID, AzureConstants.REDIRECT_URL, AzureConstants.USER_HINT,
                    "nux=1&" + AzureConstants.EXTRA_QP, authCallback);
            // nux=1 : // if this strikes you as strange it was legacy to display the correct mobile UX. You most likely won't need it in your code.

        } catch (Exception e) {
            SimpleAlertDialog.showAlertDialog(getApplicationContext(), "Exception caught", e.getMessage());
        }



    }
    // [END signIn]

    private void signOut() {
        // Clear the cache.



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP));
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                // a callback which is executed when the cookies have been removed
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    Log.d(TAG, "Cookie removed: " + aBoolean);
                }
            });
            cookieManager.flush();
//        CookieSyncManager.getInstance().sync();
        }else{
            Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP));
            //CookieSyncManager cookieSyncMngr= CookieSyncManager.createInstance(context);
            //cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            //cookieSyncMngr.stopSync();
            //cookieSyncMngr.sync();
        }

        mAuthContext.getCache().removeAll();

        // check cache
        Log.d(TAG, "SignOut: cache has next" + AzureInMemoryCacheStore.getInstance().getAll().hasNext());

        //AzureInMemoryCacheStore.getInstance().removeAll();

        updateUI(false);
    }

    private AuthenticationCallback<AuthenticationResult> authCallback = new AuthenticationCallback<AuthenticationResult>() {

        @Override
        public void onError(Exception exc) {

            Log.d(TAG, "Authentication error:" + exc.getMessage());

            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            SimpleAlertDialog.showAlertDialog(AzureSignInActivity.this,
                    "Failed to get token", exc.getMessage());
        }

        @Override
        public void onSuccess(AuthenticationResult result) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            if (result == null || result.getAccessToken() == null
                    || result.getAccessToken().isEmpty()) {
                mStatusTextView.setText("Token is empty");
                Log.d(TAG, "Token is empty");
            } else {
                // request is successful
                setLocalToken(result);
                updateLoggedInUser();
                updateUI(true);
                //getTasks();

                Log.d(TAG, "Status:" + result.getStatus() + " Expired:"
                        + result.getExpiresOn().toString());

                Log.d(TAG, "SignIn : cache has next " + AzureInMemoryCacheStore.getInstance().getAll().hasNext());
            }
        }

    };

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void setLocalToken(AuthenticationResult newToken) {
        AzureConstants.CURRENT_RESULT = newToken;
    }

    private void updateLoggedInUser() {
        TextView textView = (TextView) findViewById(R.id.status);
        textView.setText("N/A");
        if (AzureConstants.CURRENT_RESULT != null) {
            if (AzureConstants.CURRENT_RESULT.getIdToken() != null) {
                textView.setText(AzureConstants.CURRENT_RESULT.getUserInfo().getDisplayableId() + AzureConstants.CURRENT_RESULT.getAccessToken());

            } else {
                textView.setText("User with No ID Token");
            }
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
        findViewById(R.id.disconnect_button).setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                //revokeAccess();
                break;
        }
    }
}
