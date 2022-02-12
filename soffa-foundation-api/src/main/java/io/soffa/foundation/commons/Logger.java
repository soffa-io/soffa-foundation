package io.soffa.foundation.commons;

import com.mgnt.utils.TextUtils;
import io.soffa.foundation.context.RequestContext;
import io.soffa.foundation.model.TenantId;
import org.slf4j.LoggerFactory;

public class Logger {

    static {
        Logger.setRelevantPackage("io.soffa");
    }

    private final org.slf4j.Logger log;
    private String tag;

    public Logger(org.slf4j.Logger logger) {
        this.log = logger;
    }
    
    public Logger(org.slf4j.Logger logger, String tag) {
        this(logger);
        this.tag = tag;
    }

    public static void setContext(RequestContext context) {
        if (context == null) {
            org.slf4j.MDC.clear();
        } else {
            org.slf4j.MDC.setContextMap(context.getContextMap());
        }
    }

    public static void setTenantId(TenantId tenantId) {
        if (tenantId != null) {
            org.slf4j.MDC.put("tenant", tenantId.getValue());
        }else {
            org.slf4j.MDC.remove("tenant");
        }
    }

    public static void setRelevantPackage(String pkg) {
        if ("*".equals(pkg)) {
            TextUtils.setRelevantPackage(null);
        } else {
            TextUtils.setRelevantPackage(pkg);
        }
    }

    public static Logger get(Class<?> type) {
        return new Logger(LoggerFactory.getLogger(type));
    }

    public static Logger get(String name) {
        return new Logger(LoggerFactory.getLogger(name));
    }

    public static Logger get(String name, String tag) {
        return new Logger(LoggerFactory.getLogger(name), tag);
    }

    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    public void debug(String message, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(formatMessage(message, args));
        }
    }

    public void trace(String message, Object... args) {
        if (log.isDebugEnabled()) {
            log.trace(formatMessage(message, args));
        }
    }

    public void info(String message, Object... args) {
        log.info(formatMessage(message, args));
    }
    
    private String formatMessage(String message, Object... args) {
        if (TextUtil.isEmpty(tag)) {
            return TextUtil.format(message, args);
        }
        return "["+tag+"] " + TextUtil.format(message, args);
    }

    public void warn(String message, Object... args) {
        log.warn(formatMessage(message, args));
    }

    public void error(Throwable e) {
        error(ErrorUtil.loookupOriginalMessage(e), e);
    }

    public void error(Throwable error, String message, Object... args) {
        error(formatMessage(message, args), error);
    }

    public void error(String message, Throwable e) {
        log.error(message);
        log.error(ErrorUtil.getStacktrace(e));
    }


    public void error(String message, Object... args) {
        log.error(formatMessage(message, args));
    }

}
