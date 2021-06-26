package com.kubsu.eszx;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hash {

    public static String get_hash(String str)  {

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte byteData[] = md.digest();
            //конвертируем байт в шестнадцатеричный формат вторым способом
            StringBuffer hexString = new StringBuffer();
            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
//        System.out.println("Текст в шестнадцатеричном виде : " + hexString.toString());
            return  hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return " ";
        }

        //конвертируем байт в шестнадцатеричный формат первым способом
//        StringBuffer sb = new StringBuffer();
//        for (byte aByteData : byteData) {
//            sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16).substring(1));
//        }
//        System.out.println("Текст в шестнадцатеричном виде : " + sb.toString());

    }
}