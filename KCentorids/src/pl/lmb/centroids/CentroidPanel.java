package pl.lmb.centroids;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class CentroidPanel extends JPanel implements KeyListener, MouseListener
{
	private int k, n, metric;
	private int iterCounter;

	private List<GroupedPoint> points;
	private boolean startDrawing = false, linesActive;
	private List<Point> clusters;
	public final static Color[] colors = {Color.BLUE, Color.CYAN, Color.RED, Color.GREEN, Color.DARK_GRAY,
			Color.MAGENTA, Color.YELLOW, Color.PINK, Color.ORANGE, Color.LIGHT_GRAY};

	private CentroidFrame frame;
	
	public CentroidPanel(CentroidFrame frame)
	{
		this.frame = frame;
		addMouseListener(this);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, 600, 600);
		
		g2d.setColor(Color.BLACK);
		
		if (startDrawing)
		{
			for (GroupedPoint point : points)
			{
				int group = point.getGroup();
				g2d.setColor(getClusterColor(group));
				if (linesActive) //TODO
					g2d.drawLine(point.x, point.y, clusters.get(group).x,  clusters.get(group).y);
				g2d.drawOval(point.x, point.y, 3, 3);
			}

			int i = 0;
			for (Point point : clusters)
			{
				g2d.setColor(getClusterColor(i++));
				Ellipse2D.Double circle = new Ellipse2D.Double(point.x, point.y, 6, 6);
				g2d.fill(circle);
			}
		}
		
		g2d.dispose();
		
	}
	
	public void initialize()
	{
		randomize();
		clusters = new ArrayList<>();
	}
	
	private void randomize()
	{
		int r = (n < 600 ? 600 : n);
		
		List<Integer> rangeX = IntStream.range(0, r).boxed()
		        .collect(Collectors.toCollection(ArrayList::new));
		Collections.shuffle(rangeX);
		List<Integer> rangeY = IntStream.range(0, r).boxed()
		        .collect(Collectors.toCollection(ArrayList::new));
		Collections.shuffle(rangeY);
		
		points = new ArrayList<>(n);
		for (int i = 0; i < n; i++)
		{
			points.add(i, new GroupedPoint(rangeX.get(i) % 600, rangeY.get(i) % 600));
		}
		
		startDrawing = true;
	}
	
	public boolean algorithmStep()
	{
		boolean changeNoticed = false;
		for (GroupedPoint p : points)
		{
			changeNoticed = setNearestCluster(p);
		}
		boolean meansChanged = updateMeans();
		
		boolean isChanged = changeNoticed || meansChanged;
		if (isChanged)
			iterCounter++;
		
		System.out.println(isChanged);
		return isChanged;
	}
	
	public boolean updateMeans()
	{
		int x = 0, y = 0;
		int pointCounter;
		boolean changeNoticed = false;
		Point currentCluster;
		
		for (int i = 0; i < clusters.size(); i++)
		{
			x = 0;
			y = 0;
			pointCounter = 0;
			currentCluster = clusters.get(i);
			for (GroupedPoint p : points)
			{
				if (p.getGroup() == i)
				{
					x += p.x;
					y += p.y;
					pointCounter++;
				}
			}
			
			if (pointCounter == 0) //Zapobieganie potencjalnemu dzieleniu przez 0
				pointCounter = 1;
			
			x = x / pointCounter;
			y = y / pointCounter;
			if (x != currentCluster.x || y != currentCluster.y)
				changeNoticed = true;
			currentCluster.x = x;
			currentCluster.y = y;
		}
		
		return changeNoticed;
	}
	
	private boolean setNearestCluster(GroupedPoint p)
	{
		int previousCluster = p.getGroup();
		p.setGroup(findNearestCluster(p));
		
		return previousCluster != p.getGroup();
	}
	
	private int findNearestCluster(GroupedPoint p)
	{
		//TODO
		int nearest = 0;
		double distance = 0;
		double minDistance = 100000;
		
		for (int i = 0; i < clusters.size(); i++)
		{
			distance = distanceFunction(p, metric, i);
			if (distance < minDistance)
			{
				nearest = i;
				minDistance = distance;
			}
		}
		
		return nearest;
	}
	
	private double distanceFunction(GroupedPoint p, int function, int cluster)
	{
		double distance = 0;
		
		if (function == 0) //miejska
		{
			distance = Math.abs(p.x - clusters.get(cluster).x) + Math.abs(p.y - clusters.get(cluster).y);
		}
		else if (function == 1) //euklidesowa
		{
			distance = euklidesMetric(p, clusters.get(cluster));
		}
		else if (function == 2)
		{
			distance = maximum(p, clusters.get(cluster));
			System.out.println("-> " + distance);
		}
//		else if (function == 3) //metryka rzeki
//		{
//			final Point O = new Point(300, 300);
//			if (collinear(p, clusters.get(cluster), O))
//			{
//				distance = euklidesMetric(p, clusters.get(cluster));
//			}
//			else
//			{
//				distance = euklidesMetric(p,  O) + euklidesMetric(O, clusters.get(cluster));
//			}
//		}
		
		return distance;
	}
	
	private double euklidesMetric(Point p1, Point p2)
	{
		return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
	}
	
	private int maximum(Point p1, Point p2)
	{
		int val1 = Math.abs(p1.x - p2.x);
		int val2 = Math.abs (p1.y - p2.y);
		return (val1 > val2 ? val1 : val2);
	}
	
//	private boolean collinear(Point p1, Point p2, Point p3) 
//	{
//		return (p1.y - p2.y) * (p1.x - p3.x) == (p1.y - p3.y) * (p1.x - p2.x);
//	}
	
	private Color getClusterColor(int n)
	{
		return colors[n % colors.length];
	}
	
	
	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		int mouseX = e.getX();
		int mouseY = e.getY();
		//Dodaj nową grupę
		if (clusters == null)
			clusters = new ArrayList<>();
		clusters.add(new Point(mouseX, mouseY));
		
		System.out.println(mouseX + "\t" + mouseY);
		
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			linesActive = true;
			boolean changeNoticed = algorithmStep();
			if (changeNoticed)
			{
				frame.iterCounterLabel.setText("Iteracja nr: " + iterCounter);
			}
			else
			{
				JOptionPane.showMessageDialog(this, "Algorytm zakończył działanie:\n"
						+ "Dla " + clusters.size() + " grup przeprowadzono " + iterCounter + " iteracji.");
			}
		}
		
		repaint();
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	public int getK()
	{
		return k;
	}

	public void setK(int k)
	{
		this.k = k;
	}

	public int getN()
	{
		return n;
	}

	public void setN(int n)
	{
		this.n = n;
	}
	
	public List<GroupedPoint> getPoints()
	{
		return points;
	}
	
	public int getIterCounter()
	{
		return iterCounter;
	}

	public void setIterCounter(int iterCounter)
	{
		this.iterCounter = iterCounter;
	}

	public void setLinesActive(boolean linesActive)
	{
		this.linesActive = linesActive;
	}

	public void setMetric(int metric)
	{
		this.metric = metric;
		
	}
	
	
	
}
