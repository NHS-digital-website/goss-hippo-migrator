package uk.nhs.digital.gossmigrator.misc;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TextHelper {
    public static String toLowerCaseDashedValue(final String input) {
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")    // replace anything that isn't an alphanumeric with dash
                .replaceAll("-+", "-")           // eliminate duplicate dashes
                .replaceAll("(^-|-$)", "")       // eliminate leading or trailing dashes
                ;
    }

    public static String trimAndStripLeadingTrailingQuotes(final String input){
        return StringUtils.trim(input).replaceAll("^\"", "").replaceAll("\"$", "");
    }
    /* TODO delete if not required.  Got some very long names though......
    public static String toLowerCaseDashedShortValue(final String input) {
        return Arrays.stream(toLowerCaseDashedValue(input).split("-"))
                .map(word -> StringUtils.truncate(word, 3))
                .collect(Collectors.joining("-"));
    }
    */

}
