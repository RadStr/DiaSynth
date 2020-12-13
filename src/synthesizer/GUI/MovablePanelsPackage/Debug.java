package synthesizer.GUI.MovablePanelsPackage;

import java.lang.reflect.Array;

public class Debug {

    /**
     * Specific variant of debugPrintWithSep. Uses tabulator (\t) as separator.
     * @param texts are the strings, which will be written to output separated by \t.
     */
    public static void debugPrint(String... texts) {
        debugPrintWithSep("\t", texts);
    }
    /**
     * Writes the strings separated by separator. Puts new line at the end.
     * @param separator is the separator which will be put between the strings.
     * @param texts are the strings, which will be written to output separated by separator.
     */
    public static void debugPrintWithSep(String separator, String... texts) {
        // Classic implementation
//        int i;
//        for(i = 0; i < texts.length - 1; i++) {
//            System.out.print(texts[i] + separator);
//        }
//        System.out.println(texts[i]);

        //https://stackoverflow.com/questions/9633991/how-to-check-if-processing-the-last-item-in-an-iterator
        String previousSeparator = "";
        for (String text : texts) {
            System.out.print(previousSeparator + text);
            previousSeparator = separator;
        }
        System.out.println();
    }

    /**
     * Uses refraction - so it is slow. Specific variant of debugPrintWithSep. Uses tabulator (\t) as separator.
     * @param objects are the objects, on which will be called .toString() and will be written to output separated by \t.
     */
    public static void debugPrint(Object... objects) {
        debugPrintWithSep("\t", objects);
    }
    /**
     * Uses refraction - so it is slow. Writes the objects separated by separator. Puts new line at the end.
     * @param separator is the separator which will be put between the objects.
     * @param objects are the objects, on which will be called .toString() and will be written to output separated by separator.
     */
    public static void debugPrintWithSep(String separator, Object... objects) {
        //https://stackoverflow.com/questions/9633991/how-to-check-if-processing-the-last-item-in-an-iterator
        String previousSeparator = "";
        for (Object o : objects) {
//            String text = o.toString();
            System.out.print(previousSeparator + debugPrintObject(o));
//            System.out.print(previousSeparator + text);
            previousSeparator = separator;
        }
        System.out.println();
    }

    /**
     * Uses refraction to check if the given object is array, if so, then print is it in audioFormat index:\t value [space]. Else returns o.toString()
     * @param o
     * @return
     */
    public static String debugPrintObject(Object o) {
        Class<? extends Object> c = o.getClass();
        if(c.isArray()) {                                   // Check if it is array
            StringBuilder ret = new StringBuilder();
            if (c.getComponentType().isPrimitive()) {       // Check if it is array of primitives, because it needs to be iterated differently
                int length = Array.getLength(o);
                for (int i = 0; i < length; i++) {
                    Object obj = Array.get(o, i);
                    ret.append(i + ":\t" + obj + " ");
                }
            }
            else {
                Object[] arr = (Object[]) o;
                for (int i = 0; i < arr.length; i++) {
                    ret.append(i + ":\t" + arr[i] + " ");
                }
            }
            return ret.toString();
        }
        else {
            return o.toString();
        }
    }
}
