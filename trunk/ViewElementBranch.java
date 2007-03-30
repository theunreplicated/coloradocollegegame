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
	public static final short FIRST_PERSON_VIEW = 0;
	public static final short FOLLOWING_VIEW = 1;
	public static final short STATIC_VIEW = 2;

	//member variables
	private ViewingPlatform camera;
	private TransformGroup coord; //transformed coordinates for this branch
	private GameElementBranch avatar; //if we want one
	private short view = 0;

	//constructor
	public ViewElementBranch(GameElement e)
	{
		camera = new ViewingPlatform();
		coord = camera.getMultiTransformGroup().getTransformGroup(0); //coord is the VP's transformation
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); //allow us to read the transformation at runtime
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //allow us to change the transformation at runtime

		Transform3D posi;
		if(view == FIRST_PERSON_VIEW)
		{
			posi = new Transform3D(new Quat4f(e.getFacing()), new Vector3f(e.getPosition()), 1); //set us to the element's transform
		}
		else if(view == FOLLOWING_VIEW)
		{
			//fill this in (initialization)
			
			//temp
			posi = new Transform3D(new Quat4f(Constants.DEFAULT_FACING), new Vector3f(0f,0f,5f), 1);

		}
		else
		{
			posi = new Transform3D(new Quat4f(Constants.DEFAULT_FACING), new Vector3f(0f,0f,5f), 1);
		}
		coord.setTransform(posi); //set our transform group to the default position

		avatar = new GameElementBranch(e); //the avatar object for this view
	}

	//a method to fetch the ViewingPlatform in order to construct the view branch
	public ViewingPlatform getViewingPlatform()
	{
		return camera;
	}

	public short getViewMode()
	{
		return view;
	}
	
	public GameElementBranch getAvatar()
	{
		return avatar;	
	}

	public void changeView(short to)
	{
		//Implement this method!
		System.out.println("Change view to "+to); 
	}
	
	public void setTranslation(float[] p)
	{
		if(view == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setTranslation(new Vector3f(p)); //set the new translation
			coord.setTransform(t); //set as our new state
		}
		else if(view == FOLLOWING_VIEW)
		{
			//fill this in	
		}
		else
			avatar.setTranslation(p);
	}
		
	public void setRotation(float[] f)
	{
		if(view == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setRotation(new Quat4f(f)); //set our current rotation
			coord.setTransform(t);
		}
		else if(view == FOLLOWING_VIEW)
		{
			//fill this in
		}
		else
			avatar.setRotation(f);
	}
	
	public void setTransform(float[] p, float[] f)
	{
		if(view == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(new Quat4f(f), new Vector3f(p), 1);
			coord.setTransform(t);
		}
		else if(view == FOLLOWING_VIEW)
		{
			//fill this in
		}
		else
			avatar.setTransform(p,f);
	}

	public void setAppearance(Appearance a)
	{
		avatar.setAppearance(a);	
	}

	public Appearance getAppearance()
	{
		return avatar.getAppearance();
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
}