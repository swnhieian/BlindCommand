package blindcommand;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.apache.log4j.Level;
import java.io.File;
import java.util.Date;
import de.mindpipe.android.logging.log4j.LogConfigurator;

public class Log4jConfigure {
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 10;
    private static final String DEFAULT_LOG_DIR = "//BlindCommand//Log//";
    private static final String DEFAULT_LOG_FILE_NAME = "debug.log";
    private static final String TAG = "Log4jConfigure";
    // 对应AndroidManifest文件中的package
    private static final String PACKAGE_NAME = "com.shiweinan.BlindCommand";
    private static String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static boolean isConfigured = false;

    public static void configure(String fileName) {
        if(isConfigured)
            return;
        final LogConfigurator logConfigurator = new LogConfigurator();
        try {
            if (isSdcardMounted()) {
                logConfigurator.setFileName(Environment.getExternalStorageDirectory()
                        + DEFAULT_LOG_DIR + fileName);
            } else {
                logConfigurator.setFileName("//data//data//" + PACKAGE_NAME + "//files"
                        + File.separator + fileName);
            }
            logConfigurator.setMaxBackupSize(4);
            logConfigurator.setMaxFileSize(MAX_FILE_SIZE);

            //以下为通用配置
            logConfigurator.setImmediateFlush(true);
            logConfigurator.setRootLevel(Level.DEBUG);
            logConfigurator.setFilePattern("%d\t%p/%c:\t%m%n");
            logConfigurator.configure();
            android.util.Log.e(TAG, "Log4j config finish");
            System.out.println(222);
            isConfigured = true;
        } catch (Throwable throwable) {
            logConfigurator.setResetConfiguration(true);
            System.out.println(11111);
            throwable.printStackTrace();
            android.util.Log.e(TAG, "Log4j config error, use default config. Error:" + throwable);
            isConfigured = false;
        }
    }

    public static void configure() {
        configure(DEFAULT_LOG_FILE_NAME);
    }

    private static boolean isSdcardMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            int i = ContextCompat.checkSelfPermission(activity, permissions[0]);
//            if(i != PackageManager.PERMISSION_GRANTED){
//                ActivityCompat.requestPermissions(activity, permissions, 321);
//            }
//        }
//        int permission = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE);
//        }
//    }


}
