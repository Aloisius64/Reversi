package common;

public class DLVManager {
	private static DLVManager instance = null;
	private DLVMultiHelper[] dlvs;
	
	private DLVManager(){
		dlvs = new DLVMultiHelper [7];
		for (int m = 0; m < 7; m++){
			dlvs[m] = new DLVMultiHelper(m+1);
		}
	}
	
	public static DLVManager getInstance(){
		if(instance==null){
			instance = new DLVManager();
		}
		return instance;
	}
	
	public static DLVMultiHelper getDLVMultiHelper(int index){
		return getInstance().dlvs[index];
	}
}
