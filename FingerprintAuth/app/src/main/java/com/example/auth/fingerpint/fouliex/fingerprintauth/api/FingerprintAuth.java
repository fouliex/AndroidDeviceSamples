package com.example.auth.fingerpint.fouliex.fingerprintauth.api;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.widget.TextView;

import com.example.auth.fingerpint.fouliex.fingerprintauth.exception.FingerprintException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by George Fouche on 2/14/17.
 */

public class FingerprintAuth {
    private TextView message;
    private static final String KEY_NAME="gerol";


    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private Context context;
    private  FingerprintManager fingerprintManager;
    private FingerprintManager.CryptoObject cryptoObject;

    public FingerprintAuth(Context context, TextView message) {
        this.context = context;
        this.message =message;

    }

    public FingerprintManager getFingerprintManager() {
        return fingerprintManager;
    }

    public FingerprintManager.CryptoObject getCryptoObject() {
        return cryptoObject;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkFinger() {

        /** Keyguard Manager */
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);

        /** Fingerprint Manager */
        fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);

        try {
            /** Check if the fingerprint sensor is present */
            if (!fingerprintManager.isHardwareDetected()) {
                message.setText("Fingerprint authentication not supported");
                return false;
            }
            /** Verify that at least one fingerprint is registered on the smartphone */
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                message.setText("No fingerprint configured.");
                return false;
            }
            /** Verify that the lock screen is secure, protected by PIN,password or pattern */
            if (!keyguardManager.isKeyguardSecure()) {
                message.setText("Secure lock screen not enabled");
                return false;
            }

        } catch (SecurityException se) {
            se.printStackTrace();
        }
        return true;

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void setupKeyAndCipher() throws FingerprintException {
        generateKey();
        Cipher cipher = generateCipher();
        cryptoObject = new FingerprintManager.CryptoObject(cipher);
    }


    /**
     * Get access to Android keystore to store the key used to encrypt/decrypt an object and then
     * generate an encryption key
     * @throws FingerprintException
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void generateKey() throws FingerprintException {
        try {
            // Get the reference to the key store
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            // Key generator to generate the key
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }


    }

    /**
     * Generate the Cipher
     * @return
     * @throws FingerprintException
     */
    public Cipher generateCipher() throws FingerprintException {
        try {
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidKeyException
                | UnrecoverableKeyException
                | KeyStoreException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }
}
