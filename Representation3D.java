//Representation3D.java
//@author Joel Ross

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.*;
import javax.media.j3d.*;
import javax.vecmath.*;

/***
 A 3D Representation of the game using the Java3D API
***/

public class Representation3D extends Applet implements Representation
{
	//a hashmap for converting between the World and the Java3D tree.
	HashMap<GameElement,ElementBranch> elementsToNodes = new HashMap<GameElement,ElementBranch>();
	
	GameElement elementStart; //for checking the list
	BranchGroup scene; //the root of the scene--lets us add more elements
	Canvas3D canvas3D;
	
	//constructor
	public Representation3D(Client _client)
	{
		setLayout(new BorderLayout()); //set the Applet's layout
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration(); //how does SimpleUniverse want to draw stuff?
		canvas3D = new Canvas3D(config); //make a new canvas, as SimpleUniverse likes it
		add("Center",canvas3D); //add the canvas to the Applet
		Viewer viewer = new Viewer(canvas3D); //a Viewer to go with the canvas

		//ClientInput stuff
		ClientInput ci = _client.getClientInput();
		canvas3D.setFocusable(true);
		canvas3D.addMouseListener(ci);
		canvas3D.addKeyListener(ci);

		BranchGroup superRoot = new BranchGroup(); //the ultimate root of the entire scene. Created here so we can add stuff later	

		elementStart = _client.getWorldElements(); //get the Elements to start building the tree
		elementStart.attribute("isClient", true); //mark the first element as the Client (Representation-dependent attribute)
		ViewingPlatform vp = createCamera(elementStart, superRoot); //build the camera FIRST
		
		/*tie the lighting to the camera, so that objects are always lit as if from the front.*/
		TransformGroup vpt = vp.getMultiTransformGroup().getTransformGroup(0);
		BranchGroup candleStick = new BranchGroup();
		addDefaultLights(candleStick);
		vpt.addChild(candleStick);
		//addDefaultLights(superRoot); //add default lighting to world--doesn't move

		scene = createSceneGraph(elementStart); //initialize the scene based on the Client's world
		scene.setCapability(Group.ALLOW_CHILDREN_WRITE); //let us modify the children at runtime
		scene.setCapability(Group.ALLOW_CHILDREN_EXTEND); //allow us to add more Elements to the scene during runtime
		superRoot.addChild(scene); //add the scene to the tree


		SimpleUniverse simpleU = new SimpleUniverse(vp, viewer);
		//simpleU.getViewingPlatform().setNominalViewingTransform(); //set default eye's locathe Eye's location
		simpleU.addBranchGraph(createBackground()); //set the background
		
		superRoot.compile(); //let Java3D optimize the tree
		simpleU.addBranchGraph(superRoot); //add the scene to the tree. THIS ALSO TELLS IT TO BEGIN RENDERING!

		//a thread to notify us when something has changed
		RepresentationListener rl = new RepresentationListener(this, elementStart, _client.getLogger());
		rl.start();
	}
	
	//creates a "view" or camera based on the given element
	//this will probably need to be argument based depending on what kind of camera we want
	//Also, this MUST be called BEFORE createSceneGraph() (I could probably make it explicit, but it doesn't seem as nice)
	public ViewingPlatform createCamera(GameElement e, BranchGroup vscene)
	{
		//now create a ViewGameElementBranch version
		ViewElementBranch veb = new ViewElementBranch(e);
		elementsToNodes.put(e,veb); //add it to the hashmap!
//MAYBE HAVE THE AVATAR ATTACHED TO THE VIEW BRANCH? SPECIFIED IN VEB?
		if(veb.getViewMode() != ViewElementBranch.FIRST_PERSON_VIEW) //if we're not in FPV
			vscene.addChild(veb.getAvatar().getBranchScene()); //add the avatar to the scene graph.
		
		return veb.getViewingPlatform();	
	}

	//create the bulk of the Java3D tree
	public BranchGroup createSceneGraph(GameElement e)
	{
		BranchGroup gscene = new BranchGroup(); //A root node for the bulk of the scene

		GameElement first = e; //for looping
		do
		{
			if(!elementsToNodes.containsKey(e)) //check that we haven't already added the element
			{			
				GameElementBranch bg = new GameElementBranch(e); //make a new branch for the element
				elementsToNodes.put(e,bg); //make a conversion entry so we can find the branch later
				
				gscene.addChild(bg.getBranchScene()); //add the branch to the root.
			}
			e = e.next; //loop
		} while(e != first);
	
		//Representation-level objects
		gscene.addChild(createGrid());
		
		return gscene; //return the branch
	}
	
	//create and return a background for the world
	public BranchGroup createBackground()
	{
		BranchGroup root = new BranchGroup(); // a root node for the background
		
		Background back = new Background(0.0f,0.0f,0.0f); //a background node! Set the background's color here	
		back.setApplicationBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0), 200.0)); //set the bounds of the area to make a background (very very big)
	
		root.addChild(back); //add the background to the branch
		root.compile(); //just in cases
		return root; //return the branch
	}

	//add default lighting to the BranchGroup
	//currently light bounds are HUGE for testing
	public void addDefaultLights(BranchGroup bg)
	{
		DirectionalLight keyLight = new DirectionalLight(true,
			new Color3f(1.0f, 1.0f, 1.0f), 
			new Vector3f(.433f, -.5f, -.75f));
		keyLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),200.0));
		bg.addChild(keyLight);
		
		DirectionalLight fillLight = new DirectionalLight(true,
			new Color3f(0.125f, 0.125f, 0.125f), 
			new Vector3f(-.259f, .25f, -.933f));
		fillLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),200.0));
		bg.addChild(fillLight);		

		DirectionalLight backLight = new DirectionalLight(true,
			new Color3f(1.0f, 1.0f, 1.0f), 
			new Vector3f(0.0f, -0.259f, .966f));
		backLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0),200.0));
		bg.addChild(backLight);			
	
	}

	//creates a Representation-level grid to display as the ground. For testing mostly
	public Shape3D createGrid()
	{
		LineArray grid = new LineArray(88, LineArray.COORDINATES | LineArray.COLOR_3);
		
		int index = 0;
		for(int i=-10; i<=10; i++)
		{
			grid.setCoordinate(index, new Point3f(-11.0f, -1.0f, i));
			index++;
			grid.setCoordinate(index, new Point3f( 11.0f, -1.0f, i));
			index++; 
		}
		for(int i=-10; i<=10; i++)
		{
			grid.setCoordinate(index, new Point3f(i, -1.0f,-11.0f));
			index++;
			grid.setCoordinate(index, new Point3f(i, -1.0f, 11.0f));
			index++; 
		}
		
		Color3f green = new Color3f(0.0f, 1.0f, 0.0f);
		for(int i=0; i<88; i++)
			grid.setColor(i, green);	
		
		Shape3D gridShape = new Shape3D(grid);
		return gridShape;
	}

	//an update method
	public void update()
	{
		//System.out.println("In update method:");
		
		//Run through the HashMap to check if we need to trim the tree	
		Iterator<GameElement> i = elementsToNodes.keySet().iterator(); //for looping the list
		GameElement e;
		while(i.hasNext())
		{
			e = i.next(); //get the next element

			//System.out.println(elementsToNodes.get(e));
						
			if(e.next == null && e.prev == null) //if isn't attached to World's list
			{
				//delete Branch
				ElementBranch bg = elementsToNodes.get(e); //fetch the branch
				bg.detach(); //remove the branch from the tree
				i.remove(); //remove the current element from the hashmap via the iterator
			}
		}	 
		
		//run through the World's list of elements to see if anything needs changing
		ElementBranch bg;
		e = elementStart;
		do
		{
			if(e.changed)
			{
				bg = elementsToNodes.get(e); //get the corresponding GameElementBranch
				
				if(bg == null) //if element wasn't in the list
				{
					//add Branch
					GameElementBranch nbg = new GameElementBranch(e); //make a new branch for the element
					elementsToNodes.put(e,nbg); //make a conversion entry so we can find the branch later
					scene.addChild(nbg.getBranchScene()); //add the branch to the scene
				}
				else //otherwise
				{
					//change branch
					bg.setTransform(e.getPosition(),e.getFacing());
					
					if(e.attribute("color") != null)
					{
						Appearance a = bg.getAppearance(); //get old appearance
						if(a != null)
						{
							Material mat = a.getMaterial(); //get old material
							int c = (Integer)e.attribute("color");
							mat.setDiffuseColor(((c>>16)&0xff)/255f, ((c>>8)&0xff)/255f, (c&0xff)/255f); //, ((c>>24)&0xff)/255f);
							a.setMaterial(mat); //reset our material
							bg.setAppearance(a); //set the new appearance
						}
					}
				}
				
				e.changed = false; //mark as unchanged
			}
						
			e = e.next;
		} while(e!=elementStart);
	} //update

	//fetches the canvas we're drawing this on, in case we want to embed in a different window
	public Canvas3D getCanvas()
	{
		return canvas3D;	
	}

	//this looks familiar...
	public static void main(String[] args)
	{
		Client myClient = Client.initialize(args); //create a Client for the game

		Frame f = new MainFrame(new Representation3D(myClient),300,300); //run the applet inside a Frame
	} 
}
