package swb.math;

public class vec3i extends vec2i
{
	public int z;
	
	public vec3i()
	{
		super();
		z = 0;
	}
	
	public vec3i( int x, int y, int z )
	{
		super( x, y );
		this.z = z;
	}
	
	public vec3i( String elements )
	{
		this( elements.split( "/" ) );
	}
	
	public vec3i( String[] elements )
	{
		super( elements );
		z = Integer.parseInt( elements[2] ) - 1;
	}
	
	@Override
	public boolean equals( Object o )
	{
		vec3i vec = (vec3i)o;
		return  (x == vec.x || x == vec.y || x == vec.z) && 
				(y == vec.x || y == vec.y || y == vec.z) &&
				(z == vec.x || x == vec.y || z == vec.z);
	}
	
	@Override
	public String toString()
	{
		return x + ", " + y + ", " + z;
	}
}
