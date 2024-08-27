package ru.wizand.passwordmanager.db.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "passwords")
public class Password {

    // 2 - Variables
    @ColumnInfo(name = "password_id")
    @PrimaryKey(autoGenerate = true)
    private int pass_id;

    @ColumnInfo(name = "password_name")
    private String name;

    @ColumnInfo(name = "password_url")
    private String url;

    @ColumnInfo(name = "password_login")
    private String login;

    @ColumnInfo(name = "password_password")
    private String password;

    @ColumnInfo(name = "password_additional")
    private String additional;

    // 3 - Constructor
    @Ignore
    public Password() {

    }

    public Password(int pass_id, String name, String url, String login, String password, String additional) {
        this.pass_id = pass_id;
        this.name = name;
        this.url = url;
        this.login = login;
        this.password = password;
        this.additional = additional;
    }

    // 4 - Getters and Setters
    public int getPass_id() {
        return pass_id;
    }

    public void setPass_id(int pass_id) {
        this.pass_id = pass_id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }


}
