package logic.ai;

import java.io.IOException;
import java.util.List;
import logic.Controller;
import logic.GameBoard;
import logic.Player;
import DLV.DLV_InvocationException;
import DLV.Model;
import DLV.Predicate;
import DLV.Predicate.Fact;
import DLV.Program;
import common.DLVHelper;

// Sarà la classe che chiamerà il modulo dlv
public class AIController extends Controller
{
	protected int row;
	protected int column;
	protected boolean canGoOn = true;

	public AIController()
	{
		super();
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
		if (models != null)
		{
			int size = models.size();
			int selectedModel = ((int) (Math.random() * 1000)) % size;

			Model currentModel = models.get(selectedModel);

			if (currentModel.isNoModel() || currentModel.isEmpty())
			{
				return false; // Se vuoto non posso muovermi e passo al prossimo
								// turno
			}

			Predicate predicate = currentModel.nextPredicate();
			canGoOn = false;
			while (predicate.hasMoreFacts())
			{
				Fact fact = predicate.nextFact();
				canGoOn = true;
				row = Integer.parseInt(fact.getTermAt(1)) - 1;
				column = Integer.parseInt(fact.getTermAt(2)) - 1;
			}
		}
		else
		{
			return false; // Se non puoi muoverti passa il turno
		}

		// Passa al prossimo turno
		if (canGoOn)
		{
			GameBoard.getInstance().setPawnFromPlayer(row, column);
			GameBoard.getInstance().reorganizeBoard(row, column);
			Player.OK = true; // impostato a true -> il giocatore è riuscito a
								// fare la sua mossa
			try
			{
				Thread.sleep(500);
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
