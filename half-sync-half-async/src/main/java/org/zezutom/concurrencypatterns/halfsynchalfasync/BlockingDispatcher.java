package org.zezutom.concurrencypatterns.halfsynchalfasync;

/**
 * A usual single-threaded implementation.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class BlockingDispatcher {

    /**
     * Takes an image (jpg, png) and converts it to an ASCII representation.
     *
     * @param imgPath This takes either an absolute path to an image (JPG, PNG)
     *                or a relative path to a classpath resources (src/main/resources)
     * @param outPath Path to the output txt file
     * @return  True if the conversion has been successful, false otherwise.
     */
    public boolean convertToAscii(String imgPath, String outPath) {
        return new AsciiArtGenerator().convertToAscii(imgPath, outPath);
    }

    public static void main(String[] args) {
        boolean result = new BlockingDispatcher().convertToAscii("audrey_hepburn.jpg", "half-sync-half-async/data/audrey.txt");
        System.out.println("RESULT: " + result);
    }
}
