package math;

public class mat3x2 
{
	public vec3f v1;
	public vec3f v2;
	
	public mat3x2( vec3f v1, vec3f v2 )
	{
		this.v1 = v1;
		this.v2 = v2;
	}
	
	public Line toLine()
	{
		return new Line( v1, vec3f.sub( v2, v1 ) );
	}
}
