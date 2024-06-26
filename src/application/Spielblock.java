package application;

public class Spielblock {
	//static final public char BLOCK_CHAR = 'x';
	//static final public char PLAYFIELD_BLOCK_CHAR = 'o';
	
	static final public int POSITION_LENGHT_X = 2;
	static final public int POSITION_LENGHT_Y = 4;
	
	public static final String[] BLOCK_COLORS = {"Red", "Blue", "Yellow", "Purple", "Green", "Lightblue"};
	// Gibt Information darüber mit welchem Zeichen, welche Farbe der gesetzten Blöcke festgehalten wird.
	// Red = r; Blue = b, Yellow = y, Purple = p, Green = g, Lightblue = l
	
	static final public char[] BLOCK_CHAR_COLOR = new char[] {'r','b','y','p','g','l'};
	static final public char[] SET_BLOCK_CHAR_COLOR = new char[] {'1','2','3','4','5','6'};
	
	private String blockName;
	// Farben: Rot,Blau, Gelb, Lila, Orange, Grün, Hellblau
	private String color;
	private char blockChar;
	/*
	 * blockField enthält die konkrete Information wie ein Block in einem array liegt.
	 * Es gibt 
	 * square, line, hook, 2v2, mirrorHook und mirror2v2
	 * 
	 * xx     
	 * xx	
	 * 
	 * x
	 * x
	 * x
	 * x
	 * 
	 * xx  xx
	 * x    x
	 * x	x
	 * 
	 * x    x
	 * xx  xx
	 *  x  x
	 *  
	 *  x
	 *  xx
	 *  x
	 */
	// blockField passt sich den Dimensionen des zu erstellenen Blockes an.
	private char[][] blockField;
	private int blockDimX;
	private int blockDimY;
	
	private boolean turnPlus;
	
	// Position [i][0] = Y Koordinate von Pixel i
	// Position [i][1] = X Koordinate von Pixel i
	private int [][] position;
	
	public Spielblock(String name) {
		// Festlegen der Farbe des Blocks
		setRandomColor();
		switch (name) {
		case "square":
			this.blockName = "square";
			this.blockField = new char [2][2];
			this.blockField[0][0] = this.blockChar;
			this.blockField[0][1] = this.blockChar;
			this.blockField[1][0] = this.blockChar;
			this.blockField[1][1] = this.blockChar;
			break;
		case "line":
			this.blockName = "line";
			// 4 Zeilen, 1 Spalte
			this.blockField = new char [4][1];
			this.blockField[0][0] = this.blockChar;
			this.blockField[1][0] = this.blockChar;
			this.blockField[2][0] = this.blockChar;
			this.blockField[3][0] = this.blockChar;
			break;
		case "hook":
			this.blockName = "hook";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][0] = this.blockChar;
			this.blockField[0][1] = this.blockChar;
			this.blockField[1][0] = this.blockChar;
			this.blockField[2][0] = this.blockChar;
			break;
		case "mirror_hook":
			this.blockName = "mirror_hook";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][0] = this.blockChar;
			this.blockField[0][1] = this.blockChar;
			this.blockField[1][1] = this.blockChar;
			this.blockField[2][1] = this.blockChar;
			break;
		case "2v2":
			this.blockName = "2v2";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][0] = this.blockChar;
			this.blockField[1][0] = this.blockChar;
			this.blockField[1][1] = this.blockChar;
			this.blockField[2][1] = this.blockChar;
			break;
		case "mirror_2v2":
			this.blockName = "mirror_2v2";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][1] = this.blockChar;
			this.blockField[1][0] = this.blockChar;
			this.blockField[1][1] = this.blockChar;
			this.blockField[2][0] = this.blockChar;
			break;
		case "convex":
			this.blockName = "convex";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][0] = this.blockChar;
			this.blockField[1][0] = this.blockChar;
			this.blockField[1][1] = this.blockChar;
			this.blockField[2][0] = this.blockChar;
			break;
		default:
			System.out.println("FEHLER!");
			break;
		}
		// Festlegen der Dimensionen des Blockes
		blockDimX = this.blockField[0].length;
		blockDimY = this.blockField.length;
		
		turnPlus = true;
		
		// Initialisieren des Feldes für die Positionierung
		// 4 x-und y-Wert Paare
		this.position = new int [POSITION_LENGHT_Y][POSITION_LENGHT_X];
		
		
	}
	
	
	public synchronized void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	public synchronized char[][] getBlockField(){
		return this.blockField;
	}
	public synchronized void printBlock() {
		for(int i = 0;i < this.blockField.length ; i++) {
			System.out.println(" ");
			for(int j = 0; j < this.blockField[i].length; j++) {
				/*if(blockField[i][j] == BLOCK_CHAR) {
					System.out.print(BLOCK_CHAR);
				}
				else {
					System.out.print(' ');
				}
				*/
				System.out.print(blockField[i][j]);
			}
		}
	}
	
	public synchronized String printBlockAsString() {
		String blockAsString = "";
		for(int i = 0;i < this.blockField.length ; i++) {
			System.out.println(" ");
			for(int j = 0; j < this.blockField[i].length; j++) {
				/*
				if(blockField[i][j] == BLOCK_CHAR) {
					blockAsString = blockAsString + BLOCK_CHAR;
				}
				else {*/
					blockAsString = blockAsString + blockField[i][j];
				//}
			}
			if((i + 1) == blockField.length) {
				blockAsString = blockAsString + "";
			}
			else {
				blockAsString = blockAsString + " \n";
			}
		}
		return blockAsString;
	}
	
	public synchronized void setPosition(int row, int column, int value) {
		this.position[row][column] = value;
	}
	
	public synchronized int getPosition(int row, int column) {
		return this.position[row][column];
	}
	
	// Gibt ein Feld zurück, in dem der Index 0 die Anzahl der Zeilen und Index 1 die Anzahl der Spalten von position beschriebt.
	public synchronized int[] getPositionLength(){
		int[] len = new int [2];
		len[0] = this.position.length;
		len[1] = this.position[0].length;
		return len;
	}
	
	
	// Liefert den untersten Pixel des Blockes
	public synchronized int[] getLowestCurrentPosition() {
		return position[3];
	}
	
	public synchronized int getBlockDimX() {
		return this.blockDimX;
	}
	
	public synchronized int getBlockDimY() {
		return this.blockDimY;
	}
	
	public synchronized String getBlockName() {
		return this.blockName;
	}
	
	public synchronized int[][] getPosition() {
		return this.position;
	}
	public synchronized int getPositionAt(int row, int column) {
		return this.position[row][column];
	}
	
	public synchronized boolean getTurnPlus() {
		return this.turnPlus;
	}
	
	public synchronized void setTurnPlus(boolean newTurn) {
		this.turnPlus = newTurn;
	}
	
	public synchronized void setColor(String newColor) {
		this.color = newColor;
	}
	
	public synchronized String getColor() {
		return this.color;
	}
	
	public synchronized void setBlockChar(char blockChar) {
		this.blockChar = blockChar;
	}
	
	public synchronized char getBlockChar() {
		return this.blockChar;
	}
	
	public synchronized void setRandomColor() {
		int randomNumber = (int)((Math.random()) * Spielblock.BLOCK_COLORS.length);
		setColor(Spielblock.BLOCK_COLORS[randomNumber]);
		setBlockChar(Spielblock.BLOCK_CHAR_COLOR[randomNumber]);
	}
	
	public synchronized char setNewColorCharForSetBlock(char currentChar) {
		//Arrays.asList(Spielblock.BLOCK_CHAR_COLOR).indexOf(currentChar);
		//int index = Arrays.asList(Spielblock.BLOCK_CHAR_COLOR).indexOf(currentChar);
		
		int pos = -1;
		for(int i = 0; i < Spielblock.BLOCK_CHAR_COLOR.length; i++) {
		  if(Spielblock.BLOCK_CHAR_COLOR[i] == currentChar) {
		     pos = i;
		     break;
		  }
		}
		return Spielblock.SET_BLOCK_CHAR_COLOR[pos];
	}
	
	
	//public void setBlockDim() {}
}


