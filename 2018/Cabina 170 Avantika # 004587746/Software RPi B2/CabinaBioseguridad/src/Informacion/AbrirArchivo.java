/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Informacion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class AbrirArchivo {

        private Vector<ArrayList> abrir;
        
        private ArrayList<Integer> enteros;
	private ArrayList<Boolean> binarios;
        private ArrayList<Double> flotantes;
       	
	@SuppressWarnings("unchecked")
        
	public void startOpenning(File f) throws IOException, ClassNotFoundException {
            
                abrir = new Vector<>();
                
                enteros = new ArrayList<>();
                binarios = new ArrayList<>();
                flotantes = new ArrayList<>();
               
                
                FileInputStream fis=new FileInputStream(f);
		ObjectInputStream ois=new ObjectInputStream(fis);
                
		abrir=(Vector<ArrayList>) ois.readObject();

	}

	public void startConverting() throws IOException {
                           
		enteros = abrir.elementAt(1);
                flotantes = abrir.elementAt(0);
                
                //Variables.SPBombaAcido=flotantes.get(0);
                
                //Variables.tipogas = enteros.get(0);
	}
}
