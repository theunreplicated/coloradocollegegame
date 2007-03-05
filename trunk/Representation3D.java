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
 Interaction is currently being implemented
***/

public class Representation3D extends Applet implements Representation
{
	//a hashmap for converting between the World and the Java3D tree.
	HashMap<GameElement,ElementBranch> elementsToNodes = new HashMap<GameElement,ElementBranch>();
	
	GameElement elementStart; //for checking the list
	BranchGroup scene; //the root of the scene--lets us add more elements
	
	//constructor
	public Representation3D(Client _client)
	{
		setLayout(new BorderLayout()); //set the Applet's layout
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration(); //how does SimpleUniverse want to draw stuff?
		Canvas3D canvas3D = new Canvas3D(config); //make a new canvas, as SimpleUniverse likes it
		add("Center",canvas3D); //add the canvas to the Applet

		//ClientInput stuff
		ClientInput ci = _client.getClientInput();
		canvas3D.setFocusable(true);
		canvas3D.addMouseListener(ci);
		canvas3D.addKeyListener(ci);
	
			
		elementStart = _client.getWorldElements(); //get the Elements to start building the tree
		elementStart.attribute("isClient", true); //mark the first element as the Client
			
		BranchGroup superRoot = new BranchGroup(); //the ultimate root of the entire scene. Created here so we can add stuff later	

		addDefaultLights(superRoot); //add default lighting to the entire world

		//some world-level transformations (moving the camera through transforms).
		//for testing
		Transform3D trans = new Transform3D();
		trans.setTranslation(new Vector3f(0.0f, 0.0f, -20.0f));
		TransformGroup transGroup = new TransformGroup(trans);
		superRoot.addChild(transGroup);

		scene = createSceneGraph(elementStart); //initialize the scene based on the Client's world
		scene.setCapability(Group.ALLOW_CHILDREN_EXTEND); //allow us to add more Elements to the scene during runtime
		//superRoot.addChild(scene); //add the scene to the tree
		transGroup.addChild(scene); //add the scene to the tree
		
		SimpleUniverse simpleU = new SimpleUniverse(canvas3D); //make a new SimpleUniverse object
		simpleU.getViewingPlatform().setNominalViewingTransform(); //set the Eye's location
		simpleU.addBranchGraph(createBackground()); //set the background
		
		superRoot.compile(); //let Java3D optimize the tree
		simpleU.addBranchGraph(superRoot); //add the scene to the tree. THIS ALSO TELLS IT TO BEGIN RENDERING!

		//a thread to notify us when something has changed
		RepresentationListener rl = new RepresentationListener(this, elementStart, _client.getLogger());
		rl.start();
	}

	//create the bulk of the Java3D tree
	public BranchGroup createSceneGraph(GameElement e)
	{
		BranchGroup root = new BranchGroup(); //A root node for the bulk of the scene
		root.setCapability(Group.ALLOW_CHILDREN_WRITE); //let us modify the children at runtime

		GameElement first = e; //for looping
		do
		{
			ElementBranch bg = new ElementBranch(e); //make a new branch for the element
			elementsToNodes.put(e,bg); //make a conversion entry so we can find the branch later
				
			root.addChild(bg.getBranchScene()); //add the branch to the root.
			
			e = e.next; //loop
		} while(e != first);
	
		return root; //return the branch
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
	public void addDefaultLights(BranchGroup bg)
	{
		//currently Light Bounds are HUGE for testing

		AmbientLight amLight = new AmbientLight(); //ambient light
		amLight.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0), 200.0)); //set bounds of what is lit
		bg.addChild(amLight); //add light to scene

		DirectionalLight dirLight1 = new DirectionalLight(); //directional light
		dirLight1.setInfluencingBounds(new BoundingSphere(new Point3d(0.0,0.0,0.0), 200.0));
		dirLight1.setColor(new Color3f(1.0f, 1.0f, 1.0f)); //color of the light
		dirLight1.setDirection(new Vector3f(-1.0f, -0.5f, -1.0f)); //direction of the light
		bg.addChild(dirLight1); //add light to scene
	}

	//an update method
	public void update()
	{
		//System.out.println("in update method:");
		
		//Run through the HashMap to check if any of the current elements have changed	
		Iterator<GameElement> i = elementsToNodes.keySet().iterator(); //for looping the list
		GameElement e;
		while(i.hasNext())
		{
			e = i.next(); //get the next element
			
			if(e.changed)
			{
				ElementBranch bg = elementsToNodes.get(e); //fetch the branch
				
				if(e.next == null && e.prev == null) //if isn't attached to World's list
				{
					//delete Branch
					bg.detach(); //remove the branch from the tree
					i.remove(); //remove the current element from the hashmap via the iterator
				}
				else //otherwise
				{
					//change branch
					bg.setTranslation(e.position); //currently the only changes are position based			
				} 
				
				e.changed = false; //mark as changed
			}
		}	 
		
		//run through the World's list of elements to see there are any we don't have in the HashMap
		e = elementStart;
		do
		{
			if(!elementsToNodes.containsKey(e))
			{		
				System.out.println("in add");
				//add Branch
				ElementBranch nbg = new ElementBranch(e); //make a new branch for the element
				elementsToNodes.put(e,nbg); //make a conversion entry so we can find the branch later
				scene.addChild(nbg.getBranchScene()); //add the branch to the scene
			}
			
			e = e.next;
		} while(e!=elementStart);
	} //update

	//this looks familiar...
	public static void main(String[] args)
	{
		Client myClient = Client.initialize(args); //create a Client for the game


		Frame f = new MainFrame(new Representation3D(myClient),300,300); //run the applet inside a Frame
	} 
}
