package datamaskingtool.maskingStrategies;

import datamaskingtool.CustomClasses.CustomFloatList;
import datamaskingtool.CustomClasses.CustomIntegerList;
import datamaskingtool.CustomClasses.CustomStringList;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

import java.security.SecureRandom;

public class EncryptionStrategy extends MaskingStrategy {

    private static final String AES_ALGORITHM = "AES";
    private final SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();
    
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

    // Encrypt String using AES
    private String encryptString(String value) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes); // Convert to Base64 for easy storage
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting string", e);
        }
    }

    private int encryptInteger(int value) {
        return secureRandom.nextInt(1_000_000); // Non-reversible obfuscation
    }


    private float encryptFloat(float value) {
        float noise = secureRandom.nextFloat() * 10; // Add up to ±10 random noise
        return value + noise;
    }

    // Generate AES Secret Key
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

