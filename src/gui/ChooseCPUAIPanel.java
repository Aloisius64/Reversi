package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import logic.GameBoard;
import logic.GameManager;
import logic.ModalityGame;

public class ChooseCPUAIPanel extends JPanel
{
	private static final long serialVersionUID = -7417133134620666174L;
	private Image pro;
	private Image advanced;
	private Image base;
	private Image backS;
	private Image back;
	private boolean backSelected;

	private int choose;

	private MainFrame mainFrame;

	public ChooseCPUAIPanel(MainFrame mainFrame, final boolean isThereAHuman)
	{
		this.mainFrame = mainFrame;
		choose = 1;
		backSelected = false;

		try
		{
			pro = ImageIO.read(new File("Images/Diff3.png"));
			advanced = ImageIO.read(new File("Images/Diff2.png"));
			base = ImageIO.read(new File("Images/Diff1.png"));
			back = ImageIO.read(new File("Images/back2.png"));
			backS = ImageIO.read(new File("Images/back.png"));
		}
		catch (IOException e)
		{
			System.out.println("L'immagine di background non  può essere caricata.");
		}

		addMouseMotionListener(new MouseMotionAdapter()
		{
			@Override
			public void mouseMoved(MouseEvent e)
			{
				Point point = e.getPoint();
				int x = point.x;
				int y = point.y;
				if ((x > 200) && (x < 600) && (y > 290) && (y < 340))
					choose = 1;
				else if ((x > 230) && (x < 570) && (y > 360) && (y < 410))
					choose = 2;
				else if ((x > 260) && (x < 540) && (y > 430) && (y < 480))
					choose = 3;

				if ((x > 345) && (x < 481) && (y > 497) && (y < 562))
					backSelected = true;
				else
					backSelected = false;
				repaint();
			}
		});

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				Point point = e.getPoint();
				int x = point.x;
				int y = point.y;
				final GameManager gameManager;
				if ((x > 200) && (x < 600) && (y > 290) && (y < 340))
				{
					if (isThereAHuman)
						gameManager = new GameManager(ModalityGame.HumanVsCpuPro);
					else
						gameManager = new GameManager(ModalityGame.CpuProVsCpuPro);
					GameBoard.getInstance().reInit();
					ChooseCPUAIPanel.this.mainFrame.switchToPanel(new GamePanel(
							ChooseCPUAIPanel.this.mainFrame));
					Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							gameManager.update();
						}
					});
					thread.setName("Updater");
					thread.start();
				}
				else if ((x > 230) && (x < 570) && (y > 360) && (y < 410))
				{
					if (isThereAHuman)
						gameManager = new GameManager(ModalityGame.HumansVsCpuAdavanced);
					else
						gameManager = new GameManager(ModalityGame.CpuAdavancedVsCpuAdavanced);
					GameBoard.getInstance().reInit();
					ChooseCPUAIPanel.this.mainFrame.switchToPanel(new GamePanel(
							ChooseCPUAIPanel.this.mainFrame));
					Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							gameManager.update();
						}
					});
					thread.setName("Updater");
					thread.start();
				}
				else if ((x > 260) && (x < 540) && (y > 430) && (y < 480))
				{
					if (isThereAHuman)
						gameManager = new GameManager(ModalityGame.HumansVsCpu);
					else
						gameManager = new GameManager(ModalityGame.CpuVsCpu);
					GameBoard.getInstance().reInit();
					ChooseCPUAIPanel.this.mainFrame.switchToPanel(new GamePanel(
							ChooseCPUAIPanel.this.mainFrame));
					Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							gameManager.update();
						}
					});
					thread.setName("Updater");
					thread.start();
				}
				else if ((x > 345) && (x < 481) && (y > 497) && (y < 562))
				{
					ChooseCPUAIPanel.this.mainFrame.switchToPanel(new MenuPanel(
							ChooseCPUAIPanel.this.mainFrame));
				}
			}
		});

		addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				super.keyPressed(e);

				switch (e.getKeyCode())
				{
					case KeyEvent.VK_DOWN:
					{
						choose++;
						if (choose > 3)
							choose = 1;
						break;
					}
					case KeyEvent.VK_UP:
					{
						choose--;
						if (choose < 1)
							choose = 3;
						break;
					}
					case KeyEvent.VK_ESCAPE:
					{
						ChooseCPUAIPanel.this.mainFrame.switchToPanel(new MenuPanel(
								ChooseCPUAIPanel.this.mainFrame));
					}
					case KeyEvent.VK_ENTER:
					{
						final GameManager gameManager;
						switch (choose)
						{
							case 1:
								if (isThereAHuman)
									gameManager = new GameManager(ModalityGame.HumanVsCpuPro);
								else
									gameManager = new GameManager(ModalityGame.CpuProVsCpuPro);
								break;
							case 2:
								if (isThereAHuman)
									gameManager = new GameManager(ModalityGame.HumansVsCpuAdavanced);
								else
									gameManager = new GameManager(
											ModalityGame.CpuAdavancedVsCpuAdavanced);
								break;
							case 3:
								if (isThereAHuman)
									gameManager = new GameManager(ModalityGame.HumansVsCpu);
								else
									gameManager = new GameManager(ModalityGame.CpuVsCpu);
								break;
							default:
								gameManager = new GameManager(ModalityGame.CpuVsCpu);
								break;
						}
						GameBoard.getInstance().reInit();
						ChooseCPUAIPanel.this.mainFrame.switchToPanel(new GamePanel(
								ChooseCPUAIPanel.this.mainFrame));
						Thread thread = new Thread(new Runnable()
						{
							@Override
							public void run()
							{
								gameManager.update();
							}
						});
						thread.setName("Updater");
						thread.start();
						break;
					}
				}
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
				g.drawImage(pro, 0, 0, 800, 600, null);
				break;
			case 2:
				g.drawImage(advanced, 0, 0, 800, 600, null);
				break;
			case 3:
				g.drawImage(base, 0, 0, 800, 600, null);
				break;
		}
		if (backSelected)
			g.drawImage(backS, 0, 0, 800, 590, null);
		else
			g.drawImage(back, 0, 0, 800, 590, null);
	}
}
