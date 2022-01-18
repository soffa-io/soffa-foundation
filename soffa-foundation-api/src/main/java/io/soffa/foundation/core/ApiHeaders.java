package io.soffa.foundation.core;

public interface ApiHeaders {

    String TENANT_ID = "X-TenantId";
    String APPLICATION = "X-Application";
    String TRACE_ID = "X-TraceId";
    String SPAN_ID = "X-SpanId";
    /**
     * @deprecated <p>
     * use {@link SecuritySchemes#BEARER_AUTH} instead.
     */
    @Deprecated
    String JWT = "BEARER_AUTH";
    /**
     * @deprecated
     * <p>use {@link SecuritySchemes#BASIC_AUTH} instead.
     */
    @Deprecated
    String BASIC_AUTH = "BASIC_AUTH";
    /**
     * @deprecated
     * <p>use {@link SecuritySchemes#OAUTH2} instead.
     */
    @Deprecated
    String OAUTH2 = "OAUTH2";
}
