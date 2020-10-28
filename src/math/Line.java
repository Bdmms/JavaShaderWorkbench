package math;

public class Line 
{
	public vec3f origin;
	public vec3f vector;
	
	public Line( vec3f v1, vec3f vec )
	{
		this.origin = v1;
		this.vector = vec;
	}
	
	public vec3f getPoint( float t )
	{
		return new vec3f( origin.x + vector.x * t, origin.y + vector.y * t, origin.z + vector.z * t );
	}
}
