/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Informacion;

import Comunicacion.Variables;
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
                binarios.add(0,Variables.Vidrio20Luz);
                binarios.add(1,Variables.Vidrio20Motor);
                binarios.add(2,Variables.VidrioDown);
                binarios.add(3,Variables.Imperial);
                binarios.add(4,Variables.Metrico);
                
                
                enteros.add(0,Variables.PurgaSegundos);
                enteros.add(1,Variables.PurgaMinutos);
                enteros.add(2,Variables.PPurgaSegundos);
                enteros.add(3,Variables.PPurgaMinutos);
                enteros.add(4,Variables.UVSegundos);
                enteros.add(5,Variables.UVMinutos);
                enteros.add(6,Variables.U1ContraseñaSave);
                enteros.add(7,Variables.U2ContraseñaSave);
                enteros.add(8,Variables.U3ContraseñaSave);
                enteros.add(9,Variables.U4ContraseñaSave);
                enteros.add(10,Variables.HorasUV);
                enteros.add(11,Variables.SetUV);
                enteros.add(12,Variables.ZeroActualInFlow);
                enteros.add(13,Variables.DifInflow);
                enteros.add(14,Variables.ZeroActualDownFlow);
                enteros.add(15,Variables.DifDownflow);
                enteros.add(16,Variables.GInflow);
                enteros.add(17,Variables.GDownflow);
                enteros.add(18,Variables.SegundosAcumuladosUV);
                enteros.add(19,Variables.UmbralVidrioUP);
                enteros.add(20,Variables.UmbralVidrioDN);
                enteros.add(21,Variables.UmbralLVidrioUP);
                enteros.add(22,Variables.UmbralLVidrioDN);
                enteros.add(23,Variables.UmbralHVidrioUP);
                enteros.add(24,Variables.UmbralHVidrioDN);
                
                flotantes.add(0,Variables.AjusteInFlow);
                flotantes.add(1,Variables.AjusteDownFlow);
                flotantes.add(2,Variables.AjustePresInFlow);
                flotantes.add(3,Variables.AjustePresDownFlow);
                
                                                               
                guardar.add(0, binarios);
                guardar.add(1, enteros);
                guardar.add(2, flotantes);
                                
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
