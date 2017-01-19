package cstv.plugin;

import android.content.res.AssetManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

/**
 * 插件类，包括插件的安装、卸载、清除
 * Created by huangjian on 2016/6/21.
 */
public class CstvPlugin {
    private String mPluginId;            //使用插件的安装目录作为插件id
    private String mInstalledPathInfo = "";            //安装插件的随机路径信息

    private boolean isInstalling = false;
    private boolean isAssetInstalling = false;

    private String insideLibPath = "";

    protected CstvPlugin(String pluginId) {
        mPluginId = pluginId;
    }

    public synchronized boolean install() {
        isInstalling = true;
        //创建插件安装目录
        CstvPluginUtil.createDir(CstvPluginUtil.getPlugDir(mPluginId));

        //将当前时间记录为插件的随机数，等效于android系统后面~1、~2等
        mInstalledPathInfo = String.valueOf(System.currentTimeMillis());

        //获取插件apk文件
        String path = CstvPluginUtil.getZipPath(mPluginId);

        //插件文件不存在，则安装asset中的默认插件。
        if (!CstvPluginUtil.exists(path)) {
            if (CstvPluginUtil.iszeusPlugin(mPluginId)) {
                isInstalling = false;
                mInstalledPathInfo = getInstalledPathInfoNoCache();
                return false;
            }
            return installAssetPlugin();
        }
        //把下载路径下的插件文件，直接重命名到安装目录，不需要耗时的拷贝过程。
        boolean ret = CstvPluginUtil.rename(CstvPluginUtil.getZipPath(mPluginId), getAPKPath(mPluginId));
        if (!ret) {
            isInstalling = false;
            mInstalledPathInfo = getInstalledPathInfoNoCache();
            return false;
        }
        //拷贝so文件，一些插件是没有so文件，而这个方法耗时还稍微高点，所以对于没有so的插件和补丁是不会拷贝的。
        if (!CstvPluginUtil.isHotfixWithoutSoFile(mPluginId) &&
                !CstvPluginUtil.isPluginWithoutSoFile(mPluginId) &&
                !copySoFile(mInstalledPathInfo, CstvPluginUtil.getCpuArchitecture())) {
            isInstalling = false;
            mInstalledPathInfo = getInstalledPathInfoNoCache();
            return false;
        }

        //校验是否下载的是正确文件，如果插件下载错误则获取这个配置文件就会失败。
        CstvPluginManifest meta = getPluginMeta();
        if (meta == null) {
            CstvPluginUtil.deleteFile(new File(getAPKPath(mPluginId)));
            mInstalledPathInfo = getInstalledPathInfoNoCache();
            isInstalling = false;
            return false;
        }

        if (!CstvPluginUtil.writePathInfo(mPluginId, mInstalledPathInfo)) {
            isInstalling = false;
            mInstalledPathInfo = getInstalledPathInfoNoCache();
            return false;
        }
        CstvPluginManager.addInstalledPlugin(mPluginId, Integer.valueOf(meta.version));
        isInstalling = false;
        return true;
    }

    public boolean installPlugin() {
        CstvPluginManifest meta;
        synchronized (this) {

            mInstalledPathInfo = getInstalledPathInfoNoCache();;

            //拷贝so文件
            if (!copySoFile(mInstalledPathInfo, CstvPluginUtil.getCpuArchitecture())) {
                isInstalling = false;
                return false;
            }

            meta = getPluginMeta();
            if (meta == null) {
                CstvPluginUtil.deleteFile(new File(getAPKPath(mPluginId)));
                isAssetInstalling = false;
                return false;
            }
            if (!CstvPluginUtil.writePathInfo(mPluginId, mInstalledPathInfo)) {
                isAssetInstalling = false;
                return false;
            }
            isAssetInstalling = false;
        }
        CstvPluginManager.addInstalledPlugin(mPluginId, Integer.valueOf(meta.version));
        return true;
    }

    /**
     * 安装assets中的插件，assets中插件的文件名要为 mPluginId +".apk"
     *
     * @return 是否成功
     */
    public boolean installAssetPlugin() {
        CstvPluginManifest meta;
        synchronized (this) {
            isAssetInstalling = true;
            if (CstvPluginManager.isInstall(mPluginId)) {
                isInstalling = false;
                return true;
            }
            CstvPluginUtil.createDir(CstvPluginUtil.getPlugDir(mPluginId));
            mInstalledPathInfo = String.valueOf(System.currentTimeMillis());

            FileOutputStream out = null;
            InputStream in = null;
            try {
                AssetManager am = CstvPluginManager.mBaseResources.getAssets();
                in = am.open(mPluginId + CstvPluginConfig.PLUGIN_SUFF);
                CstvPluginUtil.createDirWithFile(getAPKPath(mPluginId));
                String ppp = getAPKPath(mPluginId);
                out = new FileOutputStream(getAPKPath(mPluginId), false);
                byte[] temp = new byte[2048];
                int len;
                while ((len = in.read(temp)) > 0) {
                    out.write(temp, 0, len);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mInstalledPathInfo = getInstalledPathInfoNoCache();
                isInstalling = false;
                return false;
            } finally {
                CstvPluginUtil.close(in);
                CstvPluginUtil.close(out);
            }
            //拷贝so文件
            if (!copySoFile(mInstalledPathInfo, CstvPluginUtil.getCpuArchitecture())) {
                isInstalling = false;
                mInstalledPathInfo = getInstalledPathInfoNoCache();
                return false;
            }

            meta = getPluginMeta();
            if (meta == null) {
                CstvPluginUtil.deleteFile(new File(getAPKPath(mPluginId)));
                isAssetInstalling = false;
                mInstalledPathInfo = getInstalledPathInfoNoCache();
                return false;
            }
            if (!CstvPluginUtil.writePathInfo(mPluginId, mInstalledPathInfo)) {
                isAssetInstalling = false;
                mInstalledPathInfo = getInstalledPathInfoNoCache();
                return false;
            }
            isAssetInstalling = false;
        }
        CstvPluginManager.addInstalledPlugin(mPluginId, Integer.valueOf(meta.version));
        return true;
    }

    /**
     * 清除之前版本的旧数据
     */
    public synchronized void clearOldPlugin() {
        if (getInstalledPathInfo() == null || isAssetInstalling || isInstalling) return;
        File pluginDir = new File(CstvPluginUtil.getPlugDir(mPluginId));
        String installedPathInfo = getInstalledPathInfoNoCache();
        if (TextUtils.isEmpty(installedPathInfo)) return;
        if (pluginDir.exists() && pluginDir.isDirectory()) {
            File[] list = pluginDir.listFiles();
            if (list == null) return;
            for (File f : list) {
                String fileFullName = f.getName();
                if (fileFullName.endsWith(CstvPluginConfig.PLUGIN_JAR_SUFF) || fileFullName.endsWith(CstvPluginConfig.PLUGIN_SUFF)) {
                    String fileName = fileFullName.substring(0, fileFullName.lastIndexOf("."));
                    if (!fileName.equalsIgnoreCase(installedPathInfo)) {
                        f.delete();
                        File dir = new File(f.getParent() + "/" + fileName);
                        CstvPluginUtil.deleteDirectory(dir);

                        File cacheFile = new File(CstvPluginUtil.getDexCacheFilePath(fileName));
                        if (cacheFile.exists()) {
                            cacheFile.delete();
                        }
                    }
                }
            }
        }
    }

    /**
     * 将插件中的lib库拷贝入手机内存中。
     * 根据当前手机cpu的类型拷贝合适的lib库入手机内存中
     *
     * @param installedPathInfo 安装的随机路径信息
     * @param cpuType           CPU_AMR:1 CPU_X86:2 CPU_MIPS:3 具体见{@link CstvPluginUtil}中的getCpuArchitecture()和getLibFile()方法
     * @return 是否成功
     */
    protected boolean copySoFile(String installedPathInfo, int cpuType) {
        insideLibPath = CstvPluginUtil.getInsidePluginPath() + mPluginId + "/" + installedPathInfo + "/";
        CstvPluginUtil.createDir(insideLibPath);
        String apkLibPath = CstvPluginUtil.getLibFile(cpuType);
        //首先将apk中libs文件夹下的一级so文件拷贝
        return CstvPluginUtil.unzipFile(getAPKPath(mPluginId), insideLibPath, apkLibPath);
    }

    public String getInsideLibPath(){
        return insideLibPath;
    }

    /**
     * 获取插件已经安装的apk路径
     *
     * @param pluginName 插件id
     * @return 插件已经安装的apk路径
     */
    public String getAPKPath(String pluginName) {
        int updatedVersion = getUpdatedVersion(pluginName);
        if(updatedVersion > 0){
            return CstvPluginUtil.getPlugDir(pluginName) + getInstalledPathInfo() + "_" + updatedVersion + CstvPluginConfig.PLUGIN_SUFF;
        }
        return CstvPluginUtil.getPlugDir(pluginName) + getInstalledPathInfo() + CstvPluginConfig.PLUGIN_SUFF;
    }

    private int getUpdatedVersion(String pluginName) {
        HashMap<String, Integer> updatedList = CstvPluginManager.getUpdatedPlugin();
        if(updatedList.containsKey(pluginName))
            return updatedList.get(pluginName);
        return -1;
    }
    /**
     * 获取当前安装的随机路径信息，不使用缓存，直接读取文件
     *
     * @return 当前安装的随机路径信息
     */
    public String getInstalledPathInfoNoCache() {
        return CstvPluginUtil.getInstalledPathInfo(mPluginId);
    }

    /**
     * 获取插件清单文件信息，不使用缓存，读取速度很快
     *
     * @return 插件清单文件信息
     */
    public CstvPluginManifest getPluginMeta() {
        CstvPluginManifest meta = null;
        String result = readMeta();
        if (!TextUtils.isEmpty(result)) {
            meta = parserMeta(result);
        }
        return meta;
    }

    /**
     * 解析清单文件
     *
     * @param metaString meta字符串
     * @return PluginManifest对象
     */
    private CstvPluginManifest parserMeta(String metaString) {
        CstvPluginManifest meta = new CstvPluginManifest();
        try {
            JSONObject jObject = new JSONObject(metaString.replaceAll("\r|\n", ""));
            meta.name = jObject.optString(CstvPluginManifest.PLUG_NAME);
            meta.minVersion = jObject.optString(CstvPluginManifest.PLUG_MIN_VERSION);
            meta.maxVersion = jObject.optString(CstvPluginManifest.PLUG_MAX_VERSION);
            meta.version = jObject.optString(CstvPluginManifest.PLUG_VERSION);
            meta.mainClass = jObject.optString(CstvPluginManifest.PLUG_MAINCLASS);
            meta.otherInfo = jObject.optString(CstvPluginManifest.PLUG_OTHER_INFO);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return meta;
    }

    /**
     * 卸载某个插件，通常情况下不需要卸载，除非需要显示调用
     *
     * @return 是否成功
     */
    public boolean uninstall() {
        try {
            CstvPluginManager.unInstalledPlugin(mPluginId);
            //删除手机内存中/data/data/packageName/plugins/mPluginName下的文件
            File baseModulePathF = new File(CstvPluginUtil.getPlugDir(mPluginId));
            CstvPluginUtil.deleteDirectory(baseModulePathF);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 读取meta文件
     *
     * @return meta字符串
     */
    private String readMeta() {
        return CstvPluginUtil.readZipFileString(getAPKPath(mPluginId), CstvPluginConfig.PLUGINWEB_MAINIFEST_FILE);
    }

    /**
     * 获取当前安装的随机路径信息，有缓存则使用缓存
     *
     * @return 当前安装的随机路径信息
     */
    private String getInstalledPathInfo() {
        if (!TextUtils.isEmpty(mInstalledPathInfo)) {
            return mInstalledPathInfo;
        }
        mInstalledPathInfo = CstvPluginUtil.getInstalledPathInfo(mPluginId);
        return mInstalledPathInfo;
    }
}
