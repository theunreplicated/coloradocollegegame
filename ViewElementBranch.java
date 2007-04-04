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
	public static final int FIRST_PERSON_VIEW = 0;
	public static final int OFFSET_VIEW = 1;
	public static final int FOLLOWING_VIEW = 2;
	public static final int STATIC_VIEW = 3;

	public static final float[] OFFSET = new float[] {0.0f, 1.0f, 7.5f};

	//member variables
	private ViewingPlatform camera;
	private TransformGroup coord; //transformed coordinates for this branch
	private GameElementBranch avatar; //if we want one
	
	private int viewMode = 1;

	//constructor
	public ViewElementBranch(GameElement e)
	{
		camera = new ViewingPlatform();
		coord = camera.getMultiTransformGroup().getTransformGroup(0); //coord is the VP's transformation
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_READ); //allow us to read the transformation at runtime
		coord.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE); //allow us to change the transformation at runtime

		Transform3D posi;
		if(viewMode == FIRST_PERSON_VIEW)
		{
			posi = new Transform3D(new Quat4f(e.getFacing()), new Vector3f(e.getPosition()), 1); //set us to the element's transform
		}
		else if(viewMode == OFFSET_VIEW)
		{
			Vector3f p = new Vector3f(e.getPosition());
			p.add(new Vector3f(OFFSET));
			posi = new Transform3D(new Quat4f(e.getFacing()), p, 1);
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

	public int getViewMode()
	{
		return viewMode;
	}
	
	public GameElementBranch getAvatar()
	{
		if(viewMode != FIRST_PERSON_VIEW)
			return avatar;	
		else
			return null;
	}

	public void changeView(int to)
	{
		//Implement this method!
		System.out.println("Change view to "+to); 
	}
	
	public void setTranslation(float[] p)
	{
		if(viewMode == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setTranslation(new Vector3f(p)); //set the new translation
			coord.setTransform(t); //set as our new state
		}
		else if(viewMode == OFFSET_VIEW)
		{
			//Do what here? We NEED facing and position to do an offset!

			/*
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			Vector3f pp = new Vector3f(p);
			pp.add(new Vector3f(Quaternions.rotatePoint(OFFSET,f)));
			t.setTranslation(pp); //set the new translation
			coord.setTransform(t); //set as our new state
			*/
			
			avatar.setTranslation(p);
		}
		else
			avatar.setTranslation(p);
	}
		
	public void setRotation(float[] f)
	{
		if(viewMode == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setRotation(new Quat4f(f)); //set our current rotation
			coord.setTransform(t);
		}
		else if(viewMode == OFFSET_VIEW)
		{
			//Do what here? We NEED facing and position to do an offset!
			
			/*
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			Vector3f pp = new Vector3f(p);
			pp.add(new Vector3f(Quaternions.rotatePoint(OFFSET,f)));
			t.setTranslation(pp); //set the new translation
			coord.setTransform(t); //set as our new state
			*/

			avatar.setRotation(f);
		}
		else
			avatar.setRotation(f);
	}

	public void setScale(float[] s)
	{
		if(viewMode == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setScale(new Vector3d((double)s[0], (double)s[1], (double)s[2])); //set our current scale
			coord.setTransform(t);
		}
		else if(viewMode == OFFSET_VIEW)
		{
			//should we have the offset be based on the scale? I think that's what it does atm
			
			Transform3D t = new Transform3D(); //a new Transform
			coord.getTransform(t); //fill the transform with our current settings
			t.setScale(new Vector3d((double)s[0], (double)s[1], (double)s[2])); //set our current scale
			coord.setTransform(t);
			
			avatar.setScale(s);
		}
		else
			avatar.setScale(s);
	}
	
	public void setTransform(float[] p, float[] f, float[] s)
	{
		if(viewMode == FIRST_PERSON_VIEW)
		{
			Transform3D t = new Transform3D(new Quat4f(f), new Vector3f(p), 1);
			t.setScale(new Vector3d((double)s[0], (double)s[1], (double)s[2])); //set our current scale
			coord.setTransform(t);
		}
		else if(viewMode == OFFSET_VIEW)
		{
			Vector3f pp = new Vector3f(p);
			pp.add(new Vector3f(Quaternions.rotatePoint(OFFSET,f)));
			Transform3D t = new Transform3D(new Quat4f(f), pp, 1);
			t.setScale(new Vector3d((double)s[0], (double)s[1], (double)s[2])); //set our current scale
			coord.setTransform(t);
	
			avatar.setTransform(p,f,s);
		}
		else
			avatar.setTransform(p,f,s);
	}

	/**
	 **Methods that get passed along to the avatar
	 **/

	public BranchGroup getBranchScene()
	{
		return avatar.getBranchScene();
	}
	
	public void detach()
	{
		avatar.detach();
	}
	
/*	
	public void setAppearance(Appearance a)
	{
		avatar.setAppearance(a);	
	}

	public Appearance getAppearance()
	{
		return avatar.getAppearance();
	}
*/

}