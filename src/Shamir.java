import java.math.BigInteger;
import java.io.*;
import java.util.Random;
import java.util.Arrays;
import java.util.Scanner;

public class Shamir {
	private int n;
	private int k;
	private BigInteger p;
	
	/*
	*	k - минимальное кол-во человек, которое требуется для восстановления секрета
	*	n - кол-во человек, владеющие ключом
	*/
	public Shamir (int k, int n) {
		this.n = n;
		this.k = k;
	}
	
	/*
	*	Метод принимает на вход секрет и возвращает его проекции
	*/
	public BigInteger[] encode (BigInteger secret) throws FileNotFoundException {
		Random rnd = new Random ();
		int bitLength = secret.bitLength ();
		for (;;) {
			this.p = new BigInteger (bitLength, rnd);
			this.p = this.p.add (BigInteger.ONE.shiftLeft (bitLength));
			if (HelpFunctions.rabinMillerTest (p, Consts.PRIME_TEST_CHECK_COUNT)) {
				break;
			}
		}
		/*
		*	Генерация многочлена степени k - 1 с произвольными коэффициентами
		*/
		BigInteger[] coef = HelpFunctions.getPolynom (this.k - 1, bitLength - 1);
		/*
		*	Вычисление проекций для всех n пользователей
		*/
		BigInteger[] projections = new BigInteger[this.n];
		BigInteger[] x = new BigInteger[this.n];
		for (int i = 0; i < this.n; i++) {
			x[i] = BigInteger.valueOf ((long) i + 1);
		}
		for (int i = 0; i < this.n; i++) {
			projections[i] = HelpFunctions.getFunctionResult (coef, x[i], this.p);
			projections[i] = projections[i].add (secret).mod (this.p);
		}		
		/*
		*	Запись данных в файл
		*/
		PrintWriter pw = new PrintWriter (new File (Consts.PROJECTIONS_FILENAME));
		pw.print ("n=" + this.n + "\n");
		pw.print ("polynom_degree=" + (this.k - 1) + "\n");
		pw.print ("modul=" + this.p + "\n");
		for (int i = 0; i < this.n; i++) {
			pw.print ((i + 1) + "\t" + x[i] + "\t" + projections[i] + "\n");
		}
		pw.close ();
		return projections;
	}
	
	/*
	*	Метод восстанавливает секрет
	*/
	public BigInteger decode (int[] nums, String filename) throws FileNotFoundException {
		/*
		*	Загрузка необходимых параметров из файла
		*	params[..][0] - x
		*	params[..][1] - y
		*/
		BigInteger[][] params = loadParams (nums, filename);
		/*if (nums.length < this.k) {
			System.out.println ("Need more keys");
			return null;
		} else if (nums.length > this.n) {
			System.out.println ("Error");
			return null;
		}*/
		/*
		*	Многочлен Лагранжа
		*/
		BigInteger[][] lagranzhPolynom = new BigInteger[nums.length][this.k-1];
		BigInteger[] x = new BigInteger[nums.length];
		for (int i = 0; i < nums.length; i++) {
			x[i] = params[i][0];
		}
		for (int i = 0; i < nums.length; i++) {
			int indexPolynom = i;
			lagranzhPolynom[i] = HelpFunctions.lagranzhPolynom (indexPolynom, x, this.p);
		}
		/*
		*	Результирующий многочлен
		*/
		BigInteger[] res = new BigInteger[nums.length];
		for (int i = 0; i < nums.length; i++) {
			res[i] = BigInteger.ZERO;
		}
		for (int i = 0; i < nums.length; i++) {
			BigInteger scalar = params[i][1];
			BigInteger modul = this.p;
			res = HelpFunctions.polynomAdder (res, lagranzhPolynom[i], scalar, modul);
		}
		BigInteger secret = res[0];
		return secret;
	}
	
	/*
	*	Метод загружает из файла данные для вычисления секрета
	*/
	private BigInteger[][] loadParams (int[] nums, String filename) throws FileNotFoundException {
		Scanner sc = new Scanner (new File (filename));
		this.n = Integer.parseInt (sc.nextLine ().split ("=")[1]);
		this.k = Integer.parseInt (sc.nextLine ().split ("=")[1]) + 1;
		this.p = BigInteger.valueOf (Long.parseLong (sc.nextLine ().split ("=")[1]));
		BigInteger[][] params = new BigInteger[nums.length][2];
		int index = 0;
		for (int i = 0; i < this.n; i++) {
			String[] s = sc.nextLine ().split ("\t");
			int num = Integer.parseInt (s[0]);
			if (Arrays.binarySearch (nums, num) >= 0) {
				params[index][0] = new BigInteger (s[1], 10);
				params[index][1] = new BigInteger (s[2], 10);
				index++;
			}
		}
		sc.close ();
		return params;
	}
}
