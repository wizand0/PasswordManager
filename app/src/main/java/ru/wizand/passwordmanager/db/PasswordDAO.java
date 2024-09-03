package ru.wizand.passwordmanager.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.wizand.passwordmanager.db.entity.Password;

@Dao
public interface PasswordDAO {

    @Insert
    public long addPassword(Password password);

    @Update
    public void updatePassword(Password password);

    @Delete
    public void deletePassword(Password password);

    @Query("select * from passwords")
    public List<Password> getPasswords();

    @Query("select * from passwords where password_id ==:passwordId")
    public Password getPassword(long passwordId);


@Query("SELECT * FROM passwords WHERE password_name LIKE '%' || :search || '%' " +
        "OR password_url LIKE '%' || :search || '%' " +
        "OR password_additional LIKE '%' || :search || '%'")
    public List<Password> getSearchedPasswords(String search);


}
