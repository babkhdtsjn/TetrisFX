package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class Bildrenderer {

/* Das Spielfeld ist (mit Rand) 12 breit und (mit Boden und Bufferer) 25 hoch.
 * Um das Spielfeld zu erstellen wird ein 12x25 Raster gebraucht das dynamisch befüllt wird.
 * 
 * 
 * 
 */
	static final int ROWS = Modell.PLYFLD_Y + Modell.PLYFLD_BORDER_Y + Modell.BUFFER_Y;
	static final int COLUMNS = Modell.PLYFLD_X + Modell.PLYFLD_BORDER_X;
	Image[][] img;
	ImageView[][] iv;
	//= new Image("file:data/Rand.png");
	//ImageView ivTest = new ImageView(img);	//30x30 ist gute größe
	//this.root.add(ivTest, 3, 2);
	
	static final String BORDER_PATH = "file:data/Rand.png";
	static final String LEFT_CORNER_PATH = "file:data/EckeLinks.png";
	static final String RIGHT_CORNER_PATH= "file:data/EckeRechts.png";
	static final String EMPTY_FIELD_PATH ="file:data/Leer.png";
	static final String BOTTOM_PATH = "file:data/Boden.png";
	
	static final String RED_BLOCK_PATH = "file:data/RoterBlockV3.png";
	static final String BLUE_BLOCK_PATH = "file:data/BlauerBlockV3.png";
	static final String YELLOW_BLOCK_PATH = "file:data/GelberBlockV3.png";
	static final String PURPLE_BLOCK_PATH = "file:data/LilaBlockV3.png";
	static final String GREEN_BLOCK_PATH = "file:data/GruenerBlockV3.png";
	static final String LIGHTBLUE_BLOCK_PATH = "file:data/HellblauerBlockV3.png";
	
	static final String ERROR_BLOCK_PATH = "file:data/Test.png";
	
	// Bilder
	Image imgLeftCorner = new Image(LEFT_CORNER_PATH);
	Image imgRightCorner = new Image(RIGHT_CORNER_PATH);
	Image imgBorder = new Image(BORDER_PATH);;
	Image imgBottom = new Image(BOTTOM_PATH);;
	Image imgEmpty = new Image(EMPTY_FIELD_PATH);;
	
	Image imgRedBlock  = new Image(RED_BLOCK_PATH);
	Image imgBlueBlock = new Image(BLUE_BLOCK_PATH);
	Image imgYellowBlock = new Image(YELLOW_BLOCK_PATH);
	Image imgPurpleBlock = new Image(PURPLE_BLOCK_PATH);
	Image imgGreenBlock = new Image(GREEN_BLOCK_PATH);
	Image imgLightblueBlock = new Image(LIGHTBLUE_BLOCK_PATH);
	
	Image imgError = new Image(ERROR_BLOCK_PATH);
	
	
	GridPane root;
	
	public Bildrenderer(GridPane root) {
		//rows = Modell.PLYFLD_Y + Modell.PLYFLD_BORDER_Y + Modell.BUFFER_Y;
		//columns = Modell.PLYFLD_X + Modell.PLYFLD_BORDER_X;
		
		// GridPane ist anders als ein Java Feld. Reihen- und Spaltenindex sind vertauscht.
		//img = new Image[COLUMNS][ROWS];
		iv = new ImageView[COLUMNS][ROWS];
		
		// Die verschiedenen Bilder von Teilen der GUI bzw. des Spielfeldes.
		
		
		
		// Holt sich das GridPane aus der Überklasse.
		this.root = root;
		for(int column = 0; column < COLUMNS; column++) {
			for(int row = 0; row < ROWS; row++) {
			//empty grid element
			//root.add(null, row, column);
			if(row == ROWS-1 && column == 0) {		//linke Ecken
				//img[column][row] = imgLeftCorner;
				iv[column][row] = new ImageView(imgLeftCorner);
			}
			else if(row == ROWS-1 && column == COLUMNS-1) {	//rechte Ecke
				//img[column][row] = imgRightCorner;
				iv[column][row] = new ImageView(imgRightCorner);
			}
			else if((row != ROWS-1 && row != 0) && (column == 0 || column == COLUMNS-1)) { //Rand
				//img[column][row] = imgBorder;
				iv[column][row] = new ImageView(imgBorder);
			}
			else if(row == ROWS-1 && (column != 0 || column != COLUMNS-1)) { // Boden
				//img[column][row] = imgBottom;
				iv[column][row] = new ImageView(imgBottom);
			}
			else {	//Leeres Feld
				//img[column][row] = imgEmpty;
				iv[column][row] = new ImageView(imgEmpty);
			}
			// Füge das Bild in den passenden ImageView ein.
			//iv[column][row] = new ImageView(getImageAt(column, row));
			root.add(iv[column][row], column, row);
		}
		}
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
						iv[column][row].setImage(imgRedBlock);
						break;
					case '2':
					case 'b':
						iv[column][row].setImage(imgBlueBlock);
						break;
					case '3':
					case 'y':
						iv[column][row].setImage(imgYellowBlock);
						break;
					case '4':
					case 'p':
						iv[column][row].setImage(imgPurpleBlock);
						break;
					case '5':
					case 'g':
						iv[column][row].setImage(imgGreenBlock);
						break;
					case '6':
					case 'l':
						iv[column][row].setImage(imgLightblueBlock);
						break;
					case Modell.RNDR_FREE_CHAR:
						iv[column][row].setImage(imgEmpty);
						break;
					default:
						iv[column][row].setImage(imgError);
						System.out.println("Fehler beim bestimmen der Farbe eines Blockes im Renderer");
				}		
			}
		}
	}
	
	
	
	/*
	public synchronized Image getImageAt(int column, int row) {
		return this.img[column][row];
	}
	*/
	
	
}
