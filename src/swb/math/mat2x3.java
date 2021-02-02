package swb.math;

public class mat2x3 
{
	public vec3f v1;
	public vec3f v2;
	
	public mat2x3()
	{
		v1 = new vec3f();
		v2 = new vec3f();
	}
	
	public mat2x3( float v1x, float v1y, float v1z, float v2x, float v2y, float v2z )
	{
		v1 = new vec3f( v1x, v1y, v1z );
		v2 = new vec3f( v2x, v2y, v2z );
	}
	
	public mat2x3( vec3f v1, vec3f v2 )
	{
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public Line toLine()
	{
		return new Line( v1, vec3f.sub( v2, v1 ) );
	}
	
	public String toString()
	{
		return v1.toString() + "\n" + v2.toString();
	}
}
