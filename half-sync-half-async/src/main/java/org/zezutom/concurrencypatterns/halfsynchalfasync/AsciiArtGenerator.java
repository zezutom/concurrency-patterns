package org.zezutom.concurrencypatterns.halfsynchalfasync;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Credit goes to https://evilzone.org/java/(java-code)image-to-ascii-art-generator/
 *
 * @author: Tomas Zezula
 * Date: 27/08/2014
 */
public class AsciiArtGenerator {

  public static final double RED = 0.30;

  public static final double GREEN = 0.11;

  public static final double BLUE = 0.59;

  private BufferedImage img;

  double pixval;

  private PrintWriter printWriter;

  private FileWriter fileWriter;

  private int[] gradients = new int[]{240, 210, 190, 170, 120, 110, 80, 60};

  private char[] chars = new char[]{' ', '.', '*', '+', '^', '&', '8', '#', '@'};

  /**
   * Converts an image to its ASCII representation.
   *
   * @param imgPath path to the image, relative to /src/main/resources
   * @param outPath path to the resulting text file, relative to ./data
   * @return true, if the conversion succeeds, false otherwise
   */
  public boolean convertToAscii(String imgPath, String outPath) {
    try {
      printWriter = new PrintWriter(fileWriter = new FileWriter(outPath));
      Path filePath = Paths.get(imgPath);
      if (Files.exists(filePath)) {
        img = ImageIO.read(filePath.toFile());
      } else {
        File resource = getResource(imgPath);
        if (resource != null && resource.isFile()) {
          img = ImageIO.read(resource);
        } else {
          throw new IllegalArgumentException(String.format("File '%s' couldn't be found. Does it exist?", imgPath));
        }
      }
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException("The image couldn't be created: " + e);
    }


    for (int i = 0; i < img.getHeight(); i++) {
      for (int j = 0; j < img.getWidth(); j++) {
        Color pixcol = new Color(img.getRGB(j, i));
        pixval = (((pixcol.getRed() * RED) + (pixcol.getBlue() * BLUE) + (pixcol.getGreen() * GREEN)));
        print(px2Char(pixval));
      }
      try {
        printWriter.println("");
        printWriter.flush();
        fileWriter.flush();
      } catch (Exception e) {
        throw new RuntimeException("The awesome ASCII art couldn't be saved: " + e);
      }
    }
    return true;
  }

  private File getResource(String filename) throws URISyntaxException {
    URL resource = getClass().getResource("/" + filename);
    return (resource == null) ? null : new File(resource.toURI());
  }


  public char px2Char(double px) {
    Character c = null;

    for (int i = 0; i < gradients.length; i++) {
      if (px >= gradients[i]) {
        c = chars[i];
        break;
      }
    }

    if (c == null) {
      c = chars[chars.length - 1];
    }
    return c;
  }


  public void print(char c) {
    try {
      printWriter.print(c);
      printWriter.flush();
      fileWriter.flush();
    } catch (Exception e) {
      throw new RuntimeException("Something went wrong when saving to disc: " + e);
    }
  }

  public static void main(String[] args) {
    AsciiArtGenerator art = new AsciiArtGenerator();
    art.convertToAscii("audrey_hepburn.jpg", "audrey.txt");
  }

}
