package agroconnectapp.agroconnect.in.agroconnect.networkclient.response;


public class ErrorResponse extends TResponse {
    String errorMessage;

    public ErrorResponse(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public ErrorResponse(TServerResponse response, String errorMessage) {
        super(response);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
