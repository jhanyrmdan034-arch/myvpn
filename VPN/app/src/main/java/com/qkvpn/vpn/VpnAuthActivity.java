package com.qkvpn.vpn;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.os.RemoteException;

import androidx.appcompat.app.AppCompatActivity;
import de.blinkt.openvpn.OpenVpnApi;

public class VpnAuthActivity extends AppCompatActivity {
    public static final String KEY_CONFIG = "config";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    private String mConfig;
    private String mUsername;
    private String mPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = getIntent().getStringExtra(KEY_CONFIG);
        mUsername = getIntent().getStringExtra(KEY_USERNAME);
        mPw = getIntent().getStringExtra(KEY_PASSWORD);
        try {
            Intent intent = VpnService.prepare(this);

            if (intent != null) {
                startActivityForResult(intent, 0);

            } else {

                startVpn();
                finish();
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
//            VpnProfileControlActivity.VpnNotSupportedError.showWithMessage(this, R.string.vpn_not_supported);

            finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                startVpn();
                finish();
            } else {
                finish();
            }
        } catch (UnknownError e) {
            e.printStackTrace();
        }

    }

    public void startVpn() {
        try {
            OpenVpnApi.startVpn(this, mConfig,  "", mUsername, mPw);
        } catch (RemoteException ignore) {

        }
    }
}
