package com.zhixin.SmartBar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import com.zhixin.SmartBar.BgColor.SingleColorMode;
import com.zhixin.SmartBar.BgColor.SmartFactory;
import com.zhixin.SmartBar.BgColor.SmartOptions;
import com.zhixin.SmartBar.adapters.AppItemAdapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends ListActivity implements PopupMenu.OnMenuItemClickListener
        , PopupMenu.OnDismissListener {
    private AppItemAdapter mAdapter;
    private PopupMenu mPopupMenu;
    private ActionMode mActionMode;
    private AppItemAdapter.AppItem mModifyingItem;
    private ConfigManager configManager;
    private boolean isDeleteSystemApp = true;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView list = this.getListView();
        this.getListView().setBackgroundColor(Color.GRAY);
        configManager = ConfigManager.Instance();
        configManager.load();
        isDeleteSystemApp = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("isDeleteSystemApp", true);
        loadData(isDeleteSystemApp);
        String pageName = this.getPackageName();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(view, position, id);
            }
        });
    }
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        /*Activity thisActivity=this;
        ConfigManager configManager=ConfigManager.Instance();
        configManager.load();
        String mode= configManager.getProp().getProperty(thisActivity.getPackageName());
        if(mode!=null){
            SmartBarUtils.changeSmartBarColor(thisActivity, SmartFactory.getModeByValue( Integer.parseInt(mode)));
        }*/
        //menu.add(1,1,1,"设置");
        //SmartBarUtils.changeSmartBarColor(this,0xFFFFFFFF);
        menu.add(1, 1, 1, R.string.delete);
        menu.add(1, 2, 2, isDeleteSystemApp ? R.string.showSystemApp : R.string.noShowSystemApp);
        menu.add(1, 3, 3,configManager.getInt(ConfigManager.DEF_MODE_NAME,0)==0? R.string.allCopy:R.string.allNoChange);
        return super.onCreatePanelMenu(featureId, menu);
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case 1: {
                configManager.clear();
                configManager.save();
            }
            break;
            case 2: {
                isDeleteSystemApp = !isDeleteSystemApp;
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                editor.putBoolean("isDeleteSystemApp", isDeleteSystemApp);
                editor.commit();
                item.setTitle(isDeleteSystemApp ? R.string.showSystemApp : R.string.noShowSystemApp);
            }break;
            case 3:{
                if(configManager.getInt(ConfigManager.DEF_MODE_NAME,0)==0){
                    configManager.put(ConfigManager.DEF_MODE_NAME,1);
                    item.setTitle(R.string.allNoChange);
                }
                else {
                    configManager.put(ConfigManager.DEF_MODE_NAME,0);
                    item.setTitle(R.string.allCopy);
                }
                configManager.save();
            }
            break;
        }
        loadData(isDeleteSystemApp);
        return super.onMenuItemSelected(featureId, item);
    }

    public void onListItemClick(View view, int position, long id) {
        mModifyingItem = mAdapter.getAppItem(position);
        mAdapter.setChecked(view, true);
        mPopupMenu = new PopupMenu(this, view.findViewById(R.id.app_op));
        mPopupMenu.inflate(R.menu.options);
        mPopupMenu.setOnMenuItemClickListener(this);
        mPopupMenu.setOnDismissListener(this);
        mPopupMenu.show();
    }

    public boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private void loadData(boolean removeSystemApp) {
        final PackageManager packageManager = this.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        mAdapter = new AppItemAdapter(this);
        int defMode=configManager.getInt(ConfigManager.DEF_MODE_NAME,0);
        if (pinfo != null) {
            Iterator<PackageInfo> iter = pinfo.iterator();
            while (iter.hasNext()) {
                PackageInfo info = iter.next();
                if (!isSystemApp(info) || !removeSystemApp) {
                    AppItemAdapter.AppItem item = new AppItemAdapter.AppItem(
                            info.packageName
                            , info.applicationInfo.loadLabel(packageManager).toString()
                            , info.applicationInfo.loadIcon(packageManager)
                    );
                    int mode = configManager.getInt(info.packageName,defMode);
                    item.setOption(SmartFactory.getModeByValue(mode));
                    mAdapter.addItem(item);
                }
            }
        }
        setListAdapter(mAdapter);
    }
    @Override
    public void onDismiss(PopupMenu menu) {

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SmartOptions op = null;
        switch (item.getItemId()) {
            case R.id.action_noChange:
                op = SmartOptions.NOChange;
                break;
            case R.id.action_copy:
                op = SmartOptions.CopyActionBar;
                break;
            case R.id.action_white:
                op = SmartOptions.White;
                break;
            case R.id.action_gray:
                op = SmartOptions.Gray;
                break;
            default:
                break;
        }
        if (op != null && mModifyingItem != null) {
            mModifyingItem.setDisplay(op);
            configManager.put(mModifyingItem.getPackgeName(), SmartFactory.getValueByMode(op));
            configManager.save();
            return true;
        }
        return false;
    }
}
