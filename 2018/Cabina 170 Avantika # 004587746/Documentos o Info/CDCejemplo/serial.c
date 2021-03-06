// Includes all USB code and interrupts, as well as the CDC API
#include <18F4550.h>
#device adc=10;                  //Conversor A/D de 10 bits
#fuses HSPLL,NOWDT,NOPROTECT,NOLVP,NODEBUG,USBDIV,PLL1,CPUDIV1,VREGEN
#use delay(clock=48000000)
#include <usb_cdc.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

// Definición de Entradas
#define   UP             input(PIN_D7)
#define   DOWN           input(PIN_D6)
#define   RIGHT          input(PIN_D5)
#define   LEFT           input(PIN_D4)


int16 valor_digital;          //Valor digital obtenido del conversor A/D
float valor_analogico;        //Valor analógico 
char recepcion;               //Dato recibido del PC
int8 control,Numero=0,i=0,j=255,h=128;
int8 datoTx[11]={0,0,0,0,0,0,0,0,0,0,0};
int8 datoRx[11]={0,0,0,0,0,0,0,0,0,0,0};
int16 Flujo=1023;


void EnviarNumero(int8 Numero)
{
   datoTx[0]=255;
   datoTx[1]=255;
   datoTx[2]=255;
   datoTx[3]=Numero;
   datoTx[4]=make8(Flujo,0);
   datoTx[5]=make8(Flujo,1);
   datoTx[6]=128;
   datoTx[7]=255;
   datoTx[8]=255;
   datoTx[9]=255;
   datoTx[10]=128;
               
      printf(usb_cdc_putc_fast,datoTx);
      delay_ms(4);
   
}


void main() {

usb_cdc_init();
usb_init();
usb_task();
   while(!usb_cdc_connected()) {}

   do {
      if (usb_enumerated()) {
                      
            if(UP)
            {               
               Numero=1;
               EnviarNumero(Numero);
               delay_ms(100);
            }
            else
            {
               if(DOWN)
               {
                  Numero=2;      
                  EnviarNumero(Numero);
                  delay_ms(100);
               }
               else
               {
                  if(RIGHT)
                  {
                     Numero=3;
                     EnviarNumero(Numero);
                     delay_ms(100);
                  }  
                  else
                  {
                     if(LEFT)
                     {
                        Numero=4;
                        EnviarNumero(Numero);
                        delay_ms(100);
                     } 
                     else
                     {
                        Numero=44;
                        EnviarNumero(Numero);
                        delay_ms(100);
                     }
                  }
               }
            }
            
            
         if (usb_cdc_kbhit()){         
            recepcion=usb_cdc_getc();  //lo lee
            printf(usb_cdc_putc,recepcion);
           //si es caracter vacio(barra espaciadora)enciende o apaga visualización
            
           
         }
         
      }
   } 
   
   while (TRUE);
}
