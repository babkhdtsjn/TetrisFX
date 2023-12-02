package application;

public class Main{
	Steuerung controll = new Steuerung();

	public static void main(String[] args) {
		Steuerung controll = new Steuerung();
		controll.gui.setControll(controll);
		controll.startGame(args);		
	}
}
