package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class Bildrenderer {

/* Das Spielfeld ist (mit Rand) 12 breit und (mit Boden und Bufferer) 25 hoch.
 * Um das Spielfeld zu erstellen wird ein 12x25 Raster gebraucht das dynamisch befüllt wird.
 * 
 * 
 * 
 */
	static final int ROWS = Modell.PLYFLD_Y + Modell.PLYFLD_BORDER_Y + Modell.BUFFER_Y;
	static final int COLUMNS = Modell.PLYFLD_X + Modell.PLYFLD_BORDER_X;
	//Image[][] img;
	ImageView[][] ivPlayfield;
	ImageView[][] ivNextBlock;
	
	static final String LEFT_BORDER_PATH = "file:data/RandLinksV2.png";
	static final String RIGHT_BORDER_PATH = "file:data/RandRechtsV2.png";
	static final String LEFT_CORNER_PATH = "file:data/EckeLinksV2.png";
	static final String RIGHT_CORNER_PATH= "file:data/EckeRechtsV2.png";
	static final String EMPTY_FIELD_PATH ="file:data/Leer.png";
	static final String BOTTOM_PATH = "file:data/BodenV2.png";
	
	static final String RED_BLOCK_PATH = "file:data/RoterBlockV3.png";
	static final String BLUE_BLOCK_PATH = "file:data/BlauerBlockV3.png";
	static final String YELLOW_BLOCK_PATH = "file:data/GelberBlockV3.png";
	static final String PURPLE_BLOCK_PATH = "file:data/LilaBlockV3.png";
	static final String GREEN_BLOCK_PATH = "file:data/GruenerBlockV3.png";
	static final String LIGHTBLUE_BLOCK_PATH = "file:data/HellblauerBlockV3.png";
	
	static final String ERROR_BLOCK_PATH = "file:data/Test.png";
	
	
	// Größe für das Feld in dem der nächste Block abgebildet werden soll.
	static final int NEXT_BLOCK_X = 2;
	static final int NEXT_BLOCK_Y = 4;
	
	// Bilder
	Image imgLeftCorner = new Image(LEFT_CORNER_PATH);
	Image imgRightCorner = new Image(RIGHT_CORNER_PATH);
	Image imgLeftBorder = new Image(LEFT_BORDER_PATH);
	Image imgRightBorder = new Image(RIGHT_BORDER_PATH);
	Image imgBottom = new Image(BOTTOM_PATH);;
	Image imgEmpty = new Image(EMPTY_FIELD_PATH);;
	
	Image imgRedBlock  = new Image(RED_BLOCK_PATH);
	Image imgBlueBlock = new Image(BLUE_BLOCK_PATH);
	Image imgYellowBlock = new Image(YELLOW_BLOCK_PATH);
	Image imgPurpleBlock = new Image(PURPLE_BLOCK_PATH);
	Image imgGreenBlock = new Image(GREEN_BLOCK_PATH);
	Image imgLightblueBlock = new Image(LIGHTBLUE_BLOCK_PATH);
	
	Image imgError = new Image(ERROR_BLOCK_PATH);
	
	private Text nextBlockText = new Text();
	private Text scoreDescriptionText = new Text();
	private Text scoreText = new Text();
	
	
	
	
	GridPane root;
	
	public Bildrenderer(GridPane root) {
		// ----- Spielfläche ---------
		// GridPane ist anders als ein Java Feld. Reihen- und Spaltenindex sind vertauscht.
		ivPlayfield = new ImageView[COLUMNS][ROWS];
			
		// Holt sich das GridPane aus der Überklasse.
		this.root = root;
		for(int column = 0; column < COLUMNS; column++) {
			for(int row = 0; row < ROWS; row++) {
			//empty grid element
			//root.add(null, row, column);
			if(row == ROWS-1 && column == 0) {		//linke Ecken
				//img[column][row] = imgLeftCorner;
				ivPlayfield[column][row] = new ImageView(imgLeftCorner);
			}
			else if(row == ROWS-1 && column == COLUMNS-1) {	//rechte Ecke
				//img[column][row] = imgRightCorner;
				ivPlayfield[column][row] = new ImageView(imgRightCorner);
			}
			else if((row != ROWS-1 && row != 0) && (column == 0)) { //linker Rand
				//img[column][row] = imgBorder;
				ivPlayfield[column][row] = new ImageView(imgLeftBorder);
			}
			else if((row != ROWS-1 && row != 0) && (column == COLUMNS-1)) { //rechter Rand
				//img[column][row] = imgBorder;
				ivPlayfield[column][row] = new ImageView(imgRightBorder);
			}
			else if(row == ROWS-1 && (column != 0 || column != COLUMNS-1)) { // Boden
				//img[column][row] = imgBottom;
				ivPlayfield[column][row] = new ImageView(imgBottom);
			}
			else {	//Leeres Feld
				//img[column][row] = imgEmpty;
				ivPlayfield[column][row] = new ImageView(imgEmpty);
			}
			// Füge das Bild in den passenden ImageView ein.
			//iv[column][row] = new ImageView(getImageAt(column, row));
			root.add(ivPlayfield[column][row], column, row);
			}
		}
		// ------ Nächster Block ---------
		this.nextBlockText.setStyle("-fx-font: normal bold 22px 'Courier' ");
		this.nextBlockText.setText("Nächster Block: ");
		// Put on cell (0,0), span 2 column, 1 row.
	    //  root.add(labelTitle, 0, 0, 2, 1);
		root.add(nextBlockText, COLUMNS, 0, 2,1);
		
		
		ivNextBlock = new ImageView[NEXT_BLOCK_X][NEXT_BLOCK_Y];
		for(int column = 0; column < Bildrenderer.NEXT_BLOCK_X; column++) {
			for(int row = 0; row < Bildrenderer.NEXT_BLOCK_Y; row++) {
				ivNextBlock[column][row] = new ImageView(imgEmpty);
				root.add(ivNextBlock[column][row], COLUMNS + column, row+1);
			}
		}
		// ------- Punktzahl -------------
		this.scoreText.setStyle("-fx-font: normal bold 22px 'Courier' ");
		this.scoreDescriptionText.setStyle("-fx-font: normal bold 22px 'Courier' ");
		this.scoreDescriptionText.setText("Punktezahl: ");
		this.scoreText.setText("0");
		
		root.add(scoreText, COLUMNS, ROWS-2, 2,1);
		root.add(scoreDescriptionText, COLUMNS , ROWS -3, 2,1);
		
	}
	
	public synchronized void updatePlayfieldView(char[][] playfield) {
		// Vorsicht: playfield[row][column] und Image[column][row]/ImageView [column][row]
		// Druchlaufe das Spielfeld und schaue nach Änderungen
		// Eine Reihe weniger muss durchlaufen werden, da die letzte Zeile sowieso nur die Grenze beinhaltet.
		// Bei der ersten und letzten Spalte ist es ebenso. 
		for(int row = 0; row < Bildrenderer.ROWS - 1; row++) {
			for(int column = 1; column < Bildrenderer.COLUMNS - 1; column++) {
				switch(playfield[row][column]){
					case '1':
					case 'r':
						ivPlayfield[column][row].setImage(imgRedBlock);
						break;
					case '2':
					case 'b':
						ivPlayfield[column][row].setImage(imgBlueBlock);
						break;
					case '3':
					case 'y':
						ivPlayfield[column][row].setImage(imgYellowBlock);
						break;
					case '4':
					case 'p':
						ivPlayfield[column][row].setImage(imgPurpleBlock);
						break;
					case '5':
					case 'g':
						ivPlayfield[column][row].setImage(imgGreenBlock);
						break;
					case '6':
					case 'l':
						ivPlayfield[column][row].setImage(imgLightblueBlock);
						break;
					case Modell.RNDR_FREE_CHAR:
						ivPlayfield[column][row].setImage(imgEmpty);
						break;
					default:
						ivPlayfield[column][row].setImage(imgError);
						System.out.println("Fehler beim bestimmen der Farbe eines Blockes im Renderer");
				}		
			}
		}
	}
	
	public synchronized void updateNextBlockView(Spielblock nextBlock) {
		// Mach als erstes das NextBlock Imageviewer Feld leer.
		// ivNextBlock
		
		for(int column = 0; column < ivNextBlock.length; column++) {
			for(int row = 0; row < ivNextBlock[column].length; row++) {
				this.ivNextBlock[column][row].setImage(imgEmpty);
			}
		}
		
		
		Image nextBlockImage;
		switch (nextBlock.getColor()) {
			case "Red":
				nextBlockImage = imgRedBlock;
				break;
			case "Blue":
				nextBlockImage = imgBlueBlock;
				break;
			case "Yellow":
				nextBlockImage = imgYellowBlock;
				break;
			case "Purple":
				nextBlockImage = imgPurpleBlock;
				break;
			case "Green":
				nextBlockImage = imgGreenBlock;
				break;
			case "Lightblue":
				nextBlockImage = imgLightblueBlock;
				break;
			default: 
				nextBlockImage = imgError;
				break;
		}
		char[][] blockField = nextBlock.getBlockField();
		for(int row = 0; row < blockField.length; row++) {
			for(int column = 0; column < blockField[0].length; column++) {
				if(blockField[row][column] == nextBlock.getBlockChar()) {
					ivNextBlock[column][row].setImage(nextBlockImage);
				}
				else {ivNextBlock[column][row].setImage(imgEmpty);}
			}
		}
	}
	
	public void setScoreText(String newText){
		this.scoreText.setText(newText);
	}
	
	public Text getScoreText() {
		return this.scoreText;
	}
	/*
	public synchronized Image getImageAt(int column, int row) {
		return this.img[column][row];
	}
	*/
	
	
}
