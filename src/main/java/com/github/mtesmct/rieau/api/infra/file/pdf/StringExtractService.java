package com.github.mtesmct.rieau.api.infra.file.pdf;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.springframework.stereotype.Service;

@Service
public class StringExtractService {
    public Optional<String> extract(String regexp, String text, int indexGroup) throws PatternSyntaxException {
		Optional<String> code = Optional.empty();
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) code = Optional.ofNullable(matcher.group(indexGroup));
        return code;
    }
}