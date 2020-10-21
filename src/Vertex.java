
public class Vertex 
{
	private float[] _vertex;
	
	public Vertex( float ... fs )
	{
		_vertex = fs;
	}
	
	public float[] getData() { return _vertex; } 
	public int getDimension() { return _vertex.length; }
}
