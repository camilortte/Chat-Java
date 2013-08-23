/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import view.VentanaCliente;
import view.VentanaPrincipal;
import view.VentanaServidor;

/**
 *
 * @author camilortte
 */
public class Chat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //(new VentanaPrincipal()).setVisible(true);
        new Thread(){
          public void run(){
              (new VentanaServidor()).setVisible(true);
          }  
        }.start();
        new Thread(){
          public void run(){
              (new VentanaCliente()).setVisible(true);
          }  
        }.start();
        new Thread(){
          public void run(){
              (new VentanaCliente()).setVisible(true);
          }  
        }.start();
    }
}
