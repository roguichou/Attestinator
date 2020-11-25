package com.roguichou.attestinator.attestation;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

import com.roguichou.attestinator.Constants;

@Entity (tableName = Constants.ATT_TABLE_NAME)
public class AttestationPermanente extends Attestation {
    public static final int ATTESTATION_TYPE_UNKNOWN = 0xFF;
    public static final int ATTESTATION_TYPE_HOME = 0x0;
    public static final int ATTESTATION_TYPE_WORK = 0x1;
    public static final int ATTESTATION_TYPE_ECOLE = 0x2;

    @ColumnInfo(name = "attestation_type")
    protected int attestationType;

    @ColumnInfo(name = "label")
    protected String label;

    public AttestationPermanente()
    {
        attestationType = ATTESTATION_TYPE_UNKNOWN;
        fileType = FILE_TYPE_NONE;
        label = null;
        filename = null;
    }

    public AttestationPermanente(int _attestationType, int _fileType, String _label)
    {
        attestationType = _attestationType;
        fileType = _fileType;
        label = _label;

        set_filename();

    }


    public String getLabel() {
        return label;
    }
    public int getAttestationType() {
        return attestationType;
    }

    public void setAttestationType(int attestationType) { this.attestationType = attestationType; }

    public void setLabel(String label) {
        this.label = label;
        set_filename();
    }

    @Override
    public void setFileType(int fileType) {
        this.fileType = fileType;
        set_filename();
    }

    private void set_filename()
    {
        if(label != null && fileType != FILE_TYPE_NONE)
        {
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
            filename = "att_" + label + extension;
        }
    }
}
