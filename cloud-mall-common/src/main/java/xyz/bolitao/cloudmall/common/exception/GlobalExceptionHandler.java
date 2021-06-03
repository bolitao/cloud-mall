package xyz.bolitao.cloudmall.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boli.tao
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ApiRestResponse<String> handleException(Exception e) {
        LOGGER.error("Default exception: ", e);
        return ApiRestResponse.error(ImoocMallExceptionEnum.SYSTEM_EXCEPTION.getCode(),
                ImoocMallExceptionEnum.SYSTEM_EXCEPTION.getMsg() + ": " + e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(ImoocMallException.class)
    public ApiRestResponse<String> handleImoocMallException(ImoocMallException e) {
        LOGGER.error("Imooc mall business exception: ", e);
        return ApiRestResponse.error(e.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiRestResponse<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        LOGGER.error("MethodArgumentNotValidException: ", e);
        return handleBindingReasult(e.getBindingResult());
    }

    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    private ApiRestResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {
        LOGGER.error(e.getMessage(), e);
        return ApiRestResponse.error(ImoocMallExceptionEnum.ILLEGAL_ARGUMENT.getCode(),
                ImoocMallExceptionEnum.ILLEGAL_ARGUMENT.getMsg() + ": " + e.getMessage());
    }

    /**
     * 将异常处理为对外提示
     *
     * @param bindingResult bindingResult
     * @return 异常提示
     */
    private ApiRestResponse<String> handleBindingReasult(BindingResult bindingResult) {
        List<String> list = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError objectError : allErrors) {
                String defaultMessage = objectError.getDefaultMessage();
                list.add(defaultMessage);
            }
        }
        if (list.size() == 0) {
            return ApiRestResponse.error(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR);
        }
        return ApiRestResponse.error(ImoocMallExceptionEnum.REQUEST_PARAM_ERROR.getCode(), list.toString());
    }
}
