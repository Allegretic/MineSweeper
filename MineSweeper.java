
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class MineSweeper extends JPanel implements MouseListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel grassPanel;

	// 草、地雷、旗子、開啟、空格重疊、數字
	private JButton[][] grasses;
	private boolean[][] isMine;
	private boolean[][] isFlag;
	private boolean[][] isOpen;
	private boolean[][] isCovered;
	private int[][] numbers;

	private Random rand;

	private int newW_Grasses = 9;
	private int newH_Grasses = 9;
	// 當 GameStop 前 w 和 h 可能被改動 ， 備份用
	private int w_Grasses = 0;
	private int h_Grasses = 0;

	private int mines = 0;

	private int flag = 0;

	private int result = 0;
	private final int LOSE = -1;
	private final int WAIT = 0;
	private final int WIN = 1;

	private final int BUTTONSIZE = 24;
	private Dimension buttonSize = new Dimension(BUTTONSIZE, BUTTONSIZE);

	// 滑鼠相關
	private boolean mouseflag;
	private int point_x;
	private int point_y;
	private int clickCounter;

	private int widthCounter;
	private int heightCounter;
	private boolean mineGenerator;

	private final int h[] = { -1, -1, -1, 0, 0, 1, 1, 1 };
	private final int v[] = { -1, 0, 1, -1, 1, -1, 0, 1 };

	public MineSweeper() {
		super();

		grassPanel = new JPanel();
	}

	public void gameStart() {
		remove(grassPanel);
		repaint();

		grassPanel.setLayout(new GridLayout(newH_Grasses, newW_Grasses, 0, 0));
		grassPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1, false));

		grasses = new JButton[newW_Grasses][newH_Grasses];
		isMine = new boolean[newW_Grasses][newH_Grasses];
		isFlag = new boolean[newW_Grasses][newH_Grasses];
		isOpen = new boolean[newW_Grasses][newH_Grasses];
		isCovered = new boolean[newW_Grasses][newH_Grasses];
		numbers = new int[newW_Grasses][newH_Grasses];

		for (int y = 0; y < newH_Grasses; y++) {
			for (int x = 0; x < newW_Grasses; x++) {
				grasses[x][y] = new JButton(" ");
				grasses[x][y].setFont(new Font("微軟正黑體", Font.BOLD, 16));
				grasses[x][y].setMargin(new Insets(1, 1, 1, 1));
				grasses[x][y].addMouseListener(this);
				grasses[x][y].setPreferredSize(buttonSize);
				grasses[x][y].setFocusable(false);
				grasses[x][y].setBackground(Color.lightGray);
				grassPanel.add(grasses[x][y]);
				grassPanel.setVisible(true);

			}
		}

		result = WAIT;
		add(grassPanel, BorderLayout.CENTER);

	}

	public void ganerateMine(int mine, int point_x, int point_y) {
		mineGenerator = true;
		rand = new Random();
		int x = 0, y = 0;
		for (int i = 0; i < mine; ++i) {
			x = rand.nextInt(w_Grasses);
			y = rand.nextInt(h_Grasses);

			if (x >= point_x - 1 && x <= point_x + 1 && y >= point_y - 1 && y <= point_y + 1) {
				if (x + 3 < w_Grasses) {
					x += 3;
				} else {
					x -= 3;
				}
				if (y + 3 < h_Grasses) {
					y += 3;
				} else {
					y -= 3;
				}
			}
			while (isMine[x][y] == true || (x == point_x && y == point_y)) {
				x = rand.nextInt(w_Grasses);
				y = rand.nextInt(h_Grasses);
				if (x >= point_x - 1 && x <= point_x + 1 && y >= point_y - 1 && y <= point_y + 1) {
					if (x + 3 < w_Grasses) {
						x += 3;
					} else {
						x -= 3;
					}
					if (y + 3 < h_Grasses) {
						y += 3;
					} else {
						y -= 3;
					}
				}
			}

			isMine[x][y] = true;

			for (int j = 0; j < 8; ++j) {
				int row = x + h[j];
				int col = y + v[j];
				if (isDirection(row, col)) {
					numbers[row][col]++;
				}
			}

		}

	}

	private void openGrass(int x, int y) {
		if (isFlag[x][y] == true) {
			isOpen[x][y] = false;
		} else {
			isOpen[x][y] = true;
		}
		if (isMine[x][y] == false && numbers[x][y] == 0) {
			int[][] needOpen = new int[w_Grasses * h_Grasses][2];
			int top = 0;

			// push
			needOpen[top][0] = x;
			needOpen[top][1] = y;
			top++;

			while (top > 0) {
				
				// pop
				top--;
				int topX =needOpen[top][0];
				int topY = needOpen[top][1];
				
				for (int i = 0; i < 8; ++i) {
					int row = topX + h[i];
					int col = topY + v[i];
					if (isDirection(row, col) && !isMine[row][col] && !isFlag[row][col]) {						
						if (numbers[row][col] == 0 && !isOpen[row][col]) {
							isOpen[row][col] = true;
							needOpen[top][0] = row;
							needOpen[top][1] = col;
							top++;
							
						} else if( numbers[row][col] != 0 && !isOpen[row][col]){
							 isOpen[row][col] = true;
							 
						}
							 
					}

				}
				
			}

		}

	}

	private void putFlag(int point_x, int point_y) {
		if (isOpen[point_x][point_y]) {

		} else {
			if (isFlag[point_x][point_y] == false) {
				isFlag[point_x][point_y] = true;
				flag++;
			} else if (isFlag[point_x][point_y] == true) {
				isFlag[point_x][point_y] = false;
				flag--;
			}
		}

	}

	public void scanMine(int x, int y) {
		int num_flag = 0;
		if (!isMine[x][y]) {

			for (int i = 0; i < 8; ++i) {
				int row = x + h[i];
				int col = y + v[i];
				if (isDirection(row, col) && isFlag[row][col]) {
					num_flag++;
				}
			}
			if (num_flag == numbers[x][y]) {

				for (int i = 0; i < 8; ++i) {
					int row = x + h[i];
					int col = y + v[i];
					if (isDirection(row, col)) {
						openGrass(row, col);
					}
				}
			}
		}
	}

	public void displayGrasses() {

		for (int y = 0; y < h_Grasses; y++) {
			for (int x = 0; x < w_Grasses; x++) {
				isCovered[x][y] = false;

				if (isOpen[x][y]) {

					grasses[x][y].setEnabled(false);

					if (isMine[x][y]) {
						grasses[x][y].setText("爆");
						grasses[x][y].setForeground(new Color(75, 0, 0));
						grasses[x][y].setBackground(Color.DARK_GRAY);

					} else {
						switch (numbers[x][y]) {
						case 0:
							grasses[x][y].setText(" ");
							grasses[x][y].setForeground(new Color(0, 0, 0));
							break;
						case 1:
							grasses[x][y].setText("1");
							grasses[x][y].setForeground(new Color(0, 187, 255));
							break;
						case 2:
							grasses[x][y].setText("2");
							grasses[x][y].setForeground(new Color(34, 149, 0));
							break;
						case 3:
							grasses[x][y].setText("3");
							grasses[x][y].setForeground(new Color(230, 63, 0));
							break;
						case 4:
							grasses[x][y].setText("4");
							grasses[x][y].setForeground(new Color(0, 0, 136));
							break;
						case 5:
							grasses[x][y].setText("5");
							grasses[x][y].setForeground(new Color(0, 170, 0));
							break;
						case 6:
							grasses[x][y].setText("6");
							grasses[x][y].setForeground(new Color(58, 0, 136));
							break;
						case 7:
							grasses[x][y].setText("7");
							grasses[x][y].setForeground(new Color(136, 102, 0));
							break;
						case 8:
							grasses[x][y].setText("8");
							grasses[x][y].setForeground(new Color(107, 69, 19));
							break;

						}

					}

				} else if (isFlag[x][y]) {
					grasses[x][y].setText("旗");
					grasses[x][y].setForeground(new Color(50, 155, 50));
					grasses[x][y].setBackground(new Color(50, 155, 50));
				} else if (!isFlag[x][y]) {
					grasses[x][y].setText("");
					grasses[x][y].setForeground(Color.lightGray);
					grasses[x][y].setBackground(Color.lightGray);

				}
			}
		}
		if (result == WAIT) {
			gameResult();
		}
	}

	private void gameResult() {
		int totalMines = 0;
		int totalOpens = 0;
		for (int y = 0; y < h_Grasses; ++y) {
			for (int x = 0; x < w_Grasses; ++x) {
				if (result == LOSE) {
					break;
				} else if (isMine[x][y]) {
					totalMines++;
					if (isOpen[x][y]) {
						setResult(LOSE);
						break;
					}
				} else if (isOpen[x][y]) {
					totalOpens++;

				}

			}
		}
		if (totalMines + totalOpens == h_Grasses * w_Grasses) {
			setResult(WIN);
		} else if (result != LOSE) {
			setResult(WAIT);
		}

	}

	public void gameStop() {
		for (int y = 0; y < h_Grasses; y++) {
			for (int x = 0; x < w_Grasses; x++) {
				grassPanel.remove(grasses[x][y]);
				numbers[x][y] = 0;
				isMine[x][y] = false;
				isFlag[x][y] = false;
				isOpen[x][y] = false;
			}
		}
		mines = 0;
		clickCounter = 0;
		widthCounter = 0;
		heightCounter = 0;
		mineGenerator = false;
		flag = 0;
		result = 0;

	}

	private boolean isDirection(int x, int y) {
		if (x >= 0 && x < w_Grasses && y >= 0 && y < h_Grasses) {
			return true;
		} else {
			return false;
		}
	}

	public Dimension getPanelSize() {
		return new Dimension((BUTTONSIZE + 3) * newW_Grasses + 100, (BUTTONSIZE + 3) * newH_Grasses + 75);

	}

	public int getResult() {
		return result;

	}

	public void setResult(int result) {
		if (result == LOSE) {
			this.result = LOSE;
			for (int y = 0; y < h_Grasses; ++y) {
				for (int x = 0; x < w_Grasses; ++x) {
					if (isMine[x][y]) {
						isOpen[x][y] = true;

					}
					grasses[x][y].setEnabled(false);
					displayGrasses();

				}
			}
		} else if (result == WIN) {
			this.result = WIN;
		} else {
			this.result = WAIT;
		}
	}

	public int getMineCounter() {
		int totalFlags = 0;
		int totalMines = 0;
		for (int y = 0; y < h_Grasses; ++y) {
			for (int x = 0; x < w_Grasses; ++x) {
				if (isFlag[x][y]) {
					totalFlags++;
				}
				if (isMine[x][y]) {
					totalMines++;
				}
			}
		}
		return totalMines - totalFlags;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getNewW_Grasses() {
		return newW_Grasses;

	}

	public int getNewH_Grasses() {
		return newH_Grasses;

	}

	public void setNewW_Grasses(int w_buttons) {
		widthCounter++;
		if (w_buttons <= 30 && w_buttons >= 9) {

			if (widthCounter == 1) {
				this.w_Grasses = this.newW_Grasses;
			}
			this.newW_Grasses = w_buttons;

		} else if (w_buttons < 9) {
			if (widthCounter == 1) {
				this.w_Grasses = this.newW_Grasses;
			}
			this.newW_Grasses = 9;

		} else if (w_buttons > 30) {
			if (widthCounter == 1) {
				this.w_Grasses = this.newW_Grasses;
			}
			this.newW_Grasses = 30;

		}

	}

	public void setNewH_Grasses(int h_buttons) {
		heightCounter++;
		if (h_buttons <= 24 && h_buttons >= 9) {
			if (heightCounter == 1) {
				this.h_Grasses = this.newH_Grasses;
			}
			this.newH_Grasses = h_buttons;

		} else if (h_buttons < 9) {
			if (heightCounter == 1) {
				this.h_Grasses = this.newH_Grasses;
			}
			this.newH_Grasses = 9;

		} else if (h_buttons > 24) {
			if (heightCounter == 1) {
				this.h_Grasses = this.newH_Grasses;
			}
			this.newH_Grasses = 24;

		}

	}

	public void setMines(int mines) {
		if (mines <= (newW_Grasses * newH_Grasses) - (newW_Grasses + newH_Grasses - 1) && mines >= 10) {
			this.mines = mines;
		} else if (mines < 10) {
			this.mines = 10;
		} else if (mines > (newW_Grasses * newH_Grasses) - (newW_Grasses + newH_Grasses - 1)) {
			this.mines = (newW_Grasses * newH_Grasses) - (newW_Grasses + newH_Grasses - 1);
		}
	}

	public int getMines() {
		return this.mines;
	}

	public int getW_Grasses() {
		return w_Grasses;
	}

	public void setW_Grasses(int wb_buttons) {
		this.w_Grasses = wb_buttons;
	}

	public int getH_Grasses() {
		return h_Grasses;
	}

	public void setH_Grasses(int hb_buttons) {
		this.h_Grasses = hb_buttons;
	}

	public boolean isMineGenerator() {
		return mineGenerator;
	}

	public void setMineGenerator(boolean mineGenerator) {
		this.mineGenerator = mineGenerator;
	}

	@Override
	public void mousePressed(MouseEvent event) {
		mouseflag = false;
		if (event.getModifiersEx() == (MouseEvent.BUTTON3_DOWN_MASK + MouseEvent.BUTTON1_DOWN_MASK)) {
			/*
			 * http://zhidao.baidu.com/question/1495573422835354059.html
			 * 意外找到判斷左右按鈕的方法 原先使用 if( event.getButton() == MouseEvent.BUTTON1 &&
			 * event.getButton() == MouseEvent.BUTTON3 )
			 * 這樣的話getButton不能同時傳回兩種值，而導致不成功。 但之後對左右同時按下會與單一左右按鈕的判斷混淆 於是找到了下列的方法
			 * http://blog.csdn.net/methods2011/article/details/12444003
			 * 之後在修改成放開之前都一定要碰到按鈕
			 */

			for (int y = 0; y < h_Grasses; y++) {
				for (int x = 0; x < w_Grasses; x++) {
					if (event.getSource() == grasses[x][y]) {
						point_x = x;
						point_y = y;
						mouseflag = true;
					}
				}
			}

		} else {
			for (int y = 0; y < h_Grasses; y++) {
				for (int x = 0; x < w_Grasses; x++) {
					if (event.getSource() == grasses[x][y]) {
						point_x = x;
						point_y = y;
						mouseflag = false;
					}
				}
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent event) {
		int fpoint_x = event.getX();
		int fpoint_y = event.getY();

		if (mouseflag == true && mineGenerator) {
			for (int y = 0; y < h_Grasses; y++) {
				for (int x = 0; x < w_Grasses; x++) {
					if (event.getSource() == grasses[x][y]) {
						fpoint_x = grasses[x][y].getX() + event.getX();
						fpoint_y = grasses[x][y].getY() + event.getY();
						mouseflag = true;

					}
				}
			}

			if (event.getSource() == grasses[point_x][point_y]) {
				if (fpoint_x <= grasses[point_x][point_y].getX() + grasses[point_x][point_y].getWidth()
						&& fpoint_x >= grasses[point_x][point_y].getX() && fpoint_y >= grasses[point_x][point_y].getY()
						&& fpoint_y <= grasses[point_x][point_y].getY() + grasses[point_x][point_y].getHeight()) {
					scanMine(point_x, point_y);
					displayGrasses();
				} else {
					mouseflag = false;
				}
			}

		} else if (event.getButton() == MouseEvent.BUTTON1 && mouseflag == false) {

			for (int y = 0; y < h_Grasses; y++) {
				for (int x = 0; x < w_Grasses; x++) {
					if (event.getSource() == grasses[x][y]) {
						fpoint_x = grasses[x][y].getX() + event.getX();
						fpoint_y = grasses[x][y].getY() + event.getY();
					}
				}
			}

			if (event.getSource() == grasses[point_x][point_y]) {
				if (fpoint_x <= grasses[point_x][point_y].getX() + grasses[point_x][point_y].getWidth()
						&& fpoint_x >= grasses[point_x][point_y].getX() && fpoint_y >= grasses[point_x][point_y].getY()
						&& fpoint_y <= grasses[point_x][point_y].getY() + grasses[point_x][point_y].getHeight()) {

					clickCounter++;
					if (clickCounter == 1) {
						ganerateMine(mines, point_x, point_y);
					}

					openGrass(point_x, point_y);

					displayGrasses();
				} else {
					mouseflag = false;
				}
			}

		} else if (event.getButton() == MouseEvent.BUTTON3 && mouseflag == false && mineGenerator) {
			for (int y = 0; y < h_Grasses; y++) {
				for (int x = 0; x < w_Grasses; x++) {
					if (event.getSource() == grasses[x][y]) {
						fpoint_x = grasses[x][y].getX() + event.getX();
						fpoint_y = grasses[x][y].getY() + event.getY();

					}
				}
			}

			if (event.getSource() == grasses[point_x][point_y]) {
				if (fpoint_x <= grasses[point_x][point_y].getX() + grasses[point_x][point_y].getWidth()
						&& fpoint_x >= grasses[point_x][point_y].getX() && fpoint_y >= grasses[point_x][point_y].getY()
						&& fpoint_y <= grasses[point_x][point_y].getY() + grasses[point_x][point_y].getHeight()) {
					putFlag(point_x, point_y);
					displayGrasses();
				} else {
					mouseflag = false;
				}
			}

		}
	}

	@Override
	public void mouseClicked(MouseEvent event) {

	}

	@Override
	public void mouseEntered(MouseEvent event) {

	}

	@Override
	public void mouseExited(MouseEvent event) {

	}

	public int getTotalOpen() {
		int totalOpens = 0;
		for (int y = 0; y < h_Grasses; ++y) {
			for (int x = 0; x < w_Grasses; ++x) {
				if (isOpen[x][y] && !isMine[x][y]) {
					totalOpens++;
				}
			}
		}
		return totalOpens;
	}

	public double getPercentOpen() {
		int total = 0;
		for (int y = 0; y < h_Grasses; ++y) {
			for (int x = 0; x < w_Grasses; ++x) {

				if (isOpen[x][y] && !isMine[x][y] && !isFlag[x][y]) {
					total++;
				}
				if (!isOpen[x][y] && isMine[x][y]) {
					total++;
				}
			}
		}
		return total * 100 / (w_Grasses * h_Grasses);
	}

}
