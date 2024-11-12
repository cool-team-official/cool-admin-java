package com.cool.core.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.webmvc.api.OpenApiResource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 自定义 OpenApiResource
 */
@Component
@ConditionalOnProperty(
    name = "springdoc.api-docs.enabled",
    havingValue = "true"
)
public class CustomOpenApiResource extends OpenApiResource {

    public CustomOpenApiResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory, AbstractRequestService requestBuilder, GenericResponseService responseBuilder, OperationService operationParser, SpringDocConfigProperties springDocConfigProperties, SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers) {
        super("springdocDefault", openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser, springDocConfigProperties, springDocProviders, springDocCustomizers);
    }

    @Override
    protected String getServerUrl(HttpServletRequest request, String apiDocsUrl) {
        return "";
    }

    public byte[] getOpenApiJson() throws JsonProcessingException {
        return writeJsonValue(getOpenApi(Locale.getDefault()));
    }
}