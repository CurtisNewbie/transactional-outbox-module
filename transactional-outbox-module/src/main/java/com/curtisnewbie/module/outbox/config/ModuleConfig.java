package com.curtisnewbie.module.outbox.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongjie.zhuang
 */
@MapperScan("com.curtisnewbie.module.outbox.dao")
@Configuration
public class ModuleConfig {

}
