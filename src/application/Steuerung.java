package application;


//import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
// import javafx.application.Platform;


public class Steuerung {
	// Informationen über das Spielfeld und den currentBlock als String für die GUI.
	static String map;
	
	// Legt fest wie lange gewartet wird, bis man schaut, ob der Spieler nocheinmal rotieren möchte.
	static final int WAIT_ROTATE_TIME = 100;
	
	
	Ansicht gui = new Ansicht();
	Modell model = new Modell();
	
	
	public void startGame(String[] args) {
		//Steuerung controll = new Steuerung();
		
		Thread guiThread = new Thread() {
			public void run() {
				Application.launch(Ansicht.class, args);
	
				while(true) {
					try
				    {
				        Thread.sleep(1000);
				    }
				    catch(InterruptedException ex)
				    {
				        Thread.currentThread().interrupt();
				    }
					
				}
				
			}	
		};
		
		Thread logicThread = new Thread() {
			public void run() {
				System.out.println("_______");
				
				// Setup Playfield
				model.createPlayfieldBorder();
				playLoop();
			}	
		};
		Thread inputThread = new Thread() {
			public void run() {
				while(true) {
					//System.out.println("Links Status: " + Ansicht.leftDirection);
					//System.out.println("Rechts Status: " + Ansicht.rightDirection);
				// Überprüfe ob eine Pfeiltaste gedrückt wurde.
				//Platform.runLater(() -> {
				if(Ansicht.getLeftDirection() == true) {
					model.moveCurrentBlock("LEFT");
					
				}
				else if(Ansicht.getRightDirection() == true) {
					model.moveCurrentBlock("RIGHT");
				}
				else if(Ansicht.getUpDirection() == true) {
					model.rotateCurrentBlock();
					System.out.println("---------------Rotating!!!!!!!");
					Modell.wait(WAIT_ROTATE_TIME);
				}
				
				if(Ansicht.getDownDirection() == true && Modell.getFallTime() != Modell.FALL_TIME_SPEEDUP) {
					Modell.setFallTime(Modell.FALL_TIME_SPEEDUP);
				}
				if(Ansicht.getDownDirection() == false && Modell.getFallTime() != Modell.FALL_TIME_NORMAL) {
					Modell.setFallTime(Modell.FALL_TIME_NORMAL);
				}
				try {
		               Thread.sleep(100); // Kurze Pause, um die CPU-Last zu reduzieren
		           } catch (InterruptedException e) {
		               e.printStackTrace();
		           }
				//});
				}
				}	
		};
		
		guiThread.start();
		logicThread.start();
		inputThread.start();

		// Wait for them to finish
		try {
			guiThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			logicThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			inputThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void debugTest() {}
	
	
	public void playLoop() {
		boolean run = true;
		int falldistance = 0;
		while(run) {
			// Spielblock erschaffen.
			//model.setCurrentBlock(model.createRandomBlock());
			
			//Debug
			for(int i = 1; i<(model.getPlayfieldColumnLenght()-2); i++) {
				model.setPlayfieldAt(model.getPlayfieldRowLenght() - 2,i , 'o');
			}
			
			
			model.setCurrentBlock(new Spielblock("line"));
			// TODO: Nächsten Block auch gleich erschaffen.
			// Spielblock in Bufferzone von Spielfeld legen.
			model.putBlockInBufferzone();
			// Drucke Spielfeld mit Bufferzone.
			model.printPlayfield();
			
			map = model.playfieldAsString();
			// Überprüfe ob der Spielblock fallen kann, wenn ja lass ihn fallen
			
			while(model.checkNewPosition(model.calculateNewPosition("DOWN")) == true) {
				if(falldistance == 6) {
					model.rotateCurrentBlock();
					System.out.println(""); 
				}
				falldistance +=1;
				model.moveCurrentBlock("DOWN");
				model.printPlayfield();
				map = model.playfieldAsString();
				Modell.wait(Modell.fallTime);	// Warte eine Sekunde bevor der Block weiter fällt, falls möglich.
			}
			//model.printPlayfield();
			
			//Übertrage den currentBlock aufs Spielfeld
			model.insertCurrentBlockAsPlayfieldBlock();
			map = model.playfieldAsString();
			model.printPlayfield();
			
			
			List<Integer> completedRows = model.checkRow();
			if(completedRows.isEmpty() == false) {
				model.clearRows(completedRows);
				model.printPlayfield();
				
			}
			
			// TODO: Checken, ob in den Buffer reingesetzt worden ist, dann wird das Spiel beendet.
			if (model.checkGameEnd() == true) {
				System.out.println("Spiel vorbei!!");
				run = false;
			}
			else {
				Modell.wait(2000); // Warte zwei Sekunden bevor der nächste Block fällt.
			}
		}
	}
}

