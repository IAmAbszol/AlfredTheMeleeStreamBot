package alfred.utils;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Selection {

	private static JFileChooser jfc;
	private static File f = null;
	
	public static File selectedSave() {
		JButton tmp = new JButton();
		jfc = new JFileChooser();
		jfc.setCurrentDirectory(new java.io.File("."));
		jfc.setDialogTitle("Select Where To Save Template");
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (jfc.showOpenDialog(tmp) == JFileChooser.APPROVE_OPTION) {
			JFrame frame = new JFrame("File Save Name");
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setBounds(100, 100, 450, 150);
			JPanel contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			frame.setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JPanel panel = new JPanel();
			panel.setBounds(10, 11, 414, 89);
			contentPane.add(panel);
			panel.setLayout(null);
			
			JLabel lblNewLabel = new JLabel("Desired File Name");
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 14));
			lblNewLabel.setBounds(10, 11, 394, 30);
			panel.add(lblNewLabel);
			
			JTextField textField = new JTextField();
			textField.setFont(new Font("Dialog", Font.PLAIN, 12));
			textField.setBounds(10, 52, 295, 20);
			panel.add(textField);
			textField.setColumns(10);
			
			JButton go = new JButton("Go");
			go.setBounds(315, 52, 89, 23);
			panel.add(go);
			
			frame.setResizable(false);
			frame.setVisible(true);
			
			go.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					SwingUtilities.invokeLater(new Runnable() {
			            @Override
			            public void run() {
			            	frame.dispose();
			            	f = new File(jfc.getCurrentDirectory() + "/" + textField.getText() + ".obj");
			            }
			        });
				}
				
			});
			return f = new File(jfc.getCurrentDirectory() + "/" + jfc.getName());
		}
		return f;
	}
	
	public File selectedLoad() {
		File f = null;
		JButton tmp = new JButton();
		jfc = new JFileChooser();
		jfc.setCurrentDirectory(new java.io.File("user.home"));
		jfc.setDialogTitle("Select Your Saved Render Object");
		jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (jfc.showOpenDialog(tmp) == JFileChooser.APPROVE_OPTION) {
			return f = new File(jfc.getSelectedFile().getAbsolutePath());
		}
		return f;
	}
	
}
