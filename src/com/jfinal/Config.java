package com.jfinal;

import com.common.controllers.HomeCtrl;
import com.common.controllers.LoginCtrl;
import com.works.app.sorting.controllers.SortingOrderCtrl;
import com.works.common.controllers.CommonColorCtrl;
import com.works.common.controllers.DictionaryCtrl;
import com.works.common.controllers.OrderNumberCtrl;
import com.works.common.services.DictionaryService;
import com.jfinal.config.*;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.tx.TxByMethodRegex;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.works.pc.goods.controllers.GoodsCtrl;
import com.works.pc.goods.controllers.MaterialCtrl;
import com.works.pc.goods.controllers.ProductCtrl;
import com.works.pc.order.controllers.OrderCtrl;
import com.works.pc.order.controllers.OrderReturnCtrl;
import com.works.pc.order.controllers.OrderScrapCtrl;
import com.works.pc.purchase.controllers.PurchaseOrderCtrl;
import com.works.pc.purchase.controllers.PurchasePurchasereturnProcessCtrl;
import com.works.pc.purchase.controllers.PurchaseReturnCtrl;
import com.works.pc.store.controllers.StoreCountCtrl;
import com.works.pc.store.controllers.StoreCtrl;
import com.works.pc.store.controllers.StoreProductRelationCtrl;
import com.works.pc.store.controllers.StoreStockCtrl;
import com.works.pc.supplier.controllers.SupplierCtrl;
import com.works.pc.sys.controllers.*;
import com.works.pc.warehouse.controllers.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class Config extends JFinalConfig {

    public static boolean devMode = false;
    /**
     *
     */
    public static File web_inf_path = null;

    @Override
    public void configConstant(Constants constants) {
        String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath();
        web_inf_path = new File(path).getParentFile();

        loadPropertyFile("config.txt");
        devMode = getPropertyToBoolean("devMode", false);
        constants.setDevMode(devMode);
        constants.setEncoding("utf-8");
        constants.setViewType(ViewType.JSP);
//		arg0.setError404View("/white.jsp");
//		arg0.setError500View("/500.jsp");
    }

    @Override
    public void configRoute(Routes routes) {

        routes.add("/homeCtrl", HomeCtrl.class);
        routes.add("/loginCtrl", LoginCtrl.class);


        routes.add("/mgr/works/pc/addressCtrl", AddressCtrl.class);
        routes.add("/mgr/works/pc/catalogCtrl", CatalogCtrl.class);
        routes.add("/mgr/works/pc/commonColorCtrl", CommonColorCtrl.class);
        routes.add("/mgr/works/pc/dictionaryCtrl", DictionaryCtrl.class);
		routes.add("/mgr/works/pc/goodsCtrl", GoodsCtrl.class);
        routes.add("/mgr/works/pc/materialCtrl", MaterialCtrl.class);
        routes.add("/mgr/works/pc/orderCtrl", OrderCtrl.class);
        routes.add("/mgr/works/pc/orderNumberCtrl", OrderNumberCtrl.class);
        routes.add("/mgr/works/pc/orderReturnCtrl", OrderReturnCtrl.class);
        routes.add("/mgr/works/pc/orderScrapCtrl", OrderScrapCtrl.class);
        routes.add("/mgr/works/pc/productCtrl", ProductCtrl.class);
        routes.add("/mgr/works/pc/purchaseOrderCtrl", PurchaseOrderCtrl.class);
        routes.add("/mgr/works/pc/purchasePurchasereturnProcessCtrl", PurchasePurchasereturnProcessCtrl.class);
        routes.add("/mgr/works/pc/purchaseReturnCtrl", PurchaseReturnCtrl.class);
        routes.add("/mgr/works/pc/storeCtrl", StoreCtrl.class);
        routes.add("/mgr/works/pc/storeCountCtrl", StoreCountCtrl.class);
        routes.add("/mgr/works/pc/storeProductRelationCtrl", StoreProductRelationCtrl.class);
        routes.add("/mgr/works/pc/storeStockCtrl", StoreStockCtrl.class);
        routes.add("/mgr/works/pc/supplierCtrl", SupplierCtrl.class);
        routes.add("/mgr/works/pc/sysAuthCtrl", SysAuthCtrl.class);
        routes.add("/mgr/works/pc/sysMenuCtrl", SysMenuCtrl.class);
        routes.add("/mgr/works/pc/sysRolesCtrl", SysRolesCtrl.class);
        routes.add("/mgr/works/pc/sysUserCtrl", SysUserCtrl.class);
        routes.add("/mgr/works/pc/warehouseMovementOrderCtrl", WarehouseMovementOrderCtrl.class);
        routes.add("/mgr/works/pc/warehouseOutOrderCtrl", WarehouseOutOrderCtrl.class);
        routes.add("/mgr/works/pc/warehouseCtrl", WarehouseCtrl.class);
        routes.add("/mgr/works/pc/warehouseCountCtrl", WarehouseCountCtrl.class);
        routes.add("/mgr/works/pc/warehouseScrapCtrl", WarehouseScrapCtrl.class);
        routes.add("/mgr/works/pc/warehouseStockCtrl", WarehouseStockCtrl.class);


        routes.add("/wx/sorting/sortingOrderCtrl", SortingOrderCtrl.class);


    }

    @Override
    public void configEngine(Engine engine) {

    }

    @Override
    public void configPlugin(Plugins plugins) {
        String databaseURL = getProperty("jdbcUrl");
        String databaseUser = getProperty("username");
        String databasePassword = getProperty("password").trim();

        Integer initialPoolSize = getPropertyToInt("initialPoolSize");
        Integer minIdle = getPropertyToInt("minIdle");
        Integer maxActivee = getPropertyToInt("maxActivee");

        DruidPlugin druidPlugin = new DruidPlugin(databaseURL, databaseUser, databasePassword);
        druidPlugin.set(initialPoolSize, minIdle, maxActivee);
        druidPlugin.setFilters("stat,wall");
        plugins.add(druidPlugin);

        //实体映射
        ActiveRecordPlugin activeRecordPlugin = new ActiveRecordPlugin(druidPlugin);
        plugins.add(activeRecordPlugin);

        // ehcache缓存插件
        plugins.add(new EhCachePlugin());
    }

    @Override
    public void configInterceptor(Interceptors interceptors) {
        // 给service增加事务控制，过滤方法名为save*，update*，delete*
        interceptors.addGlobalServiceInterceptor(new TxByMethodRegex("(save.*|update.*|delete.*|add.*)"));
    }

    @Override
    public void configHandler(Handlers handlers) {

    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sDate = sdf.format(new Date());
        System.out.println("当前时间：" + sDate);
        DictionaryService.loadDictionary();
    }

}
