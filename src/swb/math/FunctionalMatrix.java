package swb.math;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

import javax.imageio.ImageIO;

@SuppressWarnings("unchecked")
public class FunctionalMatrix<T extends MathObj<T>> extends MatrixCollection<T> implements MathObj<FunctionalMatrix<T>>
{
	public FunctionalMatrix( int w, int h, T defVal )
	{
		super( w, h, defVal );
	}
	
	public MatrixCollection<T> resize( final int w, final int h, boolean interpolate )
	{
		MatrixCollection<T> matrix = new MatrixCollection<T>( width, height, defaultValue );
		
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
	
	public FunctionalMatrix( FunctionalMatrix<T> matrix )
	{
		super( matrix );
	}
	
	public T average()
	{
		T avg = defaultValue.clone();
		for( Object val : data )
			avg.add( (T)val );
		avg.div( data.length );
		return avg;
	}
	
	public T sample( float x, float y )
	{
		x *= width;
		y *= height;
		int ix = (int)x;
		int iy = (int)y;
		int i0 = ix + iy * width;
		int i1 = ix + 1 < width ? i0 + 1 : i0;
		int i2 = iy + 1 < height ? i0 + width : i0;
		return MathObj.average( (T)data[i0], (T)data[i1], (T)data[i2], (T)data[i1 + i2 - i0], x - ix, y - iy);
	}
	
	public <K> MatrixCollection<K> gradient( K defVal, BiFunction<T, T, K> gradientFunc )
	{
		return filter( 3, defVal, mat -> 
		{
			T dx = MathObj.average( MathObj.sub( (T)mat.data[4],(T) mat.data[3] ), MathObj.sub( (T)mat.data[5],(T) mat.data[4] ) );
			T dy = MathObj.average( MathObj.sub( (T)mat.data[4],(T) mat.data[1] ), MathObj.sub( (T)mat.data[7],(T) mat.data[4] ) );
			return gradientFunc.apply( dx,  dy );
		} );
	}
	
	public BufferedImage toBufferedImage()
	{
		BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
		for( int y = 0, i = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				image.setRGB( x, y, ((T)data[i++]).toColor() );
		return image;
	}
	
	public void writeTo( File file ) throws IOException
	{
		if( file != null )
			ImageIO.write( toBufferedImage(), "png", file );
	}

	@Override
	public void add( FunctionalMatrix<T> b ) 
	{
		for( int i = 0; i < data.length; i++ )
			((T)data[i]).add( (T)b.data[i] );
	}

	@Override
	public void sub(FunctionalMatrix<T> b) 
	{
		for( int i = 0; i < data.length; i++ )
			((T)data[i]).sub( (T)b.data[i] );
	}

	@Override
	public void mul(float b) 
	{
		for( int i = 0; i < data.length; i++ )
			((T)data[i]).mul( b );
	}

	@Override
	public void div(float b) 
	{
		for( int i = 0; i < data.length; i++ )
			((T)data[i]).div( b );
	}

	@Override
	public int toColor() 
	{
		return average().toColor();
	}

	@Override
	public FunctionalMatrix<T> clone() 
	{
		return new FunctionalMatrix<T>( this );
	}
}
