package com.example.book.validation;

import com.example.book.exception.WrongFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static void publicationDateValidation(String publicationDate) {
        if (publicationDate != null && !"".equals(publicationDate) && !"Not specified".equals(publicationDate)) {
            final Pattern pattern = Pattern.compile("^([0-2][0-9]|(3)[0-1])(\\.)(((0)[0-9])|((1)[0-2]))(\\.)\\d{4}$");
            final Matcher matcher = pattern.matcher(publicationDate);

            if (!matcher.find()) {
                throw new WrongFormatException();
            }
        }
    }
}
