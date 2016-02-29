package logic.ai;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import logic.Choise;
import logic.GameBoard;
import logic.GameManager;
import logic.Pawn;
import logic.Player;
import DLV.DLV_InvocationException;
import DLV.Model;
import DLV.Predicate;
import DLV.Predicate.Fact;
import DLV.Program;
import common.DLVHelper;

public class AIController2 extends AIController
{
	private int depth;

	public AIController2()
	{
		super();
		depth = 3;
	}

	public AIController2(int depth)
	{
		super();
		this.depth = depth;
	}

	private Choise getChoiseFromModel(Model model) throws CloneNotSupportedException, IOException
	{
		Choise choise = new Choise();
		choise.gameBoard = GameBoard.getInstance().clone();
		if (model.isNoModel())
		{
			return null;
		}
		Predicate predicate = model.nextPredicate();
		while (predicate.hasMoreFacts())
		{
			Fact fact = predicate.nextFact();
			choise.myInRow = Integer.parseInt(fact.getTermAt(1)) - 1;
			choise.myInColumn = Integer.parseInt(fact.getTermAt(2)) - 1;
		}
		if (GameBoard.getInstance().getTypeCurrentPlayer() == Pawn.White)
		{
			choise.gameBoard.setCurrentPlayer(GameManager.blackPlayer);
		}
		else
		{
			choise.gameBoard.setCurrentPlayer(GameManager.whitePlayer);
		}
		// Stampo i fatti per il caso corrente
		choise.gameBoard.printFacts();

		Program program = new Program();
		program.addProgramFile("./dlvCode/facts.dl");
		program.addProgramFile("./dlvCode/giocabile.dl");
		program.addProgramFile("./dlvCode/adiacenti.dl");
		program.addProgramFile("./dlvCode/check.dl");
		program.addProgramFile("./dlvCode/guess.dl");
		program.addProgramFile("./dlvCode/optimize.dl");
		program.addProgramFile("./dlvCode/valore.dl");

		DLVHelper.setProgram(program);
		DLVHelper.setFilter(new String[] { "in" });
		List<Model> models = null;
		try
		{
			DLVHelper.runDlv();
			models = DLVHelper.getModels();
		}
		catch (DLV_InvocationException ex)
		{
			ex.printStackTrace();
		}

		choise.gameBoard.resetChooseMove();
		if (models != null)
		{
			int size = models.size();
			int selectedModel = ((int) (Math.random() * 1000)) % size;

			Model currentModel = models.get(selectedModel);

			if (currentModel.isNoModel() || currentModel.isEmpty())
			{
				return null;
			}

			Predicate predicateOpponent = currentModel.nextPredicate();
			while (predicateOpponent.hasMoreFacts())
			{
				Fact fact = predicateOpponent.nextFact();
				choise.opInRow = Integer.parseInt(fact.getTermAt(1)) - 1;
				choise.opInColumn = Integer.parseInt(fact.getTermAt(2)) - 1;
			}

			choise.gameBoard.setPawn(choise.gameBoard.getTypeCurrentPlayer(), choise.opInRow,
					choise.opInColumn);
			choise.gameBoard.reorganizeBoard(choise.opInRow, choise.opInColumn);
		}

		return choise;
	}

	@Override
	public boolean update(Player player)
	{
		// Calcola le celle in cui puoi muoverti, tramite DLV
		try
		{
			GameBoard.getInstance().printFacts();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		Program program = new Program();
		program.addProgramFile("./dlvCode/facts.dl");
		program.addProgramFile("./dlvCode/giocabile.dl");
		program.addProgramFile("./dlvCode/adiacenti.dl");
		program.addProgramFile("./dlvCode/check.dl");
		program.addProgramFile("./dlvCode/guess.dl");
		program.addProgramFile("./dlvCode/optimize.dl");
		program.addProgramFile("./dlvCode/valore.dl");

		DLVHelper.setProgram(program);
		DLVHelper.setFilter(new String[] { "in" });
		List<Model> models = null;
		try
		{
			DLVHelper.runDlv();
			models = DLVHelper.getModels();
		}
		catch (DLV_InvocationException ex)
		{
			ex.printStackTrace();
			return false;
		}

		GameBoard.getInstance().resetChooseMove();
		int size = models.size();
		int chooseRow = -1;
		int chooseColumn = -1;
		int maxPawn = 0;
		try
		{
			// Per ogni possibile scelta di in
			for (int m = 0; m < size; m++)
			{
				Choise initChoise = getChoiseFromModel(models.get(m));
				if (initChoise != null)
				{
					if (m == 1)
					{
						chooseRow = initChoise.myInRow;
						chooseColumn = initChoise.myInColumn;
					}
					// Coda di utilità, usata per sostituire l'albero delle scelte
					Queue<Choise> queue = new ArrayDeque<Choise>();
					queue.add(initChoise);
					for (int d = 0; d < depth; d++)
					{
						Queue<Choise> newQueue = new ArrayDeque<Choise>();
						while (!queue.isEmpty())
						{
							Choise remove = queue.remove();
							// Verifica il modello
							Pawn typeCurrentPlayer = GameBoard.getInstance().getTypeCurrentPlayer();
							int countOfPawnWhite = remove.gameBoard.countOfPawn(Pawn.White);
							int countOfPawnBlack = remove.gameBoard.countOfPawn(Pawn.Black);
							// Trovo la combinazione con il più alto numero
							// possibile di pedine del mio colore
							if (typeCurrentPlayer == Pawn.White)
							{
								if (countOfPawnWhite > maxPawn)
								{
									chooseRow = initChoise.myInRow;
									chooseColumn = initChoise.myInColumn;
									maxPawn = countOfPawnWhite;
								}
							}
							else
							{
								if (countOfPawnBlack > maxPawn)
								{
									chooseRow = initChoise.myInRow;
									chooseColumn = initChoise.myInColumn;
									maxPawn = countOfPawnBlack;
								}
							}
							//
							List<Model> removeModels = remove.getModels();
							if (removeModels != null)
							{ // Se ho modelli da valutare
								Iterator<Model> iteratorModels = removeModels.iterator();
								while (iteratorModels.hasNext())
								{
									Model nextModel = iteratorModels.next();
									if (!nextModel.isNoModel())
									{
										Choise choiseFromModel = getChoiseFromModel(nextModel);
										if (choiseFromModel != null)
										{
											newQueue.add(choiseFromModel);
										}
									}
								}
							}
						}
						queue.addAll(newQueue);
					}
				}
			}
		}
		catch (CloneNotSupportedException | IOException e)
		{
			e.printStackTrace();
		}

		if ((chooseRow != -1) && (chooseColumn != -1))
		{
			row = chooseRow;
			column = chooseColumn;
			try
			{
				GameBoard.getInstance().printFacts();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			GameBoard.getInstance().setPawnFromPlayer(row, column);
			GameBoard.getInstance().reorganizeBoard(row, column);
			Player.OK = true; // impostato a true -> il giocatore è riuscito a fare la sua mossa
			try
			{
				Thread.sleep(20);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
}
