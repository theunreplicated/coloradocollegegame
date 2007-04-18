
public interface Representation
{
	public void initialize(World w, ClientInput clientInput, Logger myLogger);
	public void changeView();
	public void changeView(int to);
	public void update();
	public void updateLocation(GameElement ge);
	public void updatePresence(GameElement ge);
}
