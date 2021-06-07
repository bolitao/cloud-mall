package xyz.bolitao.cloudmall.cart.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.bolitao.cloudmall.cart.model.dao.CartMapper;
import xyz.bolitao.cloudmall.cart.model.entity.Cart;
import xyz.bolitao.cloudmall.cart.model.vo.CartVO;
import xyz.bolitao.cloudmall.cart.service.CartService;
import xyz.bolitao.cloudmall.common.common.Constant;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;
import xyz.bolitao.cloudmall.feign.ProductFeignClient;
import xyz.bolitao.cloudmall.product.model.entity.Product;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    private final ProductFeignClient productFeignClient;
    private final CartMapper cartMapper;

    @Autowired
    public CartServiceImpl(ProductFeignClient productFeignClient, CartMapper cartMapper) {
        this.productFeignClient = productFeignClient;
        this.cartMapper = cartMapper;
    }

    @Override
    public List<CartVO> list(Integer userId) {
        List<CartVO> cartVOS = cartMapper.selectList(userId);
        for (CartVO cartVO : cartVOS) {
            cartVO.setTotalPrice(cartVO.getPrice() * cartVO.getQuantity());
        }
        return cartVOS;
    }

    @Override
    public List<CartVO> add(Integer userId, Integer productId, Integer count) {
        this.validProduct(productId, count);
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            cart = new Cart();
            cart.setProductId(productId);
            cart.setUserId(userId);
            cart.setQuantity(count);
            cart.setSelected(Constant.Cart.SELECTED);
            int insertCount = cartMapper.insertSelective(cart);
            if (insertCount == 0) {
                throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
            }
        } else {
            count = cart.getQuantity() + count;
            Cart newCart = new Cart();
            newCart.setId(cart.getId());
            newCart.setQuantity(count);
            newCart.setProductId(cart.getProductId());
            newCart.setUserId(cart.getUserId());
            newCart.setSelected(Constant.Cart.SELECTED);
            cartMapper.updateByPrimaryKeySelective(newCart);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> update(Integer userId, Integer productId, Integer count) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        } else {
            Cart newCart = new Cart();
            newCart.setId(cart.getId());
            newCart.setQuantity(count);
            newCart.setProductId(cart.getProductId());
            newCart.setUserId(cart.getUserId());
            newCart.setSelected(Constant.Cart.SELECTED);
            cartMapper.updateByPrimaryKeySelective(newCart);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> delete(Integer userId, Integer productId) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        } else {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected) {
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        } else {
            Integer updateCount = cartMapper.updateSelectOrNot(userId, productId, selected);
            System.out.println("updateSelectOrNot count: " + updateCount);
        }
        return this.list(userId);
    }

    @Override
    public List<CartVO> selectAllOrAllNot(Integer userId, Integer selected) {
        cartMapper.updateSelectOrNot(userId, null, selected);
        return this.list(userId);
    }

    private void validProduct(Integer productId, Integer count) {
        Product product = productFeignClient.detail4feign(productId);
        if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
            throw new ImoocMallException(ImoocMallExceptionEnum.PRODUCT_NOT_SELL);
        }

        // TODO: 考虑 count > (allStock - stockInCart)
        if (count > product.getStock()) {
            throw new ImoocMallException(ImoocMallExceptionEnum.STOCK_NOT_ENOUGH);
        }
    }
}
