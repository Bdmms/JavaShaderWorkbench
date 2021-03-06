package swb.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import swb.GLNode;

/**
 * This an object to process a file and generate the components derived from the file.
 */
public abstract class Importer
{
	/** Map of extensions to Importer */
	private static HashMap<String, Importer> IMPORTERS = new HashMap<>();
	
	static
	{
		 new ImporterDAE();
		 new ImporterFBX();
		 new ImporterOBJ();
		 new ImporterSMD();
	}
	
	/**
	 * Creates an Importer that handles a set of file extensions
	 * @param exts - array of file extensions
	 */
	public Importer( String[] exts )
	{
		for( String ext : exts )
		{
			IMPORTERS.put( ext, this );
			System.out.println( "Added Importer: " + ext );
		}
	}
	
	/**
	 * Reads a file and generates the components derived from the file.
	 * @param file - File that is read
	 * @return The {@link GLNode} that is the root of all derived components
	 * @throws IOException - If file cannot be read or handled
	 */
	public abstract GLNode read( File file ) throws IOException;
	
	/**
	 * Retrieves the {@link Importer} that is responsible for this extension
	 * @param extension - File extension in uppercase lettering
	 * @return {@link Importer} or null
	 */
	public static Importer getImporter( String extension )
	{
		return IMPORTERS.get( extension );
	}
	
	protected static String fileToString( File file ) throws IOException
	{
		byte[] data = new byte[(int)file.length()];
		FileInputStream stream = new FileInputStream( file );
		stream.read( data );
		stream.close();
		return new String( data );
	}
}
