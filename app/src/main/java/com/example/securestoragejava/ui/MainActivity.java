package com.example.securestoragejava.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.securestoragejava.R;
import com.example.securestoragejava.cache.CacheStore;
import com.example.securestoragejava.files.InternalTextStore;
import com.example.securestoragejava.files.StudentsJsonStore;
import com.example.securestoragejava.model.Student;
import com.example.securestoragejava.prefs.AppPrefs;
import com.example.securestoragejava.prefs.SecurePrefs;
import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SecureStorageJava";
    private final List<String> langs = Arrays.asList("fr", "en", "ar");

    private EditText etName;
    private EditText etToken;
    private Spinner spLang;
    private MaterialSwitch swDark;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etToken = findViewById(R.id.etToken);
        spLang = findViewById(R.id.spLang);
        swDark = findViewById(R.id.swDark);
        tvResult = findViewById(R.id.tvResult);

        setupLangSpinner();

        Button btnSavePrefs = findViewById(R.id.btnSavePrefs);
        Button btnLoadPrefs = findViewById(R.id.btnLoadPrefs);
        Button btnSaveJson = findViewById(R.id.btnSaveJson);
        Button btnLoadJson = findViewById(R.id.btnLoadJson);
        Button btnClear = findViewById(R.id.btnClear);

        btnSavePrefs.setOnClickListener(v -> savePrefs());
        btnLoadPrefs.setOnClickListener(v -> loadPrefsToUi());
        btnSaveJson.setOnClickListener(v -> saveJsonFile());
        btnLoadJson.setOnClickListener(v -> loadJsonFile());
        btnClear.setOnClickListener(v -> clearAll());

        loadPrefsToUi();
    }

    private void setupLangSpinner() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, langs);
        spLang.setAdapter(adapter);
    }

    private void savePrefs() {
        String name = etName.getText().toString().trim();
        String lang = langs.get(Math.max(0, spLang.getSelectedItemPosition()));
        String theme = swDark.isChecked() ? "dark" : "light";
        String token = etToken.getText().toString().trim();

        // 1. Stockage clair pour données non sensibles
        boolean ok = AppPrefs.save(this, name, lang, theme, false);

        // 2. Stockage chiffré pour le secret (Token)
        if (!token.isEmpty()) {
            try {
                SecurePrefs.saveToken(this, token);
            } catch (Exception e) {
                tvResult.setText("Erreur sécurité : impossible de chiffrer le token.");
                Log.e(TAG, "Erreur chiffrement", e);
                return;
            }
        }

        // 3. LOG SÉCURISÉ : On ne logue JAMAIS le contenu du token
        Log.d(TAG, "Profil mis à jour. Token présent : " + (!token.isEmpty()));

        try {
            CacheStore.write(this, "last_ui.txt", "name=" + name + ", theme=" + theme);
        } catch (Exception ignored) {}

        tvResult.setText("Sauvegarde prefs terminée.\n" +
                "Nom : " + name + "\n" +
                "Thème : " + theme + "\n" +
                "Token : " + (token.isEmpty() ? "Absent" : "Chiffré (AES-256)"));
        
        // Optionnel : Effacer le champ token après sauvegarde pour plus de sécurité
        etToken.setText("");
    }

    private void loadPrefsToUi() {
        AppPrefs.Triple triple = AppPrefs.load(this);

        etName.setText(triple.name);
        swDark.setChecked("dark".equals(triple.theme));

        int idx = langs.indexOf(triple.lang);
        spLang.setSelection(idx >= 0 ? idx : 0);

        String tokenInfo;
        try {
            String token = SecurePrefs.loadToken(this);
            // On affiche uniquement la longueur ou le statut, JAMAIS le texte en clair dans les logs
            if (token == null) {
                tokenInfo = "Expiré ou absent";
            } else {
                tokenInfo = "Présent (longueur: " + token.length() + ")";
            }
        } catch (Exception e) {
            tokenInfo = "Erreur de déchiffrement";
            Log.e(TAG, "Erreur lecture secure prefs", e);
        }

        tvResult.setText("Chargement sécurisé effectué.\n" +
                "Utilisateur : " + (triple.name.isEmpty() ? "(vide)" : triple.name) + "\n" +
                "Session Token : " + tokenInfo);

        Log.d(TAG, "Données chargées. Statut Token: " + tokenInfo);
    }

    private void saveJsonFile() {
        List<Student> students = Arrays.asList(
                new Student(1, "Amina", 20),
                new Student(2, "Omar", 21),
                new Student(3, "Sara", 19)
        );

        try {
            StudentsJsonStore.save(this, students);
            InternalTextStore.writeUtf8(this, "note.txt", "Sauvegarde JSON effectuée (UTF-8).");
        } catch (Exception e) {
            tvResult.setText("Erreur sauvegarde JSON : " + e.getMessage());
            return;
        }

        Log.d(TAG, "Fichiers internes écrits en MODE_PRIVATE");
        tvResult.setText("Sauvegarde fichier JSON terminée. Étudiants : " + students.size());
    }

    private void loadJsonFile() {
        List<Student> students = StudentsJsonStore.load(this);

        String note;
        try {
            note = InternalTextStore.readUtf8(this, "note.txt");
        } catch (Exception e) {
            note = "(note.txt absent)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Chargement fichier JSON terminé.\n");
        sb.append("Note : ").append(note).append("\n");
        sb.append("Nombre d'étudiants : ").append(students.size());

        tvResult.setText(sb.toString());
    }

    private void clearAll() {
        AppPrefs.clear(this);

        try {
            SecurePrefs.clear(this);
        } catch (Exception e) {
            Log.e(TAG, "Erreur clear secure prefs", e);
        }

        StudentsJsonStore.delete(this);
        InternalTextStore.delete(this, "note.txt");
        int purged = CacheStore.purge(this);

        etName.setText("");
        etToken.setText("");
        swDark.setChecked(false);
        spLang.setSelection(0);

        tvResult.setText("Nettoyage complet effectué (Données + Cache).\nFichiers purgés : " + purged);
        Log.d(TAG, "Toutes les données locales ont été supprimées.");
    }
}