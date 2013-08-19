/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author camilortte
 */
public class Servidor {

    private ServerSocket socketServidor;
    private Socket conexion;
    private int puerto;
    private boolean multiplesConexiones;
    //Alamacenamos todos los hilos de flujos entrantes
    private ArrayList<ThreadFlujo> flujosEntrada;

    public Servidor(int puerto) throws IOException {
        this.puerto = puerto;
        socketServidor = new ServerSocket(puerto);
        multiplesConexiones = true;
        flujosEntrada = new ArrayList<ThreadFlujo>();
        flujosEntrada.clear();        
    }

    private class ThreadFlujo extends Thread {

        private Socket conexion;
        private boolean stop;
        ObjectInputStream entrada;
        ObjectOutputStream salida;

        public ThreadFlujo(Socket conexion) {
            this.conexion = conexion;
            stop=false;
        }

        public boolean isStop() {
            return stop;
        }

        public void seStop(boolean stop) {
            this.stop = stop;
        }
        
        public void close(){
            try {
                stop=true;            
                conexion.close();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
       public void writte(String mensaje){
            try {
                salida.writeObject(mensaje);
                salida.flush();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
        public void run() {            
            try {
                System.out.println("Conectado con: " + conexion.getInetAddress().getHostName() + "  "+conexion.getInetAddress().getHostAddress()+"\n");
                salida=new ObjectOutputStream(conexion.getOutputStream());  
                entrada = new ObjectInputStream(conexion.getInputStream());
                while (!stop) {
                        String lectura = (String) entrada.readObject();
                        System.out.println("lecutura: " + lectura);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    entrada.close();
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public void initServer() throws IOException {
        //A la espera de multiples conexiones        
        while (multiplesConexiones) {
            System.out.println("A la espera de una conexion...");
            conexion = socketServidor.accept();
            //Creacion del hilo de escucha
            flujosEntrada.add(new ThreadFlujo(conexion));
            //inicializacion hilo
            flujosEntrada.get(flujosEntrada.size() - 1).start();
        }
    }
    
    //Envia los mensajes a todos los clientes
    public void flujoSalida(String mensaje){
       for (ThreadFlujo flujo:flujosEntrada){
                flujo.writte(mensaje);
        }               
    }
    
    //Finalizar todas las conexiones activas y cerrar el servidor
    public void close(){
        try {
            //Cerramos tdas las conexiones
            for (ThreadFlujo flujo:flujosEntrada){
                flujo.close();
            }
            //Cerramos la conexion del servidor
            socketServidor.close();
            System.out.println("Se cerraron todas las conexiones satisfactoriamente");
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
