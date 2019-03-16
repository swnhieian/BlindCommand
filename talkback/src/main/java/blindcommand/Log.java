package blindcommand;

import org.apache.log4j.Logger;
import android.text.TextUtils;
public class Log {
    public static final boolean SWITCH_LOG = true;
    private static boolean isConfigured = false;

    public static void d(String tag, String message) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.debug(System.currentTimeMillis()+"\t" + message);
        }
    }

    public static void d(String tag, String message, Throwable exception) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.debug(System.currentTimeMillis()+"\t" + message, exception);}
    }

    public static void i(String tag, String message) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.info(System.currentTimeMillis()+"\t" + message);
        }
    }

    public static void i(String tag, String message, Throwable exception) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.info(System.currentTimeMillis()+"\t" + message, exception);
        }
    }

    public static void w(String tag, String message) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.warn(System.currentTimeMillis()+"\t" + message);
        }
    }

    public static void w(String tag, String message, Throwable exception) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.warn(System.currentTimeMillis()+"\t" + message, exception);
        }
    }

    public static void e(String tag, String message) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.error(System.currentTimeMillis()+"\t" + message);
        }
    }

    public static void e(String tag, String message, Throwable exception) {
        if (SWITCH_LOG) {
            Logger LOGGER = getLogger(tag);
            LOGGER.error(System.currentTimeMillis()+"\t" + message, exception);
        }
    }

    private static Logger getLogger(String tag) {
        if (!isConfigured) {
            Log4jConfigure.configure();
            isConfigured = true;
        }
        Logger logger;
        if (TextUtils.isEmpty(tag)) {
            logger = Logger.getRootLogger();
        } else {
            logger = Logger.getLogger(tag);
        }
        return logger;
    }

}
