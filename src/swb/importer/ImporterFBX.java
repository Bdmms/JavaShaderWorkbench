package swb.importer;

import java.io.File;
import java.io.IOException;

import swb.GLNode;

public class ImporterFBX extends Importer
{
	/**
	 * Creates an Importer than handles FBX files
	 */
	public ImporterFBX()
	{
		super( new String[] { "FBX" } );
	}
	
	@Override
	public GLNode read( File file ) throws IOException
	{
		return null;
	}
}
