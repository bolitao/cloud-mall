package xyz.bolitao.cloudmall.zuul.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;
import xyz.bolitao.cloudmall.common.common.Constant;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;
import xyz.bolitao.cloudmall.user.model.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * TODO: com
 *
 * @author boli.tao
 */
@Component
public class UserFilter extends ZuulFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String requestURI = request.getRequestURI();
        // TODO: 过于片面
        if (requestURI.contains("images") || requestURI.contains("pay")) {
            return false;
        }
        if (requestURI.contains("cart") || requestURI.contains("order")) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constant.IMOOC_MALL_USER);
        if (user == null) {
            // pass gateway
            currentContext.setSendZuulResponse(false);
            currentContext.getResponse().setContentType("application/json");
            currentContext.getResponse().setCharacterEncoding("UTF-8");
            try {
                currentContext.setResponseBody(new ObjectMapper().writeValueAsString(ApiRestResponse.error(ImoocMallExceptionEnum.NEED_LOGIN)));
            } catch (JsonProcessingException e) {
                LOGGER.error("Failed to rewrite json response", e);
            }
        }
        return null;
    }
}
