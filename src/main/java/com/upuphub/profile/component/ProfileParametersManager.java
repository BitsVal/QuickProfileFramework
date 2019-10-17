package com.upuphub.profile.component;

import java.util.Map;
import java.util.Set;
/**
 * @author Leo Wang
 * @version 1.0
 * @date 2019/10/16 00:06
 */
public class ProfileParametersManager {
    private Map<String,BaseProfileDefinition> profileParametersServiceBinder;
    private Map<String,ProfileOriginalMethod> profileServiceMethodMapper;
    private Set<String> needVerifyKeysSet;
    private Set<String> needSpreadKeysSet;
    private Set<String> readOnlyKeysSet;
}
