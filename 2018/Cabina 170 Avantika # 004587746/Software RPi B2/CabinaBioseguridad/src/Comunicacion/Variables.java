/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Comunicacion;

import java.io.Serializable;

/**
 *
 * @author JuanDavid
 */
public class Variables implements Serializable {
    // Variables de Botones
    public static boolean Derecha=false,Izquierda=false,Arriba=false,Abajo=false;
    public static boolean TeclaMotor=false,TeclaLuz=false,TeclaUV=false,TeclaToma=false;
    
    // Variables Configuraciòn de Logica de Funcionamiento de Cabina
    public static boolean Vidrio20Luz=false,VidrioDown=false,Vidrio20Motor=false;
    
    // Variables de Configuraciòn de unidades de medida
    public static boolean Imperial=false,Metrico=true;
    public static int HorasUV=0,SetUV=0;
    public static int VidrioUP=0,VidrioDN=0;
    public static int AperturaDamper=0;
    public static boolean MotorDownFlow=false,MotorInFlow=false,LuzBlanca=false,
            LuzUV=false,Toma=false,Alarma=false;
    public static int PurgaMinutos=1,PurgaSegundos=0,PPurgaMinutos=0,PPurgaSegundos=0,
            U1Contraseña=0,U2Contraseña=0,U3Contraseña=0,U4Contraseña=0,
            U1ContraseñaSave=0,U2ContraseñaSave=0,U3ContraseñaSave=0,U4ContraseñaSave=0,
            U1ContraseñaNueva=0,U2ContraseñaNueva=0,U3ContraseñaNueva=0,
            U4ContraseñaNueva=0,UVMinutos=0,UVSegundos=0;
    public static int longitud=0;
    public static int SegundosPurga=0,MinutosPurga=0,SegundosUV=0,MinutosUV=0;
    public static int adc=0,adc1=0,adc2=0,adc3=0,adc4=0,adc5=0,adc6=0,adc7=0;
    public static int[] datoRX = new int [11];
    public static int[] VectorRX = new int [11];
    
    // Variables Comunicaciòn
    public static byte[] tx_msg = new byte [18];
    public static byte[] rx_msg = new byte [18];
    public static int[] rx_msgint = new int [18];
    
    // Variables para promedio
    public static double [] InFlowProm= new double[20],DownFlowProm=new double[20],
            FiltroInFlowProm=new double[20], FiltroDownFlowProm= new double[20];
    public static double InFlowPromedio=0.0,DownFlowPromedio=0.0;
    public static double PVInFlow=0.0,PVDownFlow=0.0;
    public static int indicePromedioInFlow=0,indicePromedioDownFlow=0;
        
}
