package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import logic.GameBoard;
import logic.Pawn;
import logic.Player;
import common.Point;

public class GamePanel extends JPanel
{
	private class Repainter extends Thread
	{
		public Repainter()
		{
			super("Repainter");
		}

		@Override
		public void run()
		{
			super.run();
			while (repainter != null)
				try
				{
					Thread.sleep(50);
					repaint();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
		}
	}

	private static final long serialVersionUID = -8860204251354754377L;
	private MainFrame mainFrame;
	//
	private int dimensionBoard;
	private int startXGameBoard;
	private int dimensionPawn;

	private int numWhitePawns;
	private int numBlackPawns;

	private Repainter repainter;

	private Image blackMoves;
	private Image whiteMoves;
	private Image whitePawn;
	private Image blackPawn;
	private Image chessBoard;
	private Image board;
	private Image exit;
	private Image exitSelected;
	private boolean home;

	public GamePanel(final MainFrame mainFrame)
	{
		super();
		this.mainFrame = mainFrame;
		home = false;

		numWhitePawns = 2;
		numBlackPawns = 2;

		try
		{
			blackMoves = ImageIO.read(new File("Images/GamePanel/BlackMove.png"));
			whiteMoves = ImageIO.read(new File("Images/GamePanel/WhiteMove.png"));
			whitePawn = ImageIO.read(new File("Images/white.png"));
			blackPawn = ImageIO.read(new File("Images/black-01.png"));
			chessBoard = ImageIO.read(new File("Images/chessboard2-01.png"));
			board = ImageIO.read(new File("Images/board.png"));
			exit = ImageIO.read(new File("Images/GamePanel/exit.png"));
			exitSelected = ImageIO.read(new File("Images/GamePanel/exitS.png"));
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
				int x = e.getX();
				int y = e.getY();
				if ((x > 344) && (x < 453) && (y > 412) && (y < 450))
					home = true;
				else
					home = false;
			}
		});

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				int x = e.getX();
				int y = e.getY();
				int yPosition = (e.getX() - startXGameBoard) / dimensionPawn;
				int xPosition = e.getY() / dimensionPawn;
				// Controllo di validità
				if (GameBoard.getInstance().isAvalidPosition(xPosition, yPosition))
				{
					GameBoard.getInstance().setPawnFromPlayer(xPosition, yPosition);
					GameBoard.getInstance().reorganizeBoard(xPosition, yPosition);
					Player.OK = true; // impostato a true -> il giocatore è
										// riuscito a fare la sua mossa
				}

				if ((x > 344) && (x < 453) && (y > 412) && (y < 450) && home)
				{
					mainFrame.switchToPanel(new MenuPanel(mainFrame));
				}

			}
		});
		repainter = new Repainter();
		repainter.start();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		repainter = null;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		boolean white = GameBoard.getInstance().getWhite();

		if (white)
			g.drawImage(whiteMoves, 0, 0, 800, 600, null);
		else
			g.drawImage(blackMoves, 0, 0, 800, 600, null);

		// Schacchiera e Pedine
		int correctorX = 0;
		int correctorY = 0;
		dimensionBoard = 400;// ((mainFrame.getHeight() * 90) / 100) - 150;
		startXGameBoard = (mainFrame.getWidth() - dimensionBoard) / 2;
		dimensionPawn = dimensionBoard / 8;
		// g.setColor(Color.BLACK);

		g.drawImage(board, startXGameBoard - 5, 0, dimensionBoard + 10, dimensionBoard + 5, null);
		g.drawImage(chessBoard, startXGameBoard, 0, dimensionBoard, dimensionBoard, null);

		numWhitePawns = 0;
		numBlackPawns = 0;
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				if (GameBoard.getInstance().getPawn(i, j) == Pawn.White)
				{
					g.drawImage(whitePawn, startXGameBoard + (dimensionPawn * j) + correctorX,
							correctorY + (dimensionPawn * i), dimensionPawn, dimensionPawn, null);
					numWhitePawns++;
				}
				else if (GameBoard.getInstance().getPawn(i, j) == Pawn.Black)
				{
					g.drawImage(blackPawn, startXGameBoard + (dimensionPawn * j) + correctorX,
							correctorY + (dimensionPawn * i), dimensionPawn, dimensionPawn, null);
					numBlackPawns++;
				}
			}
		}

		Iterator<Point> iterator = GameBoard.getInstance().getChooseMove().iterator();
		while (iterator.hasNext())
		{
			if (white)
				g.setColor(Color.WHITE);
			else
				g.setColor(Color.BLACK);
			Point point = iterator.next();
			g.fillOval((dimensionPawn / 5) + startXGameBoard + (dimensionPawn * point.column)
					+ correctorX + 8, (dimensionPawn / 5) + correctorY
					+ (dimensionPawn * point.row) + 7, dimensionPawn / 3, dimensionPawn / 3);
		}
		// Mosse Possibili

		Font font = new Font("Calibri", Font.BOLD, 30);
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(font);
		g2.setColor(Color.BLACK);
		FontRenderContext frc = g2.getFontRenderContext();
		float heigth = (545 * this.getHeight()) / 600;
		TextLayout whitePawnsLabel = new TextLayout("" + numWhitePawns, font, frc);
		whitePawnsLabel.draw(g2, (this.getWidth() * 255) / 800, heigth);

		g2.setColor(Color.WHITE);
		TextLayout blackPawnsLabel = new TextLayout("" + numBlackPawns, font, frc);
		blackPawnsLabel.draw(g2, (this.getWidth() * 525) / 800, heigth);

		if (home)
			g.drawImage(exitSelected, 0, 0, 800, 600, null);
		else
			g.drawImage(exit, 0, 0, 800, 600, null);
	}
}
