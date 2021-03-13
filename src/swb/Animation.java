package swb;

import swb.editors.AnimationViewer;
import swb.editors.EditorView;
import swb.math.vec3f;

public class Animation extends GLNode
{
	public static final int NODE_DATA_SIZE = 6;
	
	public final float[] data;
	public final Skeleton skeleton;
	
	public final int frameCount;
	public final int frameStride;
	
	public final float startFrame;
	public final float endFrame;
	
	public Animation( String name, Skeleton bones, int numFrames )
	{
		super( name );
		skeleton = bones;
		frameCount = numFrames;
		frameStride = skeleton.size * NODE_DATA_SIZE;
		data = new float[frameCount * frameStride];
		
		startFrame = 0.0f;
		endFrame = frameCount - 1;
	}
	
	@Override
	public EditorView createEditor()
	{
		return new AnimationViewer( this, Workbench.capabilities );
	}
	
	/**
	 * Time must be valid within the animation size
	 */
	public void interpolate( float frameTime )
	{
		int iFrame = (int)frameTime;
		int pFrame = frameStride * iFrame;
		int nFrame = pFrame + frameStride;
		float w1 = frameTime - iFrame;
		float w0 = 1.0f - w1;
		
		for( int i = 0; i < skeleton.size; i++ )
		{
			vec3f pos = skeleton.position[i];
			vec3f rot = skeleton.rotation[i];
			
			pos.set( data[pFrame++], data[pFrame++], data[pFrame++] );
			rot.set( data[pFrame++], data[pFrame++], data[pFrame++] );
			
			/*
			pos.x = data[pFrame++] * w0 + data[nFrame++] * w1;
			pos.y = data[pFrame++] * w0 + data[nFrame++] * w1;
			pos.z = data[pFrame++] * w0 + data[nFrame++] * w1;
			rot.x = data[pFrame++] * w0 + data[nFrame++] * w1;
			rot.y = data[pFrame++] * w0 + data[nFrame++] * w1;
			rot.z = data[pFrame++] * w0 + data[nFrame++] * w1;*/
		}
	}
}
