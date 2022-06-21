package com.yoki.forum.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;

public class TextUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern HTMLTAG = Pattern.compile("<[^>]*?>");
    private static final Pattern LEAD = Pattern.compile("<p class=\"lead\">(.*?)<\\/p>");
    private static final Pattern IMG = Pattern.compile("<img .*? src=\"(.*?)\".*?>");

    private static final String PONCTUATION_GROUP_AFTER = "(?<=[;.!?])"; // Assert that the regec below maches
    private static final String TIRET = "-";
    private static final String SPACE = " ";
    private static final int MAX_SUMMARY_SIZE = 500;
    private static final int MIN_PARAGRAPH = 2;

    private TextUtils(){}

    public static String toSlug(String input) {

        String nowhitespace = WHITESPACE.matcher(input).replaceAll(TIRET);
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll(Strings.EMPTY);
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static String removetag(String input) {

        return HTMLTAG.matcher(input).replaceAll(Strings.EMPTY);
    }

    public static Optional<String> getLead(String input) {

        Matcher matcher = LEAD.matcher(input);
        if(matcher.find()){
            String lead = matcher.group(1);
            return Optional.of(lead);
        }
        return Optional.empty();
    }

    public static Optional<String> getThumb(String input) {

        Matcher matcher = IMG.matcher(input);
        if(matcher.find()){
            String img = matcher.group(1);
            return Optional.of(img);
        }
        return Optional.empty();
    }

    public static String toSummary(String input) {

        Optional<String> lead = getLead(input);
        if (lead.isPresent()){
            return removetag(lead.get());
        }

        List<String> sentenses = Arrays.stream(removetag(input).split(PONCTUATION_GROUP_AFTER))
                .collect(Collectors.toList());

        if (sentenses.size() < MIN_PARAGRAPH ) {
            return sentenses.stream().collect(Collectors.joining());
        }

        StringBuilder sBsummary = new StringBuilder(sentenses.get(0));
        int i = 1;
        while (i < sentenses.size() && sBsummary.length() < MAX_SUMMARY_SIZE - 50){
            sBsummary.append(SPACE).append(sentenses.get(i));
            i++;
        }

        String summary = sBsummary.toString();

        if (summary.length() > MAX_SUMMARY_SIZE) {
            summary = summary.substring(0, MAX_SUMMARY_SIZE) + "...";
        }

        return summary;
    }
}
