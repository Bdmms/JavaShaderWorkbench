package math;

public class vec2f 
{
	public static final byte XYPLANE = 0;
	public static final byte YZPLANE = 1;
	public static final byte XZPLANE = 2;
	public static final byte YXPLANE = 3;
	public static final byte ZYPLANE = 4;
	public static final byte ZXPLANE = 5;
	public static final vec2f ZERO = new vec2f( 0.0f, 0.0f ); 
	
	public float x;
	public float y;
	
	public vec2f()
	{
		this.x = 0.0f;
		this.y = 0.0f;
	}
	
	public vec2f( float scalar )
	{
		this.x = scalar;
		this.y = scalar;
	}
	
	public vec2f( float x, float y )
	{
		this.x = x;
		this.y = y;
	}
	
	public String toString()
	{
		return x + ", " + y;
	}
	
	public boolean equals( vec2f vector )
	{
		return vector.x == x && vector.y == y;
	}
	
	public void mul( float scalar )
	{
		x *= scalar;
		y *= scalar;
	}
	
	public float dot( vec2f vec )
	{
		return x * vec.x + y * vec.y;
	}
	
	public float length()
	{
		return (float)Math.sqrt( x * x + y * y );
	}
	
	public vec3f cross( vec2f vec )
	{
		return new vec3f( 0.0f, 0.0f, x * vec.y - y * vec.x );
	}
}
