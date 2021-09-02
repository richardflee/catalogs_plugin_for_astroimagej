package com.github.richardflee.astroimagej.catalog_ui;



import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.Box;
import javax.swing.GroupLayout;
import static javax.swing.GroupLayout.Alignment.CENTER;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/*
 * Advanced Java Swing
 *
 * This program presents JDialog.
 *
 * Author: Jan Bodnar
 * Website: zetcode.com
 * Last modified: February 2015
 */
class AboutDialog extends JDialog {

    public AboutDialog(Frame parent) {
        super(parent);

        initUI();
    }

    private void initUI() {

        ImageIcon icon = new ImageIcon("notes.png");
        JLabel label = new JLabel(icon);

        JLabel name = new JLabel("Notes, 1.23");
        name.setFont(new Font("Serif", Font.BOLD, 13));

        JButton btn = new JButton("OK");
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });

        createLayout(name, label, btn);

        setModalityType(ModalityType.MODELESS);

        setTitle("About Notes");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(getParent());
    }

    private void createLayout(JComponent... arg) {

        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup(CENTER)
            .addComponent(arg[0])
            .addComponent(arg[1])
            .addComponent(arg[2])
            .addGap(200)
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGap(30)
            .addComponent(arg[0])
            .addGap(20)
            .addComponent(arg[1])
            .addGap(20)
            .addComponent(arg[2])
            .addGap(30)
        );

        pack();
    }
}

public class JDialogEx extends JFrame 
    implements ActionListener {
	
	private AboutDialog ad = null;

    public JDialogEx() {

        initUI();
    }

    private void initUI() {

        createMenuBar();
        
        setTitle("Simple Dialog");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createMenuBar() {

        JMenuBar menubar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem closeMi = new JMenuItem("Close");
        // closeMi.setMnemonic(KeyEvent.VK_A);
        fileMenu.add(closeMi);
        
        closeMi.addActionListener(this);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem aboutMi = new JMenuItem("About");
        aboutMi.setMnemonic(KeyEvent.VK_A);
        helpMenu.add(aboutMi);
        
        aboutMi.addActionListener(this);
        
       
        
        

        menubar.add(fileMenu);
        menubar.add(Box.createGlue());
        menubar.add(helpMenu);
        setJMenuBar(menubar);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    	
    	if (e.getActionCommand() == "About") {    		
    		showAboutDialog();
    	} else {
    		System.out.println("Close");
    		if (ad == null) {
    			System.out.println("NULLL");
    		} else {
    			ad.dispose();
    		}
    	}
        
    }

    private void showAboutDialog() {

        ad = new AboutDialog(this);
        ad.setVisible(true);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JDialogEx sd = new JDialogEx();
                sd.setVisible(true);
            }
        });
    }
}

