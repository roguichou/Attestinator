package com.roguichou.attestinator;

import android.content.SharedPreferences;

public class Profil
{
    //Préférences
    private SharedPreferences settings;
    //clé de Préférences
    private static final String KEY_NAME = "nom";
    private static final String KEY_FIRST_NAME = "prenom";
    private static final String KEY_BIRTH_DATE = "date_naiss";
    private static final String KEY_BIRTH_LOCATION = "lieu_naiss";
    private static final String KEY_ADD = "addresse";
    private static final String KEY_POST_CODE = "code_postal";
    private static final String KEY_CITY = "ville";

    private String profil_name = null;
    private String profil_first_name = null;
    private String profil_birth_date = null;
    private String profil_birth_location = null;
    private String profil_address = null;
    private String profil_post_code = null;
    private String profil_city = null;


    public void parsePreferences(SharedPreferences settings)
    {
        this.settings = settings;
        profil_name = settings.getString(KEY_NAME, null);
        profil_first_name = settings.getString(KEY_FIRST_NAME, null);
        profil_birth_date = settings.getString(KEY_BIRTH_DATE, null);
        profil_birth_location = settings.getString(KEY_BIRTH_LOCATION, null);
        profil_address = settings.getString(KEY_ADD, null);
        profil_post_code = settings.getString(KEY_POST_CODE, null);
        profil_city = settings.getString(KEY_CITY, null);
    }

    public boolean isValid()
    {
        return (null!=profil_name &&
                null!=profil_first_name &&
                null!=profil_birth_date &&
                null!=profil_birth_location &&
                null!=profil_address &&
                null!=profil_post_code &&
                null!=profil_city );

    }

    public String getProfilName() {
        return profil_name;
    }
    public String getProfilFirstName() {
        return profil_first_name;
    }
    public String getProfilBirthDate() {
        return profil_birth_date;
    }
    public String getProfilBirthLocation() {
        return profil_birth_location;
    }
    public String getProfilAddress() {
        return profil_address;
    }
    public String getProfilPostCode() {
        return profil_post_code;
    }
    public String getProfilCity() {
        return profil_city;
    }

    public void setProfilName(String _val) {
        profil_name = _val;
    }
    public void setProfilFirstName(String _val) {
        profil_first_name = _val;
    }
    public void setProfilBirthDate(String _val) {
        profil_birth_date = _val;
    }
    public void setProfilBirthLocation(String _val) {
        profil_birth_location = _val;
    }
    public void setProfilAddress(String _val) {
        profil_address = _val;
    }
    public void setProfilPostCode(String _val) {
        profil_post_code = _val;
    }
    public void setProfilCity(String _val) {
        profil_city = _val;
    }

    public void saveProfil() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_NAME, profil_name);
        editor.putString(KEY_FIRST_NAME, profil_first_name);
        editor.putString(KEY_BIRTH_DATE, profil_birth_date);
        editor.putString(KEY_BIRTH_LOCATION, profil_birth_location);
        editor.putString(KEY_ADD, profil_address);
        editor.putString(KEY_POST_CODE, profil_post_code);
        editor.putString(KEY_CITY, profil_city);

        editor.apply();
    }

}
