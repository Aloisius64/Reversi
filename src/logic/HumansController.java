package logic;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import DLV.DLV_InvocationException;
import DLV.Model;
import DLV.Predicate;
import DLV.Predicate.Fact;
import DLV.Program;
import common.DLVHelper;

public class HumansController extends Controller
{
	public HumansController(){
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

		Program program = new Program(); // creates a new program
		program.addProgramFile("./dlvCode/facts.dl");
		program.addProgramFile("./dlvCode/giocabile.dl");
		program.addProgramFile("./dlvCode/adiacenti.dl");
		program.addProgramFile("./dlvCode/check.dl");
		program.addProgramFile("./dlvCode/guess.dl");

		DLVHelper.setProgram(program);
		DLVHelper.setFilter(new String[] { "in" });
		List<Model> models = null;
		try
		{
			DLVHelper.runDlv();
			models = DLVHelper.getModels();
		}
		catch (DLV_InvocationException e1)
		{
			e1.printStackTrace();
			return false;
		}

		GameBoard.getInstance().resetChooseMove();
		if (models != null)
		{
			Iterator<Model> iterator = models.iterator();
			while (iterator.hasNext())
			{
				Model currentModel = iterator.next();
				if (currentModel.isNoModel() || currentModel.isEmpty())
				{
					// Se vuoto non posso muovermi e passo al prossimo turno
					return false;
				}
				while (currentModel.hasMorePredicates())
				{
					Predicate predicate = currentModel.nextPredicate();
					while (predicate.hasMoreFacts())
					{
						Fact fact = predicate.nextFact();
						int row = Integer.parseInt(fact.getTermAt(1)) - 1;
						int column = Integer.parseInt(fact.getTermAt(2)) - 1;
						GameBoard.getInstance().addChooseMove(row, column);
					}
				}
			}
		}
		else
		{
			return false; // Se non puoi muoverti passa il turno
		}

		if (GameBoard.getInstance().getChooseMove().size() > 0)
		{
			// Aspetta che la casella sia scelta
			while (!Player.OK)
			{
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			// Passa al prossimo turno
			return true;
		}
		return false;
	}
}
