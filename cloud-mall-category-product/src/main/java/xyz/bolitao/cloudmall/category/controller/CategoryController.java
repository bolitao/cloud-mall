package xyz.bolitao.cloudmall.category.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import xyz.bolitao.cloudmall.category.model.dto.AddCategoryReqDTO;
import xyz.bolitao.cloudmall.category.model.dto.UpdateCategoryReqDTO;
import xyz.bolitao.cloudmall.category.model.entity.Category;
import xyz.bolitao.cloudmall.category.model.vo.CategoryVO;
import xyz.bolitao.cloudmall.category.service.CategoryService;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;

import javax.validation.Valid;
import java.util.List;

/**
 * category controller
 *
 * @author boli.tao
 */
@Controller
public class CategoryController {
    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ApiOperation("后台管理-添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse<String> addCategory(@Valid @RequestBody AddCategoryReqDTO addCategoryReqDTO) throws ImoocMallException {
        categoryService.add(addCategoryReqDTO);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台管理-更新目录")
    @ResponseBody
    @PostMapping("/admin/category/update")
    public ApiRestResponse<String> updateCategory(@Valid @RequestBody UpdateCategoryReqDTO updateCategoryReqDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(updateCategoryReqDTO, category);
        categoryService.update(category);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台管理-删除目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse<String> deleteCategory(@RequestParam("id") Integer id) {
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台管理-目录列表")
    @GetMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse<PageInfo<Category>> listCategoryForAdmin(@RequestParam Integer pageNum,
                                                                    @RequestParam Integer pageSize) {
        PageInfo<Category> categoryPageInfo = categoryService.listCategoryForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(categoryPageInfo);
    }

    @ApiOperation("前台目录列表")
    @GetMapping("/category/list")
    @ResponseBody
    public ApiRestResponse<List<CategoryVO>> listCategoryForCustom() {
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustom(0);
        return ApiRestResponse.success(categoryVOS);
    }
}
