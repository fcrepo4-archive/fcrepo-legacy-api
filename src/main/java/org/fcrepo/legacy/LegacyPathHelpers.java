
package org.fcrepo.legacy;

public class LegacyPathHelpers {

    public static String OBJECT_PATH = "/objects";

    public static String getObjectPath(final String pid) {
        return OBJECT_PATH + "/" + pid;
    }

    public static String
            getDatastreamsPath(final String pid, final String dsid) {
        return OBJECT_PATH + "/" + pid + "/" + dsid;
    }
}
