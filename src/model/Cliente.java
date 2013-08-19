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
        try {
            while(conectado){
                //Leemos el Nickname
                //entra=(String) entrada.readObject();
                //this.ventana.setPanelText(entra, Color.cyan);
                //Leemos el mensaje
                entra=(String) entrada.readObject();
                this.ventana.setPanelText(entra+"\n", Color.black);
            }
            entrada.close();
            conexion.close();
            
        } catch (IOException ex) {
           // Logger.getLogger(VentanaCliente.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (ClassNotFoundException ex) {
        } 
    }
    
    public void flujoSalida(String mensaje){
        try {            
            //Enviamos nickname
            //salida.writeObject(nickname);
            //salida.flush();
            //Enviamos mensaje
            salida.writeObject(mensaje);
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
