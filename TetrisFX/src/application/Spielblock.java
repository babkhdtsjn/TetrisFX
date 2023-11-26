package application;

public class Spielblock {
	static final public char BLOCK_CHAR = 'x';
	static final public char PLAYFIELD_BLOCK_CHAR = 'o';
	
	static final public int POSITION_LENGHT_X = 2;
	static final public int POSITION_LENGHT_Y = 4;
	
	
	private String blockName;
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
	
	private int [][] position;
	
	public Spielblock(String name) {
		switch (name) {
		case "square":
			this.blockName = "square";
			this.blockField = new char [2][2];
			this.blockField[0][0] = BLOCK_CHAR;
			this.blockField[0][1] = BLOCK_CHAR;
			this.blockField[1][0] = BLOCK_CHAR;
			this.blockField[1][1] = BLOCK_CHAR;
			break;
		case "line":
			this.blockName = "line";
			// 4 Zeilen, 1 Spalte
			this.blockField = new char [4][1];
			this.blockField[0][0] = BLOCK_CHAR;
			this.blockField[1][0] = BLOCK_CHAR;
			this.blockField[2][0] = BLOCK_CHAR;
			this.blockField[3][0] = BLOCK_CHAR;
			break;
		case "hook":
			this.blockName = "hook";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][0] = BLOCK_CHAR;
			this.blockField[0][1] = BLOCK_CHAR;
			this.blockField[1][0] = BLOCK_CHAR;
			this.blockField[2][0] = BLOCK_CHAR;
			break;
		case "mirror_hook":
			this.blockName = "mirror_hook";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][0] = BLOCK_CHAR;
			this.blockField[0][1] = BLOCK_CHAR;
			this.blockField[1][1] = BLOCK_CHAR;
			this.blockField[2][1] = BLOCK_CHAR;
			break;
		case "2v2":
			this.blockName = "2v2";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][0] = BLOCK_CHAR;
			this.blockField[1][0] = BLOCK_CHAR;
			this.blockField[1][1] = BLOCK_CHAR;
			this.blockField[2][1] = BLOCK_CHAR;
			break;
		case "mirror_2v2":
			this.blockName = "mirror_2v2";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][1] = BLOCK_CHAR;
			this.blockField[1][0] = BLOCK_CHAR;
			this.blockField[1][1] = BLOCK_CHAR;
			this.blockField[2][0] = BLOCK_CHAR;
			break;
		case "convex":
			this.blockName = "convex";
			// 3 Zeilen, 2 Spalten
			this.blockField = new char [3][2];
			this.blockField[0][0] = BLOCK_CHAR;
			this.blockField[1][0] = BLOCK_CHAR;
			this.blockField[1][1] = BLOCK_CHAR;
			this.blockField[2][0] = BLOCK_CHAR;
			break;
		default:
			System.out.println("FEHLER!");
			break;
		}
		// Festlegen der Dimensionen des Blockes
		blockDimX = this.blockField[0].length;
		blockDimY = this.blockField.length;
		
		// Initialisieren des Feldes für die Positionierung
		// 4 x-und y-Wert Paare
		this.position = new int [POSITION_LENGHT_Y][POSITION_LENGHT_X];
	}
	
	
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	public char[][] getBlockField(){
		return this.blockField;
	}
	public void printBlock() {
		for(int i = 0;i < this.blockField.length ; i++) {
			System.out.println(" ");
			for(int j = 0; j < this.blockField[i].length; j++) {
				if(blockField[i][j] == BLOCK_CHAR) {
					System.out.print(BLOCK_CHAR);
				}
				else {
					System.out.print(' ');
				}
			}
		}
	}
	
	public void setPosition(int row, int column, int value) {
		this.position[row][column] = value;
	}
	
	public int getPosition(int row, int column) {
		return this.position[row][column];
	}
	
	// Gibt ein Feld zurück, in dem der Index 0 die Anzahl der Zeilen und Index 1 die Anzahl der Spalten von position beschriebt.
	public int[] getPositionLength(){
		int[] len = new int [2];
		len[0] = this.position.length;
		len[1] = this.position[0].length;
		return len;
	}
	
	
	// Liefert den untersten Pixel des Blockes
	public int[] getLowestCurrentPosition() {
		return position[3];
	}
	
	public int getBlockDimX() {
		return this.blockDimX;
	}
	
	public int getBlockDimY() {
		return this.blockDimY;
	}
	
	public String getBlockName() {
		return this.blockName;
	}
	
	public int[][] getPosition() {
		return this.position;
	}
	
	//public void setBlockDim() {}
}

