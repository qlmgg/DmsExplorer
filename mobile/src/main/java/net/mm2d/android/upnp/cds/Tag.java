/*
 * Copyright (c) 2016 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.android.upnp.cds;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * シンプルなXMLのタグ情報を表現するクラス
 *
 * <p>Elementのままでは情報の参照コストが高いため、
 * よりシンプルな構造に格納するためのクラス。
 * CdsObjectのXMLのようにElementが入れ子になることのない
 * タグ＋値、属性＋値の情報を表現できれば十分なものを表現するのに使用する。
 * 入れ子関係を持つXMLは表現できない。
 *
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public class Tag implements Parcelable {
    /**
     * インスタンス作成。
     *
     * パッケージ外でのインスタンス化禁止
     *
     * @param element タグ情報
     */
    static Tag create(@NonNull final Element element) {
        return create(element, false);
    }

    /**
     * インスタンス作成。
     *
     * パッケージ外でのインスタンス化禁止
     *
     * @param element タグ情報
     * @param root    タグがitem/containerのときtrue
     */
    static Tag create(
            @NonNull final Element element,
            final boolean root) {
        return create(element, root ? "" : element.getTextContent());
    }

    /**
     * インスタンス作成。
     *
     * @param element タグ情報
     * @param value   タグの値
     */
    private static Tag create(
            @NonNull final Element element,
            @NonNull final String value) {
        final String name = element.getTagName();
        final NamedNodeMap nodeMap = element.getAttributes();
        final int size = nodeMap.getLength();
        if (size == 0) {
            return new Tag(name, value, Collections.emptyMap());
        }
        final Map<String, String> attributes = new LinkedHashMap<>(size);
        for (int i = 0; i < size; i++) {
            final Node node = nodeMap.item(i);
            attributes.put(node.getNodeName(), node.getNodeValue());
        }
        return new Tag(name, value, attributes);
    }

    public static Tag EMPTY = new Tag("", "", Collections.emptyMap());
    @NonNull
    private final String mName;
    @NonNull
    private final String mValue;
    @NonNull
    private final Map<String, String> mAttributes;

    private Tag(
            @NonNull final String name,
            @NonNull final String value,
            @NonNull final Map<String, String> attributes) {
        mName = name;
        mValue = value;
        mAttributes = attributes;
    }

    /**
     * タグ名を返す。
     *
     * @return タグ名
     */
    @NonNull
    public String getName() {
        return mName;
    }

    /**
     * タグの値を返す。
     *
     * @return タグの値
     */
    @NonNull
    public String getValue() {
        return mValue;
    }

    /**
     * 属性値を返す。
     *
     * @param name 属性名
     * @return 属性値、見つからない場合null
     */
    @Nullable
    public String getAttribute(@Nullable final String name) {
        return mAttributes.get(name);
    }

    /**
     * 属性値を格納したMapを返す。
     *
     * @return 属性値を格納したUnmodifiable Map
     */
    @NonNull
    public Map<String, String> getAttributes() {
        if (mAttributes.size() == 0) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(mAttributes);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(mValue);
        for (final Entry<String, String> entry : mAttributes.entrySet()) {
            sb.append("\n");
            sb.append("@");
            sb.append(entry.getKey());
            sb.append(" => ");
            sb.append(entry.getValue());
        }
        return sb.toString();
    }

    /**
     * Parcelable用のコンストラクタ。
     *
     * @param in Parcel
     */
    private Tag(@NonNull final Parcel in) {
        mName = in.readString();
        mValue = in.readString();
        final int size = in.readInt();
        if (size == 0) {
            mAttributes = Collections.emptyMap();
        } else {
            mAttributes = new LinkedHashMap<>(size);
            for (int i = 0; i < size; i++) {
                final String name = in.readString();
                final String value = in.readString();
                mAttributes.put(name, value);
            }
        }
    }

    @Override
    public void writeToParcel(
            @NonNull final Parcel dest,
            final int flags) {
        dest.writeString(mName);
        dest.writeString(mValue);
        dest.writeInt(mAttributes.size());
        for (final Entry<String, String> entry : mAttributes.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcelableのためのフィールド
     */
    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(final Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(final int size) {
            return new Tag[size];
        }
    };
}
