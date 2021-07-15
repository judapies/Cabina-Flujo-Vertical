/////////////////////////////////////////////////////////////////////////
////                                                                 ////
////  rr2_HID_kbd_usb_Monitor.c                                      ////
////                                                                 ////
////  by RedPic                                                      ////
////                                                                 ////
////  08/10/2006                                                     ////
////                                                                 ////
/////////////////////////////////////////////////////////////////////////

#include <18F4550.h>
#device adc=10
#fuses HSPLL,NOWDT,NOPROTECT,NOLVP,NODEBUG,USBDIV,PLL5,CPUDIV1,VREGEN
#use delay(clock=48000000)


#DEFINE USB_HID_DEVICE  TRUE

#define USB_EP1_TX_ENABLE  USB_ENABLE_INTERRUPT   //turn on EP1 for IN bulk/interrupt transfers
#define USB_EP1_TX_SIZE 18

#define USB_EP1_RX_ENABLE  USB_ENABLE_INTERRUPT   //turn on EP1 for IN bulk/interrupt transfers
#define USB_EP1_RX_SIZE 18

#include <pic18_usb.h>
//#include <.\include\usb_kbd_HID.h>               //USB Configuration and Device descriptors for this UBS device
#include <usb_desc_hid.h>
#include <usb.c>                                 //handles usb setup tokens and get descriptor reports
#include <ctype.h>

/////////////////////////////////////////////////////////////////////////////
//
// Defines 
//
/////////////////////////////////////////////////////////////////////////////
#define   UP               input(PIN_B0)// Temporalmente para pruebas
#define   DOWN             input(PIN_B1)
#define   LEFT             input(PIN_B2)
#define   RIGHT            input(PIN_B3)
#define   MOTOR            input(PIN_B4)
#define   LUZ              input(PIN_B5)
#define   UV               input(PIN_B6)
#define   TOMA             input(PIN_B7)

#define   LuzBlanca_on           output_bit(PIN_D2,1)
#define   LuzBlanca_off          output_bit(PIN_D2,0)
#define   LuzUV_on               output_bit(PIN_D3,1)
#define   LuzUV_off              output_bit(PIN_D3,0)
#define   MotorIn_on             output_bit(PIN_C7,1)
#define   MotorIn_off            output_bit(PIN_C7,0)
#define   Toma_on                output_bit(PIN_D4,1)
#define   Toma_off               output_bit(PIN_D4,0)
#define   Alarma_on              output_bit(PIN_D5,1)
#define   Alarma_off             output_bit(PIN_D5,0)
#define   MotorDn_on             output_bit(PIN_C6,1)
#define   MotorDn_off            output_bit(PIN_C6,0)
#define   Fuente_on              output_bit(PIN_D6,1)
#define   Fuente_off             output_bit(PIN_D6,0)

/////////////////////////////////////////////////////////////////////////////
//
// RAM
//
/////////////////////////////////////////////////////////////////////////////

int8 enumerated;
int8 rx_msg[USB_EP1_RX_SIZE];
int8 tx_msg[18]={255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,255,255,128};

int1 hay_dato=0;

int16 tiempos=0;
// Variables para Pt100
short capturar=0;
unsigned int8 Inflow_l=0,Inflow_h=0,Downflow_l=0,Downflow_h=0,Filtro_Inflow_l=0,Filtro_Inflow_h=0,apagado=0;
unsigned int8 Filtro_Downflow_l=0,Filtro_Downflow_h=0,tiempos2=0,flancou=20,flancod=0,flancor=0,flancol=0,flancom=0,flancob=0,flancov=0,flancot=0;
unsigned int8 flancoud=0,flancodd=0,flancord=0,flancold=0,flancomd=0,flancobd=0,flancovd=0,flancotd=0;
unsigned int16 Inflow=0.0,Downflow=0.0,Filtro_Inflow=0.0,Filtro_Downflow=0.0;
float VidrioDN=0.0,VidrioUP=0.0;

/////////////////////////////////////////////////////////////////////////////
//
// usb_debug_task()
//
// When called periodically, displays debugging information over serial
// to display enumeration and connection states.  Also lights LED2 and LED3
// based upon enumeration and connection status.
//
/////////////////////////////////////////////////////////////////////////////

void usb_debug_task(void) {

   enumerated=usb_enumerated();
}

/////////////////////////////////////////////////////////////////////////////
//
// usb_keyboard_task()
//
// Sends a packet of keyboard data.  The protocol was specified in the HID
// report descriptor (see usb_desc_kbmouse.h), and is:
//     tx_msg[0]   = HID report id (2)
//     tx_msg[1]   = modifier (an 8bit bitmap of shift, tab, alt keypress)
//     tx_msg[2]   = const 0
//     tx_msg[3:7] = an array of held down keys.  a=4, b=5, etc.
//                   if msg[2:7]={0} then no keys are held down
//
//     rx_msg[1] = HID report id (2)
//     rx_msg[0] = 5bit bitmap of led status
//
/////////////////////////////////////////////////////////////////////////////

int8 char_2_usb_kbd_code(char c){

   int8 ic;

   if(isAlpha(c)){
      ic=c-'a'+4;
   }
   else{
      if(c=='0'){
        ic=39;
      }
      else{
        ic=c-'1'+30;
      }
   }
   return(ic);
}

// Funcion para conversor analogo-digital
unsigned int16 sensores(int x){
unsigned int16 y;set_adc_channel(x);delay_ms(10);y=read_adc();return (y);
}

/////////////////////////////////////////////////////////////////////////////
//
// usb_rx_task()
//
// Listens to EP1 for any incoming packets.  The only report ID that is
// configurd to send us data is 2 (keyboard LED status, see above)
//
/////////////////////////////////////////////////////////////////////////////

void usb_rx_task(void){

   
}


void LeerInflow(){
   Inflow=sensores(2);
   Inflow_l=(int8)Inflow;
   Inflow_h=(Inflow>>8);
   tx_msg[9]=(int8)Inflow_l;
   tx_msg[10]=(int8)Inflow_h;
}

void LeerDownflow(){
   Downflow=sensores(3);
   Downflow_l=(int8)Downflow;
   Downflow_h=(Downflow>>8);
   tx_msg[7]=(int8)Downflow_l;
   tx_msg[8]=(int8)Downflow_h;
}

void LeerVidrioAbajo(){
      VidrioDN=255.0*sensores(4);
      VidrioDN=VidrioDN/1023.0;
      tx_msg[12]=(int8)VidrioDN;
}

void LeerVidriook(){
      VidrioUP=255.0*sensores(5);
      VidrioUP=VidrioUP/1023.0;
      tx_msg[11]=(int8)VidrioUP;
}

void LeerFiltroInflow(){
   Filtro_Inflow=sensores(1);
   Filtro_Inflow_l=(int8)Filtro_Inflow;
   Filtro_Inflow_h=(Filtro_Inflow>>8);
   tx_msg[5]=(int8)Filtro_Inflow_l;
   tx_msg[6]=(int8)Filtro_Inflow_h;
}

void LeerFiltroDownflow(){
   Filtro_Downflow=sensores(0);
   Filtro_Downflow_l=(int8)Filtro_Downflow;
   Filtro_Downflow_h=(Filtro_Downflow>>8);
   tx_msg[3]=(int8)Filtro_Downflow_l;
   tx_msg[4]=(int8)Filtro_Downflow_h;
}

void LeerBotones(){
   if(UP)
   {
      tx_msg[13]=10;
   }
   else
   {
      tx_msg[13]=20;
   }
     
   if(DOWN)
   {
      tx_msg[14]=10;
   }
   else
   {
      tx_msg[14]=20;
   }
   
   if(RIGHT)
   {
      tx_msg[15]=10;
   }
   else
   {
      tx_msg[15]=20;
   }
   
   if(LEFT)
   {
      tx_msg[16]=10;
   }
   else
   {
      tx_msg[16]=20;
   }
      
   //if(flancou==0 && flancod==0 && flancor==0 && flancol==0 && flancom==0 && flancob==0 && flancov==0 && flancot==0)
      //tx_msg[13]=44;
}

#int_TIMER1
void temp1s(void){
   set_timer1(5536);
   tiempos++;
 
   if(tiempos>=20)
   {
      capturar=1;tiempos=0;
   }
}

void main() {

   hay_dato=0;

   delay_ms(500);

   usb_init_cs();
   setup_timer_1(T1_INTERNAL|T1_DIV_BY_1);
   enable_interrupts(INT_TIMER1);
   setup_adc_ports(AN0_TO_AN7);
   setup_adc(ADC_CLOCK_DIV_32 );
   setup_ccp1 (CCP_PWM);
   setup_ccp2 (CCP_PWM);
   setup_timer_2(T2_DIV_BY_1, 255, 1);   // T=255 us, F=47KHz  
   set_pwm1_duty(0);
   set_pwm2_duty(0);
   enable_interrupts(global);
   Fuente_on;

   while (TRUE) {

      usb_task();
      usb_debug_task();

      if (usb_enumerated()) {
      
         usb_rx_task();
         delay_ms(5);
         if (usb_kbhit(1))
         {
            usb_get_packet(1, rx_msg, sizeof(rx_msg));
   
            if(rx_msg[3]==5) //Motor DownFlow
            {
               MotorDn_on;
            }
            
            if(rx_msg[3]==10) //Motor DownFlow
            {
               MotorDn_off;
            }
            
            if(rx_msg[4]==5) //Motor InFlow
            {
               MotorIn_on;
            }
      
            if(rx_msg[4]==10) //Motor InFlow
            {
               MotorIn_off;
            }
      
            if(rx_msg[5]==5) //Luz Blanca
            {
               LuzBlanca_on;
            }
      
            if(rx_msg[5]==10) //Luz Blanca
            {
               LuzBlanca_off;
            }
      
            if(rx_msg[6]==5) //Luz UV
            {
               LuzUV_on;
            }
      
            if(rx_msg[6]==10) //Luz UV
            {
               LuzUV_off;
            }
            
            if(rx_msg[7]==5) //Toma
            {
               Toma_on;
            }
      
            if(rx_msg[7]==10) //Toma
            {
               Toma_off;
            }
      
            if(rx_msg[8]==5) //Alarma
            {
               Alarma_on;
            }
      
            if(rx_msg[8]==10) //Alarma
            {
               Alarma_off;
            }
       
            if(rx_msg[0]==10 && rx_msg[1]==10 && rx_msg[2]==10)
            {  
               LuzBlanca_off;
               LuzUV_off;
               MotorDn_off;
               MotorIn_off;
               Alarma_off;
               Toma_off;
               set_pwm1_duty(0);
               set_pwm2_duty(0);
               delay_ms(19000);
               Fuente_off;
               apagado=0;
               LeerBotones();
            }
            
            if(capturar==1)
            {
               capturar=0;
               set_pwm1_duty(rx_msg[9]);
               set_pwm2_duty(rx_msg[9]);
               LeerBotones();
               LeerDownflow();
               LeerFiltroDownflow();
               LeerFiltroInflow();
               LeerInflow();
               LeerVidrioAbajo();
               LeerVidriook();         
               usb_put_packet(1,tx_msg,sizeof(tx_msg),USB_DTS_TOGGLE);
            } 
         } 
      }
      else
      {
         LuzBlanca_off;
         LuzUV_off;
         MotorDn_off;
         MotorIn_off;
         Alarma_off;
         Toma_off;
         set_pwm1_duty(0);
         set_pwm2_duty(0);
      }
   }
}

