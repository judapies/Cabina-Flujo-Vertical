/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comunicacion;

import HID.HIDComm;
import Interfaz.CicloPurga;
import Interfaz.Menu_Configuracion;
import static Interfaz.Menu_VidaUV.LabelVidaUV;
import Interfaz.PantallaPrincipal;
import Interfaz.PantallaSplash;
import Interfaz.TimerEx;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author JuanDavid
 */
public class Hilo implements Runnable{
    
    public static int flancosplash=0;
    public static Interfaz.Menu_Principal MenuPrincipal= new Interfaz.Menu_Principal();
    public static Interfaz.Menu_Configuracion MenuConfiguracion= new Interfaz.Menu_Configuracion();
    public static Interfaz.Menu_Ajustes MenuAjustes= new Interfaz.Menu_Ajustes();
    public static Interfaz.Menu_Servicio MenuServicio= new Interfaz.Menu_Servicio();
    public static Interfaz.Menu_AjustesUV MenuAjUV= new Interfaz.Menu_AjustesUV();
    public static Interfaz.Menu_AjustesUnidades MenuAjUnidades= new Interfaz.Menu_AjustesUnidades();
    public static Interfaz.Menu_AjustesContraseña MenuAjContraseña= new Interfaz.Menu_AjustesContraseña();
    public static Interfaz.Menu_AjustesContraseñaNueva MenuAjContraseñaNueva= new Interfaz.Menu_AjustesContraseñaNueva();
    public static Interfaz.Menu_FuncionamientoLuz MenuFunLuz= new Interfaz.Menu_FuncionamientoLuz();
    public static Interfaz.Menu_FuncionamientoMotor MenuFunMotor= new Interfaz.Menu_FuncionamientoMotor();
    public static Interfaz.Menu_FuncionamientoLuzUV MenuFunLuzUV= new Interfaz.Menu_FuncionamientoLuzUV();
    public static Interfaz.Menu_TempoUV MenuTempoUV= new Interfaz.Menu_TempoUV();
    public static Interfaz.Menu_TiempoPurga MenuPurga= new Interfaz.Menu_TiempoPurga();
    public static Interfaz.Menu_TiempoPostPurga MenuPostPurga= new Interfaz.Menu_TiempoPostPurga();
    public static Interfaz.Menu_Horometro MenuHorometro= new Interfaz.Menu_Horometro();
    public static Interfaz.Menu_ResetUV MenuResetUV= new Interfaz.Menu_ResetUV();
    public static Interfaz.Menu_VidaUV MenuVidaUV= new Interfaz.Menu_VidaUV();
    public static Interfaz.MensajeContraseña MsjContraOk= new Interfaz.MensajeContraseña();
    public static Interfaz.MensajeContraseñaIncorrecta MsjContraIn= new Interfaz.MensajeContraseñaIncorrecta();
    public static Interfaz.MensajeContraseñaAlmacenada MsjContraAl= new Interfaz.MensajeContraseñaAlmacenada();
    public static PantallaSplash Splash = new PantallaSplash();
    public static Interfaz.Contraseña Clave = new Interfaz.Contraseña();
    public static CicloPurga Purga = new CicloPurga();
    public static PantallaPrincipal Principal = new PantallaPrincipal();
    public static Interfaz.PantallaOff Off = new Interfaz.PantallaOff();
    public static Interfaz.Menu_SenInFlow MenuSenInflow = new Interfaz.Menu_SenInFlow();
    public static Interfaz.Menu_PresInFlow MenuPresInflow = new Interfaz.Menu_PresInFlow();
    public static Interfaz.Menu_PresDownFlow MenuPresDownflow = new Interfaz.Menu_PresDownFlow();
    public static Interfaz.Menu_SenDownFlow MenuSenDownflow = new Interfaz.Menu_SenDownFlow();
    public static Interfaz.Menu_Modo MenuModo = new Interfaz.Menu_Modo();
    public static Interfaz.CicloPostPurga PPurga = new Interfaz.CicloPostPurga();
        
    
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        try {
            AbreConfig();
        } catch (IOException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        LimpiarVector();
        
        while(true)
        {    
            EnviarDato();
            try {
                Thread.sleep(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
            LeerDato();
            try {            
                ActualizarCampos();
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            try {
                SupervisaMenus();
            } catch (InterruptedException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Calendar cal = new GregorianCalendar();
            int hora=cal.get(Calendar.HOUR_OF_DAY);
            int minuto=cal.get(Calendar.MINUTE);
            int segundo=cal.get(Calendar.SECOND);
            Formatter formato2H = new Formatter();  // Formato de la hora
            formato2H.format("%02d", hora);
            String hora2 = formato2H.toString();    // String de la hora dos cifras
            Formatter formato2M = new Formatter();
            formato2M.format("%02d", minuto);
            String minuto2 = formato2M.toString();  // String de minutos dos cifras
            Formatter formato2S = new Formatter();
            formato2S.format("%02d", segundo);
            String segundo2 = formato2S.toString(); // String de segundos dos cifras
            
        }
    }
    
    public void AbreConfig() throws IOException, ClassNotFoundException{
        File f = new File("Config.con");
        if(f.exists())
        {
            Informacion.AbrirArchivo o=new Informacion.AbrirArchivo();
            o.startOpenning(f);
            o.startConverting();
        }
    }
    
    public void GuardaConfig(){
        File f = new File("Config.con");
        Informacion.GuardarArchivo s=new Informacion.GuardarArchivo();
            try {
                s.startSaving();
                s.endSaving(f);
            } catch (IOException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static void EnviarDato(){
        Variables.tx_msg[0]=(byte)255;
        Variables.tx_msg[1]=(byte)255;
        Variables.tx_msg[2]=(byte)255;
        
        if(Variables.MotorDownFlow==true)
            Variables.tx_msg[3]=(byte)5;
        else
            Variables.tx_msg[3]=(byte)10;
        
        if(Variables.MotorInFlow==true)
            Variables.tx_msg[4]=(byte)5;
        else
            Variables.tx_msg[4]=(byte)10;
        
        if(Variables.LuzBlanca==true)
            Variables.tx_msg[5]=(byte)5;
        else
            Variables.tx_msg[5]=(byte)10;
        
        if(Variables.LuzUV==true)
            Variables.tx_msg[6]=(byte)5;
        else
            Variables.tx_msg[6]=(byte)10;
        
        if(Variables.Toma==true)
            Variables.tx_msg[7]=(byte)5;
        else
            Variables.tx_msg[7]=(byte)10;
        
        if(Variables.Alarma==true)
            Variables.tx_msg[8]=(byte)5;
        else
            Variables.tx_msg[8]=(byte)10;
        
        Variables.tx_msg[9]=(byte)Variables.AperturaDamper;
        Variables.tx_msg[10]=(byte)255;
        Variables.tx_msg[11]=(byte)255;
        Variables.tx_msg[12]=(byte)128;
        HIDComm.EnvioHID((byte)2, Variables.tx_msg,13);
        Variables.AperturaDamper+=10;
    }
    
    public void LeerDato(){              
            Variables.rx_msgint=ArrayByteToArrayInt(Variables.rx_msg);
            
            if(Variables.rx_msg[13]==(byte)10)
            {
                Variables.Arriba=true;
                System.out.println("Arriba");  
                Variables.adc1=0;
                try 
                {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                    }
            }   
                
            if(Variables.rx_msg[13]==0X14)
            {
                Variables.Abajo=true;
                System.out.println("Abajo");
                Variables.adc2=0;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
            
            if(Variables.rx_msg[13]==0X1E)
            {
                Variables.Derecha=true;
                System.out.println("Derecha");  
                Variables.adc3=0;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
            
            if(Variables.rx_msg[13]==0X28)
            {
                Variables.Izquierda=true;
                System.out.println("Izquierda");  
                Variables.adc7=0;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
        }

    public static int ByteToInt8(byte x){
        int valor=x;
        if(valor<0)valor+=256;
        return(valor);
    }
    
    public static int[] ArrayByteToArrayInt(byte[] arreglo){
        int tmp[]= new int [18];
        
        for (int i=0; i<18; i++){
            tmp[i]=ByteToInt8(arreglo[i]);
        }
        
        return tmp;
    }
    
    public void LimpiarVector(){
        
        for (int i=0;i<18;i++) {
            Variables.tx_msg[i]=(byte)50;
        }
    }

    public static double promedio(double [] datos,double n){
        double promedio=0,suma=0;
        for(int i =0;i<n;i++){
            suma+=datos[i];
        }
        promedio=suma/n;
        return promedio;
    }
    
    public static double ADCToFlow(int lsb, int hsb, int diferencia){
        double flow=0;
        int adcFlow=0;
        final double a=0.0000001418633343546420;
        final double b=-0.00009476749001431169;
        final double c=0.040182822903506;
        final double d=-5.354852229527197;
        adcFlow=((hsb*256)+lsb)-diferencia;
        flow=(Math.pow(adcFlow,3)*a)+(Math.pow(adcFlow,2)*b)+(adcFlow*c)+(d)-0.08;
        if(flow<0.0)
            flow=0.0;
        return (flow);
    }
    
    public static double ADCToPres(int lsb, int hsb, int G){
        double pres=0;
        double adcPres=0;
        
        adcPres=(hsb*256)+lsb;
        pres=(adcPres/G)-1.0;
        if(pres<0.0)
            pres=0.0;
 
        return (pres);
    }
    
    public void ActualizarCampos(){
        DecimalFormat decimalflow = new DecimalFormat("0.00");
        
        Variables.VidrioUP=Variables.rx_msgint[11];
        Variables.VidrioDN=Variables.rx_msgint[12];

        // Lectura y actualizacion de Inflow
        Variables.PVInFlow=ADCToFlow(Variables.rx_msgint[9], Variables.rx_msgint[10],Variables.DifInflow);
        
        Variables.InFlowProm[Variables.indicePromedioInFlow]=Variables.PVInFlow;
        Variables.InFlowPromedio=promedio(Variables.InFlowProm, 6);
        Variables.indicePromedioInFlow++;
        if(Variables.indicePromedioInFlow>=6)
            Variables.indicePromedioInFlow=0;
        
        if(Variables.Metrico && !Variables.Imperial)
            PantallaPrincipal.PV_InFlow.setText(""+decimalflow.format(Variables.InFlowPromedio)+" m/s");
        
        if(!Variables.Metrico && Variables.Imperial)
            PantallaPrincipal.PV_InFlow.setText(String.format("%03d",(int)(Variables.InFlowPromedio*196.85))+" f/m");
        
        // Lectura y actualizacion de Downflow
        Variables.PVDownFlow=ADCToFlow(Variables.rx_msgint[7], Variables.rx_msgint[8],Variables.DifDownflow);
        
        Variables.DownFlowProm[Variables.indicePromedioDownFlow]=Variables.PVDownFlow;
        Variables.DownFlowPromedio=promedio(Variables.DownFlowProm, 6);
        Variables.indicePromedioDownFlow++;
        if(Variables.indicePromedioDownFlow>=6)
            Variables.indicePromedioDownFlow=0;
        
        if(Variables.Metrico && !Variables.Imperial)
            PantallaPrincipal.PV_DownFlow.setText(""+decimalflow.format(Variables.DownFlowPromedio)+" m/s");
        
        if(!Variables.Metrico && Variables.Imperial)
            PantallaPrincipal.PV_DownFlow.setText(String.format("%03d",(int)(Variables.DownFlowPromedio*196.85))+" f/m");
        
        // Lectura y ajuste de estado de Filtro InFlow
            double PorcentajeInFlow=((ADCToPres(Variables.rx_msgint[5], Variables.rx_msgint[6], Variables.GInflow))*100)/1.5;
            PantallaPrincipal.InFlowLabel.setText(String.format("%03d",(int)PorcentajeInFlow)+"%");
            
            if(PorcentajeInFlow>0 && PorcentajeInFlow<25)
            {
                PantallaPrincipal.Bar25InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25InFlow.setBackground(Color.green);
                PantallaPrincipal.Bar50InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50InFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar75InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75InFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar100InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar100InFlow.setBackground(Color.red);
                PantallaPrincipal.Bar50InFlow.setValue(0);
                PantallaPrincipal.Bar75InFlow.setValue(0);
                PantallaPrincipal.Bar100InFlow.setValue(0);
                PantallaPrincipal.Bar25InFlow.setValue((int)((PorcentajeInFlow*100)/25));
                
            }
            
            if(PorcentajeInFlow>25 && PorcentajeInFlow<50)
            {
                PantallaPrincipal.Bar25InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25InFlow.setBackground(Color.green);
                PantallaPrincipal.Bar50InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50InFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar75InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75InFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar100InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar100InFlow.setBackground(Color.red);
                PantallaPrincipal.Bar25InFlow.setValue(100);
                PantallaPrincipal.Bar75InFlow.setValue(0);
                PantallaPrincipal.Bar100InFlow.setValue(0);
                PantallaPrincipal.Bar50InFlow.setValue((int) (((PorcentajeInFlow*100)/25)-100));
                
            }
           
            if(PorcentajeInFlow>50 && PorcentajeInFlow<75)
            {
                PantallaPrincipal.Bar25InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25InFlow.setBackground(Color.green);
                PantallaPrincipal.Bar50InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50InFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar75InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75InFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar100InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar100InFlow.setBackground(Color.red);
                PantallaPrincipal.Bar50InFlow.setValue(100);
                PantallaPrincipal.Bar25InFlow.setValue(100);
                PantallaPrincipal.Bar100InFlow.setValue(0);
                PantallaPrincipal.Bar75InFlow.setValue((int) (((PorcentajeInFlow*100)/25)-200));
            }
            
            if(PorcentajeInFlow>75 && PorcentajeInFlow<=100)
            {
                PantallaPrincipal.Bar25InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25InFlow.setBackground(Color.green);
                PantallaPrincipal.Bar50InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50InFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar75InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75InFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar100InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar100InFlow.setBackground(Color.red);
                PantallaPrincipal.Bar50InFlow.setValue(100);
                PantallaPrincipal.Bar25InFlow.setValue(100);
                PantallaPrincipal.Bar75InFlow.setValue(100);
                PantallaPrincipal.Bar100InFlow.setValue((int) (((PorcentajeInFlow*100)/25)-300));
            }
            
        // Lectura y ajuste de estado de Filtro DownFlow
            double PorcentajeDownFlow=((ADCToPres(Variables.rx_msgint[3], Variables.rx_msgint[4], Variables.GDownflow))*100)/1.5;
            PantallaPrincipal.DownFlowLabel.setText(String.format("%03d",(int)PorcentajeDownFlow)+"%");
            
            if(PorcentajeDownFlow>0 && PorcentajeDownFlow<25)
            {
                PantallaPrincipal.Bar25DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25DownFlow.setBackground(Color.green);
                PantallaPrincipal.Bar50DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50DownFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar75DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75DownFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar100DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar100DownFlow.setBackground(Color.red);
                PantallaPrincipal.Bar50DownFlow.setValue(0);
                PantallaPrincipal.Bar75DownFlow.setValue(0);
                PantallaPrincipal.Bar100DownFlow.setValue(0);
                PantallaPrincipal.Bar25DownFlow.setValue((int)((PorcentajeDownFlow*100)/25));
                
            }
            
            if(PorcentajeDownFlow>25 && PorcentajeDownFlow<50)
            {
                PantallaPrincipal.Bar25DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25DownFlow.setBackground(Color.green);
                PantallaPrincipal.Bar50DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50DownFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar75DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75DownFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar100DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar100DownFlow.setBackground(Color.red);
                PantallaPrincipal.Bar25DownFlow.setValue(100);
                PantallaPrincipal.Bar75DownFlow.setValue(0);
                PantallaPrincipal.Bar100DownFlow.setValue(0);
                PantallaPrincipal.Bar50DownFlow.setValue((int) (((PorcentajeDownFlow*100)/25)-100));
                
            }
           
            if(PorcentajeDownFlow>50 && PorcentajeDownFlow<75)
            {
                PantallaPrincipal.Bar25DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25DownFlow.setBackground(Color.green);
                PantallaPrincipal.Bar50DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50DownFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar75DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75DownFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar100DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar100DownFlow.setBackground(Color.red);
                PantallaPrincipal.Bar50DownFlow.setValue(100);
                PantallaPrincipal.Bar25DownFlow.setValue(100);
                PantallaPrincipal.Bar100DownFlow.setValue(0);
                PantallaPrincipal.Bar75DownFlow.setValue((int) (((PorcentajeDownFlow*100)/25)-200));
            }
            
            if(PorcentajeDownFlow>75 && PorcentajeDownFlow<=100)
            {
                PantallaPrincipal.Bar25DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25DownFlow.setBackground(Color.green);
                PantallaPrincipal.Bar50DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50DownFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar75DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75DownFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar100DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar100DownFlow.setBackground(Color.red);
                PantallaPrincipal.Bar50DownFlow.setValue(100);
                PantallaPrincipal.Bar25DownFlow.setValue(100);
                PantallaPrincipal.Bar75DownFlow.setValue(100);
                PantallaPrincipal.Bar100DownFlow.setValue((int) (((PorcentajeDownFlow*100)/25)-300));
            }
    }
    
    public int LimitaTiempo(int valor){
        if(valor>59)
            valor=0;
        
        if(valor<0)
            valor=59;
        
        return valor;
                    
    }
    
    public int LimitaUnidad(int valor){
        if(valor>9)
            valor=0;
        
        if(valor<0)
            valor=9;
        
        return valor;
                    
    }
    
    private void SupervisaMenus() throws InterruptedException 
    {   
        Thread.sleep(200);
        OffView();
        ClaveView();
        PurgaView();
        PantallaPrincipalView();
        MenuPrincipal();
        MenuConfiguraciones();
        MenuFuncionamientoLuz();
        MenuFuncionamientoMotor();
        MenuFuncionamientoLuzUV();        
        MenuTempoUV();        
        MenuPurga();
        MenuPostPurga();
        MenuAjustes();
        MenuAjusteUnidades();
        MenuContraseña();
        MenuContraseñaNueva();
        MenuAjustesUV();
        MenuHorometroUV();
        MenuResetUV();
        MenuSetUV();
        MenuServicio();
        MenuSensorInFlow();
        MenuSensorDownFlow();  
        MenuPresionDownnFlow();
        MenuPresionInFlow();
        MenuModo();
        PostPurgaView();
    }

    private void OffView (){
        if(Off.isVisible())
        {
            if(Variables.Derecha || Variables.Izquierda || Variables.Abajo || Variables.Arriba)
            {
                TimerEx.apago=10;
                Variables.Derecha=false;
                Variables.Izquierda=false;
                Variables.Abajo=false;
                Variables.Arriba=false;
            }
        }
    }
    
    // Purga OK.
    private void PurgaView() throws InterruptedException{
        if(Purga.isVisible())
        {
            if(Variables.Derecha)
            {
                Interfaz.TimerEx.DetieneTimer();
                Purga.LabelMsg.setText("Purga Finalizada");
                Thread.sleep(2000);
                Purga.dispose();
                Thread.sleep(200);
                Principal.setVisible(true);
                Variables.LuzBlanca=true;
                Variables.MotorDownFlow=true;
                Variables.Derecha=false;
            }
            Variables.MotorInFlow=true;
            Variables.MotorDownFlow=true;
            if(Variables.MinutosPurga==Variables.PurgaMinutos &&
                    Variables.SegundosPurga==Variables.PurgaSegundos)
            {
                Interfaz.TimerEx.DetieneTimer();
                Purga.LabelMsg.setText("Purga Finalizada");
                Thread.sleep(2000);
                Purga.dispose();
                Thread.sleep(200);
                Principal.setVisible(true);
                Variables.LuzBlanca=true;
                Variables.MotorDownFlow=true;
            }
        }
    }
    
    // Clave OK, probablemente falta añadir uso de tecla izquierda. Añadir Combinacion para reestablecer Clave
    private void ClaveView() throws InterruptedException{
        if(Clave.isVisible())
        {
            if(Clave.LabelU1.getForeground()==Color.blue)
                Clave.LabelU1.setText(Integer.toString(Variables.U1Contraseña));
               
            if(Variables.Arriba==true)
            {
                TimerEx.tiempoapagado=0;
                if(Clave.LabelU1.getForeground()==Color.blue)
                {
                    Variables.U1Contraseña++;
                    Variables.U1Contraseña=LimitaUnidad(Variables.U1Contraseña);
                    Clave.LabelU1.setText(Integer.toString(Variables.U1Contraseña));
                    System.out.println("Aumenta U1");
                }
                else
                {
                    if(Clave.LabelU2.getForeground()==Color.blue)
                    {
                        Variables.U2Contraseña++;
                        Variables.U2Contraseña=LimitaUnidad(Variables.U2Contraseña);
                        Clave.LabelU2.setText(Integer.toString(Variables.U2Contraseña));
                        System.out.println("Aumenta U2");
                    }
                    else
                    {
                        if(Clave.LabelU3.getForeground()==Color.blue)
                        {
                            Variables.U3Contraseña++;
                            Variables.U3Contraseña=LimitaUnidad(Variables.U3Contraseña);
                            Clave.LabelU3.setText(Integer.toString(Variables.U3Contraseña));
                            System.out.println("Aumenta U3");
                        }
                        else
                        {
                            if(Clave.LabelU4.getForeground()==Color.blue)
                            {
                                Variables.U4Contraseña++;
                                Variables.U4Contraseña=LimitaUnidad(Variables.U4Contraseña);
                                Clave.LabelU4.setText(Integer.toString(Variables.U4Contraseña));
                                System.out.println("Aumenta U4");
                            }
                        }
                    }
                }
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true)
            {
                TimerEx.tiempoapagado=0;
                if(Clave.LabelU1.getForeground()==Color.blue)
                {
                    Variables.U1Contraseña--;
                    Variables.U1Contraseña=LimitaUnidad(Variables.U1Contraseña);
                    Clave.LabelU1.setText(Integer.toString(Variables.U1Contraseña));
                    System.out.println("Aumenta U1");
                    
                }
                else
                {
                    if(Clave.LabelU2.getForeground()==Color.blue)
                    {
                        Variables.U2Contraseña--;
                        Variables.U2Contraseña=LimitaUnidad(Variables.U2Contraseña);
                        Clave.LabelU2.setText(Integer.toString(Variables.U2Contraseña));
                        System.out.println("Aumenta U2");
                    }
                    else
                    {
                        if(Clave.LabelU3.getForeground()==Color.blue)
                        {
                            Variables.U3Contraseña--;
                            Variables.U3Contraseña=LimitaUnidad(Variables.U3Contraseña);
                            Clave.LabelU3.setText(Integer.toString(Variables.U3Contraseña));
                            System.out.println("Aumenta U3");
                        }
                        else
                        {
                            if(Clave.LabelU4.getForeground()==Color.blue)
                            {
                                Variables.U4Contraseña--;
                                Variables.U4Contraseña=LimitaUnidad(Variables.U4Contraseña);
                                Clave.LabelU4.setText(Integer.toString(Variables.U4Contraseña));
                                System.out.println("Aumenta U4");
                            }
                        }
                    }
                }
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                TimerEx.tiempoapagado=0;
                if(Clave.LabelU4.getForeground()==Color.blue)
                {
                        if(Variables.U1Contraseña==Variables.U1ContraseñaSave &&
                            Variables.U2Contraseña==Variables.U2ContraseñaSave &&
                            Variables.U3Contraseña==Variables.U3ContraseñaSave &&   
                            Variables.U4Contraseña==Variables.U4ContraseñaSave)    
                    {
                        MsjContraOk.setVisible(true);
                        try{
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        MsjContraOk.dispose();
                        Clave.dispose();
                        Thread.sleep(200);
                        Purga.setVisible(true);
                        Clave.LabelU1.setForeground(Color.blue);
                        Clave.LabelU4.setForeground(Color.black);
                        Clave.LabelU4.setText("*");
                        Variables.Derecha=false;
                    }
                    else
                    {
                        MsjContraIn.setVisible(true);
                        try{
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        MsjContraIn.dispose();
                        Clave.LabelU1.setForeground(Color.blue);
                        Clave.LabelU4.setForeground(Color.black);
                    }
                }
                else
                {
                    if(Clave.LabelU1.getForeground()==Color.blue)
                    {
                        Clave.LabelU1.setForeground(Color.black);
                        Clave.LabelU2.setForeground(Color.blue);
                        Clave.LabelU3.setForeground(Color.black);
                        Clave.LabelU4.setForeground(Color.black);
                        Clave.LabelU1.setText("*");
                        Clave.LabelU4.setText("*");
                        Clave.LabelU3.setText("*");
                        Clave.LabelU2.setText(Integer.toString(Variables.U2Contraseña));
                    }
                    else
                    {
                        if(Clave.LabelU2.getForeground()==Color.blue)
                        {
                            Clave.LabelU1.setForeground(Color.black);
                            Clave.LabelU2.setForeground(Color.black);
                            Clave.LabelU3.setForeground(Color.blue);
                            Clave.LabelU4.setForeground(Color.black);
                            Clave.LabelU2.setText("*");
                            Clave.LabelU1.setText("*");
                            Clave.LabelU4.setText("*");
                            Clave.LabelU3.setText(Integer.toString(Variables.U3Contraseña));
                        }
                        else
                        {
                            if(Clave.LabelU3.getForeground()==Color.blue)
                            {
                                Clave.LabelU1.setForeground(Color.black);
                                Clave.LabelU2.setForeground(Color.black);
                                Clave.LabelU3.setForeground(Color.black);
                                Clave.LabelU4.setForeground(Color.blue);
                                Clave.LabelU1.setText("*");
                                Clave.LabelU2.setText("*");
                                Clave.LabelU3.setText("*");
                                Clave.LabelU4.setText(Integer.toString(Variables.U4Contraseña));
                            }
                        }
                    }
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                
                Variables.Izquierda=false;
            }
        }
    }
    
    // Falta probar teclas de objetos y agregar warnings por vidrio y flujo.
    private void PantallaPrincipalView(){
        if(!MenuPrincipal.isVisible() && !MenuConfiguracion.isVisible() && !MenuAjustes.isVisible() 
                && !MenuFunLuz.isVisible() 
                && !MenuFunLuzUV.isVisible() && !MenuFunMotor.isVisible() && !MenuAjustes.isVisible() 
                && !MenuAjUV.isVisible()
                && !MenuAjContraseña.isVisible() && !MenuAjContraseñaNueva.isVisible() 
                && !MenuAjUnidades.isVisible()
                && !MenuAjUV.isVisible() && !MenuHorometro.isVisible()
                && !MenuResetUV.isVisible() && !MenuVidaUV.isVisible()
                && !Clave.isVisible() && !Purga.isVisible() && Principal.isVisible()
                && !MenuTempoUV.isVisible() && !MenuPurga.isVisible() && !MenuPostPurga.isVisible()
                && !MenuServicio.isVisible() && !MenuSenInflow.isVisible() && !MenuSenDownflow.isVisible()
                && !MenuPresInflow.isVisible() && !MenuPresDownflow.isVisible() && !PPurga.isVisible()
                && !MenuModo.isVisible())
        {
            Variables.MotorInFlow=false;
            if(Variables.Arriba==true)
            {
                MenuPrincipal.setVisible(true);
                System.out.println("Abre Menu Principal");
                Variables.Arriba=false;
            }
            
            if(Variables.Vidrio20Luz)
            {
                if(Variables.VidrioUP==5 && Variables.VidrioDN==5)
                {
                    if(PantallaPrincipal.LuzButton.getBackground()==Color.white)
                    {
                        PantallaPrincipal.LuzButton.setBackground(Color.black);
                        Variables.LuzBlanca=false;
                    }
                    else
                    {
                        PantallaPrincipal.LuzButton.setBackground(Color.white);
                        Variables.LuzBlanca=true;
                    }
                }
            }
            else
            {
                if(Variables.TeclaLuz==true)
                {
                    if(Variables.VidrioUP==5 && Variables.VidrioDN==5)
                    {
                        if(PantallaPrincipal.LuzButton.getBackground()==Color.white)
                        {
                            PantallaPrincipal.LuzButton.setBackground(Color.black);
                            Variables.LuzBlanca=false;
                        }
                        else
                        {
                            PantallaPrincipal.LuzButton.setBackground(Color.white);
                            Variables.LuzBlanca=true;
                        }
                    }
                    Variables.TeclaLuz=false;
                }
            }
            
            if(Variables.Vidrio20Motor==true)
            {
                if(Variables.VidrioUP==5 && Variables.VidrioDN==5)
                {
                    if(PantallaPrincipal.BlowerButton.getBackground()==Color.white)
                    {
                        PantallaPrincipal.BlowerButton.setBackground(Color.black);
                        Variables.MotorDownFlow=false;
                    }
                    else
                    {
                        PantallaPrincipal.BlowerButton.setBackground(Color.white);
                        Variables.MotorDownFlow=true;
                    }
                }
            }
            else
            {
                if(Variables.TeclaMotor)
                {
                    if(Variables.VidrioUP==5 && Variables.VidrioDN==5)
                    {
                        if(PantallaPrincipal.BlowerButton.getBackground()==Color.white)
                        {
                            PantallaPrincipal.BlowerButton.setBackground(Color.black);
                            Variables.MotorDownFlow=false;
                        }
                        else
                        {
                            PantallaPrincipal.BlowerButton.setBackground(Color.white);
                            Variables.MotorDownFlow=true;
                        }
                    }
                    Variables.TeclaMotor=false;
                }
            }
            
            if(Variables.VidrioDown==true)
            {
                if(Variables.VidrioUP==10 && Variables.VidrioDN==10)
                {
                    if(PantallaPrincipal.UVButton.getBackground()==Color.white)
                    {
                        PantallaPrincipal.UVButton.setBackground(Color.black);
                        Variables.LuzUV=false;
                    }
                    else
                    {
                        PantallaPrincipal.UVButton.setBackground(Color.white);
                        Variables.LuzUV=true;
                    }
                }
            }
            else
            {
                if(Variables.TeclaUV)
                {
                    if(Variables.VidrioUP==10 && Variables.VidrioDN==10)
                    {
                        if(PantallaPrincipal.UVButton.getBackground()==Color.white)
                        {
                            PantallaPrincipal.UVButton.setBackground(Color.black);
                            Variables.LuzUV=true;
                        }
                        else
                        {
                            PantallaPrincipal.UVButton.setBackground(Color.white);
                            Variables.LuzUV=true;
                        }
                    }
                    Variables.TeclaUV=false;
                }
            }
            
            if(Variables.TeclaToma)
            {
                if(PantallaPrincipal.TomaButton.getBackground()==Color.white)
                {
                    PantallaPrincipal.TomaButton.setBackground(Color.black);
                    Variables.Toma=false;
                }
                else
                {
                    PantallaPrincipal.UVButton.setBackground(Color.white);
                    Variables.Toma=true;
                }
                Variables.TeclaToma=false;
            }
        }
    }
    
    // Menu Principal OK,
    private void MenuPrincipal() {
        if(MenuPrincipal.isVisible())
        {
            if(Variables.Arriba==true)
            {
                if(MenuPrincipal.LabelConfig.getForeground()==Color.blue)
                {
                    MenuPrincipal.LabelConfig.setForeground(Color.black);
                    MenuPrincipal.LabelAjustes.setForeground(Color.black);
                    MenuPrincipal.LabelModo.setForeground(Color.black);
                    MenuPrincipal.LabelServicio.setForeground(Color.blue);
                    System.out.println("Cambia Label Config");
                }
                else
                {
                    if(MenuPrincipal.LabelModo.getForeground()==Color.blue)
                    {
                        MenuPrincipal.LabelConfig.setForeground(Color.black);
                        MenuPrincipal.LabelAjustes.setForeground(Color.blue);
                        MenuPrincipal.LabelModo.setForeground(Color.black);
                        MenuPrincipal.LabelServicio.setForeground(Color.black);
                        System.out.println("Cambia Label Servicio");
                    }
                    else
                    {
                        if(MenuPrincipal.LabelAjustes.getForeground()==Color.blue)
                        {
                            MenuPrincipal.LabelConfig.setForeground(Color.blue);
                            MenuPrincipal.LabelAjustes.setForeground(Color.black);
                            MenuPrincipal.LabelModo.setForeground(Color.black);
                            MenuPrincipal.LabelServicio.setForeground(Color.black);
                            System.out.println("Cambia Label Ajustes");
                        }
                        else
                        {
                            if(MenuPrincipal.LabelServicio.getForeground()==Color.blue)
                            {
                                MenuPrincipal.LabelConfig.setForeground(Color.black);
                                MenuPrincipal.LabelAjustes.setForeground(Color.black);
                                MenuPrincipal.LabelModo.setForeground(Color.blue);
                                MenuPrincipal.LabelServicio.setForeground(Color.black);
                                System.out.println("Cambia Label Ajustes");
                            }
                        }
                    }
                }
                Variables.Arriba=false;
            }
                    
            if(Variables.Abajo==true)
            {
                if(MenuPrincipal.LabelConfig.getForeground()==Color.blue)
                {
                    MenuPrincipal.LabelConfig.setForeground(Color.black);
                    MenuPrincipal.LabelAjustes.setForeground(Color.blue);
                    MenuPrincipal.LabelModo.setForeground(Color.black);
                    MenuPrincipal.LabelServicio.setForeground(Color.black);
                    System.out.println("Cambia Label Config");
                }
                else
                {
                    if(MenuPrincipal.LabelModo.getForeground()==Color.blue)
                    {
                        MenuPrincipal.LabelConfig.setForeground(Color.black);
                        MenuPrincipal.LabelAjustes.setForeground(Color.black);
                        MenuPrincipal.LabelModo.setForeground(Color.black);
                        MenuPrincipal.LabelServicio.setForeground(Color.blue);
                        System.out.println("Cambia Label Servicio");
                    }
                    else
                    {
                        if(MenuPrincipal.LabelAjustes.getForeground()==Color.blue)
                        {
                            MenuPrincipal.LabelConfig.setForeground(Color.black);
                            MenuPrincipal.LabelAjustes.setForeground(Color.black);
                            MenuPrincipal.LabelModo.setForeground(Color.blue);
                            MenuPrincipal.LabelServicio.setForeground(Color.black);
                            System.out.println("Cambia Label Ajustes");
                        }
                        else
                        {
                            if(MenuPrincipal.LabelServicio.getForeground()==Color.blue)
                            {
                                MenuPrincipal.LabelConfig.setForeground(Color.blue);
                                MenuPrincipal.LabelAjustes.setForeground(Color.black);
                                MenuPrincipal.LabelModo.setForeground(Color.black);
                                MenuPrincipal.LabelServicio.setForeground(Color.black);
                                System.out.println("Cambia Label Ajustes");
                            }
                        }
                    }
                }
                Variables.Abajo=false;
            }
                
            if(Variables.Derecha==true)
            {
                if(MenuPrincipal.LabelConfig.getForeground()==Color.blue)
                {
                    MenuPrincipal.dispose();
                    MenuConfiguracion.setVisible(true);
                    System.out.println("Entro Menu Config");
                }
                else
                {
                    if(MenuPrincipal.LabelServicio.getForeground()==Color.blue)
                    {
                        MenuPrincipal.dispose();
                        MenuServicio.setVisible(true);
                        System.out.println("Entro Menu Config");
                    }
                    else
                    {
                        if(MenuPrincipal.LabelAjustes.getForeground()==Color.blue)
                        {
                            MenuPrincipal.dispose();
                            MenuAjustes.setVisible(true);
                            System.out.println("Entro Menu Servicio");
                        }
                        else
                        {
                            if(MenuPrincipal.LabelModo.getForeground()==Color.blue)
                            {
                                MenuPrincipal.dispose();
                                MenuModo.setVisible(true);
                                System.out.println("Entro Menu Servicio");
                            }
                        }
                    }
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuPrincipal.dispose();
                Variables.Izquierda=false;
            }
        }
    }

    // OK
    private void MenuConfiguraciones() {
        if(MenuConfiguracion.isVisible())
        {
            if(Variables.Arriba==true)
            {
                if(MenuConfiguracion.LabelFuncionamiento.getForeground()==Color.blue)
                {
                    MenuConfiguracion.LabelFuncionamiento.setForeground(Color.black);
                    MenuConfiguracion.LabelTempoUV.setForeground(Color.black);
                    MenuConfiguracion.LabelPurga.setForeground(Color.black);
                    MenuConfiguracion.LabelPostPurga.setForeground(Color.blue);
                    System.out.println("Cambia Label Funcionamiento");
                }
                else
                {
                    if(MenuConfiguracion.LabelTempoUV.getForeground()==Color.blue)
                    {
                        MenuConfiguracion.LabelFuncionamiento.setForeground(Color.blue);
                        MenuConfiguracion.LabelTempoUV.setForeground(Color.black);
                        MenuConfiguracion.LabelPurga.setForeground(Color.black);
                        MenuConfiguracion.LabelPostPurga.setForeground(Color.black);
                        System.out.println("Cambia Label TempoUV");
                    }
                    else
                    {
                        if(MenuConfiguracion.LabelPurga.getForeground()==Color.blue)
                        {
                            MenuConfiguracion.LabelFuncionamiento.setForeground(Color.black);
                            MenuConfiguracion.LabelTempoUV.setForeground(Color.blue);
                            MenuConfiguracion.LabelPurga.setForeground(Color.black);
                            MenuConfiguracion.LabelPostPurga.setForeground(Color.black);
                            System.out.println("Cambia Label Purga");
                        }
                        else
                        {
                            if(MenuConfiguracion.LabelPostPurga.getForeground()==Color.blue)
                            {
                                MenuConfiguracion.LabelFuncionamiento.setForeground(Color.black);
                                MenuConfiguracion.LabelTempoUV.setForeground(Color.black);
                                MenuConfiguracion.LabelPurga.setForeground(Color.blue);
                                MenuConfiguracion.LabelPostPurga.setForeground(Color.black);
                                System.out.println("Cambia Label PostPurga");
                            }
                        }
                    }
                }
                Variables.Arriba=false;
            }
                    
            if(Variables.Abajo==true)
            {
                if(MenuConfiguracion.LabelFuncionamiento.getForeground()==Color.blue)
                {
                    Menu_Configuracion.LabelFuncionamiento.setForeground(Color.black);
                    MenuConfiguracion.LabelTempoUV.setForeground(Color.blue);
                    MenuConfiguracion.LabelPurga.setForeground(Color.black);
                    MenuConfiguracion.LabelPostPurga.setForeground(Color.black);
                    System.out.println("Cambia Label Funcionamiento");
                }
                else
                {
                    if(MenuConfiguracion.LabelTempoUV.getForeground()==Color.blue)
                    {
                        MenuConfiguracion.LabelFuncionamiento.setForeground(Color.black);
                        MenuConfiguracion.LabelTempoUV.setForeground(Color.black);
                        MenuConfiguracion.LabelPurga.setForeground(Color.blue);
                        MenuConfiguracion.LabelPostPurga.setForeground(Color.black);
                        System.out.println("Cambia Label Hora");
                    }
                    else
                    {
                        if(MenuConfiguracion.LabelPurga.getForeground()==Color.blue)
                        {
                            MenuConfiguracion.LabelFuncionamiento.setForeground(Color.black);
                            MenuConfiguracion.LabelTempoUV.setForeground(Color.black);
                            MenuConfiguracion.LabelPurga.setForeground(Color.black);
                            MenuConfiguracion.LabelPostPurga.setForeground(Color.blue);
                            System.out.println("Cambia Label Purga");
                        }
                        else
                        {
                            if(MenuConfiguracion.LabelPostPurga.getForeground()==Color.blue)
                            {
                                MenuConfiguracion.LabelFuncionamiento.setForeground(Color.blue);
                                MenuConfiguracion.LabelTempoUV.setForeground(Color.black);
                                MenuConfiguracion.LabelPurga.setForeground(Color.black);
                                MenuConfiguracion.LabelPostPurga.setForeground(Color.black);
                                System.out.println("Cambia Label PostPurga");
                            }
                        }
                    }
                }
                Variables.Abajo=false;
            }
                
            if(Variables.Derecha==true)
            {
                if(MenuConfiguracion.LabelFuncionamiento.getForeground()==Color.blue)
                {
                    MenuConfiguracion.dispose();
                    MenuFunLuz.setVisible(true);
                    System.out.println("Entro Menu Funcionamiento");
                }
                else
                {
                    if(MenuConfiguracion.LabelTempoUV.getForeground()==Color.blue)
                    {
                        MenuConfiguracion.dispose();
                        MenuTempoUV.setVisible(true);
                        System.out.println("Entro Menu TempoUV");
                        MenuTempoUV.LabelMinutos.setText(String.format("%02d",Variables.UVMinutos));
                        MenuTempoUV.LabelSegundos.setText(String.format("%02d",Variables.UVSegundos));
                    }
                    else
                    {
                        if(MenuConfiguracion.LabelPurga.getForeground()==Color.blue)
                        {
                            MenuConfiguracion.dispose();
                            MenuPurga.setVisible(true);
                            System.out.println("Entro Menu Purga");
                            MenuPurga.LabelMinutos.setText(String.format("%02d",Variables.PurgaMinutos));
                            MenuPurga.LabelSegundos.setText(String.format("%02d",Variables.PurgaSegundos));
                        }
                        else
                        {
                            if(MenuConfiguracion.LabelPostPurga.getForeground()==Color.blue)
                            {
                                MenuConfiguracion.dispose();
                                MenuPostPurga.setVisible(true);
                                System.out.println("Entro Menu PostPurga");
                                MenuPostPurga.LabelMinutos.setText(String.format("%02d",Variables.PPurgaMinutos));
                                MenuPostPurga.LabelSegundos.setText(String.format("%02d",Variables.PPurgaSegundos));
                            }
                        }
                    }
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuConfiguracion.dispose();
                Variables.Izquierda=false;
            }
        }
    }

    // OK
    private void MenuFuncionamientoLuz() {
        if(MenuFunLuz.isVisible())
        {
            if(Variables.Arriba==true || Variables.Abajo==true)
            {
                if(MenuFunLuz.LabelEncendera.getForeground()==Color.blue)
                {
                    Variables.Vidrio20Luz=false;
                    MenuFunLuz.LabelEncendera.setForeground(Color.black);
                    MenuFunLuz.LabelNada.setForeground(Color.blue);
                    System.out.println("Cambia Label Encendera");
                }
                else
                {
                    if(MenuFunLuz.LabelNada.getForeground()==Color.blue)
                    {
                        Variables.Vidrio20Luz=true;
                        MenuFunLuz.LabelEncendera.setForeground(Color.blue);
                        MenuFunLuz.LabelNada.setForeground(Color.black);
                        System.out.println("Cambia Label Nada");
                    }
                }
                Variables.Arriba=false;
                Variables.Abajo=false;
            }
            
            
            
            if(Variables.Derecha==true)
            {
                MenuFunLuz.dispose();
                MenuFunMotor.setVisible(true);
                System.out.println("Abre Menu Motor");
                GuardaConfig();
                Variables.Derecha=false;
            }
        }
    }

    // OK
    private void MenuFuncionamientoMotor() {
        if(MenuFunMotor.isVisible())
        {
            if(Variables.Arriba==true || Variables.Abajo==true)
            {
                if(MenuFunMotor.LabelEncendera.getForeground()==Color.blue)
                {
                    Variables.Vidrio20Motor=false;
                    MenuFunMotor.LabelEncendera.setForeground(Color.black);
                    MenuFunMotor.LabelNada.setForeground(Color.blue);
                    System.out.println("Cambia Label Encendera");
                }
                else
                {
                    if(MenuFunMotor.LabelNada.getForeground()==Color.blue)
                    {
                        Variables.Vidrio20Motor=true;
                        MenuFunMotor.LabelEncendera.setForeground(Color.blue);
                        MenuFunMotor.LabelNada.setForeground(Color.black);
                        System.out.println("Cambia Label Nada");
                    }
                }
                
                Variables.Arriba=false;
                Variables.Abajo=false;
            }
            
            
            if(Variables.Derecha==true)
            {
                MenuFunMotor.dispose();
                MenuFunLuzUV.setVisible(true);
                System.out.println("Abre Menu UV");
                GuardaConfig();
                Variables.Derecha=false;
            }
        }
    }

    // OK
    private void MenuFuncionamientoLuzUV() {
        if(MenuFunLuzUV.isVisible())
        {
            if(Variables.Arriba==true || Variables.Abajo==true)
            {
                if(MenuFunLuzUV.LabelEnciende.getForeground()==Color.blue)
                {
                    Variables.VidrioDown=false;
                    MenuFunLuzUV.LabelEnciende.setForeground(Color.black);
                    MenuFunLuzUV.LabelNada.setForeground(Color.blue);
                    System.out.println("Cambia Label Encendera");
                }
                else
                {
                    if(MenuFunLuz.LabelNada.getForeground()==Color.blue)
                    {
                        Variables.VidrioDown=true;
                        MenuFunLuzUV.LabelEnciende.setForeground(Color.blue);
                        MenuFunLuzUV.LabelNada.setForeground(Color.black);
                        System.out.println("Cambia Label Nada");
                    }
                }
                
                Variables.Arriba=false;
                Variables.Abajo=false;
            }
            
            
            if(Variables.Derecha==true)
            {
                MenuFunLuzUV.dispose();
                MenuPrincipal.setVisible(true);
                System.out.println("Abre Menu Principal");
                GuardaConfig();
                Variables.Derecha=false;
            }
        }
    }

    // OK
    private void MenuTempoUV() {
        if(MenuTempoUV.isVisible())
        {   
            if(Variables.Arriba==true && MenuTempoUV.LabelMinutos.getForeground()==Color.blue)
            {
                Variables.UVMinutos++;
                Variables.UVMinutos=LimitaTiempo(Variables.UVMinutos);
                MenuTempoUV.LabelMinutos.setText(String.format("%02d",Variables.UVMinutos));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true && MenuTempoUV.LabelMinutos.getForeground()==Color.blue)
            {
                Variables.UVMinutos--;
                Variables.UVMinutos=LimitaTiempo(Variables.UVMinutos);
                MenuTempoUV.LabelMinutos.setText(String.format("%02d",Variables.UVMinutos));
                Variables.Abajo=false;
            }
            
            if(Variables.Arriba==true && MenuTempoUV.LabelSegundos.getForeground()==Color.blue)
            {
                Variables.UVSegundos++;
                Variables.UVSegundos=LimitaTiempo(Variables.UVSegundos);
                MenuTempoUV.LabelSegundos.setText(String.format("%02d",Variables.UVSegundos));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true && MenuTempoUV.LabelSegundos.getForeground()==Color.blue)
            {
                Variables.UVSegundos--;
                Variables.UVSegundos=LimitaTiempo(Variables.UVSegundos);
                MenuTempoUV.LabelSegundos.setText(String.format("%02d",Variables.UVSegundos));
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                if(MenuTempoUV.LabelMinutos.getForeground()==Color.blue)
                {
                    MenuTempoUV.LabelMinutos.setForeground(Color.black);
                    MenuTempoUV.LabelSegundos.setForeground(Color.blue);
                }
                else
                {
                    MenuTempoUV.LabelMinutos.setForeground(Color.blue);
                    MenuTempoUV.LabelSegundos.setForeground(Color.black);
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuTempoUV.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
                Variables.Izquierda=false;
            }
        }
    }
    
    // OK Limitar Tiempo
    private void MenuPurga() {
        if(MenuPurga.isVisible())
        {   
            if(Variables.Arriba==true && MenuPurga.LabelMinutos.getForeground()==Color.blue)
            {
                Variables.PurgaMinutos++;
                Variables.PurgaMinutos=LimitaTiempo(Variables.PurgaMinutos);
                MenuPurga.LabelMinutos.setText(String.format("%02d",Variables.PurgaMinutos));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true && MenuPurga.LabelMinutos.getForeground()==Color.blue)
            {
                Variables.PurgaMinutos--;
                Variables.PurgaMinutos=LimitaTiempo(Variables.PurgaMinutos);
                MenuPurga.LabelMinutos.setText(String.format("%02d",Variables.PurgaMinutos));
                Variables.Abajo=false;
            }
            
            if(Variables.Arriba==true && MenuPurga.LabelSegundos.getForeground()==Color.blue)
            {
                Variables.PurgaSegundos++;
                Variables.PurgaSegundos=LimitaTiempo(Variables.PurgaSegundos);
                MenuPurga.LabelSegundos.setText(String.format("%02d",Variables.PurgaSegundos));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true && MenuPurga.LabelSegundos.getForeground()==Color.blue)
            {
                Variables.PurgaSegundos--;
                Variables.PurgaSegundos=LimitaTiempo(Variables.PurgaSegundos);
                MenuPurga.LabelSegundos.setText(String.format("%02d",Variables.PurgaSegundos));
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                if(MenuPurga.LabelMinutos.getForeground()==Color.blue)
                {
                    MenuPurga.LabelMinutos.setForeground(Color.black);
                    MenuPurga.LabelSegundos.setForeground(Color.blue);
                }
                else
                {
                    MenuPurga.LabelMinutos.setForeground(Color.blue);
                    MenuPurga.LabelSegundos.setForeground(Color.black);
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuPurga.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
                Variables.Izquierda=false;
            }
        }
    }

    // OK Limitar Tiempo
    private void MenuPostPurga() {
        if(MenuPostPurga.isVisible())
        {
            if(Variables.Arriba==true && MenuPostPurga.LabelMinutos.getForeground()==Color.blue)
            {
                Variables.PPurgaMinutos++;
                MenuPostPurga.LabelMinutos.setText(String.format("%02d",Variables.PPurgaMinutos));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true && MenuPostPurga.LabelMinutos.getForeground()==Color.blue)
            {
                Variables.PPurgaMinutos--;
                MenuPostPurga.LabelMinutos.setText(String.format("%02d",Variables.PPurgaMinutos));
                Variables.Abajo=false;
            }
            
            if(Variables.Arriba==true && MenuPostPurga.LabelSegundos.getForeground()==Color.blue)
            {
                Variables.PPurgaSegundos++;
                MenuPostPurga.LabelSegundos.setText(String.format("%02d",Variables.PPurgaSegundos));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true && MenuPostPurga.LabelSegundos.getForeground()==Color.blue)
            {
                Variables.PPurgaSegundos--;
                MenuPostPurga.LabelSegundos.setText(String.format("%02d",Variables.PPurgaSegundos));
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                if(MenuPostPurga.LabelMinutos.getForeground()==Color.blue)
                {
                    MenuPostPurga.LabelMinutos.setForeground(Color.black);
                    MenuPostPurga.LabelSegundos.setForeground(Color.blue);
                }
                else
                {
                    MenuPostPurga.LabelMinutos.setForeground(Color.blue);
                    MenuPostPurga.LabelSegundos.setForeground(Color.black);
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuPostPurga.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
                Variables.Izquierda=false;
            }
        }
    }

    // OK
    private void MenuAjustes() {
        if(MenuAjustes.isVisible())
        {
            if(Variables.Arriba==true)
            {
                if(MenuAjustes.LabelUnidades.getForeground()==Color.blue)
                {
                    MenuAjustes.LabelUnidades.setForeground(Color.black);
                    MenuAjustes.LabelContraseña.setForeground(Color.black);
                    MenuAjustes.LabelAjustesUV.setForeground(Color.blue);
                    System.out.println("Cambia Label Unidades");
                }
                else
                {
                    if(MenuAjustes.LabelContraseña.getForeground()==Color.blue)
                    {
                        MenuAjustes.LabelUnidades.setForeground(Color.blue);
                        MenuAjustes.LabelContraseña.setForeground(Color.black);
                        MenuAjustes.LabelAjustesUV.setForeground(Color.black);
                        System.out.println("Cambia Label Contraseña");
                    }
                    else
                    {
                        if(MenuAjustes.LabelAjustesUV.getForeground()==Color.blue)
                        {
                            MenuAjustes.LabelUnidades.setForeground(Color.black);
                            MenuAjustes.LabelContraseña.setForeground(Color.blue);
                            MenuAjustes.LabelAjustesUV.setForeground(Color.black);
                            System.out.println("Cambia Label Ajustes UV");
                        }
                    }
                }
                Variables.Arriba=false;
            }
                    
            if(Variables.Abajo==true)
            {
                if(MenuAjustes.LabelUnidades.getForeground()==Color.blue)
                {
                    MenuAjustes.LabelUnidades.setForeground(Color.black);
                    MenuAjustes.LabelContraseña.setForeground(Color.blue);
                    MenuAjustes.LabelAjustesUV.setForeground(Color.black);
                    System.out.println("Cambia Label Unidades");
                }
                else
                {
                    if(MenuAjustes.LabelContraseña.getForeground()==Color.blue)
                    {
                        MenuAjustes.LabelUnidades.setForeground(Color.black);
                        MenuAjustes.LabelContraseña.setForeground(Color.black);
                        MenuAjustes.LabelAjustesUV.setForeground(Color.blue);
                        System.out.println("Cambia Label Contraseña");
                    }
                    else
                    {
                        if(MenuAjustes.LabelAjustesUV.getForeground()==Color.blue)
                        {
                            MenuAjustes.LabelUnidades.setForeground(Color.blue);
                            MenuAjustes.LabelContraseña.setForeground(Color.black);
                            MenuAjustes.LabelAjustesUV.setForeground(Color.black);
                            System.out.println("Cambia Label Ajustes UV");
                        }
                    }
                }
                Variables.Abajo=false;
            }
                
            if(Variables.Derecha==true)
            {
                if(MenuAjustes.LabelUnidades.getForeground()==Color.blue)
                {
                    MenuAjustes.dispose();
                    MenuAjUnidades.setVisible(true);
                    System.out.println("Cambia Menu Unidades");
                }
                else
                {
                    if(MenuAjustes.LabelContraseña.getForeground()==Color.blue)
                    {
                        MenuAjustes.dispose();
                        MenuAjContraseña.setVisible(true);
                        System.out.println("Cambia Menu Contraseña");
                    }
                    else
                    {
                        if(MenuAjustes.LabelAjustesUV.getForeground()==Color.blue)
                        {
                            MenuAjustes.dispose();
                            MenuAjUV.setVisible(true);
                            System.out.println("Cambia Label Ajustes UV");
                        }
                    }
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuAjustes.dispose();
                MenuPrincipal.setVisible(true);
                Variables.Izquierda=false;
            }
        }
    }

    // OK
    private void MenuAjusteUnidades() {
        if(MenuAjUnidades.isVisible())
        {
            if(Variables.Arriba==true || Variables.Abajo==true)
            {
                if(MenuAjUnidades.LabelImperial.getForeground()==Color.blue)
                {
                    Variables.Imperial=false;
                    Variables.Metrico=true;
                    MenuAjUnidades.LabelImperial.setForeground(Color.black);
                    MenuAjUnidades.LabelMetrica.setForeground(Color.blue);
                    System.out.println("Cambia Label Imperial");
                }
                else
                {
                    if(MenuAjUnidades.LabelMetrica.getForeground()==Color.blue)
                    {
                        Variables.Imperial=true;
                        Variables.Metrico=false;
                        MenuAjUnidades.LabelImperial.setForeground(Color.blue);
                        MenuAjUnidades.LabelMetrica.setForeground(Color.black);
                        System.out.println("Cambia Label Metrica");
                    }
                }
                Variables.Arriba=false;
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                if(MenuAjUnidades.LabelImperial.getForeground()==Color.blue)
                {
                    Variables.Imperial=true;
                    Variables.Metrico=false;
                }
                
                if(MenuAjUnidades.LabelMetrica.getForeground()==Color.blue)
                {
                    Variables.Imperial=false;
                    Variables.Metrico=true;
                }
                MenuAjUnidades.dispose();
                MenuPrincipal.setVisible(true);
                System.out.println("Abre Menu Principal");
                GuardaConfig();
                Variables.Derecha=false;
            }
        }
    }
    
    // OK
    private void MenuContraseña() {
        if(MenuAjContraseña.isVisible())
        {
            if(MenuAjContraseña.LabelU1.getForeground()==Color.blue)
                MenuAjContraseña.LabelU1.setText(Integer.toString(Variables.U1Contraseña));
               
            if(Variables.Arriba==true)
            {
                if(MenuAjContraseña.LabelU1.getForeground()==Color.blue)
                {
                    Variables.U1Contraseña++;
                    Variables.U1Contraseña=LimitaUnidad(Variables.U1Contraseña);
                    MenuAjContraseña.LabelU1.setText(Integer.toString(Variables.U1Contraseña));
                    System.out.println("Aumenta U1");
                }
                else
                {
                    if(MenuAjContraseña.LabelU2.getForeground()==Color.blue)
                    {
                        Variables.U2Contraseña++;
                        Variables.U2Contraseña=LimitaUnidad(Variables.U2Contraseña);
                        MenuAjContraseña.LabelU2.setText(Integer.toString(Variables.U2Contraseña));
                        System.out.println("Aumenta U2");
                    }
                    else
                    {
                        if(MenuAjContraseña.LabelU3.getForeground()==Color.blue)
                        {
                            Variables.U3Contraseña++;
                            Variables.U3Contraseña=LimitaUnidad(Variables.U3Contraseña);
                            MenuAjContraseña.LabelU3.setText(Integer.toString(Variables.U3Contraseña));
                            System.out.println("Aumenta U3");
                        }
                        else
                        {
                            if(MenuAjContraseña.LabelU4.getForeground()==Color.blue)
                            {
                                Variables.U4Contraseña++;
                                Variables.U4Contraseña=LimitaUnidad(Variables.U4Contraseña);
                                MenuAjContraseña.LabelU4.setText(Integer.toString(Variables.U4Contraseña));
                                System.out.println("Aumenta U4");
                            }
                        }
                    }
                }
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true)
            {
                if(MenuAjContraseña.LabelU1.getForeground()==Color.blue)
                {
                    Variables.U1Contraseña--;
                    Variables.U1Contraseña=LimitaUnidad(Variables.U1Contraseña);
                    MenuAjContraseña.LabelU1.setText(Integer.toString(Variables.U1Contraseña));
                    System.out.println("Disminuye U1");
                    
                }
                else
                {
                    if(MenuAjContraseña.LabelU2.getForeground()==Color.blue)
                    {
                        Variables.U2Contraseña--;
                        Variables.U2Contraseña=LimitaUnidad(Variables.U2Contraseña);
                        MenuAjContraseña.LabelU2.setText(Integer.toString(Variables.U2Contraseña));
                        System.out.println("Disminuye U2");
                    }
                    else
                    {
                        if(MenuAjContraseña.LabelU3.getForeground()==Color.blue)
                        {
                            Variables.U3Contraseña--;
                            Variables.U3Contraseña=LimitaUnidad(Variables.U3Contraseña);
                            MenuAjContraseña.LabelU3.setText(Integer.toString(Variables.U3Contraseña));
                            System.out.println("Disminuye U3");
                        }
                        else
                        {
                            if(MenuAjContraseña.LabelU4.getForeground()==Color.blue)
                            {
                                Variables.U4Contraseña--;
                                Variables.U4Contraseña=LimitaUnidad(Variables.U4Contraseña);
                                MenuAjContraseña.LabelU4.setText(Integer.toString(Variables.U4Contraseña));
                                System.out.println("Disminuye U4");
                            }
                        }
                    }
                }
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                if(MenuAjContraseña.LabelU4.getForeground()==Color.blue)
                {
                        if(Variables.U1Contraseña==Variables.U1ContraseñaSave &&
                            Variables.U2Contraseña==Variables.U2ContraseñaSave &&
                            Variables.U3Contraseña==Variables.U3ContraseñaSave &&   
                            Variables.U4Contraseña==Variables.U4ContraseñaSave)    
                    {
                        MsjContraOk.setVisible(true);
                        try{
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        MsjContraOk.dispose();
                        MenuAjContraseña.dispose();
                        MenuAjContraseñaNueva.setVisible(true);
                        MenuAjContraseña.LabelU1.setForeground(Color.blue);
                        MenuAjContraseña.LabelU4.setForeground(Color.black);
                        MenuAjContraseña.LabelU4.setText("*");
                        Variables.Derecha=false;
                    }
                    else
                    {
                        MsjContraIn.setVisible(true);
                        try{
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        MsjContraIn.dispose();
                        MenuAjContraseña.LabelU1.setForeground(Color.blue);
                        MenuAjContraseña.LabelU4.setForeground(Color.black);
                        MenuAjContraseña.LabelU4.setText("*");
                        Variables.Derecha=false;
                    }
                }
                else
                {
                    if(MenuAjContraseña.LabelU1.getForeground()==Color.blue)
                    {
                        MenuAjContraseña.LabelU1.setForeground(Color.black);
                        MenuAjContraseña.LabelU2.setForeground(Color.blue);
                        MenuAjContraseña.LabelU3.setForeground(Color.black);
                        MenuAjContraseña.LabelU4.setForeground(Color.black);
                        MenuAjContraseña.LabelU1.setText("*");
                        MenuAjContraseña.LabelU4.setText("*");
                        MenuAjContraseña.LabelU3.setText("*");
                        MenuAjContraseña.LabelU2.setText(Integer.toString(Variables.U2Contraseña));
                    }
                    else
                    {
                        if(MenuAjContraseña.LabelU2.getForeground()==Color.blue)
                        {
                            MenuAjContraseña.LabelU1.setForeground(Color.black);
                            MenuAjContraseña.LabelU2.setForeground(Color.black);
                            MenuAjContraseña.LabelU3.setForeground(Color.blue);
                            MenuAjContraseña.LabelU4.setForeground(Color.black);
                            MenuAjContraseña.LabelU2.setText("*");
                            MenuAjContraseña.LabelU1.setText("*");
                            MenuAjContraseña.LabelU4.setText("*");
                            MenuAjContraseña.LabelU3.setText(Integer.toString(Variables.U3Contraseña));
                        }
                        else
                        {
                            if(MenuAjContraseña.LabelU3.getForeground()==Color.blue)
                            {
                                MenuAjContraseña.LabelU1.setForeground(Color.black);
                                MenuAjContraseña.LabelU2.setForeground(Color.black);
                                MenuAjContraseña.LabelU3.setForeground(Color.black);
                                MenuAjContraseña.LabelU4.setForeground(Color.blue);
                                MenuAjContraseña.LabelU1.setText("*");
                                MenuAjContraseña.LabelU2.setText("*");
                                MenuAjContraseña.LabelU3.setText("*");
                                MenuAjContraseña.LabelU4.setText(Integer.toString(Variables.U4Contraseña));
                            }
                        }
                    }
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuAjContraseña.dispose();
                MenuPrincipal.setVisible(true);
                Variables.Izquierda=false;
            }
        }
    }
    
    // OK
    private void MenuContraseñaNueva() {
        if(MenuAjContraseñaNueva.isVisible())
        {
            if(MenuAjContraseñaNueva.LabelU1.getForeground()==Color.blue)
                MenuAjContraseñaNueva.LabelU1.setText(Integer.toString(Variables.U1ContraseñaNueva));
            
            if(Variables.Arriba==true)
            {
                if(MenuAjContraseñaNueva.LabelU1.getForeground()==Color.blue)
                {
                    Variables.U1ContraseñaNueva++;
                    Variables.U1ContraseñaNueva=LimitaUnidad(Variables.U1ContraseñaNueva);
                    MenuAjContraseñaNueva.LabelU1.setText(Integer.toString(Variables.U1ContraseñaNueva));
                    System.out.println("Aumenta U1");
                }
                else
                {
                    if(MenuAjContraseñaNueva.LabelU2.getForeground()==Color.blue)
                    {
                        Variables.U2ContraseñaNueva++;
                        Variables.U2ContraseñaNueva=LimitaUnidad(Variables.U2ContraseñaNueva);
                        MenuAjContraseñaNueva.LabelU2.setText(Integer.toString(Variables.U2ContraseñaNueva));
                        System.out.println("Aumenta U2");
                    }
                    else
                    {
                        if(MenuAjContraseñaNueva.LabelU3.getForeground()==Color.blue)
                        {
                            Variables.U3ContraseñaNueva++;
                            Variables.U3ContraseñaNueva=LimitaUnidad(Variables.U3ContraseñaNueva);
                            MenuAjContraseñaNueva.LabelU3.setText(Integer.toString(Variables.U3ContraseñaNueva));
                            System.out.println("Aumenta U3");
                        }
                        else
                        {
                            if(MenuAjContraseñaNueva.LabelU4.getForeground()==Color.blue)
                            {
                                Variables.U4ContraseñaNueva++;
                                Variables.U4ContraseñaNueva=LimitaUnidad(Variables.U4ContraseñaNueva);
                                MenuAjContraseñaNueva.LabelU4.setText(Integer.toString(Variables.U4ContraseñaNueva));
                                System.out.println("Aumenta U4");
                            }
                        }
                    }
                }
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true)
            {
                if(MenuAjContraseñaNueva.LabelU1.getForeground()==Color.blue)
                {
                    Variables.U1ContraseñaNueva--;
                    Variables.U1ContraseñaNueva=LimitaUnidad(Variables.U1ContraseñaNueva);
                    MenuAjContraseñaNueva.LabelU1.setText(Integer.toString(Variables.U1ContraseñaNueva));
                    System.out.println("Disminuye U1");
                }
                else
                {
                    if(MenuAjContraseñaNueva.LabelU2.getForeground()==Color.blue)
                    {
                        Variables.U2ContraseñaNueva--;
                        Variables.U2ContraseñaNueva=LimitaUnidad(Variables.U2ContraseñaNueva);
                        MenuAjContraseñaNueva.LabelU2.setText(Integer.toString(Variables.U2ContraseñaNueva));
                        System.out.println("Disminuye U2");
                    }
                    else
                    {
                        if(MenuAjContraseñaNueva.LabelU3.getForeground()==Color.blue)
                        {
                            Variables.U3ContraseñaNueva--;
                            Variables.U3ContraseñaNueva=LimitaUnidad(Variables.U3ContraseñaNueva);
                            MenuAjContraseñaNueva.LabelU3.setText(Integer.toString(Variables.U3ContraseñaNueva));
                            System.out.println("Disminuye U3");
                        }
                        else
                        {
                            if(MenuAjContraseñaNueva.LabelU4.getForeground()==Color.blue)
                            {
                                Variables.U4ContraseñaNueva--;
                                Variables.U4ContraseñaNueva=LimitaUnidad(Variables.U4ContraseñaNueva);
                                MenuAjContraseñaNueva.LabelU4.setText(Integer.toString(Variables.U4ContraseñaNueva));
                                System.out.println("Disminuye U4");
                            }
                        }
                    }
                }
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                if(MenuAjContraseñaNueva.LabelU4.getForeground()==Color.blue)
                {
                    
                        MsjContraAl.setVisible(true);
                        try{
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        MsjContraAl.dispose();
                        MenuAjContraseñaNueva.dispose();
                        MenuPrincipal.setVisible(true);
                        Variables.Derecha=false;
                        MenuAjContraseñaNueva.LabelU4.setText("*");
                        MenuAjContraseñaNueva.LabelU1.setForeground(Color.blue);
                        MenuAjContraseñaNueva.LabelU4.setForeground(Color.black);
                        Variables.U1ContraseñaSave=Variables.U1ContraseñaNueva;
                        Variables.U2ContraseñaSave=Variables.U2ContraseñaNueva;
                        Variables.U3ContraseñaSave=Variables.U3ContraseñaNueva;
                        Variables.U4ContraseñaSave=Variables.U4ContraseñaNueva;
                        GuardaConfig();
                   
                }
                else
                {
                    if(MenuAjContraseñaNueva.LabelU1.getForeground()==Color.blue)
                    {
                        MenuAjContraseñaNueva.LabelU1.setForeground(Color.black);
                        MenuAjContraseñaNueva.LabelU2.setForeground(Color.blue);
                        MenuAjContraseñaNueva.LabelU3.setForeground(Color.black);
                        MenuAjContraseñaNueva.LabelU4.setForeground(Color.black);
                        MenuAjContraseñaNueva.LabelU1.setText("*");
                        MenuAjContraseñaNueva.LabelU3.setText("*");
                        MenuAjContraseñaNueva.LabelU4.setText("*");
                        MenuAjContraseñaNueva.LabelU2.setText(Integer.toString(Variables.U2ContraseñaNueva));
                    }
                    else
                    {
                        if(MenuAjContraseñaNueva.LabelU2.getForeground()==Color.blue)
                        {
                            MenuAjContraseñaNueva.LabelU1.setForeground(Color.black);
                            MenuAjContraseñaNueva.LabelU2.setForeground(Color.black);
                            MenuAjContraseñaNueva.LabelU3.setForeground(Color.blue);
                            MenuAjContraseñaNueva.LabelU4.setForeground(Color.black);
                            MenuAjContraseñaNueva.LabelU1.setText("*");
                            MenuAjContraseñaNueva.LabelU2.setText("*");
                            MenuAjContraseñaNueva.LabelU4.setText("*");
                            MenuAjContraseñaNueva.LabelU3.setText(Integer.toString(Variables.U3ContraseñaNueva));
                        }
                        else
                        {
                            if(MenuAjContraseñaNueva.LabelU3.getForeground()==Color.blue)
                            {
                                MenuAjContraseñaNueva.LabelU1.setForeground(Color.black);
                                MenuAjContraseñaNueva.LabelU2.setForeground(Color.black);
                                MenuAjContraseñaNueva.LabelU3.setForeground(Color.black);
                                MenuAjContraseñaNueva.LabelU4.setForeground(Color.blue);
                                MenuAjContraseñaNueva.LabelU1.setText("*");
                                MenuAjContraseñaNueva.LabelU2.setText("*");
                                MenuAjContraseñaNueva.LabelU3.setText("*");
                                MenuAjContraseñaNueva.LabelU4.setText(Integer.toString(Variables.U4ContraseñaNueva));
                            }
                        }
                    }
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuAjContraseñaNueva.dispose();
                MenuPrincipal.setVisible(true);
                Variables.Izquierda=false;
            }
        }
    }

    // OK
    private void MenuAjustesUV() {
        if(MenuAjUV.isVisible())
        {
            if(Variables.Arriba==true)
            {
                if(MenuAjUV.LabelHorometro.getForeground()==Color.blue)
                {
                    MenuAjUV.LabelHorometro.setForeground(Color.black);
                    MenuAjUV.LabelResetUV.setForeground(Color.black);
                    MenuAjUV.LabelVidaUV.setForeground(Color.blue);
                }
                else
                {
                    if(MenuAjUV.LabelVidaUV.getForeground()==Color.blue)
                    {
                        MenuAjUV.LabelHorometro.setForeground(Color.black);
                        MenuAjUV.LabelResetUV.setForeground(Color.blue);
                        MenuAjUV.LabelVidaUV.setForeground(Color.black);
                    }
                    else
                    {
                        if(MenuAjUV.LabelResetUV.getForeground()==Color.blue)
                        {
                            MenuAjUV.LabelHorometro.setForeground(Color.blue);
                            MenuAjUV.LabelResetUV.setForeground(Color.black);
                            MenuAjUV.LabelVidaUV.setForeground(Color.black);
                        }
                    }
                }
                Variables.Arriba=false;
            }
                    
            if(Variables.Abajo==true)
            {
                if(MenuAjUV.LabelHorometro.getForeground()==Color.blue)
                {
                    MenuAjUV.LabelHorometro.setForeground(Color.black);
                    MenuAjUV.LabelResetUV.setForeground(Color.blue);
                    MenuAjUV.LabelVidaUV.setForeground(Color.black);
                }
                else
                {
                    if(MenuAjUV.LabelResetUV.getForeground()==Color.blue)
                    {
                        MenuAjUV.LabelHorometro.setForeground(Color.black);
                        MenuAjUV.LabelResetUV.setForeground(Color.black);
                        MenuAjUV.LabelVidaUV.setForeground(Color.blue);
                    }
                    else
                    {
                        if(MenuAjUV.LabelVidaUV.getForeground()==Color.blue)
                        {
                            MenuAjUV.LabelHorometro.setForeground(Color.blue);
                            MenuAjUV.LabelResetUV.setForeground(Color.black);
                            MenuAjUV.LabelVidaUV.setForeground(Color.black);
                        }
                    }
                }
                Variables.Abajo=false;
            }
                
            if(Variables.Derecha==true)
            {
                if(MenuAjUV.LabelHorometro.getForeground()==Color.blue)
                {
                    MenuAjUV.dispose();
                    MenuHorometro.setVisible(true);
                    MenuHorometro.LabelHorasLaboradas.setText(String.format("%04d",Variables.HorasUV) + "Horas");
                    MenuHorometro.LabelVidaUV.setText(String.format("%04d",Variables.SetUV) + "Horas");
                }
                else
                {
                    if(MenuAjUV.LabelResetUV.getForeground()==Color.blue)
                    {
                        MenuAjUV.dispose();
                        MenuResetUV.setVisible(true);
                    }
                    else
                    {
                        if(MenuAjustes.LabelAjustesUV.getForeground()==Color.blue)
                        {
                            MenuAjUV.dispose();
                            MenuVidaUV.setVisible(true);  
                            MenuVidaUV.LabelMsg.setText("Vida Util Luz UV");
                            MenuVidaUV.LabelVidaUV.setText(String.format("%04d",Variables.SetUV) + "Horas");
                        }
                    }
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuAjUV.dispose();
                MenuPrincipal.setVisible(true);
                Variables.Izquierda=false;
            }
        }
    }
    
    // OK
    private void MenuHorometroUV() 
    {
        if(MenuHorometro.isVisible())
        {
            if(Variables.Derecha==true)
            {
                MenuHorometro.dispose();
                MenuPrincipal.setVisible(true);
                Variables.Derecha=false;
            }
        }
    }
    
    // OK
    private void MenuResetUV() throws InterruptedException {
        if(MenuResetUV.isVisible())
        {
            if(Variables.Derecha==true)
            {
                if(MenuResetUV.LabelMsg.getText().equals("¿Borrar Tiempo?"))
                {
                    if(Variables.Derecha==true)
                    {
                        Variables.SetUV=0;
                        MenuResetUV.dispose();
                        MenuPrincipal.setVisible(true);
                    }
                }
                else
                {
                    MenuResetUV.LabelMsg.setText("¿Borrar Tiempo?");
                    Thread.sleep(1000);
                    Variables.Derecha=false;
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuResetUV.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
                Variables.Izquierda=false;
            }
        }
    }
    
    // OK
    private void MenuSetUV() throws InterruptedException {
        if(MenuVidaUV.isVisible())
        {   
            if(Variables.Arriba==true)
            {
                Variables.SetUV+=100;
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo==true)
            {
                Variables.SetUV-=100;
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                MenuVidaUV.LabelMsg.setText("Set OK");
                Thread.sleep(1000);
                Variables.Derecha=false;
                MenuVidaUV.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
            }
            
            if(Variables.SetUV<100)
                Variables.SetUV=100;
            
            LabelVidaUV.setText(String.format("%04d",Variables.SetUV));
        }
    }

    // OK
    private void MenuServicio() {
        DecimalFormat decimalflow = new DecimalFormat("0.00");
        if(MenuServicio.isVisible())
        {
            if(Variables.Arriba==true)
            {
                if(MenuServicio.LabelSenInFlow.getForeground()==Color.blue)
                {
                    MenuServicio.LabelSenInFlow.setForeground(Color.black);
                    MenuServicio.LabelSenDownFlow.setForeground(Color.black);
                    MenuServicio.LabelPresInflow.setForeground(Color.black);
                    MenuServicio.LabelPresDownflow.setForeground(Color.blue);
                    System.out.println("Cambia Label SenInFlow");
                }
                else
                {
                    if(MenuServicio.LabelSenDownFlow.getForeground()==Color.blue)
                    {
                        MenuServicio.LabelSenInFlow.setForeground(Color.blue);
                        MenuServicio.LabelSenDownFlow.setForeground(Color.black);
                        MenuServicio.LabelPresInflow.setForeground(Color.black);
                        MenuServicio.LabelPresDownflow.setForeground(Color.black);
                        System.out.println("Cambia Label SenDownFlow");
                    }
                    else
                    {
                        if(MenuServicio.LabelPresInflow.getForeground()==Color.blue)
                        {
                            MenuServicio.LabelSenInFlow.setForeground(Color.black);
                            MenuServicio.LabelSenDownFlow.setForeground(Color.blue);
                            MenuServicio.LabelPresInflow.setForeground(Color.black);
                            MenuServicio.LabelPresDownflow.setForeground(Color.black);
                            System.out.println("Cambia Label PresInflow");
                        }
                        else
                        {
                            if(MenuServicio.LabelPresDownflow.getForeground()==Color.blue)
                            {
                                MenuServicio.LabelSenInFlow.setForeground(Color.black);
                                MenuServicio.LabelSenDownFlow.setForeground(Color.black);
                                MenuServicio.LabelPresInflow.setForeground(Color.blue);
                                MenuServicio.LabelPresDownflow.setForeground(Color.black);
                                System.out.println("Cambia Label PresDownflow");
                            }
                        }
                    }
                }
                Variables.Arriba=false;
            }
                    
            if(Variables.Abajo==true)
            {
                if(MenuServicio.LabelSenInFlow.getForeground()==Color.blue)
                {
                    MenuServicio.LabelSenInFlow.setForeground(Color.black);
                    MenuServicio.LabelSenDownFlow.setForeground(Color.blue);
                    MenuServicio.LabelPresInflow.setForeground(Color.black);
                    MenuServicio.LabelPresDownflow.setForeground(Color.black);
                    System.out.println("Cambia Label SenInFlow");
                }
                else
                {
                    if(MenuServicio.LabelSenDownFlow.getForeground()==Color.blue)
                    {
                        MenuServicio.LabelSenInFlow.setForeground(Color.black);
                        MenuServicio.LabelSenDownFlow.setForeground(Color.black);
                        MenuServicio.LabelPresInflow.setForeground(Color.blue);
                        MenuServicio.LabelPresDownflow.setForeground(Color.black);
                        System.out.println("Cambia Label Hora");
                    }
                    else
                    {
                        if(MenuServicio.LabelPresInflow.getForeground()==Color.blue)
                        {
                            MenuServicio.LabelSenInFlow.setForeground(Color.black);
                            MenuServicio.LabelSenDownFlow.setForeground(Color.black);
                            MenuServicio.LabelPresInflow.setForeground(Color.black);
                            MenuServicio.LabelPresDownflow.setForeground(Color.blue);
                            System.out.println("Cambia Label PresInflow");
                        }
                        else
                        {
                            if(MenuServicio.LabelPresDownflow.getForeground()==Color.blue)
                            {
                                MenuServicio.LabelSenInFlow.setForeground(Color.blue);
                                MenuServicio.LabelSenDownFlow.setForeground(Color.black);
                                MenuServicio.LabelPresInflow.setForeground(Color.black);
                                MenuServicio.LabelPresDownflow.setForeground(Color.black);
                                System.out.println("Cambia Label PresDownflow");
                            }
                        }
                    }
                }
                Variables.Abajo=false;
            }
                
            if(Variables.Derecha==true)
            {
                if(MenuServicio.LabelSenInFlow.getForeground()==Color.blue)
                {
                    MenuServicio.dispose();
                    MenuSenInflow.setVisible(true);
                    System.out.println("Entro Menu SenInFlow");
                    MenuSenInflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjusteInFlow));
                    MenuSenInflow.LabelZeroActual.setText(String.format("%03d",Variables.ZeroActualInFlow));
                }
                else
                {
                    if(MenuServicio.LabelSenDownFlow.getForeground()==Color.blue)
                    {
                        MenuServicio.dispose();
                        MenuSenDownflow.setVisible(true);
                        System.out.println("Entro Menu SenDownFlow");
                        MenuSenDownflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjusteDownFlow));
                        MenuSenDownflow.LabelZeroActual.setText(String.format("%03d",Variables.ZeroActualDownFlow));
                    }
                    else
                    {
                        if(MenuServicio.LabelPresInflow.getForeground()==Color.blue)
                        {
                            MenuServicio.dispose();
                            MenuPresInflow.setVisible(true);
                            System.out.println("Entro Menu PreInFlow");
                            MenuPresInflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjustePresInFlow));
                            MenuPresInflow.LabelZeroActual.setText(String.format("%03d",Variables.ZeroActualPresInFlow));
                        }
                        else
                        {
                            if(MenuServicio.LabelPresDownflow.getForeground()==Color.blue)
                            {
                                MenuServicio.dispose();
                                MenuPresDownflow.setVisible(true);
                                System.out.println("Entro Menu PreDownFlow");
                                MenuPresDownflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjustePresDownFlow));
                                MenuPresDownflow.LabelZeroActual.setText(String.format("%03d",Variables.ZeroActualPresDownFlow));
                            }
                        }
                    }
                }
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda==true)
            {
                MenuServicio.dispose();
                Variables.Izquierda=false;
            }
        }
    }
     
    // OK
    private void MenuSensorInFlow() {
         DecimalFormat decimalflow = new DecimalFormat("0.00");
        if(MenuSenInflow.isVisible())
        {
            if(Variables.Arriba)
            {
                Variables.AjusteInFlow+=0.01;
                MenuSenInflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjusteInFlow));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo)
            {
                Variables.AjusteInFlow-=0.01;
                MenuSenInflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjusteInFlow));
                Variables.Abajo=false;
            }
            
            Variables.ADCInFlow=(Variables.rx_msgint[10]*256)+Variables.rx_msgint[9];
            MenuSenInflow.LabelADC.setText(String.format("%03d",Variables.ADCInFlow));
            MenuSenInflow.LabelFlujo.setText(""+decimalflow.format(Variables.InFlowPromedio));
            
            if(Variables.Derecha)
            {
                Variables.DifInflow=Math.abs(Variables.ZeroFabrica-Variables.ADCInFlow);
                Variables.ZeroActualInFlow=Variables.ADCInFlow;
                MenuSenInflow.LabelZeroActual.setText(String.format("%03d",Variables.ZeroActualInFlow));
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda)
            {
                MenuSenInflow.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
                Variables.Izquierda=false;
            }
        }
    }
    
    // OK
    private void MenuSensorDownFlow() {
         DecimalFormat decimalflow = new DecimalFormat("0.00");
        if(MenuSenDownflow.isVisible())
        {
            if(Variables.Arriba)
            {
                Variables.AjusteDownFlow+=0.01;
                MenuSenDownflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjusteDownFlow));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo)
            {
                Variables.AjusteDownFlow-=0.01;
                MenuSenDownflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjusteDownFlow));
                Variables.Abajo=false;
            }
            
            Variables.ADCDownFlow=(Variables.rx_msgint[8]*256)+Variables.rx_msgint[7];
            MenuSenDownflow.LabelADC.setText(String.format("%03d",Variables.ADCDownFlow));
            MenuSenDownflow.LabelFlujo.setText(""+decimalflow.format(Variables.DownFlowPromedio));
            
            if(Variables.Derecha)
            {
                Variables.DifDownflow=Math.abs(Variables.ZeroFabrica-Variables.ADCDownFlow);
                Variables.ZeroActualDownFlow=Variables.ADCDownFlow;
                MenuSenDownflow.LabelZeroActual.setText(String.format("%03d",Variables.ZeroActualDownFlow));
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda)
            {
                MenuSenDownflow.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
                Variables.Izquierda=false;
            }
        }
     }

    // OK
    private void MenuPresionInFlow() {
         DecimalFormat decimalflow = new DecimalFormat("0.00");
        if(MenuPresInflow.isVisible())
        {
            if(Variables.Arriba)
            {
                Variables.AjustePresInFlow+=0.01;
                MenuPresInflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjustePresInFlow));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo)
            {
                Variables.AjustePresInFlow-=0.01;
                MenuPresInflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjustePresInFlow));
                Variables.Abajo=false;
            }
            
            Variables.ADCPresInFlow=(Variables.rx_msgint[6]*256)+Variables.rx_msgint[5];
            MenuPresInflow.LabelADC.setText(String.format("%03d",Variables.ADCPresInFlow));
            MenuPresInflow.LabelFlujo.setText(""+decimalflow.format(ADCToPres(Variables.rx_msgint[5], Variables.rx_msgint[6], Variables.GInflow)));
            
            if(Variables.Derecha)
            {
                Variables.GInflow=Variables.ADCPresInFlow;
                Variables.ZeroActualPresInFlow=Variables.ADCPresInFlow;
                MenuPresInflow.LabelZeroActual.setText(String.format("%03d",Variables.ZeroActualPresInFlow));
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda)
            {
                MenuPresInflow.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
                Variables.Izquierda=false;
            }
        }
    }
    
    // OK
    private void MenuPresionDownnFlow() {
         DecimalFormat decimalflow = new DecimalFormat("0.00");
        if(MenuPresDownflow.isVisible())
        {
            if(Variables.Arriba)
            {
                Variables.AjustePresDownFlow+=0.01;
                MenuPresDownflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjustePresDownFlow));
                Variables.Arriba=false;
            }
            
            if(Variables.Abajo)
            {
                Variables.AjustePresDownFlow-=0.01;
                MenuPresDownflow.LabelAjuste.setText(""+decimalflow.format(Variables.AjustePresDownFlow));
                Variables.Abajo=false;
            }
            
            Variables.ADCPresDownFlow=(Variables.rx_msgint[4]*256)+Variables.rx_msgint[3];
            MenuPresDownflow.LabelADC.setText(String.format("%03d",Variables.ADCPresDownFlow));
            MenuPresDownflow.LabelFlujo.setText(""+decimalflow.format(ADCToPres(Variables.rx_msgint[5], Variables.rx_msgint[6], Variables.GDownflow)));
            
            if(Variables.Derecha)
            {
                Variables.GDownflow=Variables.ADCPresDownFlow;
                Variables.ZeroActualPresDownFlow=Variables.ADCPresDownFlow;
                MenuPresDownflow.LabelZeroActual.setText(String.format("%03d",Variables.ZeroActualPresDownFlow));
                Variables.Derecha=false;
            }
            
            if(Variables.Izquierda)
            {
                MenuPresDownflow.dispose();
                MenuPrincipal.setVisible(true);
                GuardaConfig();
                Variables.Izquierda=false;
            }
        }
    }

     private void MenuModo() {
        if(MenuModo.isVisible())
        {
            if(Variables.Arriba==true || Variables.Abajo==true)
            {
                if(MenuModo.LabelMantenimiento.getForeground()==Color.blue)
                {
                    MenuModo.LabelMantenimiento.setForeground(Color.black);
                    MenuModo.LabelApagado.setForeground(Color.blue);
                }
                else
                {
                    if(MenuModo.LabelMantenimiento.getForeground()==Color.blue)
                    {
                        MenuModo.LabelMantenimiento.setForeground(Color.blue);
                        MenuModo.LabelApagado.setForeground(Color.black);
                    }
                }
                Variables.Arriba=false;
                Variables.Abajo=false;
            }
            
            if(Variables.Derecha==true)
            {
                MenuModo.dispose();
                if(MenuModo.LabelMantenimiento.getForeground()==Color.blue)
                {
                    MenuPrincipal.setVisible(true);
                }
                
                if(MenuModo.LabelApagado.getForeground()==Color.blue)
                {
                    Interfaz.TimerEx.IniciaTimer(1000);
                    PPurga.setVisible(true);
                }
                Variables.Derecha=false;
            }
        }
    }
     
        // Post Purga OK.
    private void PostPurgaView() throws InterruptedException{
        if(PPurga.isVisible())
        {
            Variables.MotorInFlow=true;
            Variables.MotorDownFlow=true;
            Variables.LuzBlanca=false;
            Variables.Toma=false;
            Variables.LuzUV=false;
            if(Variables.MinutosPPurga==Variables.PPurgaMinutos &&
                    Variables.SegundosPPurga==Variables.PPurgaSegundos)
            {
                Interfaz.TimerEx.DetieneTimer();
                PPurga.LabelMsg.setText("Purga Finalizada");
                Thread.sleep(2000);
                PPurga.dispose();
                Thread.sleep(200);
                Off.setVisible(true);
                Principal.dispose();
                Variables.PurgaMinutos=0;
                Variables.PurgaSegundos=0;
                Variables.PPurgaMinutos=0;
                Variables.PPurgaSegundos=0;
                Interfaz.TimerEx.IniciaTimer(1000);
            }
        }
    } 
     
}
