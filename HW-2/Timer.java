
public class Timer {

	public static void main(String args[]) throws Exception
	{
		long startTime = System.currentTimeMillis();
		homework.main(args);
		long endTime = System.currentTimeMillis();
		System.out.println("Minimax The running time is:"+(endTime - startTime)/1000+"s");
		
	}
}
