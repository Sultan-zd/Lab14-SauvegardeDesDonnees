package com.example.securestoragejava.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

public final class SecurePrefs {

    private static final String PREFS_NAME = "secure_prefs";
    private static final String KEY_API_TOKEN = "secure_api_token";
    private static final String KEY_TIMESTAMP = "token_created_at";
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 heures

    private SecurePrefs() {}

    private static SharedPreferences securePrefs(Context context) throws Exception {
        MasterKey masterKey = new MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build();

        return EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );
    }

    public static void saveToken(Context context, String token) throws Exception {
        securePrefs(context).edit()
                .putString(KEY_API_TOKEN, token)
                .putLong(KEY_TIMESTAMP, System.currentTimeMillis())
                .apply();
    }

    public static String loadToken(Context context) throws Exception {
        SharedPreferences prefs = securePrefs(context);
        long createdAt = prefs.getLong(KEY_TIMESTAMP, 0);

        // Vérification de l'expiration
        if (System.currentTimeMillis() - createdAt > EXPIRATION_MS) {
            prefs.edit().remove(KEY_API_TOKEN).remove(KEY_TIMESTAMP).apply();
            return null;
        }
        return prefs.getString(KEY_API_TOKEN, null);
    }

    public static void clear(Context context) throws Exception {
        securePrefs(context).edit().clear().apply();
    }
}