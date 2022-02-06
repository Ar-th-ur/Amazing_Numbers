package numbers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public enum NumberProperty {
	BUZZ(n -> n % 7 == 0 || n % 10 == 7),
	DUCK(n -> digits(n).anyMatch(num -> num == 0)),
	EVEN(n -> n % 2 == 0),
	GAPFUL(n -> {
		String[] s = String.valueOf(n).split("");
		if (s.length < 3) {
			return false;
		}
		return n % Integer.parseInt(s[0] + s[s.length - 1]) == 0;
	}),
	JUMPING(n -> {
		int[] array = digits(n).toArray();
		for (int i = 0; i < array.length - 1; i++) {
			if (Math.abs(array[i] - array[i + 1]) != 1) {
				return false;
			}
		}
		return true;
	}),
	ODD(n -> n % 2 == 1),
	PALINDROMIC(n -> {
		String num = String.valueOf(n);
		return num.equals(new StringBuilder(num).reverse().toString());
	}),
	SPY(n -> digits(n).sum() == digits(n).reduce(1, (f, s) -> f * s)),
	SQUARE(n -> Math.sqrt(n) % 1 == 0),
	SUNNY(n -> SQUARE.test(n + 1)),
	HAPPY(n -> {
		long oldValue = n;
		while (true) {
			long newValue = digits(oldValue)
				.map(x -> x * x)
				.sum();
			if (newValue == 1) {
				return true;
			}
			if (newValue == n || newValue == 2 || newValue == 3 || newValue == 4 || newValue == 5 || newValue == 6) {
				return false;
			}
			oldValue = newValue;
		}
	}),
	SAD(n -> !HAPPY.test(n));

	private final LongPredicate rule;

	NumberProperty(LongPredicate rule) {
		this.rule = rule;
	}

	public boolean test(long number) {
		return rule.test(number);
	}

	/**
	 * Splits given number into stream of numbers
	 */
	private static IntStream digits(long number) {
		return Arrays.stream(String.valueOf(number).split(""))
			.mapToInt(Integer::valueOf);
	}

	/**
	 * Prints property of number in one line
	 */
	private static void printPropertyInOneLine(long number) {
		System.out.print(number + " is ");
		System.out.println(Arrays.stream(NumberProperty.values())
			                   .filter(prop -> prop.test(number))
			                   .map(prop -> prop.name().toLowerCase())
			                   .collect(Collectors.joining(", "))
		);
	}

	/**
	 * Print properties for one number.
	 */
	public static void singleRequest(long number) {
		System.out.println("Properties of " + number);
		System.out.println(Arrays.stream(NumberProperty.values())
			                   .map(prop -> "\t\t" + prop.name().toLowerCase() + ": " + prop.test(number))
			                   .collect(Collectors.joining("\n"))
		);
	}

	/**
	 * Prints the property of number
	 */
	public static void printProperties(long number, int range, List<String> requests) {
		if (isNegative(number)) {
			System.out.println("The first parameter should be a natural number or zero.");
			return;
		}
		if (isNegative(range)) {
			System.out.println("The second parameter should be a natural number.");
			return;
		}
		if (requests == null) {
			singleRequest(number);
		} else {
			if (isMutuallyExclusive(requests) || isImpossibleProperties(requests)) {
				return;
			}
			requiredRequest(number, range, combine(requests));
		}
	}

	/**
	 * Combines given properties into ine LongPredicate
	 */
	private static LongPredicate combine(List<String> requests) {
		return requests.stream()
			.map(String::toUpperCase)
			.map(str -> {
				if (str.startsWith("-")) {
					return (LongPredicate) l -> !NumberProperty.valueOf(str.substring(1)).test(l);
				} else {
					return (LongPredicate) l -> NumberProperty.valueOf(str).test(l);
				}
			})
			.reduce(LongPredicate::and)
			.orElse(number -> true);
	}

	/**
	 * Returns non existent properties in request
	 */
	private static boolean isImpossibleProperties(List<String> requests) {
		List<String> existent = Stream.of(NumberProperty.values())
			.map(NumberProperty::name)
			.collect(Collectors.toList());

		List<String> nonExistent = requests.stream()
			.map(str -> str.replaceFirst("-", "").toUpperCase())
			.collect(Collectors.partitioningBy(existent::contains))
			.get(false);

		if (nonExistent.isEmpty()) {
			return false;
		}

		if (nonExistent.size() == 1) {
			System.out.printf("The property [%s] is wrong.\n", nonExistent.get(0));
		} else {
			System.out.printf("The properties [%s] are wrong.\n", String.join(" ,", nonExistent));
		}

		printAvailable();
		return true;
	}

	/**
	 * Prints all available properties of number
	 */
	private static void printAvailable() {
		System.out.printf("Available properties: [%s]\n",
		                  Arrays.stream(NumberProperty.values())
			                  .map(NumberProperty::name)
			                  .collect(Collectors.joining(", "))
		);
	}

	/**
	 * Checking for mutually exclusive properties
	 */
	private static boolean isMutuallyExclusive(List<String> properties) {
		ArrayList<String> exclusive = Stream.of(
				new ArrayList<>(List.of("even", "odd")),
				new ArrayList<>(List.of("-even", "-odd")),
				new ArrayList<>(List.of("sunny", "square")),
				new ArrayList<>(List.of("-sunny", "-square")),
				new ArrayList<>(List.of("duck", "spy")),
				new ArrayList<>(List.of("-duck", "-spy")))
			.filter(properties::containsAll)
			.findAny()
			.orElse(new ArrayList<>());

		// if contains properties like even and -even then adds these properties
		for (String property : properties) {
			if (properties.contains("-" + property)) {
				exclusive.add(property);
				exclusive.add("-" + property);
			}
		}

		if (!exclusive.isEmpty()) {
			System.out.printf("The request contains mutually exclusive properties: [%s]\n",
			                  String.join(", ", exclusive));
			System.out.println("There are no numbers with these properties.");
			return true;
		}
		return false;
	}

	/**
	 * Searches for numbers that satisfy a given property
	 * @param range count of numbers that need to find
	 */
	private static void requiredRequest(long number, int range, LongPredicate predicate) {
		while (range != 0) {
			if (predicate.test(number)) {
				printPropertyInOneLine(number);
				range--;
			}
			number++;
		}
	}

	/**
	 * @return true if it`s natural number.
	 */
	public static boolean isNegative(long number) {
		return number < 0;
	}
}
