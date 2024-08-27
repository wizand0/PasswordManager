package ru.wizand.passwordmanager.db.entity;

public class Password {


    public static final String TABLE_NAME = "password";
    public static final String COLUMN_ID = "password_id";
    public static final String COLUMN_URL = "password_url";
    public static final String COLUMN_PASSWORD = "password_password";
    public static final String COLUMN_ADDITIONAL = "password_additional";


    private int pass_id;
    private String url;
    private String password;
    private String additional;

    public Password(int pass_id, String url, String password, String additional) {
        this.pass_id = pass_id;
        this.url = url;
        this.password = password;
        this.additional = additional;
    }

    public int getPass_id() {
        return pass_id;
    }

    public void setPass_id(int pass_id) {
        this.pass_id = pass_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
