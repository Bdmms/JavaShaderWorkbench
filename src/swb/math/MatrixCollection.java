package swb.math;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public class MatrixCollection<E> extends AbstractCollection<E> implements Cloneable
{
	public final E defaultValue;
	/** The width of the matrix */
	public final int width;
	/** The height of the matrix */
	public final int height;
	/** The data array of the matrix stored linearly */
	public final Object[] data;
	
	/**
	 * Constructs an new matrix
	 * @param w - Width of the matrix
	 * @param h - Height of the matrix
	 * @param defVal - default value that is returned when out of bounds
	 */
	public MatrixCollection( int w, int h, E defVal )
	{
		this.width = w;
		this.height = h;
		this.data = new Object[ w * h ];
		this.defaultValue = defVal;
		Arrays.fill( data, defVal );
	}
	
	/**
	 * Constructs a copy of a matrix. The data array is cloned.
	 * @param matrix - original matrix
	 */
	public MatrixCollection( MatrixCollection<E> matrix )
	{
		this.width = matrix.width;
		this.height = matrix.height;
		this.data = matrix.data.clone();
		this.defaultValue = matrix.defaultValue;
	}
	
	/**
	 * Creates a new matrix of the same size filtered using the specified operation.
	 * @param filter - the operation that will return a new cell value from the old cell value
	 * @return A new filtered matrix from the original matrix
	 */
	public MatrixCollection<E> filter( Function<E, E> filter )
	{
		MatrixCollection<E> copy = new MatrixCollection<>( this );
		for( int i = 0; i < data.length; i++ )
			copy.data[i] = filter.apply( (E)data[i] );
		return copy;
	}
	
	/**
	 * Creates a new matrix of the same size filtered using the specified operation.
	 * @param size - the size of the source matrix. The source matrix will contain the surrounding cells
	 * of the iterated cell based on the size specified. (e.g. size = 3 would result in a 3x3 matrix)
	 * @param filter - the operation that will return a new cell value from the old cell values
	 * @return A new filtered matrix from the original matrix
	 */
	public MatrixCollection<E> filter( int size, Function<MatrixCollection<E>, E> filter )
	{
		MatrixCollection<E> copy = new MatrixCollection<>( this );
		int halfSize = size / 2;
		
		for( int y = 0, i = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				copy.data[i++] = filter.apply( partition( x - halfSize, y - halfSize, size, size ) );
		
		return copy;
	}
	
	/**
	 * Creates a new matrix of the same size filtered using the specified operation. The filter operation
	 * will also convert the data type from the original matrix to the new matrix
	 * @param <T> - the type casting applied to the matrix data
	 * @param size - the size of the source matrix. The source matrix will contain the surrounding cells
	 * of the iterated cell based on the size specified. (e.g. size = 3 would result in a 3x3 matrix)
	 * @param instanceType - The instance type of the new matrix
	 * @param filter - the operation that will return a new converted cell value from the old cell values
	 * @return A new converted and filtered matrix from the original matrix
	 */
	public <T> MatrixCollection<T> filter( int size, T defaultValue, Function<MatrixCollection<E>, T> filter )
	{
		MatrixCollection<T> copy = new MatrixCollection<T>( width, height, defaultValue );
		int halfSize = size / 2;
		
		for( int y = 0, i = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				copy.data[i++] = filter.apply( partition( x - halfSize, y - halfSize, size, size ) );
		
		return copy;
	}
	
	/**
	 * Converts one matrix of one type to another matrix of another type using a conversion operation.
	 * @param <T> - the type casting applied to the matrix data
	 * @param instanceType - the instance type of the new matrix
	 * @param converter - the function that converts a cell value to its new type
	 * @return The new matrix of the new specified type
	 */
	public <T> MatrixCollection<T> convertTo( T defaultValue, Function<E, T> converter )
	{
		MatrixCollection<T> copy = new MatrixCollection<T>( width, height, defaultValue );
		for( int i = 0; i < data.length; i++ )
			copy.data[i] = converter.apply( (E)data[i] );
		return copy;
	}
	
	public MatrixCollection<MatrixCollection<E>> partition( int w, int h )
	{
		int wx = width / w;
		int hy = height / h;
		
		MatrixCollection<E> defVal = new MatrixCollection<E>( w, h, defaultValue );
		defVal.applyEach( (x,y) -> defaultValue );
		MatrixCollection<MatrixCollection<E>> partitions = new MatrixCollection<>( wx, hy, defVal );
		
		partitions.applyEach( (x, y) -> partition( w * x, h * y, w, h ) );
		return partitions;
	}
	
	public MatrixCollection<E> partition( int x, int y, int w, int h )
	{
		MatrixCollection<E> partition = new MatrixCollection<>( w, h, defaultValue );
		
		w += x;
		h += y;
		for( int i = 0; y < h; y++ )
		{
			for( int px = x; px < w; px++ )
			{
				partition.data[i++] = px < 0 || px >= width || y < 0 || y >= height ? defaultValue : data[px + y * width];
			}
		}
		
		return partition;
	}
	
	public void forEach( LocateConsumer<E> consumer )
	{
		for( int x = 0, i = 0; x < width; x++ )
			for( int y = 0; y < height; y++ )
				consumer.accept( x, y, (E)data[i++] );
	}
	
	public MatrixCollection<E> applyEach( LocateFunction<E> function )
	{
		for( int x = 0, i = 0; x < width; x++ )
			for( int y = 0; y < height; y++ )
				data[i++] = function.apply( x, y, (E)data[i] );
		return this;
	}
	
	public MatrixCollection<E> applyEach( LocateSupplier<E> function )
	{
		for( int x = 0, i = 0; x < width; x++ )
			for( int y = 0; y < height; y++ )
				data[i++] = function.next( x, y );
		return this;
	}

	@Override
	public Iterator<E> iterator() 
	{
		return new MatrixIterator();
	}
	
	@Override
	public int size() 
	{
		return data.length;
	}
	
	@Override
	public Object[] toArray() 
	{
		return data;
	}
	
	@Override
	public <T> T[] toArray( T[] arr ) 
	{
		for( int i = 0; i < data.length; i++ )
			arr[i] = (T)data[i];
		return arr;
	}
	
	@Override
	public MatrixCollection<E> clone()
	{
		return new MatrixCollection<E>( this );
	}
	
	/**
	 * Implementation of {@link Iterator} that iterates the matrix in top-left to bottom-right order.
	 */
	private class MatrixIterator implements Iterator<E>
	{
		/** Iterator location */
		private int i = 0;
		
		@Override
		public boolean hasNext() 
		{
			return i < data.length;
		}

		@Override
		public E next() 
		{
			return (E)data[i++];
		}
	}
	
	@FunctionalInterface
	public static interface LocateSupplier<T>
	{
		public T next( int x, int y );
	}
	
	@FunctionalInterface
	public static interface LocateFunction<T>
	{
		public T apply( int x, int y, T obj );
	}
	
	@FunctionalInterface
	public static interface LocateConsumer<T>
	{
		public void accept( int x, int y, T obj );
	}
}
