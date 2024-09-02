package ru.wizand.passwordmanager;

import static ru.wizand.passwordmanager.AesCbcWithIntegrity.generateKeyFromPassword;
import static ru.wizand.passwordmanager.AesCbcWithIntegrity.generateSalt;
import static ru.wizand.passwordmanager.AesCbcWithIntegrity.saltString;

import java.security.GeneralSecurityException;

class EncryptionLibrary {

    public static String encryptingKey(String inputString, String salt){


        //Generating salt
//         String salt = null;
//        String salt = "4ooMz5/R/xl8df9Iife5GWmBuYaqBa54ESgTnZUOpkgNWKQ82i8OEMqoK/UwfGx8+DaRgCjidmqHYcCeL2OG3SqWjDAqukJRWCAAiBZGUGH6FdB4VqzTrg2Gp9Tbu/rbgt4tUflbPv9qQZ4C4aYs0hZBIKIguhuHqXybl0+ZvzQ=";
//        try {
//            salt = saltString(generateSalt());
//            Log.i("testing_input_salt", salt);
//        } catch (GeneralSecurityException e) {
//            throw new RuntimeException(e);
//        }
        //Generating Master key
        String key = null;

        try {

            key = String.valueOf(generateKeyFromPassword(String.valueOf(inputString), salt));

        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        return key;
    }

    public static String generatingSalt() throws GeneralSecurityException {
        String salt = "";
        salt = (String)saltString(generateSalt());

        return salt;
    }
}
