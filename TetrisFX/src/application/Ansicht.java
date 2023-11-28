package application;
import javafx.application.Platform;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.text.Text; 
import javafx.geometry.Insets;
//import javafx.scene.text.Font;
import javafx.scene.input.KeyEvent;
import javafx.event.EventHandler;
//import javafx.stage.Stage;
//import javafx.event.*;
//import javafx.scene.robot.*;
//import javafx.scene.input.*;



public class Ansicht extends Application{
	private Text playfieldText = new Text();
	private Text scoreText = new Text();
	private Text nextBlockText = new Text();
	private GridPane root = new GridPane();
    
	private Scene scene; //= new Scene(root,300,400);
	
	private Stage primaryStage; 
	//private String playfield;
		
	// Attribute die die Bewegungsrichtung des currentBlock beeinflussen.
	static volatile boolean leftDirection = false;
	static volatile boolean rightDirection = false;
	static volatile boolean downDirection = false;
	static volatile boolean upDirection = false;
	
	@Override
	public void start(Stage primaryStage) {
		/*primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        System.exit(0);
		    }
		});
		*/
		try {
			this.root.setPadding(new Insets(20));
		    this.root.setHgap(25);
		    this.root.setVgap(15);
		    this.root.add(this.playfieldText, 0,0,2,5);
		    this.root.add(this.scoreText, 3, 1);
		    this.root.add(this.nextBlockText, 3, 0);
			 // Initialisierung des Fensters
			this.primaryStage = primaryStage;
			// this.text = new Text();
			 // this.text.setFont(Font.font("Courier"));
			this.playfieldText.setStyle("-fx-font: normal bold 22px 'Courier' "); 
			this.nextBlockText.setStyle("-fx-font: normal bold 22px 'Courier' ");
			this.scoreText.setStyle("-fx-font: normal bold 25px 'Courier' "); 
			 
			 
			 //this.text.setFont("Courier", FontWeight.BOLD, FontPosture.REGULAR, 20);
			 this.playfieldText.setText("Tetris Start!"); 
			 this.nextBlockText.setText("Template!");
			 this.scoreText.setText(String.valueOf(Modell.getScore()));
			 // this.root = new StackPane();  
			 Scene scene = new Scene(root,800,800);  
			 //this.root.getChildren().add(playfieldText);  
			 this.primaryStage.setScene(scene);  
			 this.primaryStage.setTitle("Tetris");  
			 this.primaryStage.show();
			 
			 
			 Thread threadUpdate = new Thread(new Runnable() {

		            @Override
		            public void run() {
		                Runnable updater = new Runnable() {

		                    @Override
		                    public void run() {
		                    	if(Steuerung.map != null) {
		                    		//System.out.println(Steuerung.map);
		                    		updateGuiPlayfield();
		                    	}
		                    }
		                };

		                while (true) {
		                    try {
		                        Thread.sleep(34);	// In der Sekunde wird die GUI 30 mal geuodatet (30fps);
		                    } catch (InterruptedException ex) {
		                    }

		                    // UI update is run on the Application thread
		                    Platform.runLater(updater);
		                    
		                }
		            }

		        });
			 threadUpdate.setDaemon(true);
		     threadUpdate.start();
			 /*
			 Thread threadUpdate = new Thread(() -> {
				    while (true) {
				        // Führe Update-Operationen durch

				        Platform.runLater(() -> {
				            // Aktualisiere die Benutzeroberfläche
				        	if(Steuerung.map != null) {
	                    		//System.out.println(Steuerung.map);
	                    		updateGuiPlayfield();
	                    	}
				        });

				        try {
				            Thread.sleep(34); // Füge eine Verzögerung ein, um die CPU-Last zu reduzieren
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
				    }
				});
				*/
		     
		     scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
		            @Override
		            public void handle(KeyEvent event) {
		                switch (event.getCode()) {
		                    case LEFT:  
		                    	if(Ansicht.getLeftDirection() == false) {
		                    		Ansicht.setLeftDirection(true); 
		                    		System.out.println("Linke Pfeiltaste gedrückt!!");
		                    	}
		                    	break;
		                    case RIGHT: 
		                    	if(Ansicht.getRightDirection() == false) {
		                    		Ansicht.setRightDirection(true);
		                    		System.out.println("Rechte Pfeiltaste gedrückt!!");
		                    	}
		                    	break;
		                    case DOWN: 
		                    	if(Ansicht.getDownDirection() == false) {
		                    		Ansicht.setDownDirection(true);
		                    		System.out.println("Untere Pfeiltaste gedrückt!!");
		                    	}
		                    	break;
		                    case UP:
		                    	if(Ansicht.getUpDirection() == false) {
		                    		Ansicht.setUpDirection(true);
		                    		System.out.println("Obere Pfeiltaste gedrückt/Einmal Rotieren!!");
		                    		Modell.wait(Steuerung.WAIT_ROTATE_TIME);
		                    		Ansicht.setUpDirection(false);
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
	                    		System.out.println("Linke Pfeiltaste losgelassen!!");
	                    	}
	                    	break;
	                    case RIGHT: 
	                    	if(Ansicht.getRightDirection() == true) {
	                    		Ansicht.setRightDirection(false);
	                    		System.out.println("Rechte Pfeiltaste losgelassen!!");
	                    	}
	                    	break;
	                    case DOWN: 
	                    	if(Ansicht.getDownDirection() == true) {
	                    		Ansicht.setDownDirection(false);
	                    		System.out.println("Untere Pfeiltaste losgelassen!!");
	                    	}
	                    	break;
	                    
	                    default:
						break;
		                    /*case LEFT: 
		                    	if(leftDirection == true) {
		                    	leftDirection = false; 
		                    	System.out.println("Linke Pfeiltaste losgelassen!!");
		                    	}
		                    	break;
		                    case RIGHT: 
		                    	if(rightDirection == true) {
		                    	rightDirection = false; 
		                    	System.out.println("Rechte Pfeiltaste losgelassen!!");
		                    	}

		                    	break;
		                    case DOWN: 
		                    	if(Ansicht.downDirection == true) {
		                    	Ansicht.downDirection = false;
		                    	System.out.println("Untere Pfeiltaste losgelassen!!");
		                    	}
		                    break;
		                    // case SHIFT: running = true; break;
							default:
								break;
								*/
		                }
		            }
		        });
		     
		    /* Thread threadUserInput = new Thread(new Runnable() {

		            @Override
		            public void run() {
		                Runnable inputChecker = new Runnable() {

		                    @Override
		                    public void run() {
		                    	// Überprüfe ob der User eine Knopf gedrückt hat.
		                    	if(leftDirection == true) {
		                    		
		                    	}
		                    	else if(rightDirection == true) {
		                    		
		                    	}
		                    	
		                    }
		                };

		                
		                    try {
		                        Thread.sleep(1000);
		                    } catch (InterruptedException ex) {
		                  

		                    // UI update is run on the Application thread
		                    Platform.runLater(inputChecker);
		                    
		                }
		            }

		        });
		     threadUserInput.setDaemon(true);
		     threadUserInput.start();
		     */
			 
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public Ansicht() {
		/*this.text = new Text();
		this.root = new StackPane();
		this.scene = new Scene(root,300,400);
		*/
		
		//
		//root.getChildren().add(text);
		
		
		
	}
	
	public void createAndShowGui(Stage primaryStage) {
		// text.setText(playfield);
		//String playfield
		
		/*
		this.root.getChildren().add(text);    
		this.scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.setTitle("Tetris");
		primaryStage.show();
		/*
		scene.setOnKeyPressed(event -> {
            String codeString = event.getCode().toString();
            //text.setText("Pressed Key: "+ codeString);  
        });
		scene.setOnKeyReleased(event -> 
		text.setText("released Key")
    );
    */
	}
	public void updateGuiPlayfield() {	
		playfieldText.setText(Steuerung.map);
	
	}
	
	/*public void setPlayfield(String playfield) {
		this.playfield = playfield;
	}*/
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
	
	static synchronized void setLeftDirection(boolean isLeft) {
		Ansicht.leftDirection = isLeft;
		/*if(isLeft == true) {
			System.out.println("leftDirection muss jetzt true sein: " + Ansicht.leftDirection);
		}
		*/
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
	
	public Scene getScene() {
		return this.scene;
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
