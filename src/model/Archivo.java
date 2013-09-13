    package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Archivo {
    File archivo = null;
    FileReader lector = null;
    BufferedReader bufer = null;
    
    public File escribirArchivo(File archivo, String datos){
        FileWriter fichero = null;
        PrintWriter pw = null;        
        try
        {
            //fichero = new FileWriter("c:/prueba.txt");
            fichero = new FileWriter(archivo);
            pw = new PrintWriter(fichero);
            pw.println(datos);
 
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
        return archivo;
    }
    
    public void escribirArchivo(String nombreArchivo,String cadena){
        
        FileWriter fichero = null;
        PrintWriter pw = null;
        try {
            fichero = new FileWriter(nombreArchivo);
            pw = new PrintWriter(fichero);
            String arreglo[]=cadena.split("\n");                    
            for (String palabra: arreglo){
                pw.println(palabra);
            }
      

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (null != fichero) {
                fichero.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }

    }
    
    /*LEE EL ARCHIVO CON EL NOMBRE QUE RECIBE*/
     public String leerArchivo(String nombreArchivo) {
        
        String retorno="";
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File(nombreArchivo);
            lector = new FileReader(archivo);
            bufer = new BufferedReader(lector);

            // Lectura del fichero
            String linea;
            while ((linea = bufer.readLine()) != null) {
                retorno=retorno+"\n"+linea;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            if (null != lector) {
                lector.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        System.out.println(retorno);
        return retorno;
    }
     
    /*LEE EL ARCHIVO QUE RECIBE*/
     public String leerArchivo(File archivo) {
        
        String retorno="";
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            lector = new FileReader(archivo);
            bufer = new BufferedReader(lector);

            // Lectura del fichero
            String linea=bufer.readLine();    
            retorno=linea;
            while ((linea = bufer.readLine()) != null) {
                retorno=retorno+"\n"+linea;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        try {
            if (null != lector) {
                lector.close();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        
        System.out.println(retorno);
        return retorno;
    }
    
     /*Esta funcion sirve para guardar un archivo en el sistema
     Recibe la ruta, el contenido del archivo y un boolean*/
    public boolean guardarArchivo(String ruta, String contenido, boolean CleanFileContent) {
        boolean resultado = true;

        FileWriter file;
        BufferedWriter writer;

        try {
            file = new FileWriter(ruta, !CleanFileContent);
            writer = new BufferedWriter(file);
            writer.write(contenido,0,contenido.length());
            writer.close();
            file.close();
        } catch (IOException ex) {
            resultado = false;
            ex.printStackTrace();
        } finally {
            return resultado;
        }
    }
}
