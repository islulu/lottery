package com.greentown.lottery.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greentown.common.constants.ExceptionConstants;
import com.greentown.common.constants.ExceptionConstants.ResultEnums;
import com.greentown.common.exception.BusinessLogicException;
import com.greentown.common.response.AppResponseEntity;
import com.greentown.common.web.CustomHttpHeaderUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 这个类用来handle整个系统的异常或正常返回对象，包装成PortalResponse对象返回给调用方
 * 
 * @author jairy
 *
 */
@Component
@ControllerAdvice(basePackages = { "com.greentown.lottery.controller" })
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ResponseHandler implements ResponseBodyAdvice<Object> {
	private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

	/**
	 * 这里定义的是需要返回详细错误的异常
	 */
	private Map<String, ResultEnums> exceptionMappings = new HashMap<>();

	@Autowired
	ObjectMapper objectMapper;

	@PostConstruct
	private void initExceptionMappings() {
		// Springmvc的一些异常
		exceptionMappings.put(HttpRequestMethodNotSupportedException.class.getName(),
				ExceptionConstants.SYSTEM_HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION);
		exceptionMappings.put(HttpMediaTypeNotSupportedException.class.getName(),
				ExceptionConstants.SYSTEM_HTTP_MEDIATYPE_NOT_SUPPORTED_EXCEPTION);
		exceptionMappings.put(HttpMediaTypeNotAcceptableException.class.getName(),
				ExceptionConstants.SYSTEM_HTTP_MEDIATYPE_NOT_ACCEPTABLE_EXCEPTION);
		exceptionMappings.put(MissingPathVariableException.class.getName(),
				ExceptionConstants.SYSTEM_MISSING_PATHVARIABLE_EXCEPTION);
		exceptionMappings.put(MissingServletRequestParameterException.class.getName(),
				ExceptionConstants.SYSTEM_MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION);
		exceptionMappings.put(ServletRequestBindingException.class.getName(),
				ExceptionConstants.SYSTEM_SERVLET_REQUEST_BINDING_EXCEPTION);
		exceptionMappings.put(ConversionNotSupportedException.class.getName(),
				ExceptionConstants.SYSTEM_CONVERSION_NOT_SUPPORTED_EXCEPTION);
		exceptionMappings.put(TypeMismatchException.class.getName(), ExceptionConstants.SYSTEM_TYPE_MISMATCH_EXCEPTION);
		exceptionMappings.put(HttpMessageNotReadableException.class.getName(),
				ExceptionConstants.SYSTEM_HTTP_MESSAGE_NOT_READABLE_EXCEPTION);
		exceptionMappings.put(HttpMessageNotWritableException.class.getName(),
				ExceptionConstants.SYSTEM_HTTP_MESSAGE_NOT_WRITABLE_EXCEPTION);
		exceptionMappings.put(MethodArgumentNotValidException.class.getName(),
				ExceptionConstants.SYSTEM_METHOD_ARGUMENT_NOT_VALID_EXCEPTION);
		exceptionMappings.put(MissingServletRequestPartException.class.getName(),
				ExceptionConstants.SYSTEM_MISSING_SERVLET_REQUEST_PART_EXCEPTION);
		exceptionMappings.put(BindException.class.getName(), ExceptionConstants.SYSTEM_BIND_EXCEPTION);
		exceptionMappings.put(NoHandlerFoundException.class.getName(),
				ExceptionConstants.SYSTEM_NULL_POINTER_EXCEPTION);
		// Springmvc的一些异常
	}

	@ExceptionHandler(value = Exception.class)
	@ResponseBody
	public AppResponseEntity customErrorHandler(HttpServletRequest request, Exception ex) throws Exception {
		ResultEnums resultEnum = null;
		Object extra = null;
		if (ex instanceof BusinessLogicException) {
			BusinessLogicException portalException = (BusinessLogicException) ex;
			resultEnum = portalException.getResultEnum();
			extra = portalException.getExtra();
		} else {
			logger.error("IP={}, URL={}, USER_ID={}", CustomHttpHeaderUtil.getRemoteIp(request),
					request.getRequestURI(), StringUtils.defaultString(CustomHttpHeaderUtil.getUserId(request)));
			logger.error(ex.getMessage(), ex);
			ex.printStackTrace();

			if (ex instanceof SQLException || ex instanceof DataAccessException) {
				resultEnum = ExceptionConstants.SYSTEM_SQL_EXCEPTION;
			} else if (ex instanceof NullPointerException) {
				resultEnum = ExceptionConstants.SYSTEM_NULL_POINTER_EXCEPTION;
			} else if (ex instanceof IllegalStateException && StringUtils.endsWith(ex.getMessage(),
					"Consider declaring it as object wrapper for the corresponding primitive type.")) {
				resultEnum = ExceptionConstants.SYSTEM_MISSING_REQUEST_PARAMETERS;
			} else {
				resultEnum = ExceptionConstants.SYSTEM_OTHER_EXCEPTION;
			}
		}

		AppResponseEntity responseEntity = new AppResponseEntity(resultEnum, extra);

		return responseEntity;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return returnType.getMethod().getReturnType() != AppResponseEntity.class;
	}

	/**
	 * 将接口返回的对象统一包装成PortalResponseEntity类的实例并增加jsonp的支持
	 * 
	 * @see org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice#beforeBodyWrite(Object,
	 *      org.springframework.core.MethodParameter,
	 *      org.springframework.http.MediaType, Class,
	 *      org.springframework.http.server.ServerHttpRequest,
	 *      org.springframework.http.server.ServerHttpResponse)
	 */
	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
		if(body instanceof String){
			try {
				response.getHeaders().setContentType(MediaType.parseMediaType(MediaType.APPLICATION_JSON_VALUE));
				return objectMapper.writeValueAsString(new AppResponseEntity(body));
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		if (body instanceof AppResponseEntity) {
			return body;
		}
		return new AppResponseEntity(body);
	}
}
