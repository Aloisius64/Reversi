package common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import DLV.DLV_InvocationException;
import DLV.Dlv;
import DLV.Model;
import DLV.Program;

public class DLVHelper{
	private static DLVHelper instance = null;
	private static Dlv dlv;
	private static byte mode = Dlv.SYNCHRONOUS;
	private static Lock lock;
	private static Condition runCondition;
	private static boolean running = false;

	private DLVHelper(){
		dlv = new Dlv("./dlv.exe");
		dlv.setNumberOfModels(10);
		dlv.setMaxint(64);
		lock = new ReentrantLock();
		runCondition = lock.newCondition();
	}

	public static DLVHelper getInstance(){
		if(instance == null){
			instance = new DLVHelper();
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
		lock.lock();
		try{
			while(running){
				try {
					runCondition.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			running = true;
			getInstance().dlv.run(mode);
		} finally {
			lock.unlock();
		}		
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
		lock.lock();
		try{
			running = false;
			runCondition.signalAll();
		} finally {
			lock.unlock();
		}	
		return counter > 0 ? models : null;
	}
}
