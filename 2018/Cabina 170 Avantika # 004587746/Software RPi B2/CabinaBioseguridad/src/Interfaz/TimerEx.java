/*
 * The MIT License
 *
 * Copyright 2017 root.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package Interfaz;

import Comunicacion.Variables;
import static Interfaz.CicloPurga.LabelMinutos;
import static Interfaz.CicloPurga.LabelSegundos;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author root
 */
public class TimerEx {
    public static Timer timer = new Timer();
    
    public static TimerTask task = new TimerTask(){
            @Override
            public void run() {
                
                if(Comunicacion.Hilo.Purga.isVisible())
                {
                    if(Variables.VidrioUP==5 && Variables.VidrioDN==5)
                    {
                        Variables.SegundosPurga++;
                        if(Variables.SegundosPurga>59)
                        {
                            Variables.SegundosPurga=0;
                            Variables.MinutosPurga++;
                        }
                
                        LabelMinutos.setText(String.format("%02d",Variables.MinutosPurga));
                        LabelSegundos.setText(String.format("%02d",Variables.SegundosPurga));
                    }
                }
            }
        };
    
    public static void IniciaTimer(long tiempo){
        timer.schedule(task, 0, tiempo);
    }
    
    public static void DetieneTimer(){
        timer.cancel();
    }
}
