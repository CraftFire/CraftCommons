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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Util {
    public String forumCache(String cache, String player, int userid, String nummember, String activemembers, String newusername, String newuserid, String extrausername, String lastvalue) {
        StringTokenizer st = new StringTokenizer(cache, ":");
        int i = 0;
        List<String> array = new ArrayList<String>();
        while (st.hasMoreTokens()) { array.add(st.nextToken() + ":"); }
        StringBuffer newcache = new StringBuffer();
        while (array.size() > i) {
            if (array.get(i).equals("\"" + nummember + "\";i:") && nummember != null) {
                String temp = array.get(i + 1);
                temp = removeChar(temp, '"');
                temp = removeChar(temp, ':');
                temp = removeChar(temp, 's');
                temp = removeChar(temp, ';');
                temp = temp.trim();
                int tempnum = Integer.parseInt(temp) + 1;
                if (lastvalue.equalsIgnoreCase(nummember)) {
                    temp = tempnum + ";}";
                } else {
                    temp = tempnum + ";s:";
                }
                array.set(i + 1, temp);
            } else if (array.get(i).equals("\"" + newusername + "\";s:") && newusername != null) {
                array.set(i + 1, player.length() + ":");
                if (lastvalue.equalsIgnoreCase(newusername)) {
                    array.set(i + 2, "\"" + player + "\"" + ";}");
                } else {
                    array.set(i + 2, "\"" + player + "\"" + ";s" + ":");
                }
            } else if (array.get(i).equals("\"" + extrausername + "\";s:") && extrausername != null) {
                array.set(i + 1, player.length() + ":");
                if (lastvalue.equalsIgnoreCase(extrausername)) {
                    array.set(i + 2, "\"" + player + "\"" + ";}");
                } else {
                    array.set(i + 2, "\"" + player + "\"" + ";s" + ":");
                }
            } else if (array.get(i).equals("\"" + activemembers + "\";s:") && activemembers != null) {
                String temp = array.get(i + 2);
                temp = removeChar(temp, '"');
                temp = removeChar(temp, ':');
                temp = removeChar(temp, 's');
                temp = removeChar(temp, ';');
                temp = temp.trim();
                int tempnum = Integer.parseInt(temp) + 1;
                String templength = "" + tempnum;
                if (lastvalue.equalsIgnoreCase(activemembers)) {
                    temp = "\"" + tempnum + "\"" + ";}";
                } else {
                    temp = "\"" + tempnum + "\"" + ";s:";
                }
                array.set(i + 1, templength.length() + ":");
                array.set(i + 2, temp);
            } else if (array.get(i).equals("\"" + newuserid + "\";s:") && newuserid != null) {
                String dupe = "" + userid;
                array.set(i + 1, dupe.length() + ":");
                if (lastvalue.equalsIgnoreCase(newuserid)) {
                    array.set(i + 2, "\"" + userid + "\"" + ";}");
                } else {
                    array.set(i + 2, "\"" + userid + "\"" + ";s:");
                }
            }
            newcache.append(array.get(i));
            i++;
        }
        return newcache.toString();
    }

    public String forumCacheValue(String cache, String value) {
        StringTokenizer st = new StringTokenizer(cache, ":");
        int i = 0;
        List<String> array = new ArrayList<String>();
        while (st.hasMoreTokens()) { array.add(st.nextToken() + ":"); }
        while (array.size() > i) {
            if (array.get(i).equals("\"" + value + "\";s:") && value != null) {
                String temp = array.get(i + 2);
                temp = removeChar(temp, '"');
                temp = removeChar(temp, ':');
                temp = removeChar(temp, 's');
                temp = removeChar(temp, ';');
                temp = temp.trim();
                return temp;
            }
            i++;
        }
        return "no";
    }

    public String removeChar(String s, char c) {
        StringBuffer r = new StringBuffer(s.length());
        r.setLength(s.length());
        int current = 0;
        for (int i = 0; i < s.length(); i++) {
            char cur = s.charAt(i);
            if (cur != c) r.setCharAt(current++, cur);
        }
        return r.toString();
    }
}
