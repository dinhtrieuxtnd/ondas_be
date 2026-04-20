package com.example.ondas_be.application.util;

import java.util.Locale;

public final class SlugUtil {

    private SlugUtil() {
    }

    public static String toSlug(String input) {
        if (input == null) {
            return null;
        }
        String slug = input.trim().toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");
        return slug.isBlank() ? "item" : slug;
    }
}
