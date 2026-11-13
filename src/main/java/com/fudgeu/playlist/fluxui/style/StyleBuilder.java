package com.fudgeu.playlist.fluxui.style;

import com.fudgeu.playlist.fluxui.style.color.Color;
import com.fudgeu.playlist.fluxui.style.color.ColorStyle;
import com.fudgeu.playlist.fluxui.style.color.SolidColor;
import com.fudgeu.playlist.fluxui.style.container.Alignment;
import com.fudgeu.playlist.fluxui.style.container.ContentSpacing;
import com.fudgeu.playlist.fluxui.style.container.LayoutDirection;
import com.fudgeu.playlist.fluxui.style.sizing.Sizing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StyleBuilder {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern pixelPattern = Pattern.compile("(\\d+(?=px))");
    private static final Pattern percentPattern = Pattern.compile("\\d+(?=%)");
    private static final Pattern hexPattern = Pattern.compile("#([A-Fa-f0-9]{6})$");

    public static HashMap<String, Style> buildStylesheet(ResourceLocation jsonFile, ResourceManager manager) throws IOException {
        HashMap<String, Style> generatedStyles = new HashMap<>();
        JsonReader reader = new JsonReader(manager.openAsReader(jsonFile));
        reader.setLenient(true);
        JsonObject stylesheetJson = JsonParser.parseReader(reader).getAsJsonObject();

        for (String styleId : stylesheetJson.keySet()) {
            JsonObject styleObject = stylesheetJson.getAsJsonObject(styleId);
            Style style = buildStyle(styleObject);
            generatedStyles.put(styleId, style);
        }

        return generatedStyles;
    }

    public static Style buildStyle(JsonObject styleObject) {
        Style style = Style.create();

        for (String propertyId : styleObject.keySet()) {
            String propertyValue = styleObject.get(propertyId).getAsString();
            applyPropertyFromJson(style, propertyId, propertyValue);
        }

        return style;
    }

    private static void applyPropertyFromJson(Style style, String propertyName, String propertyValue) {
        switch (propertyName) {
            case "width":
                applyStyleSizing(propertyName, propertyValue, style::setWidth);
                break;
            case "height":
                applyStyleSizing(propertyName, propertyValue, style::setHeight);
                break;
            case "maxWidth":
                applyStyleSizing(propertyName, propertyValue, style::setMaxWidth);
                break;
            case "maxHeight":
                applyStyleSizing(propertyName, propertyValue, style::setMaxHeight);
                break;
            case "padding":
                applyStylePx(propertyName, propertyValue, style::setPadding);
                break;
            case "paddingTop":
                applyStylePx(propertyName, propertyValue, style::setPaddingTop);
                break;
            case "paddingBottom":
                applyStylePx(propertyName, propertyValue, style::setPaddingBottom);
                break;
            case "paddingLeft":
                applyStylePx(propertyName, propertyValue, style::setPaddingLeft);
                break;
            case "paddingRight":
                applyStylePx(propertyName, propertyValue, style::setPaddingRight);
                break;
            case "paddingVertical":
                applyStylePx(propertyName, propertyValue, px -> {
                    style.setPaddingTop(px);
                    style.setPaddingBottom(px);
                });
                break;
            case "paddingHorizontal":
                applyStylePx(propertyName, propertyValue, px -> {
                    style.setPaddingLeft(px);
                    style.setPaddingRight(px);
                });
                break;
            case "contentSpacing":
                applyStyleContentSpacing(propertyName, propertyValue, style::setContentSpacing);
                break;
            case "contentAlignment":
                applyStyleAlignment(propertyName, propertyValue, style::setContentAlignment);
                break;
            case "selfAlignment":
                applyStyleAlignment(propertyName, propertyValue, style::setSelfAlignment);
                break;
            case "gap":
                applyStylePx(propertyName, propertyValue, style::setGap);
                break;
            case "layoutDirection":
                applyStyleDirection(propertyName, propertyValue, style::setLayoutDirection);
                break;
            case "borderRadius":
                applyStylePx(propertyName, propertyValue, style::setBorderRadius);
                break;
            case "backgroundColor":
                applyStyleColor(propertyName, propertyValue, style::setBackgroundColor);
                break;
            case "foregroundColor":
                applyStyleSolidColor(propertyName, propertyValue, style::setForegroundColor);
                break;
            case "translateX":
                applyStylePx(propertyName, propertyValue, style::setTranslationX);
                break;
            case "translateY":
                applyStylePx(propertyName, propertyValue, style::setTranslationY);
        }
    }

    private static void applyStylePx(String propertyName, String propertyValue, Consumer<Supplier<Integer>> styleMethod) {
        Optional<Supplier<Integer>> result = parsePixel(propertyValue);
        result.ifPresentOrElse(styleMethod, () -> logFailedParsing(propertyName, propertyValue, "Invalid format"));
    }

    private static void applyStyleSizing(String propertyName, String propertyValue, Consumer<Sizing> styleMethod) {
        Optional<Sizing> result = parseSizing(propertyValue);
        result.ifPresentOrElse(styleMethod, () -> logFailedParsing(propertyName, propertyValue, "Invalid format"));
    }

    private static void applyStyleContentSpacing(String propertyName, String propertyValue, Consumer<ContentSpacing> styleMethod) {
        Optional<ContentSpacing> result = parseContentSpacing(propertyValue);
        result.ifPresentOrElse(styleMethod, () -> logFailedParsing(propertyName, propertyValue, "Invalid content spacing type"));
    }

    private static void applyStyleAlignment(String propertyName, String propertyValue, Consumer<Alignment> styleMethod) {
        Optional<Alignment> result = parseAlignment(propertyValue);
        result.ifPresentOrElse(styleMethod, () -> logFailedParsing(propertyName, propertyValue, "Invalid alignment type"));
    }

    private static void applyStyleColor(String propertyName, String propertyValue, Consumer<ColorStyle> styleMethod) {
        Optional<ColorStyle> result = parseColor(propertyValue);
        result.ifPresentOrElse(styleMethod, () -> logFailedParsing(propertyName, propertyValue, "Invalid color format"));
    }

    private static void applyStyleSolidColor(String propertyName, String propertyValue, Consumer<SolidColor> styleMethod) {
        Optional<SolidColor> result = parseSolidColor(propertyValue);
        result.ifPresentOrElse(styleMethod, () -> logFailedParsing(propertyName, propertyValue, "Invalid color format"));
    }

    private static void applyStyleDirection(String propertyName, String propertyValue, Consumer<LayoutDirection> styleMethod) {
        Optional<LayoutDirection> result = parseDirection(propertyValue);
        result.ifPresentOrElse(styleMethod, () -> logFailedParsing(propertyName, propertyValue, "Invalid direction type"));
    }

    private static Optional<Supplier<Integer>> parsePixel(String pixelString) {
        if (!pixelString.endsWith("px")) {
            LOGGER.error("Error in stylesheet - invalid pixel value (missing 'px' suffix)");
            return Optional.empty();
        } else {
            Matcher pixelMatcher = pixelPattern.matcher(pixelString);
            if (!pixelMatcher.find()) {
                LOGGER.error("Error in stylesheet - invalid pixel value (no number value found)");
                return Optional.empty();
            } else {
                String pixelsAsString = pixelMatcher.group();

                int pixels;
                try {
                    pixels = Integer.parseInt(pixelsAsString);
                } catch (Exception var5) {
                    LOGGER.error("Error in stylesheet - invalid pixel value (cannot convert given value to integer)");
                    return Optional.empty();
                }

                return Optional.of(() -> pixels);
            }
        }
    }

    private static Optional<Float> parsePercent(String percentString) {
        if (!percentString.endsWith("%")) {
            LOGGER.error("Error in stylesheet - invalid percent value (missing '%' suffix)");
            return Optional.empty();
        } else {
            Matcher percentMatcher = percentPattern.matcher(percentString);
            if (!percentMatcher.find()) {
                LOGGER.error("Error in stylesheet - invalid percent value (no number value found)");
                return Optional.empty();
            } else {
                String percentAsString = percentMatcher.group();

                int percent;
                try {
                    percent = Integer.parseInt(percentAsString);
                } catch (Exception var5) {
                    LOGGER.error("Error in stylesheet - invalid percent value (cannot convert given value to integer)");
                    return Optional.empty();
                }

                return Optional.of(percent / 100.0F);
            }
        }
    }

    private static Optional<Sizing> parseSizing(String sizingString) {
        if (sizingString.endsWith("px")) {
            Optional<Supplier<Integer>> pixelValueOpt = parsePixel(sizingString);
            return pixelValueOpt.map(Sizing::pxSupplier);
        } else if (sizingString.endsWith("%")) {
            Optional<Float> possiblePercentValue = parsePercent(sizingString);
            return possiblePercentValue.map(Sizing::percent);
        } else {
            return Optional.empty();
        }
    }

    private static Optional<ContentSpacing> parseContentSpacing(String spacingString) {
        switch (spacingString) {
            case "spaced-evenly":
                return Optional.of(ContentSpacing.SPACED_EVENLY);
            case "spaced-around":
                return Optional.of(ContentSpacing.SPACED_AROUND);
            case "spaced-between":
                return Optional.of(ContentSpacing.SPACED_BETWEEN);
            default:
                LOGGER.error("Error in stylesheet - invalid content spacing (does not match any valid options)");
                return Optional.empty();
        }
    }

    private static Optional<Alignment> parseAlignment(String alignmentString) {
        switch (alignmentString) {
            case "start":
                return Optional.of(Alignment.START);
            case "center":
                return Optional.of(Alignment.CENTER);
            case "end":
                return Optional.of(Alignment.END);
            default:
                LOGGER.error("Error in stylesheet - invalid alignment value (does not match any valid options)");
                return Optional.empty();
        }
    }

    private static Optional<LayoutDirection> parseDirection(String directionString) {
        switch (directionString) {
            case "horizontal":
                return Optional.of(LayoutDirection.HORIZONTAL);
            case "vertical":
                return Optional.of(LayoutDirection.VERTICAL);
            case "horizontal-reversed":
                return Optional.of(LayoutDirection.HORIZONTAL_REVERSED);
            case "vertical-reversed":
                return Optional.of(LayoutDirection.VERTICAL_REVERSED);
            default:
                LOGGER.error("Error in stylesheet - invalid direction value (does not match any valid options)");
                return Optional.empty();
        }
    }

    private static Optional<ColorStyle> parseColor(String colorString) {
        if (colorString.startsWith("solid")) {
            Matcher hexMatcher = hexPattern.matcher(colorString);
            if (!hexMatcher.find()) {
                return Optional.empty();
            } else {
                String hexCode = hexMatcher.group();
                if (hexCode.isEmpty()) {
                    return Optional.empty();
                } else {
                    Color color = Color.fromHex(hexCode);
                    return Optional.of(SolidColor.create(color));
                }
            }
        } else {
            LOGGER.error("Error in stylesheet - invalid color value (does not fit required format)");
            return Optional.empty();
        }
    }

    private static Optional<SolidColor> parseSolidColor(String colorString) {
        if (colorString.startsWith("solid")) {
            Matcher hexMatcher = hexPattern.matcher(colorString);
            if (!hexMatcher.find()) {
                return Optional.empty();
            } else {
                String hexCode = hexMatcher.group();
                if (hexCode.isEmpty()) {
                    return Optional.empty();
                } else {
                    Color color = Color.fromHex(hexCode);
                    return Optional.of(SolidColor.create(color));
                }
            }
        } else {
            LOGGER.error("Error in stylesheet - invalid color value (does not fit required format)");
            return Optional.empty();
        }
    }

    private static void logFailedParsing(String propertyName, String propertyValue, String reason) {
        LOGGER.error("Unable to apply value '" + propertyValue + "' to property '" + propertyName + "': " + reason);
        LOGGER.warn("Keeping value of '" + propertyName + "' as default.");
    }
}
