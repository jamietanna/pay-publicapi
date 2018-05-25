package uk.gov.pay.api.model.directdebit;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "AgreementRequestCreate", description = "The Agreement Payload to create a new Agreement")
public class CreateAgreementResponse {
    private String name;
    private String email;
    private AgreementType type;
    private String agreementId;
    private AgreementStatus status;

    @ApiModelProperty(value = "agreement name", required = true, example = "Service Name agreement")
    @JsonProperty(value = "name")
    public String getName() {
        return this.name;
    }

    @ApiModelProperty(value = "email", required = true, example = "email@example.com")
    @JsonProperty(value = "email")
    public String getEmail() {
        return this.email;
    }

    @ApiModelProperty(value = "agreement type", required = true, example = "on demand")
    @JsonProperty(value = "type")
    public AgreementType getType() {
        return this.type;
    }

    @ApiModelProperty(value = "agreement id", required = true, example = "jhjcvaiqlediuhh23d89hd3")
    @JsonProperty(value = "agreement_id")
    public String getAgreementId() {
        return this.agreementId;
    }

    @ApiModelProperty(value = "agreement status", required = true, example = "CREATED")
    @JsonProperty(value = "status")
    public AgreementStatus getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "AgreementRequestCreate{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", id='" + agreementId + '\'' +
                ", status='" + status + '\'' +
                ", type=" + type +
                '}';
    }
}
