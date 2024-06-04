package org.sycamore.llm.hub.service.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.sycamore.llm.hub.service.enums.DelEnum;

import java.util.Date;

@Configuration
public class MybatisConfig {
    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    /**
     * 元数据填充
     */
    @Bean
    public MetaObjectHandler myMetaObjectHandler() {
        return new MetaObjectHandler() {
            /**
             * 数据新增时填充
             *
             * @param metaObject
             */
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
                this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
                this.strictInsertFill(metaObject, "delFlag", Integer.class, DelEnum.NORMAL.code());
            }

            /**
             * 数据修改时填充
             *
             * @param metaObject
             */
            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
            }
        };
    }
}
