package com.roguichou.attestinator;

import android.content.SharedPreferences;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = Constants.PROFIL_TABLE_NAME)
public class Profil
{
    //Préférences
    @Ignore
    private SharedPreferences settings;
    //clé de Préférences
    private static final String KEY_NAME = "nom";
    private static final String KEY_FIRST_NAME = "prenom";
    private static final String KEY_BIRTH_DATE = "date_naiss";
    private static final String KEY_BIRTH_LOCATION = "lieu_naiss";
    private static final String KEY_ADD = "addresse";
    private static final String KEY_POST_CODE = "code_postal";
    private static final String KEY_CITY = "ville";

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "label")
    private String label = null;
    @ColumnInfo(name = "name")
    private String name = null;
    @ColumnInfo(name = "first_name")
    private String firstName = null;
    @ColumnInfo(name = "birth_date")
    private String birthDate = null;
    @ColumnInfo(name = "birth_location")
    private String birthLocation = null;
    @ColumnInfo(name = "address")
    private String address = null;
    @ColumnInfo(name = "post_code")
    private String postCode = null;
    @ColumnInfo(name = "city")
    private String city = null;


    public boolean isValid()
    {
        return (null!=label &&
                null!=name &&
                null!=firstName &&
                null!=birthDate &&
                null!=birthLocation &&
                null!=address &&
                null!=postCode &&
                null!=city );

    }

    public String getLabel() { return label; }
    public String getName() {
        return name;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getBirthDate() {
        return birthDate;
    }
    public String getBirthLocation() {
        return birthLocation;
    }
    public String getAddress() {
        return address;
    }
    public String getPostCode() {
        return postCode;
    }
    public String getCity() {
        return city;
    }

    public void setLabel(String _val) { label = _val; }
    public void setName(String _val) { name = _val; }
    public void setFirstName(String _val) {
        firstName = _val;
    }
    public void setBirthDate(String _val) {
        birthDate = _val;
    }
    public void setBirthLocation(String _val) {
        birthLocation = _val;
    }
    public void setAddress(String _val) {
        address = _val;
    }
    public void setPostCode(String _val) {
        postCode = _val;
    }
    public void setCity(String _val) {
        city = _val;
    }

    @Override
    public String toString()
    {
        return label;
    }
}
