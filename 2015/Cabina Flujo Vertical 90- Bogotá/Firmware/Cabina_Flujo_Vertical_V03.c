// Cuatro pulsadores de entrada (Derecha, Izquierda, arriba y abajo).
// Reloj con XT de 4 MHz.
// Programaci�n para Cabina de Flujo Laminar Vertical.
// Tiene Contrase�a de incio para permitir funcionamiento de Cabina.
// Tiene Men�:Luz UV, Luz Blanca, Motor Ventilador y Cambio de Contrase�a.
// Permite cambiar la velocidad del motor digitalmente.
// Ing. Juan David Pi�eros.
// JP Inglobal.

#include <18F4550.h>
#fuses HSPLL,NOWDT,NOPROTECT,NOLVP,NODEBUG,USBDIV,PLL1,CPUDIV1,NOVREGEN,NOMCLR// PLL1 para 4 MHz
//#fuses XT,NOWDT,NOPROTECT,NOLVP,NODEBUG,VREGEN,NOMCLR// PLL1 para 4 MHz
#use delay(clock=48000000)
#include <LCD420.c>

// Definici�n de teclado - NC
#define   UP            input(PIN_A0)
#define   DOWN          input(PIN_A1)
#define   RIGHT         input(PIN_A2)
#define   LEFT          input(PIN_A3)

// Definici�n de otros puertos
#define   Luz_UV_on         output_bit(PIN_D0,1)
#define   Luz_UV_off        output_bit(PIN_D0,0)
#define   Luz_Blanca_on     output_bit(PIN_D1,1)
#define   Luz_Blanca_off    output_bit(PIN_D1,0)
#define   Motor_L1_on       output_bit(PIN_D2,1)
#define   Motor_L1_off      output_bit(PIN_D2,0)
#define   Motor_L2_on       output_bit(PIN_D3,1)
#define   Motor_L2_off      output_bit(PIN_D3,0)
#define   Motor_L3_on       output_bit(PIN_D4,1)
#define   Motor_L3_off      output_bit(PIN_D4,0)
#define   Motor_L4_on       output_bit(PIN_D5,1)
#define   Motor_L4_off      output_bit(PIN_D5,0)

int8 Menu=0, n_opcionH=7,n_opcionL=2,unidad=11,Flecha=2,Flecha1=3,Flecha2=2, nMenuL=2, nMenuH=4,segundos=0,minutos=0;
short estadouv=0,estadofl=0,estadomv=0,Flanco=0,Flanco1=0,Flanco2=0,Flanco3=0;
signed int8   paso=0,Velocidad=1;
char t[3]={'>',' ','^'}; 
signed  int8 clave[4]={0,0,0,0};   // 0=Verdadero, 1=Falso
int8 contrasena[4]={0,0,0,0};   // 0=Verdadero, 1=Falso

int16 tiempos,horas=0;
signed int  tiempo2[5]={0,0,0,0,0};   // Tiempo transcurrido
signed int8 temporizador[5]={0,0,0,0,0};   // Tiempo transcurrido

#int_TIMER1
void temp1s(void){
   set_timer1(5536);  // 5 ms
   tiempos++;
   if(tiempos==200)   // Ha transcurrido una decima de segundo (PIC18F4550 con XT = 16MHZ)
   {tiempos=0;
   //----------------------------- Tiempo Total-----------------------------//
   if(estadouv==1)
   {
      segundos++;
      if(segundos==60)
      {
         segundos=0;minutos++;
      }
      if(minutos==60)
      {
         minutos=0;horas++;
      }
   }
        
   }
}


void mensajes(int8 x,y)// Funcion para imprimir mensajes de Menu Principal.
{  if(x==1)
   {if(estadouv==0)
      {lcd_gotoxy(2,y);printf(lcd_putc,"LUZ UV          OFF");}
    if(estadouv==1)  
      {lcd_gotoxy(2,y);printf(lcd_putc,"LUZ UV           ON");}
   }
   if(x==2)
   {if(estadofl==0)
      {lcd_gotoxy(2,y);printf(lcd_putc,"LUZ BLANCA      OFF");}
    if(estadofl==1)
      {lcd_gotoxy(2,y);printf(lcd_putc,"LUZ BLANCA       ON");}  
   }
   if(x==3)
   {if(estadomv==0)
      {lcd_gotoxy(2,y);printf(lcd_putc,"VENTILADOR      OFF");}
   if(estadomv==1)
      {lcd_gotoxy(2,y);printf(lcd_putc,"VENTILADOR       ON");}
   }
   if(x==4)
   {lcd_gotoxy(2,y);printf(lcd_putc,"VELOCIDAD DE FLUJO ");}
   if(x==5)
   {lcd_gotoxy(2,y);printf(lcd_putc,"CAMBIO CONTRASENA  ");}
   if(x==6)
   {lcd_gotoxy(2,y);printf(lcd_putc,"TIEMPO LUZ UV      ");}
}

void velocidades(int8 x)// Funcion para imprimir mensajes de Menu Principal.
{  if(x==0)
      {Motor_L2_off;Motor_L3_off;Motor_L4_off;Motor_L1_off;}
   if(x==1)
      {Motor_L2_off;Motor_L3_off;Motor_L4_off;Motor_L1_on;}
   if(x==2)
      {Motor_L3_off;Motor_L4_off;Motor_L1_off;Motor_L2_on;}
   if(x==3)
      {Motor_L4_off;Motor_L1_off;Motor_L2_off;Motor_L3_on;}
   if(x==4)
      {Motor_L2_off;Motor_L3_off;Motor_L1_off;Motor_L4_on;}
}

void main ()
{
   output_d(0);
   lcd_init();
   enable_interrupts(global);
   setup_timer_1(T1_INTERNAL|T1_DIV_BY_1);
   enable_interrupts(INT_TIMER1);
   lcd_gotoxy(1,1);
   printf(lcd_putc,"  CABINA DE FLUJO   ");
   lcd_gotoxy(1,2);
   printf(lcd_putc,"  LAMINAR VERTICAL  ");
   lcd_gotoxy(1,3);
   printf(lcd_putc,"    JP INGLOBAL     ");
   lcd_gotoxy(1,4);
   printf(lcd_putc,"   REF:  JPCFV90    ");
   delay_ms(2000);
   printf(lcd_putc,"\f");
   
   //Solo para simulacion
   //write_eeprom(0,0);delay_ms(20);write_eeprom(1,0);delay_ms(20);
   //write_eeprom(2,0);delay_ms(20);write_eeprom(3,0);delay_ms(20);
   Velocidad=read_eeprom(5);
   
   // Lee la contrase�a almacenada en la eeprom para poder comprobar con la que escribe el usuario.
   contrasena[0]=read_eeprom(0);delay_ms(20); contrasena[1]=read_eeprom(1);delay_ms(20);
   contrasena[2]=read_eeprom(2);delay_ms(20); contrasena[3]=read_eeprom(3);delay_ms(20);
   
   segundos=read_eeprom(50);minutos=read_eeprom(51);horas=read_eeprom(52);
  
   while(true){

//------------Menu0------------------------------------------------------------------   
   if(Menu == 0){ // Menu de Contrase�a para Poder iniciar el equipo
   lcd_gotoxy(1,1);
   printf(lcd_putc,"     Ingrese        ");
   lcd_gotoxy(1,2);
   printf(lcd_putc,"    Contrase�a      ");
   lcd_gotoxy(1,3);
   printf(lcd_putc,"    CLAVE=%i%i%i%i  ",clave[0],clave[1],clave[2],clave[3]);
   
      
   if(UP)//Si oprime hacia arriba
      {  if(Flanco == 0) 
            {clave[unidad-11]++;Flanco = 1;delay_ms(30);}}
         else
            {Flanco = 0;}
            
      if(DOWN)//Si oprime hacia abajo
      {  if(Flanco2 == 0) 
            {clave[unidad-11]--;Flanco2 = 1;delay_ms(30);}}
         else
            {Flanco2 = 0;}
   
      if(RIGHT)// Si Oprime Derecha
      {  if(Flanco1 == 0) 
            {Flanco1 = 1;unidad++;delay_ms(30);printf(lcd_putc,"\f");}}
         else
            {Flanco1 = 0;}   
            
       if(LEFT)// Si Oprime izquierda
      {  if(Flanco3 == 0) 
            {Flanco3 = 1;unidad--;delay_ms(30);printf(lcd_putc,"\f");}}
         else
            {Flanco3 = 0;}    
            
       if(clave[unidad-11]<0)     // Si la unidad donde se encuentra ubicado el cursor es menor que 0 pasa a 9.
       {clave[unidad-11]=9;}
       if(clave[unidad-11]>9)     // Si la unidad donde se encuentra ubicado el cursor es mayor que 9 pasa a 0.
       {clave[unidad-11]=0;}
       if(unidad<11)             // Si trata de correr mas a la izquierda de la primera unidad, deja el cursor en esa posicion.
       {unidad=11;}
       lcd_gotoxy(unidad,4);// Para mostrar cursor.
       lcd_putc(t[2]);
       if(unidad>14)             // Si a Terminado de ingresar la clave, verifica si es correcta o no.
       {
         if(clave[0]==3&&clave[1]==8&&clave[2]==9&&clave[3]==2) // Si Ingresa clave para reset general del sistema.
            {write_eeprom(0,0);delay_ms(20);write_eeprom(1,0);delay_ms(20);// Reestablece a contrase�a de Fabrica y reinicia Programa.
             write_eeprom(2,0);delay_ms(20);write_eeprom(3,0);delay_ms(20);
             reset_cpu();}
            
         if(clave[0]==contrasena[0]&&clave[1]==contrasena[1]&&clave[2]==contrasena[2]&&clave[3]==contrasena[3]) // Si las claves coinciden pasa a Menu Principal.
            {lcd_gotoxy(1,1);
            printf(lcd_putc,"                   ");
            lcd_gotoxy(1,2);
            printf(lcd_putc,"     Contrase�a    ");
            lcd_gotoxy(1,3);
            printf(lcd_putc,"      Correcta     ");
            lcd_gotoxy(1,4);
            printf(lcd_putc,"                   ");
            delay_ms(1000);Menu=1;unidad=11;printf(lcd_putc,"\f");}
         else                                         // Si la clave no coincide vuelve a mostrar el menu para ingresar la clave.
         {lcd_gotoxy(1,1);
            printf(lcd_putc,"");
            lcd_gotoxy(1,2);
            printf(lcd_putc,"     Contrase�a    ");
            lcd_gotoxy(1,3);
            printf(lcd_putc,"     Incorrecta    ");
            lcd_gotoxy(1,4);
            printf(lcd_putc,"                   ");
            delay_ms(1000);unidad=11;printf(lcd_putc,"\f");}
       }
   
       if(unidad>11&&unidad<14)
          { lcd_gotoxy(unidad-1,4);// Para mostrar cursor.
            lcd_putc(t[1]);
          }

   }
//----------------Fin-Menu0---------------------------------------------------------------   

//------------Menu1------------------------------------------------------------------   
   if(Menu == 1){ // Menu de seleccion de lo que desea encender
   lcd_gotoxy(1,1);
   printf(lcd_putc,"---MENU PRINCIPAL---");
      if(paso<0)
        {paso=0;}
        
   if(UP)//Si oprime hacia arriba
      {  if(Flanco == 0) 
            {Flecha2--;Flecha--;Flecha1=Flecha+1;Flanco = 1;delay_ms(30);}}
         else
            {Flanco = 0;}
            
      if(DOWN)//Si oprime hacia abajo
      {  if(Flanco2 == 0) 
            {Flecha2++;Flecha++;Flecha1=Flecha-1;Flanco2 = 1;delay_ms(30);}}
         else
            {Flanco2 = 0;}
   
        
        if(Flecha2>nMenuH)
        {paso++;nMenuH=Flecha2;nMenuL=nMenuH-2;Flecha=4;}
        
        if(Flecha2<nMenuL)
        {paso--;nMenuL=Flecha2;nMenuH=nMenuL+2;Flecha=2;}
        
        if(Flecha2>n_opcionH)
        {Flecha2=n_opcionL;Flecha=2;paso=0;nMenuL=Flecha2;nMenuH=nMenuL+2;}
        
        if(Flecha2<n_opcionL)
        {Flecha2=n_opcionH;Flecha=4;paso=3;nMenuH=Flecha2;nMenuL=nMenuH-2;}               
        
        mensajes(1+paso,2);
        mensajes(2+paso,3);
        mensajes(3+paso,4);

        lcd_gotoxy(1,Flecha);// Para mostrar la flecha de seleccion
        lcd_putc(t[0]);

        if(Flecha==2)
          {lcd_gotoxy(1,4);// Para mostrar la flecha de seleccion
            lcd_putc(t[1]);
            lcd_gotoxy(1,3);// Para mostrar la flecha de seleccion
            lcd_putc(t[1]);
          }
        
        if(Flecha==4)
          {lcd_gotoxy(1,2);// Para mostrar la flecha de seleccion
            lcd_putc(t[1]);
            lcd_gotoxy(1,3);// Para mostrar la flecha de seleccion
            lcd_putc(t[1]);
          }
        
        if(Flecha==3)
          { lcd_gotoxy(1,4);// Para mostrar la flecha de seleccion
            lcd_putc(t[1]);
            lcd_gotoxy(1,2);// Para mostrar la flecha de seleccion
            lcd_putc(t[1]);
          }

        if(RIGHT)// Si oprime derecha
      {  if(Flanco1 == 0) 
            {Flanco1 = 1;Menu=Flecha2;Flecha=3;delay_ms(30);printf(lcd_putc,"\f");
            if(Menu==6)
            {clave[0]=0;clave[1]=0;clave[2]=0;clave[3]=0;}}}
         else
            {Flanco1 = 0;}   

   }
//----------------Fin-Menu1---------------------------------------------------------------      
   
//----------------Menu2---------------------------------------------------------------
   if(Menu == 2){ // Menu de seleccion de Estado de Luz UV
            
         Menu=1;paso=0;
            
         if(estadofl==0)   
         {  estadouv=!estadouv;
            if(estadouv==1)
            {estadouv=1;
               lcd_gotoxy(1,1);
               printf(lcd_putc,"                    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"      Encendio      ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"       Luz UV       ");
               lcd_gotoxy(1,4);
               printf(lcd_putc,"                    ");
               Luz_UV_on;}
               
            if(estadouv==0)
            {estadouv=0;Luz_UV_off;
               lcd_gotoxy(1,1);
               printf(lcd_putc,"                    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"       Apago        ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"      Luz  UV       ");
               lcd_gotoxy(1,4);
               printf(lcd_putc,"                    ");
               write_eeprom(50,segundos);write_eeprom(51,minutos);write_eeprom(52,horas);
            }
            }   
            
         if(estadofl==1)   
            {
               lcd_gotoxy(1,1);
               printf(lcd_putc,"!! Para encender    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"     la Luz UV      ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"  apague Luz Blanca ");
               lcd_gotoxy(1,4);
               printf(lcd_putc,"      Primero!!     ");
            }   
             
            delay_ms(1000);Flecha=2;Flecha2=2;printf(lcd_putc,"\f");      
   }
//----------------Fin-Menu2--------------------------------------------------------------   
   
//----------------Menu3---------------------------------------------------------------
   if(Menu == 3){ // Menu de seleccion de Estado de Luz Blanca
            
            Menu=1; paso=0;
        if(estadouv==0)
        {   estadofl=!estadofl;
            if(estadofl==1)
            {estadofl=1;Luz_Blanca_on;
               lcd_gotoxy(1,1);
               printf(lcd_putc,"                    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"      Encendio      ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"     Luz Blanca     ");
               lcd_gotoxy(1,4);
               printf(lcd_putc,"                    ");}
           
            if(estadofl==0)
            {estadofl=0;Luz_Blanca_off;
               lcd_gotoxy(1,1);
               printf(lcd_putc,"                    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"       Apago        ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"    Luz  Blanca     ");
               lcd_gotoxy(1,4);
               printf(lcd_putc,"                    ");}
        }    
        
        if(estadouv==1)   
            {
               lcd_gotoxy(1,1);
               printf(lcd_putc,"!! Para encender    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"   la Luz Blanca    ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"  apague la Luz UV  ");
               lcd_gotoxy(1,4);
               printf(lcd_putc,"      Primero!!     ");
            }   
             
            delay_ms(1000);Flecha=2;Flecha2=2;printf(lcd_putc,"\f");           
   }      
//----------------Fin-Menu3---------------------------------------------------------------

//----------------Menu4---------------------------------------------------------------
   if(Menu == 4){ // Menu de seleccion de estado de Motor Ventilador
               
            estadomv=!estadomv;Flanco1 = 1;Menu=1; paso=0;
                      
            if(estadomv==1)
            {estadomv=1;velocidades(read_eeprom(5)); 
               lcd_gotoxy(1,1);
               printf(lcd_putc,"                    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"      Encendio      ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"     Ventilador     ");
               lcd_gotoxy(1,4);
               printf(lcd_putc,"                    ");}
               
            if(estadomv==0)
            {estadomv=0;velocidades(0); 
               lcd_gotoxy(1,1);
               printf(lcd_putc,"                    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"       Apago        ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"     Ventilador     ");
               lcd_gotoxy(1,4);
               printf(lcd_putc,"                    ");}
             
            delay_ms(1000);Flecha=2;Flecha2=2;printf(lcd_putc,"\f");
   }      
//----------------Fin-Menu4---------------------------------------------------------------

//----------------Menu5---------------------------------------------------------------
   if(Menu == 5){ // Menu de seleccion de estado de Motor Ventilador
   lcd_gotoxy(1,1);
   printf(lcd_putc,"Seleccione Velocidad");
   lcd_gotoxy(1,2);
   printf(lcd_putc,"    de Ventilador   ");
   lcd_gotoxy(8,3);
   printf(lcd_putc,"%i",Velocidad);

   if(UP)//Si oprime hacia arriba
      {  if(Flanco == 0) 
            {Velocidad++;Flanco = 1;delay_ms(30);}}
         else
            {Flanco = 0;}
            
      if(DOWN)//Si oprime hacia abajo
      {  if(Flanco2 == 0) 
            {Velocidad--;Flanco2 = 1;delay_ms(30);}}
         else
            {Flanco2 = 0;}
      
            
      if(LEFT)//Si oprime Izquierda
      {  if(Flanco3 == 0) 
            {Flecha=2;Flecha2=2;Flanco3 = 1;Menu=1; paso=0;delay_ms(30);}}
         else
            {Flanco3 = 0;}
         if(Velocidad<1)
            {Velocidad=4;}
         if(Velocidad>4)
            {Velocidad=1;}    
                    
      if(RIGHT)// Si oprime Derecha
      {  if(Flanco1 == 0) 
            {Flanco1 = 1;Menu=1;paso=0; write_eeprom(5,Velocidad);
               
             if(estadomv==1)
               {velocidades(Velocidad);}
               
               lcd_gotoxy(1,1);
               printf(lcd_putc,"                    ");
               lcd_gotoxy(1,2);
               printf(lcd_putc,"   Ha seleccionado  ");
               lcd_gotoxy(1,3);
               printf(lcd_putc,"  la velocidad  %i  ",Velocidad);
               lcd_gotoxy(1,4);
               printf(lcd_putc,"                    ");
             
            delay_ms(1000);Flecha=2;Flecha2=2;printf(lcd_putc,"\f");}}
         else
            {Flanco1 = 0;}   

        lcd_gotoxy(6,Flecha);// Para mostrar la flecha de seleccion
        lcd_putc(t[0]);
        
   }      
//----------------Fin-Menu5---------------------------------------------------------------

//----------------Menu6---------------------------------------------------------------
   if(Menu == 6){ // Menu para Cambio de Contrase�a
   lcd_gotoxy(1,1);
   printf(lcd_putc,"      Ingrese       ");
   lcd_gotoxy(1,2);
   printf(lcd_putc," Contrase�a Actual  ");
   lcd_gotoxy(1,3);
   printf(lcd_putc,"    CLAVE=%i%i%i%i  ",clave[0],clave[1],clave[2],clave[3]);
   
      
   if(UP)//Si oprime hacia arriba
      {  if(Flanco == 0) 
            {clave[unidad-11]++;Flanco = 1;delay_ms(30);}}
         else
            {Flanco = 0;}
            
      if(DOWN)//Si oprime hacia abajo
      {  if(Flanco2 == 0) 
            {clave[unidad-11]--;Flanco2 = 1;delay_ms(30);}}
         else
            {Flanco2 = 0;}
   
      if(RIGHT)// Si oprime derecha
      {  if(Flanco1 == 0) 
            {Flanco1 = 1;unidad++;delay_ms(30);printf(lcd_putc,"\f");}}
         else
            {Flanco1 = 0;}   
            
       if(LEFT)// Si oprime Izquierda
      {  if(Flanco3 == 0) 
            {Flanco3 = 1;unidad--;delay_ms(30);printf(lcd_putc,"\f");}}
         else
            {Flanco3 = 0;}    
            
       if(clave[unidad-11]<0)// Si la unidad donde se encuentra ubicado el cursor es menor que 0 pasa a 9.     
       {clave[unidad-11]=9;}
       if(clave[unidad-11]>9)// Si la unidad donde se encuentra ubicado el cursor es mayor que 9 pasa a 0.     
       {clave[unidad-11]=0;}
       if(unidad<11)
       {Menu=1;paso=0;Flecha=2;Flecha2=2;unidad=11;}
       lcd_gotoxy(unidad,4);// Para mostrar la flecha de seleccion
       lcd_putc(t[2]);
       if(unidad>14)// Si ya ingreso la contrase�a muestra si es correcta o no, dependiendo si ingreso la clave correctamente.
       {
         if(clave[0]==contrasena[0]&&clave[1]==contrasena[1]&&clave[2]==contrasena[2]&&clave[3]==contrasena[3])
            {lcd_gotoxy(1,1);
            printf(lcd_putc,"                    ");
            lcd_gotoxy(1,2);
            printf(lcd_putc,"     Contrase�a     ");
            lcd_gotoxy(1,3);
            printf(lcd_putc,"      Correcta      ");
            lcd_gotoxy(1,4);
            printf(lcd_putc,"                    ");
            delay_ms(500);Menu=61;unidad=11;printf(lcd_putc,"\f");
            clave[0]=0;clave[1]=0;clave[2]=0;clave[3]=0;}
         else
         {lcd_gotoxy(1,1);
            printf(lcd_putc,"                    ");
            lcd_gotoxy(1,2);
            printf(lcd_putc,"     Contrase�a     ");
            lcd_gotoxy(1,3);
            printf(lcd_putc,"     Incorrecta     ");
            lcd_gotoxy(1,4);
            printf(lcd_putc,"                    ");
            delay_ms(500);unidad=11;printf(lcd_putc,"\f");}
       }
               
          if(unidad>11&&unidad<14)
          { lcd_gotoxy(unidad-1,4);// Para mostrar cursor.
            lcd_putc(t[1]);
          }
   }      
//----------------Fin-Menu6---------------------------------------------------------------

//----------------Menu6.1---------------------------------------------------------------
   if(Menu == 61){ // Menu cuando ingresa correctamente la contrase�a, permite que digite nueva contrase�a.
   lcd_gotoxy(1,1);
   printf(lcd_putc,"     Ingrese        ");
   lcd_gotoxy(1,2);
   printf(lcd_putc,"  Contrase�a Nueva  ");
   lcd_gotoxy(1,3);
   printf(lcd_putc,"    CLAVE=%i%i%i%i  ",clave[0],clave[1],clave[2],clave[3]);
   
      
   if(UP)//Si oprime hacia arriba
      {  if(Flanco == 0) 
            {clave[unidad-11]++;Flanco = 1;delay_ms(30);}}
         else
            {Flanco = 0;}
            
      if(DOWN)//Si oprime hacia abajo
      {  if(Flanco2 == 0) 
            {clave[unidad-11]--;Flanco2 = 1;delay_ms(30);}}
         else
            {Flanco2 = 0;}
   
      if(RIGHT)// Si oprime Derecha
      {  if(Flanco1 == 0) 
            {Flanco1 = 1;unidad++;delay_ms(30);printf(lcd_putc,"\f");}}
         else
            {Flanco1 = 0;}   
            
       if(LEFT)// Si oprime Izquierda
      {  if(Flanco3 == 0) 
            {Flanco3 = 1;unidad--;delay_ms(30);printf(lcd_putc,"\f");}}
         else
            {Flanco3 = 0;}    
            
       if(clave[unidad-11]<0)// Si la unidad donde se encuentra ubicado el cursor es menor que 0 pasa a 9.     
       {clave[unidad-11]=9;}
       if(clave[unidad-11]>9)// Si la unidad donde se encuentra ubicado el cursor es mayor que 9 pasa a 0.     
       {clave[unidad-11]=0;}
       if(unidad<11)
       {Menu=5;unidad=11;}
       lcd_gotoxy(unidad,4);// Para mostrar la flecha de seleccion
       lcd_putc(t[2]);
       if(unidad>14)// Si ya ingreso la nueva contrase�a.
       {
            lcd_gotoxy(1,1);
            printf(lcd_putc,"                    ");
            lcd_gotoxy(1,2);
            printf(lcd_putc,"     Contrase�a     ");
            lcd_gotoxy(1,3);
            printf(lcd_putc,"     Almacenada     ");
            lcd_gotoxy(1,4);
            printf(lcd_putc,"                    ");
            write_eeprom(0,clave[0]);delay_ms(20);write_eeprom(1,clave[1]);delay_ms(20);
            write_eeprom(2,clave[2]);delay_ms(20);write_eeprom(3,clave[3]);delay_ms(20);
            delay_ms(500);Menu=1;paso=0;Flecha=2;Flecha2=2;
       }
      
           if(unidad>11&&unidad<14)
          { lcd_gotoxy(unidad-1,4);// Para mostrar cursor.
            lcd_putc(t[1]);
          }
   }      
//----------------Fin-Menu6.1---------------------------------------------------------------
   
//----------------Menu7---------------------------------------------------------------
   if(Menu == 7){ // Menu cuando ingresa correctamente la contrase�a, permite que digite nueva contrase�a.
   lcd_gotoxy(1,1);
   printf(lcd_putc,"  Duracion Actual   ");
   lcd_gotoxy(1,2);
   printf(lcd_putc,"   Tiempo= %05Lu   ",horas);
   lcd_gotoxy(1,4);
   printf(lcd_putc," RESET= Oprima ^ y > ");
   
   }
   
      if(UP && RIGHT)//Si oprime hacia arriba
      {  delay_ms(200);
         printf(lcd_putc,"\f");
         lcd_gotoxy(1,2);
         printf(lcd_putc," Reset de tiempo ");
         lcd_gotoxy(1,3);
         printf(lcd_putc,"     Exitoso     ");
         write_eeprom(50,0);write_eeprom(51,0);write_eeprom(52,0);
         delay_ms(700);
         segundos=0;minutos=0;horas=0;
         delay_ms(30);Menu=1; paso=0;Flecha=2;Flecha2=2;printf(lcd_putc,"\f");
      }
            
       if(LEFT)// Si oprime Izquierda
      {  if(Flanco3 == 0) 
            {Flanco3 = 1;delay_ms(30);Menu=1; paso=0;Flecha=2;Flecha2=2;printf(lcd_putc,"\f");}}
         else
            {Flanco3 = 0;}    
//----------------Fin-Menu7---------------------------------------------------------------

   }
}

