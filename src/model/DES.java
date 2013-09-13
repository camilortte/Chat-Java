package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


/* http://collectioncode.com/java/des-base64-algoritmos-en-java/ */
public class DES {

    Cipher encrypt;
    Cipher decrypt;
    SecretKey key;
    String keyTxt;

    public DES(String clave) {
        keyTxt = clave;
        try {
            encrypt = Cipher.getInstance("DES");
            decrypt = Cipher.getInstance("DES");

            if (keyTxt.equals("")) {
                SecretKey key = KeyGenerator.getInstance("DES").generateKey();//Genera Clave automàtica
                encrypt.init(Cipher.ENCRYPT_MODE, key);//Con clave aleatoria
                decrypt.init(Cipher.DECRYPT_MODE, key);//Con clave aleatoria
            } else {
                KeySpec ks = new DESKeySpec(keyTxt.getBytes("UTF8"));
                SecretKeyFactory kf = SecretKeyFactory.getInstance("DES");
                SecretKey ky = kf.generateSecret(ks);
                encrypt.init(Cipher.ENCRYPT_MODE, ky);
                decrypt.init(Cipher.DECRYPT_MODE, ky);
            }
        } catch (InvalidKeySpecException ex) {
        } catch (UnsupportedEncodingException ex) {
        } catch (InvalidKeyException ex) {
        } catch (NoSuchAlgorithmException ex) {
        } catch (NoSuchPaddingException ex) {
        }
    }

    /*encripta un archivo y lo returna*/
    /*public File encriptar(File archivo){
        Archivo arch= new Archivo();
        File f = new File(archivo.getName());
        String datos = encriptar(arch.leerArchivo(archivo));
        System.out.println("Archivo encriptado:"+datos);
        //arch.escribirArchivo(archivo.getName(),datos);
        f=arch.escribirArchivo(archivo, datos);
        return f;
    }
    */
    
    public File encriptar(File archivo){
        Archivo arch= new Archivo();
        File fichero=null;
        String datos = this.encriptar(arch.leerArchivo(archivo));
        fichero = arch.escribirArchivo(archivo,datos);
        return fichero;
    }
    
    public File desencriptar(File archivoCifrado){
        Archivo arch = new Archivo();
        File fichero=null;
        String datosLeidos = arch.leerArchivo(archivoCifrado);
        System.out.println("DATOS LEIDOS DE ARCHIVOCIFRADO: "+datosLeidos);
        String datosDecifrados = this.desencriptar(datosLeidos);
        System.out.println("DATOS desCIFRADOS DE DATOSleIDOS :"+datosDecifrados);
        //archivoCifrado = arch.escribirArchivo(archivoCifrado, datosDecifrados);
        fichero = arch.escribirArchivo(archivoCifrado, datosDecifrados);
        return fichero;
    }
    
    
    public String encriptar(String str) {
        try {
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            byte[] enc = encrypt.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (java.io.IOException e) {
        }
        return null;
    }

    public String desencriptar(String str) {
        try {
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            // Decrypt
            byte[] utf8 = decrypt.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (java.io.IOException e) {
        }
        return null;
    }
}

