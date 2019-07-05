package uk.gov.pay.api.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.pay.commons.model.ErrorIdentifier;

import javax.ws.rs.core.Response;

public class ConnectorResponseErrorException extends RuntimeException {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectorResponseErrorException.class);

    private ConnectorErrorResponse error;
    private int status;

    public ConnectorResponseErrorException(Response response) {
        super(response.toString());
        this.status = response.getStatus();
        this.error = readError(response);
        response.close();
    }

    ConnectorResponseErrorException(Throwable cause) {
        super(cause);
    }

    public int getErrorStatus() {
        return status;
    }
    
    public ErrorIdentifier getErrorIdentifier() {
        return error == null ? ErrorIdentifier.GENERIC : error.errorIdentifier;
    }

    public String getReason() {
        if (error != null) {
            return error.getReason();
        }
        return null;
    }

    public boolean hasReason() {
        return this.error != null && this.error.getReason() != null;
    }

    private ConnectorErrorResponse readError(Response response) {
        ConnectorErrorResponse connectorError = null;
        try {
            connectorError = response.readEntity(ConnectorErrorResponse.class);
        } catch (Exception exception) {
            LOGGER.debug("Could not read error response from connector", exception);
        }
        return connectorError;
    }

    private String getResponseBody() {
        if (error != null) {
            return error.toString();
        }
        return null;
    }

    @Override
    public String getMessage() {
        String body = getResponseBody();
        if (body != null) {
            return super.getMessage() + " and body " + body;
        }
        return super.getMessage();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ConnectorErrorResponse {

        @JsonProperty("error_identifier")
        private ErrorIdentifier errorIdentifier;

        private String reason;

        private Object message;

        public ConnectorErrorResponse(ErrorIdentifier errorIdentifier, String reason, Object message) {
            this.errorIdentifier = errorIdentifier;
            this.reason = reason;
            this.message = message;
        }

        public ErrorIdentifier getErrorIdentifier() {
            return errorIdentifier;
        }

        public String getReason() {
            return reason;
        }
        
        public Object getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "ConnectorErrorResponse{" +
                    "error_identifier='" + errorIdentifier + '\'' +
                    ", reason='" + reason + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
}
