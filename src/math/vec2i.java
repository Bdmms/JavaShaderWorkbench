package math;

public class vec2i 
{
	public int x;
	public int y;
	
	public vec2i()
	{
		x = 0;
		y = 0;
	}
	
	public vec2i( int x, int y )
	{
		this.x = x;
		this.y = y;
	}
	
	public vec2i( String elements )
	{
		this( elements.split( "/" ) );
	}
	
	public vec2i( String[] elements )
	{
		x = Integer.parseInt( elements[0] ) - 1;
		y = Integer.parseInt( elements[1] ) - 1;
	}
	
	@Override
	public boolean equals( Object o )
	{
		vec2i vec = (vec2i)o;
		return (x == vec.x || x == vec.y ) && (y == vec.x || y == vec.y );
	}
	
	@Override
	public String toString()
	{
		return x + ", " + y;
	}
}
