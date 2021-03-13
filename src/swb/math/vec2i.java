package swb.math;

public class vec2i extends veci 
{
	protected vec2i( int[] arr, int offset, int size )
	{
		super( arr, offset, size );
	}
	
	protected vec2i( String[] elements, int offset, int size )
	{
		super( elements, offset, size );
	}
	
	public vec2i()
	{
		super( new int[] { 0, 0 }, 0, 2 );
	}
	
	public vec2i( int x, int y )
	{
		super( new int[] { x, y }, 0, 2 );
	}
	
	public vec2i( String[] elements, int offset )
	{
		super( elements, offset, 2 );
	}
	
	@Override
	public boolean equals( Object o )
	{
		vec2i vec = (vec2i)o;
		return (arr[idx] == vec.arr[vec.idx] || arr[idx] == vec.arr[vec.idx+1] ) 
				&& (arr[idx+1] == vec.arr[vec.idx] || arr[idx+1] == vec.arr[vec.idx+1] );
	}
	
	public int getX()
	{
		return arr[idx];
	}
	
	public int getY()
	{
		return arr[idx+1];
	}
}
