package logic;

public class Player {
	public static boolean OK = false;
	Pawn pawn;
	Controller controller;
	
	public Player(Pawn pawn, Controller controller){
		this.pawn = pawn;
		this.controller = controller;
	}
	
	boolean update(){
		GameBoard.getInstance().setCurrentPlayer(this);
		boolean controllerUpdate = controller.update(this);
		return controllerUpdate;
	}
}
