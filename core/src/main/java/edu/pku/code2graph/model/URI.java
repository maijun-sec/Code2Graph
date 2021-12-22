package edu.pku.code2graph.model;

import org.apache.commons.io.FilenameUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Unified Resource Identifier for code elements */
public class URI extends URILike<Layer>  implements Serializable {
  public URI() {
    this(false, "");
  }

  public URI(boolean isRef, String file) {
    this.isRef = isRef;
    this.addLayer(file, Language.OTHER);
  }

  public URI(String source) {
    String[] result = source.split("//");
    this.isRef = result[0].substring(0, result[0].length() - 1).equals("use");
    addLayer(result[1], Language.OTHER);
    Language lang = Language.valueOfLabel(FilenameUtils.getExtension(result[1]).toLowerCase());
    if (result.length <= 2) return;
    String identifier = result[2];
    addLayer(identifier, lang);
    for (int i = 3; i < result.length; ++i) {
      addLayer(result[i]);
    }
  }

  public Layer addLayer(String identifier, Language language) {
    Layer layer = new Layer(identifier, language);
    layers.add(layer);
    return layer;
  }

  // hack code

  /**
   * @deprecated
   */
  public Language getLang() {
    if (layers.size() < 1) return Language.ANY;
    return layers.get(1).getLanguage();
  }

  /**
   * @deprecated
   */
  public String getFile() {
    return layers.get(0).getIdentifier();
  }

  /**
   * @deprecated
   */
  public String getIdentifier() {
    if (layers.size() <= 1) return "";
    return layers.get(1).getIdentifier();
  }

  /**
   * @deprecated
   */
  public String getInlineIdentifier() {
    if (layers.size() <= 2) return "";
    return layers.get(2).getIdentifier();
  }

  /**
   * @deprecated
   */
  public String getSymbol() {
    String identifier = layers.get(layers.size() - 1).getIdentifier();
    String[] split = identifier.split("/");
    return split[split.length - 1];
  }

  /**
   * @deprecated
   */
  public void setIdentifier(String identifier) {
    if (layers.size() < 1) {
      addLayer(identifier);
    } else {
      layers.get(1).setIdentifier(identifier);
    }
  }

  /**
   * @deprecated
   */
  public void setInlineIdentifier(String identifier) {
    while (layers.size() <= 2) {
      addLayer("");
    }
    layers.get(2).setIdentifier(identifier);
  }

  private static List<String> pre = Arrays.asList("\\*", "\\(", "\\)", "\\/", "\\[", "\\]");

  public static String checkInvalidCh(String name) {
    for (String ch : pre) {
      Pattern p = Pattern.compile(ch);
      Matcher m = p.matcher(name);
      name = m.replaceAll("\\" + ch);
    }
    return name;
  }
}
