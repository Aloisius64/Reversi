package gui;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame
{
	private static final long serialVersionUID = -4899458925033064767L;
	private static MainFrame instance = null;

	public static MainFrame getInstance()
	{
		if (instance == null)
			instance = new MainFrame();
		return instance;
	}

	public static void main(String[] args)
	{
		new MainFrame();
	}

	public MainFrame()
	{
		// super();
		initWindowProperties();
		// Da Cambiare
		// getContentPane().setLayout(new BorderLayout());
		// getContentPane().add(new GamePanel(this), BorderLayout.CENTER);
		switchToPanel(new MenuPanel(this));
		instance = this;
	}

	private void initWindowProperties()
	{
		setTitle("Lord Of The Ring Reversi");

		setSize(800, 600);
		// Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// setBounds(0, 0, screenSize.width, screenSize.height);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		setUndecorated(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void switchToPanel(JPanel panel)
	{
		getContentPane().removeAll();
		setContentPane(panel);
		// Riorganizzazione nel pannello
		panel.revalidate();
		// Abilità il pannello alla ricezioni di eventi da tastiera
		panel.requestFocus();
		setVisible(true);
		setEnabled(true);
	}
}
