package xyz.bolitao.cloudmall.order.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import xyz.bolitao.cloudmall.cart.model.dao.CartMapper;
import xyz.bolitao.cloudmall.cart.model.vo.CartVO;
import xyz.bolitao.cloudmall.cart.service.CartService;
import xyz.bolitao.cloudmall.common.CategoryProductConstant;
import xyz.bolitao.cloudmall.common.common.Constant;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;
import xyz.bolitao.cloudmall.common.util.QRCodeGenerator;
import xyz.bolitao.cloudmall.feign.ProductFeignClient;
import xyz.bolitao.cloudmall.feign.UserFeignClient;
import xyz.bolitao.cloudmall.order.model.dao.OrderItemMapper;
import xyz.bolitao.cloudmall.order.model.dao.OrderMapper;
import xyz.bolitao.cloudmall.order.model.dto.CreateOrderReqDTO;
import xyz.bolitao.cloudmall.order.model.entity.Order;
import xyz.bolitao.cloudmall.order.model.entity.OrderItem;
import xyz.bolitao.cloudmall.order.model.vo.OrderItemVO;
import xyz.bolitao.cloudmall.order.model.vo.OrderVO;
import xyz.bolitao.cloudmall.order.service.OrderService;
import xyz.bolitao.cloudmall.product.model.entity.Product;
import xyz.bolitao.cloudmall.util.OrderCodeFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author boli.tao
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderServiceImpl implements OrderService {
    private final CartService cartService;
    private final CartMapper cartMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final UserFeignClient userFeignClient;
    private final ProductFeignClient productFeignClient;
//    @Value("${boli.file-upload-ip}")
//    private String ip;

    @Autowired
    public OrderServiceImpl(CartService cartService, CartMapper cartMapper, OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper, UserFeignClient userFeignClient,
                            ProductFeignClient productFeignClient) {
        this.cartService = cartService;
        this.cartMapper = cartMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.userFeignClient = userFeignClient;
        this.productFeignClient = productFeignClient;
    }

    @Override
    public String create(CreateOrderReqDTO createOrderReqDTO) {
        // 1. userid
        Integer userId = userFeignClient.getUser().getId();

        // 2. find selected products from cart
        List<CartVO> cartVOList = cartService.list(userId);
        ArrayList<CartVO> tmpCartList = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            if (cartVO.getSelected().equals(Constant.Cart.SELECTED)) {
                tmpCartList.add(cartVO);
            }
        }
        cartVOList = tmpCartList;
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.CART_EMPTY);
        }

        // 3. check product imformation (exists, status, stock)
        validProducts4Order(cartVOList);

        // 4. trans Cart object to OrderItem object
        List<OrderItem> orderItemList = cartVOList2OrderItemList(cartVOList);

        // 5. minus stock
        for (OrderItem orderItem : orderItemList) {
            Product product = productFeignClient.detail4feign(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new ImoocMallException(ImoocMallExceptionEnum.STOCK_NOT_ENOUGH);
            }
            productFeignClient.updateStock(product.getId(), stock);
        }

        // 6. delete products from cart
        cleanCart(cartVOList);

        // 7. generate order
        Order order = new Order();
        order.setOrderNo(OrderCodeFactory.getOrderCode(Long.valueOf(userId)));
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverName(createOrderReqDTO.getReceiverName());
        order.setReceiverMobile(createOrderReqDTO.getReceiverMobile());
        order.setReceiverAddress(order.getReceiverAddress());
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        orderMapper.insertSelective(order);

        // 8. loop: save each OrderItem
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
            orderItemMapper.insertSelective(orderItem);
        }

        return order.getOrderNo();
    }

    @Override
    public OrderVO detail(String orderNo) {
        Order order = orderMapper.selectByOrderNum(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_SUCH_ORDER);
        }
        Integer userId = userFeignClient.getUser().getId();
        if (!order.getUserId().equals(userId)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.RESOURCE_NOT_PERMIT);
        }

        return getOrderVO(order);
    }

    @Override
    public PageInfo<OrderVO> list4Customer(Integer pageNum, Integer pageSize) {
        Integer userId = userFeignClient.getUser().getId();
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.select4Custom(userId);
        List<OrderVO> orderVOList = orderList2OrderVOList(orders);
        return new PageInfo<>(orderVOList);
    }

    @Override
    public void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderNum(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_SUCH_ORDER);
        }
        if (!order.getUserId().equals(userFeignClient.getUser().getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.RESOURCE_NOT_PERMIT);
        }
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKey(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public String qrcode(String orderNo) throws IOException, WriterException {
        // TODO: com
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String ip = InetAddress.getLocalHost().getHostAddress();
        String address = ip + ":" + request.getLocalPort();
        String payUrl = "http://" + address + "/pay?orderNo=" + orderNo;
        // TODO: 图片转为经过网关的形式
        QRCodeGenerator.generateQRCodeImage(payUrl, 350, 350, CategoryProductConstant.FILE_UPLOAD_DIR + orderNo +
                ".png");
        return "http://" + address + "/images/" + orderNo + ".png";
    }

    @Override
    public PageInfo<OrderVO> list4Admin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orders = orderMapper.selectAll4Admin();
        List<OrderVO> orderVOList = orderList2OrderVOList(orders);
        return new PageInfo<>(orderVOList);
    }

    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNum(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_SUCH_ORDER);
        }
        if (!order.getUserId().equals(userFeignClient.getUser().getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.RESOURCE_NOT_PERMIT);
        }

        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void deliver(String orderNo) {
        Order order = orderMapper.selectByOrderNum(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_SUCH_ORDER);
        }

        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNum(orderNo);
        if (order == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NO_SUCH_ORDER);
        }
        if (userFeignClient.getUser().getRole().equals(1) && !order.getUserId().equals(userFeignClient.getUser().getId())) {
            throw new ImoocMallException(ImoocMallExceptionEnum.RESOURCE_NOT_PERMIT);
        }


        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.DELIVERED.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new ImoocMallException(ImoocMallExceptionEnum.WRONG_ORDER_STATUS);
        }
    }

    private List<OrderVO> orderList2OrderVOList(List<Order> orders) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (Order order : orders) {
            orderVOList.add(this.getOrderVO(order));
        }
        return orderVOList;
    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNum(order.getOrderNo());
        List<OrderItemVO> orderItemVOS = new ArrayList<>();
        for (OrderItem orderItem : orderItemList) {
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOS.add(orderItemVO);
        }
        orderVO.setOrderItemVoList(orderItemVOS);
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            totalPrice = totalPrice + orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOList2OrderItemList(List<CartVO> cartVOList) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        for (CartVO cartVO : cartVOList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());

            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void validProducts4Order(List<CartVO> cartVOList) {
        for (CartVO cartVO : cartVOList) {
            Product product = productFeignClient.detail4feign(cartVO.getProductId());
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new ImoocMallException(ImoocMallExceptionEnum.PRODUCT_NOT_SELL);
            }

            if (cartVO.getQuantity() > product.getStock()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.STOCK_NOT_ENOUGH);
            }
        }
    }
}
