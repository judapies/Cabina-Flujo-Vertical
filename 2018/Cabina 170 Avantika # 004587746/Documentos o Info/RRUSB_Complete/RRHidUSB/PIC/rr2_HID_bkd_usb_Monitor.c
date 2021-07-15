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
#define   UP               input(PIN_D4)// Temporalmente para pruebas
#define   DOWN             input(PIN_D5)
#define   RIGHT            input(PIN_D6)
#define   LEFT             input(PIN_D7)

#define   LuzBlanca_on           output_bit(PIN_C2,1)
#define   LuzBlanca_off          output_bit(PIN_C2,0)
#define   LuzUV_on               output_bit(PIN_D4,1)
#define   LuzUV_off              output_bit(PIN_D4,0)
#define   MotorIn_on             output_bit(PIN_C1,1)
#define   MotorIn_off            output_bit(PIN_C1,0)
#define   Toma_on                output_bit(PIN_D5,1)
#define   Toma_off               output_bit(PIN_D5,0)
#define   Alarma_on              output_bit(PIN_C6,1)
#define   Alarma_off             output_bit(PIN_C6,0)
#define   MotorDn_on             output_bit(PIN_C0,1)
#define   MotorDn_off            output_bit(PIN_C0,0)

/////////////////////////////////////////////////////////////////////////////
//
// RAM
//
/////////////////////////////////////////////////////////////////////////////

int8 connected;
int8 enumerated;
int8 rx_msg[USB_EP1_RX_SIZE];
int8 tx_msg[18]={255,255,255,0,0,0,0,0,0,0,0,0,0,0,0,255,255,128};

char NextChar='0';
int1 hay_dato=0;

int8 i;

int16 tiempos=0;
// Variables para Pt100
short capturar=0;
int8 h=0,l=0,posicionvector=0,r=0,q=0;
float V0=0.0,V2=0.0,V3=0.0,RPM=0.0,pH=0.0,OD=0.0;
float R1=6200.0,Pt,Prom=0.0,Temperature=0.0;
float promedio[80]={0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
float pulsos1=0;
float rpm_actual=0.0,n_pulsos=512.0;
unsigned int16 pwm1=0,pwm2=0;
unsigned int8 Inflow_l=0,Inflow_h=0,Downflow_l=0,Downflow_h=0,Vidrio_abajo=0,Vidrio_ok=0,Filtro_Inflow_l=0,Filtro_Inflow_h=0;
unsigned int8 Filtro_Downflow_l=0,Filtro_Downflow_h=0;
unsigned int16 Inflow=0.0,Downflow=0.0,Filtro_Inflow=0.0,Filtro_Downflow=0.0;
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

   if (usb_kbhit(1)){
      usb_get_packet(1, rx_msg, sizeof(rx_msg));
   }
}


void LeerInflow(){
   Inflow=sensores(0);
   Inflow_l=(int8)Inflow;
   Inflow_h=(Inflow>>8);
   tx_msg[9]=(int8)Inflow_l;
   tx_msg[10]=(int8)Inflow_h;
}

void LeerDownflow(){
   Downflow=sensores(2);
   Downflow_l=(int8)Downflow;
   Downflow_h=(Downflow>>8);
   tx_msg[7]=(int8)Downflow_l;
   tx_msg[8]=(int8)Downflow_h;
}

void LeerVidrioAbajo(){
   if(sensores(4)<800)
      tx_msg[12]=5;
   else
      tx_msg[12]=10;
}

void LeerVidriook(){
   if(sensores(5)<800)
      tx_msg[11]=5;
   else
      tx_msg[11]=10;
}

void LeerFiltroInflow(){
   Filtro_Inflow=sensores(6);
   Filtro_Inflow_l=(int8)Filtro_Inflow;
   Filtro_Inflow_h=(Filtro_Inflow>>8);
   tx_msg[5]=(int8)Filtro_Inflow_l;
   tx_msg[6]=(int8)Filtro_Inflow_h;
}

void LeerFiltroDownflow(){
   Filtro_Downflow=sensores(7);
   Filtro_Downflow_l=(int8)Filtro_Downflow;
   Filtro_Downflow_h=(Filtro_Downflow>>8);
   tx_msg[3]=(int8)Filtro_Downflow_l;
   tx_msg[4]=(int8)Filtro_Downflow_h;
}

void LeerBotones(){
   if(UP)
      tx_msg[13]=10;
     
   if(DOWN)
      tx_msg[13]=20;
      
   if(RIGHT)
      tx_msg[13]=30;
      
   if(LEFT)
      tx_msg[13]=40;
      
   if(!UP && !DOWN && !RIGHT && !LEFT)
      tx_msg[13]=44;
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
   enable_interrupts(global);

   while (TRUE) {

      usb_task();
      usb_debug_task();

      if (usb_enumerated()) {
      
         usb_rx_task();
         delay_ms(5);
      }
      
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
      
      
      
      if(capturar==1)
      {
         capturar=0;
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

