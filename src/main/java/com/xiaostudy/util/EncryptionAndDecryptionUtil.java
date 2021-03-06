package com.xiaostudy.util;

import javax.crypto.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @desc 文件加解密
 *
 * @author liwei
 * @Date 2019-3-27
 */
public final class EncryptionAndDecryptionUtil {

    private static final String DES = "DES";

    private EncryptionAndDecryptionUtil(){}

    /**
     * 简单的文件加密，用位运算
     *
     * @param encryptionFile
     * @param fileName
     * @param password
     * @return
     */
    public static Boolean simple(File encryptionFile, File fileName, String password) {
        if (encryptionFile == null || fileName == null || password == null || password.trim().length() <= 0) {
            return false;
        }

        if (encryptionFile.exists() == false) {
            return false;
        }

        if (fileName.exists()) {
            String strEncryptionFile = encryptionFile.getPath() + File.separator + encryptionFile.getName();
            String strFileName = fileName.getPath() + File.separator + fileName.getName();
            return simple(strEncryptionFile, strFileName, password);
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        boolean b = true;
        try {
            fis = new FileInputStream(encryptionFile);
            fos = new FileOutputStream(fileName);
            int ch;

            while ((ch = fis.read()) != -1) {
                ch = ch ^ Integer.valueOf(password);
                fos.write(ch);
            }
        } catch (IOException e) {
            b = false;
            e.printStackTrace();
        } finally {
            FileUtil.closeFile(fis, fos, null, null);
            return b;
        }
    }

    public static Boolean simple(String strEncryptionFile, String strFileName, String password) {
        if (strEncryptionFile == null || strEncryptionFile.trim().length() <= 0 ||
                strFileName == null || strFileName.trim().length() <= 0 ||
                password == null || password.trim().length() <= 0) {
            return false;
        }

        File encryptionFile = new File(strEncryptionFile);
        if (encryptionFile.exists() == false || encryptionFile.isDirectory()) {
            return false;
        }

        File fileName = new File(strFileName);
        if (fileName.exists()) {
            String name = fileName.getName();
            if (name.contains(".")) {
                String[] split = name.split(".");
                if (split.length > 1) {
                    name = split[0] + "1." + split[1];
                } else if (split.length == 1) {
                    name = "1." + split[0];
                } else {
                    return false;
                }
            } else {
                name = name + "1";
            }
            strFileName = fileName.getPath() + File.separator + name;
            return simple(strEncryptionFile, strFileName, password);
        }

        return simple(encryptionFile, fileName, password);
    }

    /**
     * DES算法加密
     *
     * @param encryptionFile
     * @param fileName
     * @param password
     * @return
     */
    public static Boolean des(File encryptionFile, File fileName, String password) {
        if (encryptionFile == null || fileName == null || password == null || password.trim().length() <= 0) {
            return false;
        }

        if (encryptionFile.exists() == false) {
            return false;
        }

        if (fileName.exists()) {
            String strEncryptionFile = encryptionFile.getPath() + File.separator + encryptionFile.getName();
            String strFileName = fileName.getPath() + File.separator + fileName.getName();
            return encrypt(strEncryptionFile, strFileName, password);
        }

        InputStream is = null;
        OutputStream out = null;
        CipherInputStream cis = null;
        boolean b = true;
        try {
            Cipher cipher = Cipher.getInstance(DES);
            KeyGenerator _generator = KeyGenerator.getInstance(DES);
            _generator.init(new SecureRandom(password.getBytes()));
            Key key = _generator.generateKey();
            cipher.init(Cipher.ENCRYPT_MODE, key);
            is = new FileInputStream(encryptionFile);
            out = new FileOutputStream(fileName);
            cis = new CipherInputStream(is, cipher);
            byte[] buffer = new byte[1024*8];
            int r;
            while ((r = cis.read(buffer)) > 0) {
                out.write(buffer, 0, r);
            }
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        } finally {
            FileUtil.closeFile(cis, out, null, null);
            FileUtil.closeFile(is, null, null, null);
            return b;
        }
    }

    public static Boolean encrypt(String strEncryptionFile, String strFileName, String password) {
        if (strEncryptionFile == null || strEncryptionFile.trim().length() <= 0 ||
                strFileName == null || strFileName.trim().length() <= 0 ||
                password == null || password.trim().length() <= 0) {
            return false;
        }

        File encryptionFile = new File(strEncryptionFile);
        if (encryptionFile.exists() == false || encryptionFile.isDirectory()) {
            return false;
        }

        File fileName = new File(strFileName);
        if (fileName.exists()) {//如果加密后的文件存在，则文件名加1
            String name = fileName.getName();
            if (name.contains(".")) {
                String[] split = name.split(".");
                if (split.length > 1) {
                    name = split[0] + "1." + split[1];
                } else if (split.length == 1) {
                    name = "1." + split[0];
                } else {
                    return false;
                }
            } else {
                name = name + "1";
            }
            strFileName = fileName.getPath() + File.separator + name;
            return encrypt(strEncryptionFile, strFileName, password);
        }

        return des(encryptionFile, fileName, password);
    }

    /**
     * DES算法解密
     * @param decryptFile
     * @param fileName
     * @param password
     */
    public static Boolean decrypt(File decryptFile, File fileName, String password){
        if(decryptFile == null || fileName == null || password == null || password.trim().length() <= 0) {
            return false;
        }

        if(decryptFile.exists() == false) {
            return false;
        }

        if (fileName.exists()) {
            String strDecryptFile = decryptFile.getPath() + File.separator + decryptFile.getName();
            String strFileName = fileName.getPath() + File.separator + fileName.getName();
            return decrypt(strDecryptFile, strFileName, password);
        }

        InputStream is = null;
        OutputStream out = null;
        CipherOutputStream cos = null;
        boolean b = true;
        try {
            Cipher cipher = Cipher.getInstance(DES);
            KeyGenerator _generator = KeyGenerator.getInstance(DES);
            _generator.init(new SecureRandom(password.getBytes()));
            Key key = _generator.generateKey();
            cipher.init(Cipher.DECRYPT_MODE, key);
            is = new FileInputStream(decryptFile);
            out = new FileOutputStream(fileName);
            cos = new CipherOutputStream(out, cipher);
            byte[] buffer = new byte[1024*8];
            int r;
            while ((r = is.read(buffer)) >= 0) {
                System.out.println();
                cos.write(buffer, 0, r);
            }
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        } finally {
            FileUtil.closeFile(null, out, null, null);
            FileUtil.closeFile(null, cos, null, null);
            FileUtil.closeFile(is, null, null, null);
            return b;
        }
    }

    public static Boolean decrypt(String strDecryptFile, String strFileName, String password){
        if(strDecryptFile == null || strDecryptFile.trim().length() <= 0 ||
                strFileName == null || strFileName.trim().length() <= 0 ||
                password == null || password.trim().length() <= 0) {
            return false;
        }

        File decryptFile = new File(strDecryptFile);
        if(decryptFile.exists() == false) {
            return false;
        }

        File fileName = new File(strFileName);
        if (fileName.exists()) {//如果加密后的文件存在，则文件名加1
            String name = fileName.getName();
            if (name.contains(".")) {
                String[] split = name.split(".");
                if (split.length > 1) {
                    name = split[0] + "1." + split[1];
                } else if (split.length == 1) {
                    name = "1." + split[0];
                } else {
                    return false;
                }
            } else {
                name = name + "1";
            }
            strFileName = fileName.getPath() + File.separator + name;
            return decrypt(strDecryptFile, strFileName, password);
        }

        return decrypt(decryptFile, fileName, password);
    }

}
