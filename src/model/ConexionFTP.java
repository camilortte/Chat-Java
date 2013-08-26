/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

/**
 *
 * @author camilortte
 */
public class ConexionFTP {

    private FTPClient client;
    private String host;
    private String user;
    private String password;
    private File file;
    private String nombre_Archivo;
    private static String directorio = "/pub";

    public ConexionFTP(String user, String password,String host) throws SocketException, IOException {
        this.user = user;
        this.password = password;
        this.host=host;
    }

    public ConexionFTP() throws SocketException, IOException {
        this.user = "anonymous";
        this.password = "";
        host = "127.0.0.1";
    }

    public boolean upFile(File file) throws SocketException, IOException {
        this.file = file;
        this.nombre_Archivo = nombre_Archivo;
        boolean hecho = false;
        try {
            this.client = new FTPClient();
            //this.client.connect(InetAddress.getByName(host));
            this.client.connect(host);            
            this.client.login(user, password);
            this.client.enterLocalPassiveMode();
            System.out.println(this.client.changeWorkingDirectory(directorio));
            this.client.setFileType(FTP.ASCII_FILE_TYPE);
            InputStream inputStream = new FileInputStream(file);
            nombre_Archivo=file.getName();
            hecho = this.client.storeFile(nombre_Archivo, inputStream);
            System.out.println(this.client.storeFile(nombre_Archivo, inputStream));
            if (hecho) {
                System.out.println("Archivo Subido con exito");
            }
            this.client.logout();
            this.client.disconnect();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ConexionFTP.class.getName()).log(Level.SEVERE, null, ex);
            hecho = false;
            this.client.logout();
            this.client.disconnect();
        }
        return hecho;
    }

    public boolean upFile(String file, String nombre_Archivo) throws SocketException, IOException {
        this.file = new File(file);
        this.nombre_Archivo = nombre_Archivo;
        boolean hecho = false;
        try {
            this.client = new FTPClient();
            this.client.connect(InetAddress.getByName(host));
            this.client.login(user, password);            
            //this.client.enterLocalPassiveMode();
            System.out.println(this.client.changeWorkingDirectory(directorio));
            this.client.setFileType(FTP.ASCII_FILE_TYPE);
            InputStream inputStream = new FileInputStream(file);
            hecho = this.client.storeFile(nombre_Archivo, inputStream);
            System.out.println(this.client.storeFile(nombre_Archivo, inputStream));
            if (hecho) {
                System.out.println("Archivo Subido con exito");
            }
            this.client.logout();
            this.client.disconnect();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ConexionFTP.class.getName()).log(Level.SEVERE, null, ex);
            hecho = false;
        }
        return hecho;
    }

    public boolean downFile(String remoteFile) {
        boolean bien = false;
        client = new FTPClient();
        OutputStream outStream;
        try {
            this.client.connect(InetAddress.getByName(host));
            client.login(user, password);
            client.enterLocalPassiveMode();
            outStream = new FileOutputStream("a.txt");
            client.retrieveFile(remoteFile, outStream);
            bien = true;
        } catch (IOException ioe) {
            System.out.println("Error communicating with FTP server.");
            bien = false;
        }
        try {
            client.logout();
            client.disconnect();
        } catch (IOException ex) {
            Logger.getLogger(ConexionFTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bien;
    }

    public ArrayList<String> listFIles(String dir) {
        ArrayList<String> filesArray=new ArrayList<String>();
        try {
            client = new FTPClient();
            this.client.connect(InetAddress.getByName(host));
            client.login(user, password);
            client.enterLocalPassiveMode();
            FTPFile[] files = client.listFiles(this.directorio);            
            filesArray.clear();
            for (FTPFile file : files) {
                System.out.println(file.getName());
                filesArray.add(file.getName());
            }
        } catch (SocketException ex) {
            Logger.getLogger(ConexionFTP.class.getName()).log(Level.SEVERE, null, ex);
            filesArray=null;
        } catch (IOException ex) {
            Logger.getLogger(ConexionFTP.class.getName()).log(Level.SEVERE, null, ex);
            filesArray=null;
        }
        return filesArray;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getNombre_Archivo() {
        return nombre_Archivo;
    }

    public void setNombre_Archivo(String nombre_Archivo) {
        this.nombre_Archivo = nombre_Archivo;
    }

    public static String getDirectorio() {
        return directorio;
    }

    public static void setDirectorio(String directorio) {
        ConexionFTP.directorio = directorio;
    }
}
