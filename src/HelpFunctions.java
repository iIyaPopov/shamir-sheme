import java.math.BigInteger;
import java.util.Random;
import java.util.Arrays;

public class HelpFunctions {
	/*
	*	Вероятностный тест на простоту числа n
	*	Количество проверок равно checkCount
	*/
	public static boolean rabinMillerTest (BigInteger n, int checkCount) {
		if (n.equals(Consts.TWO)) {
			return true;
		}
		Random rnd = new Random ();
		int bitCount = n.bitLength ();
		BigInteger modul = n.subtract (BigInteger.ONE);
		BigInteger[] tmp = stGet (n);
		BigInteger s = tmp[0];
		BigInteger t = tmp[1];
		BigInteger a = BigInteger.ONE;
		for (int i = 0; i < checkCount; i++) {
			a = a.add (BigInteger.ONE).mod (modul);
			BigInteger x = a.modPow (t, n);
			if (x.equals (BigInteger.ONE) || x.equals (modul)) {
				
			} else {
				for (BigInteger j = BigInteger.ONE; j.compareTo (s.subtract (BigInteger.ONE)) <= 0; j = j.add (BigInteger.ONE)) {
					x = x.pow (2).mod (n);
					if (x.equals (BigInteger.ONE)) return false;
					if (!x.equals (s.subtract (BigInteger.ONE))) return false;
				}
				return false;
			}
		}
		return true;
	}
	
	/*
	*	Метод раскладывает число в виде 2^s * t
	*	Метод необходим для теста Рабина-Миллера
	*/
	public static BigInteger[] stGet (BigInteger n) {
		BigInteger[] res = new BigInteger[2];
		res[0] = BigInteger.ZERO;
		n = n.subtract (BigInteger.ONE);
		if (n.and (BigInteger.ONE).equals (BigInteger.ONE)) {
			res[0] = BigInteger.ZERO;
			res[1] = n;
			return res;
		}
		while (n.and (BigInteger.ONE).equals (BigInteger.ZERO)) {
			n = n.shiftRight (1);
			res[0] = res[0].add (BigInteger.ONE);
		}
		res[1] = n;
		return res;
	}
	
	/*
	*	Метод возвращает случайные коэффициенты полинома степени degree
	*/
	public static BigInteger[] getPolynom (int degree, int maxBitLength) {
		Random rnd = new Random ();
		BigInteger[] coef = new BigInteger[degree];
		for (int i = 0; i < degree; i++) {
			coef[i] = new BigInteger (maxBitLength, rnd);
			if (coef[i].equals (BigInteger.ZERO)) {
				coef[i] = coef[i].add (BigInteger.ONE);
			}
		}
		return coef;
	}
	
	/*
	*	Метод возвращает значение функции в точке x по модулю p
	*	Функция представляется в виде коэффициентов coef
	*/
	public static BigInteger getFunctionResult (BigInteger[] coef, BigInteger x, BigInteger p) {
		BigInteger res = BigInteger.ZERO;
		for (int i = 0; i < coef.length; i++) {
			res = res.add (x.modPow (BigInteger.valueOf ((long) i+1), p).multiply (coef[i]));
		}
		res = res.add (p).mod (p);
		return res;
	}
	
	/*
	*	Метод производит умножение двух полиномов
	*/
	public static BigInteger[] polynomMultiply (BigInteger[] a, BigInteger[] b, BigInteger modul) {
		int size = a.length + b.length - 1;
		BigInteger[] res = new BigInteger[size];
		for (int i = 0; i < size; i++) {
			res[i] = BigInteger.ZERO;
		}
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b.length; j++) {
				res[i+j] = res[i+j].add (a[i].multiply (b[j]));
			}
		}
		for (int i = 0; i < size; i++) {
			res[i] = res[i].mod (modul);
			if (res[i].compareTo (BigInteger.ZERO) < 0) {
				res[i] = res[i].add (modul);
			}
		}
		return res;
	}
	
	/*
	*	Многочлен Лагранжа
	*/
	public static BigInteger[] lagranzhPolynom (int polynomIndex, BigInteger[] x, BigInteger modul) {
		BigInteger[] res = new BigInteger[1];
		res[0] = BigInteger.ONE;
		BigInteger coef = BigInteger.ONE;
		for (int i = 0; i < x.length; i++) {
			if (i != polynomIndex) {
				BigInteger[] tmp = new BigInteger[2];
				tmp[0] = x[i].multiply (BigInteger.valueOf (-1));
				tmp[1] = BigInteger.ONE;
				res = polynomMultiply (res, tmp, modul);
				coef = coef.multiply (x[polynomIndex].subtract (x[i]));
			}
		}
		coef = coef.modInverse (modul);
		for (int i = 0; i < x.length; i++) {
			res[i] = res[i].multiply (coef).mod (modul);
		}
		return res;
	}
	
	/*
	*	Метод складывает два полинома a и b, умножая на скаляр
	*	Операции выполняются по модулю modul
	*/
	public static BigInteger[] polynomAdder (BigInteger[] a, BigInteger[] b, BigInteger scalar, BigInteger modul) {
		for (int i = 0; i < b.length; i++) {
			b[i] = b[i].multiply (scalar).mod (modul);
		}
		BigInteger[] x = a;
		BigInteger[] y = b;
		if (a.length < b.length) {
			x = b;
			y = a;
		}
		BigInteger[] res = x;
		for (int i = 0; i < y.length; i++) {
			res[i] = res[i].add (y[i]).mod (modul);
		}
		return res;
	}
}
