package swb.math;

public class vec3i extends vec2i
{
	public vec3i()
	{
		super( new int[] { 0, 0, 0 }, 0, 3 );
	}
	
	public vec3i( int x, int y, int z )
	{
		super( new int[] { x, y, z }, 0, 3 );
	}
	
	public vec3i( String[] elements, int offset )
	{
		super( elements, offset, 3 );
	}
	
	@Override
	public boolean equals( Object o )
	{
		vec3i vec = (vec3i)o;
		
		int i = idx;
		return (arr[i] == vec.arr[vec.idx] || arr[i] == vec.arr[vec.idx+1] || arr[i++] == vec.arr[vec.idx+2] ) &&
			   (arr[i] == vec.arr[vec.idx] || arr[i] == vec.arr[vec.idx+1] || arr[i++] == vec.arr[vec.idx+2] ) &&
			   (arr[i] == vec.arr[vec.idx] || arr[i] == vec.arr[vec.idx+1] || arr[i  ] == vec.arr[vec.idx+2] );
	}
	
	public int getZ()
	{
		return arr[idx+2];
	}
}
