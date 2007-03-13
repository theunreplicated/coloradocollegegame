//GameElementBranch.java
//@author Joel Ross

import javax.media.j3d.*;

/***
 An interface for J3D Representation versions of Elements (of all kinds).
***/

public interface ElementBranch
{
	public BranchGroup getBranchScene();
	public void detach();
	public void setTranslation(float[] position);
	public void setRotation(float[] facing);
	public void setTransform(float[] position, float[] facing);
}