package com.varian.oiscn.util.hipaa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class HipaaLogMessage {
    public static SimpleDateFormat HipaaTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    public static SimpleDateFormat SyslogTimeFormat = new SimpleDateFormat("HH:mm:ss:SSS");

    String applicationId = "Qin";

    @NonNull
    String userId;

    @NonNull
    String patientId;

    @NonNull
    HipaaEvent event;

    @NonNull
    HipaaObjectType objectType;

    @NonNull
    String objectId;

    String patientName;

    String comment = "";

    Date time;

    @Override
    public String toString(){

        String hippaaLogMessage = String.format(Locale.ROOT, "<HIPAAEvent version='1'><DateTime>%s</DateTime><Application>%s</Application><Host>%s</Host><User>%s</User>" +
                        "<EventType>%s</EventType><PatientID>%s</PatientID><PatientName>%s</PatientName><ObjectType>%s</ObjectType><ObjectID>%s</ObjectID><Comment>%s</Comment></HIPAAEvent>",
                HipaaTimeFormat.format(time == null ? Date.from(Instant.now()) : time), applicationId,  tryAndGetHostname(), userId, event.name(), patientId, patientName, objectType.name(), objectId, comment);
        String syslogMessage = String.format("<%s>%s\t%s\t%s\t%s\t%s", 110, SyslogTimeFormat.format(Date.from(Instant.now())), tryAndGetHostname(), applicationId, "LogAudit", hippaaLogMessage);
        return syslogMessage;
    }

    private String tryAndGetHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch(UnknownHostException e) {
            return "QIN-ARIA";
        }
    }
}
