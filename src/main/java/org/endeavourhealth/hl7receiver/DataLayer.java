package org.endeavourhealth.hl7receiver;

import org.endeavourhealth.common.postgres.PgResultSet;
import org.endeavourhealth.common.postgres.PgStoredProc;
import org.endeavourhealth.common.postgres.PgStoredProcException;
import org.endeavourhealth.common.postgres.logdigest.IDBDigestLogger;
import org.endeavourhealth.hl7receiver.model.db.*;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DataLayer implements IDBDigestLogger {

    private DataSource dataSource;

    public DataLayer(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public DbConfiguration getConfiguration(String hostname) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("configuration.get_configuration")
                .addParameter("_hostname", hostname);

        DbConfiguration dbConfiguration = pgStoredProc.executeMultiQuerySingleRow((resultSet) ->
                new DbConfiguration()
                        .setInstanceId(resultSet.getInt("instance_id")));

        List<DbChannel> dbChannels = pgStoredProc.executeMultiQuery((resultSet) ->
                new DbChannel()
                        .setChannelId(resultSet.getInt("channel_id"))
                        .setChannelName(resultSet.getString("channel_name"))
                        .setPortNumber(resultSet.getInt("port_number"))
                        .setActive(resultSet.getBoolean("is_active"))
                        .setUseTls(resultSet.getBoolean("use_tls"))
                        .setSendingApplication(resultSet.getString("sending_application"))
                        .setSendingFacility(resultSet.getString("sending_facility"))
                        .setReceivingApplication(resultSet.getString("receiving_application"))
                        .setReceivingFacility(resultSet.getString("receiving_facility"))
                        .setPid1Field(PgResultSet.getInteger(resultSet, "pid1_field"))
                        .setPid1AssigningAuthority(resultSet.getString("pid1_assigning_auth"))
                        .setPid2Field(PgResultSet.getInteger(resultSet, "pid2_field"))
                        .setPid2AssigningAuthority(resultSet.getString("pid2_assigning_auth"))
                        .setEdsServiceIdentifier(resultSet.getString("eds_service_identifier"))
                        .setNotes(resultSet.getString("notes")));

        List<DbChannelMessageType> dbChannelMessageTypes = pgStoredProc.executeMultiQuery((resultSet) ->
                new DbChannelMessageType()
                        .setChannelId(resultSet.getInt("channel_id"))
                        .setMessageType(resultSet.getString("message_type"))
                        .setAllowed(resultSet.getBoolean("is_allowed")));


        DbEds dbEds = pgStoredProc.executeMultiQuerySingleRow((resultSet) ->
                new DbEds()
                    .setEdsUrl(resultSet.getString("eds_url"))
                    .setSoftwareContentType(resultSet.getString("software_content_type"))
                    .setSoftwareVersion(resultSet.getString("software_version"))
                    .setUseKeycloak(resultSet.getBoolean("use_keycloak"))
                    .setKeycloakTokenUri(resultSet.getString("keycloak_token_uri"))
                    .setKeycloakRealm(resultSet.getString("keycloak_realm"))
                    .setKeycloakUsername(resultSet.getString("keycloak_username"))
                    .setKeycloakPassword(resultSet.getString("keycloak_password"))
                    .setKeycloakClientId(resultSet.getString("keycloak_clientid")));

        List<Integer> dbNotificationAttemptIntervalsSeconds = pgStoredProc.executeMultiQuery((resultSet) ->
                resultSet.getInt("interval_seconds"));

        // assemble data

        dbChannels.forEach(s ->
                s.setDbChannelMessageTypes(dbChannelMessageTypes
                        .stream()
                        .filter(t -> t.getChannelId() == s.getChannelId())
                        .collect(Collectors.toList())));

        return dbConfiguration
                .setDbChannels(dbChannels)
                .setDbEds(dbEds)
                .setDbNotificationAttemptIntervalsSeconds(dbNotificationAttemptIntervalsSeconds);
    }

    public int openConnection(int instanceId, int channelId, int localPort, String remoteHost, int remotePort) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.open_connection")
                .addParameter("_instance_id", instanceId)
                .addParameter("_channel_id", channelId)
                .addParameter("_local_port", localPort)
                .addParameter("_remote_host", remoteHost)
                .addParameter("_remote_port", remotePort);

        return pgStoredProc.executeSingleRow((resultSet) -> resultSet.getInt("connection_id"));
    }

    public void closeConnection(int connectionId) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.close_connection")
                .addParameter("_connection_id", connectionId);

        pgStoredProc.execute();
    }

    public int logMessage(
            int channelId,
            int connectionId,
            String messageControlId,
            String messageSequenceNumber,
            LocalDateTime messageDateTime,
            String pid1,
            String pid2,
            String inboundMessageType,
            String inboundPayload,
            String outboundMessageType,
            String outboundPayload) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.log_message")
                .addParameter("_channel_id", channelId)
                .addParameter("_connection_id", connectionId)
                .addParameter("_message_control_id", messageControlId)
                .addParameter("_message_sequence_number", messageSequenceNumber)
                .addParameter("_message_date", messageDateTime)
                .addParameter("_pid1", pid1)
                .addParameter("_pid2", pid2)
                .addParameter("_inbound_message_type", inboundMessageType)
                .addParameter("_inbound_payload", inboundPayload)
                .addParameter("_outbound_message_type", outboundMessageType)
                .addParameter("_outbound_payload", outboundPayload);

        return pgStoredProc.executeSingleRow((resultSet) -> resultSet.getInt("log_message"));
    }

    public int logDeadLetter(
            Integer instanceId,
            Integer channelId,
            Integer connectionId,
            String localHost,
            Integer localPort,
            String remoteHost,
            Integer remotePort,
            String sendingApplication,
            String sendingFacility,
            String receivingApplication,
            String receivingFacility,
            String messageControlId,
            String messageSequenceNumber,
            LocalDateTime messageDate,
            String pid1,
            String pid2,
            String inboundMessageType,
            String inboundPayload,
            String outboundMessageType,
            String outboundPayload,
            String exception,
            UUID deadLetterUuid) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.log_dead_letter")
                .addParameter("_instance_id", instanceId)
                .addParameter("_channel_id", channelId)
                .addParameter("_connection_id", connectionId)
                .addParameter("_local_host", localHost, 100)
                .addParameter("_local_port", localPort)
                .addParameter("_remote_host", remoteHost, 100)
                .addParameter("_remote_port", remotePort)
                .addParameter("_sending_application", sendingApplication, 100)
                .addParameter("_sending_facility", sendingFacility, 100)
                .addParameter("_receiving_application", receivingApplication, 100)
                .addParameter("_receiving_facility", receivingFacility, 100)
                .addParameter("_message_control_id", messageControlId, 100)
                .addParameter("_message_sequence_number", messageSequenceNumber, 100)
                .addParameter("_message_date", messageDate)
                .addParameter("_pid1", pid1, 100)
                .addParameter("_pid2", pid2, 100)
                .addParameter("_inbound_message_type", inboundMessageType, 100)
                .addParameter("_inbound_payload", inboundPayload)
                .addParameter("_outbound_message_type", outboundMessageType, 100)
                .addParameter("_outbound_payload", outboundPayload)
                .addParameter("_exception", exception)
                .addParameter("_dead_letter_uuid", deadLetterUuid);

        return pgStoredProc.executeSingleRow((resultSet) -> resultSet.getInt("log_dead_letter"));
    }

    public void logErrorDigest(String logClass, String logMethod, String logMessage, String exception) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.log_error_digest")
                .addParameter("_log_class", logClass)
                .addParameter("_log_method", logMethod)
                .addParameter("_log_message", logMessage)
                .addParameter("_exception", exception);

        pgStoredProc.execute();
    }

    public boolean getChannelForwarderLock(int channelId, int instanceId, int breakOthersLockSeconds) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.get_channel_forwarder_lock")
                .addParameter("_channel_id", channelId)
                .addParameter("_instance_id", instanceId)
                .addParameter("_break_others_lock_seconds", breakOthersLockSeconds);

        return pgStoredProc.executeSingleRow((resultSet) -> resultSet.getBoolean("get_channel_forwarder_lock"));
    }

    public void releaseChannelForwarderLock(int channelId, int instanceId) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.release_channel_forwarder_lock")
                .addParameter("_channel_id", channelId)
                .addParameter("_instance_id", instanceId);

        pgStoredProc.execute();
    }

    public DbMessage getNextUnnotifiedMessage(int channelId, int instanceId) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.get_next_unnotified_message")
                .addParameter("_channel_id", channelId)
                .addParameter("_instance_id", instanceId);

        return pgStoredProc.executeSingleOrEmptyRow((resultSet) ->
                        new DbMessage()
                                .setMessageId(resultSet.getInt("message_id"))
                                .setMessageControlId(resultSet.getString("message_control_id"))
                                .setMessageSequenceNumber(resultSet.getString("message_sequence_number"))
                                .setMessageDate(resultSet.getTimestamp("message_date").toLocalDateTime())
                                .setInboundMessageType(resultSet.getString("inbound_message_type"))
                                .setInboundPayload(resultSet.getString("inbound_payload"))
                                .setRequestMessageUuid(UUID.fromString(resultSet.getString("request_message_uuid"))));
    }

    public void addNotificationStatus(int messageId, boolean wasSuccess, int instanceId, UUID requestMessageUuid, String requestMessage, String responseMessage, String exceptionMessage) throws PgStoredProcException {

        PgStoredProc pgStoredProc = new PgStoredProc(dataSource)
                .setName("log.add_notification_status")
                .addParameter("_message_id", messageId)
                .addParameter("_was_success", wasSuccess)
                .addParameter("_instance_id", instanceId)
                .addParameter("_request_message_uuid", requestMessageUuid)
                .addParameter("_request_message", requestMessage)
                .addParameter("_response_message", responseMessage)
                .addParameter("_exception_message", exceptionMessage);

        pgStoredProc.execute();
    }
}
