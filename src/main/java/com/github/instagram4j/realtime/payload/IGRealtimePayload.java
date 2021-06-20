package com.github.instagram4j.realtime.payload;

import java.util.HashMap;
import java.util.Map;
import org.apache.thrift.TException;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.IGConstants;
import com.github.instagram4j.instagram4j.utils.IGUtils;
import com.github.instagram4j.realtime.utils.ThriftUtil;
import com.github.instagram4j.realtime.utils.ThriftUtil.ThriftField;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class IGRealtimePayload {
    @ThriftField(id = 1)
    private String clientIdentifier;
    @ThriftField(id = 4)
    private ClientInfo clientInfo;
    @ThriftField(id = 5)
    private String password;
    @ThriftField(id = 10)
    private Map<String, String> appSpecificInfo = new HashMap<>();
    
    public IGRealtimePayload(IGClient client) {
        this.clientIdentifier = client.getDeviceId().substring(0, 20);
        this.password = String.format("sessionid=%s", IGUtils.getCookieValue(client.getHttpClient().cookieJar(), "sessionid").get());
        this.clientInfo = new ClientInfo(client);
        appSpecificInfo.put("app_version", IGConstants.APP_VERSION);
        appSpecificInfo.put("X-IG-Capabilities", client.getDevice().getCapabilities());
        appSpecificInfo.put("everclear_subscriptions", "{\"inapp_notification_subscribe_comment\":\"17899377895239777\",\"inapp_notification_subscribe_comment_mention_and_reply\":\"17899377895239777\",\"video_call_participant_state_delivery\":\"17977239895057311\",\"presence_subscribe\":\"17846944882223835\"}");
        appSpecificInfo.put("User-Agent", client.getDevice().getUserAgent());
        appSpecificInfo.put("Accept-Language", "en-US");
        appSpecificInfo.put("platform", "android");
        appSpecificInfo.put("ig_mqtt_route", "django");
        appSpecificInfo.put("pubsub_msg_type_blacklist", "direct, typing_type");
        appSpecificInfo.put("auth_cache_enabled", "0");
        log.debug("Realtime payload: {}", IGUtils.objectToJson(this));
    }
    
    @Data
    private class ClientInfo {
        @ThriftField(id = 1)
        private long userId;
        @ThriftField(id = 2)
        private String userAgent;
        @ThriftField(id = 3)
        private long clientCapabilities = 183;
        @ThriftField(id = 4)
        private long endpointCapabilities = 0;
        @ThriftField(id = 5)
        private int publishFormat = 1;
        @ThriftField(id = 6)
        private boolean noAutomaticForeground = false;
        @ThriftField(id = 7)
        private boolean makeUserAvailableInForeground = true;
        @ThriftField(id = 8)
        private String deviceId;
        @ThriftField(id = 9)
        private boolean isInitiallyForeground = true;
        @ThriftField(id = 10)
        private int networkType = 1;
        @ThriftField(id = 11)
        private int networkSubtype = 0;
        @ThriftField(id = 12)
        private long clientMqttSessionId = System.currentTimeMillis();
        @ThriftField(id = 14)
        private Integer[] subscribeTopics = { 88, 135, 149, 150, 133, 146 };
        @ThriftField(id = 15)
        private String clientType = "cookie_auth";
        @ThriftField(id = 16)
        private long appId = 567067343352427l;
        @ThriftField(id = 20)
        private String deviceSecret = "";
        @ThriftField(id = 21)
        private byte clientStack = 3;
        
        public ClientInfo(IGClient client) {
            this.userId = client.getSelfProfile().getPk();
            this.userAgent = client.getDevice().getUserAgent();
            this.deviceId = client.getDeviceId();
            this.appId = Long.parseLong(IGConstants.APP_ID);
         }
    }
    
    public byte[] toThriftPayload() throws IllegalArgumentException, IllegalAccessException, TException {
        return ThriftUtil.serialize(this);
    }
    
}
