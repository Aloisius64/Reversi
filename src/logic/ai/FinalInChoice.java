package logic.ai;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FinalInChoice {
	int chooseRow = -1;
	int chooseColumn = -1;
	int maxPawn = 0;
	
	private Lock lock;
	
	FinalInChoice() {
		lock = new ReentrantLock();
	}
	
	void requireLock(){
		lock.lock();
	}
	
	void freeLock(){
		lock.unlock();
	}

	int getChooseRow() {
		return chooseRow;
	}

	void setChooseRow(int chooseRow) {
		this.chooseRow = chooseRow;
	}

	int getChooseColumn() {
		return chooseColumn;
	}

	void setChooseColumn(int chooseColumn) {
		this.chooseColumn = chooseColumn;
	}

	int getMaxPawn() {
		return maxPawn;
	}

	void setMaxPawn(int maxPawn) {
		this.maxPawn = maxPawn;
	}	
	
}
