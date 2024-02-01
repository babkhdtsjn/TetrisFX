package application;

import java.util.List;
import javafx.application.Application;

public class Steuerung {
	// Informationen über das Spielfeld und den currentBlock als String für die GUI.
	static String map;
	// Informationen über den nächsten Block für die GUI
	static String nextBlockMap;
	
	// Cooldown in ms bis der Block nach einer Rotation wieder gedreht werden kann.
	static final int WAIT_ROTATE_TIME = 100;
	
	Ansicht gui;
	Modell model;
	
	Thread guiThread;
	Thread logicThread;
	Thread inputThread;
	
	// Flags
	boolean logicThreadRun = true;
	boolean guiThreadRun = true;
	boolean inputThreadRun = true;
	
	boolean playfieldCreated = false;
	
	public Steuerung() {
		gui = new Ansicht();
		model  = new Modell();
	}
	
	public void startGame(String[] args) {
		guiThread = new Thread() {
			public void run() {
				Application.launch(Ansicht.class, args);
	
				while(guiThreadRun) {
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
		
		logicThread = new Thread() {
			public void run() {
				if(playfieldCreated == false) {
					// Setup Playfield
					model.createPlayfieldBorder();
					playfieldCreated = true;
				}
				while(logicThreadRun) {
						playLoop();
				}
				System.out.println("Ende LogikThread!");
			}	
		};
		inputThread = new Thread() {
			public void run() {
				while(inputThreadRun) {
				// Überprüfe ob eine Pfeiltaste gedrückt wurde.
				//Platform.runLater(() -> {
				if(Ansicht.getLeftDirection() == true) {
					model.moveCurrentBlock("LEFT");
					
				}
				else if(Ansicht.getRightDirection() == true) {
					model.moveCurrentBlock("RIGHT");
				}
				else if(Ansicht.getUpDirection() == true) {
					// Die Variable turned in Ansicht soll sicherstellen, dass der Block bei einem Tastenanschlag auch nur einaml gedreht wird.
					// Die Variable wird auf false zurückgesetzt wenn die Taste losgelassen wird.
					if(Ansicht.getTurned() == false) {
						Ansicht.setTurned(true);
						model.rotateCurrentBlock2();
						Modell.wait(WAIT_ROTATE_TIME);
					}
				}
		
				if(Ansicht.getDownDirection() == true && model.getFallTime() != Modell.FALL_TIME_SPEEDUP) {
					model.setFallTime(Modell.FALL_TIME_SPEEDUP);
				}
				if(Ansicht.getDownDirection() == false && model.getFallTime() != Modell.fallTimeUpdated) {
					model.setFallTime(Modell.fallTimeUpdated);
				}
				try {
		               Thread.sleep(100); // Kurze Pause, um die CPU-Last zu reduzieren
		           } catch (InterruptedException e) {
		               e.printStackTrace();
		           }
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
	
	public void endGame() {
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
	
	public void restartLogicThread() {
		logicThread = new Thread() {
			public void run() {
				if(playfieldCreated == false) {
					// Setup Playfield
					model.createPlayfieldBorder();
					playfieldCreated = true;
				}
				while(logicThreadRun) {
						playLoop();
				}
			}	
		}; 
		logicThread.start();
	}
	
	public void playLoop() {
		//boolean run = true;
		//while(run) {
			// Spielblock erschaffen.
			if(model.getNextBlock() == null) {
				model.setCurrentBlock(model.createRandomBlock());
				model.setNextBlock(model.createRandomBlock());
				Steuerung.setNextBlockMap(model.getNextBlock().printBlockAsString());
			}
			else {
				model.setCurrentBlock(model.getNextBlock());
				model.setNextBlock(model.createRandomBlock());
				Steuerung.setNextBlockMap(model.getNextBlock().printBlockAsString());
				
			}
			
			// Spielblock in Bufferzone von Spielfeld legen.
			model.putBlockInBufferzone();
			// Drucke Spielfeld mit Bufferzone.
			model.printPlayfield();
			
			map = model.playfieldAsString();
			// Überprüfe ob der Spielblock fallen kann, wenn ja lass ihn fallen
			
			
			while(model.checkNewPosition(model.calculateNewPosition("DOWN")) == true) {
				model.moveCurrentBlock("DOWN");
				model.printPlayfield();
				map = model.playfieldAsString();
				Modell.wait(Modell.getFallTime());	// Warte eine Sekunde bevor der Block weiter fällt, falls möglich.
			}
			
			//Übertrage den currentBlock aufs Spielfeld
			model.insertCurrentBlockAsPlayfieldBlock();
			map = model.playfieldAsString();
			model.printPlayfield();
			
			List<Integer> completedRows = model.checkRow();
			if(completedRows.isEmpty() == false) {
				model.clearRows(completedRows);
				model.printPlayfield();
				
			}
			// Überprüfe, ob über das Spielfeld nach oben hinaus gestapelt wurde (-> Game Over).
			if (model.checkGameEnd() == true) {
				System.out.println("Spiel vorbei!!");
				logicThreadRun = false;
				System.out.println("LogicThreadRun =  " + logicThreadRun);
				// TODO: Informiere den Spieler über das beendete Spiel und entziehe ihm die Kontrolle über den Block.
				gui.gameOverPopUp();
			}
			else {
				model.setBlockCount(model.getBlockCount() + 1);
				if(model.getBlockCount() == Modell.SPEED_UP_BLOCK_COUNT_THRESHHOLD &&
						(model.getFallTimeUpdated() - Modell.FALL_TIME_REDUCTION) >= Modell.FALL_TIME_MIN) {
					model.setFallTimeUpdated(model.getFallTimeUpdated() - Modell.FALL_TIME_REDUCTION);
					Modell.setFallTime(model.getFallTimeUpdated());
					model.setBlockCount(0);
				}
				Modell.wait(2000); // Warte zwei Sekunden bevor der nächste Block fällt.
			}
		//}
	}
	static synchronized String getMap() {
		return Steuerung.map;
	}
	
	static synchronized void setMap(String newMap) {
		Steuerung.map = newMap;
	}
	static synchronized String getNextBlockMap() {
		return Steuerung.nextBlockMap;
	}
	static synchronized void setNextBlockMap(String newNextBlockMap) {
		Steuerung.nextBlockMap = newNextBlockMap;
	}
	public synchronized void getControllToGUI() {
		this.gui.setControll(this);	
	}
	
}

