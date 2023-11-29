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
	int rows;
	int columns;
	Image[][] img;
	ImageView[][] iv;
	//= new Image("file:data/Rand.png");
	//ImageView ivTest = new ImageView(img);	//30x30 ist gute größe
	//this.root.add(ivTest, 3, 2);
	
	static final String borderPath = "file:data/Rand.png";
	static final String leftCornerPath = "file:data/EckeLinks.png";
	static final String rightCornerPath= "file:data/EckeRechts.png";
	static final String emptyFieldPath= "file:data/Leer.png";
	static final String bottomPath = "file:data/Boden.png";
	
	
	
	GridPane root;
	
	public Bildrenderer(GridPane root) {
		rows = Modell.PLYFLD_Y + Modell.PLYFLD_BORDER_Y + Modell.BUFFER_Y;
		columns = Modell.PLYFLD_X + Modell.PLYFLD_BORDER_X;
		
		img = new Image[rows][columns];
		iv = new ImageView[rows][columns];
		
		// Holt sich das GridPane aus der Überklasse.
		this.root = root;
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				//empty grid element
				//root.add(null, row, column);
				if(row == rows && column == 0) {		//linke Ecken
					img[row][column] = new Image(leftCornerPath);
				}
				else if(row == rows && column == columns) {	//rechte Ecke
					img[row][column] = new Image(rightCornerPath);
				}
				else if((row != rows && row != 0) && (column == 0 || column == columns)) { //Rand
					img[row][column] = new Image(borderPath);
				}
				else if(row == rows && (column != 0 || column != columns)) { // Boden
					img[row][column] = new Image(bottomPath);
				}
				else {	//Leeres Feld
					img[row][column] = new Image(emptyFieldPath);
				}
				// Füge das Bild in den passenden ImageView ein.
				iv[row][column] = new ImageView(getImageAt(row,column));
				root.add(iv[row][column], column, row);
			}
		}
	}
	public Image getImageAt(int row, int column) {
		return this.img[row][column];
	}
	
	
}
