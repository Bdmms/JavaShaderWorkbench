package swb.math;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Matrixf extends vecf implements Iterable<vecf>, Cloneable 
{
	protected final int width;
	protected final int height;
	protected final int stride;
	protected final vecf[] vectorData;
	
	public Matrixf( int width, int height, int stride )
	{
		super( new float[width * height * stride], 0 );
		this.width = width;
		this.height = height;
		this.stride = stride;
		this.vectorData = new vecf[width * height];
		for( int i = 0, j = 0; i < vectorData.length; i++, j += stride )
			vectorData[i] = new vecf( data, j, stride );
	}
	
	public Matrixf( float[] data, int width, int height )
	{
		super( data, 0 );
		this.vectorData = new vecf[width * height];
		this.width = width;
		this.height = height;
		this.stride = data.length / vectorData.length;
	}
	
	public Matrixf( float[] data, int width, int height, vecf[] vectorData )
	{
		super( data, 0 );
		this.width = width;
		this.height = height;
		this.stride = data.length / vectorData.length;
		this.vectorData = vectorData;
	}
	
	public void filter( Consumer<vecf> filter )
	{
		for( int i = 0; i < vectorData.length; i++ )
			filter.accept( vectorData[i] );
	}
	
	/**
	 * Creates a new matrix of the same size filtered using the specified operation.
	 * @param size - the size of the source matrix. The source matrix will contain the surrounding cells
	 * of the iterated cell based on the size specified. (e.g. size = 3 would result in a 3x3 matrix)
	 * @param filter - the operation that will return a new cell value from the old cell values
	 * @return A new filtered matrix from the original matrix
	 */
	public void filter( Matrixf result, int size, BiConsumer<vecf, Matrixf> filter )
	{
		Matrixf part = new Matrixf( size, size, stride );
		int halfSize = size / 2;
		
		for( int y = 0, i = 0; y < height; y++ )
		{
			for( int x = 0; x < width; x++ )
			{
				partition( part, x - halfSize, y - halfSize );
				filter.accept( result.vectorData[i++], part );
			}
		}
	}
	
	public void gradient( Matrixf result, GradientFunction gradientFunc )
	{
		final vecf v0 = new vecf( stride );
		final vecf v1 = new vecf( stride );
		final vecf v2 = new vecf( stride );
		
		filter( result, 3, (dst, mat) -> 
		{
			vecf.sub( v1, mat.vectorData[4], mat.vectorData[3] );
			vecf.sub( v2, mat.vectorData[5], mat.vectorData[4] );
			vecf.average( v0, v0, v1 );
			vecf.sub( v1, mat.vectorData[4], mat.vectorData[1] );
			vecf.sub( v2, mat.vectorData[7], mat.vectorData[4] );
			vecf.average( v1, v1, v2 );
			gradientFunc.grad( dst, v0, v1 );
		} );
	}
	
	/**
	 * Converts one matrix of one type to another matrix of another type using a conversion operation.
	 * @param <T> - the type casting applied to the matrix data
	 * @param instanceType - the instance type of the new matrix
	 * @param converter - the function that converts a cell value to its new type
	 * @return The new matrix of the new specified type
	 */
	public void convertTo( Matrixf mat, BiConsumer<vecf, vecf> converter )
	{
		for( int i = 0; i < vectorData.length; i++ )
			converter.accept( mat.vectorData[i], vectorData[i] );
	}
	
	public void partition( Matrixf partition, int x, int y )
	{
		vecf[] arr = partition.vectorData;
		int w = partition.width + x;
		int h = partition.height + y;
		
		for( int i = 0; y < h; y++ )
		{
			for( int px = x; px < w; px++ )
			{
				arr[i++] = px < 0 || px >= width || y < 0 || y >= height ? new vecf( stride ) : vectorData[px + y * width];
			}
		}
	}
	
	@Override
	public Iterator<vecf> iterator()
	{
		return new MatrixIterator();
	}
	
	@Override
	public Matrixf clone()
	{
		float[] newData = Arrays.copyOf( data, data.length );
		vecf[] newVData = new vecf[vectorData.length];
		for( int i = 0; i < newVData.length; i++ )
			newVData[i] = new vecf( newData, vectorData[i].idx, vectorData[i].dim );
		return new Matrixf( newData, width, height, vectorData );
	}
	
	/**
	 * Implementation of {@link Iterator} that iterates the matrix in top-left to bottom-right order.
	 */
	private class MatrixIterator implements Iterator<vecf>
	{
		/** Iterator location */
		private int i = 0;
		
		@Override
		public boolean hasNext() 
		{
			return i < data.length;
		}

		@Override
		public vecf next() 
		{
			return vectorData[i++];
		}
	}
	
	@FunctionalInterface
	public static interface GradientFunction
	{
		public void grad( vecf result, vecf dx, vecf dy );
	}
	
	@FunctionalInterface
	public static interface LinearGradientFunction
	{
		public void grad( vecf result, float dx, float dy );
	}
}
