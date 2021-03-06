package swb;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import swb.math.vec3f;

public class Camera extends MouseAdapter
{
	public final static float PI = (float)Math.PI;
	public final static float HALF_PI = PI / 2.0f;
	public final static float MIN_FOV = PI / 180.0f;
	public final static float MAX_FOV = PI / 4.0f;
	public final static float SENSITIVITY = 2.0f;
	
	private View3D viewport;
	
	private vec3f position = new vec3f( 0.0f, 0.0f, 3.0f );
	private vec3f cameraFront = new vec3f( 0.0f, 0.0f, -1.0f );
	private vec3f cameraUp = new vec3f( 0.0f, 1.0f, 0.0f );
	
	private float ratio = 1.0f;
	private float fov = MAX_FOV;
	private float pitch = 0.0f;
	private float yaw = (float)(-Math.PI / 2.0);
	
	private int buttonPress = 0;
	private int lastX;
	private int lastY;
	
	public Camera( View3D viewport )
	{
		this.viewport = viewport;
		updateView();
		updateProjection();
	}
	
	public void setResolution( int width, int height )
	{
		ratio = (float)width / (float)height;
		updateView();
		updateProjection();
	}
	
	private void updateView()
	{
		lookAt( ShaderProgram.view, position, cameraFront, cameraUp );
		
		ShaderProgram.viewPos[0] = position.x;
		ShaderProgram.viewPos[1] = position.y;
		ShaderProgram.viewPos[2] = position.z;
		
		viewport.repaint();
	}
	
	private void updateProjection()
	{
		perspective( ShaderProgram.projection, fov, ratio, 0.1f, 100.0f );
		viewport.repaint();
	}
	
	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) 
	{
		fov += e.getPreciseWheelRotation() * 0.1f;
		if ( fov < MIN_FOV ) fov = MIN_FOV;
		if ( fov > MAX_FOV)  fov = MAX_FOV;
		updateProjection();
	}

	@Override
	public void mouseDragged( MouseEvent e ) 
	{
		float xOffset = (float)(e.getX() - lastX) * SENSITIVITY / viewport.getWidth();
		float yOffset = (float)(lastY - e.getY()) * SENSITIVITY / viewport.getHeight();
		lastX = e.getX();
		lastY = e.getY();
		
		switch( buttonPress )
		{
			case MouseEvent.BUTTON1:
			{
				yaw += xOffset;
				pitch += yOffset;

				if ( pitch >=  HALF_PI ) pitch = HALF_PI - 0.01f;
				if ( pitch <= -HALF_PI ) pitch = 0.01f - HALF_PI;

				cameraFront.x = (float)( Math.cos( yaw ) * Math.cos( pitch ) );
				cameraFront.y = (float)( Math.sin( pitch ) );
				cameraFront.z = (float)( Math.sin( yaw ) * Math.cos( pitch ) );
				cameraFront.normalize();
				break;
			}
			case MouseEvent.BUTTON2:
			{
				position.add( vec3f.mul( cameraFront, yOffset * 5.0f ) );
				break;
			}
			case MouseEvent.BUTTON3:
			{
				vec3f cameraSide = cameraFront.cross( cameraUp );
				cameraSide.normalize();
				position.sub( vec3f.mul( cameraSide, xOffset * 5.0f ) );
				position.sub( vec3f.mul( cameraUp, yOffset * 5.0f ) );
				break;
			}
		}
		
		updateView();
	}
	
	@Override
	public void mousePressed( MouseEvent e ) 
	{
		lastX = e.getX();
		lastY = e.getY();
		buttonPress = e.getButton();
	}
	
	public static void lookAt( float[] view, vec3f eye, vec3f front, vec3f up )
	{
		vec3f f = front.normalized();
		vec3f s = f.cross( up.normalized() );
		s.normalize();
		vec3f u = s.cross( f );
		
		view[ 0] = s.x; 	view[ 1] = s.y; 	view[ 2] = s.z; 	view[ 3] = -s.dot( eye );
		view[ 4] = u.x; 	view[ 5] = u.y; 	view[ 6] = u.z; 	view[ 7] = -u.dot( eye );
		view[ 8] = -f.x; 	view[ 9] = -f.y; 	view[10] = -f.z; 	view[11] = f.dot( eye );
		view[12] = 0.0f; 	view[13] = 0.0f; 	view[14] = 0.0f; 	view[15] = 1.0f;
	}
	
	public static void perspective( float[] projection, float fov, float ratio, float near, float far )
	{
		float tanHalf = (float)Math.atan( fov / 2.0f );
		projection[ 0] = 1 / ( ratio * tanHalf );		projection[ 1] = 0.0f;			
		projection[ 2] = 0.0f;							projection[ 3] = 0.0f;
		projection[ 4] = 0.0f;							projection[ 5] = 1 / tanHalf;	
		projection[ 6] = 0.0f;							projection[ 7] = 0.0f;
		projection[ 8] = 0.0f;							projection[ 9] = 0.0f;			
		projection[10] = (far + near) / (near - far);	projection[11] = -2.0f * far * near / (far - near);
		projection[12] = 0.0f;							projection[13] = 0.0f;			
		projection[14] = -1.0f;							projection[15] = 0.0f;
	}
}
