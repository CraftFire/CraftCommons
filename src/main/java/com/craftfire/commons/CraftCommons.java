/*
 * This file is part of CraftCommons <http://www.craftfire.com/>.
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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.craftfire.commons.encryption.EncryptionUtil;
import com.craftfire.commons.encryption.Whirlpool;

public class CraftCommons {
    /**
     * Checks if the string is an email.
     *
     * @param string The email.
     * @return true, if the string is an email.
     */
    public static boolean isEmail(String string) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(string);
        if (m.matches()) {
          return true;
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
        String[] parts = string.split ("\\.");
        if(parts.length == 4) {
            for (String s : parts) {
                if(s == "*") {
                    continue;
                }
                if(!isInteger(s)) {
                    return false;
                }
                int i = Integer.parseInt(s);
                if (i < 0 || i > 255) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if input is an integer.
     *
     * @param input The string to parse/check.
     * @return true, if the input is an integer.
     */
    public static boolean isInteger(String input) {
        try {
           Integer.parseInt(input);
           return true;
        } catch(Exception e) {
           return false;
        }
     }

    public static boolean inVersionRange(String lastversion, String compare) {
        if(lastversion.equalsIgnoreCase(compare)) {
            return true;
        }
        String s1 = normalisedVersion(compare);
        String s2 = normalisedVersion(lastversion);
        int cmp = s1.compareTo(s2);
        if(cmp < 0) {
            return true;
        }
        return false;
    }

    /**
     * Converts a string into a MD5 hash.
     *
     * @param string The string that is going to be encrypted.
     * @return Returns a MD5 hash of the string.
     */
    public static String md5(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes("ISO-8859-1"), 0, string.length());
            byte[] hash = md.digest();
            return EncryptionUtil.bytesTohex(hash);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a string into a SHA-1 hash.
     *
     * @param string The string that is going to be encrypted.
     * @return Returns a SHA-1 hash of the string.
     */
    public static String sha1(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(string.getBytes("ISO-8859-1"), 0, string.length());
            byte[] hash = md.digest();
            return EncryptionUtil.bytesTohex(hash);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a string into a SHA-256 hash.
     *
     * @param string The string that is going to be encrypted.
     * @return Returns a SHA-256 hash of the string.
     */
    public static String sha256(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(string.getBytes("ISO-8859-1"), 0, string.length());
            byte[] hash = md.digest();
            return EncryptionUtil.bytesTohex(hash);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a string into a SHA-512 hash.
     *
     * @param string The string that is going to be encrypted.
     * @return Returns a SHA-512 hash of the string.
     */
    public static String sha512(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(string.getBytes("ISO-8859-1"), 0, string.length());
            byte[] hash = md.digest();
            return EncryptionUtil.bytesTohex(hash);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a string into a Whirpool hash.
     *
     * @param string The string that is going to be encrypted.
     * @return Returns a Whirpool hash of the string.
     */
    public static String whirlpool(String string) {
        Whirlpool w = new Whirlpool();
        byte[] digest = new byte[Whirlpool.DIGESTBYTES];
        w.NESSIEinit();
        w.NESSIEadd(string);
        w.NESSIEfinalize(digest);
        return  Whirlpool.display(digest);
    }

    public static String normalisedVersion(String version) {
        return normalisedVersion(version, ".", 4);
    }

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
            return Toolkit.getDefaultToolkit().getDefaultToolkit().createImage(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String forumCache(String cache, String player, int userid, String nummember, String activemembers, String newusername, String newuserid, String extrausername, String lastvalue) {
        Util util = new Util();
        return util.forumCache(cache, player, userid, nummember, activemembers, newusername, newuserid, extrausername, lastvalue);
    }

    public static String forumCacheValue(String cache, String value) {
        Util util = new Util();
        return util.forumCacheValue(cache, value);
    }

    public static String removeChar(String s, char c) {
        Util util = new Util();
        return util.removeChar(s, c);
    }
}
