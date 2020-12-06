package com.roguichou.attestinator;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = Constants.PROFIL_TABLE_NAME)
public class Profil
{
    @PrimaryKey(autoGenerate = true)
    public int profil_uid;

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
