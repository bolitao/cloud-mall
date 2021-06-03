package xyz.bolitao.cloudmall.category.service;

import com.github.pagehelper.PageInfo;
import xyz.bolitao.cloudmall.category.model.dto.AddCategoryReqDTO;
import xyz.bolitao.cloudmall.category.model.entity.Category;
import xyz.bolitao.cloudmall.category.model.vo.CategoryVO;


import java.util.List;

/**
 * @author boli.tao
 */
public interface CategoryService {
    void add(AddCategoryReqDTO addCategoryReqDTO);

    void update(Category updateCategory);

    void delete(Integer id);

    PageInfo<Category> listCategoryForAdmin(Integer pageNum, Integer pageSize);

    List<CategoryVO> listCategoryForCustom(Integer parentId);
}
