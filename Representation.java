import java.awt.Component;

public interface Representation
{
	public void initialize(World w, Logger myLogger);
	public Component getComponent();
	public void changeView();
	public void changeView(int to);
	public void updateLocation(GameElement ge);
	public void updatePresence(GameElement ge);
}
