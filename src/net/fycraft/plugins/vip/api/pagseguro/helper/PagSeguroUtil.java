/*
 ************************************************************************
 Copyright [2011] [PagSeguro Internet Ltda.]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ************************************************************************
 */

package net.fycraft.plugins.vip.api.pagseguro.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.fycraft.plugins.vip.api.pagseguro.exception.PagSeguroServiceException;
import net.fycraft.plugins.vip.api.pagseguro.properties.PagSeguroSystem;

import java.util.TimeZone;

/**
 * @author asilva
 * 
 */
public class PagSeguroUtil {

    private static final int NUMBER_6 = 6;
    private static final int NUMBER_4 = 4;
    private static final int NUMBER_3 = 3;
    private static final int NUMBER_1 = 1;
    private static final int NUMBER_29 = 29;
    private static final int NUMBER_23 = 23;
    private static final int NUMBER_0 = 0;

    private static final int NUMBER_100 = 100;

    private PagSeguroUtil() {
    }

    /**
     * @param s
     * @return
     * @throws ParseException
     */
    public static Date parse(String s) throws ParseException {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        // date format: YYYY-MM-DDThh:mm:ss.sTZD
        String dateWithoutTZ = s.substring(NUMBER_0, NUMBER_23);
        String timeZone = s.substring(NUMBER_23, NUMBER_29);

        Calendar calWithoutTZ = new GregorianCalendar();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        Date date = df.parse(dateWithoutTZ);
        calWithoutTZ.setTimeInMillis(date.getTime());

        calendar.set(Calendar.YEAR, calWithoutTZ.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calWithoutTZ.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calWithoutTZ.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, calWithoutTZ.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calWithoutTZ.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calWithoutTZ.get(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calWithoutTZ.get(Calendar.MILLISECOND));

        int tzHour = Integer.parseInt(timeZone.substring(NUMBER_1, NUMBER_3));
        int tzMin = Integer.parseInt(timeZone.substring(NUMBER_4, NUMBER_6));

        boolean plus = "+".equals(timeZone.substring(NUMBER_0, NUMBER_1));

        if (plus) {
            calendar.add(Calendar.HOUR, -tzHour);
            calendar.add(Calendar.MINUTE, -tzMin);
        } else {
            calendar.add(Calendar.HOUR, tzHour);
            calendar.add(Calendar.MINUTE, tzMin);
        }
        return calendar.getTime();
    }

    /**
     * Get only numbers from a string value
     * 
     * @param value
     * @return
     */
    public static String getOnlyNumbers(String value) {
        return value.replaceAll("\\D+", "").toString();
    }

    /**
     * Format Decimal number
     * 
     * @param numeric
     * @return string
     */
    public static String decimalFormat(Double numeric) {
        Locale.setDefault(new Locale("pt", "BR"));
        DecimalFormat decimal = new DecimalFormat();
        decimal.applyPattern("#,##0.00");
        return decimal.format(numeric / NUMBER_100).replaceAll(",", ".");
    }

    /**
     * Truncate a String and add final end chars to them
     * 
     * @param String
     *            value
     * @param Integer
     *            limit
     * @param String
     *            endChars
     * @return String
     */
    public static String truncateValue(String value, int limit, String endChars) {
        String result = value;
        if (value != null && value.length() > limit) {
            result = value.substring(0, limit - endChars.length()) + endChars;
        }
        return result;
    }

    /**
     * Remove extra spaces from String
     * 
     * @param String
     *            value
     * @return String
     */
    public static String removeExtraSpaces(String value) {
        return value.replaceAll("( +)", " ").trim();
    }

    /**
     * Format a String dropping extra spaces and truncate value
     * 
     * @param String
     *            value
     * @param Integer
     *            limit
     * @param String
     *            endChars
     * @return String
     */
    public static String formatString(String value, int limit, String endChars) {
        return PagSeguroUtil.truncateValue(PagSeguroUtil.removeExtraSpaces(value), limit, endChars);
    }

    /**
     * Removes Accents and special characters
     * 
     * @param value
     * @return
     */
    public static String removeAccents(String value) {
        String result = value;
        result = value.replaceAll("[Ãƒâ€šÃƒâ‚¬Ãƒï¿½Ãƒâ€žÃƒÆ’]", "A");
        result = value.replaceAll("[ÃƒÂ¢ÃƒÂ£Ãƒ ÃƒÂ¡ÃƒÂ¤]", "a");
        result = value.replaceAll("[ÃƒÅ ÃƒË†Ãƒâ€°Ãƒâ€¹]", "E");
        result = value.replaceAll("[ÃƒÂªÃƒÂ¨ÃƒÂ©ÃƒÂ«]", "e");
        result = value.replaceAll("ÃƒÅ½Ãƒï¿½ÃƒÅ’Ãƒï¿½", "I");
        result = value.replaceAll("ÃƒÂ®ÃƒÂ­ÃƒÂ¬ÃƒÂ¯", "i");
        result = value.replaceAll("[Ãƒâ€�Ãƒâ€¢Ãƒâ€™Ãƒâ€œÃƒâ€“]", "O");
        result = value.replaceAll("[ÃƒÂ´ÃƒÂµÃƒÂ²ÃƒÂ³ÃƒÂ¶]", "o");
        result = value.replaceAll("[Ãƒâ€ºÃƒâ„¢ÃƒÅ¡ÃƒÅ“]", "U");
        result = value.replaceAll("[ÃƒÂ»ÃƒÂºÃƒÂ¹ÃƒÂ¼]", "u");
        result = value.replaceAll("Ãƒâ€¡", "C");
        result = value.replaceAll("ÃƒÂ§", "c");
        result = value.replaceAll("Ãƒï¿½", "Y");
        result = value.replaceAll("[ÃƒÂ½ÃƒÂ¿]", "y");
        result = value.replaceAll("Ãƒâ€˜", "N");
        result = value.replaceAll("ÃƒÂ±", "n");
        result = value.replaceAll("-", "");
        result = value.replaceAll("_", "");
        result = value.replaceAll("[^\\p{ASCII}]", "");
        return result;
    }

    /**
     * @param number
     * @return
     */
    public static String removeCharacterPhone(String number) {
        String result = number;
        result = number.replaceAll("[()]", "");
        result = number.replaceAll("-", "");
        result = number.replaceAll(" ", "");
        return result;
    }

    /**
     * Create url
     * 
     * @param map
     * @return string
     */
    public static String urlQuery(Map<Object, Object> map) throws PagSeguroServiceException {

        boolean first = true;
        StringBuilder sb = new StringBuilder();

        for (Entry<Object, Object> pay : map.entrySet()) {

            if (first) {
                first = false;
            } else {
                sb.append("&");
            }

            try {
                sb.append(URLEncoder.encode(pay.getKey().toString(), PagSeguroSystem.getPagSeguroEncoding()));
                sb.append("=");
                sb.append(URLEncoder.encode(pay.getValue().toString(), PagSeguroSystem.getPagSeguroEncoding()));
            } catch (UnsupportedEncodingException e) {
                throw new PagSeguroServiceException("Error when trying enconde", e);
            } catch (Exception e) {
                throw new PagSeguroServiceException("Error when trying enconde", e);
            }
        }
        return sb.toString();
    }
}
