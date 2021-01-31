package tests;

import math.Matrix;
import math.MatrixColor;
import math.vec4f;

public class TestMatrix 
{
	public void test_Partition()
	{
		MatrixColor colors = new MatrixColor( 4, 4 );
		colors.applyEach( (loc) -> vec4f.random( 0.0f, 1.0f ) );
		Matrix<Matrix<vec4f>> parts = colors.partition( 2, 2 );
		Matrix<vec4f> avg = parts.convertTo( MatrixColor.TYPE, mat -> mat.average() );
		System.out.println( avg );
	}
	
	public static void main( String[] args )
	{
		new TestMatrix().test_Partition();
	}
}
