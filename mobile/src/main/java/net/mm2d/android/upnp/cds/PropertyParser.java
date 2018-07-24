package net.mm2d.android.upnp.cds;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
public class PropertyParser {

    /**
     * 与えられた文字列を10進数としてパースする。
     *
     * @param value        パースする文字列
     * @param defaultValue パースできない場合のデフォルト値
     * @return パース結果
     */
    public static int parseIntSafely(
            @Nullable final String value,
            final int defaultValue) {
        return parseIntSafely(value, 10, defaultValue);
    }

    /**
     * 与えられた文字列をradix進数としてパースする。
     *
     * @param value        パースする文字列
     * @param radix        パースする文字列の基数
     * @param defaultValue パースできない場合のデフォルト値
     * @return パース結果
     */
    public static int parseIntSafely(
            @Nullable final String value,
            final int radix,
            final int defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value, radix);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    private static final DateFormat FORMAT_D = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPAN);
    private static final DateFormat FORMAT_T = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.JAPAN);
    private static final DateFormat FORMAT_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.JAPAN);

    private static Date parseD(@NonNull final String value) throws ParseException {
        synchronized (FORMAT_D) {
            return FORMAT_D.parse(value);
        }
    }

    private static Date parseT(@NonNull final String value) throws ParseException {
        synchronized (FORMAT_T) {
            return FORMAT_T.parse(value);
        }
    }

    private static Date parseZ(@NonNull final String value) throws ParseException {
        synchronized (FORMAT_Z) {
            return FORMAT_Z.parse(value);
        }
    }


    /**
     * 与えられた文字列をパースしてDateとして戻す。
     *
     * <p>CDSで使用される日付フォーマットにはいくつかバリエーションがあるが、
     * 該当するフォーマットでパースを行う。
     *
     * @param value パースする文字列
     * @return パース結果、パースできない場合null
     */
    @Nullable
    public static Date parseDate(@Nullable final String value) {
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        try {
            if (value.length() <= 10) {
                return parseD(value);
            }
            if (value.length() <= 19) {
                return parseT(value);
            }
            if (value.lastIndexOf(':') == 22) {
                return parseZ(value.substring(0, 22) + value.substring(23));
            }
            return parseZ(value);
        } catch (final ParseException e) {
            return null;
        }
    }

    /**
     * protocolInfoの文字列からMimeTypeの文字列を抽出する。
     *
     * @param protocolInfo protocolInfo
     * @return MimeTypeの文字列。抽出に失敗した場合null
     */
    @Nullable
    public static String extractMimeTypeFromProtocolInfo(@Nullable final String protocolInfo) {
        if (TextUtils.isEmpty(protocolInfo)) {
            return null;
        }
        final String[] protocols = protocolInfo.split(";");
        if (protocols.length == 0) {
            return null;
        }
        final String[] sections = protocols[0].split(":");
        if (sections.length < 3) {
            return null;
        }
        return sections[2];
    }

    /**
     * protocolInfoの文字列からProtocolの文字列を抽出する。
     *
     * @param protocolInfo protocolInfo
     * @return Protocolの文字列。抽出に失敗した場合null
     */
    @Nullable
    public static String extractProtocolFromProtocolInfo(@Nullable final String protocolInfo) {
        if (TextUtils.isEmpty(protocolInfo)) {
            return null;
        }
        final String[] protocols = protocolInfo.split(";");
        if (protocols.length == 0) {
            return null;
        }
        final String[] sections = protocols[0].split(":");
        if (sections.length < 3) {
            return null;
        }
        return sections[0];
    }
}
