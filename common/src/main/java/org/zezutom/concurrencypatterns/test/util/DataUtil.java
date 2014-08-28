package org.zezutom.concurrencypatterns.test.util;

import java.io.File;

/**
 * @author: Tomas Zezula
 * Date: 28/08/2014
 */
public class DataUtil {

    private DataUtil() {}

    public static File getFile(String filename) {
        return new File(System.getProperty("user.dir") + "/data/" + filename);
    }
}
