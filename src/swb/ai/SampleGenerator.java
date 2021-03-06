package swb.ai;

import java.util.Iterator;

public abstract class SampleGenerator implements Iterable<DataSample>, Iterator<DataSample>
{
	private final int numSamples;
	private int generated = 0;
	
	public SampleGenerator( int numSamples )
	{
		this.numSamples = numSamples;
	}
	
	@Override
	public Iterator<DataSample> iterator() 
	{
		generated = 0;
		return this;
	}
	
	@Override
	public boolean hasNext() 
	{
		return generated < numSamples;
	}

	@Override
	public DataSample next()
	{
		generated++;
		return getNext();
	}
	
	public abstract DataSample getNext();
}
