package org.odinallfather.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

    public static void main(String[] args) {
        String s = "1:1-5:9";
        Pattern pattern = Pattern.compile("(\\d):(\\d)-(\\d):(\\d)");
        Matcher m = pattern.matcher(s);
        if (m.matches())
            System.out.println(m.group(4));
        else
            System.out.println("No");
    }

}
