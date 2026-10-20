package com.hollow.util;

/**
 * HTML 转义工具类。
 *
 * 用户输入的内容（帖子正文、评论、昵称）输出到页面前必须转义，
 * 否则别人写一段 <script> 就能在别人的浏览器里执行。
 *
 * 注意：用 JSTL 的 <c:out> 输出时它已经自动转义了，不用再调这里。
 * 只有自己拼字符串（out.print）的时候才需要手动调 escape。
 */
public class HtmlUtil {

    /**
     * 把 & < > " ' 这五个字符换成对应的实体。
     *
     * 顺序很重要：& 必须第一个换，
     * 否则会把后面换出来的 &lt; 里的 & 又转成 &amp;lt;。
     */
    public static String escape(String text) {
        if (text == null) {
            return "";
        }

        String result = text;
        result = result.replace("&", "&amp;");   // 必须最先
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("\"", "&quot;");
        result = result.replace("'", "&#39;");

        return result;
    }
}
