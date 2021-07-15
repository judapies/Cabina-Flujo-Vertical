/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Informacion;

import Comunicacion.Variables;
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
                           
		flotantes = abrir.elementAt(2);
		enteros = abrir.elementAt(1);
                binarios = abrir.elementAt(0);
                
                Variables.Vidrio20Luz=binarios.get(0);
                Variables.Vidrio20Motor=binarios.get(1);
                Variables.VidrioDown=binarios.get(2);
                Variables.Imperial=binarios.get(3);
                Variables.Metrico=binarios.get(4);
                
                
                Variables.PurgaSegundos=enteros.get(0);
                Variables.PurgaMinutos=enteros.get(1);
                Variables.PPurgaSegundos=enteros.get(2);
                Variables.PPurgaMinutos=enteros.get(3);
                Variables.UVSegundos=enteros.get(4);
                Variables.UVMinutos=enteros.get(5);
                Variables.U1ContraseñaSave=enteros.get(6);
                Variables.U2ContraseñaSave=enteros.get(7);
                Variables.U3ContraseñaSave=enteros.get(8);
                Variables.U4ContraseñaSave=enteros.get(9);
                Variables.HorasUV=enteros.get(10);
                Variables.SetUV=enteros.get(11);
                Variables.ZeroActualInFlow=enteros.get(12);
                Variables.DifInflow=enteros.get(13);
                Variables.ZeroActualDownFlow=enteros.get(14);
                Variables.DifDownflow=enteros.get(15);
                Variables.GInflow=enteros.get(16);
                Variables.GDownflow=enteros.get(17);
                
                Variables.AjusteInFlow=flotantes.get(0);
                Variables.AjusteDownFlow=flotantes.get(1);
                Variables.AjustePresInFlow=flotantes.get(2);
                Variables.AjustePresDownFlow=flotantes.get(3);
                
                //Variables.tipogas = enteros.get(0);
	}
}
