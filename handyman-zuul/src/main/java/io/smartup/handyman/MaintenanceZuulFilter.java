package io.smartup.handyman;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.smartup.handyman.model.MaintenanceStatus;

public class MaintenanceZuulFilter extends ZuulFilter {

    private static final int HTTP_ERROR_CODE_MAINTENANCE = 503;

    private final MaintenanceStatusProvider maintenanceStatusProvider;

    public MaintenanceZuulFilter(MaintenanceStatusProvider maintenanceStatusProvider) {
        this.maintenanceStatusProvider = maintenanceStatusProvider;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        MaintenanceStatus s = maintenanceStatusProvider.getStatus();

        if (s.getMode() != MaintenanceStatus.MaintenanceMode.OFF) {
            RequestContext ctx = RequestContext.getCurrentContext();

            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(HTTP_ERROR_CODE_MAINTENANCE);
            ctx.setResponseBody(s.getMode().getMessage());
        }

        return null;
    }
}
