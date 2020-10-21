import com.jogamp.opengl.GL3;

public enum GLDataType 
{
	IVEC1( 1, GL3.GL_INT, "int" ),
	VEC1( 1, GL3.GL_FLOAT, "float" ),
	DVEC1( 1, GL3.GL_DOUBLE, "double" ),
	VEC2( 2, GL3.GL_FLOAT, "vec2" ),
	VEC3( 3, GL3.GL_FLOAT, "vec3" ),
	VEC4( 4, GL3.GL_FLOAT, "vec4" ),
	DVEC2( 2, GL3.GL_DOUBLE, "dvec2" ),
	DVEC3( 3, GL3.GL_DOUBLE, "dvec3" ),
	DVEC4( 4, GL3.GL_DOUBLE, "dvec4" ),
	IVEC2( 2, GL3.GL_INT, "ivec2" ),
	IVEC3( 3, GL3.GL_INT, "ivec3" ),
	IVEC4( 4, GL3.GL_INT, "ivec4" ),
	SAMP2D( 1, GL3.GL_INT, "sampler2D" );
	
	public final String keyword;
	public final int size;
	public final int type;
	private GLDataType( int size, int type, String keyword )
	{
		this.keyword = keyword;
		this.size = size;
		this.type = type;
	}
	
	public String toString() 
	{ 
		return keyword; 
	}
	
	public static GLDataType getType( String GL_TYPE )
	{
		switch( GL_TYPE )
		{
		case "float": 	return GLDataType.VEC1;
		case "vec2": 	return GLDataType.VEC2;
		case "vec3": 	return GLDataType.VEC3;
		case "vec4": 	return GLDataType.VEC4;
		case "double": 	return GLDataType.DVEC1;
		case "dvec2": 	return GLDataType.DVEC2;
		case "dvec3": 	return GLDataType.DVEC3;
		case "dvec4": 	return GLDataType.DVEC4;
		case "int": 	return GLDataType.IVEC1;
		case "ivec2": 	return GLDataType.IVEC2;
		case "ivec3": 	return GLDataType.IVEC3;
		case "ivec4": 	return GLDataType.IVEC4;
		case "sampler2D": 	return GLDataType.SAMP2D;
		default: return null;
		}
	}
}
