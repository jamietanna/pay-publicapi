package uk.gov.pay.api.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import uk.gov.pay.api.exception.BadRequestException;
import uk.gov.pay.api.model.Address;
import uk.gov.pay.api.model.CreateCardPaymentRequest;
import uk.gov.pay.api.model.CreatePaymentRefundRequest;
import uk.gov.pay.api.model.PrefilledCardholderDetails;
import uk.gov.pay.commons.model.SupportedLanguage;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThrows;
import static uk.gov.pay.api.json.RequestJsonParser.parsePaymentRequest;
import static uk.gov.pay.api.json.RequestJsonParser.parseRefundRequest;
import static uk.gov.pay.api.matcher.BadRequestExceptionMatcher.aBadRequestExceptionWithError;
import static uk.gov.pay.commons.model.Source.CARD_API;
import static uk.gov.pay.commons.model.Source.CARD_PAYMENT_LINK;

public class RequestJsonParserTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void parsePaymentRequest_withReturnUrl_shouldParseSuccessfully() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\"\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        CreateCardPaymentRequest createPaymentRequest = parsePaymentRequest(jsonNode);

        assertThat(createPaymentRequest, is(notNullValue()));
        assertThat(createPaymentRequest.getAmount(), is(1000));
        assertThat(createPaymentRequest.getReference(), is("Some reference"));
        assertThat(createPaymentRequest.getDescription(), is("Some description"));
        assertThat(createPaymentRequest.getReturnUrl(), is("https://somewhere.gov.uk/rainbow/1"));
    }

    @Test
    public void parsePaymentRequest_withReturnUrlAndLanguageAndDelayedCaptureAndMoto_shouldParseSuccessfully() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "  \"language\": \"en\",\n" +
                "  \"delayed_capture\": true,\n" +
                "  \"moto\": true\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        CreateCardPaymentRequest createPaymentRequest = parsePaymentRequest(jsonNode);

        assertThat(createPaymentRequest, is(notNullValue()));
        assertThat(createPaymentRequest.getAmount(), is(1000));
        assertThat(createPaymentRequest.getReference(), is("Some reference"));
        assertThat(createPaymentRequest.getDescription(), is("Some description"));
        assertThat(createPaymentRequest.getReturnUrl(), is("https://somewhere.gov.uk/rainbow/1"));
        assertThat(createPaymentRequest.getLanguage(), is(Optional.of(SupportedLanguage.ENGLISH)));
        assertThat(createPaymentRequest.getDelayedCapture(), is(Optional.of(true)));
        assertThat(createPaymentRequest.getMoto(), is(Optional.of(true)));
    }

    @Test
    public void parsePaymentRefundRequest_shouldParseSuccessfully() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        CreatePaymentRefundRequest createPaymentRefundRequest = parseRefundRequest(jsonNode);

        assertThat(createPaymentRefundRequest, is(notNullValue()));
        assertThat(createPaymentRefundRequest.getAmount(), is(1000));
    }

    @Test
    public void parsePaymentRequest_whenReferenceFieldIsNotAString() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": 1234,\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\"\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: reference. Must be a valid string format"));
    }

    @Test
    public void parsePaymentRequest_whenDescriptionFieldIsNotAString() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": 1234,\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\"\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: description. Must be a valid string format"));
    }

    @Test
    public void parsePaymentRequest_whenLanguageFieldIsNotAString() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "  \"language\": 0\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: language. Must be \"en\" or \"cy\""));
    }

    @Test
    public void parsePaymentRequest_whenDelayedCaptureFieldIsNotABoolean() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "  \"delayed_capture\": \"true\"\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: delayed_capture. Must be true or false"));
    }

    @Test
    public void parsePaymentRequest_whenReturnUrlIsNotAString_shouldOverrideFormattingErrorMessage() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": 1234\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: return_url. Must be a valid URL format"));
    }

    @Test
    public void parsePaymentRequest_whenReferenceFieldIsNullValue() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": null,\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\"\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0101",
                "Missing mandatory attribute: reference"));
    }

    @Test
    public void parsePaymentRequest_whenDescriptionFieldIsNullValue() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": null,\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\"\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0101",
                "Missing mandatory attribute: description"));
    }

    @Test
    public void parsePaymentRequest_whenLanguageFieldIsNullValue() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "  \"language\": null\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: language. Must be \"en\" or \"cy\""));
    }

    @Test
    public void parsePaymentRefundRequest_whenAmountFieldIsNullValue() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": null\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parseRefundRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0601",
                "Missing mandatory attribute: amount"));
    }

    @Test
    public void parsePaymentRequest_whenAmountFieldIsMissing() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": 1234\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0101",
                "Missing mandatory attribute: amount"));
    }

    @Test
    public void parsePaymentRequest_whenReferenceFieldIsMissing() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": 1234\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0101",
                "Missing mandatory attribute: reference"));
    }

    @Test
    public void parsePaymentRequest_whenDescriptionFieldIsMissing() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"return_url\": 1234\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0101",
                "Missing mandatory attribute: description"));
    }

    @Test
    public void parsePaymentRefundRequest_whenAmountFieldIsMissing() throws Exception {
        // language=JSON
        String payload = "{}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parseRefundRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0601",
                "Missing mandatory attribute: amount"));
    }

    @Test
    public void parsePaymentRequest_whenReturnUrlIsMissing() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\"\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0101",
                "Missing mandatory attribute: return_url"));
    }

    @Test
    public void parsePaymentRequest_withAllPrefilledCardholderDetails_shouldParseSuccessfully() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "\"email\": \"j.bogs@example.org\",\n" +
                "\"prefilled_cardholder_details\": {\n" +
                "\"cardholder_name\": \"J Bogs\",\n" +
                "\"billing_address\": {\n" +
                "\"line1\": \"address line 1\",\n" +
                "\"line2\": \"address line 2\",\n" +
                "\"city\": \"address city\",\n" +
                "\"postcode\": \"AB1 CD2\",\n" +
                "\"country\": \"GB\"\n" +
                "}" + "}" + "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        CreateCardPaymentRequest createPaymentRequest = parsePaymentRequest(jsonNode);

        assertThat(createPaymentRequest, is(notNullValue()));
        assertThat(createPaymentRequest.getAmount(), is(1000));
        assertThat(createPaymentRequest.getReference(), is("Some reference"));
        assertThat(createPaymentRequest.getDescription(), is("Some description"));
        assertThat(createPaymentRequest.getReturnUrl(), is("https://somewhere.gov.uk/rainbow/1"));
        assertThat(createPaymentRequest.getEmail(), is(Optional.of("j.bogs@example.org")));
        assertThat(createPaymentRequest.getPrefilledCardholderDetails(), is(notNullValue()));
        PrefilledCardholderDetails cardholderDetails = createPaymentRequest.getPrefilledCardholderDetails().get();
        assertThat(cardholderDetails.getCardholderName().isPresent(), is(true));
        assertThat(cardholderDetails.getCardholderName().get(), is("J Bogs"));
        assertThat(cardholderDetails.getBillingAddress().isPresent(), is(true));
        Address billingAddress = cardholderDetails.getBillingAddress().get();
        assertThat(billingAddress.getLine1(), is("address line 1"));
        assertThat(billingAddress.getLine2(), is("address line 2"));
        assertThat(billingAddress.getCity(), is("address city"));
        assertThat(billingAddress.getPostcode(), is("AB1 CD2"));
        assertThat(billingAddress.getCountry(), is("GB"));
    }

    @Test
    public void parsePaymentRequest_withSomePrefilledCardholderDetails_shouldParseSuccessfully() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "\"email\": null,\n" +
                "\"prefilled_cardholder_details\": {\n" +
                "\"cardholder_name\": null,\n" +
                "\"billing_address\": {\n" +
                "\"line1\": \"address line 1\",\n" +
                "\"line2\": null,\n" +
                "\"city\": \"address city\",\n" +
                "\"postcode\": \"AB1 CD2\",\n" +
                "\"country\": \"GB\"\n" +
                "}" + "}" + "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        CreateCardPaymentRequest createPaymentRequest = parsePaymentRequest(jsonNode);

        assertThat(createPaymentRequest, is(notNullValue()));
        assertThat(createPaymentRequest.getAmount(), is(1000));
        assertThat(createPaymentRequest.getReference(), is("Some reference"));
        assertThat(createPaymentRequest.getDescription(), is("Some description"));
        assertThat(createPaymentRequest.getReturnUrl(), is("https://somewhere.gov.uk/rainbow/1"));
        assertThat(createPaymentRequest.getEmail(), is(Optional.empty()));
        assertThat(createPaymentRequest.getPrefilledCardholderDetails(), is(notNullValue()));
        assertThat(createPaymentRequest.getPrefilledCardholderDetails().isPresent(), is(true));
        PrefilledCardholderDetails cardholderDetails = createPaymentRequest.getPrefilledCardholderDetails().get();
        assertThat(cardholderDetails.getCardholderName().isPresent(), is(false));
        assertThat(cardholderDetails.getBillingAddress().isPresent(), is(true));
        Address billingAddress = cardholderDetails.getBillingAddress().get();
        assertThat(billingAddress.getLine1(), is("address line 1"));
        assertThat(billingAddress.getLine2(), is(nullValue()));
        assertThat(billingAddress.getCity(), is("address city"));
        assertThat(billingAddress.getPostcode(), is("AB1 CD2"));
        assertThat(billingAddress.getCountry(), is("GB"));
    }

    @Test
    public void parsePaymentRequest_withEmailFieldIsNotAString() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "\"email\": false,\n" +
                "\"prefilled_cardholder_details\": {\n" +
                "\"cardholder_name\": \"J Bogs\",\n" +
                "\"billing_address\": {\n" +
                "\"line1\": \"address line 1\",\n" +
                "\"line2\": \"address line 2\",\n" +
                "\"city\": \"address city\",\n" +
                "\"postcode\": \"AB1 CD2\",\n" +
                "\"country\": \"GB\"\n" +
                "}" + "}" + "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: email. Field must be a string"));
    }

    @Test
    public void parsePaymentRequest_withLine1FieldIsNotAString() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "\"email\": \"j.bogs@example.com\",\n" +
                "\"prefilled_cardholder_details\": {\n" +
                "\"cardholder_name\": \"J Bogs\",\n" +
                "\"billing_address\": {\n" +
                "\"line1\": 182,\n" +
                "\"line2\": \"address line 2\",\n" +
                "\"city\": \"address city\",\n" +
                "\"postcode\": \"AB1 CD2\",\n" +
                "\"country\": \"GB\"\n" +
                "}" + "}" + "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: line1. Field must be a string"));
    }

    @Test
    public void parsePaymentRequest_shouldSetSourceToDefaultIfNotInPayload() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 27432,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "  \"language\": \"en\"\n" +
                "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        CreateCardPaymentRequest paymentRequest = parsePaymentRequest(jsonNode);
        assertThat(paymentRequest.getInternal().get().getSource().get(), is(CARD_API));
    }

    @Test
    public void parsePaymentRequest_shouldParseSourceCorrectly() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "\"internal\": {\n" +
                "\"source\": \"CARD_PAYMENT_LINK\"\n" +
                "}" + "}";

        JsonNode jsonNode = objectMapper.readTree(payload);
        CreateCardPaymentRequest paymentRequest = parsePaymentRequest(jsonNode);
        assertThat(paymentRequest.getInternal().get().getSource().get(), is(CARD_PAYMENT_LINK));
    }

    @Test
    public void parsePaymentRequest_shouldThrowValidationException_whenSourceIsInvalidType() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "\"internal\": {\n" +
                "\"source\": true\n" +
                "}" + "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: source. Accepted value is only CARD_PAYMENT_LINK"));
    }

    @Test
    public void parsePaymentRequest_shouldThrowValidationException_whenSourceIsValidEnumTypeButNotAccepted() throws Exception {
        // language=JSON
        String payload = "{\n" +
                "  \"amount\": 1000,\n" +
                "  \"reference\": \"Some reference\",\n" +
                "  \"description\": \"Some description\",\n" +
                "  \"return_url\": \"https://somewhere.gov.uk/rainbow/1\",\n" +
                "\"internal\": {\n" +
                "\"source\": \"CARD_EXTERNAL_TELEPHONE\"\n" +
                "}" + "}";

        JsonNode jsonNode = objectMapper.readTree(payload);

        BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> parsePaymentRequest(jsonNode));
        assertThat(badRequestException, aBadRequestExceptionWithError("P0102",
                "Invalid attribute value: source. Accepted value is only CARD_PAYMENT_LINK"));
    }
}
