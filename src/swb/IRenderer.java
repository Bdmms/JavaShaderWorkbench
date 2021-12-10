package swb;

import com.jogamp.opengl.GL3;

public interface IRenderer 
{
	public void init( GL3 gl );
	public void render( GL3 gl, Camera camera );
}
