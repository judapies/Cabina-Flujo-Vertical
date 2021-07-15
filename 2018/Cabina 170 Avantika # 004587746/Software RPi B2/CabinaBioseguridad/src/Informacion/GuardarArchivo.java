/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Informacion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class GuardarArchivo {
    
        private Vector<ArrayList> guardar;
        
        private ArrayList<Integer> enteros;
	private ArrayList<Boolean> binarios;
        private ArrayList<Double> flotantes;
        //private ArrayList<Plot2DPanel> graficamovimiento;
        private ArrayList<String[]> vectorString;
        //private ArrayList<DefaultTableModel> Arraytabla;
        
        public GuardarArchivo() {
	}
	
	public void startSaving() throws IOException {

                guardar = new Vector<>();
                
                enteros = new ArrayList<>();
                binarios = new ArrayList<>();
                flotantes = new ArrayList<>();
                //graficamovimiento = new ArrayList<>();
                vectorString = new ArrayList<>();
                //Arraytabla = new ArrayList<>();
                               
                //flotantes.add(0,Variables.SPBombaAcido);
                
                //enteros.add(0,Variables.tipogas);
                                                               
                guardar.add(0, flotantes);
                guardar.add(1, enteros);
                                
	}
	
	public void endSaving(File f) throws IOException {
		if (!f.exists()) {
			f.createNewFile();
		}
		
              try(FileOutputStream fos = new FileOutputStream(f)) {
                ObjectOutputStream oos=new ObjectOutputStream(fos);
                oos.writeObject(guardar);
                oos.flush();
                System.out.println("guardar ok");
     
            } 
	}
}
