//ElementBranch.java
//@author Joel Ross

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Appearance;

/***
 An interface for J3D Representation versions of Elements (of all kinds... stuff, really).
***/

public interface ElementBranch
{
	public BranchGroup getBranchScene();
	public void detach();
	public void setTranslation(float[] position);
	public void setRotation(float[] facing);
	public void setScale(float[] scale);
	public void setTransform(float[] position, float[] facing, float[] scale);
	//public Appearance getAppearance();
	//public void setAppearance(Appearance a);
}
