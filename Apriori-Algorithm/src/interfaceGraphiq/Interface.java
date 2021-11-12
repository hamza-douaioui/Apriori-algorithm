package interfaceGraphiq;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import classes.AprioriFunctions;
import classes.FrequentItems;

public class Interface extends JFrame {
	JLabel fileName;
	JTextField fileNameField;
	JLabel minSupp;
	JTextField minSuppFiled;
	
	JButton okButton;
	JButton effacer;
	JTextArea zoneResultat;
	double minSup;

	public Interface() {
		
		fileName= new JLabel("File Name: ");
		fileNameField=new JTextField(40);
		
		minSupp = new JLabel("Minimum Support: ");
		minSuppFiled=new JTextField(40);
		
		okButton= new JButton("Validate");
		okButton.setBackground(Color.GREEN);
		
		
		effacer= new JButton("Wipe Off");
		effacer.setBackground(Color.RED);
		
		zoneResultat = new JTextArea();
		zoneResultat.setBackground(Color.GRAY);
		zoneResultat.setLineWrap(true);
		zoneResultat.setForeground(Color.BLACK);
		zoneResultat.setFont(new Font("Serif", Font.PLAIN, 20));
		zoneResultat.setEditable(false);
		zoneResultat.setText(
				"Université Chouaïb Doukkali\nFac. Sciences, Dept. Informatique\nEl JADIDA\n\tMaster BIBDA: informatique décisionnelle \n\tTP (Apriori algorithm).");


		
		
		JPanel panelFichier = new JPanel(new FlowLayout(FlowLayout.LEFT,80,10));
		panelFichier.add(fileName);
		panelFichier.add(fileNameField);
		
		JPanel panelSupport = new JPanel(new FlowLayout(FlowLayout.LEFT,60,0));
		panelSupport.add(minSupp);
		panelSupport.add(minSuppFiled);
		
		JPanel panelH= new JPanel(new GridLayout(2, 1, 20, 20));
		panelH.add(panelFichier);
		panelH.add(panelSupport);
		
		JPanel Panel = new JPanel(new BorderLayout(20, 20));
		Panel.add(panelH, BorderLayout.NORTH);
		
		JPanel p = new JPanel(new FlowLayout());
		p.add(panelH);
		p.add(okButton);
		p.add(effacer);

		


		
		JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
		mainPanel.add(p, BorderLayout.NORTH);
		mainPanel.add(zoneResultat, BorderLayout.CENTER);
		mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED, 5),
				"Apriori algorithm", TitledBorder.CENTER, TitledBorder.TOP,
				new Font("Serif", Font.BOLD, 18), Color.BLUE));

		Container con = getContentPane();
		con.add(mainPanel);
		
		
		okButton.addActionListener(e -> {
			String fileName = this.fileNameField.getText();
			try {
				minSup= Double.parseDouble( this.minSuppFiled.getText());
				if (fileName.endsWith(".txt")) {
					AprioriFunctions<String> generator = new AprioriFunctions<>();
					List<Set<String>> itemsetList = new ArrayList<>();
					try {
						itemsetList= generator.transactionListInitialiation(fileName);
						FrequentItems<String> data = generator.generate(itemsetList, minSup);
						int i = 1;
						StringBuffer strB= new StringBuffer();
						for (Set<String> itemset : data.getFrequentItemsetList()) {
							strB.append(i++ +": "+ itemset+", support: "+ data.getSupport(itemset)+"\n");
						}
						this.zoneResultat.setText(strB.toString());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}else {
					this.zoneResultat.setText("Verifier le nom de votre document exemple du nom valid 'exemple.txt'");
					// System.out.println("Verifier le nom de votre document");
				}
			} catch (Exception e2) {
				
				this.zoneResultat.setText("support minimum must be double");
				 
			}
			

		});
		
		
		effacer.addActionListener(e -> {
			this.zoneResultat.setText("Université Chouaïb Doukkali\nFac. Sciences, Dept. Informatique\nEl JADIDA\n\tMaster BIBDA: informatique décisionnelle \n\tTP (Apriori algorithm).");
			this.minSuppFiled.setText("");
			this.fileNameField.setText("");
		});
		
		// window setting dimension ......
				setTitle("Apriori Algorithm");
				setSize(new Dimension(1000, 700));
				setLocationRelativeTo(null);
				setVisible(true);
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
	}
}
