import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Создаём ошибку, которая нужна будет в случае некорректной работы.
class Ex_mistake extends Exception{
    public Ex_mistake(String print_err) {
        System.err.println(print_err);
    }
}

// Ошибка в случае деления на 0.
class Ex_nol extends Exception {
    public Ex_nol(){
        System.err.println("Ошибка. Делить на нуль нельзя!!!");
    }
}

// Класс включает в себя создание дроби, её упрощение (сокращение) и непосредственное превращение в число.
class Fraction{

    private final String a;

    /**Проверка на дробь и её возможное дополнение до дефолтного занчения 1, если нет числа после или до слеша*/
    public Fraction (String input) throws Ex_mistake, Ex_nol {
        Pattern no_extra_sign = Pattern.compile("^-?" + "[0-9]*" + "/" + "-?" + "[0-9]*$");
        Matcher ok = no_extra_sign.matcher(input);
        if (ok.find()) {
            Pattern slash_zero = Pattern.compile("/-?0");
            Matcher zero = slash_zero.matcher(input);
            if (!zero.find()){
                int slash = input.indexOf("/");
                int len = input.length() - 1;
                if (slash == 0){
                    input = 1 + input;
                }
                if (slash == len){
                    input = input + 1;
                }
                this.a = input;

            }
            else {
                throw new Ex_nol();
            }
        }
        else {
            throw new Ex_mistake("Была введена не дробь");
        }
    }

    //Превращение строковой дроби в число.
    public double make_double() {
        String f = a;

        int slash = f.indexOf("/");
        int length = f.length();

        StringBuilder time = new StringBuilder(f);
        time.delete(slash, length);
        double first = Double.parseDouble(String.valueOf(time));

        time = new StringBuilder(f);
        time.delete(0, slash + 1);
        double second = Double.parseDouble(String.valueOf(time));

        return (first/second);
    }

    // Сокращение дроби, используя поиск общего делителя.
    public Fraction cut() throws Ex_nol, Ex_mistake {
        String f = a;

        int slash = f.indexOf("/");
        int length = f.length();

        StringBuilder time = new StringBuilder(f);
        time.delete(slash, length);
        double first = Double.parseDouble(String.valueOf(time));

        time = new StringBuilder(f);
        time.delete(0, slash + 1);
        double second = Double.parseDouble(String.valueOf(time));

        int num;
        for (num = 999; num > 1; num--){
            if (first % num == 0 && second % num == 0){
                first = first/num;
                second = second/num;
                break;
            }
        }

        f = (int) first + "/" + (int) second;

        return new Fraction(f);
    }

    public String toString() {
        return a;
    }
}

/* Класс различных манипуляций, содержащих превращение числа в строковую дробь.
   Счёт выражений со скобками и без.
 */

class Manipulations {

    // Создание дроби путём умножения её на для избавления дробного остатка после запятой.
    protected static Fraction make_frac (double a) throws Ex_nol, Ex_mistake {
        int b = (int) a;
        int zero = 0;
        while (b / a != 1) {
            a = a * 10;
            b = (int) a;
            zero++;
            if (zero > 6) {
                break;
            }
        }
        String one = "1";
        for (int i = 0; i < zero; i++) {
            one = one + "0";
        }
        String time = b + "/" + one;
        return new Fraction(time).cut();
    }

    private static double plus(Fraction first, Fraction second) {
        double a = first.make_double();
        double b = second.make_double();
        return a + b;
    }

    private static double minus (Fraction first, Fraction second) {
        double a = first.make_double();
        double b = second.make_double();
        return a - b;
    }

    private static double product(Fraction first, Fraction second) {
        double a = first.make_double();
        double b = second.make_double();
        return a * b;
    }

    private static double quotient (Fraction first, Fraction second) {
        double a = first.make_double();
        double b = second.make_double();
        return a / b;
    }

    public static double with_brackets (String input) throws Ex_nol, Ex_mistake {
        double answer;

        Pattern open = Pattern.compile("\\(");
        Pattern close = Pattern.compile("\\)");

        StringBuilder time = new StringBuilder(input);

        Matcher open_bracket = open.matcher(time);
        Matcher close_bracket = close.matcher(time);

        while (open_bracket.find() || close_bracket.find()) {
            open_bracket = open.matcher(time);
            if (open_bracket.find()) {
                int start = open_bracket.end();
                time.delete(start - 1, start);
            } else {
                throw new Ex_mistake("Скобка закрыта, но не открыта");
            }
            close_bracket = close.matcher(time);
            if (close_bracket.find()) {
                int end = close_bracket.end();
                time.delete(end - 1, end);
            } else {
                throw new Ex_mistake("Скобка открыта, но не закрыта");
            }
            open_bracket = open.matcher(time);
            close_bracket = close.matcher(time);
        }

        time = new StringBuilder(input);
        StringBuilder already = new StringBuilder(input);
        open_bracket = open.matcher(time);
        close_bracket = close.matcher(time);
        int i = 0;
        while (open_bracket.find() && close_bracket.find()) {
            i++;
            int start = open_bracket.end();
            int end = close_bracket.end();
            int time_start = 0;
            time.delete(0, start).delete(end - start - 1, time.length());
            open_bracket = open.matcher(time);
            while (open_bracket.find()) {
                time_start = open_bracket.end();
                time.delete(0, time_start);
                open_bracket = open.matcher(time);
            }
            Fraction fraction_in_bracket = make_frac(infinity_string(String.valueOf(time)));
            start = start + time_start;
            if (i > 1){
                end = end + 1;
            }
            already.replace(start - 1, end, String.valueOf(fraction_in_bracket));
            close_bracket = close.matcher(already);
            open_bracket = open.matcher(already);
            time = new StringBuilder(already);
        }
        answer = infinity_string(String.valueOf(already));
        return answer;
    }

    public static double infinity_string(String input) throws Ex_nol, Ex_mistake {
        double answer;
        Pattern firs = Pattern.compile("^-?" + "[0-9]*" + "/" + "-?" + "[0-9]*");
        Matcher get_first = firs.matcher(input);

        if (get_first.find()) {
            int end = get_first.end();
            int length = input.length();
            StringBuilder another = new StringBuilder();

            StringBuilder time = new StringBuilder(input);
            if ((length - end) > 0) {
                another = time.delete(0, end);
                time = new StringBuilder(input);
                time.delete(end, length);
            }

            Fraction already_get = new Fraction(String.valueOf(time));
            answer = already_get.make_double();

            while (another.length() != 0) {
                Pattern another_frac = Pattern.compile("^\s" + "[-*/+]" + "\s" + "-?" + "[0-9]*" + "/" + "-?" + "[0-9]*");
                Matcher get_next_frac = another_frac.matcher(String.valueOf(another));
                if (get_next_frac.find()) {
                    end = get_next_frac.end();
                    length = String.valueOf(another).length();
                    time = new StringBuilder(another);
                    if ((length - end) > 0) {
                        time.delete(end, length);
                    }
                    answer = all_manipulations(already_get + String.valueOf(time));
                    already_get = make_frac(answer);
                    another.delete(0, end);
                }
                else{
                    throw new Ex_mistake("Введено неверное выражение!");
                }
            }
        }
        else {
            throw new Ex_mistake("Введено неверное выражение!");
        }
        return answer;
    }

    private static double all_manipulations (String input) throws Ex_mistake, Ex_nol {
        int something = input.indexOf(" ") + 1;
        int length = input.length();

        StringBuilder time = new StringBuilder(input);
        time.delete(something - 1, length);
        Fraction first = new Fraction(String.valueOf(time));

        time = new StringBuilder(input);
        time.delete(0, something + 2);
        Fraction second = new Fraction(String.valueOf(time));

        time = new StringBuilder(input);
        String what_do = String.valueOf(time.charAt(something));

        double result = 0;
        if (what_do.equals("+")) {
            result = Manipulations.plus(first, second);
        }
        if (what_do.equals("*")) {
            result = Manipulations.product(first, second);
        }
        if (what_do.equals("-")) {
            result = Manipulations.minus(first, second);
        }
        if (what_do.equals("/")) {
            result = Manipulations.quotient(first, second);
        }

        return result;
    }
}

public class Final_Lab {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String input;
        do {
            System.out.print("Введите выражение или quit, если хотите закончить: ");
            input = in.nextLine();
            Pattern open_close = Pattern.compile("\\(|\\)");
            Matcher bracket = open_close.matcher(input);
            try {
                double answer;
                if(bracket.find()) {
                    answer = Manipulations.with_brackets(input);
                }
                else {
                    answer = Manipulations.infinity_string(input);
                }
                Fraction fraction = Manipulations.make_frac(answer);
                System.out.println("Ответ: " + fraction + " = " + answer);
            } catch (Ex_nol ex) {
                System.out.println("Проверьте выражение и введите заново без деления на 0");
            } catch (Ex_mistake ex) {
                if (input.equals("quit")) {
                    System.out.println("Выполнен выход из программы.");
                    System.exit(0);
                }
                else {
                    System.out.println("Проверьте выражение на наличие лишних знаков или нехватки пробелов");
                }
            }
        } while (!input.equals("stop"));

        in.close();
    }
}