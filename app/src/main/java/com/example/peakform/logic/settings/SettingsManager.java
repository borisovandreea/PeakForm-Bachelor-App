package com.example.peakform.logic.settings;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsManager {
    private static final String PREFS_NAME = "PeakFormSettings";
    private static final String KEY_DOMAINS_SET = "active_domains_set";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_PIN_HASH = "user_pin_hash";

    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_DOMAINS_SET)) {
            initDefaults();
        }
    }

    private void initDefaults() {
        Set<String> defaults = new HashSet<>();
        defaults.add("Physical Fitness");
        defaults.add("Mental Health");
        defaults.add("Productivity");
        defaults.add("Social Connection");
        defaults.add("Learning & Growth");

        prefs.edit().putStringSet(KEY_DOMAINS_SET, defaults).apply();

        saveDomainSettings("Physical Fitness", 20, 30, "Daily");
        saveDomainSettings("Mental Health", 20, 20, "Daily");
        saveDomainSettings("Productivity", 20, 120, "Daily");
        saveDomainSettings("Social Connection", 20, 60, "Daily");
        saveDomainSettings("Learning & Growth", 20, 45, "Daily");
    }

    public void resetToDefaults() {
        prefs.edit().clear().apply();
        initDefaults();
    }


    public void saveUsername(String name) {
        prefs.edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUsername() {
        return prefs.getString(KEY_USER_NAME, "Explorer");
    }

    public boolean hasPinSet() {
        return prefs.contains(KEY_PIN_HASH);
    }

    public void savePin(String pin) {
        String hashedPin = hashString(pin);
        prefs.edit().putString(KEY_PIN_HASH, hashedPin).apply();
    }

    public boolean checkPin(String inputPin) {
        String storedHash = prefs.getString(KEY_PIN_HASH, "");
        return storedHash.equals(hashString(inputPin));
    }

    private String hashString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] array = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (Exception e) {
            return String.valueOf(input.hashCode());
        }
    }


    public List<String> getAllDomains() {
        return new ArrayList<>(prefs.getStringSet(KEY_DOMAINS_SET, new HashSet<>()));
    }

    public void addNewDomain(String name) {
        Set<String> current = new HashSet<>(prefs.getStringSet(KEY_DOMAINS_SET, new HashSet<>()));
        current.add(name);
        prefs.edit().putStringSet(KEY_DOMAINS_SET, current).apply();
    }

    public void saveDomainSettings(String name, int weight, int targetValue, String unit) {
        prefs.edit()
                .putFloat("w_" + name, weight / 100f)
                .putInt("t_" + name, targetValue)
                .putString("u_" + name, unit)
                .apply();
    }

    // --- GETTERS & CALCULATIONS ---

    public float getWeightByDomain(String name) {
        return prefs.getFloat("w_" + name, 0.20f);
    }

    public int getRawTarget(String name) {
        return prefs.getInt("t_" + name, 60);
    }

    public String getUnit(String name) {
        return prefs.getString("u_" + name, "Daily");
    }

    public int getDailyMinutesTarget(String name) {
        int val = getRawTarget(name);
        String unit = getUnit(name);
        if ("Weekly".equalsIgnoreCase(unit)) return val / 7;
        if ("Monthly".equalsIgnoreCase(unit)) return val / 30;
        return val;
    }

    public int getWeeklyMinutesTarget(String name) {
        int val = getRawTarget(name);
        String unit = getUnit(name);
        if ("Weekly".equalsIgnoreCase(unit)) return val;
        if ("Monthly".equalsIgnoreCase(unit)) return (val / 30) * 7;
        return val * 7;
    }
}