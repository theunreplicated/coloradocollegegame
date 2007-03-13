//ViewElementBranch.java
//@author Joel Ross

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/***
 A type of ElementBranch that can also be used to set up a ViewingPlatform object.
 This class can define a Client's GameElement which is either a first-person
 camera, a third-person element with a following camera, or a third-person
 element with a static camera (or hopefully any kind of camera we want).
***/

public class ViewElementBranch implements ElementBranch
{
	//member variables
	private ViewingPlatform camera;
	private TransformGroup coord; //transformed coordinates for this branch
	
	private GameElementBranch avatar; //if we want one

	//constructor
	public ViewElementBranch(GameElement e)
	{
		camera = new ViewingPlatform();
		coord = camera.getMultiTransformGroup().getTransformGroup(0); //coord is the VP's transformation
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); //allow us to read the transformation at runtime
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //allow us to change the transformation at runtime
		Transform3D posi = new Transform3D(new Quat4f(e.getFacing()),new Vector3f(e.getPosition()),1); //make the new coordinate system
		coord.setTransform(posi); //set our transform group to the default position
	
		avatar = new GameElementBranch(e);
	}

	//a method to fetch the ViewingPlatform in order to construct the view branch
	public ViewingPlatform getViewingPlatform()
	{
		return camera;
	}

	public GameElementBranch getAvatar()
	{
		return avatar;	
	}

	public BranchGroup getBranchScene()
	{
		//what should we be returning?
		return null;
	}
	
	public void detach()
	{
		//how do we delete a camera?
	}
	
	public void setTranslation(float[] p)
	{
		/**/
		Transform3D t = new Transform3D(); //a new Transform
		coord.getTransform(t); //fill the transform with our current settings
		t.setTranslation(new Vector3f(p)); //set the new translation
		coord.setTransform(t); //set as our new state
		/**/
		//avatar.setTranslation(p);
	}
		
	public void setRotation(float[] f)
	{
		/**/
		Transform3D t = new Transform3D(); //a new Transform
		coord.getTransform(t); //fill the transform with our current settings
		t.setRotation(new Quat4f(f)); //set our current rotation
		coord.setTransform(t);
		/**/
		//avatar.setRotation(f);
	}
	
	public void setTransform(float[] p, float[] f)
	{
		/**/
		Transform3D t = new Transform3D(new Quat4f(f), new Vector3f(p), 1);
		coord.setTransform(t);
		/**/
		//avatar.setTransform(p,f);
	}


}