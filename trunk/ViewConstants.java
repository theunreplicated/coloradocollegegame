//ViewConstants.java
//@author Joel Ross

/***
 A class to hold the flags for specifying the viewMode, as well as utility
 methods for ease of using those flags.
***/

public class ViewConstants
{

//create IS_NOT for each ?
	//flags
	public static final int EMPTY 				= 0; //empty

	public static final int CHANGES_ON_AVATAR_CHANGE	= 2; //bit2
	public static final int MOVES_ON_AVATAR_MOVE		= 4 |CHANGES_ON_AVATAR_CHANGE; //bit3
	public static final int TURNS_ON_AVATAR_TURN		= 8 |CHANGES_ON_AVATAR_CHANGE; //bit4
	public static final int ACTS_WITH_AVATAR		= MOVES_ON_AVATAR_MOVE|TURNS_ON_AVATAR_TURN; //shortcut for the above
	
	public static final int HAS_OFFSET			= 512; //bit10
	//only can pick one of these 4
	public static final int HAS_CLOSE_OFFSET		= 0 |HAS_OFFSET; //bit11,12
	public static final int HAS_MEDIUM_OFFSET		= 1024 |HAS_OFFSET; //bit11,12
	public static final int HAS_LONG_OFFSET			= 2048 |HAS_OFFSET; //bit11,12
	public static final int HAS_VARIABLE_OFFSET		= 3072 |HAS_OFFSET; //bit11,12

	public static final int OFFSET_DEPENDS_ON_AVATAR	= 16384 |HAS_OFFSET; //bit15
	//only can pick one of these 4
	public static final int LOOKS_AT_AVATAR			= 0 |OFFSET_DEPENDS_ON_AVATAR; //bit16,17
	public static final int LOOKS_WITH_AVATAR		= 32768 |OFFSET_DEPENDS_ON_AVATAR; //bit16,17
	//public static final int LOOKS_OPTION1			= 65536 |OFFSET_DEPENDS_ON_AVATAR; //bit16,17
	//public static final int LOOKS_OPTION2			= 98304 |OFFSET_DEPENDS_ON_AVATAR; //bit16,17	

	public static final int RETURNS_TO_OFFSET		= 4096 |HAS_OFFSET; //bit13
	public static final int RETURNS_ON_AVATAR_MOVE		= 16 |MOVES_ON_AVATAR_MOVE|RETURNS_TO_OFFSET; //bit5
	public static final int RETURNS_ON_AVATAR_TURN		= 32 |TURNS_ON_AVATAR_TURN|RETURNS_TO_OFFSET; //bit6
	public static final int RETURNS_TO_OFFSET_ON_TIMER	= 8192 |RETURNS_TO_OFFSET; //bit14

	public static final int CAN_CHANGE_INDEPENDENTLY	= 64 |HAS_OFFSET; //bit7
	public static final int CAN_MOVE_INDEPENDENTLY		= 128 |CAN_CHANGE_INDEPENDENTLY; //bit8
	public static final int CAN_TURN_INDEPENDENTLY		= 256 |CAN_CHANGE_INDEPENDENTLY; //bit9
	public static final int ACTS_INDEPENDENTLY		= CAN_MOVE_INDEPENDENTLY|CAN_TURN_INDEPENDENTLY; //shortcut for the above

	public static final int SHOWS_AVATAR			= 131072; //bit18
	

	//default views
	public static final int FIRST_PERSON_VIEW 	= ACTS_WITH_AVATAR|
							  LOOKS_WITH_AVATAR;
	public static final int OFFSET_VIEW		= ACTS_WITH_AVATAR|
							  LOOKS_WITH_AVATAR|
							  HAS_MEDIUM_OFFSET|
							  SHOWS_AVATAR;
	public static final int	FOLLOWING_VIEW		= RETURNS_ON_AVATAR_MOVE|
							  CAN_TURN_INDEPENDENTLY|
							  LOOKS_AT_AVATAR|
							  HAS_VARIABLE_OFFSET|
							  SHOWS_AVATAR;
	public static final int INDEPENDENT_VIEW	= ACTS_INDEPENDENTLY|
							  HAS_VARIABLE_OFFSET|
							  SHOWS_AVATAR;
		

	/**
	 **Helper methods for readability/ease of use (though will lose speed)
	 **Note that these methods return the new "view". So use them like
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