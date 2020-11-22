package com.roguichou.attestinator.attestation;


public class AttestationPermanente extends Attestation {
    public static final int ATTESTATION_TYPE_UNKNOWN = 0xFF;
    public static final int ATTESTATION_TYPE_HOME = 0x0;
    public static final int ATTESTATION_TYPE_WORK = 0x1;
    public static final int ATTESTATION_TYPE_ECOLE = 0x2;

    protected final int attestationType;


    protected final String label;


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


    public String getLabel() {
        return label;
    }
    public int getAttestationType() {
        return attestationType;
    }

}
