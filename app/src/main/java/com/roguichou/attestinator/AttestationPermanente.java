package com.roguichou.attestinator;

public class AttestationPermanente {
    public static final int ATTESTATION_TYPE_UNKNOWN = 0xFF;
    public static final int ATTESTATION_TYPE_HOME = 0x0;
    public static final int ATTESTATION_TYPE_WORK = 0x1;
    public static final int ATTESTATION_TYPE_ECOLE = 0x2;

    public static final int FILE_TYPE_PDF = 0x0;
    public static final int FILE_TYPE_JPG = 0x1;

    private final int attestationType;
    private final int fileType;
    private final String label;
    private final String filename;


    public AttestationPermanente(int _attestationType, int _fileType, String _label)
    {
        attestationType = _attestationType;
        fileType = _fileType;
        label = _label;

        String extension = null;
        switch (fileType)
        {
            case FILE_TYPE_PDF:
                extension=".pdf";
                break;

            case FILE_TYPE_JPG:
                extension=".jpg";
                break;
        }
        filename ="att_" + _label + extension;
    }


    public int getAttestationType() {
        return attestationType;
    }

    public int getFileType() {
        return fileType;
    }

    public String getLabel() {
        return label;
    }

    public String getFilename() {
        return filename;
    }
}
