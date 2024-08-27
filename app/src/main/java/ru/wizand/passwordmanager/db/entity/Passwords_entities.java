package ru.wizand.passwordmanager.db.entity;

public class Passwords_entities {

    public static final String TABLE_NAME = "passwords_entities";
    public static final String COLUMN_ID = "passwords_entities_id";
    public static final String COLUMN_NAME = "password_name";

    private int id;
    private String name;

    public Passwords_entities(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
