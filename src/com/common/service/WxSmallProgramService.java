package com.common.service;

import com.jfinal.aop.Enhancer;

public abstract class WxSmallProgramService {

    public <T> T enhance(Class<T> targetClass) {
        return Enhancer.enhance(targetClass);
    }

}
