package jo.edu.just;

public class Flags {
	//modes of server use
	public static int SERVER_MODE_QHTC = 0;	
	public static int SERVER_MODE_SQL  = 1;
	
	//modes of query use {voice , text ,etc...}
	public static int  MODE_USE_QUERY_VOICE =2;
	public static int  MODE_USE_QUERY_WIZARD =3;
	public static int  MODE_USE_QUERY_TEXT =4;
	public static int  MODE_USE_QUERY_SKETCH =1;
	
	//server request modes
	public static int QHTC_NORMAL_REQUEST_MODE =0;
	public static int QHTC_HASH_REQUEST_MODE =1;
	
	//shapes types
	public static int  SHAPE_RELATIVE_RECTANGLE = 10;
	public static int  SHAPE_BUTTON =  			11;
	public static int  SHAPE_RECTANGLE_FOR_SELECT = 12;
	public static int  SHAPE_BUTTON_FOR_RECTANGLE_SELECT =13;
	public static int  SHAPE_BUTTON_FOR_RECTANGLE_SELECT_CLOSE  = 14;
	public static int  SHAPE_BUTTON_FOR_RECTANGLE_SELECT_CAMERA = 15;
	//AnnotationTypes 
	public static int ANNOTATE_BY_PRE = 1;
	public static int ANNOTATE_BY_WIZARD = 2;
	public static int ANNOTATE_BY_VOICE = 3;
	public static int ANNOTATE_BY_TEXT = 4;
	
	
	//the query ask type
	public static int QUERY_ASK_WIZARD_TYPE = 102;
	
	
	
}
