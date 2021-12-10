package swb;

import com.jogamp.opengl.awt.GLJPanel;

public class ActiveCamera extends Camera
{
	private GLJPanel viewport;
	public float timer = 0.0f;
	
	public ActiveCamera( GLJPanel viewport )
	{
		this.viewport = viewport;
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
}
