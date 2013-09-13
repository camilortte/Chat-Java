/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import view.archivo;

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
    private DES DESCifrar;

    public ConexionFTP(String user, String password,String host) throws SocketException, IOException {
        this.user = user;
        this.password = password;
        this.host=host;
        DESCifrar=new DES("4d89g13j4j91j27c582ji69373y788r6");
    }

    public ConexionFTP() throws SocketException, IOException {
        this.user = "anonymous";
        this.password = "";
        host = "127.0.0.1";
        DESCifrar=new DES("4d89g13j4j91j27c582ji69373y788r6");
    }

    public boolean upFile(File file) throws SocketException, IOException {
        this.file = file;
        this.nombre_Archivo = nombre_Archivo;
        boolean hecho = false;
        File archivo_encriptado=crearArchivoEncriptado(file);
        System.out.println("ARCHIVO ENCRIPTADO: "+archivo_encriptado.getName());
        try {
            this.client = new FTPClient();
            //this.client.connect(InetAddress.getByName(host));
            this.client.connect(host);            
            this.client.login(user, password);
            this.client.enterLocalPassiveMode();
            System.out.println(this.client.changeWorkingDirectory(directorio));
            this.client.setFileType(FTP.ASCII_FILE_TYPE);
            InputStream inputStream = new FileInputStream(DESCifrar.encriptar(archivo_encriptado));
            nombre_Archivo=file.getName();
            hecho = this.client.storeFile(nombre_Archivo, inputStream);
            System.out.println(this.client.storeFile(nombre_Archivo, inputStream));
            if (hecho) {
                System.out.println("Archivo Subido con exito");
            }
            this.client.logout();
            this.client.disconnect();
            archivo_encriptado.delete();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ConexionFTP.class.getName()).log(Level.SEVERE, null, ex);
            hecho = false;
            this.client.logout();
            this.client.disconnect();
        }
        return hecho;
    }

    public File crearArchivoEncriptado(File archivo_a_encriptar){
        File archivo_encriptado=null;
        FileWriter fichero = null;
        PrintWriter pw = null;
        try
        {
            archivo_encriptado=new File("encript.txt");
            fichero = new FileWriter(archivo_encriptado);
            pw = new PrintWriter(fichero);
            FileReader fr = null;
            BufferedReader br = null;

            try {
               // Apertura del fichero y creacion de BufferedReader para poder
               // hacer una lectura comoda (disponer del metodo readLine()).               
               fr = new FileReader (archivo_a_encriptar);
               br = new BufferedReader(fr);

               // Lectura del fichero
               String linea;
               while((linea=br.readLine())!=null)
                  pw.println(linea);
            }
            catch(Exception e){
               e.printStackTrace();
            }finally{
                if(fr!=null)
                    fr.close();
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
          
        }
        return archivo_encriptado;
    }
    
    public boolean upFile(String file, String nombre_Archivo) throws SocketException, IOException {
        this.file = new File(file);
        File archivo_encriptado=crearArchivoEncriptado(this.file);
        this.nombre_Archivo = nombre_Archivo;
        boolean hecho = false;
        try {
            this.client = new FTPClient();
            this.client.connect(InetAddress.getByName(host));
            this.client.login(user, password);            
            //this.client.enterLocalPassiveMode();
            System.out.println(this.client.changeWorkingDirectory(directorio));
            this.client.setFileType(FTP.ASCII_FILE_TYPE);
            InputStream inputStream = new FileInputStream(archivo_encriptado);
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

   /* public boolean downFile(String remoteFile, String nombre) {
        boolean bien = false;
        client = new FTPClient();
        OutputStream outStream;
        try {
            this.client.connect(InetAddress.getByName(host));
            client.login(user, password);
            client.enterLocalPassiveMode();
            boolean changeWorkingDirectory = client.changeWorkingDirectory(directorio);
            outStream = new FileOutputStream(nombre);
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
    }*/
    
    public boolean downFile(String remoteFile,File archivo) {
        boolean bien = false;
        client = new FTPClient();
        OutputStream outStream = null;
        System.out.println("archiiiivo "+archivo.getAbsolutePath());
        try {
            this.client.connect(InetAddress.getByName(host));
            client.login(user, password);
            client.enterLocalPassiveMode();
            boolean changeWorkingDirectory = client.changeWorkingDirectory(directorio);
            outStream = new FileOutputStream(archivo);            
            client.retrieveFile(remoteFile, outStream);
            archivo = DESCifrar.desencriptar(archivo);
            //archivo=DESCifrar.desencriptar(f);
            //archivo = DESCifrar.desencriptar(archivo);
            bien = true;
        } catch (IOException ioe) {
            System.out.println("Error communicating with FTP server.");
            bien = false;
        }
        try {
            outStream.close();
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
