package swb;

public class Time 
{
	private static long time0 = 0;
	private static long time1 = System.currentTimeMillis();
	private static long diffTime = 0;
	private static float deltaTime = 0.0f;
	
	public static void tick()
	{
		time0 = time1;
		time1 = System.currentTimeMillis();
		diffTime = time1 - time0;
		deltaTime = diffTime / 1000.0f;
	}
	
	public static long currentMilliTime() { return time1; }
	public static long deltaMilliTime() { return diffTime; }
	public static float deltaTime() { return deltaTime; }
}
