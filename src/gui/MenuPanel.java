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

public class MenuPanel extends JPanel
{
	private static final long serialVersionUID = -7698882335788781866L;
	private Image humanVScpu;
	private Image humanVShuman;
	private Image cpuVScpu;
	private Image credit;

	private int choose;

	private MainFrame mainFrame;

	public MenuPanel(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		GameBoard.getInstance().reInit();
		choose = 1;

		try
		{
			humanVScpu = ImageIO.read(new File("Images/Menu2.png"));
			humanVShuman = ImageIO.read(new File("Images/Menu1.png"));
			cpuVScpu = ImageIO.read(new File("Images/Menu3.png"));
			credit = ImageIO.read(new File("Images/name.png"));
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
				if ((x > 200) && (x < 600) && (y > 290) && (y < 340))
				{
					final GameManager gameManager;
					gameManager = new GameManager(ModalityGame.HumansVsHumans);
					MenuPanel.this.mainFrame.switchToPanel(new GamePanel(MenuPanel.this.mainFrame));
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
					MenuPanel.this.mainFrame.switchToPanel(new ChooseCPUAIPanel(
							MenuPanel.this.mainFrame, true));
				else if ((x > 260) && (x < 540) && (y > 430) && (y < 480))
					MenuPanel.this.mainFrame.switchToPanel(new ChooseCPUAIPanel(
							MenuPanel.this.mainFrame, false));
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
					case KeyEvent.VK_ENTER:
					{
						final GameManager gameManager;
						switch (choose)
						{
							case 1:
								gameManager = new GameManager(ModalityGame.HumansVsHumans);
								MenuPanel.this.mainFrame.switchToPanel(new GamePanel(
										MenuPanel.this.mainFrame));
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
							case 2:
								MenuPanel.this.mainFrame.switchToPanel(new ChooseCPUAIPanel(
										MenuPanel.this.mainFrame, true));
								break;
							case 3:
								MenuPanel.this.mainFrame.switchToPanel(new ChooseCPUAIPanel(
										MenuPanel.this.mainFrame, false));
								break;
							default:
								MenuPanel.this.mainFrame.switchToPanel(new ChooseCPUAIPanel(
										MenuPanel.this.mainFrame, false));
								break;
						}
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
				g.drawImage(humanVShuman, 0, 0, 800, 600, null);
				break;
			case 2:
				g.drawImage(humanVScpu, 0, 0, 800, 600, null);
				break;
			case 3:
				g.drawImage(cpuVScpu, 0, 0, 800, 600, null);
				break;
		}
		g.drawImage(credit, 0, 0, 800, 600, null);
	}
}
