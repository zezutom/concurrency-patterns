package org.zezutom.concurrencypatterns.halfsynchalfasync;

/**
 * A usual single-threaded implementation.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class BlockingDispatcher {

    public boolean convertToAscii(String imgPath, String outPath) {
        return new AsciiArtGenerator().convertToAscii(imgPath, outPath);
    }

    public static void main(String[] args) {
        boolean result = new BlockingDispatcher().convertToAscii("audrey_hepburn01.jpeg", "audrey.txt");
        System.out.println("RESULT: " + result);
    }
}
