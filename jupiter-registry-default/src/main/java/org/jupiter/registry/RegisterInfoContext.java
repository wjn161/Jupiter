package org.jupiter.registry;

import org.jupiter.common.concurrent.ConcurrentSet;
import org.jupiter.common.util.Lists;
import org.jupiter.common.util.Maps;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import static org.jupiter.registry.RegisterMeta.Address;
import static org.jupiter.registry.RegisterMeta.ServiceMeta;

/**
 * 注册服务的全局信息, 同时也供监控程序使用
 *
 * jupiter
 * org.jupiter.registry
 *
 * @author jiachun.fjc
 */
public class RegisterInfoContext {

    // 指定服务都有哪些节点注册
    private final ConcurrentMap<ServiceMeta, ConfigWithVersion<ConcurrentMap<Address, RegisterMeta>>>
            globalRegisterInfoMap = Maps.newConcurrentHashMap();
    // 指定节点都注册了哪些服务
    private final ConcurrentMap<Address, ConcurrentSet<ServiceMeta>> globalServiceMetaMap = Maps.newConcurrentHashMap();

    public ConfigWithVersion<ConcurrentMap<Address, RegisterMeta>> getRegisterMeta(ServiceMeta serviceMeta) {
        ConfigWithVersion<ConcurrentMap<Address, RegisterMeta>> config = globalRegisterInfoMap.get(serviceMeta);
        if (config == null) {
            ConfigWithVersion<ConcurrentMap<Address, RegisterMeta>> newConfig = ConfigWithVersion.newInstance();
            newConfig.setConfig(Maps.<Address, RegisterMeta>newConcurrentHashMap());
            config = globalRegisterInfoMap.putIfAbsent(serviceMeta, newConfig);
            if (config == null) {
                config = newConfig;
            }
        }
        return config;
    }

    public ConcurrentSet<ServiceMeta> getServiceMeta(Address address) {
        ConcurrentSet<ServiceMeta> serviceMetaSet = globalServiceMetaMap.get(address);
        if (serviceMetaSet == null) {
            ConcurrentSet<ServiceMeta> newServiceMetaSet = new ConcurrentSet<>();
            serviceMetaSet = globalServiceMetaMap.putIfAbsent(address, newServiceMetaSet);
            if (serviceMetaSet == null) {
                serviceMetaSet = newServiceMetaSet;
            }
        }
        return serviceMetaSet;
    }

    // - Monitor -------------------------------------------------------------------------------------------------------

    /**
     * 获取所有已注册过服务的Provider的地址
     */
    public List<Address> listProvidersHost() {
        return Lists.newArrayList(globalServiceMetaMap.keySet());
    }
}
