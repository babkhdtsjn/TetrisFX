package application;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;

// Für Popup
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

public class Ansicht extends Application{
	private GridPane root = new GridPane();
    
	private Scene scene;
	
	private Stage primaryStage; 
	
	static volatile Steuerung controll;
	
	//Objekt für eine schönere Bildschirmausgabe
	private Bildrenderer renderer;
	
	
	// Attribute die die Bewegungsrichtung des currentBlock beeinflussen.
	static volatile boolean leftDirection = false;
	static volatile boolean rightDirection = false;
	static volatile boolean downDirection = false;
	static volatile boolean upDirection = false;
	
	// Attribut, damit sichergestellt wird, dass der Block bei einem Tastendruck auch nur einmal gedreht wird.
	static volatile boolean turned = false;
	
	@Override
	public void start(Stage primaryStage) {
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
		
		try {
			this.root.setPadding(new Insets(0));
		    this.root.setHgap(0);
		    this.root.setVgap(0);
		    
		    renderer = new Bildrenderer(root);
			 // Initialisierung des Fensters
			this.primaryStage = primaryStage;
			Scene scene = new Scene(root,530,700);  
			  
			this.primaryStage.setScene(scene);  
			this.primaryStage.setTitle("Tetris"); 
			this.primaryStage.show();
			
			 
			Thread threadUpdate = new Thread(new Runnable() {

		            @Override
		            public void run() {
		                Runnable updater = new Runnable() {

		                    @Override
		                    public void run() {
		                    	// Workaround, muss so sein.
		                    	//Steuerung tempControll = controll;
		                    	if(controll != null) {
		                    		renderer.updatePlayfieldView(controll.model.getWholePlayfield());
		                    		renderer.updateNextBlockView(controll.model.getNextBlock());
		                    	}
		                    	updateGuiScore();
		                    }
		                };

		                while (true) {
		                    try {
		                        Thread.sleep(34);
		                    } catch (InterruptedException ex) {
		                    }
		                    // UI update is run on the Application thread
		                    Platform.runLater(updater);
		                    
		                }
		            }

		        });
			 threadUpdate.setDaemon(true);
		     threadUpdate.start();

		     // Steuerung der Blockbewegung durch Nutzer erfassen und managen.
		     scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
		            @Override
		            public void handle(KeyEvent event) {
		                switch (event.getCode()) {
		                    case LEFT:  
		                    	if(Ansicht.getLeftDirection() == false) {
		                    		Ansicht.setLeftDirection(true); 
		                    		//System.out.println("Linke Pfeiltaste gedrückt!!");
		                    	}
		                    	break;
		                    case RIGHT: 
		                    	if(Ansicht.getRightDirection() == false) {
		                    		Ansicht.setRightDirection(true);
		                    		//System.out.println("Rechte Pfeiltaste gedrückt!!");
		                    	}
		                    	break;
		                    case DOWN: 
		                    	if(Ansicht.getDownDirection() == false) {
		                    		Ansicht.setDownDirection(true);
		                    		//System.out.println("Untere Pfeiltaste gedrückt!!");
		                    	}
		                    	break;
		                    case UP:
		                    	if(Ansicht.getUpDirection() == false) {
		                    		Ansicht.setUpDirection(true);
		                    		//System.out.println("Obere Pfeiltaste gedrückt/Einmal Rotieren!!");
		                    		Modell.wait(Steuerung.WAIT_ROTATE_TIME);
		                    	}
		                    	break;
		                    	
		                    default:
							break;
		                }
		               
		            }
		        });
		     scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
		            @Override
		            public void handle(KeyEvent event) {
		                switch (event.getCode()) {
		                case LEFT:  
	                    	if(Ansicht.getLeftDirection() == true) {
	                    		Ansicht.setLeftDirection(false); 
	                    		//System.out.println("Linke Pfeiltaste losgelassen!!");
	                    	}
	                    	break;
	                    case RIGHT: 
	                    	if(Ansicht.getRightDirection() == true) {
	                    		Ansicht.setRightDirection(false);
	                    		//System.out.println("Rechte Pfeiltaste losgelassen!!");
	                    	}
	                    	break;
	                    case DOWN: 
	                    	if(Ansicht.getDownDirection() == true) {
	                    		Ansicht.setDownDirection(false);
	                    		//System.out.println("Untere Pfeiltaste losgelassen!!");
	                    	}
	                    	break;
	                    case UP: 
	                    	if(Ansicht.getUpDirection() == true) {
	                    		Ansicht.setDownDirection(false);
	                    		Ansicht.setTurned(false);
	                    		//System.out.println("Obere Pfeiltaste losgelassen!!");
	                    	}
	                    	break;
	                    
	                    default:
						break;
		                }
		            }
		        });
			 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public Ansicht() {}
	
	public void updateGuiScore() {
		this.renderer.setScoreText(String.valueOf(this.controll.model.getScore()));
	}
	
	public void gameOverPopUp() {
        Platform.runLater(() -> {
            // Erstelle das Popup-Fenster
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Spiel vorbei");

            Label label = new Label("Das Spiel ist vorbei! Punktzahl: " + this.controll.model.getScore());

            Button restartButton = new Button("Neustarten");
            restartButton.setOnAction(e -> {
                // Füge hier den Code für das Neustarten hinzu
                System.out.println("!!!!!!!!!!!!!------------------------Neustarten");
                popupStage.close();
                this.controll.model.resetGame();
            });

            Button exitButton = new Button("Beenden");
            exitButton.setOnAction(e -> {
                // Füge hier den Code für das Beenden hinzu
                System.out.println("Beenden");
                Platform.exit();
                System.exit(0);
            });
            
            HBox buttonBox = new HBox(10, restartButton, exitButton);
            buttonBox.setAlignment(Pos.CENTER);

            VBox layout = new VBox(10, label, buttonBox);
            layout.setAlignment(Pos.CENTER);

            Scene scene = new Scene(layout, 300, 150);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        });
    }
	
	static synchronized boolean getLeftDirection() {
		return Ansicht.leftDirection;
	}
	
	static synchronized boolean getRightDirection() {
		return Ansicht.rightDirection;
	}
	
	static synchronized boolean getDownDirection() {
		return Ansicht.downDirection;
	}
	
	static synchronized boolean getUpDirection() {
		return Ansicht.upDirection;
	}
	
	static synchronized boolean getTurned() {
		return Ansicht.turned;
	}
	
	static synchronized void setLeftDirection(boolean isLeft) {
		Ansicht.leftDirection = isLeft;
	}
	
	static synchronized void setRightDirection(boolean isRight) {
		Ansicht.rightDirection = isRight;	
	}
	
	static synchronized void setDownDirection(boolean isDown) {
		Ansicht.downDirection = isDown;
	}
	
	static synchronized void setUpDirection(boolean isUp) {
		Ansicht.upDirection = isUp;
	}
	
	static synchronized void setTurned(boolean isTurned) {
		Ansicht.turned = isTurned;
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	public void setControll(Steuerung newControll) {
		Ansicht.controll = newControll;
	}
		
	public Steuerung getController() {
		return Ansicht.controll;
	}	
	
	public void wait(int ms)
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
}
