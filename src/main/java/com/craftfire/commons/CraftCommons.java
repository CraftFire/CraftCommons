/*
 * This file is part of CraftCommons.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * CraftCommons is licensed under the GNU Lesser General Public License.
 *
 * CraftCommons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CraftCommons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.commons;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import com.craftfire.commons.encryption.BCrypt;
import com.craftfire.commons.encryption.EncryptionUtil;
import com.craftfire.commons.encryption.PHPass;
import com.craftfire.commons.encryption.Whirlpool;
import com.craftfire.commons.enums.Encryption;
import com.craftfire.commons.exceptions.AnalyticsException;
import com.craftfire.commons.managers.AnalyticsManager;

public final class CraftCommons {
    private static Util util = new Util();

    private CraftCommons() {
        try {
            new AnalyticsManager("http://stats.craftfire.com/", "CraftCommons", "1.0.0").submit();
        } catch (MalformedURLException ignore) {
        } catch (IOException e) {
            //TODO: logging
        } catch (AnalyticsException e) {
            //TODO: logging
        }
    }

    public static String getName() {
        return "CraftCommons";
    }

    public static String getVersion() {
        return "1.0.0";
    }

    /**
     * Checks if the string is an email.
     *
     * @param string The email.
     * @return true, if the string is an email.
     */
    public static boolean isEmail(String string) {
        if (string != null && ! string.isEmpty()) {
            Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
            Matcher m = p.matcher(string);
            if (m.matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if string is a valid IP. Wildcards (*) are allowed.
     *
     * @param string The IP as a string.
     * @return true, if string is an IP.
     */
    public static boolean isIP(String string) {
        if (string != null && ! string.isEmpty()) {
            String[] parts = string.split("\\.");
            if (parts.length == 4) {
                for (String s : parts) {
                    if (s.equals("*")) {
                        continue;
                    }
                    if (! isInteger(s)) {
                        return false;
                    }
                    int i = Integer.parseInt(s);
                    if (i < 0 || i > 255) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if input is an integer.
     *
     * @param input The string to parse/check.
     * @return true, if the input is an integer.
     */
    // TODO: Move it elsewhere - used in CraftCommons aswell.
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if input is a Long object.
     *
     * @param input The string to parse/check.
     * @return true, if the input is a Long object.
     */
    // TODO: Move it elsewhere - used in CraftCommons aswell.
    public static boolean isLong(String input) {
        try {
            Long.parseLong(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String long2ip(Long i) {
        return ((i >> 24) & 0xFF) + "." +
               ((i >> 16) & 0xFF) + "." +
               ((i >> 8) & 0xFF) + "." +
               (i & 0xFF);
    }

    public static Long ip2long(String ip) {
        String[] ipArray = ip.split("\\.");
        long num = 0;
        for (int i = 0; i < ipArray.length; i++) {
            int power = 3 - i;
            num += ((Integer.parseInt(ipArray[i]) % 256 * Math.pow(256, power)));
        }
        return num;
    }

    public static int getResponseCode(String urlString) {
        try {
            return getResponseCode(new URL(urlString));
        } catch (MalformedURLException e) {
            return 0;
        }
    }

    // TODO: Move it elsewhere - used in CraftCommons aswell.
    public static int getResponseCode(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
            connection.connect();
            return connection.getResponseCode();
        } catch (IOException e) {
            return 0;
        }
    }

    // TODO: Move it elsewhere - used in CraftCommons aswell.
    public static boolean isURLOnline(URL url) {
        return getResponseCode(url) == 200;
    }

    public static boolean isURLOnline(String urlString) {
        URL url = isValidURL(urlString);
        return url != null && isURLOnline(url);
    }

    public static URL isValidURL(String urlString) {
        try {
            return new URL(urlString);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static boolean hasClass(String classString) {
        try {
            Class.forName(classString);
        } catch (ClassNotFoundException ignore) {
            return false;
        }
        return true;
    }

    @Deprecated
    public static boolean inVersionRange(String lastversion, String compare) {
        if (lastversion.equalsIgnoreCase(compare)) {
            return true;
        }
        String s1 = normalisedVersion(compare);
        String s2 = normalisedVersion(lastversion);
        int cmp = s1.compareTo(s2);
        return cmp < 0;
    }

    public static String encrypt(Encryption encryption, Object object) {
        return encrypt(encryption, object, null);
    }

    public static String encrypt(Encryption encryption, Object object, String salt) {
        return encrypt(encryption, object, salt, 0);
    }

    public static String encrypt(Encryption encryption, Object object, String salt, int iterationCount) {
        try {
            String string = (String) object;
            MessageDigest md = null;
            String newSalt = salt;
            if (encryption.equals(Encryption.MD5)) {
                md = MessageDigest.getInstance("MD5");
            } else if (encryption.equals(Encryption.SHA1)) {
                md = MessageDigest.getInstance("SHA-1");
            } else if (encryption.equals(Encryption.SHA256)) {
                md = MessageDigest.getInstance("SHA-256");
            } else if (encryption.equals(Encryption.SHA512)) {
                md = MessageDigest.getInstance("SHA-512");
            } else if (encryption.equals(Encryption.CRC32)) {
                Checksum checksum = new CRC32();
                checksum.update(string.getBytes(), 0, string.getBytes().length);
                return Long.toString(checksum.getValue());
            } else if (encryption.equals(Encryption.WHIRLPOOL)) {
                Whirlpool w = new Whirlpool();
                byte[] digest = new byte[Whirlpool.DIGESTBYTES];
                w.NESSIEinit();
                w.NESSIEadd(string);
                w.NESSIEfinalize(digest);
                return Whirlpool.display(digest);
            } else if (encryption.equals(Encryption.PHPASS)) {
                PHPass phpass;
                if (iterationCount == 0) {
                    phpass = new PHPass(8);
                } else {
                    phpass = new PHPass(iterationCount);
                }
                if (newSalt == null || newSalt.isEmpty()) {
                    newSalt = phpass.gensalt();
                }
                String hash = phpass.crypt(string, newSalt);
                if (hash.length() == 34) {
                    return hash;
                }
                return "*";
            } else if (encryption.equals(Encryption.BLOWFISH)) {
                if (newSalt == null || newSalt.isEmpty()) {
                    if (iterationCount == 0) {
                        newSalt = BCrypt.gensalt(8);
                    } else {
                        newSalt = BCrypt.gensalt(iterationCount);
                    }
                }
                return BCrypt.hashpw(string, newSalt);
            }
            if (md != null) {
                md.update(string.getBytes("ISO-8859-1"), 0, string.length());
                byte[] hash = md.digest();
                return EncryptionUtil.bytesTohex(hash);
            } else {
                return null;
            }
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public static Encryption unixHashIdentify(String hash){
        if (hash.startsWith("$1$") || hash.startsWith("$md5$")) {
            return Encryption.MD5;
        } else if (hash.startsWith("$2")) {
            return Encryption.BLOWFISH;
        } else if (hash.startsWith("$5$")) {
            return Encryption.SHA256;
        } else if (hash.startsWith("$6$")) {
            return Encryption.SHA512;
        } else if (hash.startsWith("$P$") || hash.startsWith("$H$")) {
            return Encryption.PHPASS;
        }
        return null;
    }

    @Deprecated
    public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

    @Deprecated
    public static String normalisedVersion(String version, String sep, int maxWidth) {
        String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(String.format("%" + maxWidth + 's', s));
        }
        return sb.toString();
    }

    /**
     * @param urlstring The url of the image.
     * @return Image object.
     */
    public static Image urlToImage(String urlstring) {
        try {
            URL url = new URL(urlstring);
            return Toolkit.getDefaultToolkit().createImage(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String convertStreamToString(InputStream is) {
        try {
            return new java.util.Scanner(is).useDelimiter("\\A").next();
        } catch (java.util.NoSuchElementException e) {
            return "";
        }
    }

    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        for ( int i=0; i<hex.length()-1; i+=2 ) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char)decimal);
        }
        return sb.toString();
    }

    public static String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++){
            hex.append(Integer.toHexString(chars[i]));
        }
        return hex.toString();
    }

    @Deprecated
    public static String forumCache(String cache, String player, int userid, String nummember, String activemembers,
                                    String newusername, String newuserid, String extrausername, String lastvalue) {
        return util.forumCache(cache, player, userid, nummember, activemembers, newusername, newuserid,
                               extrausername, lastvalue);
    }

    @Deprecated
    public static String forumCacheValue(String cache, String value) {
        return util.forumCacheValue(cache, value);
    }

    public static String removeChar(String s, char c) {
        return util.removeChar(s, c);
    }

    public static Util getUtil() {
        return util;
    }
}
