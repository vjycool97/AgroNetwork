package agroconnectapp.agroconnect.in.agroconnect.networkclient.response;

/**
 * Created by twigly on 20/7/15.
 */
public class TServerResponse {
    public enum ServerResponse {
        USER_NOT_FOUND,
        ITEM_NOT_FOUND,
        STORE_NOT_FOUND,
        ADDRESS_NOT_FOUND,
        ORDER_NOT_FOUND,
        EMAIL_ALREADY_EXISTS,
        MOBILE_ALREADY_EXISTS,
        WRONG_PASSWORD,
        FORM_ERROR,
        LOGIN_REQUIRED,
        NOT_ALLOWED,
        SERVER_EXCEPTION,
        CACHE_ERROR,
        NO_ACTIVE_STORE,
        ITEM_SOLD_OUT,
        INVALID_ORDER_DETAIL,
        INVALID_DELIVERY_ZONE,
        NOT_ENOUGH_ITEMS_AVAILABLE,
        STORE_CLOSED,
        COUPON_NOT_FOUND,
        OK,
        UNKNOWN_ERROR,
        NO_PENDING_FEEDBACK,
        BATCHED_RESPONSE,
        NO_VALID_COUPON_USER,
        COUPON_NOT_VALID,
        COUPON_EXPIRED,
        GENERIC_ERROR,
        OTP_MAX_RETRY_REACHED,
        OTP_INVALID,
        INVALID_MOBILE,
        INVALID_EMAIL,
        VERIFICATION_PENDING,
        CANCEL_NOT_ALLOWED
    }

    ServerResponse serverResponse;
    String message;

    public TServerResponse(){
        this.serverResponse = ServerResponse.OK;
        this.message = "success";
    }

    public ServerResponse getServerResponse() {
        return serverResponse;
    }

    public void setServerResponse(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }

    public void setServerResponse(String strResponse) {
        for (ServerResponse res : ServerResponse.values()) {
            if (res.toString().equals(strResponse)) {
                this.setServerResponse(res);
                return;
            }
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
