package logic;

import java.io.IOException;
import java.util.List;

import DLV.DLV_InvocationException;
import DLV.Model;
import DLV.Program;

import common.DLVHelper;

public class Choise {
	public int myInRow;
	public int myInColumn;
	public GameBoard gameBoard;
	public int opInRow;
	public int opInColumn;

	public Choise(){

	}

	public List<Model> getModels(){
		// Calcola le celle in cui puoi muoverti, tramite DLV
		try
		{
			gameBoard.printFacts();
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
			return null;
		}
		return models;
	}
}
