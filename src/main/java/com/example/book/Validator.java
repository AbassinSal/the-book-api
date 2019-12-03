package com.example.book;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Validator {
    static void publicationDateValidation(String publicationDate) {
        if (publicationDate != null && !"".equals(publicationDate) && !"Not specified".equals(publicationDate)) {
            Pattern pattern = Pattern.compile("^([0-2][0-9]|(3)[0-1])(\\.)(((0)[0-9])|((1)[0-2]))(\\.)\\d{4}$");
            Matcher matcher = pattern.matcher(publicationDate);

            if (!matcher.find()) {
                throw new WrongFormatException();
            }
        }
    }
}
