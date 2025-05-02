package datamaskingtool.maskingStrategies;

import datamaskingtool.CustomClasses.*;
import com.privacylogistics.FF3Cipher;
import java.util.Date;
import java.security.SecureRandom;

public class EncryptionStrategy extends MaskingStrategy {

    private final SecureRandom secureRandom = new SecureRandom();
    private final Integer MOD = 10000;

    // FPE parameters
//    private final FPEString fpeEncryptor;
    private final String fpeKey = "00112233445566778899aabbccddeeff00112233445566778899aabbccddeeff"; // 16-character key for AES-128 (adjust as needed)
    private final String tweak = "abcdef1234567890";      // Tweak, should be consistent and secure
    private FF3Cipher c6;

    public EncryptionStrategy() {
        this.c6 = new FF3Cipher(fpeKey, tweak, generateFullASCIICharset());
    }

    private String generateFullASCIICharset() {
        StringBuilder sb = new StringBuilder();

        // Lowercase letters (a-z)
        for (int i = 97; i <= 122; i++) {
            sb.append((char) i);
        }

        // Uppercase letters (A-Z)
        for (int i = 65; i <= 90; i++) {
            sb.append((char) i);
        }

        // Digits (0-9)
        for (int i = 48; i <= 57; i++) {
            sb.append((char) i);
        }

        // Symbols (ASCII 32-47, 58-64, 91-96, 123-126)
        for (int i = 32; i <= 47; i++) {
            sb.append((char) i);
        }
        for (int i = 58; i <= 64; i++) {
            sb.append((char) i);
        }
        for (int i = 91; i <= 96; i++) {
            sb.append((char) i);
        }
        for (int i = 123; i <= 126; i++) {
            sb.append((char) i);
        }

        return sb.toString();
    }


    @Override
    public CustomStringList mask(CustomStringList values) {
        if (values == null || values.isEmpty()) {
            return values;
        }

        CustomStringList encryptedList = new CustomStringList();
        for (String value : values) {
            encryptedList.add(fpeEncrypt(value));
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
    public CustomDateList mask(CustomDateList values) {
        if (values == null || values.isEmpty()) {
            return values;
        }

        CustomDateList encryptedList = new CustomDateList();
        for (Date value : values) {
            encryptedList.add(encryptDate(value));
        }

        return encryptedList;
    }

    @Override
    public CustomBooleanList mask(CustomBooleanList values) {
        if (values == null || values.isEmpty()) {
            return values;
        }

        CustomBooleanList encryptedList = new CustomBooleanList();
        for (Boolean value : values) {
            encryptedList.add(false);
        }

        return encryptedList;
    }

    private String fpeEncrypt(String value) {
        try {
            if (value.length() < 4) {
                value = padValue(value);
            }
            return this.c6.encrypt(value);
        } catch (Exception e) {
            throw new RuntimeException("FPE encryption failed for: " + value, e);
        }
    }

    private String padValue(String value) {
        // Simple padding by appending 'X' till length 3
        StringBuilder sb = new StringBuilder(value);
        while (sb.length() < 4) {
            sb.append('X');
        }
        return sb.toString();
    }

    private int encryptInteger(int value) {
        int newValue = (value % MOD * secureRandom.nextInt(1_000) % MOD) % MOD;
        return newValue;
    }

    private Date encryptDate(Date date) {
        return new Date(0);
    }

    private float encryptFloat(float value) {
        float noise = secureRandom.nextFloat() * 100;
        return value + noise;
    }


}
