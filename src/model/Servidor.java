
package model;

import java.awt.Color;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import view.VentanaServidor;

public class Servidor {

    private ServerSocket socketServidor;
    private ServerSocket socketServidorUsers;
    private Socket conexion;
    private int puerto;
    private boolean multiplesConexiones;
    //Alamacenamos todos los hilos de flujos entrantes
    private ArrayList<ThreadFlujo> flujosEntrada;
    private VentanaServidor ventana;
    private ArrayList<String> usuarios;
    private ObjectOutputStream sal2;
    String clave="4d89g13j4j91j27c582ji69373y788r6";

    public Servidor(int puerto) throws IOException {
        this.puerto = puerto;
        usuarios=new ArrayList<String>();
        usuarios.clear();
        socketServidor = new ServerSocket(puerto);
        multiplesConexiones = true;
        flujosEntrada = new ArrayList<ThreadFlujo>();
        flujosEntrada.clear();        
        
                
    }
    
    public Servidor(int puerto,VentanaServidor ventana) throws IOException {
        this.ventana=ventana;
        this.puerto = puerto;
        usuarios=new ArrayList<String>();
        usuarios.clear();
        socketServidor = new ServerSocket(puerto);        
        socketServidorUsers=new ServerSocket(8000);
        multiplesConexiones = true;
        flujosEntrada = new ArrayList<ThreadFlujo>();
        flujosEntrada.clear();        
    }
    
    
    public void deleteUser(String nickname){
         for (ThreadFlujo flujo:flujosEntrada){   
           if(flujo.nickname==nickname){
                flujo.close();
                break;
           }
        } 
    }

    private class ThreadFlujo extends Thread {

        public Socket conexion;
        public Socket conexionUsuarios;
        private boolean stop;
        ObjectInputStream entrada;
        ObjectOutputStream salida;        
        ObjectOutputStream salidaUsuarios;
        public String nickname;
        String clave="4d89g13j4j91j27c582ji69373y788r6";

        public ThreadFlujo(Socket conexion,Socket conexionUsuarios) {
            this.conexion = conexion;
            this.conexionUsuarios=conexionUsuarios;
            /*try {
                salidaUsuarios=new ObjectOutputStream(conexionUsuarios.getOutputStream());
                salidaUsuarios.writeObject(null);
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            
            stop=false;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
        
        

        public boolean isStop() {
            return stop;
        }

        public void seStop(boolean stop) {
            this.stop = stop;
        }
        
        public void close(){
            try {
                conexion.close();                
                
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            stop=true;            
        }
        
       public void writte(String mensaje){
            try {
                salida.writeUTF(mensaje);
                salida.flush();
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
       
       public void writeUsuarios(ArrayList<String> usuarios){
           try {
               salidaUsuarios.flush();
                salidaUsuarios.writeObject(usuarios);                
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
       
     
       
        public void run() {            
            try {
                //Obtenemos datos como el NICKNAME
                entrada=new ObjectInputStream(conexion.getInputStream());
                this.nickname=(String)entrada.readObject();
                //Actualizamos usuarios
                usuarios.add(nickname);
                ventana.setUsuarios(usuarios);    
                /**asdasdasdasdasd*/
                salidaUsuarios=new ObjectOutputStream(conexionUsuarios.getOutputStream());
                salidaUsuarios.writeObject(usuarios);
                //sal2=new ObjectOutputStream(conexionUsers.getOutputStream());
                //sal2.writeObject(usuarios);
                //Informamos de la conexion                
                salida=new ObjectOutputStream(conexion.getOutputStream());                                 
                ventana.setPanelText("Conectado con: " +  this.nickname+ " desde "+conexion.getInetAddress().getHostAddress()+"\n", Color.blue);
                flujoSalida(2,"Conectado con: ",  this.nickname+" desde "+conexion.getInetAddress().getHostAddress());
                System.out.println("Conectado con: " + this.nickname+ " desde "+conexion.getInetAddress().getHostAddress()+"\n");
                flujoSalidaUsuarios(usuarios,this.nickname);
                //Ecuchando algun mensaje entrante
                //entrada = new ObjectInputStream(conexion.getInputStream());  
                DES CifradoDes=new DES(this.clave);
                while (!stop) {                        
                        //Obtenemos el mensaje y lo imprimimos en pantalla    
                    System.out.print("El tipo de emsanje entranre es: " );                    
                        String lectura = (String) entrada.readUTF();
                        System.out.println(lectura);
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        Calendar cal = Calendar.getInstance();   
                        switch (Integer.parseInt(lectura)){
                            case 1:                                 
                                lectura = CifradoDes.desencriptar((String) entrada.readUTF());                                                                         
                                ventana.setPanelText(this.nickname+"("+dateFormat.format(cal.getTime())+")>> ",Color.magenta);
                                ventana.setPanelText(lectura+"\n",Color.black);
                                //Lo enviamos a los demas usuarios
                                //flujoSalida(this.nickname+">>"+lectura,this.nickname);
                                flujoSalida(lectura,this.nickname);
                                break;
                            case 4:
                                lectura = CifradoDes.desencriptar((String) entrada.readUTF()); 
                                ventana.setPanelText(this.nickname+"("+dateFormat.format(cal.getTime())+")>> ",Color.magenta);
                                ventana.setPanelText("ha subido el archivo "+lectura+"\n",Color.blue);
                                
                                //Lo enviamos a los demas usuarios
                                //flujoSalida(this.nickname+">>"+lectura,this.nickname);
                                flujoSalida(4,this.nickname,lectura);          
                                break;
                        } 
                        
                       
                        
                        
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {                
                try {
                    System.out.println("Controlado");
                    //Actualizamos usuarios                
                    usuarios.remove(nickname);
                    ventana.setUsuarios(usuarios);
                    ventana.setPanelText("Cerrando  conexion con: " + this.nickname + " desde "+conexion.getInetAddress().getHostAddress()+"\n", Color.red);
                    flujoSalida(3,"Cerrando  conexion con: ",this.nickname+" desde "+conexion.getInetAddress().getHostAddress()+"\n");
                    System.out.println("Conectado conexion con: " + this.nickname + " desde "+conexion.getInetAddress().getHostAddress()+"\n");
                    /*
                    try {
                        sal2.writeObject(usuarios);
                    } catch (IOException ex1) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex1);
                    }*/
                    
                } catch(Exception e){
                    e.printStackTrace();                    
                   
                }
            } 
            finally {
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
        Socket conexionUsuarios;
        while (multiplesConexiones) {
            System.out.println("A la espera de una conexion...");
            conexion = socketServidor.accept();          
            
            conexionUsuarios=socketServidorUsers.accept();
           
            
            System.out.println("Se establecio conexion CON    "+conexionUsuarios.getInetAddress());
            //Creacion del hilo de escucha
            flujosEntrada.add(new ThreadFlujo(conexion,conexionUsuarios));
            //inicializacion hilo
            flujosEntrada.get(flujosEntrada.size() - 1).start();
            //escuchamos los usuarios entrantes
            //aaaaaaaaaaaaaaaaaaa
            //conexionUsers=socketServidorUsers.accept();
            
        }
    }
    
    public void flujoSalidaUsuarios(ArrayList<String> usuare,String nickname){
        /*for (ThreadFlujo flujo:flujosEntrada){                 
                flujo.writeUsuarios(usuare);
        }  */
    }
    
    //Envia los mensajes a todos los clientes
    public void flujoSalida(Integer indicador,String user_or_other_thing,String mensaje){
        DES CifradoDes=new DES(this.clave);
       for (ThreadFlujo flujo:flujosEntrada){   
           if(flujo.nickname!=user_or_other_thing){
                flujo.writte(indicador.toString());
                flujo.writte(user_or_other_thing);                
                flujo.writte(CifradoDes.encriptar(mensaje));
           }
        }               
    }
    
   
    
    //Envia los mensajes a todos los clientes expecto a USER
    /*Es util para no renviar el mensaje escrito por un cliente*/
    public void flujoSalida(String mensaje, String user){
       DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
       Calendar cal = Calendar.getInstance();  
       
       DES CifradoDes = new DES(this.clave);
       
       
       for (ThreadFlujo flujo:flujosEntrada){
            if(flujo.nickname!=user){
                flujo.writte("1");
                flujo.writte(user+"("+dateFormat.format(cal.getTime())+")>> ");
                //flujo.writte(mensaje);
                flujo.writte(CifradoDes.encriptar(mensaje));
            }
        }               
    }
       
    
    //Finalizar todas las conexiones activas y cerrar el servidor
    public void close(){
        try {
            //Cerramos tdas las conexiones
            for (ThreadFlujo flujo:flujosEntrada){
                ventana.setPanelText("Cerrando  conexion con: " + conexion.getInetAddress().getHostName() + "  "+conexion.getInetAddress().getHostAddress()+"\n", Color.red);
                System.out.println("Conectado conexion con: " + conexion.getInetAddress().getHostName() + "  "+conexion.getInetAddress().getHostAddress()+"\n");
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
