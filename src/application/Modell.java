package application;

import java.util.*;

public class Modell{
	static final int PLYFLD_X = 10;	// Breite des Spielfeldes.
	static final int PLYFLD_BORDER_X = 2; // 2 da eine Linie | an beide Seiten des Feldes gezogen wird.
	
	static final int PLYFLD_Y = 18;	// Länge des Spielfeldes.
	static final int PLYFLD_BORDER_Y = 1; // 1, da nur eine Linie - unten gezogen wird.
	static final int BUFFER_Y = 4; // Größe der Bufferzone nach oben hin, in dem die Blöcke spawnen.
	
	static final char PLYFLD_SIDE_BORDER_CHAR = '|'; // Zeichen das für die seitlichen Grenzen genutzt wird.
	static final char PLYFLD_BOTTOM_BORDER_CHAR = '-'; // Zeichen das für die untere Grenze genutzt wird.
	static final char PLYFLD_FREE_CHAR = ' '; // Zeichen das für ein freies Feld genutzt wird.
	
	// Variablen für die Graphische Ausgabe auf der GUI
	static final char RNDR_SIDE_BORDER_CHAR = PLYFLD_SIDE_BORDER_CHAR;
	static final char RNDR_BOTTOM_BORDER_CHAR = PLYFLD_BOTTOM_BORDER_CHAR;
	static final char RNDR_FREE_CHAR = ' ';
	
	// Größe des Spielfeldes + Platz für die Berandung.
	private char[][] playfield;
	private int ply_len_x;
	private int ply_len_y;
	
	// Block der gerade gesteuert wird und nach unten fällt.
	private volatile Spielblock currentBlock = null;
	private Spielblock nextBlock = null;
	
	// Punktzahl des Spielers
	int score = 0;
	static final  int SCORE_BONUS_CLEARED_ROW = 10;
	
	// ms die ein Block braucht um zu fallen.
	static final int FALL_TIME_NORMAL = 1000;		
	static final int FALL_TIME_SPEEDUP = 150;
	static final int FALL_TIME_MIN = 150;
	static int fallTimeUpdated = FALL_TIME_NORMAL;
	
	static int fallTime = fallTimeUpdated;
	static int fallTimeSpeedup = FALL_TIME_SPEEDUP;
	
	// Nach dieser Anzahl an gefallenen Blöcken, wird die Fallgeschwindigkeit erhöht.
	static final int SPEED_UP_BLOCK_COUNT_THRESHHOLD = 10;		
	static final int FALL_TIME_REDUCTION = 70;					
	
	private int blockCount = 0;
	
	

	// Position an der der Block in die Bufferzone gesetzt wird, wenn er spawnt.
	// Variabel, da der Startplatz von der Dimension des Blockes abhängig ist.
	int blockstart_x = 0; 
	int blockstart_y = 0;
	
	// Controller Klasse
	private Steuerung controll;
	
	static void wait(int ms)
	{
	    try
	    {
	        Thread.sleep(ms);
	    }
	    catch(InterruptedException ex)
	    {
	        Thread.currentThread().interrupt();
	    }
	}
	
	
	public Modell() {
		// PLYFLD_L beschreibt die Länge nach Unten; +1 für die Grenze nach unten hin
		// + BUFFER_L, in den die Spielblöcke als erstes gerendert werden.
		// PLYFLD_W beschreibt die Breite nach Rechts; + 2 für die Randbegrenzung.
		playfield = new char[PLYFLD_Y + BUFFER_Y + PLYFLD_BORDER_Y][PLYFLD_X  + PLYFLD_BORDER_X];
		// Längenangaben für Vergleiche, getrimmt für den korrekten Vergleich
		ply_len_x = playfield.length;
		ply_len_x -= 1;
		
		ply_len_y = playfield[0].length;
		ply_len_y -= 1;
	}
	
	public void createPlayfieldBorder() {
		for(int i = 0; i < playfield.length; i++){
			for(int j = 0; j < playfield[0].length; j++){
				if(j == 0 || j == ply_len_y){
					playfield[i][j] = PLYFLD_SIDE_BORDER_CHAR;
				}
				else if(i == ply_len_x){
					playfield[i][j] = PLYFLD_BOTTOM_BORDER_CHAR;
				}
				else{
					playfield[i][j] = PLYFLD_FREE_CHAR;
				}
			}
		}
	}
	
	// Zuständig dafür wie das Spielfeld im Fenster gerendert wird.
	public String playfieldAsString() {
		String playfieldString = "";
		for(int i = 0; i < playfield.length-1; i++){
			for(int j = 0; j < playfield[0].length;j++) {
				// Wenn es ein leeres Feld ist, mach daraus einen größeren Abstand.
				if(playfield[i][j] == PLYFLD_FREE_CHAR) {
					playfieldString = playfieldString + RNDR_FREE_CHAR;
				}
				else if(playfield[i][j] == PLYFLD_SIDE_BORDER_CHAR) {
					playfieldString = playfieldString + RNDR_SIDE_BORDER_CHAR;
				}
				else if(playfield[i][j] == PLYFLD_BOTTOM_BORDER_CHAR) {
					playfieldString = playfieldString + RNDR_BOTTOM_BORDER_CHAR;
				}
				else {playfieldString = playfieldString + playfield[i][j];}
			}	
			if((i + 1) == playfield.length) {
				playfieldString = playfieldString + "";
			}
			else {
			playfieldString = playfieldString + " \n";
			}
		}
		return playfieldString;
	}
	
	// Debug Funktion, die innerhalb der Konsole das Spielfeld ausgibt.
	public void printPlayfield() {
		for(int i = 0; i < this.getPlayfieldRowLenght(); i++){
			// Nächste Zeile
			System.out.println(" ");
			for(int j = 0; j < this.getPlayfieldColumnLenght(); j++){
				System.out.print(this.getPlayfieldAt(i, j));
			}
		}
		System.out.println("Spielfeld mit Buffer gerendert!");
	}
	/*
	public void printPlayfieldWithoutBuffer() {
		for(int i = 0; i < playfield.length; i++){
			// Überspring die Bufferzeilen
			if(i < BUFFER_Y) {
				continue;
			}
			// Nächste Zeile
			System.out.println(" ");
			for(int j = 0; j < playfield[0].length; j++){
				if(j == 0 || j == ply_len_y){
					System.out.print(playfield[i][j]);
				}
				else if(i == ply_len_x){
					System.out.print(playfield[i][j]);
				}
				else{
					System.out.print(playfield[i][j]);
				}
			}
		}
		
	}
	*/
	
	public Spielblock createPlayblock(String playblockName) {
		Spielblock block = new Spielblock(playblockName);
		return block;
	}
	
	public void debugPrintBlock(Spielblock block) {
		block.printBlock();
	}

	public Spielblock createRandomBlock() {
		Spielblock playblock;
		int randomNumber = (int)((Math.random()) * 5 + 1);
		switch(randomNumber) {
			case 1 :
				playblock = new Spielblock("square");
				break;
			case 2:
				playblock = new Spielblock("line");
				break;
			case 3:
				playblock = new Spielblock("convex");
				break;
			case 4:
				randomNumber = (int)((Math.random()) * 2 + 1);
				if(randomNumber == 1) {
					playblock = new Spielblock("hook");
				}
				else if (randomNumber == 2) {
					playblock = new Spielblock("mirror_hook");
				}
				else {
					playblock = null;
				}
				break;
			case 5:
				randomNumber = (int)((Math.random()) * 2 + 1);
				if(randomNumber == 1) {
					playblock = new Spielblock("2v2");
				}
				else if (randomNumber == 2) {
					playblock = new Spielblock("mirror_2v2");
				}
				else {
					playblock = null;
				}
				break;
			default:
				playblock = null;
				System.out.println("Fehler bei Generation eines Zuffalsblocks");
		}
		
		return playblock;
	}

	// Legt den erstellten Block aus currentBlock in das Spielfeld ab.
	public void putBlockInBufferzone() {
		int currentBlockDimX = this.currentBlock.getBlockDimX(); //blockField[0].length;
		int currentBlockDimY = this.currentBlock.getBlockDimY(); //blockField.length;
		char[][] currentBlockField = this.currentBlock.getBlockField();
		
		// Bestimme den Startpunkt anhand der Blockdimension von currentBlock.
		this.blockstart_x = (PLYFLD_X + PLYFLD_BORDER_X)/2 - 1; // Hälfte der Breite des Feldes
		this.blockstart_y = (BUFFER_Y - currentBlockDimY); // Länge des Buffers - Länge von currentBlock; da der Buffer dem ersten Teil des ganzen Feldes entspricht, reicht es damit zu rechnen, anstatt mit dem ganzen Feld.
		
		// Zählervariable zum Tracking der Position von currentBlock. 
		// Zählt bis 3 hoch, um alle 4 Positionen des Blockes im Feld currentBlock.position festzuhalten.
		int positionCounter = 0;
		char blockColor = this.currentBlock.getBlockChar();
		
		// Schreibe den Block seiner Form nach in das Spielfeld
		for(int i = 0; i < currentBlockDimY; i++) {
			for(int j = 0; j < currentBlockDimX; j++) {
				// Wenn die momentane Position ein Teil des Blockes enthält, soll dieser auf das Spielfeld übertragen werden.
				if(currentBlockField[i][j] == blockColor) {
					// Die Position im Spielfeld muss um den versatz blockstart angepasst werden, da das Feld currentBlock eine andere Größe hat.
					this.playfield[i+this.blockstart_y][j+this.blockstart_x] = blockColor;
					this.currentBlock.setPosition(positionCounter, 0, (i+this.blockstart_y));
					this.currentBlock.setPosition(positionCounter, 1, (j+this.blockstart_x));
					positionCounter++;
				}
			}
		}	
		
	}
	
	//Setzt alle Positionen von currentBlock eine Zeile weiter nach unten.
	public void currentBlockFall() {
		int newPosition;
		// Entferne den currentBlock temporär aus dem Spielfeld.
		// Durchlaufe das Spielfeld
		for(int i = 0; i < playfield.length; i++){
			for(int j = 0; j < playfield[0].length; j++){
				if(playfield[i][j] == this.currentBlock.getBlockChar()) {
					playfield[i][j] = PLYFLD_FREE_CHAR;
				}
			}
		}
		// Setze die neue Position von currentBlock.
		for(int i = 0; i < 4; i++) {
			// Update Position im Spielfeld.
			newPosition = this.currentBlock.getPosition(i, 0) + 1;
			this.currentBlock.setPosition(i, 0, newPosition);
			// Update das Spielfeld mit der aktuellen Position.
			playfield[this.currentBlock.getPosition(i,0)][this.currentBlock.getPosition(i,1)] = this.currentBlock.getBlockChar();
		}
	}

	// Überprüft ob der currentBlock am Boden oder an anderen Blöcken anstoßen würde.
	// Wenn das der Fall ist wird true ausgegeben, ansosnten false.
	public boolean checkForObstacle() {
		boolean obstacleDetected = false;
		// Enthält die x und y Koordinate des niedrigsten Punktes von CurrentBlock
		int[] lowestPosition = this.currentBlock.getLowestCurrentPosition();
		int nextPositionY = lowestPosition[1];
		// Wenn currentBlock != square ist:
		if(this.currentBlock.getBlockName() == "line") {
			if(this.playfield[lowestPosition[0]+1][nextPositionY] == PLYFLD_BOTTOM_BORDER_CHAR) {
				// Nächster Feld auf das der currentBlock fällt ist die untere Grenze, er kann nicht weiter fallen.
				obstacleDetected = true;
			}
			// Wenn der Nächste Block eine Zahl als Farbkodierung hat, ist dieser ein gesetzter Block und damit ein Hinderniss.
			else if(contains(Spielblock.SET_BLOCK_CHAR_COLOR, this.playfield[lowestPosition[0]+1][nextPositionY])) {
				// Nächstes Feld auf das der currentBlock fällt ist ein anderer Block, er kann nicht weiter fallen.
				obstacleDetected = true;
			}
			else {
				obstacleDetected = false;
			}
		}
		// Wenn currentBlock == square ist, wird eine breitere Detektion gebraucht.
		// Die lowestPosition ist beim square Block der rechte untere Pixel.
		else if(this.currentBlock.getBlockName() != "line") {
			if(this.playfield[lowestPosition[0]+1][nextPositionY] == PLYFLD_BOTTOM_BORDER_CHAR ||
					this.playfield[lowestPosition[0]+1][nextPositionY - 1] == PLYFLD_BOTTOM_BORDER_CHAR
					) {
				// Nächster Feld auf das der currentBlock fällt ist die untere Grenze, er kann nicht weiter fallen.
				obstacleDetected = true;
			}
			else if(contains(Spielblock.SET_BLOCK_CHAR_COLOR, this.playfield[lowestPosition[0]+1][nextPositionY]) ||
					contains(Spielblock.SET_BLOCK_CHAR_COLOR, this.playfield[lowestPosition[0]+1][nextPositionY - 1])) {
				// Nächstes Feld auf das der currentBlock fällt ist ein anderer Block, er kann nicht weiter fallen.
				obstacleDetected = true;
			}
			else {
				obstacleDetected = false;
			}
		}
		return obstacleDetected;
	}
	
	public synchronized int[][] calculateNewPosition(String direction){
		int[][] newPosition = new int[4][2];
		
		if(direction == "RIGHT") {
			for(int i = 0; i < newPosition.length; i++) {
				newPosition[i][0] = this.currentBlock.getPositionAt(i, 0); //Y
				newPosition[i][1] = this.currentBlock.getPositionAt(i, 1) + 1; //X
			}
		}
		else if(direction == "LEFT") {
			for(int i = 0; i < newPosition.length; i++) {
				newPosition[i][0] = this.currentBlock.getPositionAt(i, 0); //Y
				newPosition[i][1] = this.currentBlock.getPositionAt(i, 1) - 1; //X
			}
		}
		else if(direction == "DOWN") {
			for(int i = 0; i < newPosition.length; i++) {
				newPosition[i][0] = this.currentBlock.getPositionAt(i, 0); //Y
				newPosition[i][0] = (newPosition[i][0]) + 1;
				newPosition[i][1] = this.currentBlock.getPositionAt(i, 1); //X
			}			
		}		
		//System.out.println("calculateNewPosition(): Direction: "+ direction+ ", Alte Position: "+ this.currentBlock + ", Neue Position: "+ newPosition);
		//System.out.println("Alte Positionen: ");
		
		for(int i = 0; i < newPosition.length; i++) {
			System.out.println("X-Koordinate: " + this.currentBlock.getPositionAt(i, 1) + " Y-Koordinate: "+ this.currentBlock.getPositionAt(i, 0));
		}
		//System.out.println("Neue Positionen: ");
		for(int i = 0; i < newPosition.length; i++) {
			System.out.println("X-Koordinate: " + newPosition[i][1] + "Y-Koordinate: "+ newPosition[i][0]);
		}
		return newPosition;
	}
	
	//Überprüft die newPosition die 4 Zeilen und 2 Spalten hat, bevor eine vom Spieler angeforderte Bewegung ausgeführt wird.
	public boolean checkNewPosition(int [][] newPosition) {
		boolean notObstructed = true;
		
		//Durchlaufe jede Position und überprüfe ob diese im Spielfeld frei ist.
		for(int i_row = 0; i_row < 4; i_row++) {
			int[] currentlyCheckingPosition = new int[2];
			currentlyCheckingPosition[0] = newPosition[i_row][0];//y-Koordinate eines Pixels der neuen Position / Reihe.
			currentlyCheckingPosition[1] = newPosition[i_row][1];//x-Koordinate eines Pixels der neuen Position / Spalte.
			try {
				if(this.playfield[currentlyCheckingPosition[0]][currentlyCheckingPosition[1]] == PLYFLD_SIDE_BORDER_CHAR || 
						this.playfield[currentlyCheckingPosition[0]][currentlyCheckingPosition[1]] == PLYFLD_BOTTOM_BORDER_CHAR) {
					// Block stößt an seitlichen bzw unteren Rand an.
					notObstructed = false;
				}
				else if (contains(Spielblock.SET_BLOCK_CHAR_COLOR, this.playfield[currentlyCheckingPosition[0]][currentlyCheckingPosition[1]])) {
					// Block stoßt an bereits gesetzten Block an.
					notObstructed = false;
				}
			}
			catch (Exception e) {
				System.out.println("Fehler in checkNewPosiition!");
            }
		}
		return notObstructed;
	}
	
	
	
	// Überträgt die Positionen vom derzeitigen currentBlock in das Spielfeld als .
	public void insertCurrentBlockAsPlayfieldBlock() {
		for(int i =0; i < Spielblock.POSITION_LENGHT_Y;i++) {
			this.setPlayfieldAt(this.currentBlock.getPosition(i, 0), this.currentBlock.getPosition(i, 1), this.currentBlock.setNewColorCharForSetBlock(this.currentBlock.getBlockChar()));
		}
	}
	
	// Überprüft ob ein currentBlock nach Setzen im Buffer ist, dann ist das Spiel vorbei.
	public boolean checkGameEnd() {
		boolean gameEnd = false;
		for(int i = 0; i < Modell.BUFFER_Y; i ++) {
			for(int j = 0; j < Modell.PLYFLD_X; j++){
				if(contains(Spielblock.SET_BLOCK_CHAR_COLOR, this.playfield[i][j])) {
					gameEnd = true;
					break;
				}
			}	
		}
		return gameEnd;
	}
	
	public void resetGame() {
		// Entferne alle Blöcke aus dem Spielfeld.
		playfield = new char[PLYFLD_Y + BUFFER_Y + PLYFLD_BORDER_Y][PLYFLD_X  + PLYFLD_BORDER_X];
		createPlayfieldBorder();
		// Setze den Score auf 0.
		this.score = 0;
		//Setze die Geschwindigkeit auf normal zurück.
		this.setFallTimeUpdated(FALL_TIME_NORMAL);
		this.setBlockCount(0);
		// Update die GUI.
		// Resette die Flag für den Spiellogik-Thread
		
		this.controll.logicThreadRun = true;
		System.out.println("LogicThreadRun =  " + this.controll.logicThreadRun);
		// TODO: Starte das Spiel neu.
		this.controll.restartLogicThread();
		
	}
	
	// Funktion zum Bewegen des currentBlocks nach links und rechts.
	public void moveCurrentBlock(String direction) {
		int[][] newPosition = new int[4][2];
		
		if(direction == "RIGHT") {
			// Setze die neue Position; für eine Bewegung nach Rechts wird die Spalte um eins erhöht.
			for(int i_row = 0; i_row < 4; i_row++) {
				newPosition[i_row][0] = this.currentBlock.getPosition(i_row, 0);		//y-Koordinate
				newPosition[i_row][1] = this.currentBlock.getPosition(i_row, 1) + 1;	//x-Koordinate
			}		
		}
		else if(direction == "LEFT") {
			for(int i_row = 0; i_row < 4; i_row++) {
				newPosition[i_row][0] = this.currentBlock.getPosition(i_row, 0);		//y-Koordinate
				newPosition[i_row][1] = this.currentBlock.getPosition(i_row, 1) - 1;	//x-Koordinate
			}	
		}
		else if(direction == "DOWN") {
			for(int i_row = 0; i_row < 4; i_row++) {
				newPosition[i_row][0] = this.currentBlock.getPosition(i_row, 0) + 1;		//y-Koordinate
				newPosition[i_row][1] = this.currentBlock.getPosition(i_row, 1);	//x-Koordinate
			}
		}
		
		else {
			System.out.println("Irgendwas ist schief gelaufen.");
			// Fehler!
		}
		// Überprüfe, ob der currentBlock auch wirklich auf die neue Position gerückt werden darf.
		// Wenn checkNewPosition true ist, befindet sich auf den neuen Pixelpositionen kein anderes Objekt.
		if(checkNewPosition(newPosition) == true) {
			for(int i_row = 0; i_row < 4; i_row++) {
				// Lösche die alten Werte.
				this.setPlayfieldCharacterAtPosition(this.currentBlock.getPosition(i_row, 0), this.currentBlock.getPosition(i_row, 1), PLYFLD_FREE_CHAR);
			}
			// Ändere die currentPosition vom currentBlock.
			for(int i_row = 0; i_row < 4; i_row++) {
				// Ändere die Werte vom currentBlock.
				this.currentBlock.setPosition(i_row, 0, newPosition[i_row][0]); // Neuer y-Koordinaten Wert.
				this.currentBlock.setPosition(i_row, 1, newPosition[i_row][1]); // Neuer x-Koordinaten Wert.
				
				// Übertrage die neuen Werte auf das Spielfeld
				
				playfield[this.currentBlock.getPosition(i_row, 0)][this.currentBlock.getPosition(i_row, 1)] = this.currentBlock.getBlockChar();//Spielblock.BLOCK_CHAR;
			}
		}
		
		
		
		
		
	}
	
	// Funktion zum Rotieren des currenBlocks (im Uhrzeigersinn)
	// Alle Blöcke rotieren sich um ihren zweiten Pixel.
	public void rotateCurrentBlock() {
		if(this.currentBlock.getBlockName() != "square") {
		 int rotatingPixel = 2;
		 
		int[][] oldPosition = new int[4][2];
		
		
		for(int i = 0; i < oldPosition.length; i++) {
			oldPosition[i][0] = this.currentBlock.getPositionAt(i, 0) ; 
			oldPosition[i][1] = this.currentBlock.getPositionAt(i, 1) ; 
		}
		
		System.out.println("-------- Alte Position des zu rotierenden Blockes:");
		System.out.println("-- Pixel 1: x: "+ oldPosition[0][1] + " y: " + oldPosition[0][0]);
		System.out.println("-- Pixel 2: x: "+ oldPosition[1][1] + " y: " + oldPosition[1][0]);
		System.out.println("-- Pixel 3: x: "+ oldPosition[2][1] + " y: " + oldPosition[2][0]);
		System.out.println("-- Pixel 4: x: "+ oldPosition[3][1] + " y: " + oldPosition[3][0]);
		
		// Aktualisierte Werte werden erst in newPosition zwischengespeichert und nachdem sie mit checkPosition überprüft wurden,
		// auf die tatsächliche Positionsvariable und ins Feld geschrieben.
		int[][] newPosition =  new int[4][2];	
		newPosition[1][0] = this.currentBlock.getPositionAt(1, 0);
		newPosition[1][1] = this.currentBlock.getPositionAt(1, 1);
		
		int rotatingPixelYCord = newPosition[rotatingPixel-1][0];
		int rotatingPixelXCord = newPosition[rotatingPixel-1][1];
		
		System.out.println("-------- Rotations Pixel:");
		System.out.println("-- Pixel 1: x: "+ rotatingPixelXCord + " y: " + rotatingPixelYCord);
		
		for(int i = 0; i < 4; i++) {
			System.out.println("XXXXXXXXXXXXXXXX Counter: " + i);
			
			if(i != (rotatingPixel-1)) {
				System.out.println("-------- Beginn der Berechnungen für die Rotation:");
				int yCord = oldPosition[i][0];
				int xCord = oldPosition[i][1];
				System.out.println("+- Pixel Nr "+i+" des Blockes wird gerade bearbeitet.");
				System.out.println("+- x: "+ xCord + " y: " + yCord);
				// Rechne den Unterschied zwischen dem iterierendem Pixel und dem Vergleichspixel.
				int distanceToRotatingPixelY = rotatingPixelYCord - yCord;
				int distanceToRotatingPixelX = rotatingPixelXCord - xCord;
				
				System.out.println("+- DeltaX Distanzunterschied (rotatingPixelXCord(" + rotatingPixelXCord + ") - xCord( " + xCord + "))  zu Rotationspixel: "+ distanceToRotatingPixelX);
				System.out.println("+- DeltaY Distanzunterschied (rotatingPixelYCord(" + rotatingPixelYCord + ") - yCord( " + yCord + "))  zu Rotationspixel: "+ distanceToRotatingPixelY);
				
				// Wenn die Distanz negativ ist, befindet sich die Pixelposition vor dem Dreher.
				// Ist die Distanz positiv, befindet sie sich hinter ihm.
				
				int newCordY = 0;
				int newCordX = 0;
				
				System.out.println("+- Berechnung der neuen x und y Koordinaten: ");
				
				if(distanceToRotatingPixelY < 0 && distanceToRotatingPixelX < 0) {
					newCordX = rotatingPixelXCord - Math.abs(distanceToRotatingPixelY);
					newCordY = rotatingPixelYCord + Math.abs(distanceToRotatingPixelX);
					System.out.println("+- DeltaX Distanzunterschied ist negativ -> Die x-Koordinate ("+ xCord + ") von Pixel " + i + " ist GRÖßER als die des Rotationspunktes (" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte x Koordinate des Blockpixels befindet sich RECHTS vom Rotationspunkt.");
					System.out.println("+- DeltaY Distanzunterschied ist negativ -> Die y-Koordinate ("+ yCord + ") von Pixel " + i + " ist GrÖßER als die des Rotationspunktes.(" + rotatingPixelYCord + ").");
					System.out.println("+- Die alte y Koordinate des Blockpixels befindet sich UNTER dem Rotationspunkt.");
					
					System.out.println("+- Der neue Punkt muss UNTEN LINKS vom Rotationspixel liegen.");
					System.out.println("+- Die neue X Koordinate: "+ newCordX + "; Die alte X Koordinate: " + xCord);
					System.out.println("+- Die neue Y Koordinate: "+ newCordY + "; Die alte Y Koordinate: " + yCord);
					System.out.println("+- newCordX(" + newCordX + ") = rotatingPixelXCord( " + rotatingPixelXCord + ") -  Math.abs(distanceToRotatingPixelY)(" +  Math.abs(distanceToRotatingPixelY) +")" );
					System.out.println("+- newCordY(" + newCordY + ") = rotatingPixelYCord( " + rotatingPixelYCord + ") +  Math.abs(distanceToRotatingPixelX)(" +  Math.abs(distanceToRotatingPixelX) +")" );		
				}
				else if(distanceToRotatingPixelY > 0 && distanceToRotatingPixelX < 0) {
					newCordX = rotatingPixelXCord + Math.abs(distanceToRotatingPixelY);
					newCordY = rotatingPixelYCord + Math.abs(distanceToRotatingPixelX);
					
					System.out.println("+- DeltaX Distanzunterschied ist negativ -> Die x-Koordinate ("+ xCord + ") von Pixel " + i + " ist GRÖßER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte x Koordinate des Blockpixels befindet sich RECHTS vom Rotationspunkt.");
					System.out.println("+- DeltaY Distanzunterschied ist positiv -> Die y-Koordinate ("+ yCord + ") von Pixel " + i + " ist KLEINER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte y Koordinate des Blockpixels befindet sich ÜBER dem Rotationspunkt.");
					
					System.out.println("+- Der neue Punkt muss UNTEN RECHTS vom Rotationspixel liegen.");
					System.out.println("+- Die neue X Koordinate: "+ newCordX + "; Die alte X Koordinate: " + xCord);
					System.out.println("+- newCordX(" + newCordX + ") = rotatingPixelXCord( " + rotatingPixelXCord + ") -  Math.abs(distanceToRotatingPixelY)(" +  Math.abs(distanceToRotatingPixelY) +")" );
					System.out.println("+- newCordY(" + newCordY + ") = rotatingPixelYCord( " + rotatingPixelYCord + ") -  Math.abs(distanceToRotatingPixelX)(" +  Math.abs(distanceToRotatingPixelX) +")" );		
				
				}
				else if(distanceToRotatingPixelY > 0 && distanceToRotatingPixelX > 0) {
					newCordX = rotatingPixelXCord + Math.abs(distanceToRotatingPixelY);
					newCordY = rotatingPixelYCord - Math.abs(distanceToRotatingPixelX);
					
					System.out.println("+- DeltaX Distanzunterschied ist positiv -> Die x-Koordinate ("+ xCord + ") von Pixel " + i + " ist KLEINER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte x Koordinate des Blockpixels befindet sich LINKS vom Rotationspunkt.");
					System.out.println("+- DeltaY Distanzunterschied ist positiv -> Die y-Koordinate ("+ yCord + ") von Pixel " + i + " ist KLEINER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte y Koordinate des Blockpixels befindet sich ÜBER dem Rotationspunkt.");
					
					System.out.println("+- Der neue Punkt muss OBEN RECHTS vom Rotationspixel liegen.");
					System.out.println("+- Die neue X Koordinate: "+ newCordX + "; Die alte X Koordinate: " + xCord);
					System.out.println("+- newCordX(" + newCordX + ") = rotatingPixelXCord( " + rotatingPixelXCord + ") +  Math.abs(distanceToRotatingPixelY)(" +  Math.abs(distanceToRotatingPixelY) +")" );
					System.out.println("+- newCordY(" + newCordY + ") = rotatingPixelYCord( " + rotatingPixelYCord + ") -  Math.abs(distanceToRotatingPixelX)(" +  Math.abs(distanceToRotatingPixelX) +")" );		
				
				}
				else if(distanceToRotatingPixelY < 0 && distanceToRotatingPixelX > 0) {
					newCordX = rotatingPixelXCord - Math.abs(distanceToRotatingPixelY);
					newCordY = rotatingPixelYCord - Math.abs(distanceToRotatingPixelX);
					
					System.out.println("+- DeltaX Distanzunterschied ist positiv -> Die x-Koordinate ("+ xCord + ") von Pixel " + i + " ist KLEINER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte x Koordinate des Blockpixels befindet sich LINKS vom Rotationspunkt.");
					System.out.println("+- DeltaY Distanzunterschied ist negativ -> Die y-Koordinate ("+ yCord + ") von Pixel " + i + " ist GRÖßER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte y Koordinate des Blockpixels befindet sich UNTER dem Rotationspunkt.");
					
					System.out.println("+- Der neue Punkt muss OBEN LINKS vom Rotationspixel liegen.");
					System.out.println("+- Die neue X Koordinate: "+ newCordX + "; Die alte X Koordinate: " + xCord);
					System.out.println("+- newCordX(" + newCordX + ") = rotatingPixelXCord( " + rotatingPixelXCord + ") +  Math.abs(distanceToRotatingPixelY)(" +  Math.abs(distanceToRotatingPixelY) +")" );
					System.out.println("+- newCordY(" + newCordY + ") = rotatingPixelYCord( " + rotatingPixelYCord + ") +  Math.abs(distanceToRotatingPixelX)(" +  Math.abs(distanceToRotatingPixelX) +")" );		
				
				}
				
				else if(distanceToRotatingPixelY == 0 && distanceToRotatingPixelX < 0) {
					newCordX = rotatingPixelXCord;
					newCordY = rotatingPixelYCord + Math.abs(distanceToRotatingPixelX);
					
					System.out.println("+- DeltaX Distanzunterschied ist negativ -> Die x-Koordinate ("+ xCord + ") von Pixel " + i + " ist GRÖßER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte x Koordinate des Blockpixels befindet sich RECHTS vom Rotationspunkt.");
					System.out.println("+- DeltaY Distanzunterschied ist gleich 0 -> Die y-Koordinate ("+ yCord + ") von Pixel " + i + " ist AUF DER SELBEN KOORDINATE wie die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte y Koordinate des Blockpixels befindet sich IN EINER LINIE mit dem Rotationspunkt.");
					
					System.out.println("+- Der neue Punkt muss IN EINER LINIE UNTER dem Rotationspixel liegen.");
					System.out.println("+- Die neue X Koordinate: "+ newCordX + "; Die alte X Koordinate: " + xCord);
					System.out.println("+- newCordX(" + newCordX + ") = rotatingPixelYCord( " + rotatingPixelYCord + ")");
					System.out.println("+- newCordY(" + newCordY + ") = rotatingPixelYCord( " + rotatingPixelYCord + ") +  Math.abs(distanceToRotatingPixelX)(" +  Math.abs(distanceToRotatingPixelX) +")" );		
				
				}
				
				else if(distanceToRotatingPixelY == 0 && distanceToRotatingPixelX > 0) {
					newCordX = rotatingPixelXCord;
					newCordY = rotatingPixelYCord - Math.abs(distanceToRotatingPixelX);
					
					System.out.println("+- DeltaX Distanzunterschied ist positiv -> Die x-Koordinate ("+ xCord + ") von Pixel " + i + " ist KLEINER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte x Koordinate des Blockpixels befindet sich LINKS vom Rotationspunkt.");
					System.out.println("+- DeltaY Distanzunterschied ist gleich 0 -> Die y-Koordinate ("+ yCord + ") von Pixel " + i + " ist AUF DER SELBEN KOORDINATE wie die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte y Koordinate des Blockpixels befindet sich IN EINER LINIE mit dem Rotationspunkt.");
					
					System.out.println("+- Der neue Punkt muss IN EINER LINIE ÜBER dem Rotationspixel liegen.");
					System.out.println("+- Die neue X Koordinate: "+ newCordX + "; Die alte X Koordinate: " + xCord);
					System.out.println("+- newCordX(" + newCordX + ") = rotatingPixelYCord( " + rotatingPixelYCord + ")");
					System.out.println("+- newCordY(" + newCordY + ") = rotatingPixelYCord( " + rotatingPixelYCord + ") -  Math.abs(distanceToRotatingPixelX)(" +  Math.abs(distanceToRotatingPixelX) +")" );		
				
				}
				
				else if(distanceToRotatingPixelY < 0 && distanceToRotatingPixelX == 0) {
					newCordX = rotatingPixelXCord - Math.abs(distanceToRotatingPixelY);
					newCordY = rotatingPixelYCord;
					
					System.out.println("+- DeltaX Distanzunterschied ist gleich 0 -> Die x-Koordinate ("+ xCord + ") von Pixel " + i + " ist AUF DER SELBEN KOORDINATE wie die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte x Koordinate des Blockpixels befindet sich IN EINER LINIE mit dem Rotationspunkt.");
					System.out.println("+- DeltaY Distanzunterschied ist negativ -> Die y-Koordinate ("+ yCord + ") von Pixel " + i + " ist GRÖßER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte y Koordinate des Blockpixels befindet sich UNTER dem Rotationspunkt.");
					
					System.out.println("+- Der neue Punkt muss IN EINER LINIE LINKS dem Rotationspixel liegen.");
					System.out.println("+- Die neue X Koordinate: "+ newCordX + "; Die alte X Koordinate: " + xCord);
					System.out.println("+- newCordX(" + newCordX + ") = rotatingPixelXCord( " + rotatingPixelYCord + ") -  Math.abs(distanceToRotatingPixelY)(" +  Math.abs(distanceToRotatingPixelY) + ")");
					System.out.println("+- newCordY(" + newCordY + ") = rotatingPixelXCord( " + rotatingPixelXCord + "");
				
				}
				
				else if(distanceToRotatingPixelY > 0 && distanceToRotatingPixelX == 0) {
					newCordX = rotatingPixelXCord + Math.abs(distanceToRotatingPixelY);
					newCordY = rotatingPixelYCord;
					
					System.out.println("+- DeltaX Distanzunterschied ist gleich 0 -> Die x-Koordinate ("+ xCord + ") von Pixel " + i + " ist AUF DER SELBEN KOORDINATE wie die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte x Koordinate des Blockpixels befindet sich IN EINER LINIE mit dem Rotationspunkt.");
					System.out.println("+- DeltaY Distanzunterschied ist positiv -> Die y-Koordinate ("+ yCord + ") von Pixel " + i + " ist KLEINER als die des Rotationspunktes(" + rotatingPixelXCord + ").");
					System.out.println("+- Die alte y Koordinate des Blockpixels befindet sich ÜBER dem Rotationspunkt.");
					
					System.out.println("+- Der neue Punkt muss IN EINER LINIE RECHTS dem Rotationspixel liegen.");
					System.out.println("+- Die neue X Koordinate: "+ newCordX + "; Die alte X Koordinate: " + xCord);
					System.out.println("+- newCordX(" + newCordX + ") = rotatingPixelXCord( " + rotatingPixelYCord + ") +  Math.abs(distanceToRotatingPixelY)(" +  Math.abs(distanceToRotatingPixelY) + ")");
					System.out.println("+- newCordY(" + newCordY + ") = rotatingPixelXCord( " + rotatingPixelXCord + "");
				}
				
				newPosition[i][0] = newCordY;
				newPosition[i][1] = newCordX;
				
			
			
			}
		}
		System.out.println("+++++++++++++ Alte Position des zu rotierenden Blockes:");
		System.out.println("-- Pixel 1: x: "+ oldPosition[0][1] + " y: " + oldPosition[0][0]);
		System.out.println("-- Pixel 2: x: "+ oldPosition[1][1] + " y: " + oldPosition[1][0]);
		System.out.println("-- Pixel 3: x: "+ oldPosition[2][1] + " y: " + oldPosition[2][0]);
		System.out.println("-- Pixel 4: x: "+ oldPosition[3][1] + " y: " + oldPosition[3][0]);
		
		System.out.println("+++++++++++++ Neue Position des zu rotierenden Blockes:");
		System.out.println("-- Pixel 1: x: "+ newPosition[0][1] + " y: " + newPosition[0][0]);
		System.out.println("-- Pixel 2: x: "+ newPosition[1][1] + " y: " + newPosition[1][0]);
		System.out.println("-- Pixel 3: x: "+ newPosition[2][1] + " y: " + newPosition[2][0]);
		System.out.println("-- Pixel 4: x: "+ newPosition[3][1] + " y: " + newPosition[3][0]);
		System.out.println();
				
			// Prüfe ob platz für Rotation ist oder ob nicht gedreht werden kann.
			if(this.checkNewPosition(newPosition) == true) {
				currentBlock.setTurnPlus(!(currentBlock.getTurnPlus())); 
				// Lösche alte Werte aus dem Spielfeld.
				for(int i = 0; i < oldPosition.length; i++) {
					this.setPlayfieldCharacterAtPosition(oldPosition[i][0], oldPosition[i][1], PLYFLD_FREE_CHAR);
				}
					
				for(int i = 0; i < newPosition.length; i++) {
					// Setze die neuen Were in den currentBlock ein.
					this.currentBlock.setPosition(i, 0, newPosition[i][0]);
					this.currentBlock.setPosition(i, 1, newPosition[i][1]);
					//Setze die neuen Werte in das Spielfeld ein.
					this.setPlayfieldCharacterAtPosition(newPosition[i][0], newPosition[i][1], this.currentBlock.getBlockChar());
				}
				System.out.println("Nach Rotation: ");
				this.printPlayfield();
			}
		}
	}
	
	
	
	// Funktion die Überprüft, ob irgendwo auf dem Spielfeld eine Reihe zustandegekommen ist.
	public List<Integer> checkRow() {
		// Wird am Anfang der Überprüfung einer Reihe auf true gesetzt.
		// Sollte irgendein Wert innerhalb der Reihe PLYFLD_FREE_CHAR entsprechen, wird es bis zur Überprüfung der nächsten Reihe auf false gesetzt.
		boolean isCompletedRow;
		
		// Wenn eine Reihe auf dem Spielfeld voll ist, wird die Reihennummer in diese Liste gesetzt.
		// Diese Liste wird später an eine Methode übergeben, die sich um die Folgen kümmert.
		List<Integer> completedRows = new ArrayList<>();
		
		for(int i = 0; i < this.getPlayfieldRowLenght() - 1; i++){
			isCompletedRow = true;
			for(int j = 0; j < this.getPlayfieldColumnLenght(); j++){
				if(this.getPlayfieldAt(i, j) == PLYFLD_FREE_CHAR || this.getPlayfieldAt(i, j) == PLYFLD_BOTTOM_BORDER_CHAR) {
					isCompletedRow = false;
				}
			}
			if(isCompletedRow == true) {
				completedRows.add(i);
			}
		}
		return completedRows;
	}
	
	// Methode die die fertige Reihen vernichtet und Punkte addiert.
	public void clearRows(List<Integer> completedRows) {
		if(completedRows.isEmpty() == false) {
			// Eine Kopie des Spielfeldes wird genutzt, damit das Ergebnis nicht verfälscht wird.
			char[][] oldPlayfield = new char[this.getPlayfieldRowLenght()][this.getPlayfieldColumnLenght()];
			for(int i = 0; i < this.getPlayfieldRowLenght(); i ++) {
				for(int j = 0; j < this.getPlayfieldColumnLenght(); j++) {
					oldPlayfield[i][j] = this.getPlayfieldAt(i, j);
				}
			}
			Collections.reverse(completedRows);
			
			//Wichtige Variable, die die Reihennummer anpasst, wenn mehrere Reihen gleichzeitig gecleared werden.
			//Da alle anderen Reihen nacheinander abgearbeitet werden, muss der Reihenindex um eins erhöht werden, wenn sich die Werte aller Reihen in Folge eines Cleares um eins nach unten verschieben.
			int runs = 0;
			for(int element : completedRows) {
				element = element + runs;
				// Durchlaufe die einzelnen Spalten der fertigen Reihe und setzte ihren Wert auf frei.
				// Angefangen wir dabei von den oberen fertigen Reihen.
				
				for(int j = 1; j < (playfield[0].length) - 1; j++){
					//this.playfield[element][j] = Modell.PLYFLD_FREE_CHAR;
					this.setPlayfieldAt(element, j, Modell.PLYFLD_FREE_CHAR);
					
				}
				this.printPlayfield();
				this.setScore(this.score + Modell.SCORE_BONUS_CLEARED_ROW);
				// Setzte alle Werte der darüberliegenden Reihen eins nach unten.
				//TODO: Überprüfen ob hier noch immer der Wurm sitzt.
				// i > 3; da die Reihen 0-3 nur zum setzen des Blockes ins Spielfeld gehören.
				for(int i = 4; i < element+1; i++) {
					for(int j = 0; j < oldPlayfield[0].length; j++) {					
						this.setPlayfieldAt(i, j, oldPlayfield[i-1][j]);
					}
					
				}
				// Aktualisiere oldPlayfield auf den jetzigen Stand, damit nachfolgende zu bereinigende Reihe auch korrekt abgearbeitet werden.
				for(int i = 0; i < this.getPlayfieldRowLenght(); i ++) {
					for(int j = 0; j < this.getPlayfieldColumnLenght(); j++) {
						oldPlayfield[i][j] = this.getPlayfieldAt(i, j);
					}
				}
				runs++;
			}
			
		}

		
		
	}
	
	
	// Getter und Setter Methoden
	
	public synchronized char[][] getWholePlayfield(){
		return this.playfield.clone();
	}
	
	public synchronized int getPlayfieldRowLenght() {
		return this.playfield.length;
	}
	
	public synchronized int getPlayfieldColumnLenght() {
		return this.playfield[0].length;
	}
	
	public synchronized char getPlayfieldAt(int row, int column) {
		return this.playfield[row][column];
	}
	
	public synchronized void setPlayfieldAt(int row, int column, char c) {
		this.playfield[row][column] = c;
	}
	
	public synchronized Spielblock getCurrentBlock() {
		return this.currentBlock;
	}
	public synchronized void setCurrentBlock(Spielblock newCurrentBlock) {
		this.currentBlock = newCurrentBlock;
	}
	
	public synchronized Spielblock getNextBlock() {
		return this.nextBlock;
	}
	
	public synchronized void setNextBlock(Spielblock newNextBlock) {
		this.nextBlock = newNextBlock;
	}
	
	public synchronized int getScore() {
		return this.score;
	}
	
	public synchronized void setScore(int newScore) {
		this.score = newScore;
	}
	
	static synchronized int getFallTime() {
		return Modell.fallTime;
	}
	
	synchronized void setControll(Steuerung controll) {
		this.controll = controll;
	}
	
	synchronized Steuerung getController() {
		return this.controll;
	}
	
	static synchronized void setFallTime(int newFallTime) {
		Modell.fallTime = newFallTime;
	}
	
	public synchronized char getPlayfieldCharacterAtPosition(int row, int column) {
		char positionChar = this.playfield[row][column];
		return positionChar;
	}
	
	public synchronized void setPlayfieldCharacterAtPosition(int row, int column, char newCharacter) {
		this.playfield[row][column] = newCharacter;
	}
	
	public synchronized int getBlockCount() {
		return this.blockCount;
	}
	
	public synchronized void setBlockCount(int blockCount) {
		this.blockCount = blockCount;
	}
	
	public synchronized void setFallTimeUpdated(int fallTime) {
		this.fallTimeUpdated = fallTime;
	}
	
	public synchronized int getFallTimeUpdated() {
		return this.fallTimeUpdated;
	}
	
	
	public boolean contains(char[] array, char findChar) {
		boolean contains = false;
		for(int i = 0; i < array.length; i++) {
			if(array[i] == findChar) {
				contains = true;
				break;
			}
		}
		return contains;
	}
	// Getter und Setter Methoden Ende
	
}
