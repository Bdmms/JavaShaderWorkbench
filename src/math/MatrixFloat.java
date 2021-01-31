package math;

import java.util.function.BiFunction;

public class MatrixFloat extends Matrix<Float>
{
	public static final MatrixInstance<Float> TYPE = new MatrixInstance<Float>()
	{
		public MatrixFloat newInstance( int w, int h )
		{
			return new MatrixFloat( w, h );
		}
	};
	
	/**
	 * Constructs an empty double matrix of the specified size
	 * @param w - Width of the matrix
	 * @param h - Height of the matrix
	 */
	public MatrixFloat( int w, int h )
	{
		super( w, h, new Float[w*h], TYPE );
	}
	
	@Override
	protected Float defaultValue()
	{
		return 0.0f;
	}
	
	@Override
	public Float average()
	{
		float avg = 0.0f;
		for( Float val : data ) avg += val;
		return avg / data.length;
	}
	
	@Override
	public Matrix<vec4f> gradient( BiFunction<Float, Float, vec4f> gradientFunc )
	{
		return filter( 3, vec4f.class, mat -> 
		{
			float dx = ((mat.data[4] - mat.data[3]) + (mat.data[5] - mat.data[4])) * 0.5f;
			float dy = ((mat.data[4] - mat.data[1]) + (mat.data[7] - mat.data[4])) * 0.5f;
			return gradientFunc.apply( dx,  dy );
		} );
	}
	
	@Override
	public Float sample( float x, float y )
	{
		x *= width;
		y *= height;
		int ix = (int)x;
		int iy = (int)y;
		int i0 = ix + iy * width;
		int i1 = ix + 1 < width ? i0 + 1 : i0;
		int i2 = iy + 1 < height ? i0 + width : i0;
		float wx = x - ix;
		float wy = y - iy;
		return (data[i0] * (1.0f - wx) + data[i1] * wx) * (1.0f - wy) 
				+ (data[i2] * (1.0f - wx) + data[i1 + i2 - i0] * wx) * wy;
	}
	
	@Override
	public int convertToColor( Float value )
	{
		int ch = value < 0.0f ? 0x00 : (value > 1.0f ? 0xFF : ((int)(value * 255.0f) & 0xFF));
		return 0xFF000000 | (ch << 16) | (ch << 8) | ch;
	}
}
