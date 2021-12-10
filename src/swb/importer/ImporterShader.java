package swb.importer;

import java.io.File;
import java.io.IOException;

import swb.ShaderCode;
import swb.assets.Asset;
import swb.assets.ShaderAsset;

public class ImporterShader extends Importer
{
	@Override
	public Asset initAsset( String filepath ) 
	{
		return new ShaderAsset( filepath );
	}

	@Override
	public boolean loadAsset( Asset asset ) 
	{
		ShaderAsset shader = (ShaderAsset)asset;
		
		try 
		{
			String text = Importer.fileToString( new File( asset.path ) );
			shader.code = new ShaderCode( asset.path, text, ShaderCode.pathToShaderType( asset.path ) );
			asset.setInternalState( Asset.State.LOADED );
			return true;
		} 
		catch( IOException e ) 
		{
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public String[] getExtensions() 
	{
		return new String[] { "VS", "GS", "FS" };
	}
}
