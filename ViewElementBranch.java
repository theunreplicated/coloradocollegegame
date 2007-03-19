//ViewElementBranch.java
//@author Joel Ross

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/***
 A type of ElementBranch that defines a J3D branch for a view--a camera.
 This class can define a Client's GameElement which is either a first-person
 camera, a third-person element with a following camera, or a third-person
 element with a static camera (or hopefully any kind of camera we want).
 
 Currently the methods are set to define a first-person camera.
***/

public class ViewElementBranch implements ElementBranch
{
	//member variables
	private ViewingPlatform camera;
	private TransformGroup coord; //transformed coordinates for this branch
	
	private GameElementBranch avatar; //if we want one

	private static boolean STATIC_VIEW = false; //reset this for quick view change in testing
						    //Remove/change these checks once we have a switch working

	//constructor
	public ViewElementBranch(GameElement e)
	{
		camera = new ViewingPlatform();
		coord = camera.getMultiTransformGroup().getTransformGroup(0); //coord is the VP's transformation
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); //allow us to read the transformation at runtime
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //allow us to change the transformation at runtime
		Transform3D posi = new Transform3D(new Quat4f(e.getFacing()),new Vector3f(e.getPosition()),1); //set us to the element's transform
		coord.setTransform(posi); //set our transform group to the default position
	
		if(STATIC_VIEW)
			avatar = new GameElementBranch(e); //the avatar object for this view
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
		if(!STATIC_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setTranslation(new Vector3f(p)); //set the new translation
			coord.setTransform(t); //set as our new state
		}
		else
			avatar.setTranslation(p);
	}
		
	public void setRotation(float[] f)
	{
		if(!STATIC_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setRotation(new Quat4f(f)); //set our current rotation
			coord.setTransform(t);
		}
		else
			avatar.setRotation(f);
	}
	
	public void setTransform(float[] p, float[] f)
	{
		if(!STATIC_VIEW)
		{
			Transform3D t = new Transform3D(new Quat4f(f), new Vector3f(p), 1);
			coord.setTransform(t);
		}
		else
			avatar.setTransform(p,f);
	}


}