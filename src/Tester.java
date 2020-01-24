import java.math.BigInteger;
import java.io.*;

public class Tester {
	public static void main(String[] args) {
		int n = 10;
		int k = 5;
		/*
		*	mode == 1 - encoding
		*	mode == 2 - decoding
		*/
		int mode = 2;
		int[] nums = {1, 2, 3, 4};
		Shamir shamir = new Shamir (k, n);
		BigInteger secret = new BigInteger ("12345");
		try {
			if (mode == 1) shamir.encode (secret);
			if (mode == 2) {
				BigInteger decodeSecret = shamir.decode (nums, Consts.PROJECTIONS_FILENAME);
				System.out.println ("Secret is " + decodeSecret);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace ();
		}
	}
}