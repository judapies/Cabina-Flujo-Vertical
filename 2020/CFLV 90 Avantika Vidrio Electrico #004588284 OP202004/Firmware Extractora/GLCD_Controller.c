//Programa para Cabina de Bioseguridad

#include <18F4685.h>
#device adc=10
#device HIGH_INTS=TRUE //Activamos niveles de prioridad
#fuses HS,WDT8192,NOPROTECT,NOLVP,NODEBUG,NOSTVREN,NOPUT,NOCPD,NOWRT,NOIESO,NOFCMEN,NOPBADEN,NOWRTC,NOWRTB,NOEBTR,NOEBTRB,NOCPB,NOXINST,NOLPT1OSC,NOMCLR
#use delay(clock=20000000)
#use rs232(baud=9600, xmit=PIN_C6, rcv=PIN_C7, bits=8, parity=N,errors)
#priority rda
#include <MATH.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <DS1302.c>
#include "FuncionesDisplay.h"

#define   UP               !input(PIN_B4)
#define   DOWN             !input(PIN_B5)
#define   RIGHT            !input(PIN_B6)
#define   LEFT             !input(PIN_B7)
#define   VIDRIOUUP        !input(PIN_E2)
#define   VIDRIOUP         !input(PIN_E1)
#define   VIDRIODN         !input(PIN_E0)

#define   Alarma_on           output_bit(PIN_C0,1)
#define   Alarma_off          output_bit(PIN_C0,0)
#define   LuzBlanca_on        output_bit(PIN_D0,0)
#define   LuzBlanca_off       output_bit(PIN_D0,1)
#define   LuzUV_on            output_bit(PIN_D1,0)
#define   LuzUV_off           output_bit(PIN_D1,1)
#define   Aux_on              output_bit(PIN_D2,0)
#define   Aux_off             output_bit(PIN_D2,1)
#define   Motor_on            output_bit(PIN_C3,1)
#define   Motor_off           output_bit(PIN_C3,0)
#define   Motor2_on           output_bit(PIN_C2,1)
#define   Motor2_off          output_bit(PIN_C2,0)
#define   Toma_on             output_bit(PIN_C1,1)
#define   Toma_off            output_bit(PIN_C1,0)
#define   Display_on          output_bit(PIN_D5,1)
#define   Display_off         output_bit(PIN_D5,0)
#define   O2_on               output_bit(PIN_D6,1)
#define   O2_off              output_bit(PIN_D6,0)
#define   O1_on               output_bit(PIN_D7,1)
#define   O1_off              output_bit(PIN_D7,0)

#define OFF 0
#define ON  1
#define RX_BUFFER_SIZE  10
char Rx_Buffer[RX_BUFFER_SIZE+1];
char Rx_Buffer2[RX_BUFFER_SIZE+1];
char RX_Wr_Index=0;
char RX_Rd_Index=0;
char RX_Counter=0;

#define TX_BUFFER_SIZE  24
char Tx_Buffer[TX_BUFFER_SIZE+1];
char TX_Wr_Index=0;
char TX_Counter=0;


/* Tabla de Modulos, Primera Fila A�o Regular y el siguiente a�o Bisiesto
E   F   M   A   M   J   J   A   S   O   N   D
0   3   3   6   1   4   6   2   5   0   3   5
0   3   4   0   2   5   0   3   6   1   4   6
*/
int8 Regular[12]={0,3,3,6,1,4,6,2,5,0,3,5};
int8 Bisiesto[12]={0,3,4,0,2,5,0,3,6,1,4,6};

short imprimir=0,flag_blower=0,flag_toma=0,flag_luz=0,w=0,flag_uv=0,flag_alarma2=0,pulsoSubir=OFF,pulsoBajar=OFF,flag_arriba=OFF;
short tiempo_purga=0,tiempo_trabajo=0,flag_latencia=0,entro=0,GuardaEEPROM=0,Minutos=OFF,Segundos=OFF,Latencia=0,tiempo_uv=0,guardauv=0;
short Cruce2=OFF,flagClave=OFF,Encendio=OFF;
short guardatrabajo=0,Lectura=0,tiempo_postpurga=0,ECO=0;
int8 Menu=240,Opcion=1,negativo=10,G_l=0,G_h=0,tiemporeset=0,codigoSubir=0,codigoBajar=0,codigoDetener=0;
int8 q=0,r=0,l=0,h=0,Entero=0,Decimal1=0,Alarma=10,Alarma2=10,LuzUV=0,Vel1=0,Vel2=0,mediaMovil=0;
int8 Year=18,Mes=9,Dia=13,Hora=0,Minuto=0,Segundo=0,dow=0,Dato_Exitoso=0,MenuAntt=8;
int8 YearTx=0,MesTx=0,DiaTx=0,HoraTx=0,MinutoTx=0,dowTx=0,z=0,Dato2=0,Inicio=0,TipoClave=0,PantallaPrincipal=0,Digito=0,n=0;
int8 sPurga=0,sPurgap=0,mPurga=0,mPurgap=0,sPPurga=0,sPPurgap=0,mPPurga=0,mPPurgap=0,sUV=0,mUV=0,sUVp=0,mUVp=0,UV2=10,Modulo=0,Opcion2=0;

unsigned int16 Temporal1=0,segundos_uv=0;

int16 t_latencia=0,minutos_uv=0,minutos_trabajo=0,G16=0,tClave=0;
signed int  Tuv[4]={0,0,0,0};          // Tiempo de UV transcurrido(en Horas)
signed int  Ttrabajo[4]={0,0,0,0};        // Tiempo de trabajo transcurrido (en Horas)
signed int  Password[4]={0,0,0,0};        // Contrasena Almacenada de 4 digitos
signed int  Contrasena[4]={0,0,0,0};      // Contrasena de 4 digitos
float Inflow=0.0,Downflow=0.0,Filtro_Downflow=0.0,Filtro_Inflow=0.0,Diferencia=0.0;
float Velocidad=0.0,x_uno=0.0,x_cuadrado=0.0,x_cubo=0.0;
float Temporal=0.0,Temporal2=0.0,UVTime=0.0,TrabajoTime=0.0,barra=0.0,Gdn=0.0,Ajustedn=0.0;
float a=0.0000001418633343546420,b=-0.00009476749001431169,c= 0.040182822903506,d= -5.354852229527197;//Sensor 2 Posible bueno
//float t=378.463968402560,f=-18275.0402762787;//Sensor 2 Posible bueno

float promedio[30]={0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
float promedio2[30]={0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
float PromPresion[30]={0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
float prom[10];
float zero_fabrica=203.0,zero_actual=0.0,Relacion=0.57;
float V1=0.0,G=2.0;

int16 pulsos=0;
int8 i=0,l2=0,h2=0,Temperatura=0;
short Cruce=OFF,Purga=OFF;
int8 VelMotor=0,VelMotor2=0;

int1 BITS[32];
int16 Ta,Tb,TICK=0;
Int1 BIT_START, NUEVO_DATO;
int BYTE_IR[4];
int8 BIT_BYTE(int1 D7, int1 D6, int1 D5, int1 D4, int1 D3, int1 D2, int1 D1, int1 D0);

typedef struct{
   float zero_actual;
   float Diferencia;
   int16 adc;
   int8 negativo ;
   float Velocidad;
   int8 l;
   int8 h;
   float Flujo;   
   float Ajuste;
   float promedio[80];
}SensorFlujo;

typedef struct{
   int16 adc;
   float V1;
   float G;
   float Presion;
   float RPT100;
   int8 r;
   int8 h;   
   float Ajuste;   
   float PromPresion[10];
   float promediopresion;
}SensorPresion;

SensorFlujo Flujo[5];
SensorPresion Presion[5];
#include "Funciones.h"

#int_EXT1 HIGH
void RB1(){
   Ta=get_timer1();
   TICK = Ta-tb;
   Tb = Ta;
   
   Entro=1;
   
   if(BIT_START==1){
       //BIT 1
       if(TICK>1200 && TICK < 1600)  // 700 con Pres 4 y 20MHz 
      {
         BITS[n]=1; n++;
      }
      //BIT 0
       if(TICK>500 && TICK < 900)  // 350 con Pres 4 y 20MHz 
      {
         BITS[n]=0; n++;
      }
      if(n>=32){
         NUEVO_DATO=1;
         BIT_START=0;
      }
   
   }
   //BIT START
   if(TICK>3500 && TICK < 7500 && BIT_START==0)  //3500 con Pres 4 y 20MHz 
   {
      BIT_START=1;
      n=0; 
   }
}

#int_EXT HIGH
void ext0() {  
   Cruce=ON;
   Cruce2=ON;
   
   if(VelMotor==5){
      set_timer0(228); // Cada cuenta del timer0 es 51.2us y timer1 es 1.59us
      Motor_off; 
      Motor2_off; 
   }else if(VelMotor==4){
      set_timer0(201);      
      Motor_off; 
      Motor2_off; 
   }else if(VelMotor==3){
      set_timer0(180);
      Motor_off; 
      Motor2_off; 
   }else if(VelMotor==2){
      set_timer0(165);
      Motor_off; 
      Motor2_off; 
   }else if(VelMotor==1){
      set_timer0(150);
      Motor_off; 
      Motor2_off; 
   }
   /*
   if(VelMotor2==5){
      set_timer1(64668); // Cada cuenta del timer0 es 51.2us y timer1 es 1.59us      
      Motor2_off;      
   }else if(VelMotor2==4){
      set_timer1(63801);      
      Motor2_off; 
   }else if(VelMotor2==3){
      set_timer1(62934);
      Motor2_off; 
   }else if(VelMotor2==2){
      set_timer1(62067);      
      Motor2_off; 
   }else if(VelMotor2==1){
      set_timer1(61200);      
      Motor2_off; 
   }
   */
   pulsos++;   
}

#int_TIMER0 HIGH
void temp0s(void){
   if(VelMotor>0 && VelMotor<6){
   if(Cruce){         //If the triac pin is low we change the state and prepare the timer
      Motor_on;
      Motor2_on; 
      set_timer0(253);         // Next timer overflow will clear the triac pin
      Cruce=OFF;         // Flag now signals that we need to turn off the triac output pin
   } else {            //The triac has been triggered, cut the pulse
      Motor_off;
      Motor2_off; 
      set_timer0(0);         //Set timer period to maximum, timer should not overflow untill next zero cross interrupt
   }
   }
}
/*
#int_TIMER1 HIGH
void temp1s(void){
   if(VelMotor2>0 && VelMotor2<6){
   if(Cruce2){         //If the triac pin is low we change the state and prepare the timer
      Motor2_on;
      set_timer1(65435);         // Next timer overflow will clear the triac pin
      Cruce2=OFF;         // Flag now signals that we need to turn off the triac output pin
   } else {            //The triac has been triggered, cut the pulse
      Motor2_off;
      set_timer1(0);         //Set timer period to maximum, timer should not overflow untill next zero cross interrupt
   }
   }
}
*/
#int_rda 
void serial_rx_isr(){
   Dato2=fgetc();
   if(Dato2==0x65){//Inicio Comunicacion
      Inicio=1;
      RX_Wr_Index=0;
   }
   if(Inicio==1){
      Rx_Buffer2[RX_Wr_Index] = Dato2;
      RX_Wr_Index++;
   }
   if(RX_Wr_Index >= RX_BUFFER_SIZE){
      RX_Wr_Index =0;
      Inicio=0;
   }
   if(RX_Wr_Index==0){
      if(Rx_Buffer2[0]==0x65 && Rx_Buffer2[1]==0xff && Rx_Buffer2[2]==0xff && Rx_Buffer2[8]==0x00 && Rx_Buffer2[9]==0xff )
         Dato_Exitoso=5;
      else
         Dato_Exitoso=10;
   }
   //if(Menu==20)
   //  Alarma_on;
     
   //output_toggle(PIN_C0);  
}

void main(){
   //delay_ms(1000);
   enable_interrupts(int_rda);
   enable_interrupts(global);
   enable_interrupts(INT_EXT_L2H);
   enable_interrupts(INT_EXT1_H2L);
   setup_timer_0(RTCC_8_BIT|RTCC_DIV_256);
   enable_interrupts(INT_TIMER0);
   setup_timer_1(T1_INTERNAL|T1_DIV_BY_8);
   //enable_interrupts(INT_TIMER1);
   setup_wdt(WDT_ON);
   setup_adc_ports(AN0_TO_AN4);
   setup_adc(ADC_CLOCK_DIV_32);
   port_b_pullups(true);
   setup_wdt(WDT_ON);
   Display_on;
   Cruce=OFF;
   Cruce2=OFF;
   VelMotor=0;
   VelMotor2=0;
   Motor_off;  
   delay_ms(1);
   rtc_init();  
   delay_ms(1000);
   for(i=0;i<10;i++){
      printf("page Bienvenida");
      SendDataDisplay();   
   }
   Motor_off;
   LuzBlanca_off;Alarma_off;
//-------Inicio de Cabina-------------------------------------------------------------------------//   
   Motor2_off;
   LeeEEPROM(); // Lectura de variables almacenadas en la EEPROM 
   LimitaValores();
   delay_ms(1000);
   while(true){ 
   
      leeControlRemoto();
      if(pulsos>=120){ 
      //enable_interrupts(INT_RDA);
      //enable_interrupts(global); 
      pulsos=0;
      imprimir=1;Lectura=1;
      
      if(flag_blower==1)
         minutos_trabajo++; 
  
      if(tiempo_purga==1 || Purga){                  
         sPurga++;
         if(sPurga>59){
            sPurga=0;
            mPurga++;
         }
      }
      
      if(tiempo_postpurga){
         sPPurga++;
         if(sPPurga>59){
            sPPurga=0;
            mPPurga++;
         }
      }
   
      if(tiempo_uv==1){
         segundos_uv++;
         sUV++;
         if(sUV>59){
            sUV=0;
            mUV++;
         }
      }
   }
   
      if(VelMotor==6){
         Motor_on;
         Motor2_on; 
         set_timer0(0);
      }
      /*
      if(VelMotor2==6){
         Motor2_on;
         //set_timer1(0);
      }
      */
      if(VelMotor==0){
         Motor_off;
         Motor2_off; 
         set_timer0(0);
      }
      /*
      if(VelMotor2==0){
         Motor2_off;
         //set_timer1(0);
      }
      */
      restart_wdt();
      LeeDisplay();
   
//**Actualizaci�n de Lectura de Sensores**//   
   if(Lectura==1){
      Lectura=0;
      rtc_get_date(Dia,Mes,Year,dow);
      rtc_get_time(Hora,Minuto,Segundo);
      InFlow=LeerFlujo(mediaMovil,1);
      //DownFlow=InFlow*Relacion;
      DownFlow=D6FW101(8,2);//LeerFlujo(8,2);
      Filtro_InFlow=Leer_Sensor_Presion(8,3);
      //Filtro_InFlow=0;
      Filtro_DownFlow=Leer_Sensor_Presion(8,4);
      Temperatura=leeTemperatura(10);
      if(Alarma==5 || Alarma2==5){
         if(flag_alarma2==0){
            Alarma_on;
            flag_alarma2=1;
         }else{
            Alarma_off;
            flag_alarma2=0;
         }
      }
      
      if(Alarma==10 && Alarma2==10){
         Alarma_off;
      }
      
      if(flag_latencia==1)   
         t_latencia++;         
   
      if(t_latencia>=15)
         Latencia=1;
   }
//----------------------------------------//

//**Contador de Tiempo de Trabajo del filtro**//
   if(minutos_trabajo>=3600){ // Tiempo de Trabajo
      minutos_trabajo=0; //Minutos Trabajo realmente son segundos.
      write_eeprom(37,make8(minutos_trabajo,0));
      delay_ms(20);
      write_eeprom(38,make8(minutos_trabajo,1));
      delay_ms(20);
      Ttrabajo[0]++;
      if(Ttrabajo[0]>9){
         Ttrabajo[0]=0;
         Ttrabajo[1]++;
      }
      if(Ttrabajo[1]>9){
         Ttrabajo[1]=0;
         Ttrabajo[2]++;
      }
      if(Ttrabajo[2]>9){
         Ttrabajo[2]=0;
         Ttrabajo[3]++;
      }
      if(Ttrabajo[3]>9){
         Ttrabajo[3]=0;
      }
      write_eeprom(16,Ttrabajo[0]);
      delay_ms(20);
      write_eeprom(17,Ttrabajo[1]);
      delay_ms(20);
      write_eeprom(18,Ttrabajo[2]);
      delay_ms(20);
      write_eeprom(19,Ttrabajo[3]);
      delay_ms(20);
   }else if(minutos_trabajo==900 || minutos_trabajo==1800 || minutos_trabajo==2700 ){
      write_eeprom(37,make8(minutos_trabajo,0));
      delay_ms(20);
      write_eeprom(38,make8(minutos_trabajo,1));
      delay_ms(20);
   }
//----------------------------------------//

//**Contador de Tiempo de Trabajo de UV**//
         if(segundos_uv>=60){
            segundos_uv=0;
            minutos_uv++;
         }
         
         if(minutos_uv>=60){
            minutos_uv=0;
            Tuv[0]++;
            if(Tuv[0]>9){
               Tuv[0]=0;
               Tuv[1]++;
            }
            if(Tuv[1]>9){
               Tuv[1]=0;
               Tuv[2]++;
            }
            if(Tuv[2]>9){
               Tuv[2]=0;
               Tuv[3]++;
            }
            if(Tuv[3]>9){
               Tuv[3]=0;
            }
            write_eeprom(39,minutos_uv);
            delay_ms(20);
            write_eeprom(12,Tuv[0]);
            delay_ms(20);
            write_eeprom(13,Tuv[1]);
            delay_ms(20);
            write_eeprom(14,Tuv[2]);
            delay_ms(20);
            write_eeprom(15,Tuv[3]);
            delay_ms(20);
         }else if(minutos_uv==15 || minutos_uv==30 || minutos_uv==45){
            write_eeprom(39,minutos_uv);
            delay_ms(20);
         }
//----------------------------------------//   
   if(Menu!=5){// Si no esta en el menu de estados apaga la alarma
      Alarma_off;
      Alarma=10;
      Alarma2=10;
   }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------
   if(Menu==240){ //Menu de Bienvenida.
      delay_ms(1500);
      printf("page Clave");
      SendDataDisplay();
      ApagaSalidas();
   }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
    if(Menu==0){ //Menu de Contrase�a de acceso.      
      entro=OFF;
      
      if(RX_Buffer[4]==0x11){//11
         if(TipoClave!=0){
            printf("page Ajustes");
            SendDataDisplay();
            PantallaPrincipal=0;
         }
      }
      
      if(Digito==1){
         printf("t0.txt=\"%u\"",Contrasena[0]);
         SendDataDisplay();
         printf("va0.val=1");
         SendDataDisplay();
      }else if(Digito==2){
         printf("t0.txt=\"%u%u\"",Contrasena[0],Contrasena[1]);
         SendDataDisplay();
         printf("va0.val=2");
         SendDataDisplay();
      }else if(Digito==3){
         printf("t0.txt=\"%u%u%u\"",Contrasena[0],Contrasena[1],Contrasena[2]);
         SendDataDisplay();
         printf("va0.val=3");
         SendDataDisplay();
      }else if(Digito==4){
         printf("t0.txt=\"%u%u%u%u\"",Contrasena[0],Contrasena[1],Contrasena[2],Contrasena[3]);
         SendDataDisplay();
         printf("va0.val=4");
         SendDataDisplay();
      }
      
      if(UP){
         delay_ms(200);
         if(UP){
         if(Digito==0)
            Digito=1;
            if(Contrasena[Digito-1]<9)
               Contrasena[Digito-1]++;
         }
      }
      
      if(DOWN){
         delay_ms(200);
         if(Digito==0)
            Digito=1;
         if(DOWN){
            if(Contrasena[Digito-1]>0)
               Contrasena[Digito-1]--;
         }
      }
      
      if(RIGHT){
         delay_ms(200);
         if(RIGHT){
            Digito++;
         }
      }
      
      if(Digito>4){
      Digito=0;
      printf("t0.txt=\"\"");
      SendDataDisplay();
         if(TipoClave==0){
            if(Contrasena[0]==3&&Contrasena[1]==8&&Contrasena[2]==9&&Contrasena[3]==2){ // Si Ingresa clave para reset general del sistema.
               write_eeprom(0,0);delay_ms(20);write_eeprom(1,0);delay_ms(20);// Reestablece a contrase�a de Fabrica y reinicia Programa.
               write_eeprom(2,0);delay_ms(20);write_eeprom(3,0);delay_ms(20);
               reset_cpu();
            }
            if((Contrasena[0]==Password[0])&&(Contrasena[1]==Password[1])&&(Contrasena[2]==Password[2])&&(Contrasena[3]==Password[3])){
               printf("page ClaveCorrecta");
               SendDataDisplay();
            }else{
               printf("page ClaveBad");
               SendDataDisplay();
            } 
         }else if(TipoClave==1){
            if(Contrasena[0]==4&&Contrasena[1]==4&&Contrasena[2]==4&&Contrasena[3]==4){ // Si Ingresa clave de Servicio Tecnico
               printf("page Config");
               SendDataDisplay();
            }else{
               printf("page Ajustes");
               SendDataDisplay();
            }
         }else if(TipoClave==2){
            if((Contrasena[0]==Password[0])&&(Contrasena[1]==Password[1])&&(Contrasena[2]==Password[2])&&(Contrasena[3]==Password[3])){
               printf("page Clave");
               SendDataDisplay();
               printf("titulo.txt=\"Clave Nueva\"");
               SendDataDisplay();
               TipoClave=3;
            }else{
               printf("page Ajustes");
               SendDataDisplay();
            } 
         }else if(TipoClave==3){
            printf("titulo.txt=\"Clave Nueva\"");
            SendDataDisplay();
            printf("page Ajustes");
            SendDataDisplay();
            if(!GuardaEEPROM){
               write_eeprom(0,RX_Buffer[4]-0x30);delay_ms(20);write_eeprom(1,RX_Buffer[5]-0x30);delay_ms(20);
               write_eeprom(2,RX_Buffer[6]-0x30);delay_ms(20);write_eeprom(3,RX_Buffer[7]-0x30);delay_ms(20);
               Password[0]=RX_Buffer[4]-0x30;
               Password[1]=RX_Buffer[5]-0x30;
               Password[2]=RX_Buffer[6]-0x30;
               Password[3]=RX_Buffer[7]-0x30;
               GuardaEEPROM=ON;
               Alarma_on;delay_ms(1000);Alarma_off;
            }
         }
      }
      
      if(TipoClave==3){
         printf("titulo.txt=\"Clave Nueva\"");
         SendDataDisplay();
         printf("doevents");
         SendDataDisplay();
      }else if(TipoClave==2){
         printf("titulo.txt=\"Clave Actual\"");
         SendDataDisplay();
         printf("doevents");
         SendDataDisplay();
      }else if(TipoClave==1){
         printf("titulo.txt=\"Clave Tecnico\"");
         SendDataDisplay();
         printf("doevents");
         SendDataDisplay();
      }else if(TipoClave==0){
         printf("titulo.txt=\"Ingrese Contrase�a\"");
         SendDataDisplay();
      }
      
      if(RX_Buffer[3]==0x0f){//0f, recibe caracteres ingresados desde el Display
         if(TipoClave==0){
            if(RX_Buffer[4]==0x33&&RX_Buffer[5]==0x38&&RX_Buffer[6]==0x39&&RX_Buffer[7]==0x32){ // Si Ingresa clave para reset general del sistema.
               write_eeprom(0,0);delay_ms(20);write_eeprom(1,0);delay_ms(20);// Reestablece a contrase�a de Fabrica y reinicia Programa.
               write_eeprom(2,0);delay_ms(20);write_eeprom(3,0);delay_ms(20);
               reset_cpu();
            }
            if(RX_Buffer[4]>=0x30 && RX_Buffer[5]>=0x30 && RX_Buffer[6]>=0x30 && RX_Buffer[7]>=0x30
            && RX_Buffer[4]<=0x39 && RX_Buffer[5]<=0x39 && RX_Buffer[6]<=0x39 && RX_Buffer[7]<=0x39){
            if((RX_Buffer[4]==Password[0]+0x30)&&(RX_Buffer[5]==Password[1]+0x30)&&(RX_Buffer[6]==Password[2]+0x30)&&(RX_Buffer[7]==Password[3]+0x30)){
                  printf("page ClaveCorrecta");
                  SendDataDisplay();
               }else{
                  printf("page ClaveBad");
                  SendDataDisplay();
               } 
            }
         }else if(TipoClave==1){
            if(RX_Buffer[4]==0x34&&RX_Buffer[5]==0x34&&RX_Buffer[6]==0x34&&RX_Buffer[7]==0x34){ // Si Ingresa clave de Servicio Tecnico
               printf("page Config");
               SendDataDisplay();
            }else{
               printf("page Ajustes");
               SendDataDisplay();
            }
         }else if(TipoClave==2){
            if(RX_Buffer[4]>=0x30 && RX_Buffer[5]>=0x30 && RX_Buffer[6]>=0x30 && RX_Buffer[7]>=0x30
            && RX_Buffer[4]<=0x39 && RX_Buffer[5]<=0x39 && RX_Buffer[6]<=0x39 && RX_Buffer[7]<=0x39){
            if((RX_Buffer[4]==Password[0]+0x30)&&(RX_Buffer[5]==Password[1]+0x30)&&(RX_Buffer[6]==Password[2]+0x30)&&(RX_Buffer[7]==Password[3]+0x30)){
               printf("page Clave");
               SendDataDisplay();               
               printf("titulo.txt=\"Clave Nueva\"");
               SendDataDisplay();
               TipoClave=3;               
               GuardaEEPROM=0;
               RX_Buffer[3]=0x00;RX_Buffer2[3]=0x00;  
            }else{
               printf("page Ajustes");
               SendDataDisplay();               
               //RX_Buffer[3]=0x00;RX_Buffer2[3]=0x00;  
            } 
            }
         }else if(TipoClave==3){
            printf("titulo.txt=\"Clave Nueva\"");
            SendDataDisplay();
            printf("page Ajustes");
            SendDataDisplay();
            if(!GuardaEEPROM){
               write_eeprom(0,RX_Buffer[4]-0x30);delay_ms(20);write_eeprom(1,RX_Buffer[5]-0x30);delay_ms(20);
               write_eeprom(2,RX_Buffer[6]-0x30);delay_ms(20);write_eeprom(3,RX_Buffer[7]-0x30);delay_ms(20);
               Password[0]=RX_Buffer[4]-0x30;
               Password[1]=RX_Buffer[5]-0x30;
               Password[2]=RX_Buffer[6]-0x30;
               Password[3]=RX_Buffer[7]-0x30;
               GuardaEEPROM=ON;
               Alarma_on;delay_ms(1000);Alarma_off;
            }
         }
      }
    }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
      if(Menu==1){ //Menu Principal
         printf("fecha.txt=\"Fecha:%02u/%02u/20%02u    Hora: %02u:%02u:%02u  \"",Dia,Mes,Year,Hora,Minuto,Segundo);
         SendDataDisplay();
         SendDataDisplay();
         GuardaEEPROM=OFF;
         LuzBlanca_off;LuzUV_off;
         
         if(RX_Buffer[4]==0x01){//Selecciono Tiempo Purga
            printf("page TiempoPurga");
            SendDataDisplay();
         }
         
         if(RX_Buffer[4]==0x02){//Selecciono Tiempo UV
            printf("page TiempoUV");
            SendDataDisplay();            
         }
         
         if(RX_Buffer[4]==0x03){//Selecciono Tiempo Post Purga
            printf("page TimePostP");
            SendDataDisplay();            
         }
         
         if(RX_Buffer[4]==0x04){//Selecciono Mantenimiento
            printf("page Mto");
            SendDataDisplay();            
         }
         
         if(RX_Buffer[4]==0x05){//Selecciono Hora y Fecha
            printf("page Fecha");
            SendDataDisplay();
            DiaTx=Dia;
            MesTx=Mes;
            YearTx=Year;
            HoraTx=Hora;
            MinutoTx=Minuto;
         }
         
         if(RX_Buffer[4]==0x06){//Selecciono Cambio de Clave
            printf("page Clave");
            SendDataDisplay();            
            TipoClave=2;
         }
         
         if(RX_Buffer[4]==0xa0){//Selecciono Servicio
            printf("page Clave");
            SendDataDisplay();            
            TipoClave=1;
         }
         
         if(RX_Buffer[4]==0xb0){//Selecciono Regresar
            printf("page Principal");
            SendDataDisplay();                        
         }
      }
 //_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
      if(Menu==2){ //Menu de Tiempo de Purga
         printf("fecha.txt=\"Fecha:%02u/%02u/20%02u    Hora: %02u:%02u:%02u  \"",Dia,Mes,Year,Hora,Minuto,Segundo);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Selecciono Minutos
            Minutos=ON;
            Segundos=OFF;
         }
         
         if(RX_Buffer[4]==0x0b){//Selecciono Segundos
            Segundos=ON;
            Minutos=OFF;
         }           
         
         if(RIGHT){
            delay_ms(200);
            if(RIGHT){
               if(!Segundos){
                  Segundos=ON;
                  Minutos=OFF;
                  printf("tsecest.pco=65535");
                  SendDataDisplay();  
                  printf("tminest.pco=0");
                  SendDataDisplay();  
               }else if(!Minutos){
                  Segundos=OFF;
                  Minutos=ON;
                  printf("tsecest.pco=0");
                  SendDataDisplay();  
                  printf("tminest.pco=65535");
                  SendDataDisplay();  
               }
            }
         }
         
         if(RX_Buffer[4]==0x11 || LEFT){//Selecciono Regresar
            delay_ms(200);
            if(RX_Buffer[4]==0x11 || LEFT){
               Segundos=OFF;
               Minutos=OFF;
               printf("page Ajustes");
               SendDataDisplay();  
               if(!GuardaEEPROM){
                  write_eeprom(4,mPurgap);
                  delay_ms(10);
                  write_eeprom(5,sPurgap);
                  delay_ms(10);
                  //mPurga=mPurgap;
                  //sPurga=sPurgap;
                  GuardaEEPROM=ON;
               }
            }
         }
         
         if(RX_Buffer[4]==0x0c || RX_Buffer[4]==0x0d || UP){//Tecla arriba Oprimida
            delay_ms(10);
            if(RX_Buffer[4]==0x0c || RX_Buffer[4]==0x0d || UP){//Tecla arriba Oprimida
               if(Segundos){
                  sPurgap++;
               }
               if(Minutos){
                  mPurgap++;
               }  
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;
            }
         }
         
         if(RX_Buffer[4]==0x0f || RX_Buffer[4]==0x0e || DOWN){//Tecla abajo oprimida
            delay_ms(10);
            if(RX_Buffer[4]==0x0f || RX_Buffer[4]==0x0e || DOWN){//Tecla abajo oprimida
               if(Segundos && sPurgap>0){
                  sPurgap--;
               }
               if(Minutos){
                  mPurgap--;
               }
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;
            }
         }
         
         if(mPurgap<1)mPurgap=99;
         if(mPurgap>99)mPurgap=1;
         if(sPurgap>59)sPurgap=0;
            
         printf("tminest.txt=\"%02u\"",mPurgap);
         SendDataDisplay();
         printf("tsecest.txt=\"%02u\"",sPurgap);
         SendDataDisplay();
    }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
      if(Menu==3){ //Menu de Tiempo de Post-Purga
         printf("fecha.txt=\"Fecha:%02u/%02u/20%02u    Hora: %02u:%02u:%02u  \"",Dia,Mes,Year,Hora,Minuto,Segundo);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Selecciono Minutos
            Minutos=ON;
            Segundos=OFF;
         }
         
         if(RX_Buffer[4]==0x0b){//Selecciono Segundos
            Segundos=ON;
            Minutos=OFF;
         }           
         
         if(RIGHT){
            delay_ms(200);
            if(RIGHT){
               if(!Segundos){
                  Segundos=ON;
                  Minutos=OFF;
                  printf("tsecest.pco=65535");
                  SendDataDisplay();  
                  printf("tminest.pco=0");
                  SendDataDisplay();  
               }else if(!Minutos){
                  Segundos=OFF;
                  Minutos=ON;
                  printf("tsecest.pco=0");
                  SendDataDisplay();  
                  printf("tminest.pco=65535");
                  SendDataDisplay();  
               }
            }
         }
         
         if(RX_Buffer[4]==0x11 || LEFT){//Selecciono Regresar
            delay_ms(200);
            if(RX_Buffer[4]==0x11 || LEFT){
               Segundos=OFF;
               Minutos=OFF;
               printf("page Ajustes");
               SendDataDisplay();  
               if(!GuardaEEPROM){
                  write_eeprom(6,mPPurgap);
                  delay_ms(10);
                  write_eeprom(7,sPPurgap);
                  delay_ms(10);
                  //mPPurga=mPPurgap;
                  //sPPurga=sPPurgap;
                  GuardaEEPROM=ON;
               }
            }
         }        
         
         if(RX_Buffer[4]==0x0c || RX_Buffer[4]==0x0d || UP){//Tecla arriba Oprimida
            delay_ms(10);
            if(RX_Buffer[4]==0x0c || RX_Buffer[4]==0x0d || UP){//Tecla arriba Oprimida
               if(Segundos){
                  sPPurgap++;
               }
               if(Minutos){
                  mPPurgap++;
               }  
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;
            }
         }
         
         if(RX_Buffer[4]==0x0f || RX_Buffer[4]==0x0e || DOWN){//Tecla abajo oprimida
            delay_ms(10);
            if(RX_Buffer[4]==0x0f || RX_Buffer[4]==0x0e || DOWN){//Tecla abajo oprimida
               if(Segundos && sPPurgap>0){
                  sPPurgap--;
               }
               if(Minutos){
                  mPPurgap--;
               }
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;
            }
         }
         
         if(mPPurgap<1)mPPurgap=99;
         if(mPPurgap>99)mPPurgap=1;
         if(sPPurgap>59)sPPurgap=0;
            
         printf("tminest.txt=\"%02u\"",mPPurgap);
         SendDataDisplay();
         printf("tsecest.txt=\"%02u\"",sPPurgap);
         SendDataDisplay();
    }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
      if(Menu==4){//Menu de Purga.
         ubicaVidrio20cm();
         if(VIDRIOUP){
            Purga=ON;
            tiempo_purga=1;
            printf("titulo.txt=\"Purga\"");
            SendDataDisplay();
         }else{
            Purga=OFF;
            tiempo_purga=0;
            printf("titulo.txt=\"!Ubicando el vidrio!\"");
            SendDataDisplay();
         }
         LuzBlanca_off;//Alarma_off;
         printf("minPurga.txt=\"%02u\"",mPurga);
         SendDataDisplay();
         printf("secPurga.txt=\"%02u\"",sPurga);
         SendDataDisplay();
         printf("fecha.txt=\"Fecha:%02u/%02u/20%02u    Hora: %02u:%02u:%02u  \"",Dia,Mes,Year,Hora,Minuto,Segundo);
         SendDataDisplay();
         VelMotor=Vel1;
         VelMotor2=Vel2;
         Aux_on;
         
         if(RX_Buffer[4]==0x11 || RIGHT){
            delay_ms(1000);
            if(RX_Buffer[4]==0x11 || RIGHT){
               tiempo_purga=0;
               printf("page Principal");
               SendDataDisplay();
               flag_luz=1;flag_blower=1;flag_toma=1;
            }
         }
       
         if((sPurga>=sPurgap)&&(mPurga>=mPurgap)){
            tiempo_purga=0;flag_luz=!flag_luz;flag_blower=1;flag_toma=1;
            printf("page Principal");
            SendDataDisplay();
         }      
      }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
      if(Menu==5){ //Menu General.
         Purga=OFF;
         w=0;
         UVTime=Tuv[0]+(Tuv[1]*10)+(Tuv[2]*100)+(Tuv[3]*1000);         
         VelMotor2=Vel2;
         Aux_on;
         if(imprimir==1){
            printf("fex.txt=\"Velocidad: %u \"",VelMotor);
            SendDataDisplay();
            printf("fdown.txt=\"Saturaci�n: %2.2f m/s\"",Filtro_Downflow);
            SendDataDisplay();
            printf("fecha.txt=\"Fecha:%02u/%02u/20%02u    Hora: %02u:%02u:%02u \"",Dia,Mes,Year,Hora,Minuto,Segundo);
            //printf("fecha.txt=\"Pdn:%2.2f  %04Luf  %04Lu\"",Filtro_Downflow,Presion[4].adc,sensores(4));
            SendDataDisplay();
            imprimir=0;
         }
      
// Dibujo de barra de estado de filtro                  
      barra=100.0*(Filtro_Downflow/1.0);
      if(barra>100.0)
         barra=100.0;
      
      if(barra<0.0)
         barra=0.0;
      
      printf("pdown.txt=\"%03u%%\"",(int8)barra);
      SendDataDisplay();
      dibujaBarraDn();
      
      if(Filtro_Downflow>0.9){
         flag_latencia=1;
         if(Latencia==1){         
            if(Filtro_Downflow>0.9){
               Alarma=5;
            }
         }
      }else{
         Latencia=0;t_latencia=0;flag_latencia=0;
         Alarma=10;
      }
      
      if(VIDRIOUP){ // Si el vidrio esta en la posici�n correcta         
         Alarma2=10;
         if(flag_luz==1){
            printf("b0.pic=17");
            SendDataDisplay();    
            LuzBlanca_on;
         }else{
            printf("b0.pic=16");
            SendDataDisplay(); 
            LuzBlanca_off;
         }
      }else{// Si el vidrio no esta en la posici�n correcta
         LuzBlanca_off;
         printf("b0.pic=16");
         SendDataDisplay(); 
      }
         
      if(VIDRIODN){  
         Alarma2=10;
         flag_blower=0;
         //flag_uv=1;
         flag_Luz=0;LuzBlanca_off;
         if(flag_uv){
            tiempo_uv=1;
            LuzUV_on;
            printf("b1.pic=25");
            SendDataDisplay(); 
            if(guardauv==1){
               guardauv=0;
               write_eeprom(39,minutos_uv);
               delay_ms(20);
               write_eeprom(12,Tuv[0]);
               delay_ms(20);
               write_eeprom(13,Tuv[1]);
               delay_ms(20);
               write_eeprom(14,Tuv[2]);
               delay_ms(20);
               write_eeprom(15,Tuv[3]);
               delay_ms(20);
            }               
         }else{
            LuzUV_off;tiempo_uv=0;
            printf("b1.pic=26");
            SendDataDisplay(); 
            if(guardauv==0){
               guardauv=1;
               write_eeprom(39,minutos_uv);
               delay_ms(20);
               write_eeprom(12,Tuv[0]);
               delay_ms(20);
               write_eeprom(13,Tuv[1]);
               delay_ms(20);
               write_eeprom(14,Tuv[2]);
               delay_ms(20);
               write_eeprom(15,Tuv[3]);
               delay_ms(20);
            }
         }
      }else{
         LuzUV_off;tiempo_uv=0;ECO=OFF;
         printf("b1.pic=26");
         SendDataDisplay(); 
         flag_uv=0;
      }
         
      if(Alarma==5){
         printf("estado.txt=\"Cambie Filtro\"");
         SendDataDisplay();    
         printf("icon.pic=29");
         SendDataDisplay(); 
         //cambiaColor(63911);
      }else{
         if(!VIDRIOUP){               
            printf("estado.txt=\"Ubique Vidrio\"");
            SendDataDisplay();  
            printf("icon.pic=20");
            SendDataDisplay(); 
            //cambiaColor(56544);
            Alarma2=5;
         }else{   
            if(ECO){
               printf("estado.txt=\"Modo Eco\"");
               SendDataDisplay(); 
            }else{
               printf("estado.txt=\"Operaci�n Segura\"");
               SendDataDisplay();    
            }
            printf("icon.pic=19");
            SendDataDisplay(); 
            Alarma2=10;
            //cambiaColor(0);
         }
      }   
                  
      if(flag_blower==1){
         printf("b2.pic=27");
         SendDataDisplay(); 
         tiempo_trabajo=1;
         if(guardatrabajo==1){
            VelMotor=6;
            guardatrabajo=0;
            write_eeprom(37,make8(minutos_trabajo,0));
            delay_ms(20);
            write_eeprom(38,make8(minutos_trabajo,1));
            delay_ms(20);
            write_eeprom(16,Ttrabajo[0]);
            delay_ms(20);
            write_eeprom(17,Ttrabajo[1]);
            delay_ms(20);
            write_eeprom(18,Ttrabajo[2]);
            delay_ms(20);
            write_eeprom(19,Ttrabajo[3]);
            delay_ms(20);            
         }
         VelMotor=Vel1;
      }else{
         //Filtro_Downflow=0;
         printf("b2.pic=28");
         SendDataDisplay(); 
         tiempo_trabajo=0;VelMotor=0;
         if(guardatrabajo==0){
            guardatrabajo=1;
            write_eeprom(37,make8(minutos_trabajo,0));
            delay_ms(20);
            write_eeprom(38,make8(minutos_trabajo,1));
            delay_ms(20);
            write_eeprom(16,Ttrabajo[0]);
            delay_ms(20);
            write_eeprom(17,Ttrabajo[1]);
            delay_ms(20);
            write_eeprom(18,Ttrabajo[2]);
            delay_ms(20);
            write_eeprom(19,Ttrabajo[3]);
            delay_ms(20);
         }
      }
      
      if(DOWN){
         delay_ms(10);
         if(DOWN){
            flag_luz=!flag_luz;
         }
      }
      
      if(RIGHT && LEFT){
         delay_ms(500);
         if(RIGHT && LEFT){
            printf("page PostPurga");
            SendDataDisplay();
         }
      }else{
         if(RIGHT){
            delay_ms(10);
            if(RIGHT){
               flag_blower=!flag_blower;
            }
         }
         
         if(LEFT){
            delay_ms(10);
            if(LEFT){
               flag_uv=!flag_uv;
            }
         }
      }
      
      if(UP){//Si oprime boton de Toma.         
         delay_ms(500);
         if(UP){//Si oprime boton de Toma.
            delay_ms(30);
            printf("page Ajustes");
            SendDataDisplay();
         }else{
            delay_ms(30);flag_toma=!flag_toma;
         }
      }
      
      if(RX_Buffer[4]==0x01){         
         flag_luz=!flag_luz;
         RX_Buffer[4]=0x00;  
         RX_Buffer2[4]=0x00;
      }
      
      if(RX_Buffer[4]==0x02){
         flag_blower=!flag_blower;
         RX_Buffer[4]=0x00;  
         RX_Buffer2[4]=0x00;
      }
      
      if(RX_Buffer[4]==0x03){
         flag_toma=!flag_uv;
         RX_Buffer[4]=0x00;  
         RX_Buffer2[4]=0x00;
      }
      
      if(RX_Buffer[4]==0x04){
         flag_uv=!flag_uv;
         RX_Buffer[4]=0x00;  
         RX_Buffer2[4]=0x00;
      }
      
      if(RX_Buffer[4]==0x05){         
         printf("page Ajustes");
         SendDataDisplay();
      }
      
      if(RX_Buffer[4]==0x0a){         
         subirVidrio();
         BYTE_IR[3]=0x00;
      }
      
      if(RX_Buffer[4]==0x0c){         
         bajarVidrio();
         BYTE_IR[3]=0x00;
      }
      
      if(RX_Buffer[4]==0x0b || RX_Buffer[4]==0x0d){         
         detenerVidrio();
         BYTE_IR[3]=0x00;
      }
      /*
      if(RX_Buffer[4]==0x06){         
         printf("page PostPurga");
         SendDataDisplay();
      }
      */
            
    }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
      if(Menu==6){//Menu de Post-Purga.
         tiempo_postpurga=1;
         LuzBlanca_off;LuzUV_off;Toma_off;Alarma_off;
         printf("minPurga.txt=\"%02u\"",mPPurga);
         SendDataDisplay();
         printf("secPurga.txt=\"%02u\"",sPPurga);
         SendDataDisplay();
         VelMotor=Vel1;
         VelMotor2=Vel2;
         Aux_on;
         printf("fecha.txt=\"Fecha:%02u/%02u/20%02u    Hora: %02u:%02u:%02u  \"",Dia,Mes,Year,Hora,Minuto,Segundo);
         SendDataDisplay();
         
         if(RX_Buffer[4]==0x11){
            tiempo_postpurga=0;
            printf("page Principal");
            SendDataDisplay();
            RX_Buffer[4]=0x00;  
            RX_Buffer2[4]=0x00;
         }
       
         if((sPPurga>=sPPurgap)&&(mPPurga>=mPPurgap)){
            tiempo_postpurga=0;
            ApagaSalidas();
            //Display_off;
            Encendio=OFF;
            VelMotor=0;VelMotor2=0;LuzBlanca_off;LuzUV_off;Toma_off;Alarma_off;Motor_off;Motor2_off;Aux_off;
            printf("page Off");
            SendDataDisplay();
            reset_cpu();
         }      
      }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------      
      if(Menu==7){ //Menu de Configuraci�n de Fecha y Hora
         if(RX_Buffer[4]==0x0a){//Selecciono Hora
            Opcion=4;
         }
         
         if(RX_Buffer[4]==0x0b){//Selecciono Minuto
            Opcion=5;
         }
         
         if(RX_Buffer[4]==0x0c){//Selecciono Dia
            Opcion=1;
         }
         
         if(RX_Buffer[4]==0x0d){//Selecciono Hora
            Opcion=2;
         }
         
         if(RX_Buffer[4]==0x0e){//Selecciono Year
            Opcion=3;
         }
         
         if(RIGHT){
            delay_ms(200);
            if(RIGHT){
               Opcion++;
            }
         }
         if(Opcion>5)
            Opcion=1;
         if(Opcion<1)
            Opcion=5;
            
         if(RX_Buffer[4]==0x11 || LEFT){//Selecciono Regresar
            printf("page Ajustes");
            SendDataDisplay();  
            PantallaPrincipal=0;
            
            if(esBisiesto(YearTx))
               Modulo=Bisiesto[MesTx];
            else
               Modulo=Regular[MesTx];
         
            dowTx=((YearTx-1)%7+((YearTx-1)/4-3*((YearTx-1)/100+1)/4)%7+Modulo+DiaTx%7)%7;
            rtc_set_datetime(DiaTx,MesTx,YearTx,dowTx,HoraTx,MinutoTx);
         }
         
         if((RX_Buffer[4]==0x2a)|| UP){//Tecla Arriba Oprimida
            delay_ms(200);
            if((RX_Buffer[4]==0x2a)|| UP){   
               if(Opcion==1){
                  if(MesTx==2){
                     if(esBisiesto(YearTx)){
                        if(DiaTx<29)
                           DiaTx++;
                        else
                           DiaTx=1;   
                     }else{
                        if(DiaTx<28)
                           DiaTx++;
                        else
                           DiaTx=1;   
                     }
                  }else{
                     if(MesTx<=7){
                        if(MesTx % 2 ==0){
                           if(DiaTx<30)
                              DiaTx++;    
                           else
                              DiaTx=1;   
                        }else{
                           if(DiaTx<31)
                              DiaTx++;    
                           else
                              DiaTx=1;   
                        }    
                     }else{
                        if(MesTx % 2 ==0){
                           if(DiaTx<31)
                              DiaTx++;  
                           else
                              DiaTx=1;
                        }else{
                           if(DiaTx<30)
                              DiaTx++;    
                           else
                              DiaTx=1;
                        }    
                     }
                  }
               }else if(Opcion==2){
                  if(MesTx<12)
                     MesTx++;
                  else
                     MesTx=1;
               }else if(Opcion==3){
                  if(YearTx<99)
                     YearTx++;
                  else 
                     YearTx=0;
               }else if(Opcion==4){
                  if(HoraTx<24)
                     HoraTx++;
                  else
                     HoraTx=0;
               }else if(Opcion==5){
                  if(MinutoTx<59)
                     MinutoTx++;
                  else
                     MinutoTx=0;
               }
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;
            }
         }
         
         if(RX_Buffer[4]==0x2b || DOWN){//Tecla Abajo Oprimida
            delay_ms(200);
            if(RX_Buffer[4]==0x2b || DOWN){   
               if(Opcion==1){
                  if(DiaTx>0)
                     DiaTx--;
               }else if(Opcion==2){
                  if(MesTx>1)
                     MesTx--;
                  else
                     MesTx=12;
               }else if(Opcion==3){
                  if(YearTx>0)
                     YearTx--;
                  else
                     YearTx=99;
               }else if(Opcion==4){
                  if(HoraTx>0)
                     HoraTx--;
                  else
                     HoraTx=23;
               }else if(Opcion==5){
                  if(MinutoTx>0)
                     MinutoTx--;
                  else
                     MinutoTx=59;
               }
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;
            }
         }
      
      printf("thora.txt=\"%02u\"",HoraTx);
      SendDataDisplay();
      printf("tminutos.txt=\"%02u\"",MinutoTx);
      SendDataDisplay();
      printf("tdia.txt=\"%02u\"",DiaTx);
      SendDataDisplay();
      printf("tmes.txt=\"%02u\"",MesTx);
      SendDataDisplay();
      printf("tyear.txt=\"%02u\"",YearTx);
      SendDataDisplay();
      LimitaDia();  
    }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------      
    if(Menu==8){//Menu de off
      VelMotor=0;VelMotor2=0;LuzBlanca_off;LuzUV_off;Toma_off;Alarma_off;Motor_off;Motor2_off;Aux_off;
      mPurga=0;sPurga=0;mPPurga=0;sPPurga=0;mUV=0;sUV=0;flagClave=0;tClave=0;
      tiempo_purga=0;tiempo_postpurga=0;TipoClave=0;
      if(!Encendio)
         //Display_off;
      
      if(RIGHT || UP || DOWN){
         delay_ms(500);
         if(RIGHT || UP || DOWN){
            Display_on;
            Encendio=ON;
            delay_ms(1000);
            printf("page Bienvenida");
            SendDataDisplay();                  
         }
      }
      
      if(RX_Buffer[4]==0x11){
         printf("page Bienvenida");
         SendDataDisplay();                  
      }
    }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
      if(Menu==9){ //Menu de Tiempo de UV
         printf("fecha.txt=\"Fecha:%02u/%02u/20%02u    Hora: %02u:%02u:%02u  \"",Dia,Mes,Year,Hora,Minuto,Segundo);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Selecciono Minutos
            Minutos=ON;
            Segundos=OFF;
         }
         
         if(RX_Buffer[4]==0x0b){//Selecciono Segundos
            Segundos=ON;
            Minutos=OFF;
         }    
         
         if(RIGHT){
            delay_ms(200);
            if(RIGHT){
               if(!Segundos){
                  Segundos=ON;
                  Minutos=OFF;
                  printf("tsecest.pco=65535");
                  SendDataDisplay();  
                  printf("tminest.pco=0");
                  SendDataDisplay();  
               }else if(!Minutos){
                  Segundos=OFF;
                  Minutos=ON;
                  printf("tsecest.pco=0");
                  SendDataDisplay();  
                  printf("tminest.pco=65535");
                  SendDataDisplay();  
               }
            }
         }
         
         if(RX_Buffer[4]==0x11 || LEFT){//Selecciono Regresar
            delay_ms(200);
            if(RX_Buffer[4]==0x11 || LEFT){
               Segundos=OFF;
               Minutos=OFF;
               printf("page Ajustes");
               SendDataDisplay();  
               if(!GuardaEEPROM){
                  write_eeprom(8,mUVp);
                  delay_ms(10);
                  write_eeprom(9,sUVp);
                  delay_ms(10);
                  mUV=mUVp;
                  sUV=sUVp;
                  GuardaEEPROM=ON;
               }
            }
         }
         
         if(RX_Buffer[4]==0x0c || RX_Buffer[4]==0x0d || UP){//Tecla arriba Oprimida
            delay_ms(10);
            if(RX_Buffer[4]==0x0c || RX_Buffer[4]==0x0d || UP){//Tecla arriba Oprimida
               if(Segundos){
                  sUVp++;
               }
               if(Minutos){
                  mUVp++;
               }  
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;
            }
         }
         
         if(RX_Buffer[4]==0x0f || RX_Buffer[4]==0x0e || DOWN){//Tecla abajo oprimida
            delay_ms(10);
            if(RX_Buffer[4]==0x0f || RX_Buffer[4]==0x0e || DOWN){//Tecla abajo oprimida
               if(Segundos && sUVp>0){
                  sUVp--;
               }
               if(Minutos){
                  mUVp--;
               }
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;
            }
         }
         
         if(mUVp<1)mUVp=99;
         if(mUVp>99)mUVp=1;
         if(sUVp>59)sUVp=0;
            
         printf("tminest.txt=\"%02u\"",mUVp);
         SendDataDisplay();
         printf("tsecest.txt=\"%02u\"",sUVp);
         SendDataDisplay();
    }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

//_--------------------------------------------------------------------------------------------------------------------------------------------------------      
    if(Menu==10){ // Menu de Mantenimiento
         
         LuzBlanca_on;
         UVTime=Tuv[0]+(Tuv[1]*10)+(Tuv[2]*100)+(Tuv[3]*1000);   
         TrabajoTime=Ttrabajo[0]+(Ttrabajo[1]*10)+(Ttrabajo[2]*100)+(Ttrabajo[3]*1000);
         
         printf("b0.txt=\"UV(horas):%05Lu %X\"",(int16)UVTime,BYTE_IR[3]);
         SendDataDisplay();
         printf("b1.txt=\"Filtro(horas):%05Lu\"",(int16)TrabajoTime);
         SendDataDisplay();
         
         printf("ceroIn.txt=\"Cero InFlow: %2.0f\"",Flujo[1].zero_actual);
         SendDataDisplay();
         printf("ceroDn.txt=\"Cero DnFlow: %2.0f\"",Flujo[2].zero_actual);
         SendDataDisplay();
         printf("ajusteIn.txt=\"Ajuste InFlow: %2.1f\"",Flujo[1].ajuste);
         SendDataDisplay();
         printf("ajusteDn.txt=\"Ajuste InFlow: %2.1f\"",Flujo[2].ajuste);
         SendDataDisplay();
         printf("inflow.txt=\"InFlow: %2.2f %2.2f \"",Flujo[1].Velocidad,Flujo[1].Flujo);
         SendDataDisplay();
         printf("downflow.txt=\"DownFlow: %2.2f\"",DownFlow);
         SendDataDisplay();
         printf("adcin.txt=\"ADCIn: %04Lu\"",(int16)Flujo[1].adc);
         SendDataDisplay();
         printf("adcdn.txt=\"ADCDn: %04Lu\"",sensores(2));
         SendDataDisplay();
         
         if(RX_Buffer[4]==0x01 || (UP && RIGHT)){ // Reinicia Tiempo de Luz UV         
            delay_ms(2000); 
            if(RX_Buffer[4]==0x01 || (UP && RIGHT)){ // Reinicia Tiempo de Luz UV
               write_eeprom(12,0);
               delay_ms(20);
               write_eeprom(13,0);
               delay_ms(20);
               write_eeprom(14,0);
               delay_ms(20);
               write_eeprom(15,0);
               delay_ms(20);
               Tuv[3]=0;Tuv[2]=0;Tuv[1]=0;Tuv[0]=0;
               LuzBlanca_off;
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;
               printf("page Ajustes");
               SendDataDisplay();
            }
         }
      
         if(RX_Buffer[4]==0x02 || (DOWN && RIGHT)){ // Reinicia Tiempo de Trabajo        
            delay_ms(2000);
            if(RX_Buffer[4]==0x02 || (DOWN && RIGHT)){ // Reinicia Tiempo de Trabajo        
               write_eeprom(16,0);
               delay_ms(20);
               write_eeprom(17,0);
               delay_ms(20);
               write_eeprom(18,0);
               delay_ms(20);
               write_eeprom(19,0);
               delay_ms(20);
               Ttrabajo[3]=0;Ttrabajo[2]=0;Ttrabajo[1]=0;Ttrabajo[0]=0;
               LuzBlanca_off;
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;
               printf("page Ajustes");
               SendDataDisplay();
            }
         }
         
         if(RX_Buffer[4]==0x21 || LEFT){//Oprimio salir
            delay_ms(50);
            if(RX_Buffer[4]==0x21 || LEFT){
               printf("page Ajustes");
               SendDataDisplay();
               LuzBlanca_off;
               // Falta apagar Todo
            }
         }
     }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------    

//_--------------------------------------------------------------------------------------------------------------------------------------------------------      
    if(Menu==15){//Menu de Clave Correcta
      if(!entro){
         delay_ms(2000);
         entro=ON;
      }
      printf("page Purga");
      SendDataDisplay();
   }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------      

//_--------------------------------------------------------------------------------------------------------------------------------------------------------      
    if(Menu==16){//Menu de Clave InCorrecta
      if(!entro){
         delay_ms(2000);
         entro=ON;
      }
      printf("page Clave");
      SendDataDisplay();
   }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------      

//_--------------------------------------------------------------------------------------------------------------------------------------------------------
      if(Menu==100){ //Menu de Configuraci�n de Parametros de Control
     
      if(Opcion2>13)
         Opcion2=1;
      if(Opcion2<1)
         Opcion2=13;
      
      if(Opcion2==1){
         printf("Config.t2c.txt=\"Velocidad\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%u\"",Vel1);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida
               Vel1+=1.0;
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;            
               //BorraBuffer();
         }
            
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida
               Vel1-=1.0;
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;            
               //BorraBuffer();
         }
         VelMotor=Vel1;
         if(Vel1>6)
            Vel1=1;
         if(Vel1<1)
            Vel1=6;
      }else if(Opcion2==2){
         VelMotor=0;
         VelMotor2=0;
         printf("Config.t2c.txt=\"Cero InFlow\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%2.0f\"",Flujo[1].zero_actual);
         SendDataDisplay();
         
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida
            Flujo[1].zero_actual=sensores(1);
            Flujo[1].adc=sensores(1);
            Flujo[1].Diferencia=zero_fabrica-Flujo[1].adc;
               
            if(Flujo[1].Diferencia>=0){
               Flujo[1].negativo=10;write_eeprom(50,Flujo[1].negativo);delay_ms(20);
            }
            if(Flujo[1].Diferencia<0){
               Flujo[1].negativo=20;write_eeprom(50,Flujo[1].negativo);delay_ms(20);
            }
            Flujo[1].Diferencia=abs(Flujo[1].Diferencia);
            Temporal1=(int16)Flujo[1].Diferencia;
            write_eeprom(42,make8(Temporal1,0));
            delay_ms(20);
            write_eeprom(43,make8(Temporal1,1));//Guardar valor de Setpoint en eeprom
            delay_ms(20);
   
            Temporal1=(int16)Flujo[1].zero_actual;
            write_eeprom(33,make8(Temporal1,0));
            delay_ms(20);
            write_eeprom(34,make8(Temporal1,1));//Guardar valor de Setpoint en eeprom
            delay_ms(20);
            RX_Buffer[4]=0x00;  
            RX_Buffer2[4]=0x00;                           
         }
            
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida            
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;            
               //BorraBuffer();
         }
      }else if(Opcion2==3){
         VelMotor=0;
         VelMotor2=0;
         printf("Config.t2c.txt=\"Cero Flujo2\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%2.0f\"",Flujo[2].zero_actual);
         SendDataDisplay();
         
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida
            Flujo[2].zero_actual=sensores(2);
            Flujo[2].adc=sensores(2);
            Flujo[2].Diferencia=zero_fabrica-Flujo[2].adc;
               
            if(Flujo[2].Diferencia>=0){
               Flujo[2].negativo=10;write_eeprom(51,Flujo[2].negativo);delay_ms(20);
            }
            if(Flujo[2].Diferencia<0){
               Flujo[2].negativo=20;write_eeprom(51,Flujo[2].negativo);delay_ms(20);
            }
            Flujo[2].Diferencia=abs(Flujo[2].Diferencia);
            Temporal1=(int16)Flujo[2].Diferencia;
            write_eeprom(44,make8(Temporal1,0));
            delay_ms(20);
            write_eeprom(45,make8(Temporal1,1));//Guardar valor de Setpoint en eeprom
            delay_ms(20);
   
            Temporal1=(int16)Flujo[2].zero_actual;
            write_eeprom(35,make8(Temporal1,0));
            delay_ms(20);
            write_eeprom(36,make8(Temporal1,1));//Guardar valor de Setpoint en eeprom
            delay_ms(20);
            RX_Buffer[4]=0x00;  
            RX_Buffer2[4]=0x00;                           
         }
            
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida            
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;            
               //BorraBuffer();
         }
      }else if(Opcion2==4){
         printf("Config.t2c.txt=\"Ajuste InFlow\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%2.1f\"",Flujo[1].Ajuste);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida            
               if(Flujo[1].Ajuste<10)
                  Flujo[1].Ajuste+=0.1;
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;                           
         }
            
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida            
               if(Flujo[1].Ajuste>0)
                  Flujo[1].Ajuste-=0.1;
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;            
               //BorraBuffer();
         }
      }else if(Opcion2==5){
         printf("Config.t2c.txt=\"Ajuste Flujo2\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%2.1f\"",Flujo[2].Ajuste);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida
               if(Flujo[2].Ajuste<10)
                  Flujo[2].Ajuste+=0.1;
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;            
               //BorraBuffer();
         }
            
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida
               if(Flujo[2].Ajuste>0)
                  Flujo[2].Ajuste-=0.1;
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;            
               //BorraBuffer();            
         }
      }else if(Opcion2==6){
         VelMotor=0;
         VelMotor2=0;
         printf("Config.t2c.txt=\"Cero P1\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%2.0f\"",Presion[4].G);
         //printf("t3c.txt=\"%2.0f\"",Gdn);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida            
            Presion[4].G=sensores(4);//Presion[4].adc;
            G16=(int16)Presion[4].G;
            //Gdn=sensores(4);//Presion[4].adc;
            //G16=(int16)Gdn;
            G_l=G16; G_h=(G16>>8);
               
            write_eeprom(46,G_l);
            delay_ms(20);
            write_eeprom(47,G_h);//Guardar valor de Setpoint en eeprom
            delay_ms(20);
            RX_Buffer[4]=0x00;  
            RX_Buffer2[4]=0x00;
            Alarma_on;delay_ms(500);Alarma_off;
         }
            
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida            
               
         }
      }else if(Opcion2==7){
         VelMotor=0;
         VelMotor2=0;
         printf("Config.t2c.txt=\"Cero P2\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%2.0f\"",Presion[3].G);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida            
            Presion[3].G=sensores(3);//Presion[3].adc;
            G16=(int16)Presion[3].G;
            G_l=G16; G_h=(G16>>8);
               
            write_eeprom(48,G_l);
            delay_ms(20);
            write_eeprom(49,G_h);//Guardar valor de Setpoint en eeprom
            delay_ms(20);
            RX_Buffer[4]=0x00;  
            RX_Buffer2[4]=0x00;
            Alarma_on;delay_ms(500);Alarma_off;
         }
         
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida            
               
         }
      }else if(Opcion2==8){
         printf("Config.t2c.txt=\"Ajuste Presion1\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%2.1f\"",Presion[4].Ajuste);
         //printf("t3c.txt=\"%2.1f\"",Ajustedn);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida            
               if(Presion[4].Ajuste<5.0)
                  Presion[4].Ajuste+=0.1;
               //if(Ajustedn<5.0)
               //   Ajustedn+=0.1;
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;            
         }
            
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida
               if(Presion[4].Ajuste>0.0)
                  Presion[4].Ajuste-=0.1;
               //if(Ajustedn>0.0)
               //   Ajustedn-=0.1;
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;            
         }
      }else if(Opcion2==9){
         printf("Config.t2c.txt=\"Ajuste Presion2\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%2.1f\"",Presion[3].Ajuste);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida            
               if(Presion[3].Ajuste<5.0)
                  Presion[3].Ajuste+=0.1;
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;            
         }
            
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida
               if(Presion[3].Ajuste>0.0)
                  Presion[3].Ajuste-=0.1;
               RX_Buffer[4]=0x00;
               RX_Buffer2[4]=0x00;            
         }
      }else if(Opcion2==10){
         printf("Config.t2c.txt=\"Subir Codigo\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%X\"",codigoSubir);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida
               codigoSubir=BYTE_IR[3];
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;            
               write_eeprom(54,codigoSubir);
               delay_ms(20);
               Alarma_on;delay_ms(500);Alarma_off;
         }
      }else if(Opcion2==11){
         printf("Config.t2c.txt=\"Bajar Codigo\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%X\"",codigoBajar);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida
               codigoBajar=BYTE_IR[3];
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;            
               write_eeprom(55,codigoBajar);
               delay_ms(20);
               Alarma_on;delay_ms(500);Alarma_off;
         }
      }else if(Opcion2==12){
         printf("Config.t2c.txt=\"Detener Codigo\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%X\"",codigoDetener);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida
               codigoDetener=BYTE_IR[3];
               RX_Buffer[4]=0x00;  
               RX_Buffer2[4]=0x00;            
               write_eeprom(56,codigoDetener);
               delay_ms(20);
               Alarma_on;delay_ms(500);Alarma_off;
         }
      }else if(Opcion2==13){
         printf("Config.t2c.txt=\"Media Movil\"");
         SendDataDisplay();      
         printf("t3c.txt=\"%02u\"",mediaMovil);
         SendDataDisplay();
         if(RX_Buffer[4]==0x0a){//Tecla Arriba Oprimida
            mediaMovil++;
            RX_Buffer[4]=0x00;  
            RX_Buffer2[4]=0x00;            
         }
         if(RX_Buffer[4]==0x0b){//Tecla Abajo Oprimida
            mediaMovil--;
            RX_Buffer[4]=0x00;
            RX_Buffer2[4]=0x00;            
         }
         if(mediaMovil>80)
            mediaMovil=10;
         if(mediaMovil<10)
            mediaMovil=80;   
      }
      
      if(RX_Buffer[4]==0x0c){//Tecla Izquierda Oprimida
            Opcion2++;
            RX_Buffer[4]=0x00;  
            RX_Buffer2[4]=0x00;            
            //BorraBuffer();   
      }
         
      if(RX_Buffer[4]==0x0d){//Tecla Derecha Oprimida         
            Opcion2--;
            RX_Buffer[4]=0x00;
            RX_Buffer2[4]=0x00;            
            //BorraBuffer();
      }
      
      if(RX_Buffer[4]==0x11){//Tecla Regresar
         delay_ms(20);
         if(RX_Buffer[4]==0x11){
            if(!GuardaEEPROM){
               Temporal=Flujo[1].Ajuste;
               Entero=(int)Flujo[1].Ajuste;
               Temporal=Temporal-Entero;
               Temporal2=Temporal*10.0;
               Decimal1=(int8)Temporal2;
               write_eeprom(25,Entero);
               delay_ms(20);
               write_eeprom(26,Decimal1);
               delay_ms(20);
               
               Temporal=Flujo[2].Ajuste;
               Entero=(int)Flujo[2].Ajuste;
               Temporal=Temporal-Entero;
               Temporal2=Temporal*10.0;
               Decimal1=(int8)Temporal2;
               write_eeprom(27,Entero);
               delay_ms(20);
               write_eeprom(28,Decimal1);
               delay_ms(20);
               
               Temporal=Presion[4].Ajuste;
               Entero=(int)Presion[4].Ajuste;
               //Temporal=Ajustedn;
               //Entero=(int)Ajustedn;
               Temporal=Temporal-Entero;
               Temporal2=Temporal*10.0;
               Decimal1=(int8)Temporal2;
               write_eeprom(29,Entero);
               delay_ms(20);
               write_eeprom(30,Decimal1);
               delay_ms(20);
               
               Temporal=Presion[3].Ajuste;
               Entero=(int)Presion[3].Ajuste;
               Temporal=Temporal-Entero;
               Temporal2=Temporal*10.0;
               Decimal1=(int8)Temporal2;
               write_eeprom(31,Entero);
               delay_ms(20);
               write_eeprom(32,Decimal1);
               delay_ms(20);
               write_eeprom(40,Vel1);
               delay_ms(20);
               write_eeprom(41,Vel2);
               delay_ms(20);
               
               Temporal=Relacion;
               Entero=(int)Relacion;
               Temporal=Temporal-Entero;
               Temporal2=Temporal*100.0;
               Decimal1=(int8)Temporal2;
               write_eeprom(52,Entero);
               delay_ms(20);
               write_eeprom(53,Decimal1);
               delay_ms(20);
               
               write_eeprom(57,mediaMovil);
               delay_ms(20);
               GuardaEEPROM=ON;
            }
            printf("page Ajustes");
            SendDataDisplay();
            //BorraBuffer();
         }
      }
      printf("fecha.txt=\"Fecha:%02u/%02u/20%02u    Hora: %02u:%02u\"",Dia,Mes,Year,Hora,Minuto);
      SendDataDisplay(); 
    }
//_--------------------------------------------------------------------------------------------------------------------------------------------------------       

   }

}
int8 BIT_BYTE(int1 D0, int1 D1, int1 D2, int1 D3, int1 D4, int1 D5, int1 D6, int1 D7){
   int8 dato;
   dato= D7*128 + D6*64 + D5*32 + D4*16 + D3*8 + D2*4 + D1*2 +D0*1; // 0 al 255 
   return dato; 
}
