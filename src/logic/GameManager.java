package logic;

import gui.GameOverPanel;
import gui.MainFrame;
import logic.ai.AIController;
import logic.ai.AIController3;
import logic.ai.AIController4;

public class GameManager
{
	public static Player whitePlayer;
	public static Player blackPlayer;

	public GameManager()
	{
		createHumansVsHumans();
	}

	public GameManager(ModalityGame modalityGame)
	{
		switch (modalityGame)
		{
			case HumansVsCpu:
				createHumansVsCpu();
				break;
			case CpuVsCpu:
				createCpuVsCpu();
				break;
			case HumansVsCpuAdavanced:
				createHumansVsCpuAdvanced();
				break;
			case CpuAdavancedVsCpuAdavanced:
				createCpuVsCpuAdvanced();
				break;
			case CpuProVsCpuPro:
				createCpuVsCpuPro();
				break;
			case HumanVsCpuPro:
				createHumanVsCpuPro();
				break;
			default:
				createHumansVsHumans();
				break;
		}
	}

	private void createCpuVsCpu()
	{
		// Creo la tabella di gioco
		GameBoard.getInstance();
		// Creo i giocatori
		whitePlayer = new Player(Pawn.White, new AIController());
		blackPlayer = new Player(Pawn.Black, new AIController());
	}

	private void createCpuVsCpuAdvanced()
	{
		// Creo la tabella di gioco
		GameBoard.getInstance();
		// Creo i giocatori
		whitePlayer = new Player(Pawn.White, new AIController3());
		blackPlayer = new Player(Pawn.Black, new AIController3());
	}

	private void createCpuVsCpuPro()
	{
		// Creo la tabella di gioco
		GameBoard.getInstance();
		// Creo i giocatori
		whitePlayer = new Player(Pawn.White, new AIController4());
		blackPlayer = new Player(Pawn.Black, new AIController4());
	}

	private void createHumansVsCpu()
	{
		// Creo la tabella di gioco
		GameBoard.getInstance();
		// Creo i giocatori
		whitePlayer = new Player(Pawn.White, new HumansController());
		blackPlayer = new Player(Pawn.Black, new AIController());
	}

	private void createHumansVsCpuAdvanced()
	{
		// Creo la tabella di gioco
		GameBoard.getInstance();
		// Creo i giocatori
		whitePlayer = new Player(Pawn.White, new HumansController());
		blackPlayer = new Player(Pawn.Black, new AIController3());
	}

	private void createHumansVsHumans()
	{
		// Creo la tabella di gioco
		GameBoard.getInstance();
		// Creo i giocatori
		whitePlayer = new Player(Pawn.White, new HumansController());
		blackPlayer = new Player(Pawn.Black, new HumansController());
	}

	private void createHumanVsCpuPro()
	{
		// Creo la tabella di gioco
		GameBoard.getInstance();
		// Creo i giocatori
		whitePlayer = new Player(Pawn.White, new HumansController());
		blackPlayer = new Player(Pawn.Black, new AIController4());
	}

	public void update()
	{
		boolean playerWhiteUpdate = false;
		boolean playerBlackUpdate = false;
		do
		{
			int time = (int) System.currentTimeMillis() / 1000;
			playerBlackUpdate = blackPlayer.update();
			int timeBlack = (int) System.currentTimeMillis() / 1000;
			System.out.println("BlackTime: " + (timeBlack - time));
			Player.OK = false;
			playerWhiteUpdate = whitePlayer.update();
			int timeWhite = (int) System.currentTimeMillis() / 1000;
			System.out.println("BlackTime: " + (timeWhite - timeBlack));
			Player.OK = false;
			// try
			// {
			// Thread.sleep(20);
			// }
			// catch (InterruptedException e)
			// {
			// e.printStackTrace();
			// }
		} while (playerWhiteUpdate || playerBlackUpdate);
		// Partita finita -> si passa all'endPanel
		int blackPawn = GameBoard.getInstance().countOfPawn(blackPlayer.pawn);
		int whitePawn = GameBoard.getInstance().countOfPawn(whitePlayer.pawn);
		if (blackPawn > whitePawn)
		{
			// Ha vinto il nero
			MainFrame.getInstance().switchToPanel(new GameOverPanel(MainFrame.getInstance(), 2));
			System.out.println("Nero");
		}
		else if (whitePawn > blackPawn)
		{
			// Ha vinto il bianco
			MainFrame.getInstance().switchToPanel(new GameOverPanel(MainFrame.getInstance(), 1));
			System.out.println("Bianco");
		}
		else
		{
			// Parità
			System.out.println("Pari");
		}
	}
}
