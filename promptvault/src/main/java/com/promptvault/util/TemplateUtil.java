package com.promptvault.util;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for prompt templates that use {{variable}} style placeholders,
 * e.g. "Write an email for {{role}} at {{company}} about {{technology}}".
 */
public final class TemplateUtil {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_]+)\\s*}}");

    private TemplateUtil() {
    }

    /** Returns the distinct variable names found in a template's content, in order of first appearance. */
    public static Set<String> extractVariables(String content) {
        Set<String> variables = new LinkedHashSet<>();
        if (content == null) {
            return variables;
        }
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
        return variables;
    }

    /** Renders a template by substituting {{key}} placeholders with the supplied values. */
    public static String render(String content, java.util.Map<String, String> values) {
        if (content == null) {
            return null;
        }
        String result = content;
        for (var entry : values.entrySet()) {
            result = result.replaceAll("\\{\\{\\s*" + Pattern.quote(entry.getKey()) + "\\s*}}",
                    Matcher.quoteReplacement(entry.getValue() == null ? "" : entry.getValue()));
        }
        return result;
    }
}
