package Helper;

public class Answer {

	public static double answer(int d) {
		double res = 1.0;
		for (int i = 0; i < d / 2; i++) {
			res *= 3.141592653589793;
		}

		int divisor = 1;
		if (d % 2 == 1) {
			res *= Math.pow(2, d / 2 + 1);
			for (int i = 1; i <= d; i += 2) {
				divisor *= i;
			}
		} else {
			for (int i = 2; i <= d / 2; i++) {
				divisor *= i;
			}
		}

		res /= (double) divisor;
		return res;
	}
}
