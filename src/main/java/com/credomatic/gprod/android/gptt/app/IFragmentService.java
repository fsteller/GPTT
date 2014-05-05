package com.credomatic.gprod.android.gptt.app;

/**
 * Created by fhernandezs on 18/12/13 for GProdTestingTool.
 */
public interface IFragmentService {

    public static final String ARG_SERVICE_ID = "service_id";

    public abstract void restoreDefaultValues();

    public abstract void reloadValues();

}
