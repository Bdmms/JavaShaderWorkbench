package swb;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.math.FloatUtil;

import swb.math.mat4x4;
import swb.math.vec3f;

public class Camera extends MouseAdapter
{
	public final static float HALF_PI = FloatUtil.PI / 2.0f;
	public final static float MIN_FOV = FloatUtil.PI / 180.0f;
	public final static float MAX_FOV = FloatUtil.PI / 4.0f;
	public final static float SENSITIVITY = 2.0f;
	
	private GLJPanel viewport;
	
	public float timer = 0.0f;
	public final mat4x4 view = new mat4x4();
	public final mat4x4 projection = new mat4x4();
	public final vec3f position = new vec3f( 0.0f, 0.0f, 3.0f );
	public final vec3f cameraFront = new vec3f( 0.0f, 0.0f, -1.0f );
	public final vec3f cameraUp = new vec3f( 0.0f, 1.0f, 0.0f );
	
	private float ratio = 1.0f;
	private float fov = MAX_FOV;
	private float pitch = 0.0f;
	private float yaw = FloatUtil.PI / -2.0f;
	
	private int buttonPress = 0;
	private int lastX;
	private int lastY;
	
	public Camera( GLJPanel viewport )
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
		view.lookAt( position, cameraFront, cameraUp );
		viewport.repaint();
	}
	
	private void updateProjection()
	{
		projection.setPerspective( fov, ratio, 0.1f, 100.0f );
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

				cameraFront.set( 
						FloatUtil.cos( yaw ) * FloatUtil.cos( pitch ),
						FloatUtil.sin( pitch ),
						FloatUtil.sin( yaw ) * FloatUtil.cos( pitch )
				);
				cameraFront.normalize();
				break;
			}
			case MouseEvent.BUTTON2:
			{
				position.addMul( cameraFront, yOffset * 5.0f );
				break;
			}
			case MouseEvent.BUTTON3:
			{
				vec3f cameraSide = cameraFront.cross( cameraUp );
				cameraSide.normalize();
				position.subMul( cameraSide, xOffset * 5.0f );
				position.subMul( cameraUp, yOffset * 5.0f );
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
}
