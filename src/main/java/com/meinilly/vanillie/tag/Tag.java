package com.meinilly.vanillie.tag;

import java.util.UUID;

public class Tag {
    int id = 0;
    UUID owner_uuid;
    String tag = "";

    public Tag(int id, UUID ownerUuid, String tag) {
        this.id = id;
        this.owner_uuid = ownerUuid;
        this.tag = tag;
    } 

    public UUID getOwnerUUID() {
        return this.owner_uuid;
    }

    public int getId() {
        return this.id;
    }

    public String getTagString() {
        return tag;
    }
}
