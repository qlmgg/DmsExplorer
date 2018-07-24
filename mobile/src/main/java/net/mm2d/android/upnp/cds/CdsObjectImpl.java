/*
 * Copyright (c) 2018 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.android.upnp.cds;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Date;
import java.util.List;

/**
 * CdsObjectの実装。
 *
 * @author [大前良介 (OHMAE Ryosuke)](mailto:ryo@mm2d.net)
 */
class CdsObjectImpl implements CdsObject {

    /**
     * このオブジェクトがitemか否か、itemのときtrue
     */
    private final boolean mItem;

    /**
     * DIDL-Liteノードの情報
     */
    @NonNull
    private final Tag mRootTag;

    /**
     * XMLのタグ情報。
     *
     * <p>タグ名をKeyとして、TagのListを保持する。
     * 同一のタグが複数ある場合はListに出現順に格納する。
     */
    @NonNull
    private final TagMap mTagMap;

    /**
     * MediaServerのUDN
     */
    @NonNull
    private final String mUdn;
    /**
     * \@idの値。
     */
    @NonNull
    private final String mObjectId;
    /**
     * \@parentIDの値。
     */
    @NonNull
    private final String mParentId;
    /**
     * dc:titleの値
     */
    @NonNull
    private final String mTitle;
    /**
     * upnp:classの値
     */
    @NonNull
    private final String mUpnpClass;
    /**
     * upnp:classのint値表現。
     *
     * @see #TYPE_UNKNOWN
     * @see #TYPE_VIDEO
     * @see #TYPE_AUDIO
     * @see #TYPE_IMAGE
     * @see #TYPE_CONTAINER
     */
    @ContentType
    private final int mType;

    private static class Param {
        @NonNull
        private final String mObjectId;
        @NonNull
        private final String mParentId;
        @NonNull
        private final String mTitle;
        @NonNull
        private final String mUpnpClass;

        Param(TagMap map) {
            final String objectId = map.getValue(ID);
            final String parentId = map.getValue(PARENT_ID);
            final String title = map.getValue(DC_TITLE);
            final String upnpClass = map.getValue(UPNP_CLASS);
            if (objectId == null || parentId == null || title == null || upnpClass == null) {
                throw new IllegalArgumentException("Malformed item");
            }
            mObjectId = objectId;
            mParentId = parentId;
            mTitle = title;
            mUpnpClass = upnpClass;
        }
    }

    /**
     * elementをもとにインスタンス作成
     *
     * @param udn     MediaServerのUDN
     * @param element objectを示すelement
     * @param rootTag DIDL-Liteノードの情報
     */
    CdsObjectImpl(
            @NonNull final String udn,
            @NonNull final Element element,
            @NonNull final Tag rootTag) {
        mUdn = udn;
        mItem = isItem(element.getTagName());
        mRootTag = rootTag;
        mTagMap = parseElement(element);
        final Param param = new Param(mTagMap);
        mObjectId = param.mObjectId;
        mParentId = param.mParentId;
        mTitle = param.mTitle;
        mUpnpClass = param.mUpnpClass;
        mType = getType(mItem, mUpnpClass);
    }

    private static boolean isItem(String tagName) {
        switch (tagName) {
            case ITEM:
                return true;
            case CONTAINER:
                return false;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * 子要素の情報をパースし、格納する。
     *
     * @param element objectを示すelement
     */
    @NonNull
    private static TagMap parseElement(@NonNull final Element element) {
        final TagMap map = new TagMap();
        map.putTag("", new Tag(element, true));
        Node node = element.getFirstChild();
        for (; node != null; node = node.getNextSibling()) {
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            map.putTag(node.getNodeName(), new Tag((Element) node));
        }
        return map;
    }

    @ContentType
    private static int getType(
            final boolean isItem,
            final String upnpClass) {
        if (!isItem) {
            return TYPE_CONTAINER;
        } else if (upnpClass.startsWith(IMAGE_ITEM)) {
            return TYPE_IMAGE;
        } else if (upnpClass.startsWith(AUDIO_ITEM)) {
            return TYPE_AUDIO;
        } else if (upnpClass.startsWith(VIDEO_ITEM)) {
            return TYPE_VIDEO;
        }
        return TYPE_UNKNOWN;
    }
    @Override
    @NonNull
    public String getUdn() {
        return mUdn;
    }

    @Override
    public boolean isContainer() {
        return !mItem;
    }

    @Override
    public boolean isItem() {
        return mItem;
    }

    @Override
    @ContentType
    public int getType() {
        return mType;
    }

    @Override
    @NonNull
    public String getObjectId() {
        return mObjectId;
    }

    @Override
    @NonNull
    public String getParentId() {
        return mParentId;
    }

    @Override
    @NonNull
    public String getUpnpClass() {
        return mUpnpClass;
    }

    @Override
    @NonNull
    public String getTitle() {
        return mTitle;
    }

    @Override
    @Nullable
    public String getValue(@NonNull final String xpath) {
        return mTagMap.getValue(xpath);
    }

    @Override
    @Nullable
    public String getValue(
            @NonNull final String xpath,
            final int index) {
        return mTagMap.getValue(xpath, index);
    }

    @Override
    @Nullable
    public String getValue(
            @Nullable final String tagName,
            @Nullable final String attrName) {
        return mTagMap.getValue(tagName, attrName);
    }

    @Override
    @Nullable
    public String getValue(
            @Nullable final String tagName,
            @Nullable final String attrName,
            final int index) {
        return mTagMap.getValue(tagName, attrName, index);
    }

    @Override
    @Nullable
    public Tag getTag(@Nullable final String tagName) {
        return mTagMap.getTag(tagName);
    }

    @Override
    @Nullable
    public Tag getTag(
            @Nullable final String tagName,
            final int index) {
        return mTagMap.getTag(tagName, index);
    }

    @Override
    @NonNull
    public Tag getRootTag() {
        return mRootTag;
    }

    @Override
    @NonNull
    public TagMap getTagMap() {
        return mTagMap;
    }

    @Override
    @Nullable
    public List<Tag> getTagList(@Nullable final String tagName) {
        return mTagMap.getTagList(tagName);
    }

    @Override
    public int getIntValue(
            @NonNull final String xpath,
            final int defaultValue) {
        return PropertyParser.parseIntSafely(getValue(xpath), defaultValue);
    }

    @Override
    public int getIntValue(
            @NonNull final String xpath,
            final int index,
            final int defaultValue) {
        return PropertyParser.parseIntSafely(getValue(xpath, index), defaultValue);
    }

    @Override
    @Nullable
    public Date getDateValue(@NonNull final String xpath) {
        return PropertyParser.parseDate(getValue(xpath));
    }

    @Override
    @Nullable
    public Date getDateValue(
            @NonNull final String xpath,
            final int index) {
        return PropertyParser.parseDate(getValue(xpath, index));
    }

    @Override
    public int getResourceCount() {
        final List<Tag> list = getTagList(CdsObject.RES);
        return list == null ? 0 : list.size();
    }

    @Override
    public boolean hasResource() {
        final List<Tag> tagList = getTagList(CdsObject.RES);
        return !(tagList == null || tagList.isEmpty());
    }

    @Override
    public boolean hasProtectedResource() {
        final List<Tag> tagList = getTagList(CdsObject.RES);
        if (tagList == null) {
            return false;
        }
        for (final Tag tag : tagList) {
            final String protocolInfo = tag.getAttribute(CdsObject.PROTOCOL_INFO);
            final String mimeType = PropertyParser.extractMimeTypeFromProtocolInfo(protocolInfo);
            if (!TextUtils.isEmpty(mimeType) && mimeType.equals("application/x-dtcp1")) {
                return true;
            }
        }
        return false;
    }

    @Override
    @NonNull
    public String toString() {
        return getTitle();
    }

    @Override
    @NonNull
    public String toDumpString() {
        return mTagMap.toString();
    }

    @Override
    public int hashCode() {
        return mTagMap.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CdsObjectImpl)) {
            return false;
        }
        final CdsObjectImpl obj = (CdsObjectImpl) o;
        return mObjectId.equals(obj.mObjectId) && mUdn.equals(obj.mUdn);
    }

    /**
     * Parcelable用のコンストラクタ。
     *
     * @param in Parcel
     */
    private CdsObjectImpl(@NonNull final Parcel in) {
        mUdn = in.readString();
        mItem = in.readByte() != 0;
        mRootTag = in.readParcelable(Tag.class.getClassLoader());
        mTagMap = in.readParcelable(TagMap.class.getClassLoader());
        final Param param = new Param(mTagMap);
        mObjectId = param.mObjectId;
        mParentId = param.mParentId;
        mTitle = param.mTitle;
        mUpnpClass = param.mUpnpClass;
        mType = getType(mItem, mUpnpClass);
    }

    @Override
    public void writeToParcel(
            @NonNull final Parcel dest,
            int flags) {
        dest.writeString(mUdn);
        dest.writeByte((byte) (mItem ? 1 : 0));
        dest.writeParcelable(mRootTag, flags);
        dest.writeParcelable(mTagMap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Parcelableのためのフィールド
     */
    public static final Creator<CdsObjectImpl> CREATOR = new Creator<CdsObjectImpl>() {
        @Override
        public CdsObjectImpl createFromParcel(@NonNull final Parcel in) {
            return new CdsObjectImpl(in);
        }

        @Override
        public CdsObjectImpl[] newArray(final int size) {
            return new CdsObjectImpl[size];
        }
    };
}
