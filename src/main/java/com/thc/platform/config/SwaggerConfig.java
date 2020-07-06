package com.thc.platform.config;

import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author zw
 * @version 1.0
 * @date 2019-11-09 11:43
 **/
@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(getApiInfo()).select()
                //过滤的接口
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class)).paths(PathSelectors.any()).build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                // 标题
                .title("GLOBAL-PLATFORM-EXTENT-API")
                // 描述信息
                .description("全局extend项目API文档")
                //版本
                .version("4.1.1").build();
    }

}
