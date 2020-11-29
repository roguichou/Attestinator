package com.roguichou.attestinator.db;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.roguichou.attestinator.Constants;
import com.roguichou.attestinator.Profil;
import com.roguichou.attestinator.attestation.AttestationPermanente;


@Database(entities = { AttestationPermanente.class, Profil.class}, version = 3)
public abstract class AttestinatorDatabase extends RoomDatabase {

    public abstract AttestinatorDao getAttestinatorDao();

    private static AttestinatorDatabase attestinatorDB;

    public static AttestinatorDatabase getInstance(Context context) {
        if (null == attestinatorDB) {
            attestinatorDB = buildDatabaseInstance(context);
        }
        return attestinatorDB;
    }



    private static AttestinatorDatabase buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                AttestinatorDatabase.class,
                Constants.ATT_DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }

    public void cleanUp(){
        attestinatorDB = null;
    }

}