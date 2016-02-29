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

public class DLVMultiHelper{
	private Dlv dlv;
	private byte mode = Dlv.SYNCHRONOUS;
	private Lock lock;
	private Condition runCondition;
	private boolean running = false;

	public DLVMultiHelper(int index){
		dlv = new Dlv("./dlv"+index+".exe");
		dlv.setNumberOfModels(10);
		dlv.setMaxint(64);
		lock = new ReentrantLock();
		runCondition = lock.newCondition();
	}

	public void setProgram(Program program){
		dlv.setProgram(program);
	}

	public void setMaxInt(int maxInt){
		dlv.setMaxint(maxInt);
	}

	public void setFilter(String[] filters){
		dlv.setFilter(filters);
	}	

	public void setDlvMode(byte mode){
		this.mode = mode;
	}

	public void runDlv() throws DLV_InvocationException{
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
			dlv.run(mode);
		} finally {
			lock.unlock();
		}		
	}

	public List<Model> getModels() throws DLV_InvocationException{
		List<Model> models = new ArrayList<Model>();
		Dlv dlvInstance = dlv;	
		int counter = 0;
		while(dlvInstance.hasMoreModels())
		{
			Model model=dlvInstance.nextModel();
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
