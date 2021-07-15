/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comunicacion;

import HID.HIDComm;
import Interfaz.CicloPurga;
import Interfaz.MensajeContraseña;
import Interfaz.Menu_Configuracion;
import Interfaz.Menu_FuncionamientoLuz;
import Interfaz.Menu_TiempoPurga;
import static Interfaz.Menu_VidaUV.LabelVidaUV;
import java.text.DecimalFormat;
import Interfaz.PantallaPrincipal;
import Interfaz.PantallaSplash;
import java.awt.Color;
import java.sql.Types;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;


/**
 *
 * @author JuanDavid
 */
public class Hilo implements Runnable{
    
    public static int flancosplash=0;
    Interfaz.Menu_Principal MenuPrincipal= new Interfaz.Menu_Principal();
    Interfaz.Menu_Configuracion MenuConfiguracion= new Interfaz.Menu_Configuracion();
    Interfaz.Menu_Ajustes MenuAjustes= new Interfaz.Menu_Ajustes();
    Interfaz.Menu_AjustesUV MenuAjUV= new Interfaz.Menu_AjustesUV();
    Interfaz.Menu_AjustesUnidades MenuAjUnidades= new Interfaz.Menu_AjustesUnidades();
    Interfaz.Menu_AjustesContraseña MenuAjContraseña= new Interfaz.Menu_AjustesContraseña();
    Interfaz.Menu_AjustesContraseñaNueva MenuAjContraseñaNueva= new Interfaz.Menu_AjustesContraseñaNueva();
    Interfaz.Menu_FuncionamientoLuz MenuFunLuz= new Interfaz.Menu_FuncionamientoLuz();
    Interfaz.Menu_FuncionamientoMotor MenuFunMotor= new Interfaz.Menu_FuncionamientoMotor();
    Interfaz.Menu_FuncionamientoLuzUV MenuFunLuzUV= new Interfaz.Menu_FuncionamientoLuzUV();
    Interfaz.Menu_TempoUV MenuTempoUV= new Interfaz.Menu_TempoUV();
    Interfaz.Menu_TiempoPurga MenuPurga= new Interfaz.Menu_TiempoPurga();
    Interfaz.Menu_TiempoPostPurga MenuPostPurga= new Interfaz.Menu_TiempoPostPurga();
    Interfaz.Menu_Horometro MenuHorometro= new Interfaz.Menu_Horometro();
    Interfaz.Menu_ResetUV MenuResetUV= new Interfaz.Menu_ResetUV();
    Interfaz.Menu_VidaUV MenuVidaUV= new Interfaz.Menu_VidaUV();
    Interfaz.MensajeContraseña MsjContraOk= new Interfaz.MensajeContraseña();
    Interfaz.MensajeContraseñaIncorrecta MsjContraIn= new Interfaz.MensajeContraseñaIncorrecta();
    Interfaz.MensajeContraseñaAlmacenada MsjContraAl= new Interfaz.MensajeContraseñaAlmacenada();
    PantallaSplash Splash = new PantallaSplash();
    public static CicloPurga Purga = new CicloPurga();
        
    
    @Override
    @SuppressWarnings("SleepWhileInLoop")
    public void run() {
        LimpiarVector();
        int hilo=0;
        for (int i=0;i<18;i++) {
            Variables.tx_msg[i]=(byte)50;
        }
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
    
    
    public static void EnviarDato(){
        Variables.tx_msg[0]=(byte)255;
        Variables.tx_msg[1]=(byte)255;
        Variables.tx_msg[2]=(byte)255;
        
        if(Variables.MotorDownFlow==true)
            Variables.tx_msg[4]=(byte)5;
        else
            Variables.tx_msg[4]=(byte)10;
        
        if(Variables.MotorInFlow==true)
            Variables.tx_msg[5]=(byte)5;
        else
            Variables.tx_msg[5]=(byte)10;
        
        if(Variables.LuzBlanca==true)
            Variables.tx_msg[6]=(byte)5;
        else
            Variables.tx_msg[6]=(byte)10;
        
        if(Variables.LuzUV==true)
            Variables.tx_msg[7]=(byte)5;
        else
            Variables.tx_msg[7]=(byte)10;
        
        if(Variables.Toma==true)
            Variables.tx_msg[8]=(byte)5;
        else
            Variables.tx_msg[8]=(byte)10;
        
        if(Variables.Alarma==true)
            Variables.tx_msg[9]=(byte)5;
        else
            Variables.tx_msg[9]=(byte)10;
        
        Variables.tx_msg[10]=(byte)Variables.AperturaDamper;
        Variables.tx_msg[11]=(byte)255;
        Variables.tx_msg[12]=(byte)255;
        Variables.tx_msg[13]=(byte)128;
        HIDComm.EnvioHID((byte)2, Variables.tx_msg);
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
        
        for (int i = 0; i < 64; i++) {
           
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
    
    public static double ADCToFlow(int lsb, int hsb){
        double flow=0;
        int adcFlow=0;
        final double a=0.0000001418633343546420;
        final double b=-0.00009476749001431169;
        final double c=0.040182822903506;
        final double d=-5.354852229527197;
        adcFlow=(hsb*256)+lsb;
        flow=(Math.pow(adcFlow,3)*a)+(Math.pow(adcFlow,2)*b)+(adcFlow*c)+(d)-0.08;
        if(flow<0.0)
            flow=0.0;
        return (flow);
    }
    
    public static double ADCToPres(int lsb, int hsb, int G){
        double pres=0;
        int adcPres=0;
        
        adcPres=(hsb*256)+lsb;
        pres=(adcPres/G)-1;
        if(pres<0.0)
            pres=0;
        return (pres);
    }
    
    public void ActualizarCampos(){
        DecimalFormat decimalflow = new DecimalFormat("0.00");
        
        Variables.VidrioUP=Variables.rx_msgint[11];
        Variables.VidrioDN=Variables.rx_msgint[12];

        // Lectura y actualizacion de Inflow
        Variables.PVInFlow=ADCToFlow(Variables.rx_msgint[9], Variables.rx_msgint[10]);
        
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
        Variables.PVDownFlow=ADCToFlow(Variables.rx_msgint[7], Variables.rx_msgint[8]);
        
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
            double PorcentajeInFlow=((ADCToPres(Variables.rx_msgint[5], Variables.rx_msgint[6], 100))*100)/1.5;
            PantallaPrincipal.InFlowLabel.setText(PorcentajeInFlow+"%");
            
            if(PorcentajeInFlow>0 && PorcentajeInFlow<25)
            {
                PantallaPrincipal.Bar25InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25InFlow.setBackground(Color.green);
                PantallaPrincipal.Bar25InFlow.setValue((int)((PorcentajeInFlow*100)/25));
                
            }
            
            if(PorcentajeInFlow>25 && PorcentajeInFlow<50)
            {
                PantallaPrincipal.Bar25InFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar25InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25InFlow.setValue(100);
                PantallaPrincipal.Bar50InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50InFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar50InFlow.setValue((int) (((PorcentajeInFlow*100)/25)-100));
                
            }
           
            if(PorcentajeInFlow>50 && PorcentajeInFlow<75)
            {
                PantallaPrincipal.Bar25InFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar25InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25InFlow.setValue(100);
                PantallaPrincipal.Bar50InFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar50InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50InFlow.setValue(100);
                PantallaPrincipal.Bar75InFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75InFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar75InFlow.setValue((int) (((PorcentajeInFlow*100)/25)-200));
            }
            
        // Lectura y ajuste de estado de Filtro DownFlow
            double PorcentajeDownFlow=((ADCToPres(Variables.rx_msgint[3], Variables.rx_msgint[4], 100))*100)/1.5;
            PantallaPrincipal.DownFlowLabel.setText(PorcentajeDownFlow+"%");
            
            if(PorcentajeDownFlow>0 && PorcentajeDownFlow<25)
            {
                PantallaPrincipal.Bar25DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25DownFlow.setBackground(Color.green);
                PantallaPrincipal.Bar25DownFlow.setValue((int)((PorcentajeDownFlow*100)/25));
                
            }
            
            if(PorcentajeDownFlow>25 && PorcentajeDownFlow<50)
            {
                PantallaPrincipal.Bar25DownFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar25DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25DownFlow.setValue(100);
                PantallaPrincipal.Bar50DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50DownFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar50DownFlow.setValue((int) (((PorcentajeDownFlow*100)/25)-100));
                
            }
           
            if(PorcentajeDownFlow>50 && PorcentajeDownFlow<75)
            {
                PantallaPrincipal.Bar25DownFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar25DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar25DownFlow.setValue(100);
                PantallaPrincipal.Bar50DownFlow.setBackground(Color.yellow);
                PantallaPrincipal.Bar50DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar50DownFlow.setValue(100);
                PantallaPrincipal.Bar75DownFlow.setForeground(Color.blue);
                PantallaPrincipal.Bar75DownFlow.setBackground(Color.orange);
                PantallaPrincipal.Bar75DownFlow.setValue((int) (((PorcentajeDownFlow*100)/25)-200));
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
        
        if(!Splash.isVisible() && flancosplash==0)
        {
            Splash.setVisible(true);
            flancosplash=1;
            Thread.sleep(5000);
            Splash.dispose();
            Purga.setVisible(true);
        }
        
        if(Purga.isVisible())
        {
            if(Variables.MinutosPurga==Variables.PurgaMinutos &&
                    Variables.SegundosPurga==Variables.PurgaSegundos)
            {
                Interfaz.TimerEx.DetieneTimer();
                Purga.LabelMsg.setText("Purga Finalizada");
                Thread.sleep(2000);
                Purga.dispose();
            }
        }
        Thread.sleep(200);
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
        
        if(Splash.isVisible())
        {
            if(Variables.Derecha)
            {
                Splash.dispose();
                //PantallaPrincipal.setVisible(true);
                Variables.Derecha=false;
            }
        }
        
        if(!MenuPrincipal.isVisible() && !MenuConfiguracion.isVisible() && !MenuAjustes.isVisible() && !MenuFunLuz.isVisible() 
                && !MenuFunLuzUV.isVisible() && !MenuFunMotor.isVisible() && !MenuAjustes.isVisible() && !MenuAjUV.isVisible()
                && !MenuAjContraseña.isVisible() && !MenuAjContraseñaNueva.isVisible() && !MenuAjUnidades.isVisible()
                && !MenuAjUV.isVisible() && !MenuHorometro.isVisible()
                && !MenuResetUV.isVisible() && !MenuVidaUV.isVisible())
        {
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

    private void MenuPrincipal() {
        if(MenuPrincipal.isVisible())
        {
            if(Variables.Arriba==true)
            {
                if(MenuPrincipal.LabelConfig.getForeground()==Color.blue)
                {
                    MenuPrincipal.LabelConfig.setForeground(Color.black);
                    MenuPrincipal.LabelAjustes.setForeground(Color.black);
                    MenuPrincipal.LabelServicio.setForeground(Color.blue);
                    System.out.println("Cambia Label Config");
                }
                else
                {
                    if(MenuPrincipal.LabelServicio.getForeground()==Color.blue)
                    {
                        MenuPrincipal.LabelConfig.setForeground(Color.black);
                        MenuPrincipal.LabelAjustes.setForeground(Color.blue);
                        MenuPrincipal.LabelServicio.setForeground(Color.black);
                        System.out.println("Cambia Label Servicio");
                    }
                    else
                    {
                        if(MenuPrincipal.LabelAjustes.getForeground()==Color.blue)
                        {
                            MenuPrincipal.LabelConfig.setForeground(Color.blue);
                            MenuPrincipal.LabelAjustes.setForeground(Color.black);
                            MenuPrincipal.LabelServicio.setForeground(Color.black);
                            System.out.println("Cambia Label Ajustes");
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
                    MenuPrincipal.LabelServicio.setForeground(Color.black);
                    System.out.println("Cambia Label Config");
                }
                else
                {
                    if(MenuPrincipal.LabelServicio.getForeground()==Color.blue)
                    {
                        MenuPrincipal.LabelConfig.setForeground(Color.blue);
                        MenuPrincipal.LabelAjustes.setForeground(Color.black);
                        MenuPrincipal.LabelServicio.setForeground(Color.black);
                        System.out.println("Cambia Label Servicio");
                    }
                    else
                    {
                        if(MenuPrincipal.LabelAjustes.getForeground()==Color.blue)
                        {
                            MenuPrincipal.LabelConfig.setForeground(Color.black);
                            MenuPrincipal.LabelAjustes.setForeground(Color.black);
                            MenuPrincipal.LabelServicio.setForeground(Color.blue);
                            System.out.println("Cambia Label Ajustes");
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
                        MenuConfiguracion.setVisible(true);
                        System.out.println("Entro Menu Config");
                    }
                    else
                    {
                        if(MenuPrincipal.LabelAjustes.getForeground()==Color.blue)
                        {
                            MenuPrincipal.dispose();
                            MenuAjustes.setVisible(true);
                            System.out.println("Entro Menu Config");
                        }
                    }
                }
                Variables.Derecha=false;
            }
        }
    }

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
                        System.out.println("Cambia Label Hora");
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
                        MenuFunLuz.setVisible(true);
                        System.out.println("Entro Menu Funcionamiento");
                    }
                    else
                    {
                        if(MenuConfiguracion.LabelPurga.getForeground()==Color.blue)
                        {
                            MenuConfiguracion.dispose();
                            MenuPurga.setVisible(true);
                            System.out.println("Entro Menu Purga");
                        }
                        else
                        {
                            if(MenuConfiguracion.LabelPostPurga.getForeground()==Color.blue)
                            {
                                MenuConfiguracion.dispose();
                                MenuPostPurga.setVisible(true);
                                System.out.println("Entro Menu PostPurga");
                            }
                        }
                    }
                }
                Variables.Derecha=false;
            }
        }
    }

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
                Variables.Derecha=false;
            }
        }
    }

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
                Variables.Derecha=false;
            }
        }
    }

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
                Variables.Derecha=false;
            }
        }
    }

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
                Variables.Izquierda=false;
            }
        }
    }
    
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
                Variables.Izquierda=false;
            }
        }
    }

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
                MenuPurga.dispose();
                MenuPrincipal.setVisible(true);
                Variables.Izquierda=false;
            }
        }
    }

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
        }
    }

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
                MenuAjUnidades.dispose();
                MenuPrincipal.setVisible(true);
                System.out.println("Abre Menu Principal");
                Variables.Derecha=false;
            }
        }
    }
    
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
                    System.out.println("Aumenta U1");
                    
                }
                else
                {
                    if(MenuAjContraseña.LabelU2.getForeground()==Color.blue)
                    {
                        Variables.U2Contraseña--;
                        Variables.U2Contraseña=LimitaUnidad(Variables.U2Contraseña);
                        MenuAjContraseña.LabelU2.setText(Integer.toString(Variables.U2Contraseña));
                        System.out.println("Aumenta U2");
                    }
                    else
                    {
                        if(MenuAjContraseña.LabelU3.getForeground()==Color.blue)
                        {
                            Variables.U3Contraseña--;
                            Variables.U3Contraseña=LimitaUnidad(Variables.U3Contraseña);
                            MenuAjContraseña.LabelU3.setText(Integer.toString(Variables.U3Contraseña));
                            System.out.println("Aumenta U3");
                        }
                        else
                        {
                            if(MenuAjContraseña.LabelU4.getForeground()==Color.blue)
                            {
                                Variables.U4Contraseña--;
                                Variables.U4Contraseña=LimitaUnidad(Variables.U4Contraseña);
                                MenuAjContraseña.LabelU4.setText(Integer.toString(Variables.U4Contraseña));
                                System.out.println("Aumenta U4");
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
                    System.out.println("Aumenta U1");
                }
                else
                {
                    if(MenuAjContraseñaNueva.LabelU2.getForeground()==Color.blue)
                    {
                        Variables.U2ContraseñaNueva--;
                        Variables.U2ContraseñaNueva=LimitaUnidad(Variables.U2ContraseñaNueva);
                        MenuAjContraseñaNueva.LabelU2.setText(Integer.toString(Variables.U2ContraseñaNueva));
                        System.out.println("Aumenta U2");
                    }
                    else
                    {
                        if(MenuAjContraseñaNueva.LabelU3.getForeground()==Color.blue)
                        {
                            Variables.U3ContraseñaNueva--;
                            Variables.U3ContraseñaNueva=LimitaUnidad(Variables.U3ContraseñaNueva);
                            MenuAjContraseñaNueva.LabelU3.setText(Integer.toString(Variables.U3ContraseñaNueva));
                            System.out.println("Aumenta U3");
                        }
                        else
                        {
                            if(MenuAjContraseñaNueva.LabelU4.getForeground()==Color.blue)
                            {
                                Variables.U4ContraseñaNueva--;
                                Variables.U4ContraseñaNueva=LimitaUnidad(Variables.U4ContraseñaNueva);
                                MenuAjContraseñaNueva.LabelU4.setText(Integer.toString(Variables.U4ContraseñaNueva));
                                System.out.println("Aumenta U4");
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
                        MenuAjUV.LabelHorometro.setForeground(Color.blue);
                        MenuAjUV.LabelResetUV.setForeground(Color.black);
                        MenuAjUV.LabelVidaUV.setForeground(Color.black);
                    }
                    else
                    {
                        if(MenuAjUV.LabelVidaUV.getForeground()==Color.blue)
                        {
                            MenuAjUV.LabelHorometro.setForeground(Color.black);
                            MenuAjUV.LabelResetUV.setForeground(Color.blue);
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
                        }
                    }
                }
                Variables.Derecha=false;
            }
        }
    }
    
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
    
    private void MenuResetUV() throws InterruptedException 
    {
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
                Variables.Izquierda=false;
            }
        }
    }
    
    private void MenuSetUV() throws InterruptedException 
    {
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
            }
            
            if(Variables.SetUV<100)
                Variables.SetUV=100;
            
            LabelVidaUV.setText(String.format("%04d",Variables.SetUV));
        }
    }
}
