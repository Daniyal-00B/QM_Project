import java.util.*;

public class QMSimplify {

    // دریافت مین‌ترم‌ها از ورودی رشته‌ای و تبدیل به لیست عددی
    public static List<Integer> getMinterms(String input) {
        List<Integer> minterms = new ArrayList<>();
        String[] parts = input.split(",");
        for (String part : parts) {
            try {
                minterms.add(Integer.parseInt(part.trim()));
            } catch (NumberFormatException e) {
                // نادیده گرفتن مقادیر نامعتبر
            }
        }
        return minterms;
    }

    // تبدیل لیست عددی مین‌ترم‌ها به لیست رشته‌ای باینری با طول مشخص
    public static List<String> decimalToBinary(List<Integer> minterms, int numVars) {
        List<String> binaries = new ArrayList<>();
        for (int minterm : minterms) {
            String bin = Integer.toBinaryString(minterm);
            while (bin.length() < numVars)
                bin = "0" + bin;
            binaries.add(bin);
        }
        return binaries;
    }

    // شمارش تعداد ۱ها در یک رشته باینری (برای استفاده در الگوریتم گروه‌بندی)
    public static int countOnes(String bin) {
        int count = 0;
        for (char c : bin.toCharArray())
            if (c == '1') count++;
        return count;
    }

    // بررسی اینکه آیا دو رشته باینری فقط در یک بیت تفاوت دارند و اگر بله ترکیب‌شان می‌کند
    public static String combine(String a, String b) {
        StringBuilder res = new StringBuilder();
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) == b.charAt(i))
                res.append(a.charAt(i));
            else {
                res.append('-');
                diff++;
            }
        }
        if (diff == 1)
            return res.toString();
        return null;
    }

    // اجرای مرحله ساده‌سازی Quine-McCluskey برای یافتن prime implicants
    public static Set<String> quineMcCluskey(List<Integer> minterms, int numVars) {
        List<String> binTerms = decimalToBinary(minterms, numVars);
        Set<String> primeImplicants = new HashSet<>();
        boolean combined;

        do {
            combined = false;
            List<String> newComb = new ArrayList<>();
            boolean[] used = new boolean[binTerms.size()];

            for (int i = 0; i < binTerms.size(); i++) {
                for (int j = i + 1; j < binTerms.size(); j++) {
                    String combinedTerm = combine(binTerms.get(i), binTerms.get(j));
                    if (combinedTerm != null) {
                        newComb.add(combinedTerm);
                        used[i] = true;
                        used[j] = true;
                        combined = true;
                    }
                }
            }

            for (int i = 0; i < binTerms.size(); i++) {
                if (!used[i])
                    primeImplicants.add(binTerms.get(i));
            }

            binTerms = new ArrayList<>(new HashSet<>(newComb)); // حذف تکراری‌ها

        } while (combined);

        primeImplicants.addAll(binTerms);
        return primeImplicants;
    }

    // تبدیل یک رشته باینری ساده‌شده به عبارت بولی با استفاده از متغیرهای A, B, ...
    public static String binaryToExpression(String bin) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bin.length(); i++) {
            if (bin.charAt(i) == '-')
                continue;
            char var = (char) ('A' + i);
            if (bin.charAt(i) == '0')
                sb.append(var).append('\''); // نماینده NOT
            else
                sb.append(var);
        }
        return sb.toString();
    }

    // گرفتن ورودی از کاربر
    public static InputData getInput() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Minterms (example: 0,1,2): ");
        String mInput = scanner.nextLine();
        List<Integer> minterms = getMinterms(mInput);

        System.out.print("Number of Variables: ");
        int numVars = scanner.nextInt();

        return new InputData(minterms, numVars);
    }

    // تبدیل prime implicants نهایی به عبارات بولی و نمایش خروجی
    public static void displayResult(Set<String> simplified) {
        List<String> result = new ArrayList<>();
        for (String bin : simplified)
            result.add(binaryToExpression(bin));
        System.out.println("\nSimplified Result: " + String.join(" + ", result));
    }

    // کلاس داخلی برای ذخیره ورودی‌ها با ساختاری مناسب
    public static class InputData {
        List<Integer> minterms;
        int numVars;

        public InputData(List<Integer> minterms, int numVars) {
            this.minterms = minterms;
            this.numVars = numVars;
        }
    }

    // نقطه شروع برنامه
    public static void main(String[] args) {
        InputData input = getInput();
        Set<String> simplified = quineMcCluskey(input.minterms, input.numVars);
        displayResult(simplified);
    }
}
