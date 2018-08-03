/**
 *
 */
package com.varian.oiscn.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base Response
 */
public class BaseResponse implements Serializable {

    /**
     * Default Id
     */
    private static final long serialVersionUID = 1L;

    /**
     * normal data
     */
    protected Object data = null;
    /**
     * 00: normal, 9x: error
     */
    protected String status = "00";
    /**
     * error list (errId: id (error-xxx), errItem: Error Item value / name
     **/
    protected List<Map<String, Object>> errors = new ArrayList<>();
    /**
     * message
     */
    protected String msg;

    /**
     * Add Error Id and Item.<br>
     *
     * @param errId
     * @param errItem
     * @return
     */
    public BaseResponse addError(String errId, Object errItem) {
        status = "99";
        Map<String, Object> err = new HashMap<>();
        err.put("id", errId);
        err.put("item", errItem);
        errors.add(err);
        return this;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public BaseResponse setData(Object data) {
        this.data = data;
        return this;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the errors
     */
    public List<Map<String, Object>> getErrors() {
        return errors;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public BaseResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }
}
