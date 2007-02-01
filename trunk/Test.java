public class Test
{
	public static void main(String[] args)
	{
		int[] a = new int[5];
		a[0] = 3;
		a[1] = 7;
		a[2] = 150;
		a[3] = 150;
		a[4] = 0;

		byte[] b = Constants.toByteArray(a);
		int[] c = Constants.fromByteArray(b);

		System.out.println(c[0] + " " + c[1] + " " + c[2] + " " + c[3] + " " + c[4]);
	}

}
