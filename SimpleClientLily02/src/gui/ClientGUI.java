/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import ai.LaboAI;
import ai.Tochka;
import ai.TajimaLabAI;
import gameElements.Game;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import network.ServerConnecter;

/**
 *
 * @author koji
 */
public class ClientGUI extends javax.swing.JFrame implements MessageRecevable {

    //ウィンドウタイトル
    private static final String WINDOW_TITLE = "Tochka v3.0";

    //サーバとの通信クラス
    private ServerConnecter connecter;
    //表示部分のドキュメントを管理するクラス
    private DefaultStyledDocument document;
    //BlockusAI
    private TajimaLabAI myAI;

    private String defaultIP;
    private String defaultPort = "18420";

    // 選択された重みファイル
    private File weightFile = null;
    private final String DEFAULT_FORDER_PATH = "";

    /**
     * コンストラクタ　文字の表示部分のみを初期化する
     *
     * @param ai 使用するAI
     */
    public ClientGUI(TajimaLabAI ai) {
        initComponents();
        try {
            defaultIP = InetAddress.getLocalHost().getHostAddress();//"192.168.108.100";
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.document = new DefaultStyledDocument();
        this.jTextPane1.setDocument(this.document);
        this.myAI = ai;
        this.jTextField2.setText(defaultIP);
        this.jTextField4.setText(defaultPort);
        this.setTitle(WINDOW_TITLE + " (" + myAI.getMyName() + ")");
        this.resetNNPanel();
    }

    public ClientGUI() {
        initComponents();
        try {
            defaultIP = InetAddress.getLocalHost().getHostAddress();//"192.168.108.100";
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.document = new DefaultStyledDocument();
        this.jTextPane1.setDocument(this.document);
        this.myAI = null;
        this.jTextField2.setText(defaultIP);
        this.jTextField4.setText(defaultPort);
        this.setTitle(WINDOW_TITLE + "(Lily 0)");
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jCheckBox1 = new javax.swing.JCheckBox();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        jCheckBox2 = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        nnPanel = new javax.swing.JPanel();
        inputPanel = new javax.swing.JPanel();
        inputMiddle1LinePanel = new javax.swing.JPanel();
        middle1Panel = new javax.swing.JPanel();
        middle1Middle2LinePanel = new javax.swing.JPanel();
        middle2Panel = new javax.swing.JPanel();
        middle2OutputLinePanel = new javax.swing.JPanel();
        outputPanel = new javax.swing.JPanel();

        jCheckBox1.setText("jCheckBox1");

        jToggleButton1.setText("jToggleButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Lily0 v1,0");

        jLabel1.setText("Log");

        jScrollPane1.setViewportView(jTextPane1);

        jLabel2.setText("Message");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField1KeyPressed(evt);
            }
        });

        jButton1.setText("Send");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("Connect");

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField2.setText("192.168.0.2");
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel4.setText("address");

        jLabel5.setText("port");

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextField4.setText("16041");

        jButton2.setText("Connect");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel6.setText("NAME");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextField3KeyPressed(evt);
            }
        });

        jButton3.setText("Send");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel7.setText("PLAY");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "P(教授)", "S(学生)" }));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "1-1(ゼミ)", "2-1(図書館)", "2-2(図書館)", "3-1(実験)", "3-2(実験)", "3-3(実験)", "3-4(実験)", "4-1(発表)", "4-2(発表)", "4-3(発表)", "4-4(発表)", "4-5(発表)", "5-1(論文)", "5-2(論文)", "5-3(論文)", "5-4(論文)", "5-5(論文)", "6-1(研究報告)", "6-2(研究報告)", "6-3(研究報告)", "7-1(機材)" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-", "F", "G", "M", "FF", "FG", "GG", "T0", "T1", "T2", "T3", "T4", "T5" }));

        jButton4.setText("Send");
        jButton4.setToolTipText("");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("AI Mode");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jLabel8.setText("Weight File");

        jTextField5.setEditable(false);
        jTextField5.setText("null");

        jButton5.setText("Choose");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jLabel9.setText("Neural Network");

        nnPanel.setBackground(new java.awt.Color(255, 255, 255));
        nnPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        nnPanel.setLayout(new javax.swing.BoxLayout(nnPanel, javax.swing.BoxLayout.LINE_AXIS));

        inputPanel.setBackground(new java.awt.Color(204, 255, 255));
        inputPanel.setPreferredSize(new java.awt.Dimension(120, 400));

        javax.swing.GroupLayout inputPanelLayout = new javax.swing.GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 156, Short.MAX_VALUE)
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        nnPanel.add(inputPanel);

        inputMiddle1LinePanel.setBackground(new java.awt.Color(255, 255, 255));
        inputMiddle1LinePanel.setPreferredSize(new java.awt.Dimension(20, 400));

        javax.swing.GroupLayout inputMiddle1LinePanelLayout = new javax.swing.GroupLayout(inputMiddle1LinePanel);
        inputMiddle1LinePanel.setLayout(inputMiddle1LinePanelLayout);
        inputMiddle1LinePanelLayout.setHorizontalGroup(
            inputMiddle1LinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );
        inputMiddle1LinePanelLayout.setVerticalGroup(
            inputMiddle1LinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        nnPanel.add(inputMiddle1LinePanel);

        middle1Panel.setBackground(new java.awt.Color(255, 204, 255));
        middle1Panel.setPreferredSize(new java.awt.Dimension(10, 400));

        javax.swing.GroupLayout middle1PanelLayout = new javax.swing.GroupLayout(middle1Panel);
        middle1Panel.setLayout(middle1PanelLayout);
        middle1PanelLayout.setHorizontalGroup(
            middle1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 46, Short.MAX_VALUE)
        );
        middle1PanelLayout.setVerticalGroup(
            middle1PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        nnPanel.add(middle1Panel);

        middle1Middle2LinePanel.setBackground(new java.awt.Color(255, 255, 255));
        middle1Middle2LinePanel.setPreferredSize(new java.awt.Dimension(20, 400));

        javax.swing.GroupLayout middle1Middle2LinePanelLayout = new javax.swing.GroupLayout(middle1Middle2LinePanel);
        middle1Middle2LinePanel.setLayout(middle1Middle2LinePanelLayout);
        middle1Middle2LinePanelLayout.setHorizontalGroup(
            middle1Middle2LinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );
        middle1Middle2LinePanelLayout.setVerticalGroup(
            middle1Middle2LinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        nnPanel.add(middle1Middle2LinePanel);

        middle2Panel.setBackground(new java.awt.Color(255, 255, 204));
        middle2Panel.setPreferredSize(new java.awt.Dimension(10, 400));

        javax.swing.GroupLayout middle2PanelLayout = new javax.swing.GroupLayout(middle2Panel);
        middle2Panel.setLayout(middle2PanelLayout);
        middle2PanelLayout.setHorizontalGroup(
            middle2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 46, Short.MAX_VALUE)
        );
        middle2PanelLayout.setVerticalGroup(
            middle2PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        nnPanel.add(middle2Panel);

        middle2OutputLinePanel.setBackground(new java.awt.Color(255, 255, 255));
        middle2OutputLinePanel.setPreferredSize(new java.awt.Dimension(20, 400));

        javax.swing.GroupLayout middle2OutputLinePanelLayout = new javax.swing.GroupLayout(middle2OutputLinePanel);
        middle2OutputLinePanel.setLayout(middle2OutputLinePanelLayout);
        middle2OutputLinePanelLayout.setHorizontalGroup(
            middle2OutputLinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 56, Short.MAX_VALUE)
        );
        middle2OutputLinePanelLayout.setVerticalGroup(
            middle2OutputLinePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        nnPanel.add(middle2OutputLinePanel);

        outputPanel.setBackground(new java.awt.Color(204, 255, 204));
        outputPanel.setPreferredSize(new java.awt.Dimension(120, 400));

        javax.swing.GroupLayout outputPanelLayout = new javax.swing.GroupLayout(outputPanel);
        outputPanel.setLayout(outputPanelLayout);
        outputPanelLayout.setHorizontalGroup(
            outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 156, Short.MAX_VALUE)
        );
        outputPanelLayout.setVerticalGroup(
            outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );

        nnPanel.add(outputPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nnPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField5))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 230, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3))
                            .addComponent(jTextField1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel9))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addGap(20, 20, 20))
                    .addComponent(jCheckBox2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nnPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * 送信ボタンを押したときの動作
     */
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String sendText = this.jTextField1.getText();
        this.sendMessage(sendText);
        this.jTextField1.setText("");

        // PLAYのところをリセット
        resetCombos();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped

    }//GEN-LAST:event_jTextField1KeyTyped

    private void jTextField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            this.jButton1.doClick();
        }
    }//GEN-LAST:event_jTextField1KeyPressed

    /**
     * 接続ボタンを押したときの動作　受信用のスレッドを立ち上げ待機する。
     */
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String hostname = this.jTextField2.getText();
        int port = Integer.parseInt(this.jTextField4.getText());
        this.connecter = new ServerConnecter(this);

        // チェックボックスを確認してAI起動
        if (this.jCheckBox2.isSelected()) {
            // 係数ファイルが選択されていない＝やり直し
            if (this.weightFile == null) {
                JOptionPane.showMessageDialog(this, "係数ファイルが指定されていません！", "error!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Game game = new Game();
            try {
                this.myAI = new Tochka(game, this.weightFile.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
//            this.myAI = new Tochka(game);
            this.myAI.setConnecter(this.connecter);
            this.myAI.setOutputInterface(this);
            this.jTextField1.setEnabled(false);
            this.jButton1.setEnabled(false);
            this.jTextField3.setText(this.myAI.getMyName());
            this.jTextField3.setEnabled(false);
            this.jButton3.setEnabled(false);
            this.jComboBox1.setEnabled(false);
            this.jComboBox2.setEnabled(false);
            this.jComboBox3.setEnabled(false);
            this.jButton4.setEnabled(false);
        }

        try {
            this.connecter.connectToServer(hostname, port);
        } catch (UnknownHostException ex) {
            this.addMessage("UnknownHostException");
        } catch (IOException ex) {
            this.addMessage("IOException");
        }


    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        // 名前送信メッセージをセット
        String line = "101 NAME ";
        line += this.jTextField3.getText();
        //this.jTextField1.setText(line);
        // 送信
        this.sendMessage(line);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        // プレイメッセージをセット
        String line = "205 PLAY 0 ";
        // 選択したものを取得
        String koma = (String) this.jComboBox1.getSelectedItem();
        String action = (String) this.jComboBox2.getSelectedItem();
        String trend = (String) this.jComboBox3.getSelectedItem();
        // コマ・アクションが未選択
        if (koma.equals("-")) {
            return;
        }
        if (action.equals("-")) {
            return;
        }
        // コマ名をスライス
        koma = koma.substring(0, 1);
        // アクション名をスライス
        action = action.substring(0, 3);
        // 送信文に追加
        line += koma + " " + action;
        // オプション
        // 1-1のとき
        if (action.equals("1-1")) {
            if (trend.equals("F") || trend.equals("G") || trend.equals("M")) {
                line += " " + trend;
            } else {
                return;
            }
        }
        // 2-1, 3-4のとき
        if (action.equals("2-1") || action.equals("3-4")) {
            if (trend.equals("F") || trend.equals("G")) {
                line += " " + trend;
            } else {
                return;
            }
        }
        // 6-2のとき
        if (action.equals("6-2")) {
            if (trend.equals("FF") || trend.equals("FG") || trend.equals("GG")) {
                line += " " + trend;
            } else {
                return;
            }
        }
        // 7-1のとき
        if (action.equals("7-1")) {
            if (trend.startsWith("T")) {
                line += " " + trend;
            } else {
                return;
            }
        }
        //this.jTextField1.setText(line);
        // 送信
        this.sendMessage(line);
        // コンボボックスリセット
        this.resetCombos();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField3KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            this.jButton3.doClick();
        }
    }//GEN-LAST:event_jTextField3KeyPressed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    /**
     * 重みファイル選択
     *
     * @param evt
     */
    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        // ファイル選択ウィンドウを開く
        JFileChooser filechooser = new JFileChooser(DEFAULT_FORDER_PATH);
        filechooser.addChoosableFileFilter(new CsvFilter());
        filechooser.setAcceptAllFileFilterUsed(false);

        int selected = filechooser.showOpenDialog(this);
        if (selected == JFileChooser.APPROVE_OPTION) {
            this.weightFile = filechooser.getSelectedFile();
            this.jTextField5.setText(this.weightFile.getName());
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    /**
     * コンボボックスのリセット
     */
    private void resetCombos() {
        this.jComboBox1.setSelectedIndex(0);
        this.jComboBox2.setSelectedIndex(0);
        this.jComboBox3.setSelectedIndex(0);
    }

    /**
     * 通信先にメッセージを送信する。サーバにつながっていない場合は送らない
     */
    public void sendMessage(String sendText) {
        //属性情報を作成
        SimpleAttributeSet attribute = new SimpleAttributeSet();
        //属性情報の文字色に赤を設定
        attribute.addAttribute(StyleConstants.Foreground, Color.RED);

        try {
            //サーバーへ送信
            if (this.connecter.canWrite()) {
                connecter.sendMessage(sendText);
                document.insertString(document.getLength(), "[send]" + sendText + "\n", attribute);
            } else {
                document.insertString(document.getLength(), "(送信失敗)" + sendText + "\n", attribute);
            }
            this.jTextPane1.setCaretPosition(document.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * 通信によって文字を取得したときに（だけ）呼び出される
     */
    @Override
    public void reciveMessage(String text) {
        //属性情報の文字色に青を設定
        try {
            SimpleAttributeSet attribute = new SimpleAttributeSet();
            attribute.addAttribute(StyleConstants.Foreground, Color.BLUE);
            //ドキュメントにその属性情報つきの文字列を挿入
            document.insertString(document.getLength(), "[recv]" + text + "\n", attribute);
            this.jTextPane1.setCaretPosition(document.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * ログなどの追加用　黒文字で表示
     */
    @Override
    public void addMessage(String text) {
        try {
            SimpleAttributeSet attribute = new SimpleAttributeSet();
            attribute.addAttribute(StyleConstants.Foreground, Color.BLACK);
            //ドキュメントにその属性情報つきの文字列を挿入
            document.insertString(document.getLength(), "[log]" + text + "\n", attribute);
            this.jTextPane1.setCaretPosition(document.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * ログ出力用　色付き
     */
    public void addMessage(String text, SimpleAttributeSet attribute) {
        try {
            //ドキュメントにその属性情報つきの文字列を挿入
            document.insertString(document.getLength(), "[log]" + text + "\n", attribute);
            this.jTextPane1.setCaretPosition(document.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(ClientGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void resetNNPanel() {
        // 円のサイズを決定する
        double circleSize = inputPanel.getHeight() / Tochka.INPUT_LENGTH;
        inputPanel.setLayout(new GridLayout(1, 1));
        
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel inputMiddle1LinePanel;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JPanel middle1Middle2LinePanel;
    private javax.swing.JPanel middle1Panel;
    private javax.swing.JPanel middle2OutputLinePanel;
    private javax.swing.JPanel middle2Panel;
    private javax.swing.JPanel nnPanel;
    private javax.swing.JPanel outputPanel;
    // End of variables declaration//GEN-END:variables
}
