
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class MineSweeperUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 美化用變數
	private Dimension screenSize;
	private GridBagConstraints layoutCons;
	private Font font = new Font("微軟正黑體", Font.PLAIN, 16);
	private Dimension gameSize;
	private ImageIcon icon;
	private ImageIcon emptyIcon;
	
	// 遊戲等級參數
	private int level = 1;
	private final int EASY = 1;
	private final int NORMAL = 2;
	private final int HARD = 3;
	private final int CUSTOM = 4;
	private final int EASY_WIDTH = 9;
	private final int EASY_HEIGHT = 9;
	private final int EASY_MINES = 10;
	private final int NORMAL_WIDTH = 16;
	private final int NORMAL_HEIGHT = 16;
	private final int NORMAL_MINES = 40;
	private final int HARD_WIDTH = 30;
	private final int HARD_HEIGHT = 16;
	private final int HARD_MINES = 99;

	// 長、寬、地雷變數
	private int w_button;
	private int h_button;
	private int mines;

	private int result = 0;
	private final int LOSE = -1;
	private final int WIN = 1;

	private Timer counter;
	private int seconds = 0;
	
	// 地雷介面
	private MineSweeper minePanel;

	// 主視窗元件
	private JMenuBar menu;
	private JMenu gameMenu;
	private JMenuItem newGame;
	private JMenuItem option;
	private JMenuItem exit;

	private JMenu helpMenu;
	private JMenuItem help;
	private JMenuItem about;

	private JPanel infoPanel;
	private JTextField time;
	private JTextField mineCount;

	// 歡迎視窗元件
	private JDialog w_Welcome;
	private JButton btn_easy;
	private JButton btn_normal;
	private JButton btn_hard;
	private JButton btn_custom;

	// 自訂遊戲視窗元件
	private JDialog w_Custom;
	private JPanel customPanel;
	private JSlider slider_width;
	private JSlider slider_height;
	private JSlider slider_mine;
	private JTextField text_width;
	private JTextField text_height;
	private JTextField text_mine;
	private JButton btn_CustomConfirm;

	// 選項視窗元件
	private JDialog w_Option;
	private JPanel optionPanel;
	private JPanel levelPanel;
	private JCheckBox cbx_easy;
	private JCheckBox cbx_normal;
	private JCheckBox cbx_hard;
	private JCheckBox cbx_custom;
	private JButton btn_OptConfirm;
	private JPanel settingPanel;

	// 結果視窗
	private JDialog w_Result;
	private JButton btn_newGame;
	private JButton btn_exit;

	// 幫助、關於視窗
	private JFrame w_Help;
	private JFrame w_About;
	
	

	// 建構基本視窗
	public MineSweeperUI() {
		super("MineSweeperStack");
		
		icon = new ImageIcon(getClass().getResource("MineSweeperIcon.png"));
		emptyIcon = new ImageIcon(getClass().getResource("EmptyIcon.png"));
		gameSize = new Dimension(800, 600);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		/*
		 * *
		 * http://stackoverflow.com/questions/3680221/how-can-i-get-the-monitor-
		 * size-in-java
		 */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		super.setSize(gameSize);
		super.setIconImage(icon.getImage());
		super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		super.setLocation(((int) screenSize.getWidth() - getWidth()) / 2,
				((int) (screenSize.getHeight() - getHeight()) / 2));
		super.setLayout(new BorderLayout());
		layoutCons = new GridBagConstraints();

		menu = new JMenuBar();
		gameMenu = new JMenu("Game");

		newGame = new JMenuItem("New Game");
		newGame.addActionListener(new NewGameListener());
		gameMenu.add(newGame);

		option = new JMenuItem("Option");
		option.addActionListener(new MenuListener());
		gameMenu.add(option);

		exit = new JMenuItem("Exit");
		exit.addActionListener(new ExitListener());
		gameMenu.add(exit);

		menu.add(gameMenu);

		helpMenu = new JMenu("Help");

		help = new JMenuItem("View Help");
		help.addActionListener(new MenuListener());
		helpMenu.add(help);

		about = new JMenuItem("About");
		about.addActionListener(new MenuListener());
		helpMenu.add(about);

		menu.add(helpMenu);
		super.setJMenuBar(menu);

		infoPanel = new JPanel();
		infoPanel.setLayout(new BorderLayout());

		JPanel timePanel = new JPanel();

		JLabel label_time = new JLabel("Times");
		label_time.setHorizontalAlignment(JLabel.CENTER);
		label_time.setFont(font);

		timePanel.add(label_time);

		time = new JTextField("Times", 6);
		time.setEditable(false);
		time.setHorizontalAlignment(JTextField.CENTER);
		time.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		time.setFont(font);
		timePanel.add(time);

		infoPanel.add(timePanel, BorderLayout.WEST);

		JPanel mineCountPanel = new JPanel();

		JLabel label_mineCount = new JLabel("Mines");
		label_mineCount.setHorizontalAlignment(JLabel.CENTER);
		label_mineCount.setFont(font);
		mineCountPanel.add(label_mineCount);

		mineCount = new JTextField("Mine", 6);
		mineCount.setEditable(false);
		mineCount.setHorizontalAlignment(JTextField.CENTER);
		mineCount.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		mineCount.setFont(font);
		mineCountPanel.add(mineCount);

		infoPanel.add(mineCountPanel, BorderLayout.EAST);
		add(infoPanel, BorderLayout.SOUTH);
		super.setVisible(true);

		minePanel = new MineSweeper();

		counter = new Timer();

		welcome();

	}

	// 擺入遊戲元件
	private void gameRunning() {

		gameSize.setSize(minePanel.getPanelSize());
		super.setSize(gameSize);
		super.setLocation(((int) screenSize.getWidth() - getWidth()) / 2,
				((int) (screenSize.getHeight() - getHeight()) / 2));
		super.setResizable(false);

		counter.schedule(new TimerTask() {
			@Override
			public void run() {
				time.setText(Float.toString((float) seconds++ / 10));
				mineCount.setText(Integer.toString(minePanel.getMineCounter()));
				result = minePanel.getResult();
				if (!minePanel.isMineGenerator()) {
					seconds = 0;
					time.setText("0");
				}else if( seconds > 99999){
					seconds = 99999;
				}
				if (result == LOSE) {
					result(LOSE);
					seconds = 0;

				} else if (result == WIN) {
					result(WIN);
					seconds = 0;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}, 0, 1);

		add(minePanel, BorderLayout.CENTER);
		super.setVisible(true);
	}

	// 難度選擇
	private void levelChoosing(int level) {
		switch (level) {
		case EASY:
			minePanel.setNewW_Grasses(EASY_WIDTH);
			minePanel.setNewH_Grasses(EASY_HEIGHT);
			minePanel.setMines(EASY_MINES);
			this.level = level;
			break;
		case NORMAL:
			minePanel.setNewW_Grasses(NORMAL_WIDTH);
			minePanel.setNewH_Grasses(NORMAL_HEIGHT);
			minePanel.setMines(NORMAL_MINES);
			this.level = level;
			break;
		case HARD:
			minePanel.setNewW_Grasses(HARD_WIDTH);
			minePanel.setNewH_Grasses(HARD_HEIGHT);
			minePanel.setMines(HARD_MINES);
			this.level = level;
			break;
		case CUSTOM:
			if (w_button == EASY_WIDTH && h_button == EASY_HEIGHT && mines == EASY_MINES) {
				this.level = EASY;
				levelChoosing(EASY);
			} else if (w_button == NORMAL_WIDTH && h_button == NORMAL_HEIGHT && mines == NORMAL_MINES) {
				this.level = NORMAL;
				levelChoosing(NORMAL);
			} else if (w_button == HARD_WIDTH && h_button == HARD_HEIGHT && mines == HARD_MINES) {
				this.level = HARD;
				levelChoosing(HARD);
			} else {
				minePanel.setNewW_Grasses(w_button);
				minePanel.setNewH_Grasses(h_button);
				minePanel.setMines(mines);
				this.level = level;
			}
			break;
		}
	}

	// 設定歡迎視窗
	private void welcome() {
		w_Welcome = new JDialog(this, "Choose the level", true);
		w_Welcome.setSize(400, 400);
		w_Welcome.setResizable(false);
		w_Welcome.setLocation(((int) screenSize.getWidth() - w_Welcome.getWidth()) / 2,
				((int) (screenSize.getHeight() - w_Welcome.getHeight()) / 2));
		w_Welcome.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		w_Welcome.addWindowListener(new FrameListener());
		/*
		 * http://stackoverflow.com/questions/2080348/java-swing-children-
		 * windows
		 */
		JPanel welcomePanel = new JPanel(new GridLayout(4, 0));

		btn_easy = new JButton("Easy (  9 x 9 , 10 mines )");
		btn_easy.addActionListener(new LevelListener());
		btn_easy.setFont(font);
		btn_easy.setFocusable(false);
		welcomePanel.add(btn_easy);

		btn_normal = new JButton("Normal ( 16 x 16 , 40 mines )");
		btn_normal.addActionListener(new LevelListener());
		btn_normal.setFont(font);
		btn_normal.setFocusable(false);
		welcomePanel.add(btn_normal);

		btn_hard = new JButton("Hard ( 30 x 16 , 99 mines )");
		btn_hard.addActionListener(new LevelListener());
		btn_hard.setFont(font);
		btn_hard.setFocusable(false);
		welcomePanel.add(btn_hard);

		btn_custom = new JButton(" Custom ");
		btn_custom.addActionListener(new LevelListener());
		btn_custom.setFont(font);
		btn_custom.setFocusable(false);
		welcomePanel.add(btn_custom);

		w_Welcome.add(welcomePanel);
		w_Welcome.setVisible(true);

	}

	// 設定自訂遊戲視窗
	private void custom() {
		w_Custom = new JDialog(this, "Custom", true);
		w_Custom.setSize(400, 300);
		w_Custom.setResizable(false);
		w_Custom.setLocation(((int) screenSize.getWidth() - w_Custom.getWidth()) / 2,
				((int) (screenSize.getHeight() - w_Custom.getHeight()) / 2));
		w_Custom.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		w_Custom.addWindowListener(new FrameListener());
		w_Custom.setAlwaysOnTop(true);

		customPanel = new JPanel(new GridBagLayout());

		JLabel width = new JLabel("Width ( 9 ~ 30 )");
		width.setHorizontalAlignment(JLabel.CENTER);
		width.setFont(font);
		addComponents(customPanel, width, 1, 0, 1, 1);

		slider_width = new JSlider(SwingConstants.HORIZONTAL, 9, 30, 9);
		slider_width.setPaintTicks(true);
		slider_width.addChangeListener(new SliderListener());
		slider_width.setFocusable(false);
		addComponents(customPanel, slider_width, 0, 1, 1, 1);

		text_width = new JTextField(Integer.toString(slider_width.getValue()));
		text_width.setEditable(false);
		text_width.setHorizontalAlignment(JTextField.CENTER);
		text_width.setFont(font);
		addComponents(customPanel, text_width, 1, 1, 1, 1);

		JLabel height = new JLabel("Height ( 9 ~ 24 )");
		height.setHorizontalAlignment(JLabel.CENTER);
		height.setFont(font);
		addComponents(customPanel, height, 1, 2, 1, 1);

		slider_height = new JSlider(SwingConstants.HORIZONTAL, 9, 24, 9);
		slider_height.setPaintTicks(true);
		slider_height.addChangeListener(new SliderListener());
		slider_height.setFocusable(false);
		addComponents(customPanel, slider_height, 0, 3, 1, 1);

		text_height = new JTextField(Integer.toString(slider_height.getValue()));
		text_height.setEditable(false);
		text_height.setHorizontalAlignment(JTextField.CENTER);
		text_height.setFont(font);
		addComponents(customPanel, text_height, 1, 3, 1, 1);

		JLabel mines = new JLabel("Mines ( 10 ~ 668 )");
		mines.setHorizontalAlignment(JLabel.CENTER);
		mines.setFont(font);
		addComponents(customPanel, mines, 1, 4, 1, 1);

		slider_mine = new JSlider(SwingConstants.HORIZONTAL, 10, 668, 10);
		slider_mine.setMaximum((slider_width.getValue() * slider_height.getValue())
				- (slider_width.getValue() + slider_height.getValue() - 1));
		slider_mine.setPaintTicks(true);
		slider_mine.addChangeListener(new SliderListener());
		slider_mine.setFocusable(false);
		addComponents(customPanel, slider_mine, 0, 5, 1, 1);

		text_mine = new JTextField(Integer.toString(slider_mine.getValue()));
		text_mine.setEditable(false);
		text_mine.setHorizontalAlignment(JTextField.CENTER);
		text_mine.setFont(font);
		addComponents(customPanel, text_mine, 1, 5, 1, 1);

		w_Custom.add(customPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		btn_CustomConfirm = new JButton("Confirm");
		btn_CustomConfirm.addActionListener(new ConfirmListener());
		btn_CustomConfirm.setFont(font);
		buttonPanel.add(btn_CustomConfirm);

		w_Custom.add(buttonPanel, BorderLayout.SOUTH);

		w_Custom.setVisible(true);
	}

	// 設定選項視窗
	private void setting() {
		w_Option = new JDialog(this, "Option", true);
		w_Option.setSize(400, 300);
		w_Option.setResizable(false);
		w_Option.setLocation(((int) screenSize.getWidth() - w_Option.getWidth()) / 2,
				((int) (screenSize.getHeight() - w_Option.getHeight()) / 2));
		w_Option.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		w_Option.addWindowListener(new FrameListener());

		// 大的面板
		optionPanel = new JPanel(new BorderLayout());
		optionPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
		/*
		 * http://stackoverflow.com/questions/5854005/setting-horizontal-and-
		 * verical-margins
		 */

		// 上面板
		levelPanel = new JPanel(new GridBagLayout());
		levelPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));

		cbx_easy = new JCheckBox("Easy");
		cbx_easy.addItemListener(new LevelListener());
		layoutCons.anchor = GridBagConstraints.WEST;
		addComponents(levelPanel, cbx_easy, 0, 0, 1, 1);

		cbx_normal = new JCheckBox("Nornal");
		cbx_normal.addItemListener(new LevelListener());
		layoutCons.anchor = GridBagConstraints.WEST;
		addComponents(levelPanel, cbx_normal, 0, 1, 1, 1);

		cbx_hard = new JCheckBox("Hard");
		cbx_hard.addItemListener(new LevelListener());
		layoutCons.anchor = GridBagConstraints.WEST;
		addComponents(levelPanel, cbx_hard, 0, 2, 1, 1);

		cbx_custom = new JCheckBox("Custom");
		cbx_custom.addItemListener(new LevelListener());
		cbx_custom.setEnabled(false);
		if( minePanel.isMineGenerator()){
			cbx_custom.setEnabled(true);
		}
		layoutCons.anchor = GridBagConstraints.WEST;
		addComponents(levelPanel, cbx_custom, 0, 3, 1, 1);

		optionPanel.add(levelPanel, BorderLayout.NORTH);

		// 中面板
		customPanel = new JPanel(new GridLayout(3, 1, 100, 2));

		text_width = new JTextField(Integer.toString(minePanel.getNewW_Grasses()));
		text_width.setHorizontalAlignment(JTextField.CENTER);
		layoutCons.anchor = GridBagConstraints.CENTER;
		text_width.addKeyListener(new TextFieldListener());
		addComponents(customPanel, text_width, 0, 1, 1, 1);

		JLabel text_width = new JLabel("Width ( 9 ~ 30)");
		layoutCons.anchor = GridBagConstraints.WEST;
		addComponents(customPanel, text_width, 1, 1, 1, 1);

		text_height = new JTextField(Integer.toString(minePanel.getNewH_Grasses()));
		text_height.setHorizontalAlignment(JTextField.CENTER);
		layoutCons.anchor = GridBagConstraints.CENTER;
		text_height.addKeyListener(new TextFieldListener());
		addComponents(customPanel, text_height, 0, 2, 1, 1);

		JLabel text_height = new JLabel("Height ( 9 ~ 24 )");
		layoutCons.anchor = GridBagConstraints.WEST;
		addComponents(customPanel, text_height, 1, 2, 1, 1);

		text_mine = new JTextField(Integer.toString(minePanel.getMines()));
		text_mine.setHorizontalAlignment(JTextField.CENTER);
		layoutCons.anchor = GridBagConstraints.CENTER;
		text_mine.addKeyListener(new TextFieldListener());
		addComponents(customPanel, text_mine, 0, 3, 1, 1);

		JLabel text_mines = new JLabel("Mines ( 10 ~ 667 )");
		layoutCons.anchor = GridBagConstraints.WEST;
		addComponents(customPanel, text_mines, 1, 3, 1, 1);

		optionPanel.add(customPanel, BorderLayout.CENTER);

		settingPanel = new JPanel(new BorderLayout());
		settingPanel.setForeground(Color.black);

		btn_OptConfirm = new JButton("Confirm");
		btn_OptConfirm.addActionListener(new ConfirmListener());
		settingPanel.add(btn_OptConfirm);

		optionPanel.add(settingPanel, BorderLayout.SOUTH);

		w_Option.add(optionPanel);

		ButtonGroup levelCheckBox = new ButtonGroup();
		levelCheckBox.add(cbx_easy);
		levelCheckBox.add(cbx_normal);
		levelCheckBox.add(cbx_hard);
		levelCheckBox.add(cbx_custom);
		switch (level) {
		case EASY:
			cbx_easy.setSelected(true);
			break;
		case NORMAL:
			cbx_normal.setSelected(true);
			break;
		case HARD:
			cbx_hard.setSelected(true);
			break;
		case CUSTOM:
			cbx_custom.setSelected(true);
			break;
		}

		w_Option.setVisible(true);
	}

	// 結果視窗
	private void result(int result) {
		w_Result = new JDialog(this, "Result", true);
		w_Result.setSize(400, 300);
		w_Result.setResizable(false);
		w_Result.setLocation(((int) screenSize.getWidth() - w_Result.getWidth()) / 2,
				((int) (screenSize.getHeight() - w_Result.getHeight()) / 2));
		w_Result.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		w_Result.addWindowListener(new FrameListener());
		w_Result.setAlwaysOnTop(true);

		JPanel displayPanel = new JPanel();
		displayPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

		JTextPane text_field = new JTextPane();
		SimpleAttributeSet attribs = new SimpleAttributeSet();
		StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
		StyleConstants.setFontFamily(attribs, "微軟正黑體");
		StyleConstants.setFontSize(attribs, 20);
		text_field.setParagraphAttributes(attribs, true);
		// http://stackoverflow.com/questions/24315757/java-align-jtextarea-to-the-right
		text_field.setFont(font);
		text_field.setBackground(new Color(240, 240, 240));
		text_field.setEditable(false);
		text_field.setPreferredSize(new Dimension(380, 150));
		displayPanel.add(text_field);
		w_Result.add(displayPanel, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();

		btn_newGame = new JButton("New Game");
		btn_newGame.addActionListener( new NewGameListener() );
		btn_newGame.setFont(font);
		buttonPanel.add(btn_newGame);

		btn_exit = new JButton("Exit");
		btn_exit.addActionListener(new ExitListener());
		btn_exit.setFont(font);
		buttonPanel.add(btn_exit);

		w_Result.add(buttonPanel, BorderLayout.SOUTH);

		switch (result) {
		case WIN:
			text_field.setText(String.format("Win\n%d seconds\nOpen:%d\nCorrectRate : %.1f%%", seconds / 10,
					minePanel.getTotalOpen(), minePanel.getPercentOpen()));
			result = 0;
			break;
		case LOSE:
			text_field.setText(String.format("Lose\n%d seconds\nOpen:%d\nCorrectRate : %.1f%%", seconds / 10,
					minePanel.getTotalOpen(), minePanel.getPercentOpen()));
			result = 0;
			break;
		}
		w_Result.setVisible(true);
	}

	// 幫助視窗
	private void help() {
		w_Help = new JFrame("Help");
		
		w_Help.setSize(400, 600);
		w_Help.setIconImage( emptyIcon.getImage() );
		w_Help.setResizable(false);
		w_Help.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		w_Help.setLocation(((int) screenSize.getWidth() - w_Help.getWidth() ) , 0);
		w_Help.addWindowListener(new FrameListener());

		JPanel helpPanel = new JPanel();

		JTextPane text_help = new JTextPane();
		text_help.setPreferredSize(new Dimension(380, 580));
		text_help.setContentType("text/html");
		text_help.setText("<div style= \"font-family:微軟正黑體;line-height:12pt\">"
						+"<b>目標</b>"
						+"<p style=\"margin-left: 24.0pt\">在最短的時間內找出空的方塊並避開地雷。</p>"
						+"<p >&nbsp;</p>"
						+"<b>滑鼠操作</strong></b>"
						+"<ul>"
						+"<li>左鍵：翻開格子。</li>"
						+"<li>右鍵：插入旗子。</li>"
						+"<li>同時按下：掃雷。</li>"
						+"</ul>"
						+"<b>玩法</b>"
						+"<ul>"
						+"<li>當翻開一個地雷，遊戲就會結束。</li>"
						+"<li>當翻開一個空格，遊戲可以繼續玩。</li>"
						+"<li>當翻開一個數字，代表周圍八個格子內有多少地雷，用來推論周邊格子是否安全。</li>"
						+"<li>旗子：認為某塊格子是地雷時，可按右鍵將旗子插入，按第二次的時候可以解除。</li>"
						+"<li>掃雷：某塊空格上的數字等於周圍八的格子所含旗子數量時，掃雷會自動翻開其他的格子。</li>"
						+"</ul>"
						+"</div>"
						);
		text_help.setBackground(new Color(240, 240, 240));
		// text_help.setLineWrap(true);

		JScrollPane scroll_help = new JScrollPane(text_help);
		scroll_help.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		helpPanel.add(scroll_help, BorderLayout.CENTER);

		w_Help.add(helpPanel);
		w_Help.setVisible(true);

	}

	// 關於視窗
	private void about() {
		w_About = new JFrame("About");
		
		w_About.setSize(400, 300);
		w_About.setIconImage( emptyIcon.getImage() );
		w_About.setResizable(false);
		w_About.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		w_About.setLocation(((int) screenSize.getWidth() - w_About.getWidth()) / 2,
				((int) (screenSize.getHeight() - w_About.getHeight()) / 2));
		w_About.addWindowListener(new FrameListener());
		
		JPanel aboutPanel = new JPanel();
		aboutPanel.setLayout( new FlowLayout() );
		aboutPanel.setBorder( BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder( BorderFactory.createTitledBorder(""));
		
		JLabel title = new JLabel("MineSweeper",SwingConstants.CENTER);
		title.setVerticalTextPosition( JLabel.CENTER );
		title.setFont( new Font( "微軟正黑體", Font.BOLD, 42));
		
		titlePanel.add(title,BorderLayout.CENTER);
		titlePanel.setPreferredSize( new Dimension( 380, 75 ));
		
		aboutPanel.add(titlePanel);
		
		JPanel contextPanel = new JPanel();
		contextPanel.setPreferredSize( new Dimension( 380, 455));
		
		JTextPane text_help = new JTextPane();
		text_help.setPreferredSize(new Dimension(380, 455));
		text_help.setBackground(new Color(240, 240, 240));
		text_help.setContentType("text/html");
		text_help.setText("<div style= \"font-family:微軟正黑體;font-size:16pt\">"
				+"<p>MineSweeper for ADVANCED PROGRAMMING LANGUAGES</p>"
				+"<p>version 1.0</p>"
				+"<p>2016.08.19 - 2016.08.24</p>"
				);
		
		contextPanel.add(text_help);
		aboutPanel.add(contextPanel);
		
		w_About.add(aboutPanel);
		
		w_About.setVisible(true);
	}

	// 添加元件、排版以及監聽
	private void addComponents(JPanel panel, Component component, int x, int y, int width, int height) {
		layoutCons.gridx = x;
		layoutCons.gridy = y;
		layoutCons.ipadx = 150;
		layoutCons.gridwidth = width;
		layoutCons.gridheight = height;
		layoutCons.insets = new Insets(2, 2, 2, 2);
		panel.add(component, layoutCons);

	}

	
	
	class NewGameListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if( e.getSource() == btn_newGame ){	
				w_Result.dispose();
			}
			minePanel.gameStop();
			levelChoosing(level);
			minePanel.gameStart();
			gameRunning();
	
		}

	}
	
	class MenuListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == option) {
				setting();

			} else if (e.getSource() == help) {
				help();
			} else if (e.getSource() == about) {
				about();
			}
			
		}
		
	}
	
	class ConfirmListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_CustomConfirm) {
				w_button = slider_width.getValue();
				h_button = slider_height.getValue();
				mines = slider_mine.getValue();
				levelChoosing(CUSTOM);
				minePanel.setW_Grasses(slider_width.getValue());
				minePanel.setH_Grasses(slider_height.getValue());
				minePanel.setMines(slider_mine.getValue());
				w_Custom.dispose();
				minePanel.gameStart();
				gameRunning();

			} else if (e.getSource() == btn_OptConfirm) {
				if (cbx_custom.isSelected()) {
					w_button = Integer.parseInt(text_width.getText());
					h_button = Integer.parseInt(text_height.getText());
					mines = Integer.parseInt(text_mine.getText());
					levelChoosing(CUSTOM);
					w_Option.dispose();
					minePanel.gameStop();
					levelChoosing(level);
					minePanel.gameStart();
					gameRunning();

				} else {
					w_Option.dispose();
					minePanel.gameStop();
					levelChoosing(level);
					minePanel.gameStart();
					gameRunning();

				}

			}
			
		}
		
	}

	class ExitListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_exit) {
				counter.cancel();
				w_Result.dispose();
			}
			dispose();
			
		}
	}
	
	class LevelListener implements ActionListener, ItemListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_easy) {
				levelChoosing(EASY);
				minePanel.setW_Grasses(EASY_WIDTH);
				minePanel.setH_Grasses(EASY_HEIGHT);
				minePanel.setMines(EASY_MINES);
				minePanel.gameStart();
				w_Welcome.dispose();
				gameRunning();

			} else if (e.getSource() == btn_normal) {
				levelChoosing(NORMAL);
				minePanel.setW_Grasses(NORMAL_WIDTH);
				minePanel.setH_Grasses(NORMAL_HEIGHT);
				minePanel.setMines(NORMAL_MINES);
				minePanel.gameStart();
				w_Welcome.dispose();
				gameRunning();

			} else if (e.getSource() == btn_hard) {
				levelChoosing(HARD);
				minePanel.setW_Grasses(HARD_WIDTH);
				minePanel.setH_Grasses(HARD_HEIGHT);
				minePanel.setMines(HARD_MINES);
				minePanel.gameStart();
				w_Welcome.dispose();
				gameRunning();

			} else if (e.getSource() == btn_custom) {
				w_Welcome.dispose();
				custom();

			} 
			
		}
		
		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getItemSelectable() == cbx_custom) {
				text_width.setEditable(true);
				text_height.setEditable(true);
				text_mine.setEditable(true);
			} else if (e.getItemSelectable() == cbx_easy) {
				levelChoosing(EASY);
				text_width.setText(Integer.toString(EASY_WIDTH));
				text_height.setText(Integer.toString(EASY_HEIGHT));
				text_mine.setText(Integer.toString(EASY_MINES));
				text_width.setEditable(false);
				text_height.setEditable(false);
				text_mine.setEditable(false);
			} else if (e.getItemSelectable() == cbx_normal) {
				levelChoosing(NORMAL);
				text_width.setText(Integer.toString(NORMAL_WIDTH));
				text_height.setText(Integer.toString(NORMAL_HEIGHT));
				text_mine.setText(Integer.toString(NORMAL_MINES));
				text_width.setEditable(false);
				text_height.setEditable(false);
				text_mine.setEditable(false);
			} else if (e.getItemSelectable() == cbx_hard) {
				levelChoosing(HARD);
				text_width.setText(Integer.toString(HARD_WIDTH));
				text_height.setText(Integer.toString(HARD_HEIGHT));
				text_mine.setText(Integer.toString(HARD_MINES));
				text_width.setEditable(false);
				text_height.setEditable(false);
				text_mine.setEditable(false);
			}

		}
	}
	
	class SliderListener implements ChangeListener{
		@Override
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == slider_width) {
				text_width.setText(Integer.toString(slider_width.getValue()));
				slider_mine.setMaximum((slider_width.getValue() * slider_height.getValue())
						- (slider_width.getValue() + slider_height.getValue() - 1));

			} else if (e.getSource() == slider_height) {
				text_height.setText(Integer.toString(slider_height.getValue()));
				slider_mine.setMaximum((slider_width.getValue() * slider_height.getValue())
						- (slider_width.getValue() + slider_height.getValue() - 1));

			} else if (e.getSource() == slider_mine) {
				text_mine.setText(Integer.toString(slider_mine.getValue()));

			}

		}

	}
	
	class TextFieldListener implements KeyListener{
		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (e.getSource() == text_width) {
					minePanel.setNewW_Grasses(Integer.parseInt(text_width.getText()));
					text_width.setText(Integer.toString(minePanel.getNewW_Grasses()));
					minePanel.setMines(Integer.parseInt(text_mine.getText()));
					text_mine.setText(Integer.toString(minePanel.getMines()));

				} else if (e.getSource() == text_height) {
					minePanel.setNewH_Grasses(Integer.parseInt(text_height.getText()));
					text_height.setText(Integer.toString(minePanel.getNewH_Grasses()));
					minePanel.setMines(Integer.parseInt(text_mine.getText()));
					text_mine.setText(Integer.toString(minePanel.getMines()));

				} else if (e.getSource() == text_mine) {
					minePanel.setMines(Integer.parseInt(text_mine.getText()));
					text_mine.setText(Integer.toString(minePanel.getMines()));
				}
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
		
	}

	class FrameListener implements WindowListener{
		@Override
		public void windowActivated(WindowEvent e) {
			if( e.getWindow() == w_Help ){
				help.setEnabled(false);
				
			}else if( e.getWindow() == w_About ){
				about.setEnabled(false);
			}

		}

		@Override
		public void windowClosed(WindowEvent e) {

		}

		@Override
		public void windowClosing(WindowEvent e) {
			if (e.getWindow() == w_Welcome) {
				minePanel.setNewW_Grasses(EASY_WIDTH);
				minePanel.setNewH_Grasses(EASY_HEIGHT);
				minePanel.setMines(EASY_MINES);
				levelChoosing(EASY);
				minePanel.gameStart();
				gameRunning();

			} else if (e.getWindow() == w_Custom) {
				minePanel.setNewW_Grasses(EASY_WIDTH);
				minePanel.setNewH_Grasses(EASY_HEIGHT);
				minePanel.setMines(EASY_MINES);
				levelChoosing(EASY);
				minePanel.gameStart();
				gameRunning();

			} else if (e.getWindow() == w_Option) {

			} else if (e.getWindow() == w_Result) {
				minePanel.gameStop();
				levelChoosing(level);
				minePanel.gameStart();
				gameRunning();
			} else if( e.getWindow() == w_Help ){
				help.setEnabled( true );
			
			} else if( e.getWindow() == w_About ){
				help.setEnabled( true );
			}
		}

		@Override
		public void windowDeactivated(WindowEvent e) {
			
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			
		}

		@Override
		public void windowIconified(WindowEvent e) {
			
		}

		@Override
		public void windowOpened(WindowEvent e) {
			
		}
	}

}
