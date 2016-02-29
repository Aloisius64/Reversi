package common;

import java.util.ArrayList;
import java.util.List;

import DLV.DLV_InvocationException;
import DLV.Dlv;
import DLV.Model;
import DLV.Program;

public class DLVHelperOld {
	private static DLVHelperOld instance = null;
	private static Dlv dlv;
	private static byte mode = Dlv.SYNCHRONOUS;
	
	private DLVHelperOld(){
		dlv = new Dlv("./dlv.exe");
		dlv.setNumberOfModels(10);
		dlv.setMaxint(64);
	}
	
	public static DLVHelperOld getInstance(){
		if(instance == null){
			instance = new DLVHelperOld();
		}
		return instance;
	}
	
	@SuppressWarnings("static-access")
	public static void setProgram(Program program){
		getInstance().dlv.setProgram(program);
	}
	
	@SuppressWarnings("static-access")
	public static void setMaxInt(int maxInt){
		getInstance().dlv.setMaxint(maxInt);
	}
	
	@SuppressWarnings("static-access")
	public static void setFilter(String[] filters){
		getInstance().dlv.setFilter(filters);
	}	
	
	@SuppressWarnings("static-access")
	public static void setDlvMode(byte mode){
		getInstance().mode = mode;
	}
	
	@SuppressWarnings("static-access")
	public static void runDlv() throws DLV_InvocationException{
		getInstance().dlv.run(mode);
	}
	
	@SuppressWarnings("static-access")
	public static List<Model> getModels() throws DLV_InvocationException{
		List<Model> models = new ArrayList<Model>();
		Dlv dlvInstance = getInstance().dlv;	
		int counter = 0;
		while(dlvInstance.hasMoreModels())
		{
			Model model=dlv.nextModel();
			models.add(model);
			counter++;
		}
		return counter > 0 ? models : null;
	}
}
