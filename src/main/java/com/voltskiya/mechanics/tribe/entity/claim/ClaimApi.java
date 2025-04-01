package com.voltskiya.mechanics.tribe.entity.claim;

import com.voltskiya.mechanics.tribe.entity.claim.query.QDTribeClaim;

public class ClaimApi {

    public static DTribeClaim findClaim(long chunkKey) {
        return new QDTribeClaim().where().chunkKey.eq(chunkKey).findOne();
    }
}
