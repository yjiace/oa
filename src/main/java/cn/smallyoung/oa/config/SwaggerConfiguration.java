package cn.smallyoung.oa.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpSession;

/**
 * @author smallyoung
 * @data 2020/10/29
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("OA办公管理系统")
                .description("OA办公管理系统,服务相关接口")
                .termsOfServiceUrl("https://oa.smallyoung.cn/")
                .contact(new Contact("small-young","https://www.smallyoung.cn",""))
                .version("1.0.0")
                .build();
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("1.0.0")
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.smallyoung.oa"))
                .paths(PathSelectors.any())
                .build()
                //添加忽略类型
                .ignoredParameterTypes(HttpSession.class);
    }

}
