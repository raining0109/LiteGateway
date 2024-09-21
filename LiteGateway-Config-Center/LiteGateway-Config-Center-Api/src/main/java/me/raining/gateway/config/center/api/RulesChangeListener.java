package me.raining.gateway.config.center.api;

import me.raining.gateway.common.config.Rule;
import java.util.List;

/**
 * @author raining
 * @version 1.0.0
 * @description 规则变更监听器
 */
public interface RulesChangeListener {

    /**
     * 规则变更时调用此方法 对网关的规则进行更新，需要手动传入
     */
    void onRulesChange(List<Rule> rules);
}
