package com.example.auth.fingerpint.fouliex.fingerprintauth;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.auth.fingerpint.fouliex.fingerprintauth.api.FingerprintAuth;
import com.example.auth.fingerpint.fouliex.fingerprintauth.api.FingerprintHandler;
import com.example.auth.fingerpint.fouliex.fingerprintauth.exception.FingerprintException;

import static android.graphics.Color.BLACK;


public class MainActivity extends AppCompatActivity {
    private TextView message;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        message = (TextView) findViewById(R.id.fingerStatus);
        Button btn = (Button) findViewById(R.id.authBtn);


        final FingerprintHandler fingerPrintHandler = new FingerprintHandler(message);
        final FingerprintAuth fingerPrintAuth = new FingerprintAuth(MainActivity.this, message);

        if (!fingerPrintAuth.checkFinger()) {
            btn.setEnabled(false);
        } else {
            /** We are ready to set up the cipher and the key */
            try {
                fingerPrintAuth.setupKeyAndCipher();
            } catch (FingerprintException fpe) {
                // Handle exception
                btn.setEnabled(false);
            }
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                message.setTextColor(BLACK);
                message.setText("Swipe your finger");
                fingerPrintHandler.doAuth(fingerPrintAuth.getFingerprintManager(), fingerPrintAuth.getCryptoObject());
            }
        });
    }


}
