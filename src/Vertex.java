
public interface Vertex
{
	public String toString();
	public float[] getData();
	
	public void writeDataBuffer( float[] buffer, int offset );
	public boolean isModified();
	
	public static String[] toStringArr( Vertex vertex )
	{
		float[] arr = vertex.getData();
		String[] sArr = new String[ arr.length ];
		for( int i = 0; i < arr.length; i++ )
			sArr[i] = Float.toString( arr[i] );
		return sArr;
	}
}
