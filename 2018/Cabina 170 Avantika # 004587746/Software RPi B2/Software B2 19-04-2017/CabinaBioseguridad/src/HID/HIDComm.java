package HID;

import Comunicacion.Variables;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import purejavahidapi.*;

public class HIDComm {
	volatile static boolean deviceOpen = false;
        public static HidDeviceInfo devInfo = null;
        public static byte[] data2 = new byte [4];
        public static HidDevice dev;

	public static void main() {
        try {

	while (true) {
	// System.exit(0);
				
	if (!deviceOpen)
        {
            System.out.println("scanning");
            List<HidDeviceInfo> devList = PureJavaHidApi.enumerateDevices();
            for (HidDeviceInfo info : devList) 
            {
                if (info.getVendorId() == (short) 0x0461 && info.getProductId() == (short) 0x0020) 
                {
                    devInfo = info;
                    break;
                }
	}
	if (devInfo == null) 
        {
            System.out.println("device not found");
            Thread.sleep(1000);
	} 
        else
        {
            System.out.println("device found");
            if (true) 
            {
                deviceOpen = true;
                dev = PureJavaHidApi.openDevice(devInfo);
        
        dev.setDeviceRemovalListener(new DeviceRemovalListener() 
        {
            @Override
            public void onDeviceRemoval(HidDevice source) {
                System.out.println("device removed");
                deviceOpen = false;
            }
	});
	
        dev.setInputReportListener(new InputReportListener() {
            @Override
            public void onInputReport(HidDevice source, byte Id, byte[] data, int len) 
            {
                Variables.rx_msg=data;
                //System.out.printf("onInputReport: id %d len %d data ", Id, len);
                //for (int i = 0; i < len; i++)
                    //System.out.printf("%02X ", data[i]);
                    //System.out.printf("%02X ", Variables.rx_msg[i]);
                    //System.out.println();
            }
	});
        
        new Thread(new Runnable() {
        
	@Override
	public void run() {
        System.out.println("Entro -Hilo");
	
        while (true) 
        {
            try 
            {
                
                int len = 0;
                if (((len = dev.getFeatureReport(Variables.rx_msg, Variables.rx_msg.length)) >= 0) && true) 
                {
                    int Id = Variables.rx_msg[0];
                    //System.out.printf("getFeatureReport: id %d len %d data ", Id, len);
                    //for (int i = 0; i < data.length; i++)
                        //System.out.printf("%02X ", Variables.rx_msg[i]);
                        System.out.println(Variables.rx_msg[13]);
                    System.out.println();
                }
            Thread.sleep(200);
        } 
        catch (InterruptedException ex) {
        Logger.getLogger(HIDComm.class.getName()).log(Level.SEVERE, null, ex);
        }

	}

	}
	}).start();

	Thread.sleep(2000);
	//dev.close();
	//deviceOpen = false;
	}
	}
	}
	}
	} catch (Throwable e) {
	e.printStackTrace();
	}
	}
        
        public static void EnvioHID(byte codigo, byte[] datos, int length){
            if(dev!=null)
                dev.setOutputReport(codigo, datos, length);
        }
}
