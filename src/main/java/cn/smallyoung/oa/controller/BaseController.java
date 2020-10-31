package cn.smallyoung.oa.controller;

import cn.smallyoung.oa.base.BaseService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * @author smallyoung
 * @data 2020/10/31
 */
public abstract class BaseController<T, ID extends Serializable> {

    @Autowired
    private BaseService<T, ID> baseService;

    @GetMapping(value = "findPage")
    @ApiOperation(value = "分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(name = "limit", value = "页数", dataType = "Integer")
    })
    public Page<T> findAll(@RequestParam(defaultValue = "1")Integer page,
                           HttpServletRequest request, @RequestParam(defaultValue = "10")Integer limit) {
        return baseService.findAll(WebUtils.getParametersStartingWith(request, "search_"),
                PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "updateTime")));
    }

    @GetMapping(value = "findList")
    @ApiOperation(value = "查询所有")
    public List<T> findAll(HttpServletRequest request) {
        return baseService.findAll(WebUtils.getParametersStartingWith(request, "search_"), Sort.by(Sort.Direction.DESC, "updateTime"));
    }

    @GetMapping(value = "findById")
    @ApiOperation(value = "根据id查询")
    public T findById(ID id){
        return baseService.findById(id).orElse(null);
    }
}
