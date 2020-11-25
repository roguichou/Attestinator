package com.roguichou.attestinator.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.roguichou.attestinator.Constants;
import com.roguichou.attestinator.attestation.AttestationPermanente;


@Database(entities = { AttestationPermanente.class }, version = 1)
public abstract class AttestationsDatabase extends RoomDatabase {

    public abstract AttestationPermanenteDao getAttestationPermanenteDao();

    private static AttestationsDatabase attestationsDB;

    public static AttestationsDatabase getInstance(Context context) {
        if (null == attestationsDB) {
            attestationsDB = buildDatabaseInstance(context);
        }
        return attestationsDB;
    }

    private static AttestationsDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                AttestationsDatabase.class,
                Constants.ATT_DB_NAME)
                .allowMainThreadQueries().build();
    }

    public void cleanUp(){
        attestationsDB = null;
    }

}