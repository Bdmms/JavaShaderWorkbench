package swb;

import swb.math.vecf;

/**
 * Defines the structure of a vertex in a VertexBuffer.
 * Used when the vertices are dynamically modified.
 * @author Sean Rannie
 */
public abstract class Vertex extends vecf
{
	public Vertex(float[] arr, int offset, int size) 
	{
		super(arr, offset, size);
	}
	
	public Vertex(int size) 
	{
		super(size);
	}
	
	/**
	 * Checks if the vertex has been modified
	 * @return Whether the vertex has been modified
	 */
	public abstract boolean isModified();
}
