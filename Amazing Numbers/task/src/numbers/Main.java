package numbers;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		System.out.println("Welcome to Amazing Numbers!\n");
		System.out.println("Supported requests:\n" +
			                   "- enter a natural number to know its properties;\n" +
			                   "- enter two natural numbers to obtain the properties of the list:\n" +
			                   "  * the first parameter represents a starting number;\n" +
			                   "  * the second parameter shows how many consecutive numbers are to be printed;\n" +
			                   "- two natural numbers and properties to search for;\n" +
			                   "- a property preceded by minus must not be present in numbers;\n" +
			                   "- separate the parameters with one space;\n" +
			                   "- enter 0 to exit.\n");

		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("Enter a request:");
			String[] request = scanner.nextLine().split(" ");
			try {
				long number = Long.parseLong(request[0]);
				if (number == 0) {
					System.out.println("Goodbye!");
					break;
				}

				if (request.length == 1) {
					NumberProperty.printProperties(number, 0, null);
				} else {
					int range =  Integer.parseInt(request[1]);
					List<String> properties = Arrays.asList(request).subList(2, request.length);
					NumberProperty.printProperties(number, range, properties);
				}
			} catch (NumberFormatException ex) {
				System.out.println("The first parameter should be a natural number or zero.");
			}
		}
	}
}

