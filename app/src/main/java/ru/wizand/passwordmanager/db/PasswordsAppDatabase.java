package ru.wizand.passwordmanager.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import ru.wizand.passwordmanager.db.entity.Password;

@Database(entities = {Password.class},
        version = 1)
public abstract class PasswordsAppDatabase extends RoomDatabase {


    // Linking DAO and Database
    public abstract PasswordDAO getPasswordDAO();





}
