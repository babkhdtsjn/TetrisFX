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
	static final char RNDR_FREE_CHAR = '_';
	
	// Größe des Spielfeldes + Platz für die Berandung.
	private char[][] playfield;
	private int ply_len_x;
	private int ply_len_y;
	
	// Block der gerade gesteuert wird und nach unten fällt.
	private volatile Spielblock currentBlock = null;
	
	// Punktzahl des Spielers
	static int score = 0;
	static final  int SCORE_BONUS_CLEARED_ROW = 10;
	
	// ms die ein Block braucht um zu fallen.
	static final int FALL_TIME_NORMAL = 1000;
	static final int FALL_TIME_SPEEDUP = 350;
	static int fallTime = 1000;
	
	// Position an der der Block in die Bufferzone gesetzt wird, wenn er spawnt.
	// Variabel, da der Startplatz von der Dimension des Blockes abhängig ist.
	int blockstart_x = 0; 
	int blockstart_y = 0;
	
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
		//System.out.println("Spielfeld fertig erstellt!");
	}
	
	// Macht aus dem Feld playfield ein String den später die GUI bei der ausgabe verwerten kann.
	// Ergebnis: "|      x      | \n" + "...\n" + ...
	/*public String playfieldAsString() {
		String playfieldString = "";
		for(int i = 0; i < playfield.length; i++){
			playfieldString = playfieldString + '"';
			for(int j = 0; j < playfield[0].length;j++) {
				playfieldString = playfieldString + playfield[i][j];
			}
			if((i + 1) == playfield.length) {
				playfieldString = playfieldString + '"';
			}
			else {
			playfieldString = playfieldString + " \\n" + '"' + " + ";
			}
		}
		return playfieldString;
	}
	*/
	
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
	
	
	public void printPlayfield() {
		for(int i = 0; i < playfield.length; i++){
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
		System.out.println("Spielfeld mit Buffer gerendert!");
	}
	
	public void printPlayfieldWithoutBuffer() {
		for(int i = 0; i < playfield.length; i++){
			// Überspring die Bufferzeilen
			if(i < BUFFER_Y) {
				//System.out.println("Ich bin im Continue zum "+ (i+1) + ". mal!");
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
		// Schreibe den Block seiner Form nach in das Spielfeld
		for(int i = 0; i < currentBlockDimY; i++) {
			for(int j = 0; j < currentBlockDimX; j++) {
				// Wenn die momentane Position ein Teil des Blockes enthält, soll dieser auf das Spielfeld übertragen werden.
				if(currentBlockField[i][j] == Spielblock.BLOCK_CHAR) {
					// Die Position im Spielfeld muss um den versatz blockstart angepasst werden, da das Feld currentBlock eine andere Größe hat.
					this.playfield[i+this.blockstart_y][j+this.blockstart_x] = Spielblock.BLOCK_CHAR;
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
				if(playfield[i][j] == Spielblock.BLOCK_CHAR) {
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
			playfield[this.currentBlock.getPosition(i,0)][this.currentBlock.getPosition(i,1)] = Spielblock.BLOCK_CHAR;
	
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
			else if(this.playfield[lowestPosition[0]+1][nextPositionY] == Spielblock.PLAYFIELD_BLOCK_CHAR) {
				// Nächstes Feld auf das der currentBlock fällt ist ein anderer Block, er kann nicht weiter fallen.
				obstacleDetected = true;
			}
			else {
				obstacleDetected = false;
			}
		}
		// Wenn currentBlock == square ist, wird eine breitere Detektion gebraucht.
		// Die lowestPosition ist beim square Block der rechte untere Pixel.
		// TODO: fix für alle Blöcke die 2 Breit sind::!!
		else if(this.currentBlock.getBlockName() != "line") {
			if(this.playfield[lowestPosition[0]+1][nextPositionY] == PLYFLD_BOTTOM_BORDER_CHAR ||
					this.playfield[lowestPosition[0]+1][nextPositionY - 1] == PLYFLD_BOTTOM_BORDER_CHAR
					) {
				// Nächster Feld auf das der currentBlock fällt ist die untere Grenze, er kann nicht weiter fallen.
				obstacleDetected = true;
			}
			else if(this.playfield[lowestPosition[0]+1][nextPositionY] == Spielblock.PLAYFIELD_BLOCK_CHAR ||
					this.playfield[lowestPosition[0]+1][nextPositionY - 1] == Spielblock.PLAYFIELD_BLOCK_CHAR) {
				// Nächstes Feld auf das der currentBlock fällt ist ein anderer Block, er kann nicht weiter fallen.
				obstacleDetected = true;
			}
			else {
				obstacleDetected = false;
			}
		}
		return obstacleDetected;
	}
	
	//Überprüft die new Position die 4 Zeilen und 2 Spalten hat.
	//Bevor eine vom Spieler angeforderte Bewegung ausgeführt wird.
	public boolean checkNewPosition(int [][] newPosition) {
		boolean notObstructed = true;
		
		//Durchlaufe jede Position und überprüfe ob diese im Spielfeld frei ist.
		for(int i_row = 0; i_row < 4; i_row++) {
			int[] currentlyCheckingPosition = new int[2];
			currentlyCheckingPosition[0] = newPosition[i_row][0];//y-Koordinate eines Pixels der neuen Position / Reihe.
			currentlyCheckingPosition[1] = newPosition[i_row][1];//x-Koordinate eines Pixels der neuen Position / Spalte.
			if(this.playfield[currentlyCheckingPosition[0]][currentlyCheckingPosition[1]] == PLYFLD_SIDE_BORDER_CHAR ) {
				// Block stößt an Rand an.
				notObstructed = false;
			}
			else if(this.playfield[currentlyCheckingPosition[0]][currentlyCheckingPosition[1]] == Spielblock.PLAYFIELD_BLOCK_CHAR) {
				// Block stoßt an bereits gesetzten Block an.
				notObstructed = false;
			}
		}
		return notObstructed;
	}
	
	
	
	// Überträgt die Positionen vom derzeitigen currentBlock in das Spielfeld als PLAYFIELD_BLOCK_CHAR.
	public void insertCurrentBlockAsPlayfieldBlock() {
		for(int i =0; i < Spielblock.POSITION_LENGHT_Y;i++) {
			this.playfield[this.currentBlock.getPosition(i, 0)][this.currentBlock.getPosition(i, 1)] = Spielblock.PLAYFIELD_BLOCK_CHAR;
		}
	}
	
	// Überprüft ob ein currentBlock nach Setzen im Buffer ist, dann ist das Spiel vorbei.
	public boolean checkGameEnd() {
		boolean gameEnd = false;
		for(int i = 0; i < Modell.BUFFER_Y; i ++) {
			for(int j = 0; j < Modell.PLYFLD_X; j++){
				if(this.playfield[i][j] == Spielblock.PLAYFIELD_BLOCK_CHAR) {
					gameEnd = true;
					break;
				}
			}	
		}
		return gameEnd;
	}
	
	// Funktion zum Bewegen des currentBlocks nach links und rechts.
	public void moveCurrentBlock(String direction) {
		int[][] newPosition = new int[4][2];
		
		if(direction == "right") {
			// Setze die neue Position; für eine Bewegung nach Rechts wird die Spalte um eins erhöht.
			for(int i_row = 0; i_row < 4; i_row++) {
				newPosition[i_row][0] = this.currentBlock.getPosition(i_row, 0);		//y-Koordinate
				newPosition[i_row][1] = this.currentBlock.getPosition(i_row, 1) + 1;	//x-Koordinate
			}		
		}
		else if(direction == "left") {
			for(int i_row = 0; i_row < 4; i_row++) {
				newPosition[i_row][0] = this.currentBlock.getPosition(i_row, 0);		//y-Koordinate
				newPosition[i_row][1] = this.currentBlock.getPosition(i_row, 1) - 1;	//x-Koordinate
			}	
		}
		else {
			System.out.println("Irgendwas ist schief gelaufen.");
			// Fehler!
		}
		// Überprüfe, ob der currentBlock auch wirklich auf die neue Position gerückt werden darf.
		// Wenn checkNewPosition true ist, befindet sich auf den neuen Pixelpositionen kein anderes Objekt.
		if(checkNewPosition(newPosition) == true) {	
			// Ändere die currentPosition vom currentBlock.
			for(int i_row = 0; i_row < 4; i_row++) {
				// Lösche die alten Werte.
				playfield[this.currentBlock.getPosition(i_row, 0)][this.currentBlock.getPosition(i_row, 1)] = PLYFLD_FREE_CHAR;
				
				// Ändere die Werte vom currentBlock.
				this.currentBlock.setPosition(i_row, 0, newPosition[i_row][0]); // Neuer y-Koordinaten Wert.
				this.currentBlock.setPosition(i_row, 1, newPosition[i_row][1]); // Neuer x-Koordinaten Wert.
				
				// Übertrage die neuen Werte auf das Spielfeld
				
				playfield[this.currentBlock.getPosition(i_row, 0)][this.currentBlock.getPosition(i_row, 1)] = Spielblock.BLOCK_CHAR;
			}
		}
		
		
		
		
		
	}
	
	// Funktion zum Rotieren des currenBlocks (im Uhrzeigersinn)
	// Alle Blöcke rotieren sich um ihren zweiten Pixel.
	public void rotateCurrentBlock() {
		final int rotatingPixel = 2;
		
		int[][] oldPosition = this.currentBlock.getPosition();
		int distanceToRotatingPixelY = 0;
		int distanceToRotatingPixelX = 0;
		
		
		int rotatingPixelYCord = oldPosition[rotatingPixel-1][0];
		int rotatingPixelXCord = oldPosition[rotatingPixel-1][1];
		for(int i = 0; i < oldPosition.length; i++) {
			if(i != (rotatingPixel-1)) {
				int yCord = oldPosition[i][0];
				int xCord = oldPosition[i][1];
				// Rechne den Unterschied zwischen dem iterierendem Pixel und dem Vergleichspixel.
				distanceToRotatingPixelY = yCord - rotatingPixelYCord;
				distanceToRotatingPixelX = xCord - rotatingPixelXCord;
				// Spiegele die Koordinaten des iterierendem Pixels am Vergleichspixel.
				int newCordY = rotatingPixelYCord - distanceToRotatingPixelY;
				int newCordX = rotatingPixelXCord - distanceToRotatingPixelX;
				
				// Setze den neuen Wert ein.
				this.currentBlock.setPosition(i, 0, newCordY);
				this.currentBlock.setPosition(i, 1, newCordX);
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
		
		for(int i = 0; i < playfield.length; i++){
			isCompletedRow = true;
			for(int j = 0; j < playfield[0].length; j++){
				if(playfield[i][j] == PLYFLD_FREE_CHAR) {
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
			char[][] oldPlayfield = this.playfield;
			Collections.reverse(completedRows);
			for(int element : completedRows) {
				// Durchlaufe die einzelnen Spalten der fertigen Reihe und setzte ihren Wert auf frei.
				// Angefangen wir dabei von den oberen fertigen Reihen.
				for(int j = 0; j < playfield[0].length; j++){
					this.playfield[element][j] = Modell.PLYFLD_FREE_CHAR;
				}
				Modell.setScore(Modell.score + Modell.SCORE_BONUS_CLEARED_ROW);
				// Setzte alle Werte der darüberliegenden Reihen eins nach unten.
				for(int i = element+1; i < playfield.length; i++) {
					for(int j = 0; j < playfield[i].length; j++) {
						playfield[i - 1][j] = oldPlayfield[i][j];
						playfield[i][j] = Modell.PLYFLD_FREE_CHAR;
					}
				}
			}
			
		}

		
		
	}
	
	
	// Getter und Setter Methoden
	
	public Spielblock getCurrentBlock() {
		return this.currentBlock;
	}
	public void setCurrentBlock(Spielblock newCurrentBlock) {
		this.currentBlock = newCurrentBlock;
	}
	
	static synchronized int getScore() {
		return Modell.score;
	}
	
	static synchronized void setScore(int newScore) {
		Modell.score = newScore;
	}
	
	static synchronized int getFallTime() {
		return Modell.fallTime;
	}
	
	static synchronized void setFallTime(int newFallTime) {
		Modell.fallTime = newFallTime;
	}
	
	
	// Getter und Setter Methoden Ende
	
/*
	public void playLoop() {
		// Setup für das Spiel. Baue als erstes das Spielfeld auf.
		createPlayfieldBorder();
		
		printPlayfield();
		
		// String plyfld = playfieldAsString(); 
		
		// Ansicht.createAndShowGUI(plyfld);
		
		boolean run = true;
		while(run) {
			// Spielblock erschaffen.
			this.currentBlock = createRandomBlock();
			// Spielblock in Bufferzone von Spielfeld legen.
			putBlockInBufferzone();
			// Drucke Spielfeld mit Bufferzone.
			printPlayfield();
			System.out.println(playfieldAsString2());
			// Überprüfe ob der Spielblock fallen kann, wenn ja lass ihn fallen
			this.moveCurrentBlock("right");
			while(this.checkForObstacle() == false) {
				currentBlockFall();
				printPlayfield();
				wait(1000);	// Warte eine Sekunde bevor der Block weiter fällt, falls möglich.
			}
			//Übertrage den currentBlock aufs Spielfeld
			insertCurrentBlockAsPlayfieldBlock();
			// TODO: Checken, ob eine Pfeiltaste gedrückt wurde.
			
			// TODO: Checken, ob eine vollständige Reihe zustandegekommen ist.
			// TODO: Checken, ob in den Buffer reingesetzt worden ist, dann wird das Spiel beendet.
			if (checkGameEnd() == true) {
				System.out.println("Spiel vorbei!!");
				run = false;
			}
			else {
				wait(2000); // Warte zwei Sekunden bevor der nächste Block fällt.
			}
		}
		
		// Wenn Spielblock anderen Spielblock erreicht wird er zu Teil von Spielfeld
		
	}
	*/
}
