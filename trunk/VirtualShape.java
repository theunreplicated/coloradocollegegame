public interface VirtualShape
{
	public float[] getBoundingBox();
	public void scale( double[] _factors );
	public float[] getPosition();
	public float[] getFacing();
}
