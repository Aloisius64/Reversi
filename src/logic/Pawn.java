package logic;

public enum Pawn {
	Unknow(0),
	White(1),
	Black(2),
	Selectable(3);
	
	private final int type;
	
	Pawn(int type){
		this.type = type;
	}

	public int getType() {
		return type;
	}
}
