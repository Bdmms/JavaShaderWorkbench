package math;

public class Sphere 
{
	public vec3f position;
	public float radius;
	
	public Sphere()
	{
		position = new vec3f();
		radius = 0.0f;
	}
	
	public Sphere( vec3f pos, float r )
	{
		position = pos;
		radius = r;
	}
}
