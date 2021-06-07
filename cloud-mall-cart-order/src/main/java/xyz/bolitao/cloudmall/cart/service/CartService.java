package xyz.bolitao.cloudmall.cart.service;


import xyz.bolitao.cloudmall.cart.model.vo.CartVO;

import java.util.List;

public interface CartService {
    List<CartVO> add(Integer userId, Integer productId, Integer count);

    List<CartVO> list(Integer userId);

    List<CartVO> update(Integer userId, Integer productId, Integer count);

    List<CartVO> delete(Integer userId, Integer productId);

    List<CartVO> selectOrNot(Integer userId, Integer productId, Integer selected);

    List<CartVO> selectAllOrAllNot(Integer userId, Integer selected);
}
