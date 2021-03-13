package swb.importer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import swb.Animation;
import swb.GLNode;
import swb.ModelUtils;
import swb.Skeleton;
import swb.math.mat4x4;
import swb.math.vec3f;

public class ImporterSMD extends Importer
{
	private static class SMDNode
	{
		public int id;
		public String name;
		public int parentId;
		
		public SMDNode( String[] parts )
		{
			id = Integer.parseInt( parts[0] );
			name = parts[1];
			parentId = Integer.parseInt( parts[2] );
		}
	}
	
	private static class SMDNodeFrame
	{
		int id;
		vec3f position = new vec3f();
		vec3f rotation = new vec3f();
		
		public SMDNodeFrame( String[] parts )
		{
			id = Integer.parseInt( parts[0] );
			position = new vec3f( Float.parseFloat( parts[1] ), Float.parseFloat( parts[2] ), Float.parseFloat( parts[3] ) );
			rotation = new vec3f( Float.parseFloat( parts[4] ), Float.parseFloat( parts[5] ), Float.parseFloat( parts[6] ) );
			
			// HACK
			position.div( 10.0f );
		}
	}
	
	private static class SMDFrame
	{
		public SMDNodeFrame[] nodes;
		
		public SMDFrame( int numNodes)
		{
			nodes = new SMDNodeFrame[numNodes];
		}
	}
	
	private static class SMDAnimation
	{
		List<SMDNode> nodes = new ArrayList<>();
		List<SMDFrame> frames = new ArrayList<>();
		
		public SMDFrame getFrame( int i )
		{
			for( int j = frames.size(); j <= i; j++ )
				frames.add( new SMDFrame( nodes.size() ) );
			return frames.get( i );
		}
	}
	
	private static class AnimationProcessor
	{
		private int[] parent;
		private float[] data;
		private boolean[] processed;
		private mat4x4 transform = new mat4x4();
		
		private void processAnimation( Animation animation )
		{
			processed = new boolean[animation.skeleton.size];
			parent = animation.skeleton.parent;
			data = animation.data;
			
			for( int i = 0; i < data.length; i += animation.frameStride )
			{
				for( int j = 0; j < processed.length; j++ )
					processed[j] = false;
				
				for( int j = 0; j < animation.skeleton.size; j++ )
				{
					processNode( i, j );
				}
			}
		}
		
		private void processNode( int frame, int node )
		{
			if( processed[node] ) return;
			
			int parentNode = parent[node];
			if( parentNode == -1 )
			{
				processed[node] = true;
				return;
			}
			
			processNode( frame, parentNode );
			int pNodeIdx = frame + parentNode * 6;
			int nodeIdx = frame + node * 6;
			
			transform.setTransform3D( data[pNodeIdx + 3], data[pNodeIdx + 4], data[pNodeIdx + 5], 1.0f, 1.0f, 1.0f );
			transform.transform3f( data, nodeIdx );
			
			data[nodeIdx++] += data[pNodeIdx++];
			data[nodeIdx++] += data[pNodeIdx++];
			data[nodeIdx++] += data[pNodeIdx++];
			data[nodeIdx++] += data[pNodeIdx++];
			data[nodeIdx++] += data[pNodeIdx++];
			data[nodeIdx++] += data[pNodeIdx++];
			processed[node] = true;
		}
	}
	
	/*
	private static class SMDTriangle
	{
		String material;
		vec3f position;
		vec3f normal;
		vec2f uvs;
		int links;
		int[] bones;
		float[] weights;
		
		public SMDTriangle( String[] parts )
		{
			int i = 0;
			material = parts[i++];
			position.x = Float.parseFloat( parts[i++] );
			position.y = Float.parseFloat( parts[i++] );
			position.z = Float.parseFloat( parts[i++] );
			normal.x = Float.parseFloat( parts[i++] );
			normal.y = Float.parseFloat( parts[i++] );
			normal.z = Float.parseFloat( parts[i++] );
			uvs.x = Float.parseFloat( parts[i++] );
			uvs.y = Float.parseFloat( parts[i++] );
			links = Integer.parseInt( parts[i++] );
			bones = new int[links];
			weights = new float[links];
			
			for( int j = 0; j < links; j++ )
			{
				bones[j] = Integer.parseInt( parts[i++] );
				weights[j] = Float.parseFloat( parts[i++] );
			}
		}
	}*/
	
	/**
	 * Creates an Importer than handles SMD files
	 */
	public ImporterSMD()
	{
		super( new String[] { "SMD" } );
	}
	
	private int readNodes( String[] lines, int i, List<SMDNode> nodes )
	{
		for( ; i < lines.length; i++ )
		{
			if( lines[i].equals( "end" ) ) return i;
			nodes.add( new SMDNode( lines[i].split( "\\s" ) ) );
		}
		
		return i;
	}
	
	private int readSkeleton( String[] lines, int i, SMDAnimation animation )
	{
		int time = 0;
		SMDFrame current = animation.getFrame( time );
		
		for( ; i < lines.length; i++ )
		{
			if( lines[i].equals( "end" ) ) return i;
			
			String[] parts = lines[i].split( "\\s" );
			if( parts[0].equals( "time" ) )
			{
				time = Integer.parseInt( parts[1] );
				current = animation.getFrame( time );
			}
			else
			{
				SMDNodeFrame node = new SMDNodeFrame( parts );
				current.nodes[node.id] = node;
			}
		}
		
		return i;
	}
	
	@Override
	public GLNode read( File file ) throws IOException
	{
		String[] lines = ModelUtils.fileToString( file ).split( "\r*\n" );
		
		SMDAnimation animation = new SMDAnimation();
		
		for( int i = 0; i < lines.length; i++ )
		{
			switch( lines[i] )
			{
			case "nodes": i = readNodes( lines, i + 1, animation.nodes ); break;
			case "skeleton": i = readSkeleton( lines, i + 1, animation ); break;
			case "triangles": // Unused
			}
		}
		
		// Convert animation and skeleton
		Skeleton skeleton = new Skeleton( animation.nodes.size() );
		
		for( SMDNode node : animation.nodes )
		{
			skeleton.parent[node.id] = node.parentId;
			skeleton.name[node.id] = node.name; 
		}
		
		Animation aNode = new Animation( "Animation0", skeleton, animation.frames.size() );
		
		int frameIdx = 0;
		int lastIdx = -aNode.frameStride;
		
		// TODO: support non-sequential frames
		for( SMDFrame frame : animation.frames )
		{
			for( SMDNodeFrame node : frame.nodes )
			{
				if( node == null )
				{
					System.err.println( "Unsupported operation" );
					// Copy over from last frame
					// TODO: interpolate the gaps
					aNode.data[frameIdx++] = aNode.data[lastIdx++];
					aNode.data[frameIdx++] = aNode.data[lastIdx++];
					aNode.data[frameIdx++] = aNode.data[lastIdx++];
					aNode.data[frameIdx++] = aNode.data[lastIdx++];
					aNode.data[frameIdx++] = aNode.data[lastIdx++];
					aNode.data[frameIdx++] = aNode.data[lastIdx++];
				}
				else
				{
					// Add new frame node
					node.position.copyTo( aNode.data, frameIdx );
					node.rotation.copyTo( aNode.data, frameIdx + 3 );
					frameIdx += 6;
					lastIdx += 6;
				}
			}
		}
		
		AnimationProcessor processor = new AnimationProcessor();
		processor.processAnimation( aNode );
		
		System.out.println( animation.nodes.size() + " bones" );
		System.out.println( animation.frames.size() + " frames of animation" );
		
		return aNode;
	}
}
