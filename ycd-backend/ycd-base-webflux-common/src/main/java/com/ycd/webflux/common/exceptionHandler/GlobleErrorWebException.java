package com.ycd.webflux.common.exceptionHandler;


import com.ycd.common.util.SimpleUtil;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


public class GlobleErrorWebException extends AbstractErrorWebExceptionHandler {


    private static final Map<HttpStatus.Series, String> SERIES_VIEWS;


    ServerCodecConfigurer serverCodecConfigurer = new DefaultServerCodecConfigurer();

    static {
        Map<HttpStatus.Series, String> views = new EnumMap<>(HttpStatus.Series.class);
        views.put(HttpStatus.Series.CLIENT_ERROR, "4xx");
        views.put(HttpStatus.Series.SERVER_ERROR, "5xx");
        SERIES_VIEWS = Collections.unmodifiableMap(views);
    }

    private final ErrorProperties errorProperties = new ErrorProperties();


    public GlobleErrorWebException(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, applicationContext);
        setMessageWriters(serverCodecConfigurer.getWriters());
        setMessageReaders(serverCodecConfigurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(
            ErrorAttributes errorAttributes) {
        return route(acceptsTextHtml(), this::renderErrorView).andRoute(all(),
                this::renderErrorResponse);
    }

    /**
     * Render the error information as an HTML view.
     *
     * @param request the current request
     * @return a {@code Publisher} of the HTTP response
     */
    protected Mono<ServerResponse> renderErrorView(ServerRequest request) {
        boolean includeStackTrace = isIncludeStackTrace(request, MediaType.TEXT_HTML);
        Map<String, Object> error = getErrorAttributes(request, includeStackTrace);
        HttpStatus errorStatus = getHttpStatus(error);
        ServerResponse.BodyBuilder responseBody = ServerResponse.status(errorStatus)
                .contentType(MediaType.TEXT_HTML);
        return Flux
                .just("error/" + errorStatus.value(),
                        "error/" + SERIES_VIEWS.get(errorStatus.series()), "error/error")
                .flatMap((viewName) -> renderErrorView(viewName, responseBody, error))
                .switchIfEmpty(this.errorProperties.getWhitelabel().isEnabled()
                        ? renderDefaultErrorView(responseBody, error)
                        : Mono.error(getError(request)))
                .next();
    }

    /**
     * Render the error information as a JSON payload.
     *
     * @param request the current request
     * @return a {@code Publisher} of the HTTP response
     */
    protected Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        boolean includeStackTrace = isIncludeStackTrace(request, MediaType.ALL);
        Map<String, Object> error = getErrorAttributes(request, includeStackTrace);
        return ServerResponse.status(getHttpStatus(error))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(BodyInserters.fromObject(error));
    }

    /**
     * Determine if the stacktrace attribute should be included.
     *
     * @param request  the source request
     * @param produces the media type produced (or {@code MediaType.ALL})
     * @return if the stacktrace attribute should be included
     */
    protected boolean isIncludeStackTrace(ServerRequest request, MediaType produces) {
        ErrorProperties.IncludeStacktrace include = this.errorProperties
                .getIncludeStacktrace();
        if (include == ErrorProperties.IncludeStacktrace.ALWAYS) {
            return true;
        }
        if (include == ErrorProperties.IncludeStacktrace.ON_TRACE_PARAM) {
            return isTraceEnabled(request);
        }
        return false;
    }

    /**
     * Get the HTTP error status information from the error map.
     *
     * @param errorAttributes the current error information
     * @return the error HTTP status
     */
    protected HttpStatus getHttpStatus(Map<String, Object> errorAttributes) {
        if (SimpleUtil.isEmpty(errorAttributes.get("status"))) {
            return HttpStatus.OK;
        }
        int statusCode = (int) errorAttributes.get("status");
        return HttpStatus.valueOf(statusCode);
    }

    /**
     * Predicate that checks whether the current request explicitly support
     * {@code "text/html"} media type.
     * <p>
     * The "match-all" media type is not considered here.
     *
     * @return the request predicate
     */
    protected RequestPredicate acceptsTextHtml() {
        return (serverRequest) -> {
            try {
                List<MediaType> acceptedMediaTypes = serverRequest.headers().accept();
                acceptedMediaTypes.remove(MediaType.ALL);
                MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
                return acceptedMediaTypes.stream()
                        .anyMatch(MediaType.TEXT_HTML::isCompatibleWith);
            } catch (InvalidMediaTypeException ex) {
                return false;
            }
        };
    }
}
