package math;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.imageio.ImageIO;

/**
 * Implements the {@link IMatrix} interface using a defined data type
 * @param <E> - The data type stored in the matrix
 */
public class Matrix<E> implements Iterable<E>
{
	/** The instance type of the matrix, it is used to create new instances of the subclass */
	public final MatrixInstance<E> type;
	/** The width of the matrix */
	public final int width;
	/** The height of the matrix */
	public final int height;
	/** The data array of the matrix stored linearly */
	public final E[] data;
	
	/**
	 * Constructs an new matrix
	 * @param w - Width of the matrix
	 * @param h - Height of the matrix
	 * @param data - instance of a populated or unpopulated array
	 * @param type - The instance of the matrix's type
	 */
	public Matrix( int w, int h, E[] data, MatrixInstance<E> type )
	{
		this.width = w;
		this.height = h;
		this.type = type;
		this.data = data;
	}
	
	@SuppressWarnings("unchecked")
	public Matrix( int w, int h, Class<E> classType )
	{
		this.width = w;
		this.height = h;
		this.type = new MatrixInstance<E>() 
		{
			@Override
			public Matrix<E> newInstance(int w, int h) 
			{
				return new Matrix<E>( width, height, 
						(E[])Array.newInstance( classType, w * h ), type );
			}
			
		};
		this.data = (E[])Array.newInstance( classType, w * h );
	}
	
	/**
	 * Constructs a copy of a matrix. The data array is cloned.
	 * @param matrix - original matrix
	 */
	public Matrix( Matrix<E> matrix )
	{
		this.width = matrix.width;
		this.height = matrix.height;
		this.data = matrix.data.clone();
		this.type = matrix.type;
	}
	
	/*
	 * The default value used when out of bounds in matrix operations
	 */
	protected E defaultValue()
	{
		return null;
	}
	
	/**
	 * Average of all elements in the matrix
	 * @return average of matrix, or default value if this operations in not defined
	 */
	public E average()
	{
		return defaultValue();
	}
	
	/**
	 * Returns the derivative of the matrix map
	 * @param gradientFunc
	 * @return
	 */
	public Matrix<vec4f> gradient( BiFunction<E, E, vec4f> gradientFunc )
	{
		return null;
	}
	
	/**
	 * Defines how to convert the cell data into a color. 
	 * It will always return 0 if it is not overrided by a subclass.
	 * @param obj - the original cell value
	 * @return The converted color value of the object
	 */
	protected int convertToColor( E obj )
	{
		return 0;
	}
	
	/**
	 * Defines how to interpolate the data at a floating-point position.
	 * It will always return the non-interpolated value if it is not overrided by a subclass.
	 * @param x - Horizontal position of the image from 0.0 to 1.0
	 * @param y - Vertical position of the image from 0.0 to 1.0
	 * @return The interpolated value at the specified location
	 */
	public E sample( float x, float y )
	{
		return data[ (int)(x * width) + (int)(y * height) * width ];
	}
	
	/**
	 * Creates a new matrix of the same size filtered using the specified operation.
	 * @param filter - the operation that will return a new cell value from the old cell value
	 * @return A new filtered matrix from the original matrix
	 */
	public Matrix<E> filter( Function<E, E> filter )
	{
		Matrix<E> copy = type.newInstance( width, height );
		for( int i = 0; i < data.length; i++ )
			copy.data[i] = filter.apply( data[i] );
		return copy;
	}
	
	/**
	 * Creates a new matrix of the same size filtered using the specified operation.
	 * @param size - the size of the source matrix. The source matrix will contain the surrounding cells
	 * of the iterated cell based on the size specified. (e.g. size = 3 would result in a 3x3 matrix)
	 * @param filter - the operation that will return a new cell value from the old cell values
	 * @return A new filtered matrix from the original matrix
	 */
	public Matrix<E> filter( int size, Function<Matrix<E>, E> filter )
	{
		Matrix<E> copy = type.newInstance( width, height );
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
	public <T> Matrix<T> filter( int size, Class<T> instanceType, Function<Matrix<E>, T> filter )
	{
		Matrix<T> copy = new Matrix<T>( width, height, instanceType );
		int halfSize = size / 2;
		
		for( int y = 0, i = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				copy.data[i++] = filter.apply( partition( x - halfSize, y - halfSize, size, size ) );
		
		return copy;
	}
	
	public <T> Matrix<T> filter( int size, MatrixInstance<T> instanceType, Function<Matrix<E>, T> filter )
	{
		Matrix<T> copy = instanceType.newInstance( width, height );
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
	public <T> Matrix<T> convertTo( Class<T> instanceType, Function<E, T> converter )
	{
		Matrix<T> copy = new Matrix<T>( width, height, instanceType );
		for( int i = 0; i < data.length; i++ )
			copy.data[i] = converter.apply( data[i] );
		return copy;
	}
	
	public <T> Matrix<T> convertTo( MatrixInstance<T> instanceType, Function<E, T> converter )
	{
		Matrix<T> copy = instanceType.newInstance( width, height );
		for( int i = 0; i < data.length; i++ )
			copy.data[i] = converter.apply( data[i] );
		return copy;
	}
	
	public Matrix<E> upscale( final int w, final int h, boolean interpolate )
	{
		Matrix<E> matrix = type.newInstance( w, h );
		
		if( interpolate )
			for( int y = 0, i = 0; y < w; y++ )
				for( int x = 0; x < w; x++ )
					matrix.data[i++] = sample( (float)x / w, (float)y / h );
		else
			for( int y = 0, i = 0; y < w; y++ )
				for( int x = 0; x < w; x++ )
					matrix.data[i++] = data[ (x * width / w) + (y * height / h) * width ];
		
		return matrix;
	}
	
	public Matrix<Matrix<E>> partition( int w, int h )
	{
		int wx = width / w;
		int hy = height / h;
		@SuppressWarnings("unchecked")
		Matrix<Matrix<E>> partitions = new Matrix<Matrix<E>>( wx, hy, (Class<Matrix<E>>) getClass() );
		
		partitions.applyEach( loc -> partition( w * loc.x, h * loc.y, w, h ) );
		return partitions;
	}
	
	public Matrix<E> partition(int x, int y, int w, int h)
	{
		Matrix<E> partition = type.newInstance( w, h );
		
		w += x;
		h += y;
		for( int i = 0; y < h; y++ )
		{
			for( int px = x; px < w; px++ )
			{
				partition.data[i++] = px < 0 || px >= width || y < 0 || y >= height 
						? defaultValue() : data[px + y * width];
			}
		}
		
		return partition;
	}
	
	public BufferedImage toBufferedImage()
	{
		BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		for( int y = 0, i = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				image.setRGB( x, y, convertToColor( data[i++] ) );
		return image;
	}
	
	public void writeTo( File file ) throws IOException
	{
		ImageIO.write( toBufferedImage(), "png", file );
	}
	
	@Override
	public Iterator<E> iterator() 
	{
		return new MatrixIterator();
	}
	
	public void forEach( BiConsumer<Location, E> consumer )
	{
		Location index = new Location();
		for( int i = 0; index.y < height; index.y++ )
			for( index.x = 0; index.x < width; index.x++ )
				consumer.accept( index, data[i++] );
	}
	
	public void applyEach( BiFunction<Location, E, E> function )
	{
		Location index = new Location();
		for( int i = 0; index.y < height; index.y++ )
			for( index.x = 0; index.x < width; index.x++ )
				data[i++] = function.apply( index, data[i] );
	}
	
	public void applyEach( Function<Location, E> function )
	{
		Location index = new Location();
		for( int i = 0; index.y < height; index.y++ )
			for( index.x = 0; index.x < width; index.x++ )
				data[i++] = function.apply( index );
	}
	
	public String toString()
	{
		String message = new String();
		for( int i = 0, y = 0; y < height; y++ )
		{
			for( int x = 0; x < width; x++ )
			{
				E obj = data[i++];
				message += (obj == null ? "null" : obj.toString()) + " ";
			}
			
			message += "\n";
		}
		return message;
	}
	
	protected static interface MatrixInstance<E>
	{
		public Matrix<E> newInstance( int w, int h );
	}
	
	/**
	 * An object for storing a 2D coordinate
	 */
	public class Location
	{
		public int x = 0;
		public int y = 0;
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
			return data[i++];
		}
	}
}
