package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GameOverPanel extends JPanel
{
	private static final long serialVersionUID = 205360759363871585L;
	private Image fellowship;
	private Image sauron;
	private Image draw;
	private Image click;

	private int choose;

	public GameOverPanel(MainFrame mainFrame, int choose)
	{
		this.choose = choose;

		try
		{
			fellowship = ImageIO.read(new File("Images/FellowshipWin.png"));
			sauron = ImageIO.read(new File("Images/SauronWin.png"));
			draw = ImageIO.read(new File("Images/Menu3.png"));
			click = ImageIO.read(new File("Images/click.png"));
		}
		catch (IOException e)
		{
			System.out.println("L'immagine di background non  può essere caricata.");
		}

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				MainFrame.getInstance().switchToPanel(new MenuPanel(MainFrame.getInstance()));
				repaint();
			}
		});

		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				MainFrame.getInstance().switchToPanel(new MenuPanel(MainFrame.getInstance()));
				repaint();
			}
		});
	}

	@Override
	public void paintComponent(Graphics g)
	{
		switch (choose)
		{
			case 1:
				g.drawImage(fellowship, 0, 0, 800, 600, null);
				break;
			case 2:
				g.drawImage(sauron, 0, 0, 800, 600, null);
				break;
			case 3:
				g.drawImage(draw, 0, 0, 800, 600, null);
				break;
		}
		g.drawImage(click, 0, 0, 800, 600, null);
	}
}
