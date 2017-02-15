package com.example.auth.fingerpint.fouliex.fingerprintauth.api;
import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.TextView;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;


/**
 * Created by George Fouche on 2/13/17.
 */
@TargetApi(Build.VERSION_CODES.M)
public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {
    private TextView message;

    public FingerPrintHandler(TextView message) {
        this.message = message;
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        super.onAuthenticationError(errorCode, errString);
        message.setText("Auth error");
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        super.onAuthenticationHelp(helpCode, helpString);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        message.setText("auth ok");
        message.setTextColor(GREEN);

    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        message.setText("auth failed");
        message.setTextColor(RED);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void doAuth(FingerprintManager manager, FingerprintManager.CryptoObject obj) {
        CancellationSignal signal = new CancellationSignal();

        try {
            manager.authenticate(obj, signal, 0, this, null);
        } catch (SecurityException sce) {
        }
    }
}
