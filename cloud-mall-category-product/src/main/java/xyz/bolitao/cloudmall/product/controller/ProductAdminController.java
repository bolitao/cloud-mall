package xyz.bolitao.cloudmall.product.controller;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.bolitao.cloudmall.common.CategoryProductConstant;
import xyz.bolitao.cloudmall.common.common.ApiRestResponse;
import xyz.bolitao.cloudmall.common.exception.ImoocMallException;
import xyz.bolitao.cloudmall.common.exception.ImoocMallExceptionEnum;
import xyz.bolitao.cloudmall.product.model.dto.AddProductReqDTO;
import xyz.bolitao.cloudmall.product.model.dto.UpdateProductReqDTO;
import xyz.bolitao.cloudmall.product.model.entity.Product;
import xyz.bolitao.cloudmall.product.service.ProductService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * 后台商品管理 controller
 *
 * @author boli.tao
 */
@Controller
public class ProductAdminController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductAdminController.class);

    private final ProductService productService;

    @Value("${boli.file-upload-ip}")
    String ip;
    @Value("${boli.file-upload-port}")
    Integer port;

    @Autowired
    public ProductAdminController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/admin/product/add")
    @ResponseBody
    public ApiRestResponse<String> addProduct(@Valid @RequestBody AddProductReqDTO addProductReqDTO) {
        productService.add(addProductReqDTO);
        return ApiRestResponse.success();
    }

    @PostMapping("/admin/upload/file")
    @ResponseBody
    public ApiRestResponse<String> upload(
            HttpServletRequest httpServletRequest,
            @RequestParam("file") MultipartFile file) throws IOException, URISyntaxException {
        String originalFilename = file.getOriginalFilename();
        Assert.notNull(originalFilename, "文件名为空");
        String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFilename = uuid + suffixName;
        File fileDirectory = new File(CategoryProductConstant.FILE_UPLOAD_DIR);
        File destFile = new File(CategoryProductConstant.FILE_UPLOAD_DIR + newFilename);
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdirs()) {
                throw new ImoocMallException(ImoocMallExceptionEnum.MKDIR_FAILED);
            }
        }
        file.transferTo(destFile);
        // TODO: com
        return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL().toString())) + "/category" +
                "-product/images/" + newFilename);
    }

    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), ip, port, null, null, null);
        } catch (URISyntaxException e) {
            LOGGER.error("Failed to get uri info", e);
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @RequestMapping("/admin/product/update")
    @ResponseBody
    public ApiRestResponse<String> updateProduct(@RequestBody @Valid UpdateProductReqDTO updateProductReqDTO) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReqDTO, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @RequestMapping("/admin/product/delete")
    @ResponseBody
    public ApiRestResponse<String> deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ResponseBody
    @ApiOperation("后台批量上下架")
    @PostMapping("/admin/product/batchUpdateSellStatus")
    public ApiRestResponse<String> batchUpdateSellStatus(@RequestParam Integer[] ids,
                                                         @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ResponseBody
    @ApiOperation("后台管理 - 商品分页查询")
    @GetMapping("/admin/product/list")
    public ApiRestResponse<PageInfo<Product>> list4Admin(@RequestParam Integer pageNum,
                                                         @RequestParam Integer pageSize) {
        PageInfo<Product> productPageInfo = productService.list4Admin(pageNum, pageSize);
        return ApiRestResponse.success(productPageInfo);
    }
}
