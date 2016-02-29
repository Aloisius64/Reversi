package logic;

public enum ModalityGame
{
	HumansVsHumans(1), HumansVsCpu(2), CpuVsCpu(3), HumansVsCpuAdavanced(4), CpuAdavancedVsCpuAdavanced(
			5), HumanVsCpuPro(6), CpuProVsCpuPro(7);

	private final int type;

	private ModalityGame(int type)
	{
		this.type = type;
	}

	public int getType()
	{
		return type;
	}
}