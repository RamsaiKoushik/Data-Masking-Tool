package datamaskingtool.maskingStrategies;

import datamaskingtool.CustomClasses.CustomBooleanList;
import datamaskingtool.CustomClasses.CustomDateList;
import datamaskingtool.CustomClasses.CustomFloatList;
import datamaskingtool.CustomClasses.CustomIntegerList;
import datamaskingtool.CustomClasses.CustomStringList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.sql.Date;
import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;

public class EncryptionStrategy extends MaskingStrategy {

    private static final String AES_ALGORITHM = "AES";
    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();
    private final Integer MOD = 100000000;
    
    public EncryptionStrategy() {
        this.secretKey = generateKey(); // Generate a secure key
    }

    @Override
    public CustomStringList mask(CustomStringList values) {
        if (values == null || values.isEmpty()) {
            return values;
        }
        CustomStringList encryptedList = new CustomStringList();
        for (String value : values) {
            encryptedList.add(encryptString(value));
        }
        return encryptedList;
    }

    @Override
    public CustomIntegerList mask(CustomIntegerList values) {
        if (values == null || values.isEmpty()) {
            return values;
        }
        CustomIntegerList encryptedList = new CustomIntegerList();
        for (Integer value : values) {
            encryptedList.add(encryptInteger(value));
        }
        return encryptedList;
    }

    @Override
    public CustomFloatList mask(CustomFloatList values) {
        if (values == null || values.isEmpty()) {
            return values;
        }
        CustomFloatList encryptedList = new CustomFloatList();
        for (Float value : values) {
            encryptedList.add(encryptFloat(value));
        }
        return encryptedList;
    }

    @Override
    public CustomDateList mask(CustomDateList values){
        if (values == null || values.isEmpty()) {
            return values;
        }
        CustomDateList encryptedList = new CustomDateList();

        for (Date value: values){
            encryptedList.add(encryptDate(value));
        }

        return encryptedList;
    }

    @Override
    public CustomBooleanList mask(CustomBooleanList  values){
        if (values == null || values.isEmpty()) {
            return values;
        }

        CustomBooleanList  encryptedList = new CustomBooleanList();

        for (Boolean value: values){
            encryptedList.add(false);
        }

        return encryptedList;
    }

    private String encryptString(String value) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes); 
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting string", e);
        }
    }

    private int encryptInteger(int value) {
        return (value%MOD*secureRandom.nextInt(1_000_000)%MOD)%MOD; 
    }

    private Date encryptDate(Date date){
        return new Date(0); 
    }

    private float encryptFloat(float value) {
        float noise = secureRandom.nextFloat() * 100; 
        return value + noise;
    }

   
    private SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            keyGen.init(256); // Use a 256-bit key
            return keyGen.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }

}

