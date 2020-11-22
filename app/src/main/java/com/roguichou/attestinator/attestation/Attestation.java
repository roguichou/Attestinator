package com.roguichou.attestinator.attestation;

import java.io.Serializable;

public abstract class Attestation implements Serializable
{
    public static final int FILE_TYPE_PDF = 0x0;
    public static final int FILE_TYPE_JPG = 0x1;
    public static final int FILE_TYPE_NONE = 0xFF;

    protected int fileType;
    protected String filename;


    public int getFileType() {
        return fileType;
    }

    public String getFilename() {
        return filename;
    }

}
