package Plugins;

/**
 * Created by iBaax on 2/10/16.
 */
public class TextBoxHandler {

    public static String IsNullOrEmpty(String text) {

        try {

            if (text == null) {

                return "";
            }
            if (text.equals("null")) {

                return "";
            }

            return text;
        } catch (Exception ex) {
            return "";
        }
    }

}
