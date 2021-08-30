package com.curtisnewbie.module.outbox.common;

import com.curtisnewbie.common.enums.IntEnum;

/**
 * Enum for message.is_published
 *
 * @author yongjie.zhuang
 */
public enum MessageIsPublished implements IntEnum {

    NO(0),

    YES(1);

    public final int val;

    MessageIsPublished(int v) {
        this.val = v;
    }

    @Override
    public int getValue() {
        return val;
    }
}
