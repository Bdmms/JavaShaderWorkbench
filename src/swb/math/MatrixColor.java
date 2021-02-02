package swb.math;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;

import javax.imageio.ImageIO;

public class MatrixColor extends Matrix<vec4f>
{
	public static final MatrixInstance<vec4f> TYPE = new MatrixInstance<vec4f>()
	{
		public MatrixColor newInstance( int w, int h )
		{
			return new MatrixColor( w, h );
		}
	};
	
	/**
	 * Constructs an empty color matrix of the specified size
	 * @param w - Width of the matrix
	 * @param h - Height of the matrix
	 */
	public MatrixColor( int w, int h )
	{
		super( w, h, new vec4f[ w * h ], TYPE );
	}
	
	/**
	 * Constructs a color matrix from an image {@link File}
	 * @param image - source file
	 */
	public MatrixColor( File file ) throws IOException
	{
		this( ImageIO.read( file ) );
	}
	
	/**
	 * Constructs a color matrix from a {@link BufferedImage}
	 * @param image - source image
	 */
	public MatrixColor( BufferedImage image )
	{
		this( image.getWidth(), image.getHeight() );
		
		for( int y = 0, i = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
				data[i++] = vec4f.createVector( image.getRGB( x, y ) );
	}
	
	public MatrixColor( MatrixColor matrix )
	{
		super( matrix );
	}
	
	@Override
	protected vec4f defaultValue()
	{
		return new vec4f();
	}
	
	@Override
	public vec4f average()
	{
		return vec4f.average( data );
	}
	
	@Override
	public Matrix<vec4f> gradient( BiFunction<vec4f, vec4f, vec4f> gradientFunc )
	{
		return filter( 3, mat -> 
		{
			vec4f dx = vec4f.average( vec4f.sub( mat.data[4], mat.data[3] ), vec4f.sub( mat.data[5], mat.data[4] ) );
			vec4f dy = vec4f.average( vec4f.sub( mat.data[4], mat.data[1] ), vec4f.sub( mat.data[7], mat.data[4] ) );
			return gradientFunc.apply( dx,  dy );
		} );
	}
	
	/**
	 * Converts the rgba color values of a color matrix into their average single channel value.
	 * @return A greyscale {@link Matrix<Double>} of the color matrix
	 */
	public Matrix<Float> greyscale()
	{
		return convertTo( MatrixFloat.TYPE, color -> color.greyValue() );
	}
	
	/**
	 * Converts the color image into a normal map defined by the color matrix's gradient
	 * @return The normal map converted from the original color matrix
	 */
	public Matrix<vec4f> normalMap()
	{
		return greyscale().gradient( (dx,dy) -> 
		{
			float c = (float)Math.sqrt( dx * dx + dy * dy + 1.0f );
			return new vec4f( (-dx  / c + 1.0f) * 0.5f, (-dy  / c + 1.0f) * 0.5f, 1.0f / c, 1.0f );
		} );
	}
	
	@Override
	public vec4f sample( float x, float y )
	{
		x *= width;
		y *= height;
		int ix = (int)x;
		int iy = (int)y;
		int i0 = ix + iy * width;
		int i1 = ix + 1 < width ? i0 + 1 : i0;
		int i2 = iy + 1 < height ? i0 + width : i0;
		return vec4f.average( data[i0], data[i1], data[i2], data[i1 + i2 - i0], x - ix, y - iy);
	}
	
	@Override
	protected int convertToColor( vec4f color )
	{
		return color.toRGBA();
	}
}
