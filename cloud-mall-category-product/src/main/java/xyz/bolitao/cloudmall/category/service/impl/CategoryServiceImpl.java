package xyz.bolitao.cloudmall.category.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import xyz.bolitao.cloudmall.category.model.dao.CategoryMapper;
import xyz.bolitao.cloudmall.category.model.dto.AddCategoryReqDTO;
import xyz.bolitao.cloudmall.category.model.entity.Category;
import xyz.bolitao.cloudmall.category.model.vo.CategoryVO;
import xyz.bolitao.cloudmall.category.service.CategoryService;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author boli.tao
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override

    public void add(AddCategoryReqDTO addCategoryReqDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryReqDTO, category);
        Category oldCategory = categoryMapper.selectByName(addCategoryReqDTO.getName());
        if (oldCategory != null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.NAME_ALREADY_EXISTED);
        }

        int count = categoryMapper.insertSelective(category);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    @Override
    public void update(Category updateCategory) {
        if (updateCategory.getName() != null) {
            Category categoryOld = categoryMapper.selectByName(updateCategory.getName());
            if (categoryOld != null && !categoryOld.getId().equals(updateCategory.getId())) {
                throw new ImoocMallException(ImoocMallExceptionEnum.NAME_ALREADY_EXISTED);
            }
        }

        int count = categoryMapper.updateByPrimaryKeySelective(updateCategory);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Category oldCategory = categoryMapper.selectByPrimaryKey(id);
        if (oldCategory == null) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
        int count = categoryMapper.deleteByPrimaryKey(id);
        if (count == 0) {
            throw new ImoocMallException(ImoocMallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public PageInfo<Category> listCategoryForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize, "type, order_num");
        List<Category> categoryList = categoryMapper.selectList();
        return new PageInfo<>(categoryList);
    }

    @Override
    @Cacheable(value = "listCategoryForCustom")
    public List<CategoryVO> listCategoryForCustom(Integer parentId) {
        List<CategoryVO> categoryVOList = new ArrayList<>();
        this.recursivelyFindCategories(categoryVOList, parentId);
        return categoryVOList;
    }

    private void recursivelyFindCategories(List<CategoryVO> categoryVOList, Integer parentId) {
        // 获取所有子类别，并按父子关系组成目录树 TODO: comp
        List<Category> categoryList = categoryMapper.selectCategoriesByParentId(parentId);
        if (!CollectionUtils.isEmpty(categoryList)) {
            for (Category category : categoryList) {
                CategoryVO categoryVO = new CategoryVO();
                BeanUtils.copyProperties(category, categoryVO);
                categoryVOList.add(categoryVO);
                recursivelyFindCategories(categoryVO.getChildCategory(), categoryVO.getId());
            }
        }
    }
}
