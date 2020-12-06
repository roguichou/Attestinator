package com.roguichou.attestinator.attestation;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public abstract class Attestation implements Serializable
{
    public static final int FILE_TYPE_PDF = 0x0;
    public static final int FILE_TYPE_JPG = 0x1;
    public static final int FILE_TYPE_NONE = 0xFF;

    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "file_type")
    protected int fileType;

    protected String filename;


    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) { this.fileType = fileType; }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {this.filename = filename;}
}
