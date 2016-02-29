package logic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import DLV.DLV_InvocationException;
import DLV.Model;
import DLV.Predicate;
import DLV.Predicate.Fact;
import DLV.Program;
import common.DLVHelper;
import common.DLVMultiHelper;
import common.Point;

public class GameBoard implements Cloneable
{
	private static GameBoard instance = null;

	public static GameBoard getInstance()
	{
		if (instance == null)
		{
			instance = new GameBoard();
		}
		return instance;
	}

	private boolean white;
	int size;
	private Pawn[][] gameBoard;
	private BufferedWriter fileWriter;
	//
	Player currentPlayer;

	//
	private List<Point> chooseMove;

	private GameBoard()
	{
		size = 8;
		white = false;
		gameBoard = new Pawn[size][size];
		for (int i = 0; i < size; i++)
		{
			gameBoard[i] = new Pawn[size];
			for (int j = 0; j < size; j++)
			{
				gameBoard[i][j] = Pawn.Unknow;
			}
		}
		gameBoard[3][3] = Pawn.White;
		gameBoard[3][4] = Pawn.Black;
		gameBoard[4][3] = Pawn.Black;
		gameBoard[4][4] = Pawn.White;
		//
		chooseMove = new ArrayList<Point>();
	}

	public GameBoard(int choose)
	{
		size = 8;
		gameBoard = new Pawn[size][size];
		for (int i = 0; i < size; i++)
		{
			gameBoard[i] = new Pawn[size];
			for (int j = 0; j < size; j++)
			{
				gameBoard[i][j] = Pawn.Unknow;
			}
		}
		//
		chooseMove = new ArrayList<Point>();
	}

	public void addChooseMove(int row, int column)
	{
		chooseMove.add(new Point(row, column));
	}

	@Override
	public GameBoard clone() throws CloneNotSupportedException
	{
		super.clone();
		GameBoard clone = new GameBoard(0);

		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				clone.gameBoard[i][j] = gameBoard[i][j];
			}
		}
		return clone;
	}

	public int countOfPawn(Pawn pawn)
	{
		int count = 0;
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (gameBoard[i][j] == pawn)
				{
					count++;
				}
			}
		}
		return count;
	}

	public List<Point> getChooseMove()
	{
		return chooseMove;
	}

	public Pawn getPawn(int row, int column)
	{
		return gameBoard[row][column];
	}

	public Pawn getTypeCurrentPlayer()
	{
		return currentPlayer.pawn;
	}

	public boolean getWhite()
	{
		return white;
	}

	public boolean isAvalidPosition(int xPosition, int yPosition)
	{
		Iterator<Point> iterator = chooseMove.iterator();
		while (iterator.hasNext())
		{
			Point next = iterator.next();
			if ((next.row == xPosition) && (next.column == yPosition))
			{
				return true;
			}
		}
		return false;
	}

	public void printFacts() throws IOException
	{
		fileWriter = new BufferedWriter(new FileWriter(new File("./dlvCode/facts.dl")));
		fileWriter.write("giocatore(" + 0 + "," + currentPlayer.pawn.toString().toLowerCase()
				+ ").\n");
		Pawn opponentPawn;
		if (currentPlayer.pawn == Pawn.White)
		{
			opponentPawn = Pawn.Black;
		}
		else
		{
			opponentPawn = Pawn.White;
		}
		fileWriter.write("giocatore(" + 1 + "," + opponentPawn.toString().toLowerCase() + ").\n");

		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (gameBoard[i][j] != Pawn.Unknow)
				{
					if (currentPlayer.pawn == gameBoard[i][j])
					{
						fileWriter.write("pedina(" + 0 + ", " + (i + 1) + ", " + (j + 1) + ").\n");
					}
					else
					{
						fileWriter.write("pedina(" + 1 + ", " + (i + 1) + ", " + (j + 1) + ").\n");
					}
					fileWriter.flush();
				}
			}
		}
		fileWriter.close();
	}

	public void printFactsToFile(String file) throws IOException
	{
		BufferedWriter newFileWriter = new BufferedWriter(new FileWriter(new File("./dlvCode/"
				+ file)));
		newFileWriter.write("giocatore(" + 0 + "," + currentPlayer.pawn.toString().toLowerCase()
				+ ").\n");
		Pawn opponentPawn;
		if (currentPlayer.pawn == Pawn.White)
		{
			opponentPawn = Pawn.Black;
		}
		else
		{
			opponentPawn = Pawn.White;
		}
		newFileWriter
				.write("giocatore(" + 1 + "," + opponentPawn.toString().toLowerCase() + ").\n");

		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				if (gameBoard[i][j] != Pawn.Unknow)
				{
					if (currentPlayer.pawn == gameBoard[i][j])
					{
						newFileWriter.write("pedina(" + 0 + ", " + (i + 1) + ", " + (j + 1)
								+ ").\n");
					}
					else
					{
						newFileWriter.write("pedina(" + 1 + ", " + (i + 1) + ", " + (j + 1)
								+ ").\n");
					}
					newFileWriter.flush();
				}
			}
		}
		newFileWriter.close();
	}

	// Usata per creare successive partite
	public void reInit()
	{
		instance = new GameBoard();
	}

	public void reorganizeBoard(int xPosition, int yPosition)
	{
		Program program = new Program();
		program.addProgramFile("./dlvCode/facts.dl");
		program.addProgramFile("./dlvCode/check.dl");
		program.addProgramFile("./dlvCode/giocabile.dl");
		program.addProgramFile("./dlvCode/adiacenti.dl");
		program.addString("in(" + 0 + ", " + (xPosition + 1) + ", " + (yPosition + 1) + ").");
		DLVHelper.setProgram(program);
		DLVHelper.setFilter(new String[] { "mangiata" });
		List<Model> models = null;
		try
		{
			DLVHelper.runDlv();
			models = DLVHelper.getModels();
		}
		catch (DLV_InvocationException e1)
		{
			e1.printStackTrace();
			return;
		}

		if (models != null)
		{
			Iterator<Model> iterator = models.iterator();
			while (iterator.hasNext())
			{
				Model currentModel = iterator.next();
				if (!currentModel.isNoModel())
				{
					while (currentModel.hasMorePredicates())
					{
						Predicate predicate = currentModel.nextPredicate();
						while (predicate.hasMoreFacts())
						{
							Fact fact = predicate.nextFact();
							int row = Integer.parseInt(fact.getTermAt(1)) - 1;
							int column = Integer.parseInt(fact.getTermAt(2)) - 1;
							Pawn pawn = gameBoard[row][column];
							if (pawn == Pawn.White)
							{
								gameBoard[row][column] = Pawn.Black;
							}
							else if (pawn == Pawn.Black)
							{
								gameBoard[row][column] = Pawn.White;
							}
						}
					}
				}
			}
		}
	}

	public void reorganizeBoardWithDLV(int xPosition, int yPosition, DLVMultiHelper dlvHelper)
	{
		Program program = new Program();
		program.addProgramFile("./dlvCode/facts.dl");
		program.addProgramFile("./dlvCode/check.dl");
		program.addProgramFile("./dlvCode/giocabile.dl");
		program.addProgramFile("./dlvCode/adiacenti.dl");
		program.addString("in(" + 0 + ", " + (xPosition + 1) + ", " + (yPosition + 1) + ").");
		dlvHelper.setProgram(program);
		dlvHelper.setFilter(new String[] { "mangiata" });
		List<Model> models = null;
		try
		{
			dlvHelper.runDlv();
			models = dlvHelper.getModels();
		}
		catch (DLV_InvocationException e1)
		{
			e1.printStackTrace();
			return;
		}

		if (models != null)
		{
			Iterator<Model> iterator = models.iterator();
			while (iterator.hasNext())
			{
				Model currentModel = iterator.next();
				if (currentModel.isNoModel())
				{
					return;
				}
				while (currentModel.hasMorePredicates())
				{
					Predicate predicate = currentModel.nextPredicate();
					while (predicate.hasMoreFacts())
					{
						Fact fact = predicate.nextFact();
						int row = Integer.parseInt(fact.getTermAt(1)) - 1;
						int column = Integer.parseInt(fact.getTermAt(2)) - 1;
						Pawn pawn = gameBoard[row][column];
						if (pawn == Pawn.White)
						{
							gameBoard[row][column] = Pawn.Black;
						}
						else if (pawn == Pawn.Black)
						{
							gameBoard[row][column] = Pawn.White;
						}
					}
				}
			}
		}
	}

	public void resetChooseMove()
	{
		chooseMove.clear();
	}

	public void setCurrentPlayer(Player player)
	{
		currentPlayer = player;
	}

	public void setPawn(Pawn pawn, int row, int column)
	{
		gameBoard[row][column] = pawn;
	}

	public void setPawnFromPlayer(int row, int column)
	{
		gameBoard[row][column] = currentPlayer.pawn;
		white = !white;
	}
}
