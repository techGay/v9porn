package com.awesapp.isafe.svs.parsers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.u9porn.utils.PackageManagerWrapper;

/**
 * 来自 https://github.com/SplashCodes/JAViewer
 *
 * @author megoc
 */
@SuppressWarnings("JniMissingFunction")
public class PSVS21 {

    private final static String PACKAGE_NAME = "stub.isafe";

    public static native String computeHash(Context context, String str, String str2);

    private static final Signature SIGNATURE = new Signature(
            new byte[]{
                    (byte) 48, (byte) 130, (byte) 2, (byte) 135, (byte) 48, (byte) 130, (byte) 1, (byte) 240, (byte) 160, (byte) 3, (byte) 2, (byte) 1, (byte) 2, (byte) 2, (byte) 4, (byte) 84, (byte) 221, (byte) 151, (byte) 111, (byte) 48, (byte) 13, (byte) 6, (byte) 9, (byte) 42, (byte) 134, (byte) 72, (byte) 134, (byte) 247, (byte) 13, (byte) 1, (byte) 1, (byte) 5, (byte) 5, (byte) 0, (byte) 48, (byte) 129, (byte) 134, (byte) 49, (byte) 11, (byte) 48, (byte) 9, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 6, (byte) 19, (byte) 2, (byte) 72, (byte) 75, (byte) 49, (byte) 18, (byte) 48, (byte) 16, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 8, (byte) 19, (byte) 9, (byte) 72, (byte) 111, (byte) 110, (byte) 103, (byte) 32, (byte) 75, (byte) 111, (byte) 110, (byte) 103, (byte) 49, (byte) 18, (byte) 48, (byte) 16, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 7, (byte) 19, (byte) 9, (byte) 72, (byte) 111, (byte) 110, (byte) 103, (byte) 32, (byte) 75, (byte) 111, (byte) 110, (byte) 103, (byte) 49, (byte) 24, (byte) 48, (byte) 22, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 10, (byte) 19, (byte) 15, (byte) 65, (byte) 119, (byte) 101, (byte) 115, (byte) 97, (byte) 112, (byte) 112, (byte) 32, (byte) 76, (byte) 105, (byte) 109, (byte) 105, (byte) 116, (byte) 101, (byte) 100, (byte) 49, (byte) 27, (byte) 48, (byte) 25, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 11, (byte) 19, (byte) 18, (byte) 77, (byte) 111, (byte) 98, (byte) 105, (byte) 108, (byte) 101, (byte) 32, (byte) 68, (byte) 101, (byte) 118, (byte) 101, (byte) 108, (byte) 111, (byte) 112, (byte) 109, (byte) 101, (byte) 110, (byte) 116, (byte) 49, (byte) 24, (byte) 48, (byte) 22, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 3, (byte) 19, (byte) 15, (byte) 65, (byte) 119, (byte) 101, (byte) 115, (byte) 97, (byte) 112, (byte) 112, (byte) 32, (byte) 76, (byte) 105, (byte) 109, (byte) 105, (byte) 116, (byte) 101, (byte) 100, (byte) 48, (byte) 32, (byte) 23, (byte) 13, (byte) 49, (byte) 53, (byte) 48, (byte) 50, (byte) 49, (byte) 51, (byte) 48, (byte) 54, (byte) 49, (byte) 57, (byte) 50, (byte) 55, (byte) 90, (byte) 24, (byte) 15, (byte) 50, (byte) 48, (byte) 54, (byte) 53, (byte) 48, (byte) 49, (byte) 51, (byte) 49, (byte) 48, (byte) 54, (byte) 49, (byte) 57, (byte) 50, (byte) 55, (byte) 90, (byte) 48, (byte) 129, (byte) 134, (byte) 49, (byte) 11, (byte) 48, (byte) 9, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 6, (byte) 19, (byte) 2, (byte) 72, (byte) 75, (byte) 49, (byte) 18, (byte) 48, (byte) 16, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 8, (byte) 19, (byte) 9, (byte) 72, (byte) 111, (byte) 110, (byte) 103, (byte) 32, (byte) 75, (byte) 111, (byte) 110, (byte) 103, (byte) 49, (byte) 18, (byte) 48, (byte) 16, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 7, (byte) 19, (byte) 9, (byte) 72, (byte) 111, (byte) 110, (byte) 103, (byte) 32, (byte) 75, (byte) 111, (byte) 110, (byte) 103, (byte) 49, (byte) 24, (byte) 48, (byte) 22, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 10, (byte) 19, (byte) 15, (byte) 65, (byte) 119, (byte) 101, (byte) 115, (byte) 97, (byte) 112, (byte) 112, (byte) 32, (byte) 76, (byte) 105, (byte) 109, (byte) 105, (byte) 116, (byte) 101, (byte) 100, (byte) 49, (byte) 27, (byte) 48, (byte) 25, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 11, (byte) 19, (byte) 18, (byte) 77, (byte) 111, (byte) 98, (byte) 105, (byte) 108, (byte) 101, (byte) 32, (byte) 68, (byte) 101, (byte) 118, (byte) 101, (byte) 108, (byte) 111, (byte) 112, (byte) 109, (byte) 101, (byte) 110, (byte) 116, (byte) 49, (byte) 24, (byte) 48, (byte) 22, (byte) 6, (byte) 3, (byte) 85, (byte) 4, (byte) 3, (byte) 19, (byte) 15, (byte) 65, (byte) 119, (byte) 101, (byte) 115, (byte) 97, (byte) 112, (byte) 112, (byte) 32, (byte) 76, (byte) 105, (byte) 109, (byte) 105, (byte) 116, (byte) 101, (byte) 100, (byte) 48, (byte) 129, (byte) 159, (byte) 48, (byte) 13, (byte) 6, (byte) 9, (byte) 42, (byte) 134, (byte) 72, (byte) 134, (byte) 247, (byte) 13, (byte) 1, (byte) 1, (byte) 1, (byte) 5, (byte) 0, (byte) 3, (byte) 129, (byte) 141, (byte) 0, (byte) 48, (byte) 129, (byte) 137, (byte) 2, (byte) 129, (byte) 129, (byte) 0, (byte) 178, (byte) 185, (byte) 41, (byte) 229, (byte) 6, (byte) 175, (byte) 174, (byte) 154, (byte) 215, (byte) 185, (byte) 50, (byte) 200, (byte) 206, (byte) 11, (byte) 43, (byte) 210, (byte) 25, (byte) 68, (byte) 23, (byte) 185, (byte) 145, (byte) 159, (byte) 154, (byte) 105, (byte) 87, (byte) 177, (byte) 35, (byte) 104, (byte) 67, (byte) 88, (byte) 80, (byte) 103, (byte) 6, (byte) 155, (byte) 156, (byte) 68, (byte) 55, (byte) 182, (byte) 207, (byte) 94, (byte) 111, (byte) 73, (byte) 136, (byte) 135, (byte) 157, (byte) 210, (byte) 159, (byte) 149, (byte) 219, (byte) 7, (byte) 100, (byte) 53, (byte) 146, (byte) 111, (byte) 50, (byte) 26, (byte) 104, (byte) 115, (byte) 59, (byte) 231, (byte) 156, (byte) 165, (byte) 111, (byte) 82, (byte) 3, (byte) 214, (byte) 192, (byte) 67, (byte) 129, (byte) 149, (byte) 160, (byte) 169, (byte) 175, (byte) 229, (byte) 13, (byte) 65, (byte) 136, (byte) 160, (byte) 210, (byte) 61, (byte) 63, (byte) 90, (byte) 3, (byte) 138, (byte) 161, (byte) 61, (byte) 98, (byte) 0, (byte) 69, (byte) 253, (byte) 48, (byte) 234, (byte) 36, (byte) 17, (byte) 185, (byte) 88, (byte) 54, (byte) 62, (byte) 132, (byte) 77, (byte) 158, (byte) 4, (byte) 130, (byte) 77, (byte) 233, (byte) 191, (byte) 40, (byte) 191, (byte) 194, (byte) 255, (byte) 138, (byte) 138, (byte) 34, (byte) 188, (byte) 151, (byte) 242, (byte) 184, (byte) 198, (byte) 70, (byte) 138, (byte) 146, (byte) 64, (byte) 187, (byte) 227, (byte) 242, (byte) 51, (byte) 166, (byte) 35, (byte) 2, (byte) 3, (byte) 1, (byte) 0, (byte) 1, (byte) 48, (byte) 13, (byte) 6, (byte) 9, (byte) 42, (byte) 134, (byte) 72, (byte) 134, (byte) 247, (byte) 13, (byte) 1, (byte) 1, (byte) 5, (byte) 5, (byte) 0, (byte) 3, (byte) 129, (byte) 129, (byte) 0, (byte) 26, (byte) 113, (byte) 61, (byte) 179, (byte) 21, (byte) 78, (byte) 14, (byte) 144, (byte) 92, (byte) 71, (byte) 27, (byte) 215, (byte) 216, (byte) 204, (byte) 86, (byte) 66, (byte) 106, (byte) 108, (byte) 219, (byte) 136, (byte) 91, (byte) 74, (byte) 132, (byte) 158, (byte) 186, (byte) 141, (byte) 72, (byte) 229, (byte) 70, (byte) 150, (byte) 161, (byte) 180, (byte) 240, (byte) 170, (byte) 192, (byte) 198, (byte) 54, (byte) 221, (byte) 117, (byte) 13, (byte) 64, (byte) 182, (byte) 165, (byte) 250, (byte) 74, (byte) 247, (byte) 203, (byte) 151, (byte) 235, (byte) 255, (byte) 8, (byte) 77, (byte) 231, (byte) 140, (byte) 166, (byte) 172, (byte) 58, (byte) 235, (byte) 128, (byte) 152, (byte) 71, (byte) 39, (byte) 90, (byte) 29, (byte) 80, (byte) 158, (byte) 2, (byte) 241, (byte) 131, (byte) 189, (byte) 62, (byte) 14, (byte) 24, (byte) 250, (byte) 203, (byte) 179, (byte) 83, (byte) 130, (byte) 207, (byte) 101, (byte) 83, (byte) 224, (byte) 88, (byte) 62, (byte) 17, (byte) 60, (byte) 40, (byte) 186, (byte) 225, (byte) 15, (byte) 78, (byte) 139, (byte) 103, (byte) 121, (byte) 201, (byte) 227, (byte) 243, (byte) 81, (byte) 74, (byte) 155, (byte) 110, (byte) 208, (byte) 62, (byte) 152, (byte) 204, (byte) 138, (byte) 208, (byte) 190, (byte) 69, (byte) 55, (byte) 255, (byte) 157, (byte) 159, (byte) 97, (byte) 27, (byte) 86, (byte) 148, (byte) 182, (byte) 179, (byte) 122, (byte) 189, (byte) 81, (byte) 239, (byte) 15, (byte) 127, (byte) 78, (byte) 29, (byte) 105
            }
    );

    static {
        System.loadLibrary("isafe");
    }

    public static class StubContext extends ContextWrapper {

        private PackageManager manager;

        public StubContext(Context base) {
            super(base);
            this.manager = base.getPackageManager();
        }

        @Override
        public String getPackageName() {
            return PACKAGE_NAME;
        }

        @Override
        public PackageManager getPackageManager() {
            return new StubPackageManager(manager);
        }
    }

    static class StubPackageManager extends PackageManagerWrapper {

        StubPackageManager(PackageManager manager) {
            super(manager);
        }

        @Override
        public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
            if (PACKAGE_NAME.equals(packageName)) {
                return new PackageInfo() {
                    {
                        this.signatures = new Signature[]{
                                SIGNATURE
                        };
                    }
                };
            }
            return manager.getPackageInfo(packageName, flags);
        }
    }

}
