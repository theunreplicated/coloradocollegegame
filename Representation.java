
public interface Representation
{
	public void changeView();
	public void changeView(int to);
	public void update();
	public void initialize(World w, ClientInput clientInput, Logger myLogger);
}
