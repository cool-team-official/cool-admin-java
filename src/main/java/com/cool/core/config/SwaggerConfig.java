package com.cool.core.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(info = @Info(title = "COOL-ADMIN", version = "4.0", description = "一个很酷的后台权限管理系统开发框架", contact = @Contact(name = "闪酷科技")), security = @SecurityRequirement(name = "Authorization"), externalDocs = @ExternalDocumentation(description = "参考文档", url = "https://cool-js.com"))
@SecurityScheme(type = SecuritySchemeType.APIKEY, name = "Authorization", in = SecuritySchemeIn.HEADER)
public class SwaggerConfig {

}
