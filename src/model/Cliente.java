/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private int puerto;
    private String host, nickname;
    private boolean conectado;
    private VentanaCliente ventana;
    ObjectInputStream objectoEntrante;

    public Cliente(int puerto, String host, String nickname, VentanaCliente ventana) {
        this.host = host;
        this.puerto = puerto;
        this.nickname = nickname;
        conectado=false;
        this.ventana=ventana;
    }

    public void initClient() {
        try {
            conexion = new Socket(host, puerto);
            salida = new ObjectOutputStream(conexion.getOutputStream());
            salida.writeObject(nickname);
            entrada = new ObjectInputStream(conexion.getInputStream());            
            new Thread() {
                public void run() {
                    conectado=true;
                    flujoEntrada();
                }
            ;
        } .start();  
            /*----No funcional----*/
            new Thread() {
                public void run() {
                    conectado=true;
                    //flujoUsuarios();
                }
            ;
            } .start(); 
            /*--------------------*/
            
        } catch (UnknownHostException ex) {
        } catch (IOException ex) {
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
                entra=(String) entrada.readUTF();
                System.out.println("El mensaje obtenido es: "+entra);
                switch (tipo){
                    case 1:
                        this.ventana.setPanelText(nickname_input, Color.magenta);
                        this.ventana.setPanelText(entra+"\n", Color.black);
                        break;
                    case 2:
                        this.ventana.setPanelText(nickname_input, Color.green);
                        this.ventana.setPanelText(entra+"\n", Color.black);
                        break;
                    case 3:
                        this.ventana.setPanelText(nickname_input, Color.red);
                        this.ventana.setPanelText(entra+"\n", Color.black);
                        break;
                }
                
            }
            entrada.close();
            conexion.close();
            
        } catch (IOException ex) {
           // Logger.getLogger(VentanaCliente.class.getName()).log(Level.SEVERE, null, ex);
            
        } 
    }
    
    public void flujoUsuarios(){
        try {
            objectoEntrante = new ObjectInputStream(conexion.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        while(conectado){            
            try {
                System.out.println("Ewsperoandpo el array");
                ArrayList<String> usuarios=(ArrayList<String>) objectoEntrante.readObject();
                ventana.setUsuarios(usuarios);
                System.out.println("Listo ela");
                System.out.println(usuarios.toString());
            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void flujoSalida(String mensaje){
        try {            
            //Enviamos nickname
            //salida.writeObject(nickname);
            //salida.flush();
            //Enviamos mensaje
            salida.writeUTF(mensaje);
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
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }
}
