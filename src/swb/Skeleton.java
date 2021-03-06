package swb;

import swb.math.vec3f;

public class Skeleton 
{
	public vec3f[] position;
	public vec3f[] rotation;
	public String[] name;
	public int[] parent;
	public int size;
	
	public Skeleton( int numNodes )
	{
		size = numNodes;
		position = new vec3f[numNodes];
		rotation = new vec3f[numNodes];
		parent = new int[numNodes];
		name = new String[numNodes];
		
		for( int i = 0; i < size; i++ )
		{
			position[i] = new vec3f();
			rotation[i] = new vec3f();
		}
	}
}
