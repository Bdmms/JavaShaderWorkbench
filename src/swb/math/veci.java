package swb.math;

import swb.utils.PrintUtil;

public class veci 
{
	protected final int dim;
	protected final int end;
	protected final int idx;
	protected final int[] arr;
	
	public veci( int[] arr, int offset, int size )
	{
		this.dim = size;
		this.idx = offset;
		this.arr = arr;
		this.end = idx + dim;
	}
	
	public veci( int[] arr, int size )
	{
		this( arr, 0, size );
	}
	
	public veci( int[] arr )
	{
		this( arr, 0, arr.length );
	}
	
	public veci( int size )
	{
		this( new int[size], 0, size );
	}
	
	public veci( String[] elements, int offset, int size )
	{
		this( size );
		
		for( int i = 0; i < size; i++ )
			arr[i] = Integer.parseInt( elements[i+offset] );
	}
	
	public void copyTo( int[] data, int offset )
	{
		System.arraycopy( arr, idx, data, offset, dim );
	}
	
	@Override
	public String toString()
	{
		return PrintUtil.toString( "\t", arr, idx, end );
	}
}
