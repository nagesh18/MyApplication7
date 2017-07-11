package query;

/**
 * Created by nagesh on 28/5/17.
 */

public class SuccessModel {

    private Boolean status;
    private int code;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    private String caller_id;
    private String user_name;

    public void setCaller_id(String id){
        this.caller_id = id;
    }

    public String getCaller_id(){
        return caller_id;
    }

    public void setUsername(String username) {
        this.user_name = username;
    }
    public String getUsername(){
        return user_name;
    }

    private String message;

    public void setMessage(String msg){
        this.message = msg;
    }

    public String getMessage(){
        return message;
    }
}
