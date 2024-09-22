package me.raining.gateway.common.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author raining
 * @version 1.0.0
 * @description 动态服务缓存配置管理类，用于存放从配置中心获取到的配置（规则，服务...）
 */
public class DynamicConfigManager {

    //	规则集合
    private ConcurrentHashMap<String /* ruleId */ , Rule> ruleMap = new ConcurrentHashMap<>();

    //路径以及规则集合
    //一个路径对应着一个规则 {key:serviceId+"."+path, value:rule}
    private ConcurrentHashMap<String /* 路径 */ , Rule> pathRuleMap = new ConcurrentHashMap<>();
    //一个服务对应着多个规则
    private ConcurrentHashMap<String /* 服务名 */ , List<Rule>> serviceRuleMap = new ConcurrentHashMap<>();

    /**
     * 私有化构造方法
     */
    private DynamicConfigManager() {
    }

    /**
     * 单例
     */
    private static class SingletonHolder {
        private static final DynamicConfigManager INSTANCE = new DynamicConfigManager();
    }

    public static DynamicConfigManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /***************** 	对规则缓存进行操作的系列方法 	***************/
    public void putRule(String ruleId, Rule rule) {
        ruleMap.put(ruleId, rule);
    }

    public void putAllRule(List<Rule> ruleList) {
        ConcurrentHashMap<String, Rule> newRuleMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Rule> newPathMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, List<Rule>> newServiceMap = new ConcurrentHashMap<>();
        for (Rule rule : ruleList) {
            newRuleMap.put(rule.getId(), rule);
            List<Rule> rules = newServiceMap.get(rule.getServiceId());
            if (rules == null) {
                rules = new ArrayList<>();
            }
            rules.add(rule);
            newServiceMap.put(rule.getServiceId(), rules);

            List<String> paths = rule.getPaths();
            for (String path : paths) {
                String key = rule.getServiceId() + "." + path;
                newPathMap.put(key, rule);
            }
        }
        ruleMap = newRuleMap;
        pathRuleMap = newPathMap;
        serviceRuleMap = newServiceMap;
    }

    public Rule getRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public void removeRule(String ruleId) {
        ruleMap.remove(ruleId);
    }

    public ConcurrentHashMap<String, Rule> getRuleMap() {
        return ruleMap;
    }

    public Rule getRuleByPath(String path) {
        return pathRuleMap.get(path);
    }

    public List<Rule> getRuleByServiceId(String serviceId) {
        return serviceRuleMap.get(serviceId);
    }


}
