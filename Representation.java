import java.awt.Component;

public interface Representation
{
	public void initialize(World w, Logger myLogger);
	public Component getComponent();
	public void changeView();
	public void changeView(int to);
	public void adjustCamera(float[] translation, float[] rotation);
	public void setCamera(float[] translation, float[] rotation);
	public float[] getCameraPosition();
	public float[] getCameraFacing();
	public void updateLocation(GameElement ge);
	public void updatePresence(GameElement ge);
	public void displayMessage(String message, int flags);
}
