package com.varian.oiscn.appointment.calling;

import java.util.ArrayList;
import java.util.List;

/**
 * FHIR Service Base Exception.<br>
 */
public class CallingServiceException extends Exception {

    /**
     * Default Serial Id
     */
    private static final long serialVersionUID = 1L;
    /**
     * Server Status
     */
    protected ServerStatusEnum status = null;
    protected List<String> badItemList = new ArrayList<>();

    /**
     * Constructor.<br>
     * @param status
     */
    public CallingServiceException(ServerStatusEnum status) {
        this.status = status;
    }

    /**
     * Return Server Status.<br>
     * @return badItemList
     */
    public ServerStatusEnum getStatus() {
        return status;
    }

    /**
     * Add bad item of configuration file.
     *
     * @param badItem bad Item
     */
    public void addBadItem(String badItem) {
        badItemList.add(badItem);
    }

    /**
     * Return bad item list of configuration file.
     *
     * @return Bad Item List
     */
    public List<String> getBadItemList() {
        return badItemList;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return status.getErrMsg();
    }
}
