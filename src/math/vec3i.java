package math;

public class vec3i 
{
	public int x; //v
	public int y; //vt
	public int z; //vn
	
	public vec3i( String elements )
	{
		String[] parts = elements.split( "/" );
		x = Integer.parseInt( parts[0] ) - 1;
		y = Integer.parseInt( parts[1] ) - 1;
		z = Integer.parseInt( parts[2] ) - 1;
	}
	
	public vec3i( int x, int y, int z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean equals( Object o )
	{
		vec3i vec = (vec3i)o;
		return  (x == vec.x || x == vec.x || x == vec.x) && 
				(y == vec.y || y == vec.y || y == vec.y) &&
				(z == vec.z || x == vec.z || z == vec.z);
	}
}
