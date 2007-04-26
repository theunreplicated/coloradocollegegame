//ElementBranch.java
//@author Joel Ross

import javax.media.j3d.BranchGroup;
//import javax.media.j3d.Appearance;//needed?

/***
 An interface for J3D Representation versions of Elements (of all kinds... stuff, really).
***/

public interface ElementBranch
{
	public BranchGroup getBranchScene();
	public void detach();
	public void setTransform(float[] position, float[] facing, float[] scale);
}
