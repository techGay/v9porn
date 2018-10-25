package com.u9porn.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ChangedPackages;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.SharedLibraryInfo;
import android.content.pm.VersionedPackage;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**来自 https://github.com/SplashCodes/JAViewer
 * @author megoc
 */
public class PackageManagerWrapper extends PackageManager {

    protected PackageManager manager;

    public PackageManagerWrapper(PackageManager manager) {
        this.manager = manager;
    }

    @Override
    public PackageInfo getPackageInfo(String s, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public PackageInfo getPackageInfo(VersionedPackage versionedPackage, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public String[] currentToCanonicalPackageNames(String[] strings) {
        return new String[0];
    }

    @Override
    public String[] canonicalToCurrentPackageNames(String[] strings) {
        return new String[0];
    }

    @Nullable
    @Override
    public Intent getLaunchIntentForPackage(@NonNull String s) {
        return null;
    }

    @Nullable
    @Override
    public Intent getLeanbackLaunchIntentForPackage(@NonNull String s) {
        return null;
    }

    @Override
    public int[] getPackageGids(@NonNull String s) throws NameNotFoundException {
        return new int[0];
    }

    @Override
    public int[] getPackageGids(String s, int i) throws NameNotFoundException {
        return new int[0];
    }

    @Override
    public int getPackageUid(String s, int i) throws NameNotFoundException {
        return 0;
    }

    @Override
    public PermissionInfo getPermissionInfo(String s, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public List<PermissionInfo> queryPermissionsByGroup(String s, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public PermissionGroupInfo getPermissionGroupInfo(String s, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public List<PermissionGroupInfo> getAllPermissionGroups(int i) {
        return null;
    }

    @Override
    public ApplicationInfo getApplicationInfo(String s, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName componentName, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName componentName, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName componentName, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName componentName, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public List<PackageInfo> getInstalledPackages(int i) {
        return null;
    }

    @Override
    public List<PackageInfo> getPackagesHoldingPermissions(String[] strings, int i) {
        return null;
    }

    @Override
    public int checkPermission(String s, String s1) {
        return PERMISSION_GRANTED;
    }

    @Override
    public boolean isPermissionRevokedByPolicy(@NonNull String s, @NonNull String s1) {
        return false;
    }

    @Override
    public boolean addPermission(PermissionInfo permissionInfo) {
        return false;
    }

    @Override
    public boolean addPermissionAsync(PermissionInfo permissionInfo) {
        return false;
    }

    @Override
    public void removePermission(String s) {

    }

    @Override
    public int checkSignatures(String s, String s1) {
        return SIGNATURE_MATCH;
    }

    @Override
    public int checkSignatures(int i, int i1) {
        return SIGNATURE_MATCH;
    }

    @Nullable
    @Override
    public String[] getPackagesForUid(int i) {
        return new String[0];
    }

    @Nullable
    @Override
    public String getNameForUid(int i) {
        return null;
    }

    @Override
    public List<ApplicationInfo> getInstalledApplications(int i) {
        return null;
    }

    @Override
    public boolean isInstantApp() {
        return false;
    }

    @Override
    public boolean isInstantApp(String s) {
        return false;
    }

    @Override
    public int getInstantAppCookieMaxBytes() {
        return 0;
    }

    @NonNull
    @Override
    public byte[] getInstantAppCookie() {
        return new byte[0];
    }

    @Override
    public void clearInstantAppCookie() {

    }

    @Override
    public void updateInstantAppCookie(@Nullable byte[] bytes) {

    }

    @Override
    public String[] getSystemSharedLibraryNames() {
        return new String[0];
    }

    @NonNull
    @Override
    public List<SharedLibraryInfo> getSharedLibraries(int i) {
        return null;
    }

    @Nullable
    @Override
    public ChangedPackages getChangedPackages(int i) {
        return null;
    }

    @Override
    public FeatureInfo[] getSystemAvailableFeatures() {
        return new FeatureInfo[0];
    }

    @Override
    public boolean hasSystemFeature(String s) {
        return false;
    }

    @Override
    public boolean hasSystemFeature(String s, int i) {
        return false;
    }

    @Override
    public ResolveInfo resolveActivity(Intent intent, int i) {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int i) {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentActivityOptions(@Nullable ComponentName componentName, @Nullable Intent[] intents, Intent intent, int i) {
        return null;
    }

    @Override
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int i) {
        return null;
    }

    @Override
    public ResolveInfo resolveService(Intent intent, int i) {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentServices(Intent intent, int i) {
        return null;
    }

    @Override
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int i) {
        return null;
    }

    @Override
    public ProviderInfo resolveContentProvider(String s, int i) {
        return null;
    }

    @Override
    public List<ProviderInfo> queryContentProviders(String s, int i, int i1) {
        return null;
    }

    @Override
    public InstrumentationInfo getInstrumentationInfo(ComponentName componentName, int i) throws NameNotFoundException {
        return null;
    }

    @Override
    public List<InstrumentationInfo> queryInstrumentation(String s, int i) {
        return null;
    }

    @Override
    public Drawable getDrawable(String s, int i, ApplicationInfo applicationInfo) {
        return null;
    }

    @Override
    public Drawable getActivityIcon(ComponentName componentName) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getActivityIcon(Intent intent) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getActivityBanner(ComponentName componentName) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getActivityBanner(Intent intent) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getDefaultActivityIcon() {
        return null;
    }

    @Override
    public Drawable getApplicationIcon(ApplicationInfo applicationInfo) {
        return null;
    }

    @Override
    public Drawable getApplicationIcon(String s) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getApplicationBanner(ApplicationInfo applicationInfo) {
        return null;
    }

    @Override
    public Drawable getApplicationBanner(String s) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getActivityLogo(ComponentName componentName) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getActivityLogo(Intent intent) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getApplicationLogo(ApplicationInfo applicationInfo) {
        return null;
    }

    @Override
    public Drawable getApplicationLogo(String s) throws NameNotFoundException {
        return null;
    }

    @Override
    public Drawable getUserBadgedIcon(Drawable drawable, UserHandle userHandle) {
        return null;
    }

    @Override
    public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle userHandle, Rect rect, int i) {
        return null;
    }

    @Override
    public CharSequence getUserBadgedLabel(CharSequence charSequence, UserHandle userHandle) {
        return null;
    }

    @Override
    public CharSequence getText(String s, int i, ApplicationInfo applicationInfo) {
        return null;
    }

    @Override
    public XmlResourceParser getXml(String s, int i, ApplicationInfo applicationInfo) {
        return null;
    }

    @Override
    public CharSequence getApplicationLabel(ApplicationInfo applicationInfo) {
        return null;
    }

    @Override
    public Resources getResourcesForActivity(ComponentName componentName) throws NameNotFoundException {
        return null;
    }

    @Override
    public Resources getResourcesForApplication(ApplicationInfo applicationInfo) throws NameNotFoundException {
        return null;
    }

    @Override
    public Resources getResourcesForApplication(String s) throws NameNotFoundException {
        return null;
    }

    @Override
    public void verifyPendingInstall(int i, int i1) {

    }

    @Override
    public void extendVerificationTimeout(int i, int i1, long l) {

    }

    @Override
    public void setInstallerPackageName(String s, String s1) {

    }

    @Override
    public String getInstallerPackageName(String s) {
        return null;
    }

    @Override
    public void addPackageToPreferred(String s) {

    }

    @Override
    public void removePackageFromPreferred(String s) {

    }

    @Override
    public List<PackageInfo> getPreferredPackages(int i) {
        return null;
    }

    @Override
    public void addPreferredActivity(IntentFilter intentFilter, int i, ComponentName[] componentNames, ComponentName componentName) {

    }

    @Override
    public void clearPackagePreferredActivities(String s) {

    }

    @Override
    public int getPreferredActivities(@NonNull List<IntentFilter> list, @NonNull List<ComponentName> list1, String s) {
        return 0;
    }

    @Override
    public void setComponentEnabledSetting(ComponentName componentName, int i, int i1) {

    }

    @Override
    public int getComponentEnabledSetting(ComponentName componentName) {
        return COMPONENT_ENABLED_STATE_DEFAULT;
    }

    @Override
    public void setApplicationEnabledSetting(String s, int i, int i1) {

    }

    @Override
    public int getApplicationEnabledSetting(String s) {
        return COMPONENT_ENABLED_STATE_DEFAULT;
    }

    @Override
    public boolean isSafeMode() {
        return false;
    }

    @Override
    public void setApplicationCategoryHint(@NonNull String s, int i) {

    }

    @NonNull
    @Override
    public PackageInstaller getPackageInstaller() {
        return null;
    }

    @Override
    public boolean canRequestPackageInstalls() {
        return false;
    }
}
