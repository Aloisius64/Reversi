package logic.ai;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

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

public class AIController3 extends AIController
{
	private class TreeWorker extends Thread{
		String factsFile;
		CyclicBarrier barrier;
		FinalInChoice finalChoice;
		Model model;

		TreeWorker(String fatcsFile, CyclicBarrier barrier, Model model, FinalInChoice finalChoice) {
			this.factsFile = fatcsFile;
			this.barrier = barrier;
			this.model = model;
			this.finalChoice = finalChoice;
		}

		@Override
		public void run() {
			super.run();
			try {
				calculateChoiceTree(model, factsFile, finalChoice);
				barrier.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}

	}
	private int depth;

	public AIController3()
	{
		super();
		depth = 3;
	}

	public AIController3(int depth)
	{
		super();
		this.depth = depth;
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
		FinalInChoice finalChoice = new FinalInChoice();
		try {
			TreeWorker[] workers = new TreeWorker[size];
			CyclicBarrier barrier = new CyclicBarrier(size+1);
			for (int m = 0; m < size; m++){
				workers[m] = new TreeWorker("facts"+(m+1)+".dl", barrier, models.get(m), finalChoice);
				workers[m].start();
			}
			barrier.await();
		} catch (InterruptedException | BrokenBarrierException e2) {
			e2.printStackTrace();
		}

		try
		{
			GameBoard.getInstance().printFacts();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
		
		if(finalChoice.chooseRow==-1 && finalChoice.chooseColumn==-1){
			return false;
		}
		
		row = finalChoice.chooseRow;
		column = finalChoice.chooseColumn;
		GameBoard.getInstance().setPawnFromPlayer(row, column);
		GameBoard.getInstance().reorganizeBoard(row, column);
		Player.OK = true; // impostato a true -> il giocatore è riuscito a fare la sua mossa
		
		return true;
	}

	private void calculateChoiceTree(Model startModel, String facts, FinalInChoice finalChoice){
		try
		{
			Choise initChoise = getChoiseFromModel(startModel, facts);
			if (initChoise != null)
			{
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
						finalChoice.requireLock();
						try{
							if (typeCurrentPlayer == Pawn.White)
							{
								if (countOfPawnWhite > finalChoice.maxPawn)
								{
									finalChoice.chooseRow = initChoise.myInRow;
									finalChoice.chooseColumn = initChoise.myInColumn;
									finalChoice.maxPawn = countOfPawnWhite;
								}
							}
							else
							{
								if (countOfPawnBlack > finalChoice.maxPawn)
								{
									finalChoice.chooseRow = initChoise.myInRow;
									finalChoice.chooseColumn = initChoise.myInColumn;
									finalChoice.maxPawn = countOfPawnBlack;
								}
							}
						} finally {
							finalChoice.freeLock();
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
									Choise choiseFromModel = getChoiseFromModel(nextModel, facts);
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
		catch (CloneNotSupportedException | IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private Choise getChoiseFromModel(Model model, String factsFile) throws CloneNotSupportedException, IOException
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
		choise.gameBoard.printFactsToFile(factsFile);

		Program program = new Program();
		program.addProgramFile("./dlvCode/"+factsFile);
		program.addProgramFile("./dlvCode/giocabile.dl");
		program.addProgramFile("./dlvCode/adiacenti.dl");
		program.addProgramFile("./dlvCode/check.dl");
		program.addProgramFile("./dlvCode/guess.dl");
		program.addProgramFile("./dlvCode/optimize.dl");
		program.addProgramFile("./dlvCode/valore.dl");

		DLVHelper.setProgram(program);
		DLVHelper.setFilter(new String[] {"in"});
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

			choise.gameBoard.setPawn(choise.gameBoard.getTypeCurrentPlayer(), choise.opInRow, choise.opInColumn);
			choise.gameBoard.reorganizeBoard(choise.opInRow, choise.opInColumn);
		}
		return choise;
	}
}
