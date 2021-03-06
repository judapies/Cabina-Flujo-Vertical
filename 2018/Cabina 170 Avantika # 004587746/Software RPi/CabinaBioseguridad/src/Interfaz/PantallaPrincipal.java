/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaz;

import static Comunicacion.Hilo.PPurga;
import Comunicacion.Variables;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Calendar;
import java.util.Formatter;
import java.util.GregorianCalendar;
import javax.swing.ImageIcon;
import jssc.SerialPort;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author JP Electronica
 */
public class PantallaPrincipal extends javax.swing.JFrame  {

    /**
     * Creates new form PantallaPrincipal
     */
    
    public PantallaPrincipal() {
        initComponents();
        
        PanelPrincipal.setBackground(Color.BLACK);
        PanelVelocidades.setBackground(Color.BLACK);
        PanelEstados.setBackground(Color.BLACK);
        PanelCabina.setBackground(Color.BLACK);
        setLocationRelativeTo(null);
                
        Bar25DownFlow.setForeground(Color.gray);
        Bar25DownFlow.setBackground(Color.GREEN);
        
        Bar50DownFlow.setForeground(Color.gray);
        Bar50DownFlow.setBackground(Color.YELLOW);
        
        Bar75DownFlow.setForeground(Color.gray);
        Bar75DownFlow.setBackground(Color.orange);
        
        Bar100DownFlow.setForeground(Color.gray);
        Bar100DownFlow.setBackground(Color.RED);
        
        BarUV.setForeground(Color.BLUE);
        BarUV.setValue(50);
        
        Imagen Imagen1 = new Imagen();
        PanelCabina.add(Imagen1);
        PanelCabina.repaint();       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelPrincipal = new javax.swing.JPanel();
        PanelEstados = new javax.swing.JPanel();
        PanelVelocidades = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        PV_InFlow = new javax.swing.JTextField();
        PV_DownFlow = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        LuzButton = new javax.swing.JButton();
        TomaButton = new javax.swing.JButton();
        BlowerButton = new javax.swing.JButton();
        UVButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        Bar50DownFlow = new javax.swing.JProgressBar();
        Bar75DownFlow = new javax.swing.JProgressBar();
        DownFlowLabel = new javax.swing.JLabel();
        Bar100DownFlow = new javax.swing.JProgressBar();
        Bar25DownFlow = new javax.swing.JProgressBar();
        jLabel11 = new javax.swing.JLabel();
        PanelUV = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        BarUV = new javax.swing.JProgressBar();
        LabelMinUV = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        LabelSegUV = new javax.swing.JLabel();
        PanelCabina = new javax.swing.JPanel();
        OffButton = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        MenuTiempo = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(800, 480));
        setUndecorated(true);
        setSize(new java.awt.Dimension(800, 480));

        PanelPrincipal.setMaximumSize(new java.awt.Dimension(800, 480));
        PanelPrincipal.setMinimumSize(new java.awt.Dimension(800, 480));

        PanelEstados.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        PanelEstados.setMaximumSize(new java.awt.Dimension(559, 472));
        PanelEstados.setMinimumSize(new java.awt.Dimension(559, 472));

        PanelVelocidades.setBackground(new java.awt.Color(0, 0, 0));
        PanelVelocidades.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)), "Velocidades", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic", 0, 12), new java.awt.Color(240, 240, 240))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Yu Gothic Medium", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(240, 240, 240));
        jLabel3.setText("Exhaust");

        PV_InFlow.setEditable(false);
        PV_InFlow.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        PV_InFlow.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        PV_InFlow.setText("0.54");
        PV_InFlow.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PV_InFlowKeyPressed(evt);
            }
        });

        PV_DownFlow.setEditable(false);
        PV_DownFlow.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        PV_DownFlow.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        PV_DownFlow.setText("0.36");

        jLabel4.setFont(new java.awt.Font("Yu Gothic Medium", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(240, 240, 240));
        jLabel4.setText("DownFlow ");

        javax.swing.GroupLayout PanelVelocidadesLayout = new javax.swing.GroupLayout(PanelVelocidades);
        PanelVelocidades.setLayout(PanelVelocidadesLayout);
        PanelVelocidadesLayout.setHorizontalGroup(
            PanelVelocidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVelocidadesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelVelocidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addGroup(PanelVelocidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PV_DownFlow)
                    .addComponent(PV_InFlow, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                .addGap(32, 32, 32))
        );
        PanelVelocidadesLayout.setVerticalGroup(
            PanelVelocidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVelocidadesLayout.createSequentialGroup()
                .addGroup(PanelVelocidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(PV_InFlow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(PanelVelocidadesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(PV_DownFlow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        LuzButton.setBackground(new java.awt.Color(0, 0, 0));
        LuzButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/light-on.png"))); // NOI18N
        LuzButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LuzButtonActionPerformed(evt);
            }
        });

        TomaButton.setBackground(new java.awt.Color(0, 0, 0));
        TomaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/poweroutlet-us.png"))); // NOI18N
        TomaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TomaButtonActionPerformed(evt);
            }
        });

        BlowerButton.setBackground(new java.awt.Color(0, 0, 0));
        BlowerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/fan.png"))); // NOI18N
        BlowerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BlowerButtonActionPerformed(evt);
            }
        });

        UVButton.setBackground(new java.awt.Color(0, 0, 0));
        UVButton.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        UVButton.setForeground(new java.awt.Color(240, 240, 240));
        UVButton.setText("UV");
        UVButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UVButtonActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(240, 240, 240)), "Estado de Filtros", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic", 0, 12), new java.awt.Color(240, 240, 240))); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(490, 254));
        jPanel1.setMinimumSize(new java.awt.Dimension(490, 254));

        DownFlowLabel.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        DownFlowLabel.setForeground(new java.awt.Color(240, 240, 240));
        DownFlowLabel.setText("80%");

        jLabel11.setFont(new java.awt.Font("Yu Gothic Medium", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(240, 240, 240));
        jLabel11.setText("DownFlow");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 320, Short.MAX_VALUE)
                        .addComponent(jLabel11))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Bar25DownFlow, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Bar50DownFlow, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Bar75DownFlow, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Bar100DownFlow, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(DownFlowLabel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(Bar50DownFlow, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Bar75DownFlow, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(Bar100DownFlow, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(56, 56, 56))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(Bar25DownFlow, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)))
                        .addComponent(jLabel11))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(DownFlowLabel)))
                .addGap(18, 18, 18))
        );

        PanelUV.setBackground(new java.awt.Color(0, 0, 0));
        PanelUV.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)), "UV", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Yu Gothic", 0, 12), new java.awt.Color(240, 240, 240))); // NOI18N

        jLabel8.setFont(new java.awt.Font("Yu Gothic Medium", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(240, 240, 240));
        jLabel8.setText("Temporizador:");

        jLabel9.setFont(new java.awt.Font("Yu Gothic Medium", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(240, 240, 240));
        jLabel9.setText("Estado Luz UV:");

        LabelMinUV.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        LabelMinUV.setForeground(new java.awt.Color(255, 255, 255));
        LabelMinUV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelMinUV.setText("00");

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(":");

        LabelSegUV.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        LabelSegUV.setForeground(new java.awt.Color(255, 255, 255));
        LabelSegUV.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelSegUV.setText("00");

        javax.swing.GroupLayout PanelUVLayout = new javax.swing.GroupLayout(PanelUV);
        PanelUV.setLayout(PanelUVLayout);
        PanelUVLayout.setHorizontalGroup(
            PanelUVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelUVLayout.createSequentialGroup()
                .addGroup(PanelUVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGroup(PanelUVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelUVLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BarUV, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelUVLayout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(LabelMinUV)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LabelSegUV)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelUVLayout.setVerticalGroup(
            PanelUVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelUVLayout.createSequentialGroup()
                .addGroup(PanelUVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelUVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(LabelMinUV)
                        .addComponent(jLabel2)
                        .addComponent(LabelSegUV))
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PanelUVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(BarUV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout PanelEstadosLayout = new javax.swing.GroupLayout(PanelEstados);
        PanelEstados.setLayout(PanelEstadosLayout);
        PanelEstadosLayout.setHorizontalGroup(
            PanelEstadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelEstadosLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(LuzButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(TomaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BlowerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(UVButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PanelEstadosLayout.createSequentialGroup()
                .addGroup(PanelEstadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PanelEstadosLayout.createSequentialGroup()
                        .addComponent(PanelVelocidades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelUV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        PanelEstadosLayout.setVerticalGroup(
            PanelEstadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelEstadosLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelEstadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelVelocidades, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PanelUV, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelEstadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(UVButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BlowerButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LuzButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TomaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        PanelCabina.setMaximumSize(new java.awt.Dimension(262, 470));
        PanelCabina.setMinimumSize(new java.awt.Dimension(262, 470));
        PanelCabina.setPreferredSize(new java.awt.Dimension(262, 470));

        OffButton.setBackground(new java.awt.Color(0, 0, 0));
        OffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Imagenes/Windows-Turn-Off-icon (1).png"))); // NOI18N
        OffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OffButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelCabinaLayout = new javax.swing.GroupLayout(PanelCabina);
        PanelCabina.setLayout(PanelCabinaLayout);
        PanelCabinaLayout.setHorizontalGroup(
            PanelCabinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCabinaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(OffButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(183, Short.MAX_VALUE))
        );
        PanelCabinaLayout.setVerticalGroup(
            PanelCabinaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCabinaLayout.createSequentialGroup()
                .addComponent(OffButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 405, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PanelPrincipalLayout = new javax.swing.GroupLayout(PanelPrincipal);
        PanelPrincipal.setLayout(PanelPrincipalLayout);
        PanelPrincipalLayout.setHorizontalGroup(
            PanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPrincipalLayout.createSequentialGroup()
                .addComponent(PanelCabina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelEstados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelPrincipalLayout.setVerticalGroup(
            PanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelPrincipalLayout.createSequentialGroup()
                .addGroup(PanelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelCabina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PanelEstados, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel5.setText("jLabel5");

        jMenu1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jMenu1.setText("Cabina Bioseguridad A2   JP Inglobal");
        jMenu1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jMenuBar1.add(jMenu1);
        jMenuBar1.add(MenuTiempo);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void PV_InFlowKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PV_InFlowKeyPressed
        if(evt.getKeyChar()=='u')   
            Variables.Arriba=true;
        else
            Variables.Arriba=false;
        
        if(evt.getKeyChar()=='d')   
            Variables.Abajo=true;
        else
            Variables.Abajo=false;
        
        if(evt.getKeyChar()=='r')   
            Variables.Derecha=true;
        else
            Variables.Derecha=false;
        
        if(evt.getKeyChar()=='l')   
            Variables.Izquierda=true;
        else
            Variables.Izquierda=false;
    }//GEN-LAST:event_PV_InFlowKeyPressed

    private void LuzButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LuzButtonActionPerformed
        Variables.TeclaLuz=true;
    }//GEN-LAST:event_LuzButtonActionPerformed

    private void TomaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TomaButtonActionPerformed
        Variables.TeclaToma=true;
    }//GEN-LAST:event_TomaButtonActionPerformed

    private void BlowerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BlowerButtonActionPerformed
        Variables.TeclaMotor=true;
        
        if(Variables.Ubica20=true)
        {
            Variables.Ubica20=false;
            Variables.MoviendoVidrio=false;
            Variables.MovimientoVidrio=20; // Vidrio Quieto
        }
        
        if(!Variables.UbicaUV && (Variables.VidrioUP==10 || Variables.VidrioDN==10))
        {
            Variables.Motor=true;
            Comunicacion.Hilo.Dialog.setVisible(true);
        }
    }//GEN-LAST:event_BlowerButtonActionPerformed

    private void UVButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UVButtonActionPerformed
        Variables.TeclaUV=true;
        
        if(Variables.UbicaUV=true)
        {
            Variables.UbicaUV=false;
            Variables.MoviendoVidrio=false;
            Variables.MovimientoVidrio=20; // Vidrio Quieto
        }
            
        if(!Variables.Ubica20 && (Variables.VidrioUP==5 || Variables.VidrioDN==5))
        {
            Variables.UV=true;
            Comunicacion.Hilo.Dialog.setVisible(true);
        }
    }//GEN-LAST:event_UVButtonActionPerformed

    private void OffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OffButtonActionPerformed
        int msg=JOptionPane.showConfirmDialog(null, "Desea Apagar la Cabina?", "Confirmar Apagado", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if(msg==0)
        {
            Interfaz.TimerEx.IniciaTimer(1000);
            PPurga.setVisible(true);
            Variables.Alarma=false;
        }
    }//GEN-LAST:event_OffButtonActionPerformed

            
    public class Imagen extends javax.swing.JPanel {
 
        public Imagen() {
            
            this.setSize(262, 470); //se selecciona el tama??o del panel
        }
 
        //Se crea un m??todo cuyo par??metro debe ser un objeto Graphics
        
        @Override
        public void paint(Graphics grafico) {
            Dimension height = getSize();
        //Se selecciona la imagen que tenemos en el paquete de la //ruta del programa
            ImageIcon Img = new ImageIcon(getClass().getResource("/Imagenes/CabinaB2.png")); 
            //se dibuja la imagen que tenemos en el paquete Images //dentro de un panel   
            grafico.drawImage(Img.getImage(), 0, 0, height.width, height.height, null);
            setOpaque(false);
            super.paintComponent(grafico);
        }
    }
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PantallaPrincipal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                    new PantallaPrincipal().setVisible(true);
            }
        });
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JProgressBar Bar100DownFlow;
    public static javax.swing.JProgressBar Bar25DownFlow;
    public static javax.swing.JProgressBar Bar50DownFlow;
    public static javax.swing.JProgressBar Bar75DownFlow;
    public static javax.swing.JProgressBar BarUV;
    public static javax.swing.JButton BlowerButton;
    public static javax.swing.JLabel DownFlowLabel;
    public static javax.swing.JLabel LabelMinUV;
    public static javax.swing.JLabel LabelSegUV;
    public static javax.swing.JButton LuzButton;
    private javax.swing.JMenu MenuTiempo;
    private javax.swing.JButton OffButton;
    public static javax.swing.JTextField PV_DownFlow;
    public static javax.swing.JTextField PV_InFlow;
    private javax.swing.JPanel PanelCabina;
    private javax.swing.JPanel PanelEstados;
    private javax.swing.JPanel PanelPrincipal;
    private javax.swing.JPanel PanelUV;
    private javax.swing.JPanel PanelVelocidades;
    public static javax.swing.JButton TomaButton;
    public static javax.swing.JButton UVButton;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
