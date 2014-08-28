package org.zezutom.concurrencypatterns.halfsynchalfasync;

/**
 * A usual single-threaded implementation.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class SingleThreadedApp {

    public boolean convertToAscii(String imgPath, String outPath) {
        return new AsciiArt().convertToAscii(imgPath, outPath);
    }

    public static void main(String[] args) {
        boolean result = new SingleThreadedApp().convertToAscii("audrey_hepburn01.jpeg", "audrey.txt");
        System.out.println("RESULT: " + result);
    }
}
