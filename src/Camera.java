import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import math.vec3f;

public class Camera implements KeyListener, MouseWheelListener, MouseMotionListener, MouseListener
{
	public final static float PI = (float)Math.PI;
	public final static float HALF_PI = PI / 2.0f;
	public final static float MIN_FOV = PI / 128.0f;
	public final static float MAX_FOV = PI / 4.0f;
	public final static float SENSITIVITY = 0.05f;
	
	private View3D viewport;
	
	private vec3f position = new vec3f();
	private vec3f cameraFront = new vec3f( 0.0f, 0.0f, -1.0f );
	private vec3f cameraUp = new vec3f( 0.0f, 1.0f, 0.0f );
	
	private float[] projection = ShaderProgram.projection;
	private float[] view = ShaderProgram.view;
	private float[] d_view = new float[16];
	
	private float ratio = 1.0f;
	private float fov = MAX_FOV;
	private float pitch = 0.0f;
	private float yaw = (float)(-Math.PI / 2.0);
	
	private boolean[] keys = new boolean[4];
	private int buttonPress = 0;
	private int lastX;
	private int lastY;
	
	public Camera( View3D viewport )
	{
		this.viewport = viewport;
	}
	
	public void setResolution( int width, int height )
	{
		ratio = (float)width / (float)height;
	}
	
	public void updateView()
	{
		lookAt( view, position, cameraFront, cameraUp );
		
		// Truncate fourth row/column
		d_view[ 0] = view[ 0];
		d_view[ 1] = view[ 1];
		d_view[ 2] = view[ 2];
		d_view[ 4] = view[ 4];
		d_view[ 5] = view[ 5];
		d_view[ 6] = view[ 6];
		d_view[ 8] = view[ 8];
		d_view[ 9] = view[ 9];
		d_view[10] = view[10];
		
		viewport.repaint();
	}
	
	public void updateProjection()
	{
		perspective( projection, fov, ratio, 0.1f, 100.0f );
		viewport.repaint();
	}
	
	public void updateKeys()
	{
		float cameraSpeed = 2.5f * 1.0f; // deltaTime
		if ( keys[0] ) position.add( vec3f.mul( cameraFront, cameraSpeed ) );
		if ( keys[1] ) position.sub( vec3f.mul( cameraFront, cameraSpeed ) );
		if ( keys[2] ) 
		{
			vec3f cameraSide = cameraFront.cross( cameraUp );
			cameraSide.normalize();
			position.sub( vec3f.mul( cameraSide, cameraSpeed ) );
		}
		if ( keys[3] ) 
		{
			vec3f cameraSide = cameraFront.cross( cameraUp );
			cameraSide.normalize();
			position.add( vec3f.mul( cameraSide, cameraSpeed ) );
		}
	}
	
	@Override
	public void keyPressed( KeyEvent e )
	{
		switch( e.getKeyCode() )
		{
		case KeyEvent.VK_W: keys[0] = true;
		case KeyEvent.VK_S: keys[1] = true;
		case KeyEvent.VK_A: keys[2] = true;
		case KeyEvent.VK_D: keys[3] = true;
		}
	}
	
	@Override
	public void keyReleased( KeyEvent e )
	{
		switch( e.getKeyCode() )
		{
		case KeyEvent.VK_W: keys[0] = false;
		case KeyEvent.VK_S: keys[1] = false;
		case KeyEvent.VK_A: keys[2] = false;
		case KeyEvent.VK_D: keys[3] = false;
		}
	}

	@Override
	public void keyTyped( KeyEvent e ) { }

	@Override
	public void mouseWheelMoved( MouseWheelEvent e ) 
	{
		if (fov >= MIN_FOV && fov <= MAX_FOV)
			fov -= e.getPreciseWheelRotation() * 0.1f;
		else if (fov < MIN_FOV)
			fov = MIN_FOV;
		else if (fov > MAX_FOV)
			fov = MAX_FOV;
		updateProjection();
	}

	@Override
	public void mouseDragged( MouseEvent e ) 
	{
		float xOffset = (e.getX() - lastX) / viewport.getWidth();
		float yOffset = (lastY - e.getY()) / viewport.getHeight();
		lastX = e.getX();
		lastY = e.getY();
		
		if( buttonPress == MouseEvent.BUTTON1 )
		{
			yaw += xOffset * SENSITIVITY;
			pitch += yOffset * SENSITIVITY;

			if ( pitch >=  HALF_PI ) pitch = HALF_PI - 0.01f;
			if ( pitch <= -HALF_PI ) pitch = 0.01f - HALF_PI;

			cameraFront.x = (float)( Math.cos( yaw ) * Math.cos( pitch ) );
			cameraFront.y = (float)( Math.sin( pitch ) );
			cameraFront.z = (float)( Math.sin( yaw ) * Math.cos( pitch ) );
			cameraFront.normalize();
			updateView();
		}
		
		/*
		if( buttonPress == MouseEvent.BUTTON1 )
		{
			rotX += xOffset;
			rotY += yOffset;
			updateTransformation();
		}
		else if( buttonPress == MouseEvent.BUTTON3 )
		{
			ShaderProgram.view[3] -= xOffset * 0.5f;
			ShaderProgram.view[7] -= yOffset * 0.5f;
			repaint();
		}*/
	}
	
	@Override
	public void mousePressed( MouseEvent e ) 
	{
		lastX = e.getX();
		lastY = e.getY();
		buttonPress = e.getButton();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) { }

	@Override
	public void mouseClicked(MouseEvent arg0) { }

	@Override
	public void mouseEntered(MouseEvent arg0) { }

	@Override
	public void mouseExited(MouseEvent arg0) { }

	@Override
	public void mouseReleased(MouseEvent arg0) { }
	
	public static void lookAt( float[] view, vec3f eye, vec3f front, vec3f up )
	{
		vec3f f = new vec3f( eye );
		f.add( front );
		f.sub( eye );
		f.normalize();
		
		vec3f s = f.cross( up.normalized() );
		s.normalize();
		
		vec3f u = s.cross( f );
		
		view[ 0] = s.x;
		view[ 1] = s.y;
		view[ 2] = s.z;
		view[ 4] = u.x;
		view[ 5] = u.y;
		view[ 6] = u.z;
		view[ 8] = -f.x;
		view[ 9] = -f.y;
		view[10] = -f.z;
		view[12] = -s.dot( eye );
		view[13] = -u.dot( eye );
		view[14] = f.dot( eye );
	}
	
	public static void perspective( float[] projection, float fov, float ratio, float near, float far )
	{
		projection[ 0] = 0.0f;	projection[ 1] = 0.0f;	projection[ 2] = 0.0f;	projection[ 3] = 0.0f;
		projection[ 4] = 0.0f;	projection[ 5] = 0.0f;	projection[ 6] = 0.0f;	projection[ 7] = 0.0f;
		projection[ 8] = 0.0f;	projection[ 9] = 0.0f;	projection[10] = 0.0f;	projection[11] = 0.0f;
		projection[12] = 0.0f;	projection[13] = 0.0f;	projection[14] = 0.0f;	projection[15] = 0.0f;
		
		float tanHalf = (float)Math.atan( fov / 2.0f );
		
		projection[ 0] = 1 / ( ratio * tanHalf );
		projection[ 5] = 1 / tanHalf;
		projection[10] = (far + near) / (near - far);
		projection[11] = -1.0f;
		projection[14] = -2.0f * far * near / (far - near);
	}
}
