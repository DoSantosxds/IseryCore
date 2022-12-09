package net.iseryproject.core.util;

public enum LanguageConfigurationFileLocale
{
    ENGLISH("en"),
    EXPLICIT("ex"),
    FRENCH("fr"),
    SPANISH("es"),
    PORTUGUESE("pt");

    private final String abbreviation;

    private LanguageConfigurationFileLocale(final String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public static LanguageConfigurationFileLocale getByAbbreviation(final String abbreviation) {
        for (final LanguageConfigurationFileLocale locale : values()) {
            if (locale.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return locale;
            }
        }
        return LanguageConfigurationFileLocale.ENGLISH;
    }

    public static LanguageConfigurationFileLocale getByName(final String name) {
        for (final LanguageConfigurationFileLocale locale : values()) {
            if (locale.name().equalsIgnoreCase(name)) {
                return locale;
            }
        }
        return LanguageConfigurationFileLocale.ENGLISH;
    }

    public String getAbbreviation() {
        return this.abbreviation;
    }
}
