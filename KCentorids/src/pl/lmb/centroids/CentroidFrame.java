package pl.lmb.centroids;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CentroidFrame extends JFrame
{
	public final static int WIDTH = 600;
	public final static int HEIGHT = 660;
	
	private CentroidPanel centroidPanel;
	protected JLabel iterCounterLabel;
	private String[] functions = {"Metryka miejska", "Metryka euklidesowa", "Metryka maksimowa"};
	
	public CentroidFrame()
	{
		setLayout(new BorderLayout());
		setTitle("Algorytm k-centroidów");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setSize(new Dimension(WIDTH, HEIGHT));
		
		centroidPanel = new CentroidPanel(this);
		centroidPanel.setPreferredSize(new Dimension(600, 600));
		add(centroidPanel, BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel();
		
		JPanel fields = new JPanel();
		JLabel nLabel = new JLabel("Liczba punktów: ");
		JComboBox<String> functionComboBox = new JComboBox<>(functions);
		JTextField nField = new JTextField("1000", 6);
		fields.add(functionComboBox);
		fields.add(nLabel);
		fields.add(nField);
		
		JButton losujButton = new JButton("Losuj");
		losujButton.addActionListener(e ->
			{
				int n, function = 0;
				try
				{
					function = functionComboBox.getSelectedIndex();
					n = Integer.parseInt(nField.getText());
					centroidPanel.setIterCounter(0);
				}
				catch (NumberFormatException exc)
				{
					JOptionPane.showMessageDialog(this, "Wprowadzone wartosci nie są dodatnimi liczbami całkowitymi!\n"
							+ "Zostany nadane wartości domyślne -> k=3, n=1000",
							"Błędne wartości", JOptionPane.ERROR_MESSAGE);
					n = 1000;
				}
				
				centroidPanel.setN(n);
				centroidPanel.setMetric(function);
				centroidPanel.setIterCounter(0);
				centroidPanel.setLinesActive(false);
				centroidPanel.initialize();
				centroidPanel.requestFocusInWindow();
				centroidPanel.revalidate();
				centroidPanel.repaint();
			});
		
		iterCounterLabel = new JLabel("Iteracja nr: ");
		southPanel.add(fields);
		southPanel.add(losujButton);
		southPanel.add(iterCounterLabel);
		
		add(southPanel, BorderLayout.SOUTH);
		
		
		setVisible(true);
	}
}
