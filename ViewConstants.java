//ViewConstants.java
//@author Joel Ross

/***
 A class to hold the flags for specifying the viewMode, as well as utility
 methods for ease of using those flags.
***/

public class ViewConstants
{
	public static final int STATIC_VIEW = 0;
	public static final int FIRST_PERSON_VIEW = 1;
	public static final int OFFSET_VIEW = 2;
	public static final int INDEPENDENT_VIEW = 3;
	public static final int FOLLOWING_VIEW = 4;

	//the flags (REMEMBER TO REARRANGE BEFORE FINAL IMPLEMENTATION)
		//bit1: has avatar (not)
		//bit2: has offset (not)
		//bit3-4: offset is (close, medium, long, variable)
		//bit5: camera can move independently (not)
		//bit6: move on avatar move (not)
		//bit7: rotate on avatar rotate (not)
		//bit8: returns to offset (not)
		//bit9: returns based on timer (based on movement)
		//bit10+: moves/rotates around the avatar?
		//others?

		//maybe consider how the view changes on 1) avatar-moves and 2) camera moves and 3) both
		//   also add in 4)"default" position, and 5) elements shown (avatar)

	
		
	/**
	 **Helper methods for readability/ease of use (though will lose speed)
	 **Note that these methods all return the new "view". So use them like
	 **  view = ViewConstants.setFlag(view,flag);
	 **/
	public static int setFlag(int view, int flag)
	{
		return view|flag;
	}

	public static int unsetFlag(int view, int flag)
	{
		return view&(~flag);
	}
	
	public static int toggleFlag(int view, int flag)
	{
		return view^flag;
	}
	
	public static boolean isSet(int view, int flag)
	{
		return ((view&flag) > 0);
	}
}