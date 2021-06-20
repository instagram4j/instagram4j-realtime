package com.github.instagram4j.realtime.payload;

import com.github.instagram4j.instagram4j.IGConstants;
import com.github.instagram4j.instagram4j.responses.direct.DirectInboxResponse;
import lombok.Data;

@Data
public class IrisPayload {
    private final int seq_id;
    private final long snapshot_at_ms;
    private final String snapshot_app_version = IGConstants.APP_VERSION;
    
    public IrisPayload(DirectInboxResponse res) {
        this.seq_id = res.getSeq_id();
        this.snapshot_at_ms = (long) res.get("snapshot_at_ms");
    }
}
