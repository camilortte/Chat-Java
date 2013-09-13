/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import view.VentanaCliente;

/**
 *
 * @author camilortte
 */
public class Cliente {

    private ObjectInputStream entrada;
    private ObjectOutputStream salida;
    private Socket conexion;
    private Socket conexionUsers;
    private ObjectInputStream entradaUsuarios;
    private int puerto;
    private String host, nickname;
    private boolean conectado;
    private VentanaCliente ventana;
    public ArrayList<String> usuarios;
    String clave="4d89g13j4j91j27c582ji69373y788r6";

    public Cliente(int puerto, String host, String nickname, VentanaCliente ventana) {
        this.host = host;
        this.puerto = puerto;
        this.nickname = nickname;
        conectado=false;
        this.ventana=ventana;
        this.usuarios=new ArrayList<String>();
        this.usuarios.clear();
    }

    public void initClient() {
        try {
            conexion = new Socket(host, puerto);
            conexionUsers=new Socket(host,8000); 
            salida = new ObjectOutputStream(conexion.getOutputStream());            
            salida.writeObject(nickname);
            salida.flush();
            entrada = new ObjectInputStream(conexion.getInputStream());  
            new Thread() {
                public void run() {
                    conectado=true;
                    flujoEntrada();
                }
            ;
        } .start();  
            new Thread() {
                public void run() {
                    flujoUsuarios();
                }
            ;
            } .start(); 
            
        } catch (UnknownHostException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public boolean isConectado() {
        return conectado;
    }

    public void setConectado(boolean conectado) {
        this.conectado = conectado;
    }
    
    
    
    public void flujoEntrada(){
        String entra="";
        String nickname_input="";
        Integer tipo;
        DES objetoDES = new DES(this.clave);
        
        try {
            while(conectado){
                //Leemos el Nickname
                //entra=(String) entrada.readObject();
                //this.ventana.setPanelText(entra, Color.cyan);
                //leemos el tipo de mensaje
                /*                 
                 * 1 = mensaje
                 * 2 = Conexion
                 * 3 = desconexion
                 */
                tipo=Integer.parseInt(entrada.readUTF());
                //Leemos el nickname
                nickname_input=(String) entrada.readUTF();
                System.out.println("El nickname obtenido es: "+nickname_input);
                //Leemos el mensaje                
                //entra=(String) entrada.readUTF();
                entra=objetoDES.desencriptar((String) entrada.readUTF());
                System.out.println(entra);
                System.out.println("El mensaje obtenido es: "+entra);
                switch (tipo){
                    case 1:
                        this.ventana.setPanelText(nickname_input, Color.magenta);
                        this.ventana.setPanelText(entra+"\n", Color.black);
                        break;
                    case 2:
                        this.ventana.setPanelText(nickname_input, Color.green);
                        this.ventana.setPanelText(entra+"\n", Color.black);
                        System.out.println("Esto es lo que llega al actualziar uSER: "+entra);
                        entra=entra.substring(0,entra.lastIndexOf(" desde"));
                        System.out.println("Esto despues de la operacion:__"+entra+"___");
                        if (!this.usuarios.contains(entra)){
                            this.usuarios.add(entra);
                        }
                        this.ventana.setUsuarios(usuarios);
                        break;
                    case 3:
                        this.ventana.setPanelText(nickname_input, Color.red);
                        this.ventana.setPanelText(entra+"\n", Color.black);
                        //Descomponemos la palabra.
                        entra=entra.substring(0,entra.lastIndexOf(" desde"));
                        this.usuarios.remove(entra);
                        this.ventana.setUsuarios(usuarios);
                        break;
                    case 4:
                        this.ventana.setPanelText(nickname_input, Color.red);
                        this.ventana.setPanelText(" Subió el archivo "+entra+"\n", Color.black);
                        int acepto=JOptionPane.showConfirmDialog(ventana, "El usuario "+nickname_input+" ha subido el archivo "+entra+", ¿desea descargarlo?.","Nuevo mensaje",JOptionPane.YES_NO_OPTION);
                        if(acepto==JOptionPane.YES_OPTION){                  
                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setSelectedFile(new File(entra));
                            if (fileChooser.showSaveDialog(ventana) == JFileChooser.APPROVE_OPTION) {
                                File file = fileChooser.getSelectedFile();
                                ConexionFTP con=new ConexionFTP("1","1",host);
                                System.out.println("El file que sale  de Cliente 4 "+file.getAbsolutePath());
                                con.downFile(entra,file);
                                System.out.println("Se descargo el archivo satisfactoriamente");
                                
                            }                            
                        }
                        break;
                }
                
            }
            entrada.close();
            conexion.close();
            
        } catch (IOException ex) {
           // Logger.getLogger(VentanaCliente.class.getName()).log(Level.SEVERE, null, ex);
            this.ventana.setPanelText("La conexion se Cerro :(\n", Color.red);
            this.usuarios.clear();
            this.ventana.setUsuarios(usuarios);
            
        } 
    }
    
    public void flujoUsuarios(){
        System.out.println("iniciado el flujo de usuarios");
        try {
            entradaUsuarios=new ObjectInputStream(conexionUsers.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        while(conectado){            
            try {   
                ArrayList<String> usuarios=(ArrayList<String>) entradaUsuarios.readObject();  
                this.usuarios=usuarios;
                ventana.setUsuarios(usuarios);
                System.out.println("En "+this.nickname+" llega "+usuarios.toString());
                //System.out.println(usuarios.toString());
            } catch (Exception ex) {
                conectado=false;
                ex.printStackTrace();
            }
        }
    }
    
    public void flujoSalida(Integer tipo,String mensaje){
        DES objetoDES = new DES(this.clave);
        String cifrado=objetoDES.encriptar(mensaje);
        try {            
            //Enviamos nickname
            //salida.writeObject(nickname);
            //salida.flush();
            //Enviamos mensaje       
            salida.writeUTF(tipo.toString());
            salida.flush();
            //salida.writeUTF(mensaje);
            salida.writeUTF(cifrado);
            salida.flush();
        } catch (IOException ex) {
            //Logger.getLogger(VentanaCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void close(){
        try {
            this.salida.close();
            this.entrada.close();
            this.conexion.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
    
    
}
