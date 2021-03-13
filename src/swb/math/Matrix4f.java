package swb.math;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jogamp.opengl.math.FloatUtil;

public class Matrix4f extends Matrixf
{
	public Matrix4f( int width, int height ) 
	{
		super( width, height, 4 );
		
		// Wrap the vectors around the raw data
		for( int i = 0, j = 0; i < vectorData.length; i++, j += 4 )
			vectorData[i] = new vec4f( data, j );
	}
	
	/**
	 * Constructs a color matrix from an image {@link File}
	 * @param image - source file
	 */
	public Matrix4f( File file ) throws IOException
	{
		this( ImageIO.read( file ) );
	}
	
	/**
	 * Constructs a color matrix from a {@link BufferedImage}
	 * @param image - source image
	 */
	public Matrix4f( BufferedImage image )
	{
		super( image.getWidth(), image.getHeight(), 4 );
		
		for( int y = 0, i = 0; y < height; y++ )
			for( int x = 0; x < width; x++ )
			{
				vec4f v = new vec4f();
				v.setColor( image.getRGB( x, y ) );
				vectorData[i++] = v;
			}
	}
	
	public float[] greyscale()
	{
		float[] greyMap = new float[width * height];
		for( int i = 0, j = 0; i < greyMap.length; i++, j += 2 )
			greyMap[i] = (data[j++] + data[j++] + data[j]) / 3.0f;
		return greyMap;
	}
	
	/**
	 * Converts the color image into a normal map defined by the color matrix's gradient
	 * @return The normal map converted from the original color matrix
	 */
	public void normalMap( Matrix4f result )
	{
		float[] src = greyscale();
		float[] dst = result.data;
		
		for( int i = 0, j = 0; i < src.length; i++ )
		{
			float m1 = i > width ? src[i-width] : 0.0f;
			float m3 = i > 0 ? src[i-1] : 0.0f;
			float m4 = src[i];
			float m5 = i < src.length - 1 ? src[i+1] : 0.0f;
			float m7 = i < src.length - width ? src[i+width] : 0.0f;
			float dx = ((m4 - m3) + (m5 - m4)) / 2.0f;
			float dy = ((m4 - m1) + (m7 - m4)) / 2.0f;
			float c = FloatUtil.sqrt( dx * dx + dy * dy + 1.0f );
			
			dst[j++] = (-dx / c + 1.0f) * 0.5f;
			dst[j++] = (-dy / c + 1.0f) * 0.5f;
			dst[j++] = 1.0f / c;
			dst[j++] = 1.0f;
		}
	}
}
